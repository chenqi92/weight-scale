package cn.allbs.weightscale.service;

import cn.allbs.weightscale.enums.ScaleCommand;
import cn.allbs.weightscale.model.WeightData;
import cn.allbs.weightscale.util.SerialPortUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 类 WeightScaleService
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Service
public class WeightScaleService {

    @Resource
    private SerialPortUtil serialPortUtil;

    public WeightData performOperation(String portName, int scaleId, int operationCode) {
        ScaleCommand command;
        try {
            command = ScaleCommand.fromOperationCode(operationCode);
        } catch (IllegalArgumentException e) {
            return null;
        }

        serialPortUtil.initialize(portName, scaleId);
        WeightData data = serialPortUtil.sendCommand(scaleId, command);
        serialPortUtil.close(scaleId);
        return data;
    }
}
