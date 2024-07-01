package cn.allbs.weightscale.service;

import cn.allbs.weightscale.config.SerialPortManager;
import cn.allbs.weightscale.exception.BhudyException;
import cn.allbs.weightscale.util.SerialPortUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WeightScaleService {

    @Resource
    private SerialPortManager serialPortManager;

    /**
     * 执行操作
     *
     * @param address       地址
     * @param portName      串口名称
     * @param operationCode 操作码
     * @return 结果
     */
    public String performOperation(String address, String portName, String operationCode) {
        try {
            // 检查串口是否已经打开
            if (!serialPortManager.isPortOpen(portName)) {
                log.info("串口未开，正在打开串口{}", portName);
                serialPortManager.openPort(portName);
            } else {
                log.info("串口已经打开{}", portName);
            }

            // 根据地址和操作码生成指令
            byte[] command = SerialPortUtil.generateCommand(address, operationCode.charAt(0));
            log.info("Sending command: {}", SerialPortUtil.byteArrayToHexString(command));
            byte[] response = serialPortManager.writeAndRead(portName, command);
            log.info("Received response: {}", response);

            // 处理响应数据并返回结果
            return SerialPortUtil.parseWeightData(response);
        } catch (Exception e) {
            throw new BhudyException("Error performing operation: " + e.getMessage(), e);
        }
    }

    public String getCurrentWeight(String portName) {
        try {
            // 检查串口是否已经打开
            if (!serialPortManager.isPortOpen(portName)) {
                log.info("串口未开，正在打开串口{}", portName);
                serialPortManager.openPort(portName);
            } else {
                log.info("串口已经打开{}", portName);
            }

            return serialPortManager.readWeightOnce(portName);
        } catch (Exception e) {
            throw new BhudyException("Error performing operation: " + e.getMessage(), e);
        }
    }
}
