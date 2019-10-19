# 《SpringBoot 2.x》



[TOC]







**SpringDataJpa与ThymeLeaf**

## 课程主要内容

- SpringDataJpa进阶使用
-  SpringDataJpa自定义查询
-  整合Servlet、Filter、Listener
-  文件上传
- Thymeleaf常用标签





# SpringBoot整合

## 整合Servlet

### 注解方式

**启动类上添加注解**

```java
@SpringBootApplication
== @ServletComponentScan ==
    
public class Springboot011Application {

public static void main(String[] args) {
	SpringApplication.run(Springboot011Application.class, args);
}

}
```



**Servlet类**

``` java
@WebServlet(name = "myServlet",urlPatterns = "/srv",loadOnStartup = 1)
public class MyServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO Auto-generated method stub
        System.out.println("111");
        super.doGet(req, resp);
    }

}
```




### 编码方式

**启动类中添加**

``` java
	@Bean
	public ServletRegistrationBean<MyServlet2> getServletRegistrationBean(){
		ServletRegistrationBean<MyServlet2> bean = new ServletRegistrationBean<>(new MyServlet2(), "/s2");
		bean.setLoadOnStartup(1);
		return bean;
	}
```




这种方式在Servlet中无需注解

### 整合Filter

需要实现接口

```java
implements Filter
```

### 监听器

实现接口 ServletContextListener 

```java 
MyListener implements ServletContextListener
```

# SpringBoot中静态资源访问

可以把静态文件放到以下工程目录下

- src/main/resources/static
- src/main/webapp





# 常用表单数据接收方式

### PathVariable

```java
   @GetMapping(value = "/hello/{id}")
    public String hello(@PathVariable("id") Integer id){
        return "ID:" + id;
}


```
### 实体对象接受

#### JSON数据

```java

@PostMapping(value = "/user")
public User saveUser2(@RequestBody User user) {
    return user;
}
```

#### 普通实体对象

```java
@PostMapping(value = "/user")
public User saveUser2(User user) {
    return user;
}
```



#### 参数名取值

```java
@PostMapping(value = "/post")
public String post(@RequestParam(name = "name") String name,
                   @RequestParam(name = "age") Integer age) {
    String content = String.format("name = %s,age = %d", name, age);
    return content;
}
```





### SpringBoot文件上传

#### 指定上传文件大小

```java
spring.http.multipart.maxFileSize=200MB
spring.http.multipart.maxRequestSize=200MB

spring.servlet.multipart.max-request-size = 200MB
spring.servlet.multipart.max-file-size = 200MB
```

#### Html表单

````html
<form action="fileUploadController" method="post" enctype="multipart/form-data">
		上传文件：<input type="file" name="filename"/><br/>
		<input type="submit"/>
	</form>
````

#### Controller

```java
	@RequestMapping("/fileUploadController")
	public String fileUpload(MultipartFile filename) throws Exception{
		System.out.println(filename.getOriginalFilename());
		filename.transferTo(new File("e:/"+filename.getOriginalFilename()));
		return "ok";
	}
```





# SpringData Jpa进阶使用

## 官方文档：

https://docs.spring.io/spring-data/jpa/docs/2.1.8.RELEASE/reference/html/

## 接口继承

``` java
public interface AccountRepository extends JpaRepository<Account, Integer>
```

**JpaRepository**接口中已经实现了常用的增删改查分页查询等功能

### 控制台显示SQL

application.properties 中配置``` spring.jpa.show-sql=true ```

## 自定义查询

| 关键字      | 意义                                                         |
| :---------- | ------------------------------------------------------------ |
| And         | 等价于 SQL 中的 and 关键字，比如 findByUsernameAndPassword(String user, Striang pwd)； |
| Or          | 等价于 SQL 中的 or 关键字，比如 findByUsernameOrAddress(String user, String addr)； |
| Between     | 等价于 SQL 中的 between 关键字，比如 findBySalaryBetween(int max, int min)； |
| LessThan    | 等价于 SQL 中的 "<"，比如 findBySalaryLessThan(int max)；    |
| GreaterThan | 等价于 SQL 中的">"，比如 findBySalaryGreaterThan(int min)；  |
| IsNull      | 等价于 SQL 中的 "is null"，比如 findByUsernameIsNull()；     |
| IsNotNull   | 等价于 SQL 中的 "is not null"，比如 findByUsernameIsNotNull()； |
| NotNull     | 与 IsNotNull 等价；                                          |
| Like        | 等价于 SQL 中的 "like"，比如 findByUsernameLike(String user)； |
| NotLike     | 等价于 SQL 中的 "not like"，比如 findByUsernameNotLike(String user)； |
| OrderBy     | 等价于 SQL 中的 "order by"，比如 findByUsernameOrderBySalaryAsc(String user)； |
| Not         | 等价于 SQL 中的 "！ ="，比如 findByUsernameNot(String user)； |
| In          | 等价于 SQL 中的 "in"，比如 findByUsernameIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数； |
| NotIn       | 等价于 SQL 中的 "not in"，比如 findByUsernameNotIn(Collection<String> userList) ，方法的参数可以是 Collection 类型，也可以是数组或者不定长参数； |





