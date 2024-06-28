package cn.allbs.weightscale.util;

import cn.allbs.weightscale.model.WeightData;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

/**
 * 类 SerialPortUtil
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Slf4j
@UtilityClass
public class SerialPortUtil {

    /**
     * 发送指令
     *
     * @param data 指令数据
     */
    public void processData(String data) {
        // 根据实际文档描述的数据帧格式进行解析
        if (data.startsWith("\u0002") && data.endsWith("\u0003")) {
            // 获取命令类型或数据标识符
            String commandType = data.substring(1, 3);

            switch (commandType) {
                case "AB":
                    WeightData weightData = parseWeightData(data);
                    // 存储解析后的数据
                    log.info("解析的地磅数据: {}", weightData);
                    break;
                // 添加更多的命令类型处理
                default:
                    log.error("未知的命令类型: {}", commandType);
                    break;
            }
        } else {
            log.error("收到的数据格式不正确: {}", data);
        }
    }

    public WeightData parseWeightData(String data) {
        WeightData weightData = new WeightData();

        try {
            // 解析数据部分
            String sign = data.substring(3, 4); // 获取正负号
            String weight = data.substring(4, 10).trim(); // 获取重量值
            String unitCode = data.substring(10, 11); // 获取单位代码
            String statusCode = data.substring(11, 12); // 获取状态代码

            // 根据单位代码设置单位
            String unit = switch (unitCode) {
                case "0" -> "kg"; // 千克
                case "1" -> "t"; // 吨
                default -> "unknown"; // 未知单位
            };

            // 根据状态代码设置状态
            String status = switch (statusCode) {
                case "0" -> "stable"; // 稳定
                case "1" -> "unstable"; // 不稳定
                default -> "unknown"; // 未知状态
            };

            weightData.setWeight(sign + weight); // 设置重量
            weightData.setUnit(unit); // 设置单位
            weightData.setStatus(status); // 设置状态
        } catch (Exception e) {
            log.error("解析数据时出错: {}", e.getMessage());
            weightData.setWeight("error");
            weightData.setUnit("error");
            weightData.setStatus("error");
        }

        return weightData;
    }

    /**
     * 十六进制数的字符串转换为对应的字节数组
     *
     * @param s 十六进制字符串
     * @return 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        s = s.replaceAll("\\s", ""); // 去除所有空格
        int len = s.length();

        // 确保字符串长度为偶数
        if (len % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string: " + s);
        }

        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            int high = Character.digit(s.charAt(i), 16);
            int low = Character.digit(s.charAt(i + 1), 16);
            if (high == -1 || low == -1) {
                throw new IllegalArgumentException("Invalid hex character in string: " + s);
            }
            data[i / 2] = (byte) ((high << 4) + low);
        }
        return data;
    }


    /**
     * 字节数组转换为十六进制字符串
     *
     * @param byteArray 字节数组
     * @return 十六进制字符串
     */
    public static String byteArrayToHexString(byte[] byteArray) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : byteArray) {
            hexString.append(String.format("%02X ", b));
        }
        // 去掉最后一个空格
        return hexString.toString().trim();
    }
}
