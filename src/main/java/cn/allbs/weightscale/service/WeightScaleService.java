package cn.allbs.weightscale.service;

import cn.allbs.weightscale.config.R;
import cn.allbs.weightscale.config.SerialPortManager;
import cn.allbs.weightscale.enums.ScaleCommand;
import cn.allbs.weightscale.model.WeightData;
import cn.allbs.weightscale.util.SerialPortUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 类 WeightScaleService
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Slf4j
@Service
public class WeightScaleService {

    @Resource
    private SerialPortManager serialPortManager;

    public R<WeightData> performOperation(String portName, int operationCode) {
        ScaleCommand command;
        try {
            command = ScaleCommand.fromOperationCode(operationCode);
        } catch (IllegalArgumentException e) {
            return R.fail(e.getLocalizedMessage());
        }
        // 选择串口
        if (serialPortManager.selectPort(portName)) {
            // 打开串口
            if (serialPortManager.openPort()) {
                // 发送和读取数据示例
                byte[] dataToSend = SerialPortUtil.hexStringToByteArray(command.getCommand());
                byte[] responseData = serialPortManager.writeAndRead(dataToSend);
                log.info("Received: {}", new String(responseData));
                WeightData data = SerialPortUtil.parseWeightData(new String(responseData));
                // 关闭串口
                serialPortManager.close();
                return R.ok(data);
            }
        }
        return R.fail("没有对应串口或者串口打开失败！");
    }
}
