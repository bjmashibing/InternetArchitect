# mybatis

MyBatis是一个优秀的持久层框架，它对jdbc的操作数据库的过程进行封装，使开发者只需要关注SQL本身，而不需要花费精力去处理例如注册驱动、创建connection、创建statement、手动设置参数、结果集检索等jdbc繁杂的过程代码。



## 配置文件



### MybatisConfig.xml

  SSM中需要配置

- 数据url
- 数据库连接池
- 映射文件
- 事务

在SpringBoot中整合到property中了

### Mapper.xml

#### namespace

接口绑定 和接口

就可以不用写DAO实现类，Mybatis会通过绑定自动找到要执行的sql语句。

**resultMap**

结果集对应到实体类的字段到属性映射

## xml 方式

## 传统方式

#### xml配置



```xml
<?xml version="1.0" encoding="UTF-8"?>  
<!DOCTYPE configuration PUBLIC "-//mybatis.org//DTD Config 3.0//EN" "http://mybatis.org/dtd/mybatis-3-config.dtd">  
  
<!-- version: $Id$ -->  
<configuration>  
    <!-- 引用JDBC属性的配置文件 -->  
    <properties resource="database.properties" />  
  
    <environments default="development">  
        <environment id="development">  
            <!-- 使用JDBC的事务管理 -->  
            <transactionManager type="JDBC" />  
            <!-- POOLED ：JDBC连接对象的数据源连接池的实现，不直接支持第三方数据库连接池 -->  
            <dataSource type="POOLED">  
                <property name="driver" value="${database.driver}" />  
                <property name="url" value="${database.url}" />  
                <property name="username" value="${database.username}" />  
                <property name="password" value="${database.password}" />  
            </dataSource>  
        </environment>  
  
    </environments>  
  
    <!-- ORM映射文件 -->  
    <mappers>  
        <!-- 注解的方式 -->  
        <mapper class="com.iflytek.dao.mapper.AccountMapper" />  
        <!-- XML的方式 -->  
        <mapper resource="com/mashibing/dao/xml/AccountMapper.xml" />  
        <!-- 这里对于注解，还可以通过<package name="com.mashibing.dao.mapper"/> -->  
    </mappers>  
</configuration>  
```



#### Service 配置



```java
public class AccountService {  

    public boolean insertAccount(Account account) {  
        boolean flag = false;  
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();  
        try {  
            accountMapper accountMapper = sqlSession.getMapper(AccountMapper.class);  
            int result = accountMapper.insert(student);  
            if (result > 0) {  
                flag = true;  
            }  
            sqlSession.commit();  
        } finally {  
            sqlSession.close();  
        }  
        return flag;  
    }  
  

    public Student getAccountById(int id) {  
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();  
        try {  
            AccountMapper AccountMapper = sqlSession.getMapper(AccountMapper.class);  
            return AccountMapper.selectByPrimaryKey(id);  
        } finally {  
            sqlSession.close();  
        }  
    }  
  

    public List<Student> getAllStudents() {  
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();  
        try {  
            StudentMapper StudentMapper = sqlSession.getMapper(StudentMapper.class);  
            return StudentMapper.selectByExample(new StudentExample());  
        } finally {  
            sqlSession.close();  
        }  
    }  
  

    public boolean updateAccount(Account account) {  
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();  
        boolean flag = false;  
        try {  
            AccountMapper AccountMapper = sqlSession.getMapper(AccountMapper.class);  
            int result = AccountMapper.updateByPrimaryKey(Account);  
            if (result > 0) {  
                flag = true;  
            }  
            sqlSession.commit();  
        } finally {  
            sqlSession.close();  
        }  
        return flag;  
  
    }  
  
 
    public boolean deleteAccount(int id) {  
        SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession();  
        boolean flag = false;  
        try {  
            AccountMapper AccountMapper = sqlSession.getMapper(AccountMapper.class);  
            int result = AccountMapper.deleteByPrimaryKey(id);  
            if (result > 0) {  
                flag = true;  
            }  
            sqlSession.commit();  
        } finally {  
            sqlSession.close();  
        }  
        return flag;  
    }  
  
}  
```







## 与SpringBoot整合

### 引入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>2.1.6.RELEASE</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.example</groupId>
	<artifactId>springboot03-mybatis</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>springboot03-mybatis</name>
	<description>Demo project for Spring Boot</description>

	<properties>
		<java.version>1.8</java.version>
	</properties>

	<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
		<dependency>
			<groupId>org.mybatis.spring.boot</groupId>
			<artifactId>mybatis-spring-boot-starter</artifactId>
			<version>2.0.1</version>
		</dependency>

		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
		</dependency>
		
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>

```



### mapper

``` xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.mashibing.springboot.mapper.AccountMapper">
   
   <resultMap type="com.mashibing.springboot.mapper.Account" id="BaseResultMap">
   
   	<result column="login_name" property="loginName"/>
   	<result column="password" property="password"/>
   
   </resultMap>
   
   
    <insert id="save" parameterType="Account">
        INSERT INTO account(login_name,password)
        VALUES
        (
        #{loginName},#{password}
        )
    </insert>
    
    <select id="findAll" resultMap="BaseResultMap">
        select * from account
    </select>
    
</mapper>



```

### application.properties

``` pr
spring.datasource.url=jdbc:mysql://localhost:3306/ssm?characterEncoding=utf8&useSSL=false&serverTimezone=UTC
##数据库用户名
spring.datasource.username=root
##数据库密码
spring.datasource.password=840416

# 用来实例化mapper接口
mybatis.type-aliases-package=com.mashibing.springboot.mapper
# 对应的sql映射
mybatis.mapper-locations=classpath:mybatis/mapper/*.xml
```

### AccountMapper

``` java
package com.mashibing.springboot.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {

	void save(Account account);
}


```

### Account

``` java

public class Account {
	private int id;
	private String loginName;
	private String password;
	private String nickName;
	private int age;
	private String location;
	private int banlance;
	public int getId() {

```



### 显示日志

logging.level.com.mashibing.springboot.mapper=debug



## 注解查询

```java
	@Select("select * from account1")
	List<Account> findAll();

```





## 查找mapper接口

### 在入口加入 MapperScan

@MapperScan("com.mashibing.springboot.mapper")
public class Springboot03MybatisApplication {

### 每一个mapper接口上加入

@Mapper
public interface AccountMapper {

## Mapper 自动生成

### eclipse插件 市场搜素

MyBatis Generator

### 图形化

https://github.com/zouzg/mybatis-generator-gui

## 分页查询



### 依赖

```xml
<!-- https://mvnrepository.com/artifact/com.github.pagehelper/pagehelper-spring-boot-starter -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.12</version>
</dependency>

```

### Service

```java
	public Object findPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		AccountExample example = new AccountExample();
		return mapper.selectByExample(example );
	}
```

