package cn.allbs.weightscale.config;

import cn.allbs.weightscale.exception.BhudyException;
import cn.allbs.weightscale.handler.SerialPortListener;
import cn.allbs.weightscale.util.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 类 SerialPortManager
 *
 * @date 2024/6/27
 */
@Slf4j
@Component
public class SerialPortManager {

    private final Map<String, SerialPort> serialPorts;
    private final Map<String, SerialPortListener> listeners;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private SerialPortConfig serialPortConfig;

    public SerialPortManager() {
        serialPorts = new HashMap<>();
        listeners = new HashMap<>();
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        log.info("\nUsing Library Version v{}", SerialPort.getVersion());
        SerialPort[] ports = SerialPort.getCommPorts();
        log.info("\nAvailable Ports:\n");
        Map<String, String> portMappings = serialPortConfig.getPortMappings();
        for (SerialPort port : ports) {
            log.info("{}: {} - {}", port.getSystemPortName(), port.getDescriptivePortName(), port.getPortDescription());
            serialPorts.put(port.getSystemPortName(), port);
            startListener(port.getSystemPortName(), portMappings.get(port.getSystemPortName()));
        }
    }

    /**
     * 添加串口
     *
     * @param portName 串口名称
     * @return 结果
     */
    public boolean addPort(String portName) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals(portName)) {
                serialPorts.put(portName, port);
                log.info("Added Port: {} - {}", port.getSystemPortName(), port.getDescriptivePortName());
                return true;
            }
        }
        log.info("Port {} not found!", portName);
        return false;
    }

    /**
     * 开始监听
     *
     * @param portName 串口名称
     * @return 结果
     */
    private void startListener(String portName, String redisKey) {
        if (!listeners.containsKey(portName)) {
            SerialPort port = serialPorts.get(portName);
            if (port != null) {
                SerialPortListener listener = new SerialPortListener(port, portName, redisTemplate, redisKey);
                listeners.put(portName, listener);
                Thread listenerThread = new Thread(listener);
                listenerThread.start();
            }
        }
    }

    /**
     * 打开串口
     *
     * @param portName 串口名称
     */
    public void openPort(String portName) {
        SerialPort port = serialPorts.get(portName);
        if (port != null && !port.isOpen()) {
            log.info("\nPre-setting RTS: {}", port.setRTS() ? "Success" : "Failure");
            if (!port.openPort()) {
                log.info("Open serial port {} error!", portName);
            } else {
                log.info("\nOpening {}: {} - {}", port.getSystemPortName(), port.getDescriptivePortName(), port.getPortDescription());
                port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
                port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
            }
        }
    }

    /**
     * 是否打开串口
     *
     * @param portName 串口名称
     * @return 结果
     */
    public boolean isPortOpen(String portName) {
        SerialPort port = serialPorts.get(portName);
        return port == null || !port.isOpen();
    }

    /**
     * 关闭串口
     *
     * @param portName 串口名称
     */
    public void closePort(String portName) {
        SerialPort port = serialPorts.get(portName);
        SerialPortListener listener = listeners.get(portName);
        if (port != null && port.isOpen()) {
            if (listener != null) {
                listener.stop();
                listeners.remove(portName);
            }
            port.closePort();
            log.info("Closed Port: {}", portName);
        }
    }

    /**
     * 写入数据
     *
     * @param portName 串口名称
     * @param data     数据
     * @return 结果
     */
    public int write(String portName, byte[] data) {
        SerialPort port = serialPorts.get(portName);
        if (port == null || !port.isOpen()) {
            return 0;
        }
        return port.writeBytes(data, data.length);
    }

    /**
     * 读取数据
     *
     * @param portName 串口名称
     * @param data     数据
     * @return 结果
     */
    public int read(String portName, byte[] data) {
        SerialPort port = serialPorts.get(portName);
        if (port == null || !port.isOpen()) {
            return 0;
        }
        return port.readBytes(data, data.length);
    }

    /**
     * 写入并读取数据
     *
     * @param portName 串口名称
     * @param bytes    数据
     * @return 结果
     */
    public byte[] writeAndRead(String portName, byte[] bytes) {
        byte[] resultData = null;
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            SerialPort port = serialPorts.get(portName);
            if (port == null || !port.isOpen()) throw new BhudyException("Port not open or not found: " + portName);
            int numWrite = port.writeBytes(bytes, bytes.length);
            if (numWrite > 0) {
                Thread.sleep(100); // 休眠0.1秒，等待下位机返回数据。如果不休眠直接读取，有可能无法成功读到数据
                while (port.bytesAvailable() > 0) {
                    byte[] newData = new byte[port.bytesAvailable()];
                    int numRead = port.readBytes(newData, newData.length);
                    if (numRead > 0) {
                        bao.write(newData);
                    }
                }
                resultData = bao.toByteArray();
            }
        } catch (Exception e) {
            throw new BhudyException(e.getMessage());
        }
        return resultData;
    }

    /**
     * 读取一次重量
     *
     * @param portName 串口名称
     * @return 结果
     */
    public String readWeightOnce(String portName) {
        log.info("读取一次重量");
        try {
            // 固定读取12位
            byte[] readBuffer = new byte[12];
            SerialPort port = serialPorts.get(portName);
            int numRead = port.readBytes(readBuffer, readBuffer.length);
            if (numRead > 0) {
                String result = SerialPortUtil.parseWeightData(readBuffer);
                log.info("{}读取只读取一次串口{}的数据为:{}", LocalDateTime.now(), portName, result);
                return result;
            }
        } catch (Exception e) {
            log.error("读取一次重量异常!异常原因{}", e.getMessage());
        }
        return null;
    }

    /**
     * 关闭所有串口
     */
    public void closeAllPorts() {
        for (String portName : serialPorts.keySet()) {
            closePort(portName);
        }
    }
}