## 自定义SQL  @Query注解

### 占位符

``` java
public interface UserDao extends Repository<AccountInfo, Long> { 

@Query("select a from AccountInfo a where a.accountId = ?1") 
public AccountInfo findByAccountId(Long accountId); 

@Query("select a from AccountInfo a where a.balance > ?1") 
public Page<AccountInfo> findByBalanceGreaterThan(Integer balance,Pageable pageable); 
}
```




### 参数名

```java
public interface UserDao extends Repository<AccountInfo, Long> { 

    public AccountInfo save(AccountInfo accountInfo); 

    @Query("from AccountInfo a where a.accountId = :id") 
    public AccountInfo findByAccountId(@Param("id")Long accountId); 

    @Query("from AccountInfo a where a.balance > :balance") 
    public Page<AccountInfo> findByBalanceGreaterThan(@Param("balance")Integer balance,Pageable pageable); 
}
```





### 更新操作

``` java
@Modifying 
@Query("update AccountInfo a set a.salary = ?1 where a.salary < ?2") 
public int increaseSalary(int after, int before);
```



### 直接使用Native SQL

设置属性 **nativeQuery = true**

``` java
public interface UserRepository extends JpaRepository<User, Long> {

  @Query(value = "SELECT * FROM USERS WHERE EMAIL_ADDRESS = ?1", nativeQuery = true)
  User findByEmailAddress(String emailAddress);
}
```





# Thymeleaf

## Eclipse自动提示插件

## 官方文档
https://www.thymeleaf.org/doc/tutorials/2.1/usingthymeleaf.html#base-objects

### 安装地址：

http://www.thymeleaf.org/eclipse-plugin-update-site/

### Html中添加约束

```html
<html xmlns:th="http://www.thymeleaf.org">
```



## URL地址处理

### @{...}

1）@{userList} 相对当前路径结果为：http://localhost/thymeleaf/user/userList

2）@{./userList} 相对当前路径结果为：http://localhost/thymeleaf/user/userList

3）@{../tiger/home} 相对当前路径结果为：http://localhost/thymeleaf/tiger/home

4）@{/tiger/home} 相对应用根目录结果为：http://localhost/thymeleaf/tiger/home

