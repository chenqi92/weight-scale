package cn.allbs.weightscale.config;

import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * 类 SerialPortConfig
 *
 * @author ChenQi
 * @date 2024/7/1
 */
@Configuration
public class SerialPortConfig {

    @Resource
    private Environment env;

    public Map<String, String> getPortMappings() {
        String profile = env.getProperty("spring.profiles.active", "weisanlu");
        Map<String, String> portMappings = new HashMap<>();
        portMappings.put("COM3", env.getProperty("serial." + profile + ".COM3"));
        portMappings.put("COM4", env.getProperty("serial." + profile + ".COM4"));
        return portMappings;
    }
}
