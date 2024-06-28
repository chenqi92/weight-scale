package cn.allbs.weightscale.config;

import cn.allbs.weightscale.exception.BhudyException;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
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

    public SerialPortManager() {
        serialPorts = new HashMap<>();
    }

//    @PostConstruct
    public void init() {
        log.info("\nUsing Library Version v{}", SerialPort.getVersion());
        SerialPort[] ports = SerialPort.getCommPorts();
        log.info("\nAvailable Ports:\n");
        for (SerialPort port : ports) {
            log.info("{}: {} - {}", port.getSystemPortName(), port.getDescriptivePortName(), port.getPortDescription());
            serialPorts.put(port.getSystemPortName(), port);
            openPort(port.getSystemPortName());
        }
    }

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

    public boolean openPort(String portName) {
        SerialPort port = serialPorts.get(portName);
        if (port == null) {
            log.info("Port {} is not managed!", portName);
            return false;
        }
        if (!port.isOpen()) {
            log.info("\nPre-setting RTS: {}", port.setRTS() ? "Success" : "Failure");
            if (!port.openPort()) {
                log.info("Open serial port {} error!", portName);
                return false;
            }
            log.info("\nOpening {}: {} - {}", port.getSystemPortName(), port.getDescriptivePortName(), port.getPortDescription());
            port.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
            port.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
        }
        return true;
    }

    public boolean isPortOpen(String portName) {
        SerialPort port = serialPorts.get(portName);
        return port != null && port.isOpen();
    }

    public void closePort(String portName) {
        SerialPort port = serialPorts.get(portName);
        if (port != null && port.isOpen()) {
            port.closePort();
            log.info("Closed Port: {}", portName);
        }
    }

    public int write(String portName, byte[] data) {
        SerialPort port = serialPorts.get(portName);
        if (port == null || !port.isOpen()) {
            return 0;
        }
        return port.writeBytes(data, data.length);
    }

    public int read(String portName, byte[] data) {
        SerialPort port = serialPorts.get(portName);
        if (port == null || !port.isOpen()) {
            return 0;
        }
        return port.readBytes(data, data.length);
    }

    /**
     * 向指定com口发送数据并且读取数据
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

    public void closeAllPorts() {
        for (String portName : serialPorts.keySet()) {
            closePort(portName);
        }
    }
}
