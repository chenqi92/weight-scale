package cn.allbs.weightscale.enums;

import lombok.Getter;

/**
 * 指令枚举
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Getter
public enum ScaleCommand {

    // 读取毛重
    READ_GROSS_WEIGHT("02 41 42 30 33 03", 1, "读取毛重"),
    // 读取皮重
    READ_TARE_WEIGHT("02 41 43 30 32 03", 2, "读取皮重"),
    // 读取净重
    READ_NET_WEIGHT("02 41 44 30 35 03", 3, "读取净重"),
    // 归零
    ZERO_SCALE("02 41 45 30 30 03", 4, "归零"),
    // 去皮
    TARE_SCALE("02 41 46 30 31 03", 5, "去皮"),
    // 校准
    CALIBRATE_SCALE("02 41 47 30 34 03", 6, "校准"),
    // 读取状态
    READ_SCALE_STATUS("02 41 48 30 36 03", 7, "读取状态");

    private final String command;
    private final int operationCode;
    private final String description;

    ScaleCommand(String command, int operationCode, String description) {
        this.command = command;
        this.operationCode = operationCode;
        this.description = description;
    }

    /**
     * 根据操作码获取指令
     *
     * @param operationCode 操作码
     * @return 指令
     */
    public static ScaleCommand fromOperationCode(int operationCode) {
        for (ScaleCommand cmd : ScaleCommand.values()) {
            if (cmd.getOperationCode() == operationCode) {
                return cmd;
            }
        }
        throw new IllegalArgumentException("Invalid operation code: " + operationCode);
    }

}
