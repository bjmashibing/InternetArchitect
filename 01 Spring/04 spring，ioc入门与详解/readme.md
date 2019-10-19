# Spring家族介绍与开发环境

## 课程主要内容

- Spring能做什么？

- Spring开发环境搭建

- HelloWorld程序

## Spring能做什么？

Spring是一个轻量级的控制反转（IoC/di）和面向切面编程(AOP) 的对象容器框架

Spring官网：http://spring.io/ 

Spring 是一个于 2003 年兴起的一个轻量级的 Java 开源开发框架

如今Spring已是Java项目的标配，它极大的简化了开发过程，降低了开发难度。

- 方便解耦，简化开发；

- AOP 编程的支持；

- 声明式事务的支持；

- 方便程序的测试；
-  方便集成各种优秀框架。

**IoC**

Inversion of Control

**AOP** 

Aspect Oriented Programming 

## Spring5新特性

Spring5 是一个重要的版本，距离SpringFramework4差不多四年

![1571502929658](images/1571502929658.png)                     

- 支持JDK8
  - Spring 接口中的默认方法
  - 基于 Java8 反射增强的内部代码改进
  - 在框架代码中使用函数式编程 - lambda表达式 和 stream流

- 响应式编程支持Spring Web Reactive异步的、非阻塞的、事件驱动的服务

- 支持J2EE7
  - Servlet 3.1
  - JMS 2.0
  - JPA 2.1
  - JAX-RS 2.0

- Bean Validation 1.1
- Hibernate 5

- Jackson 2.6

-  EhCache 2.10

- JUnit 5

- Tiles 3

- Kotlin

## 开发环境

### Spring Jar包

#### SpringFramework官网下载地址：

https://repo.spring.io/libs-release-local/org/springframework/spring/

解压缩Jar包可以看见如下结构

![1571503965778](1571503965778.png)

-  Doc  文档 

- Libs Jar包

- Schema XML约束

- license.txt 许可协议

- notice.txt 注意事项

- readme.txt 读我

#### 常用Jar包

| spring-core                         | 框架的基础功能，包括IOC和AOP功能                             |
| ----------------------------------- | ------------------------------------------------------------ |
| spring-aspects                      | 提供了与AspectJ的集成，AspectJ是一个面向切面的框架，它扩展了Java语言。AspectJ定义了AOP语法，它有一个专门的编译器用来生成遵守Java字节编码规范的Class文件。 |
| spring-beans                        | 所有应用都要用到，包含访问配置文件、创建和管理 bean 以及进行 Inversion of Control(控制反转) / Dependency Injection（依赖注入）操作相关的所有类。外部依赖 spring-core |
| spring-context                      |                                                              |
| spring-aop、spring-instrument       | 面向切面编程、植入代理                                       |
| spring-expression                   | 模块提供了强大的表达式语言去支持查询和操作运行时对象图。这是对JSP 2.1规范中规定的统一表达式语言的扩展。该语言支持设置和获取属性值，属性分配，方法调用，访问数组，集合和索引器的内容，逻辑和算术运算，变量命名以及从Spring的IoC容器中以名称检索对象。 它还支持列表投影和选择以及常见的列表聚合。 |
| spring-messaging                    | 消息传递                                                     |
| spring-jdbc、spring-jms、spring-orm | 数据访问支持                                                 |
| spring-jcl                          | Jakarta Commons Logging采用了设计模式中的“适配器模式”，它对外提供统一的接口，然后在适配类中将对日志的操作委托给具体的日志框架。 |
| spring-tx                           | 事务                                                         |
| spring-webmvc、spring-web           | Webmvc框架支持                                               |
| spring-webflux                      | Servlet3.1 + Netty 方式的WebMvc                              |
| spring-websocket                    | 对ws支持                                                     |

#### commons-logging

运行Spring程序额外还需要commons-logging包

http://commons.apache.org/proper/commons-logging/download_logging.cgi

 

### 开发工具

可以使用Eclipse、Idea等

推荐使用官方开发工具STS

https://spring.io/tools

   ![1571504062996](images/1571504062996.png)

### 环境变量

#### window下配置

- 新建->变量名"JAVA_HOME"，变量值"C:\Java\jdk1.8.0_05"（即JDK的安装路径） 

- 编辑->变量名"Path"，在原变量值的最后面加上“;%JAVA_HOME%\bin;%JAVA_HOME%\jre\bin” 

- 新建->变量名“CLASSPATH”,变量值“.;%JAVA_HOME%\lib;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\lib\tools.jar”

在控制台分别输入java，javac，java -version 命令，测试环境是否设置成功

## Hello world

#### 新建java项目

#### 包引入

   ![1571504086302](images/1571504086302.png)

#### 配置文件

在src下新建

**applicationContext.xml**

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
 
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 
xmlns:p="http://www.springframework.org/schema/p"
 
xmlns:mvc="http://www.springframework.org/schema/mvc"
 
xmlns:context="http://www.springframework.org/schema/context"
 
xsi:schemaLocation="http://www.springframework.org/schema/beans
http://www.springframework.org/schema/beans/spring-beans.xsd
http://www.springframework.org/schema/mvc
http://www.springframework.org/schema/mvc/spring-mvc.xsd
http://www.springframework.org/schema/context
http://www.springframework.org/schema/context/spring-context.xsd">
        
        "src/applicationContext.xml"
        <bean id="user" class="com.msb.User"></bean>
</beans>

```





#### Bean

建立准备由Spring管理的Bean

包含两个属性，生成get/set方法

```
	private String name;
	private Integer age;
```





#### 测试类

```
ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
		User user = (User)ctx.getBean("user");
		user.setName("uu");
		user.setAge(18);
		System.out.println(ToStringBuilder.reflectionToString(user));;

```



​           

### ToStringBuilder 工具类

- Commons项目中用来处理Java基本对象方法的工具类包，可以简化很多平时经常要用到的写法，例如判断字符串是否为空等等。

-  是一个最常用的工具，作为jdk的补充。

- Lang下有很多Utils类，提供了若干static方法供调用，涵盖了字符串操作、字符操作、JVM交互操作、归类、异常和位域校验等等。

项目地址

http://commons.apache.org/proper/commons-lang/download_lang.cgi

 