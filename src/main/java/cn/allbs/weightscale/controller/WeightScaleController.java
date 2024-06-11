package cn.allbs.weightscale.controller;

import cn.allbs.weightscale.model.WeightData;
import cn.allbs.weightscale.service.WeightScaleService;
import jakarta.annotation.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 类 WeightScaleController
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@RestController
public class WeightScaleController {

    @Resource
    private WeightScaleService weightScaleService;

    /**
     * 获取称重数据
     *
     * @param scaleId  称重编号
     * @param portName 端口名称
     * @return 称重数据
     */
    @GetMapping("/weight/{scaleId}")
    public ResponseEntity<WeightData> getWeight(@PathVariable int scaleId, @RequestParam String portName) {
        return weightScaleService.readWeightData(portName, scaleId);
    }
}
