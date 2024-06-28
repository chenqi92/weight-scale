package cn.allbs.weightscale.service;

import cn.allbs.weightscale.config.SerialPortManager;
import cn.allbs.weightscale.exception.BhudyException;
import cn.allbs.weightscale.model.WeightData;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeightScaleService {

    @Resource
    private SerialPortManager serialPortManager;

    public WeightData performOperation(String address, String portName, String operationCode) {
        try {
            // 检查串口是否已经打开
            if (!serialPortManager.isPortOpen(portName)) {
                log.info("串口未开，正在打开串口{}", portName);
                serialPortManager.openPort(portName);
            } else {
                log.info("串口已经打开{}", portName);
            }

            // 根据地址和操作码生成指令
            byte[] command = generateCommand(address, operationCode.charAt(0));
            log.info("Sending command: {}", new String(command));
            byte[] response = serialPortManager.writeAndRead(portName, command);
            log.info("Received response: {}", new String(response));

            // 处理响应数据并返回结果
            return parseResponse(response);
        } catch (Exception e) {
            throw new BhudyException("Error performing operation: " + e.getMessage(), e);
        }
    }

    private byte[] generateCommand(String address, char command) {
        byte start = 0x02; // 开始符
        byte end = 0x03; // 结束符

        byte addressByte = (byte) address.charAt(0);
        byte commandByte = (byte) command;

        // 计算异或校验
        byte xor = (byte) (start ^ addressByte ^ commandByte);
        byte xorHigh = (byte) ((xor >> 4) & 0x0F);
        byte xorLow = (byte) (xor & 0x0F);

        // 高四位和低四位的ASCII码转换
        xorHigh += (byte) ((xorHigh <= 9) ? 0x30 : 0x37);
        xorLow += (byte) ((xorLow <= 9) ? 0x30 : 0x37);

        // 生成字节数组
        return new byte[]{start, addressByte, commandByte, xorHigh, xorLow, end};
    }

    private WeightData parseResponse(byte[] response) {
        // 解析响应数据并创建 WeightData 对象
        // 这里是一个示例，实际解析逻辑需要根据具体需求编写
        StringBuilder weightStringBuilder = new StringBuilder();
        for (int i = 1; i < response.length - 3; i += 6) {
            String hexPart = new String(new byte[]{response[i + 2], response[i + 3]});
            weightStringBuilder.append((char) Integer.parseInt(hexPart, 16));
        }

        WeightData data = new WeightData();
        data.setWeight(weightStringBuilder.toString());
        return data;
    }
}
