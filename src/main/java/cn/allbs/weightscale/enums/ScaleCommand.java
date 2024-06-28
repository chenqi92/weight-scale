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

    // 握手
    HANDSHAKE("A", "握手"),
    // 读取毛重
    READ_GROSS_WEIGHT("B", "读取毛重"),
    // 读取皮重
    READ_TARE_WEIGHT("C", "读取皮重"),
    // 读取净重
    READ_NET_WEIGHT("D", "读取净重");

    private final String operationCode;
    private final String description;

    ScaleCommand(String operationCode, String description) {
        this.operationCode = operationCode;
        this.description = description;
    }

    /**
     * 根据操作码获取描述
     *
     * @param operationCode 操作码
     * @return 描述
     */
    public static String getDescriptionByOperationCode(String operationCode) {
        for (ScaleCommand command : ScaleCommand.values()) {
            if (command.getOperationCode().equals(operationCode)) {
                return command.getDescription();
            }
        }
        throw new IllegalArgumentException("Invalid operation code: " + operationCode);
    }
}
