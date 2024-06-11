package cn.allbs.weightscale.service;

import cn.allbs.weightscale.model.WeightData;
import cn.allbs.weightscale.util.SerialPortUtil;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
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

    /**
     * 读取称重数据
     *
     * @param portName 端口名称
     * @param scaleId  称重编号
     * @return 称重数据
     */
    public ResponseEntity<WeightData> readWeightData(String portName, int scaleId) {
        serialPortUtil.initialize(portName, scaleId);
        WeightData data = serialPortUtil.readData(scaleId, portName);
        serialPortUtil.close(scaleId);
        return ResponseEntity.ok(data);
    }
}
