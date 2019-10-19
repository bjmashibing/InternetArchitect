#  OA 用户模块

- 项目角色分配
  - 项目经理
    - 只懂业务
    - 跟进进度
    - 人员、财务预算
  - 技术总监
  - 项目组长 teamLeader
  - 后端开发
  - 设计师 UI/UE
  - 产品经理
  - 前端
- 开发流程
  - 需求  %30
  - 设计 %20
  - 实现 %50
- 用户管理
  - 登录
  - 异步提交
  - 对未登录用户做控制
- 前端页面美化一下

## 数据库连接池

### 默认连接池

```java
@Autowired
DataSource dataSource;
	
		System.out.println("数据源>>>>>>" + dataSource.getClass());
        Connection connection;
		try {
			connection = dataSource.getConnection();
			System.out.println("连接>>>>>>>>>" + connection);
			System.out.println("连接地址>>>>>" + connection.getMetaData().getURL());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


```

以前版本，如 Spring Boot 1.5 默认使用 org.apache.tomcat.jdbc.pool.DataSource 作为数据源；

HikariDataSource 号称 Java WEB 当前速度最快的数据源，相比于传统的 C3P0 、DBCP、Tomcat jdbc 等连接池更加优秀；

![1561363553381](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1561363553381.png)

![1561363566787](C:\Users\Administrator\AppData\Roaming\Typora\typora-user-images\1561363566787.png)



```java

spring.datasource.type=com.zaxxer.hikari.HikariDataSource1
## 最小空闲连接数量
spring.datasource.hikari.minimum-idle=5
## 连接池最大连接数，默认是10  池中最大连接数，包括闲置和使用中的连接
spring.datasource.hikari.maximum-pool-size=15
spring.datasource.hikari.auto-commit=true
## 空闲连接存活最大时间，默认600000（10分钟）
spring.datasource.hikari.idle-timeout=30000
## 连接池名称
spring.datasource.hikari.pool-name=22DatebookHikariCP
## 此属性控制池中连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.connection-test-query=SELECT 1
```





### Druid

官方地址

https://github.com/alibaba/druid/tree/master/druid-spring-boot-starter



https://mvnrepository.com/artifact/com.alibaba/druid-spring-boot-starter/1.1.17

#### 加入依赖

```xml
		<!-- https://mvnrepository.com/artifact/com.alibaba/druid-spring-boot-starter -->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid-spring-boot-starter</artifactId>
    <version>1.1.17</version>
</dependency>
```



#### 配置

```java
spring.datasource.type=com.alibaba.druid.pool.DruidDataSource
# 初始化大小，最小，最大
spring.datasource.initial-size=5
spring.datasource.max-active=20
spring.datasource.min-idle=5
# 配置获取连接等待超时的时间
spring.datasource.max-wait=60000
# 配置一个连接在池中最小生存的时间，单位是毫秒
spring.datasource.min-evictable-idle-time-millis=60000
spring.datasource.validation-query=SELECT 1
spring.datasource.validation-query-timeout=2000

```



#### web监控

```java
package com.mashibing.springboot.controller;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;


@Configuration
public class DruidConfig {

    /**
     * 将自定义的 Druid 数据源添加到容器中，不再让 Spring Boot 自动创建
     * 这样做的目的是：绑定全局配置文件中的 druid 数据源属性到 com.alibaba.druid.pool.DruidDataSource
     * 从而让它们生效
     *
     * @return
     * @ConfigurationProperties(prefix = "spring.datasource")：作用就是将 全局配置文件中 前缀为 spring.datasource
     * 的属性值注入到 com.alibaba.druid.pool.DruidDataSource 的同名参数中
     */
    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    /**
     * 配置 Druid 监控 之  管理后台的 Servlet
     * 内置 Servler 容器时没有web.xml 文件，所以使用 Spring Boot 的注册 Servlet 方式
     */
    @Bean
    public ServletRegistrationBean statViewServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(),
                "/druid/*");

        /**
         * loginUsername：Druid 后台管理界面的登录账号
         * loginPassword：Druid 后台管理界面的登录密码
         * allow：Druid 后台允许谁可以访问
         *      initParams.put("allow", "localhost")：表示只有本机可以访问
         *      initParams.put("allow", "")：为空或者为null时，表示允许所有访问
         * deny：Druid 后台拒绝谁访问
         *      initParams.put("deny", "192.168.1.20");表示禁止此ip访问
         */
        Map<String, String> initParams = new HashMap<>();
        initParams.put("loginUsername", "admin");
        initParams.put("loginPassword", "123456");
        initParams.put("allow", "");

        /** 设置初始化参数*/
        bean.setInitParameters(initParams);
        return bean;
    }
}

```