5）@{https://www.baidu.com/} 绝对路径结果为：https://www.baidu.com

6）``` <link type="text/css" rel="stylesheet" th:href="@{/css/home.css}">```

@ 以 "/" 开头定位到项**目根路径**，否则使用相对路径

### th:href

```html
<body>
<a th:href="@{userList(id=1)}">1、@{userList(id=9527)}</a>
<a th:href="@{userList(id=1,name=华安)}">2、@{userList(id=1,name=yoyo)}</a>
<a th:href="@{userList(id=1,name=${userName})}">3、@{userList(id=1,name=${userName})}</a>
</body>

```

### th:text 文本

空格属于特殊字符，必须使用单引号包含整个字符串

``` html
<p class="css1 css2" th:class="'css1 css2'">样式</p>
<p th:text="'Big China'">中国</p>
<p th:text="${userName}">userName</p>
<p th:text="'small smile'+',very good.' + ${userName}">temp</p>
```




#### 数字计算
``` html
<p th:text="80">8</p>
<p th:text="8+8">8 + 8</p>
<p th:text="8+8+' Love '+9+9">8 + 8+' Love '+9+9</p>
<p th:text="8+8+' Love '+(9+9)">8 + 8+' Love '+(9+9)</p>
<p th:text="100-${age}"></p>
```

#### Boolean判断
``` html
<p th:text="true">布尔</p>
<p th:text="true and false">true and true</p>
<p th:if="${isMarry}">已结婚</p>
<p th:if="${age}&gt;18">已成年</p>
<p th:if="${age}&lt;18">未成年</p>
```

#### 运算
``` html
<p th:text="15 * 4">值为 60 </p>
<p th:text="15 * 4-100/10">值为 50 </p>
<p th:text="100 % 8">值为 4</p>
```

#### 比较
```html
<p th:if="5>3">5 大于 3</p>
<p th:if="5 &gt;4">5 大于 4</p>
<p th:if="10>=8 and 7 !=8">10大于等于8，且 7 不等于 8 </p>
<p th:if="!${isMarry}">!false</p>
<p th:if="not(${isMarry})">not(false)</p>
```

### 三元运算符
```html
<p th:text="7&gt;5?'7大':'5大'">三元运算符</p>
<p th:text="${age}!=null?${age}:'age等于 null'"></p>
<p th:text="${age}!=null?(${age}>=18?'成年':'未成年'):'age等于 null'"></p>
<p th:text="${age2}!=null?${age2}:'age2等于 null'"></p>
<p th:class="${isMarry}?'css2':'css3'">已婚</p>
```


### th:utext转义
```html
map .addAttribute("china", "<b>Chian</b>,USA,UK");
<p th:text="${china}">默认转义</p>
<p th:utext="${china}">不会转义</p>
```


### th:attr 设置属性
HTML5 所有的属性，都可以使用 th:* 的形式进行设置值
```html
<a href="http://baidu.com" th:attr="title='百度'">百度</a>
```
html属性设置
```html

<a href="" th:attr="title='前往百度',href='http://baidu.com'">前往百度</a>
设置 href 属性
<a href="userList.html" th:attr="href=@{/user/userHome}">用户首页</a>
设置 id 属性，data-target 属性 Html 本身是没有的，但允许用户自定义
<a href="#" th:attr="id='9527',data-target='user'">归海一刀</a>

<p th:abc="9527">th:abc="9527"</p>

<p th:xxoo="yoyo">th:xxoo="yoyo"</p>

```

#### Checked selected 

##### Checked
```html
<input type="checkbox" name="option1" checked/><span>是否已婚1？</span>
<input type="checkbox" name="option2" checked="checked"/><span>是否已婚2？</span>


后台传值 ： model.addAttribute("isMarry", true);

<input type="checkbox" name="option3" th:checked="${isMarry}"/><span>是否已婚？</span>
<input type="radio" name="option4" th:checked="${isMarry}"/><span>是否已婚？</span>
<input type="radio" name="option5" th:checked="!${isMarry}"/><span>是否已婚？</span>

```


<!--option3、option4 会选中；option5 不会选中-->
##### select autofocus
``` html 
<select>
	<option>a</option>
	<option th:selected="${isMarry}">已婚</option>
	<option  th:selected="${!isMarry}">未婚</option>

</select>
<input type="text" th:autofocus="false">
<input type="text" th:autofocus="true">
<input type="text" th:autofocus="false">

```





### 日期格式化

    <span th:text="${#dates.format(date, 'yyyy-MM-dd HH:mm')}"></span>
## 循环

JSTL 有一个 **c:foreach**，同理 Thymeleaf 也有一个 th:each。

作用都是一样的，都是用于遍历数组、List、Set、Map 等数据。
在Select上循环

```html
 <option th:each="city : ${list}" th:text="${city.name}" th:selected="${cityName} eq ${city.name}">Peking</option>
```





#### 状态变量 loopStatus

如果不指定 为变量 **Stat**

- index: 当前迭代对象的index（从0开始计算）
- count:  当前迭代对象的index(从1开始计算)   
- size: 被迭代对象的大小     current:当前迭代变量 
- even/odd: 布尔值，当前循环是否是偶数/奇数（从0开始计算）
- first: 布尔值，当前循环是否是第一个   
- last: 布尔值，当前循环是否是最后一个
- 

```html
<tr th:each="city,status : ${list}" th:style="${status.odd}?'background-color:#c2c2c2'">
  		<!-- EL JSTL-->
  		<td th:text = "${status.count}"></td>
  		<td th:text = "${city.id}"></td>
  		<td th:text = "${city.name}"></td>
  	</tr>
```

## 逻辑判断
### If/else

```html
<p th:if="${isMarry}">已婚1</p>
<p th:unless="${isMarry}">未婚</p>

```

### Switch/case 多条件判断
``` html
<div th:switch="1">
    <p th:case="0">管理员</p>
    <p th:case="1">操作员</p>
    <p th:case="*">未知用户</p>
</div>



<div th:switch="-1">
    <p th:case="0">管理员</p>
    <p th:case="*">操作员</p>
    <p th:case="*">未知用户</p>
</div>

<div th:switch="${isMarry}">
    <p th:case="true">已婚</p>
    <p th:case="true">已成年</p>
    <p th:case="false">未婚</p>
</div>

<div th:switch="'For China'">
    <p th:case="'For USA'">美国</p>
    <p th:case="'For UK'">英国</p>
    <p th:case="'For China'">中国</p>
    <p th:case="*">未知国籍</p>
</div>

```
## 内联表达式
[[...]] 等价于 th:text（结果将被 HTML 转义），[(...)] 等价于 th:utext（结果不会执⾏HTML转义）
```html
<p>[[${china}]]</p>
<p>[(${china})]</p>
<p>[[Lo1ve]]</p>
<p>[['I Love You Baby']]</p>
<p>[(9527)]</p>
```
禁⽤内联th:inline ="none"

### 内联 JavaScript
```javascript
<script type="text/javascript" th:inline="javascript">
     var info = [[${info}]];
        var age = [[${age}]];
        var id = [[${id}]];
        var name = [[${name}]];
        console.log(id, name, age, info);
</script>
```
### 前后端分离开发
```javascript
   <script type="text/javascript" th:inline="javascript">
        /**
         * Thymeleaf 将自动忽略掉注释之后 和 分号之前的所有内容,如下为 "前端测试"
         */
        var info = /*[[${info}]]*/ "前端测试";
        console.log(info);
</script>
```
## Servlet作用域中的对象属性
### URL/request
```html
<p>${param.size()}=[[${param.size()}]]</p>
<p>${param.id}=[[${param.id}]]</p>
```

### Session
```html
<p>${session.size()}=[[${session.size()}]]</p>
<p>${session.user.id}=[[${session.user.id}]]</p>
```

