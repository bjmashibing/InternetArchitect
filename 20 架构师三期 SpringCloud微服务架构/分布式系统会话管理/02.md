# 会话管理

## 密码存储

### 暴力破解/字典/彩虹表

常见密文存储的几种方式：

- 明文
- hash(明文)
- hash(明文 + 盐)

盐的几种实现：

- 用户名 手机号等 每个账户不一样
- 统一的盐
- 随机盐（保存数据库）
- 随机盐（从密码取）

### 防止破解

没有绝对安全的网络，即使拿不到密码 也可以发送重放攻击

- 多次加盐取hash
- 使用更复杂的单向加密算法比如Bcrypt
- 使用https
- 风控系统
  - 二次安全校验
  - 接口调用安全校验
  - 异地登录等
  - 大额转账

### Bcrypt结构

![img](../images/webp)



### 密码加密

接口

PasswordEncoder 

三个方法

```java
	/**
	 * Encode the raw password. Generally, a good encoding algorithm applies a SHA-1 or
	 * greater hash combined with an 8-byte or greater randomly generated salt.
	 用来加密
	 */
	String encode(CharSequence rawPassword);

	/**
	 * Verify the encoded password obtained from storage matches the submitted raw
	 * password after it too is encoded. Returns true if the passwords match, false if
	 * they do not. The stored password itself is never decoded.
	 *
	 * @param rawPassword the raw password to encode and match
	 * @param encodedPassword the encoded password from storage to compare with
	 * @return true if the raw password, after encoding, matches the encoded password from
	 * storage
	 校验密码
	 */
	boolean matches(CharSequence rawPassword, String encodedPassword);

	/**
	 * Returns true if the encoded password should be encoded again for better security,
	 * else false. The default implementation always returns false.
	 * @param encodedPassword the encoded password to check
	 * @return true if the encoded password should be encoded again for better security,
	 * else false.
	 
	 是否需要再次加密
	 */
	default boolean upgradeEncoding(String encodedPassword) {
		return false;
	}
```

实现类

![image-20200511153323518](../images/image-20200511153323518.png)

### 认证方式

不加密

```
	@Bean
	PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}
```

BCrypt

```
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
```



## JDBC用户存储

### 依赖

```
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

### 配置文件

```properties

spring.datasource.username=root
spring.datasource.password=840416
spring.datasource.url=jdbc:mysql:///mq?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
```



### 建表

Spring Security默认情况下需要两张表，用户表和权限表,可以参考

org.springframework.security.core.userdetails.jdbc.users.ddl



### 登录实现及用户注册

```java
	@Bean
	protected UserDetailsService userDetailsService() {
		
		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
		
		if(manager.userExists("yiming")) {
			System.out.println("已注册");
		}else {
			
			manager.createUser(User.withUsername("yiming")
					.password(new BCryptPasswordEncoder().encode("123"))
					.roles("admin")
					.build()
					);
		}
		
		return manager;
	}
```

或者

```java
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		JdbcUserDetailsManager manager = auth.
			jdbcAuthentication()
		.dataSource(dataSource).getUserDetailsService();
		
		manager.createUser(User.withUsername("1112")
					.password(new BCryptPasswordEncoder().encode("123"))
					.roles("admin")
					.build());
```



### mysql

```sql
create table users(username varchar(50) not null primary key,password varchar(500) not null,enabled boolean not null);

create table authorities (username varchar(50) not null,authority varchar(50) not null,constraint fk_authorities_users foreign key(username) references users(username));

create unique index ix_auth_username on authorities (username,authority);
```

## 如何使用mybatis/jpa查询用户

### 自定义用户登录查询

新建一个service实现`UserDetailsService`接口

```
@Service
public class UserService implements UserDetailsService{

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 在这里执行查询
		System.out.println("开始查询数据源。。。");
		
		if(new Random().nextBoolean()) {
			
			throw new LockedException("用户已锁定");
		}else {
			throw new BadCredentialsException("我错了");
		}
	}

}
```

将service注入到配置

```
	@Autowired
	UserService userSrv;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		auth.userDetailsService(userSrv);
		}
```



### 自定义用户权限校验

#### 校验器

```
@Service
public class MyAuthprovider implements AuthenticationProvider {
	
	@Autowired
	UserService userSrv;
	
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		// 密码校验
		
		System.out.println("开始自定义验证~~~~");
		System.out.println(authentication);
		
		//查询用户名
		UserDetails userDetails = userSrv.loadUserByUsername("xxx");
		
		// 密码加密器
		BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
		
		// 密码加密
		String encodePass = passwordEncoder.encode(authentication.getCredentials().toString());
		
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, encodePass, userDetails.getAuthorities());
		
		return authenticationToken;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return true;
	}

}
```



#### 配置校验器

```
		JdbcUserDetailsManager manager = auth.
			jdbcAuthentication()
		.dataSource(dataSource).getUserDetailsService();
		
		
		auth.authenticationProvider(new MyAuthprovider());
```





## 记住我

```
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.rememberMe()
		.and()
		.csrf().disable()
```

## 同一用户多地点登录

此配置和记住我有冲突

### 踢掉其他已登录的用户

```java
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.csrf().disable()
		.sessionManagement()
		.maximumSessions(1);
```

### 禁止其他终端登录

```
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		.and()
		.formLogin()
		.and()
		.csrf().disable()
		.sessionManagement()
		.maximumSessions(1)
		.maxSessionsPreventsLogin(true)
