package cn.allbs.weightscale.controller;

import cn.allbs.weightscale.config.R;
import cn.allbs.weightscale.service.WeightScaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
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
@Tag(name = "读取串口数据")
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
    @Operation(summary = "查询串口当前数据")
    @Parameters({
            @Parameter(name = "operationCode", description = "执行的操作A握手,B读毛重,C读皮重,D读净重", required = true, schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "portName", description = "串口全名", required = true, schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content =
                    {@Content(schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "success": true,
                                      "msg": "操作成功",
                                      "data": "1234"
                                    }""")})})
    })
    @GetMapping("/scale/{address}")
    public R<String> performOperation(@PathVariable("address") String address, @RequestParam String operationCode, @RequestParam String portName) {
        return R.ok(weightScaleService.performOperation(address, portName, operationCode));
    }

    /**
     * 读取称重数据或者执行指令
     *
     * @param portName 端口名称
     * @return 称重数据
     */
    @Operation(summary = "查询串口当前数据")
    @Parameters({
            @Parameter(name = "operationCode", description = "执行的操作A握手,B读毛重,C读皮重,D读净重", required = true, schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
            @Parameter(name = "portName", description = "串口全名", required = true, schema = @Schema(implementation = String.class), in = ParameterIn.QUERY),
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", content =
                    {@Content(schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = """
                                    {
                                      "code": 200,
                                      "success": true,
                                      "msg": "操作成功",
                                      "data": "1234"
                                    }""")})})
    })
    @GetMapping("/currentWight")
    public R<String> performOperation(@RequestParam String portName) {
        return R.ok(weightScaleService.getCurrentWeight(portName));
    }
}
