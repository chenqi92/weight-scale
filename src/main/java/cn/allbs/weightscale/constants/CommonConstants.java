package cn.allbs.weightscale.constants;

/**
 * 通用变量
 *
 * @author ChenQi
 * @date 2024/2/26
 */
public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 200;

    /**
     * 成功标识语
     */
    String SUCCESS_MSG = "SUCCESS";

    /**
     * 失败标记
     */
    Integer FAIL = -1;

    /**
     * 失败标识语
     */
    String FAILED_MSG = "ERROR";

    /**
     * 时区设置
     */
    String ASIA_SHANGHAI = "Asia/Shanghai";

    /**
     * spring
     */
    String SPRING = "spring.";

    /**
     * spring.application
     */
    String SPRING_APPLICATION = SPRING + "application.";

    /**
     * spring.application.name
     */
    String SPRING_APPLICATION_NAME = SPRING_APPLICATION + "name";

    /**
     * 时间格式
     */
    String DATETIME_PATTERN = "yyyy年MM月dd日HH时mm分ss秒SSS毫秒";
}