```



及时清理过期session

```
	@Bean
	HttpSessionEventPublisher httpSessionEventPublisher() {
	    return new HttpSessionEventPublisher();
	}
```







## 防火墙

### ip白名单

#### 指定ip可以不登录

```
		http.
		// 哪些 地址需要登录
		authorizeRequests()
		//所有请求都需要验证
		.anyRequest().authenticated()
		
		.antMatchers("/ip1").hasIpAddress("127.0.0.1")
```

#### 禁止ip访问

用Filter 实现、或者用HandlerInterceptor 实现

### StrictHttpFirewall

spring security 默认使用StrictHttpFirewall限制用户请求

#### method

缺省被允许的`HTTP method`有 [`DELETE`, `GET`, `HEAD`, `OPTIONS`, `PATCH`, `POST`, `PUT`]

#### URI

**在其`requestURI`/`contextPath`/`servletPath`/`pathInfo`中，必须不能包含以下字符串序列之一 :**

```
["//","./","/…/","/."]
```



#### 分号

```
;或者%3b或者%3B
// 禁用规则
setAllowSemicolon(boolean)
```



#### 斜杠

```
%2f`或者`%2F
// 禁用规则
setAllowUrlEncodedSlash(boolean)
```

#### 反斜杠

```
\或者%5c或者%5B
// 禁用规则
setAllowBackSlash(boolean)
```

#### 英文句号

```
%2e或者%2E
// 禁用规则
setAllowUrlEncodedPeriod(boolean)
```



#### 百分号

```
%25
// 禁用规则
setAllowUrlEncodedPercent(boolean)
```

#### 防火墙与sql注入

' ; -- % 多数非法字符已经在请求的参数上被禁用

为啥用户名不能有特殊字符

preparestatement 

awf前端拦截

## 自定义配置

### 指定登录的action

```
.loginProcessingUrl("/login")
```
### 指定登录成功后的页面

			//直接访问登录页面时返回的地址,如果访问的是登录页的话返回指定的地址
			.defaultSuccessUrl("/",true)
			 //必须返回指定地址
			.defaultSuccessUrl("/",true)

### 指定错误页

		//指定错误页
		.failureUrl("/error.html?error1")
### 注销登录

#### 默认方式

```
<a href="/logout">GET logout</a>
<br />
<form action="/logout" method="post">
    <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
    <input type="submit" value="POST Logout"/>
</form>
```

#### 自定义url

```
		.and()
		.logout()
		.logoutUrl("/out")
```

### 增加退出处理器

```

		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("退出1");
			}
		})
		
		
		.addLogoutHandler(new LogoutHandler() {
			
			@Override
			public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
				// TODO Auto-generated method stub
				System.out.println("退出2");
			}
		})
```

### 登录成功处理器

不同角色 跳转到不同页面

		.successHandler(new AuthenticationSuccessHandler() {
			
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				// TODO Auto-generated method stub
				
				System.out.println("登录成功1");
				// 根据权限不同，跳转到不同页面
				request.getSession().getAttribute(name)
				request.getRequestDispatcher("").forward(request, response);
			}
		})
其中 Authentication 参数包含了 用户权限信息

### 登录失败处理器

```
		.failureHandler(new AuthenticationFailureHandler() {
			
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				// TODO Auto-generated method stub
				exception.printStackTrace();
				request.getRequestDispatcher(request.getRequestURL().toString()).forward(request, response);
			}
		})
```

可以限制登录错误次数

#### 常见登录异常

![image-20200511181553196](../images/image-20200511181553196.png)

**LockedException** 账户被锁定

**CredentialsExpiredException** 密码过期

**AccountExpiredException** 账户过期

**DisabledException** 账户被禁用

**BadCredentialsException** 密码错误

**UsernameNotFoundException** 用户名错误

## 访问权限

访问权限可以配置URL匹配用户角色或权限

		http.authorizeRequests()
		.antMatchers("/admin/**").hasRole("admin")
		.antMatchers("/user/**").hasRole("user")
	@Bean

### Ant 风格路径表达式

| 通配符 | 说明                    |
| ------ | ----------------------- |
| ?      | 匹配任何单字符          |
| *      | 匹配0或者任意数量的字符 |
| **     | 匹配0或者更多的目录     |

#### 例子

| URL路径            | 说明                                                         |
| ------------------ | ------------------------------------------------------------ |
| /app/*.x           | 匹配(Matches)所有在app路径下的.x文件                         |
| /app/p?ttern       | 匹配(Matches) /app/pattern 和 /app/pXttern,但是不包括/app/pttern |
| /**/example        | 匹配(Matches) /app/example, /app/foo/example, 和 /example    |
| /app/**/dir/file.* | 匹配(Matches) /app/dir/file.jsp, /app/foo/dir/file.html,/app/foo/bar/dir/file.pdf, 和 /app/dir/file.java |
| /**/*.jsp          | 匹配(Matches)任何的.jsp 文件                                 |



#### 最长匹配原则

最长匹配原则(has more characters)
说明，URL请求/app/dir/file.jsp，现在存在两个路径匹配模式/**/*.jsp和/app/dir/*.jsp，那么会根据模式/app/dir/*.jsp来匹配

#### 匹配顺序

security像shiro一样，权限匹配有顺序，比如不能把.anyRequest().authenticated()写在其他规则前面

### 权限继承

​	

	RoleHierarchy roleHierarchy() {
		
		RoleHierarchyImpl impl = new RoleHierarchyImpl();
		impl.setHierarchy("ROLE_admin > ROLE_user");
		
		return impl;
		
	}

### 