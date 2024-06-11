package cn.allbs.weightscale.util;

import cn.allbs.weightscale.enums.ScaleCommand;
import cn.allbs.weightscale.model.WeightData;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 类 SerialPortUtil
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Component
public class SerialPortUtil {

    private static final Logger logger = LoggerFactory.getLogger(SerialPortUtil.class);

    private final Map<Integer, SerialPort> serialPorts = new HashMap<>();
    private final Map<Integer, BufferedReader> inputs = new HashMap<>();
    private final Map<Integer, OutputStream> outputs = new HashMap<>();
    private final Map<Integer, StringBuilder> dataBuffers = new HashMap<>();

    public void initialize(String portName, int scaleId) {
        SerialPort serialPort = SerialPort.getCommPort(portName);
        serialPort.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
        serialPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 2000, 0);

        if (serialPort.openPort()) {
            try {
                BufferedReader input = new BufferedReader(new InputStreamReader(serialPort.getInputStream(), StandardCharsets.US_ASCII));
                OutputStream output = serialPort.getOutputStream();

                serialPorts.put(scaleId, serialPort);
                inputs.put(scaleId, input);
                outputs.put(scaleId, output);
                dataBuffers.put(scaleId, new StringBuilder());

                serialPort.addDataListener(new SerialPortDataListener() {
                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                            byte[] newData = new byte[serialPort.bytesAvailable()];
                            int numRead = serialPort.readBytes(newData, newData.length);

                            if (numRead > 0) {
                                String inputLine = new String(newData, 0, numRead, StandardCharsets.US_ASCII);
                                StringBuilder dataBuffer = dataBuffers.get(scaleId);
                                dataBuffer.append(inputLine);

                                if (inputLine.endsWith("\u0003")) {
                                    String data = dataBuffer.toString();
                                    // 清空缓冲区
                                    dataBuffer.setLength(0);
                                    processData(data, scaleId);
                                }
                            }
                        }
                    }
                });

            } catch (Exception e) {
                logger.error("初始化串口时出错: {}", e.getMessage());
                serialPort.closePort();
            }
        } else {
            logger.error("无法打开端口 {}", portName);
        }
    }

    public void close(int scaleId) {
        if (serialPorts.containsKey(scaleId)) {
            SerialPort serialPort = serialPorts.get(scaleId);
            if (serialPort != null) {
                serialPort.closePort();
                logger.info("关闭串口连接 (Scale {})", scaleId);
                serialPorts.remove(scaleId);
                inputs.remove(scaleId);
                outputs.remove(scaleId);
                dataBuffers.remove(scaleId);
            }
        }
    }

    private void processData(String data, int scaleId) {
        // 根据文档描述的数据帧格式进行解析
        // 假设数据以 STX 开始，以 ETX 结束
        if (data.startsWith("\u0002") && data.endsWith("\u0003")) {
            // 获取命令类型或数据标识符
            String commandType = data.substring(1, 3);
            if (commandType.equals("AB")) {
                WeightData weightData = parseWeightData(data);
                logger.info("解析的地磅数据 (Scale {}): {}", scaleId, weightData);
                // 添加更多的命令类型处理
            } else {
                logger.error("未知的命令类型: {}", commandType);
            }
        } else {
            logger.error("收到的数据格式不正确: {}", data);
        }
    }

    private WeightData parseWeightData(String data) {
        WeightData weightData = new WeightData();

        try {
            // 解析数据部分
            String sign = data.substring(3, 4); // 获取正负号
            String weight = data.substring(4, 10).trim(); // 获取重量值
            String unitCode = data.substring(10, 11); // 获取单位代码
            String statusCode = data.substring(11, 12); // 获取状态代码

            // 根据单位代码设置单位
            String unit = switch (unitCode) {
                case "0" -> "kg"; // 千克
                case "1" -> "t"; // 吨
                default -> "unknown"; // 未知单位
            };

            // 根据状态代码设置状态
            String status = switch (statusCode) {
                case "0" -> "stable"; // 稳定
                case "1" -> "unstable"; // 不稳定
                default -> "unknown"; // 未知状态
            };

            weightData.setWeight(sign + weight); // 设置重量
            weightData.setUnit(unit); // 设置单位
            weightData.setStatus(status); // 设置状态
        } catch (Exception e) {
            logger.error("解析数据时出错: {}", e.getMessage());
            weightData.setWeight("error");
            weightData.setUnit("error");
            weightData.setStatus("error");
        }

        return weightData;
    }

    public void sendCommand(int scaleId, ScaleCommand command) {
        try {
            OutputStream output = outputs.get(scaleId);
            if (output != null) {
                output.write(hexStringToByteArray(command.getCommand()));
                output.flush();
                logger.info("发送指令: {} 到地磅 {}", command.getDescription(), scaleId);
            }
        } catch (Exception e) {
            logger.error("发送指令时出错: {}", e.getMessage());
        }
    }

    /**
     * 将十六进制字符串转换为字节数组
     *
     * @param s 十六进制字符串
     * @return 字节数组
     */
    private byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public WeightData readData(int scaleId, String portName) {
        if (!serialPorts.containsKey(scaleId)) {
            initialize(portName, scaleId);
        }

        WeightData weightData;
        try {
            sendCommand(scaleId, ScaleCommand.READ_GROSS_WEIGHT);
            // 模拟等待一段时间以接收数据
            Thread.sleep(2000);
            weightData = parseWeightData(dataBuffers.get(scaleId).toString());
        } catch (Exception e) {
            logger.error("读取数据时出错: {}", e.getMessage());
            weightData = new WeightData();
            weightData.setWeight("error");
            weightData.setUnit("error");
            weightData.setStatus("error");
        }
        logger.info("从地磅 {} 读取的数据: {}", scaleId, weightData);

        close(scaleId); // 读取完数据后关闭连接
        return weightData;
    }
}