# spring boot +  mybatis 用户管理



## 1. 登录验证

### 登录页面

#### 前端异步提交及登录跳转 js

```javascript
	
			$(".login_btn").click(function(){
							
				console.log("val:" + 	$("#loginName").val())
				console.log("val:" + 	$("#password").val())
				
				var loginName = $("#loginName").val();
				var password = $("#password").val();
				
				if(loginName == '' || password == ''){
					$(".tip").html("用户名或密码不能为空")
					
					$(".tip").css("display","block")
				}else{
					
					var url = "/account/login";
					var method = {loginName:loginName,password:password};
					$.post(url,method,function(data){
						console.log("data:" + JSON.stringify(data))
						
						if(data.code == 200){
							//登录成功
							window.location.href = "/index";
							
						}else{
							
							$(".tip").html(data.msg)
							$(".tip").css("display","block")
						}
					});
					
					console.log("提交")
				}
				
			})
			
```

### 首页

``` html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
<meta charset="UTF-8">
<title>人力资源管理系统</title>

<link rel="stylesheet" th:href="@{/css/bootstrap.min.css}">
<script type="text/javascript" th:src="@{/js/common/jquery.min.js}"></script>
	

<script type="text/javascript" th:src="@{/js/bootstrap.min.js}"></script>
</head>
<body>

<!-- 页头 开始 ↓ -->

<nav class="navbar navbar-default"> 
  <div class="container-fluid">
    <!-- Brand and toggle get grouped for better mobile display -->
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="/">首页</a>
    </div>

    <!--  人员管理  -->
    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
     
      <ul class="nav navbar-nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
         	 人员管理 <span class="caret"></span>
          
          </a>
          <ul class="dropdown-menu">
            <li><a href="#">Action</a></li>
            <li><a href="#">Another action</a></li>
            <li><a href="#">Something else here</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">Separated link</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">One more separated link</a></li>
          </ul>
        </li>
      </ul>
      
    <!--  系统设置  -->
      
       <ul class="nav navbar-nav">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">系统设置 <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="/account/list">用户列表</a></li>
            <li><a href="#">Another action</a></li>
            <li><a href="#">Something else here</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">Separated link</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="#">One more separated link</a></li>
          </ul>
        </li>
      </ul>
      
      <form class="navbar-form navbar-left">
        <div class="form-group">
          <input type="text" class="form-control" placeholder="Search">
        </div>
        <button type="submit" class="btn btn-default">搜索</button>
      </form>
      
      
      <!--  当前用户 已登录 -->
      
      <ul th:if="${session.account}!=null" class="nav navbar-nav navbar-right">
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">
          欢迎回来: 
          [[${session.account}==null?'':${session.account.loginName}]]
          
          
           <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="#">Action</a></li>
            <li><a href="#">Another action</a></li>
            <li><a href="#">Something else here</a></li>
            <li role="separator" class="divider"></li>
            <li><a href="/account/logOut">退出登录</a></li>
          </ul>
        </li>
      </ul>
      
      <!--  当前用户未登录 -->
      
      <ul th:if="${session.account}==null" class="nav navbar-nav navbar-right">
        <li><a href="/login">登录</a></li>
        <li><a href="/register">注册</a></li>
      </ul>
      
      
      
      
    </div><!-- /.navbar-collapse -->
  </div><!-- /.container-fluid -->
</nav>


<!-- 页头 结束 ↑ -->

<div class="jumbotron">
  <h1 align="center">人力资源管理系统</h1>
  <p>...</p>
</div>
  <p>
  <a class="btn btn-primary btn-lg" href="#" role="button">首页</a>
  <a class="btn btn-primary btn-lg" href="#" role="button">登录</a>
  <a class="btn btn-primary btn-lg" href="#" role="button">注册</a>
  <a class="btn btn-primary btn-lg" href="#" role="button">用户管理</a>
  
  
  </p>

</body>
</html>
```



