[TOC]

# Dubbo03



## restful风格的API

Representational State Transfer，资源表现层状态转换

### 根路径

mashibing.com

### 协议

http://

### 版本

v1

可以直接写在URL上，或者写在header中传递“Accept-Version: v2”

```
 @RequestMapping(headers = "Accept-Version=v2",value = "models",method = RequestMethod.GET)
```





###  用HTTP协议里的动词来实现资源的增删改查

```
GET 用来获取资源，
POST 用来新建资源（也可以用于更新资源）。
DELETE 用来删除资源。 
UPDATE http://api.chesxs.com/v1/fence 更新围栏信息 
```

### 用例

#### 单个资源

http://mashibing.com/api/v1/Users/1   使用Get方法获取id是1的用户数据

```
         正确：GET /model/models/{id}        #获取单个资源

         正确：POST /model/models            #创建单个资源

         正确：PUT /model/models/{id}        #更新单个资源

         正确：DELETE /model/models/{id}   #删除单个资源

         正确：PATCH /model/models/{id}   #更新单个资源（只传差异）

         正确：GET /model/configRuleFile    #获取单个资源（如果仅有一个值时，应采用单数方式）
```

 返回结果：

如果指定的资源并不存在，那么应该返回404 Not Found状态，否则应该返回200 OK状态码

#### 资源集合

对于资源集合，支持以下URL
    
       正确： GET /model/models                             #获取资源列表
    
       正确： GET /model/models?ids={ids}             #批量获取资源列表
    
       正确： DELETE /model/models?ids={ids}       #批量删除资源列表
    
       返回结果：

如果列表为空，则应该空数组



#### 响应结果

|      | 响应状态码 | 含义                 |
| ---- | ---------- | -------------------- |
| 成功 | 200        | 调用成功             |
|      | 201        | 创建成功             |
|      | 204        | 执行成功，但无返回值 |
| 失败 | 400        | 无效请求             |
|      | 401        | 没有登录             |
|      | 403        | 没有权限             |
|      | 404        | 请求的资源不存在     |
|      | 500        | 服务内部错误         |

## swagger（丝袜哥）

Swagger是一个简单但功能强大的API表达工具。它具有地球上最大的API工具生态系统，数以千计的开发人员，使用几乎所有的现代编程语言，都在支持和使用Swagger。使用Swagger生成API，我们可以得到交互式文档，自动生成代码的SDK以及API的发现特性等。

### OpenAPI

OpenAPI规范是Linux基金会的一个项目，试图通过定义一种用来描述API格式或API定义的语言，来规范RESTful服务开发过程。OpenAPI规范帮助我们描述一个API的基本信息

比如：

- 有关该API的一般性描述
- 可用路径（/资源）
- 在每个路径上的可用操作（获取/提交...）
- 每个操作的输入/输出格式

根据OpenAPI规范编写的二进制文本文件，能够像代码一样用任何VCS工具管理起来一旦编写完成，API文档可以作为：

- 需求和系统特性描述的根据
- 前后台查询、讨论、自测的基础
- 部分或者全部代码自动生成的根据
- 其他重要的作用，比如开放平台开发者的手册...

### 资源

#### 官网 

https://swagger.io/

#### 在线编辑器

http://editor.swagger.io/



### 编写API文档

我们可以选择使用JSON或者YAML的语言格式来编写API文档

```yaml
swagger: '2.0'
info:
  version: 1.0.0
  title: mashibing.com api
  description: 马老师的官网接口
  contact:
    name: yiming
    url: http://mashibing.com
    email: 888@qqq.com
  license:
    name: MIT
    url: http://opensource.org/licenses/MIT
schemes: 
  - http
  
host: mashibing.com
basePath: /api/v1

paths:
  /user/{userid}:
    get:
      summary: 获取一个用户
      description: 根据id获取用户信息
      parameters: 
        - name: userid
          in: path
          required: true
          description: 用户id
          type: string
      responses:
        200:
          description: OK
    
  /user:
    get:
      summary: 返回List 包含所有用户
      description: 返回List 包含所有用户
      parameters:
          - name: pageSize
            in: query
            description: 每页显示多少
            type: integer
          - name: pageNum
            in: query
            description: 当前第几页
            type: integer
      
      responses:
        200:
          description: OK
          
          schema:
            type: array
            items: 
              required:
                - username
              properties:
                username:
                  type: string
                password:
                  type: string    
 
```

### 整合SpringBoot 

#### 官方依赖

```
<dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger2</artifactId>
        <version>2.2.2</version>
</dependency>
<dependency>
        <groupId>io.springfox</groupId>
        <artifactId>springfox-swagger-ui</artifactId>
        <version>2.2.2</version>
</dependency>
<dependency>
        <groupId>org.codehaus.jackson</groupId>
        <artifactId>jackson-core-asl</artifactId>
        <version>1.9.13</version>
</dependency>
```

#### 第三方

https://github.com/SpringForAll/spring-boot-starter-swagger

#### 依赖引入

```
<dependency>
    <groupId>com.spring4all</groupId>
    <artifactId>swagger-spring-boot-starter</artifactId>
    <version>1.9.0.RELEASE</version>
</dependency>
```

#### 启用注解



http://localhost:803//v2/api-docs

http://localhost:8080/swagger-ui.html



#### 分组

![1566031879599](C:\Users\Administrator\Desktop\tmp\swagger\1566031879599.png)

```
swagger.docket.controller.title=group-controller
swagger.docket.controller.base-package=com.mashibing.springboot.controller

swagger.docket.restcontroller.title=group-restcontroller
swagger.docket.restcontroller.base-package=com.mashibing.springboot.controller.rest

```

#### 实体模型

```java
	@ApiModelProperty(value = "权限id", name = "id",dataType = "integer",required = true,example = "1")
    private Integer id;
```

#### 接口方法

```java
	@ApiOperation(value = "获取所有权限")
	@RequestMapping(value = "list",method = RequestMethod.GET)
	public List<Permission> list() {
		
		return permissionSrv.findAll();
	}


	@ApiOperation(value = "添加权限")
	@RequestMapping("update")
	public RespStat update(@ApiParam(name="permission",required = true, example = "{json}",value = "权限对象") @RequestBody Permission permission) {
		
		System.out.println("permission:" + ToStringBuilder.reflectionToString(permission));
		permissionSrv.update(permission);
		return RespStat.build(200);
	}
```

接口类描述

```

@Api(value = "用户权限管理",tags={"用户操作接口"})
```

