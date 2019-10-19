# OA 用户模块 03

[TOC]

# YAML

在之前的实例中，我们使用了src/main/resources/application.properties对我们的项目做了个性化的配置，这种配置方式简单明了，也是我们经常使用的

但是应对复杂的商业需求下，多环境和编程化的配置无法得以满足

为此Spring Cloud为我们提供了YAML的配置方式丰富我们的功能和简化我们的开发同时也能简单明了的分辨配置内容。

## 什么是YAML

YAML是“YAML Ain't Markup Language YAML不是一种标记语言”的外语缩写

但为了强调这种语言以数据做为中心，而不是以置标语言为重点，而用返璞词重新命名。它是一种直观的能够被电脑识别的数据序列化格式，是一个可读性高并且容易被人类阅读，容易和脚本语言交互，用来表达资料序列的编程语言。

它是类似于标准通用标记语言的子集XML的数据描述语言，语法比XML简单很多。

## YAML有以下基本规则：

1、大小写敏感 

2、使用缩进表示层级关系 

3、禁止使用tab缩进，只能使用空格键 

4、缩进长度没有限制，只要元素对齐就表示这些元素属于一个层级。 

5、使用#表示注释 

6、字符串可以不用引号标注

## 自定义参数

自定义参数可以让我们在配置文件中定义一些参数以供在程序中使用

在这里我们使用Spring注解的方式实现这个功能

首先创建一个**实体类**来保存一些我们的系统设置

```java
/**
 * 系统配置相关
 * @author Administrator
 *
 */
@Component
public class Config {

	@Value(value = "${config.systemName}")
	private String systemName;

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}
}
```

application.properties中自定义系统变量

如果需要中文字符需要自行转换成Unicode

```
config.systemName=\u0078\u0078\u006f\u006f\u77f3\u5316\u662f\u6211\u5bb6
```

使用注解自动注入

```java
	@Autowired
	Config config;
```

### 参数引用

在application.propertie中的各个参数值是可以相互引用的

我们修改一下之前的配置

```properties
dalao.name=mashibing

dalao.yanzhi=100

dalao.desc=${dalao.name}is a good teacher,bing bu shi yin wei ${dalao.name} de yan zhi = ${dalao.yanzhi} 
```



### 随机数

有些特殊需求，我们不希望设置的属性值是一个固定值，比如服务器随机端口号，某些编号等，我们可以使用${radom}在配置中产生随机int，long或是string

 

${random.int()} = 随机int

${random.long} = 随机long

${random.int(50)} = 50以内的随机数

${random.int(50,100)} = 50~100之间的int随机数

${random.value}= 随机字符串

配置文件中使用

```properties
dalao.xiaodi.zhangyang.yanzhi=${random.int(50,100)}

dalao.xiaodi.zhangyang.xinqing=${random.value}
```

## 外部参入

## 优先级

如果在properties里配置了属性，会覆盖yml里的属性设置。

在微服务架构中经常会使用自动运维部署工具，使用这些工具来启动我们的服务

我们的Spring boot程序通常是使用java –jar的方式来启动运行的

对于服务端口号或是一些其他需要在启动服务的时候才能决定的值，如果在配置中写死或是用随机明显是满足不了需求的

我们可以使用外部参数替换自定义的参数 

```
比如临时决定服务端口：

java -jar demo-0.0.1-SNAPSHOT.jar --server.port=60

颜值同时发生变化：

java -jar demo-0.0.1-SNAPSHOT.jar --server.port=60 --dalao.xiaodi.zhangyang.yanzhi=100

 
```

使用外部配置方式可以让我们在服务启动时改变像服务端口，数据库连接密码，自定义属性值等等 



## 多环境配置

在实际开发中，我们的一套代码可能会被同时部署到开发、测试、生产等多个服务器中，每个环境中诸如数据库密码等这些个性化配置是避免不了的，虽然我们可以通过自动化运维部署的方式使用外部参数在服务启动时临时替换属性值，但这也意味着运维成本增高。

我们可以通过多套配置来避免对于不同环境修改不同的配置属性

命名规则为：

```
Application-*.properties

Application-dev.properties  = 开发环境

Application-test.properties= 测试环境

Application-prod.properties=生成环境
```

 

