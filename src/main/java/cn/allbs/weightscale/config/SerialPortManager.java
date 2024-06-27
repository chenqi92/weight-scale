package cn.allbs.weightscale.config;

import cn.allbs.weightscale.exception.BhudyException;
import com.fazecast.jSerialComm.SerialPort;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

/**
 * 类 SerialPortManager
 *
 * @author ChenQi
 * @date 2024/6/27
 */
@Slf4j
@Component
public class SerialPortManager {

    private SerialPort mSerialPort;

    public SerialPortManager() {
        mSerialPort = null;
    }

    @PostConstruct
    public void init() {
        log.info("\nUsing Library Version v{}", SerialPort.getVersion());
        SerialPort[] ports = SerialPort.getCommPorts();
        log.info("\nAvailable Ports:\n");
        for (int i = 0; i < ports.length; ++i) {
            log.info("{}: {} - {}", ports[i].getSystemPortName(), ports[i].getDescriptivePortName(), ports[i].getPortDescription());
        }
    }

    public boolean selectPort(String portName) {
        SerialPort[] ports = SerialPort.getCommPorts();
        for (SerialPort port : ports) {
            if (port.getSystemPortName().equals(portName)) {
                mSerialPort = port;
                log.info("Selected Port: {} - {}", mSerialPort.getSystemPortName(), mSerialPort.getDescriptivePortName());
                return true;
            }
        }
        log.info("Port {} not found!", portName);
        return false;
    }

    public boolean openPort() {
        if (mSerialPort == null) {
            log.info("No port selected to open!");
            return false;
        }
        log.info("\nPre-setting RTS: {}", mSerialPort.setRTS() ? "Success" : "Failure");
        if (!mSerialPort.openPort()) {
            log.info("Open serial port error!");
            return false;
        }
        log.info("\nOpening " + mSerialPort.getSystemPortName() + ": " + mSerialPort.getDescriptivePortName() + " - " + mSerialPort.getPortDescription());
        mSerialPort.setFlowControl(SerialPort.FLOW_CONTROL_DISABLED);
        mSerialPort.setComPortParameters(9600, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY);
        mSerialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING | SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 1000);
        return true;
    }

    public void close() {
        if (mSerialPort != null && mSerialPort.isOpen()) {
            mSerialPort.closePort();
        }
    }

    public int write(byte[] data) {
        if (mSerialPort == null || !mSerialPort.isOpen()) {
            return 0;
        }
        return mSerialPort.writeBytes(data, data.length);
    }

    public int read(byte[] data) {
        if (mSerialPort == null || !mSerialPort.isOpen()) {
            return 0;
        }
        return mSerialPort.readBytes(data, data.length);
    }

    /**
     * 向com口发送数据并且读取数据
     */
    public byte[] writeAndRead(byte[] bytes) {
        byte[] reslutData = null;
        try (ByteArrayOutputStream bao = new ByteArrayOutputStream()) {
            if (mSerialPort == null || !mSerialPort.isOpen()) throw new BhudyException();
            int numWrite = mSerialPort.writeBytes(bytes, bytes.length);
            if (numWrite > 0) {
                Thread.sleep(100);//休眠0.1秒，等待下位机返回数据。如果不休眠直接读取，有可能无法成功读到数据
                while (mSerialPort.bytesAvailable() > 0) {
                    byte[] newData = new byte[mSerialPort.bytesAvailable()];
                    int numRead = mSerialPort.readBytes(newData, newData.length);
                    if (numRead > 0) {
                        bao.write(newData);
                    }
                }
                reslutData = bao.toByteArray();
            }
        } catch (Exception e) {
            throw new BhudyException(e.getMessage());
        }
        return reslutData;
    }
}
