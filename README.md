### 表厂线下加盐抽检测试服务
##### 0.项目介绍

该服务实现了表厂生产燃气表的加盐抽检测试，燃气表切换到该服务器，在该服务器上执行一系列的指令后，由指令的执行结果来确定是否切换到燃气正式服务器。

##### 1.开发框架

+ SpringBoot 集成Netty

+ Netty 实现燃气表和该服务之间的通信，通信协议为UDP。
+ 数据加解密使用第三方服务
+ 数据库使用MySQL，使用JDBC连接并操作数据库。



##### 2.目录介绍

![](/images/项目目录.png)

+ images 图片
+ log 日志目录
+ src -> main -> java -> com -> simit -> data 数据库操作接口和类
+ src -> main -> java -> com -> simit -> entity 数据库表对应的实体
+ src -> main -> java -> com -> simit -> netty netty框架和业务逻辑相关的类
+ src -> main -> java -> com -> simit -> netty -> encode 解码器和编码器Handler
+ src -> main -> java -> com -> simit -> netty -> entity -> exception 业务逻辑异常处理类
+ src -> main -> java -> com -> simit -> netty -> entity -> field 指令相关类
+ src -> main -> java -> com -> simit -> netty -> util 工具类
+ src -> main -> java -> com -> simit -> netty -> Datagram 数据包类
+ src -> main -> java -> com -> simit -> netty -> NettyServer netty启动配置类
+ src -> main -> java -> com -> simit -> netty -> ServerHandler 主要业务逻辑类Handler 
+ src -> main -> java -> com -> simit ->  NBTestServerApplication SpringBoot启动类



##### 3.主要业务逻辑

###### 3.1 解码字节流数据

接收字节流，将字节流转换为16进制的字符串。使用该字符串数据包初始化Datagram对象，过程中校验数据包、解密数据包、计算数据包的填充随机数。将初始化后的Datagram对象传递给ChannelPipeline链的下一个ChannelHandler。

###### 3.2测试业务逻辑

+ 收到Datagram对象后，根据命令码的类型发送收到数据应答

+ 按命令测试序列执行命令：常规数据上报 -> <u>升级加密插件</u> -> 升级加密插件应答 -> <u>结束报文</u> -> 常规数据上报 -> <u>远程关阀使能</u> -> 远程关阀使能应答

   -> <u>远程关阀</u> -> 远程关阀应答 -> <u>远程开阀</u> -> 远程开阀应答 -> <u>恢复出厂设置</u>  -> 恢复出厂设置应答 -> <u>修改网络参数</u> -> 修改网络参数应答 -> <u>结束报文</u>。其中，下划线标记的是测试服务器发送给燃气表的指令。测试过程由常规数据上报（主动）和命令以及命令的应答一起组成的测试链条组成，任何一环断掉都会影响整个测试过程（断掉后由非序列上报处理）。

###### 3.3编码字节流数据

将指令或者结束报文数据封装到Datagram中，传入到编码器Handler中，以字节流的形式发送给燃气表。



##### 4.配置文件

###### 4.1 application.properties

```xml
# application
netty.port=8084

# configure
server.decrypt-url=https://129.211.33.130:8443/zky_encryption/decrypts
server.encrypt-url=https://129.211.33.130:8443/zky_encryption/encryption

field.data.manage-word=0000
field.data.upload-period=000A
field.data.target-ip=47.103.45.115
field.data.target-port=8089



# mysql
spring.datasource.url=jdbc:mysql://39.98.112.235:3306/FCT?characterEncoding=utf8
spring.datasource.username=FCT
spring.datasource.password=3yKXEcCaHXctKN3s
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```



4.2 logback.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%red(%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n)</pattern>
        </encoder>
    </appender>

    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
            <charset>utf-8</charset>
        </encoder>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>log/%d{yyyy-MM-dd}.log</fileNamePattern>
        </rollingPolicy>
    </appender>

    <root level="INFO">
        <appender-ref ref="CONSOLE" />
        <appender-ref ref="FILE" />
    </root>
</configuration>
```



##### 5.部署

###### 5.1Maven打包

使用Maven将项目打包成jar文件

###### 5.2Docker部署

+ Dockerfile文件

  ```sh
  # Dockerfile
  FROM java:8
  MAINTAINER ysxu
  ADD target/nb-encrypted-test-server-0.0.1-SNAPSHOT.jar app.jar
  RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
  RUN echo 'Asia/Shanghai' >/etc/timezone
  ENTRYPOINT ["java","-jar","app.jar"]
  ```

+ 创建部署配置

  ![](C:\Users\11983\Desktop\GitHub\nb-encrypted-test-server\images\部署配置.png)



5.3部署项目

![](/images/部署项目.png)

+ 配置好远程docker后，点击Service
+ 右键Docker，点击Deploy



