接下来我们在 application.properties中设置哪套配置生效的开关

使用 **spring.profiles.active=dev**

在使用java –jar的方式启动服务的时候我们就可以通过外部参数改变整套配置了

**java -jar demo-0.0.1-SNAPSHOT.jar -- spring.profiles.active=test**

## 使用YAML完成多环境配置

### 方式一：单一yml文件 配合多propertys文件

```yaml
spring:

  profiles:

    active:

      - prod
```







### 方式二：单一yml文件内配置所有变量

```yaml
server:     

  port: 8881 

spring:

  profiles:

     active:

      - prod

 --- 

spring:     

  profiles: test 

server:     

  port: 8882 

dalao:

  name: mashibing

  yanzhi: 100

  desc: ${dalao.name}is a good teacher,\啊啊啊  bing bu shi yin wei ${dalao.name} de yan zhi = ${dalao.yanzhi} 

  

 ---

spring:

  profiles: dev 

server:     

  port: 8082

dalao:

  name: mashibing

  yanzhi: 100

  desc: ${dalao.name}is a good teacher,开发 bing bu shi yin wei ${dalao.name} de yan zhi = ${dalao.yanzhi} 

 ---

spring:

  profiles: prod 

server:     

  port: 8083

dalao:

  name: mashibing

  yanzhi: 100

  desc: ${dalao.name}is a good teacher,生产  bing bu shi yin wei ${dalao.name} de yan zhi = ${dalao.yanzhi}
```





# 权限管理

### RBAC

基于角色的权限访问控制（Role-Based Access Control）作为传统访问控制（自主访问，强制访问）的有前景的代替受到广泛的关注。在RBAC中，权限与角色相关联，用户通过成为适当角色的成员而得到这些角色的权限。这就极大地简化了权限的管理。在一个组织中，角色是为了完成各种工作而创造，用户则依据它的责任和资格来被指派相应的角色，用户可以很容易地从一个角色被指派到另一个角色。角色可依新的需求和系统的合并而赋予新的权限，而权限也可根据需要而从某角色中回收。角色与角色的关系可以建立起来以囊括更广泛的客观情况。



### 表设计

sql

``` sql
-- MySQL dump 10.13  Distrib 8.0.16, for Win64 (x86_64)
--
-- Host: localhost    Database: ssm
-- ------------------------------------------------------
-- Server version	5.7.26-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
 SET NAMES utf8 ;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `account`
--

DROP TABLE IF EXISTS `account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `account` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `login_name` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `nick_name` varchar(45) DEFAULT NULL,
  `age` int(11) DEFAULT '0',
  `location` varchar(45) DEFAULT NULL,
  `role` varchar(45) DEFAULT 'user',
  PRIMARY KEY (`id`),
  UNIQUE KEY `login_name_UNIQUE` (`login_name`)
) ENGINE=InnoDB AUTO_INCREMENT=40 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `account_role`
--

DROP TABLE IF EXISTS `account_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `account_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account_id` int(11) DEFAULT NULL,
  `role_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id_idx` (`role_id`),
  KEY `account_id_idx` (`account_id`),
  CONSTRAINT `account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `role_id` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `menu`
--

DROP TABLE IF EXISTS `menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `menu` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `roles` varchar(45) DEFAULT NULL,
  `index` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `permission`
--

DROP TABLE IF EXISTS `permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `uri` varchar(45) NOT NULL,
  `name` varchar(45) NOT NULL,
  `c` tinyint(1) DEFAULT '0',
  `r` tinyint(1) DEFAULT '0',
  `u` tinyint(1) DEFAULT '0',
  `d` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`),
  UNIQUE KEY `uri_UNIQUE` (`uri`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role`
--

DROP TABLE IF EXISTS `role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `role` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name_UNIQUE` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `role_permission`
--

DROP TABLE IF EXISTS `role_permission`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `role_permission` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_id` int(11) DEFAULT NULL,
  `permission_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `role_id_idx` (`role_id`),
  KEY `role_id_for_role_permission` (`role_id`),
  KEY `permission_id_idx` (`permission_id`),
  CONSTRAINT `permission_id` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `role_id_p` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-07-04 14:52:33

```

