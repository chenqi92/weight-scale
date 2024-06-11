package cn.allbs.weightscale.config;

import cn.allbs.weightscale.util.SerialPortUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 类 SerialPortConfig
 *
 * @author ChenQi
 * @date 2024/6/11
 */
@Configuration
public class SerialPortConfig {

    @Bean
    public SerialPortUtil serialPortUtil() {
        return new SerialPortUtil();
    }
}
