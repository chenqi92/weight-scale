package cn.allbs.weightscale.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 类 WeightData
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Data
@Schema(title = "称重数据", name = "WeightData")
public class WeightData {

    @Schema(description = "称重", name = "weight", implementation = String.class, example = "2.0")
    private String weight;

    @Schema(description = "单位", name = "unit", implementation = String.class, example = "kg")
    private String unit;

    @Schema(description = "状态", name = "status", implementation = String.class, example = "")
    private String status;
}
