package cn.allbs.weightscale.util;

import cn.allbs.weightscale.exception.BhudyException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

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

    /**
     * 解析重量数据（主要是校验）
     *
     * @param data 数据
     * @return 重量
     */
    public static String parseWeightData(byte[] data) {
        if (data.length < 12) {
            throw new BhudyException("串口未接收到数据或者数据长度不够，返回数据内容为" + Arrays.toString(data));
        }
        if (data[0] != 0x02 || data[data.length - 1] != 0x03) {
            throw new IllegalArgumentException("Invalid data format");
        }

        byte[] payload = Arrays.copyOfRange(data, 1, data.length - 3);
        byte xorHigh = data[data.length - 3];
        byte xorLow = data[data.length - 2];
        byte end = data[data.length - 1];

        // 校验异或
        byte calculatedXor = 0;
        for (int i = 1; i < data.length - 3; i++) { // 从第2位到异或校验前一位
            calculatedXor ^= data[i];
        }

        byte expectedXorHigh = (byte) ((calculatedXor >> 4) & 0x0F);
        byte expectedXorLow = (byte) (calculatedXor & 0x0F);

        // Convert XOR values to ASCII
        expectedXorHigh += (byte) ((expectedXorHigh <= 9) ? 0x30 : 0x37);
        expectedXorLow += (byte) ((expectedXorLow <= 9) ? 0x30 : 0x37);

        if (expectedXorHigh != xorHigh || expectedXorLow != xorLow) {
            log.info("校验失败，报文中高四位{},低四位{};主动校验后的高四位:{},低四位{};", xorHigh, xorLow, expectedXorHigh, expectedXorLow);
        }

        return parseWeight(payload);
    }

    /**
     * 解析重量数据
     *
     * @param payload 数据
     * @return 重量
     */
    private static String parseWeight(byte[] payload) {
        if (payload.length != 8) {
            throw new IllegalArgumentException("Invalid payload length for weight data");
        }

        char sign = (char) payload[0];
        String weightValue = new String(Arrays.copyOfRange(payload, 1, 7)).trim();
        int decimalPointPosition = payload[7] - '0';

        // 插入小数点
        StringBuilder weight = new StringBuilder(weightValue);
        if (decimalPointPosition > 0 && decimalPointPosition < weight.length()) {
            weight.insert(weight.length() - decimalPointPosition, '.');
        }

        // 转换为实际数值
        String weightString = sign + weight.toString();

        // 去掉小数位为0的部分
        double weightNumber = Double.parseDouble(weightString);
        if (decimalPointPosition == 0) {
            return String.valueOf((int) weightNumber); // 如果小数位为0，返回整数
        } else {
            return String.valueOf(weightNumber);
        }
    }

    /**
     * 计算异或校验和
     *
     * @param data 数据
     * @return 校验和
     */
    public static byte calculateXorChecksum(byte[] data) {
        byte xor = 0;
        for (byte b : data) {
            xor ^= b;
        }
        return xor;
    }

    /**
     * 生成指令
     *
     * @param address 地址
     * @param command 指令
     * @return 指令字节数组
     */
    public byte[] generateCommand(String address, char command) {
        byte start = 0x02; // 开始符
        byte end = 0x03; // 结束符

        byte addressByte = (byte) address.charAt(0);
        byte commandByte = (byte) command;

        // 计算异或校验
        byte xor = (byte) (addressByte ^ commandByte);
        byte xorHigh = (byte) ((xor >> 4) & 0x0F);
        byte xorLow = (byte) (xor & 0x0F);

        // 高四位和低四位的ASCII码转换
        xorHigh += (byte) ((xorHigh <= 9) ? 0x30 : 0x37);
        xorLow += (byte) ((xorLow <= 9) ? 0x30 : 0x37);

        // 生成字节数组
        return new byte[]{start, addressByte, commandByte, xorHigh, xorLow, end};
    }

    public static void main(String[] args) {
        byte[] data = {
                0x02, // Start byte
                0x2B, // '+'
                0x30, 0x30, 0x31, 0x35, 0x30, 0x30, 0x30, 0x31, // '00150001'
                0x46, // XOR checksum
                0x03  // End byte
        };

        String result = parseWeightData(data);
        System.out.println(result);
    }
}
