package cn.allbs.weightscale.handler;

import cn.allbs.weightscale.config.SerialPortConfig;
import cn.allbs.weightscale.constants.CommonConstants;
import cn.allbs.weightscale.util.SerialPortUtil;
import com.fazecast.jSerialComm.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
public class SerialPortListener implements Runnable {

    private final SerialPort serialPort;
    private final String portName;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final SerialPortConfig serialPortConfig;

    public SerialPortListener(SerialPort serialPort, String portName, RedisTemplate<String, Object> redisTemplate, SerialPortConfig serialPortConfig) {
        this.serialPort = serialPort;
        this.portName = portName;
        this.redisTemplate = redisTemplate;
        this.serialPortConfig = serialPortConfig;
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(this::readFromPort, 0, 100, TimeUnit.MILLISECONDS);
    }

    private void readFromPort() {
        try {
            if (serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[12]; // 根据实际数据长度调整缓冲区大小
                int numRead = serialPort.readBytes(readBuffer, readBuffer.length);
                if (numRead > 0) {
                    String result = SerialPortUtil.parseWeightData(readBuffer);
                    log.info("{}读取到串口{}的数据为:{}", LocalDateTime.now().format(DateTimeFormatter.ofPattern(CommonConstants.DATETIME_PATTERN)), portName, result);
                    // 存入Redis
                    redisTemplate.opsForValue().set(serialPortConfig.getPortMappings().get(portName), result);
                }
            }
        } catch (Exception e) {
            log.error("Error reading from serial port", e);
        }
    }

    // 停止监听器的方法
    public void stop() {
        scheduler.shutdown();
    }
}
