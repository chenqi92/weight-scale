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
     * 读取称重数据或者执行指令
     *
     * @param portName 端口名称
     * @return 称重数据
     */
    @GetMapping("/scale/{operationCode}")
    public ResponseEntity<WeightData> performOperation(@PathVariable int operationCode, @RequestParam String portName) {
        return ResponseEntity.ok(weightScaleService.performOperation(portName, operationCode));
    }
}