### Filter

```java
package com.mashibing.springboot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.stereotype.Component;

@Component
@WebFilter(urlPatterns = "/*")
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		
		
		HttpServletRequest req = (HttpServletRequest)request;
		Object attribute = req.getSession().getAttribute("account");
		
		
		String loginURI = "/login";
		System.out.println(req.getRequestURI());;
		HttpServletResponse rep = (HttpServletResponse)response;
		
		if(req.getRequestURI().equals(loginURI)) {
			chain.doFilter(request, response);
			return;
		}
		if(null == attribute ) {
			 
			rep.sendRedirect("/login");
			return;
		}else {
			
			chain.doFilter(request, response);
		}
		
		
		
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		
		System.out.println("---init");
		Filter.super.init(filterConfig);
	}
	

}

```

### controller

```java
	@RequestMapping("/login")
	public String login(@RequestParam(required = false) String loginName,@RequestParam(required = false) String password ,
			HttpServletRequest request
			,Model model) {

		boolean canLogin = false;
		if(StringUtils.isEmpty(loginName) || StringUtils.isEmpty(password)) {
			System.out.println("111");
			return "login";
		}
		
		Account account = accSrv.login(loginName,password);
		if(null != account) {
			System.out.println("22");
			request.getSession().setAttribute("account", account);
			return "redirect:list";
		}
		
		System.out.println("canLogin:" + canLogin);
		model.addAttribute("msg", "请重试");
		return "login";
	}
```



### 用户列表

#### html

```html
<table class="table table-hover">
  <tr>
 
    <th data-field="id">ID</th>
    <th data-field="loginName">loginName</th>
    <th data-field="nickName">nickName</th>
    <th data-field="age">age</th>
    <th data-field="location">location</th>
    <th data-field="price">操作</th>
  </tr>
  	<tr th:each="row : ${page.list}">
		<!-- EL JSTL-->
		<td th:text = "${row.id}"></td>
		<td th:text = "${row.loginName}"></td>
		<td th:text = "${row.nickName}"></td>
		<td th:text = "${row.age}"></td>
		<td th:text = "${row.location}"></td>
		<td >crud</td>
	</tr>
```

#### controller

```java
	@RequestMapping("/list")
	public String list (@RequestParam(defaultValue = "asc") String order,@RequestParam(defaultValue = "1")int offset,@RequestParam(defaultValue = "10")int limit,Model model) {
		PageInfo<Account> page = accSrv.findAllByPage(offset,limit,order);
		model.addAttribute("page", page);
		return "list";
	}


	public PageInfo<Account> findAllByPage(int offset, int limit, String order) {
		
		PageHelper.startPage(offset, limit);
		
		AccountExample example = new AccountExample();
		example.setOrderByClause("id " + order);
		List<Account> list = mapper.selectByExample(example );
		
		
		return new PageInfo<Account>(list);
	}
```





## JSON相关操作

### 对象转字符串

JSON.stringify(data)

### 字符串转对象

JSON.parse(jsonBook);

## 常见错误

### The alias 'GeneratedCriteria' is already mapped to the value

由**mybatis.type-aliases-package=com.mashibing.springboot.mapper**引起

解决方法：把实体类和Example分开放





# 作业

## 1 . 把数据库表里的password 加密一下 md5

## 2. 把老师实现的功能 1遍 底线 最好3遍以上

### 3 . 分页做完



### 预习：1.文件上传

### 预习：2.MyBatis puls