# 协议解析
地磅称重解析
同时支持的`连续发送方式`和`指令应答方式`
项目启动后可以通过`localhost:7878/doc.html`查看可以请求的两个接口，返回的数据都为直接的称重数据，当前项目地磅设置单位为Kg
![](https://nas.allbs.cn:9006/cloudpic/2024/07/bf1df4fb8a788f03220bfeb45ed39544.png)


# 获取数据的方式
## 连续发送方式
顾名思义项目启动后会一直接收串口数据，本项目中考虑到连续发送间隔时间太短，一是无意义数据较多，二是cpu负荷较大，所以额外起了线程，每100毫秒接收并解析一次，根据自己需要设置。
调整方式为修改`SerialPortListener`类中的`scheduler.scheduleAtFixedRate(this::readFromPort, 0, 100, TimeUnit.MILLISECONDS);`period即可，现在设置的时`100`,单位为毫秒
获取该数据有两种方式：
- 一种是获取缓存到redis中的`pc:weight:*`,这个`*`代表的是不同地磅缓存的数据，具体定义见application.yml的active和`SerialPortConfig`的`getPortMappings`方法获取的rediskey值
- 第二种是通过接口，`/weight`，这个接口是获取最新的一次称重数据，如果没有称重数据则返回`null`，参数需要传指定的串口名称，比如我当前项目两个串口分别为`COM3`,`COM4`一个进的地磅一个出的地磅

## 指令应答方式
根据指令获取具体数据
只能通过接口获取
接口: `/scale`
传的参数有
- `address`地址，从A~Z
- `operationCode`操作方式，A握手,B读毛重,C读皮重,D读净重
- `portName`串口，比如当前项目的两个串口`COM3`,`COM4`,其他项目可能是`COM1`,`COM2`之类的。

## 实际效果
![](https://nas.allbs.cn:9006/cloudpic/2024/07/4330f270f20a8427864073a6886a82a2.png)
