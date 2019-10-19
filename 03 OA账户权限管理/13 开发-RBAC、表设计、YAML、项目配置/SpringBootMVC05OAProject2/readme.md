# OA 用户模块 02

## 列表分页

### service

```java
	public PageInfo<Account> findByPage(int pageNum, int pageSize) {

		PageHelper.startPage(pageNum, pageSize);
		
		AccountExample example = new AccountExample();
		PageInfo<Account> pageInfo = new PageInfo<Account>(accMapper.selectByExample(example ),5);
	return	pageInfo;
	}

```
### 前端

``` html
 <li><a th:href="@{'/account/list?pageNum=' + ${accountList.prePage}}">上一页</a></li>
    
    <li th:each="num : ${accountList.navigatepageNums}"><a th:href="@{'/account/list?pageNum='+${num}}">[[${num}]]</a></li>
    
    <li><a th:href="@{'/account/list?pageNum=' + ${accountList.nextPage}}">下一页</a></li>
```





## 增删改查

### 删除

#### html

```html
 <a th:href="@{'javascript:deleteUser('+${account.id}+');'}" >删除</a>
```



#### JS

``` javascript
	function deleteUser(id){
	
		var url = "/account/deleteAccount";
		var args = {accountId:id};
		$.post(url,args,function(data){
			
			console.log(data)
			if(data.code == 200){
				
				window.location.reload();
			}else {
				
				alert("操作失败：" + data.msg)
			}
		})
		
	}

```

#### 确认

##### 纯js

```javascript
			  var r=confirm("Press a button")
			  if (r==true)
			    {
				  console.log("You pressed OK!")
			    }
			  else
			    {
			   console.log("You pressed Cancel!")
			    }
```



##### 模态窗口

```html


    <div id="com-alert" class="modal" style="z-index:9999;display: none;" >  
      <div class="modal-dialog modal-sm">  
        <div class="modal-content">  
          <div class="modal-header">  
            <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">×</span><span class="sr-only">Close</span></button>  
            <h5 class="modal-title"><i class="fa fa-exclamation-circle"></i> [Title]</h5>  
          </div>  
          <div class="modal-body small">  
            <p>[Message]</p>  
          </div>  
          <div class="modal-footer" >  
            <button type="button" class="btn btn-primary ok" data-dismiss="modal">[BtnOk]</button>  
            <button type="button" class="btn btn-default cancel" data-dismiss="modal">[BtnCancel]</button>  
          </div>  
        </div>  
      </div>  
    </div>  
```

##### js



```javascript
$(function () {  
	  window.Modal = function () {  
	    var reg = new RegExp("\\[([^\\[\\]]*?)\\]", 'igm');  
	    var alr = $("#com-alert");  
	    var ahtml = alr.html();  
	  
	    var _tip = function (options, sec) {  
	        alr.html(ahtml);    // 复原  
	        alr.find('.ok').hide();  
	        alr.find('.cancel').hide();  
	        alr.find('.modal-content').width(500);  
	        _dialog(options, sec);  
	          
	        return {  
	            on: function (callback) {  
	            }  
	        };  
	    };  
	  
	    var _alert = function (options) {  
	      alr.html(ahtml);  // 复原  
	      alr.find('.ok').removeClass('btn-success').addClass('btn-primary');  
	      alr.find('.cancel').hide();  
	      _dialog(options);  
	  
	      return {  
	        on: function (callback) {  
	          if (callback && callback instanceof Function) {  
	            alr.find('.ok').click(function () { callback(true) });  
	          }  
	        }  
	      };  
	    };  
	  
	    var _confirm = function (options) {  
	      alr.html(ahtml); // 复原  
	      alr.find('.ok').removeClass('btn-primary').addClass('btn-success');  
	      alr.find('.cancel').show();  
	      _dialog(options);  
	  
	      return {  
	        on: function (callback) {  
	          if (callback && callback instanceof Function) {  
	            alr.find('.ok').click(function () { callback(true) });  
	            alr.find('.cancel').click(function () { return; });  
	          }  
	        }  
	      };  
	    };  
	  
	    var _dialog = function (options) {  
	      var ops = {  
	        msg: "提示内容",  
	        title: "操作提示",  
	        btnok: "确定",  
	        btncl: "取消"  
	      };  
	  
	      $.extend(ops, options);  
	  
	      var html = alr.html().replace(reg, function (node, key) {  
	        return {  
	          Title: ops.title,  
	          Message: ops.msg,  
	          BtnOk: ops.btnok,  
	          BtnCancel: ops.btncl  
	        }[key];  
	      });  
	        
	      alr.html(html);  
	      alr.modal({  
	        width: 250,  
	        backdrop: 'static'  
	      });  
	    }  
	  
	    return {  
	      tip: _tip,  
	      alert: _alert,  
	      confirm: _confirm  
	    }  
	  
	  }();  
	});
function showTip(msg, sec, callback){  
    if(!sec) {  
        sec = 1000;  
    }  
    Modal.tip({  
        title:'提示',  
        msg: msg  
    }, sec);  
    setTimeout(callback, sec);  
}  
  
/** 
 * 显示消息 
 * @param msg 
 */  
function showMsg(msg, callback){  
    Modal.alert({  
        title:'提示',  
        msg: msg,  
        btnok: '确定'  
    }).on(function (e) {  
        if(callback){  
            callback();  
        }  
     });  
}  
  
/** 
 * 模态对话框 
 * @param msg 
 * @returns 
 */  
function showConfirm(msg,callback){  
    //var res = false;  
    Modal.confirm(  
      {  
          title:'提示',  
          msg: msg,  
      }).on( function (e) {  
          callback();  
          //res=true;  
      });  
    //return res;  
}
```







```javascript
function deleteUser(id){
	
		var url = "/account/deleteAccount";
		var args = {accountId:id};
		
		showConfirm("确认要删除吗？", function() {
			$.post(url,args,function(data){
				
				console.log(data)
				if(data.code == 200){
					
					window.location.reload();
				}else {
					
					alert("操作失败：" + data.msg)
				}
			})
		});
	}
```

##### 代码调用

``` javascript
showTip("haha", 1000, function() {});
showMsg("haha", function() {});
showConfirm("haha", function() {});
```



## 表单校验

## 文件上传

```java


spring.resources.static-locations=classpath:/META-INF/resources/,classpath:/resources/,classpath:/static/,classpath:/public/,file:D:/upload/


```



```java
	@RequestMapping("/fileUploadController")
	public String fileUpload (MultipartFile filename,String password) {
		System.out.println("password:" + password);
		System.out.println("file:" + filename.getOriginalFilename());
		try {
			
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
        File upload = new File(path.getAbsolutePath(), "static/upload/");
        
        System.out.println("upload:" + upload);
        
        
        filename.transferTo(new File(upload+"/"+filename.getOriginalFilename()));
        
        
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "profile";
	}
```



## 项目部署

### 添加包名称

**<build>** 的子标签下 添加**<finalName>oa</finalName>**  打包的时候会按照指定名称生产文件

根标签project 下添加 <packaging>war</packaging>指明要打成war包




### 引入依赖 避免Tomcat的包重复

```

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <!--打包的时候可以不用包进去，别的设施会提供。事实上该依赖理论上可以参与编译，测试，运行等周期。
        相当于compile，但是打包阶段做了exclude操作-->
    <scope>provided</scope>
</dependency>

在入口类上添加标记
public class ServletInitializer extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //此处的Application.class为带有@SpringBootApplication注解的启动类
        return builder.sources(Application.class);
    }

}

```

## 批量生成文件

### 导入依赖

pom.xml

``` xml
		
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-generator</artifactId>
    <version>3.1.2</version>
</dependency>

<dependency>
    <groupId>org.freemarker</groupId>
    <artifactId>freemarker</artifactId>
    <version>2.3.28</version>
</dependency>
```

### 官方示例

https://mp.baomidou.com/guide/generator.html



###   自定义注入

``` java
        InjectionConfig cfg = new InjectionConfig() {
            @Override
            public void initMap() {
                HashMap<String, Object> map = new HashMap<>();
                map.put("xxoo", "yimingge");
                this.setMap(map);
            }
        };
```



### 自定义模板

模板文件实际位置

src/main/resources/templates/entity.ftl

```java
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();

        
        templateConfig.setEntity("/templates/entity");

```

### 读取自定义注入内容

 末班文件中加入

``` java
 ${cfg.xxoo}
```



## freemarker 模板

```
${"abc"?cap_first}  //首字母大写
${entity} 实体类名
${table} 表
```

### entity模板

``` java
package ${package.Entity};
import java.io.Serializable;

/**  


 * <p>
  ${cfg.xxoo}
 * ${table.comment!}
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
public class ${entity} implements Serializable {

<#if entitySerialVersionUID>
    private static final long serialVersionUID = 1L;
</#if>


 //TableInfo(importPackages=[java.io.Serializable], convert=false, name=menu, comment=, entityName=Menu, mapperName=MenuMapper, xmlName=MenuMapper, serviceName=IMenuService, serviceImplName=MenuServiceImpl, controllerName=MenuController, fields=[TableField(convert=false, keyFlag=false, keyIdentityFlag=false, name=name, type=varchar(45), propertyName=name, columnType=STRING, comment=, fill=null, customMap=null), TableField(convert=false, keyFlag=false, keyIdentityFlag=false, name=roles, type=varchar(45), propertyName=roles, columnType=STRING, comment=, fill=null, customMap=null), TableField(convert=false, keyFlag=false, keyIdentityFlag=false, name=index, type=varchar(45), propertyName=index, columnType=STRING, comment=, fill=null, customMap=null)], commonFields=[TableField(convert=false, keyFlag=true, keyIdentityFlag=true, name=id, type=int(11), propertyName=id, columnType=INTEGER, comment=, fill=null, customMap=null)], fieldNames=name, roles, index)

<#-- ----------  主键 字段循环遍历  ---------->
<#list table.commonFields as field>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#-- ----------  BEGIN 字段循环遍历  ---------->
<#list table.fields as field>
    private ${field.propertyType} ${field.propertyName};
</#list>
<#------------  END 字段循环遍历  ---------->

<#if !entityLombokModel>
    <#list table.fields as field>
        <#if field.propertyType == "boolean">
            <#assign getprefix="is"/>
        <#else>
            <#assign getprefix="get"/>
        </#if>
    public ${field.propertyType} ${getprefix}${field.capitalName}() {
        return ${field.propertyName};
    }

    <#if entityBuilderModel>
    public ${entity} set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    <#else>
    public void set${field.capitalName}(${field.propertyType} ${field.propertyName}) {
    </#if>
        this.${field.propertyName} = ${field.propertyName};
        <#if entityBuilderModel>
        return this;
        </#if>
    }
    </#list>
</#if>

<#if entityColumnConstant>
    <#list table.fields as field>
    public static final String ${field.name?upper_case} = "${field.name}";

    </#list>
</#if>
<#if activeRecord>
    @Override
    protected Serializable pkVal() {
    <#if keyPropertyName??>
        return this.${keyPropertyName};
    <#else>
        return null;
    </#if>
    }

</#if>


<#list table.commonFields as field>
 
       public void  set${field.propertyName?cap_first}(${field.propertyType} ${field.propertyName}){
    	this.${field.propertyName} = ${field.propertyName};
    };
    
    
      public ${field.propertyType}  get${field.propertyName?cap_first}(){
    
    	return ${field.propertyName};
    };
</#list>

<#-- ----------  get set 字段循环遍历  ---------->
<#list table.fields as field>

       public void  set${field.propertyName?cap_first}(${field.propertyType} ${field.propertyName}){
    	this.${field.propertyName} = ${field.propertyName};
    };
    
    
      public ${field.propertyType}  get${field.propertyName?cap_first}(){
    
    	return ${field.propertyName};
    };
</#list>
<#------------  END 字段循环遍历  ---------->

}

```

``` java

/**
 * <p>
 * ${table.comment!} 前端控制器
 * </p>
 *${table}
 * @author ${author}
 * @since ${date}
 */
@Controller
@RequestMapping("<#if package.ModuleName??>/${package.ModuleName}</#if>/<#if controllerMappingHyphenStyle??>${controllerMappingHyphen}<#else>${table.entityPath}</#if>")

public class ${table.controllerName} {



	@Autowired
	 ${entity}Service ${table.name}Srv;


	@RequestMapping("/delete${entity}")
	@ResponseBody
	public RespStat delete${table.controllerName}(int id) {
		System.out.println("id:" + id);
		RespStat stat = ${table.name}Srv.delete(id);
		return stat;
	}
	
	
	
		@RequestMapping("/list")
		public String list(@RequestParam(defaultValue = "1") int pageNum,@RequestParam(defaultValue = "1" ) int pageSize,Model model) {
		
		PageInfo<${entity}>accountList = ${table.name}Srv.findByPage(pageNum,pageSize);
		
		return "/${table.name}/list";
	}
	
```





后面要讲的

1. 多表关联查询



# 课后作业

- 1. 注册功能 异步
  2. 修改密码
  3. 权限认证
     1. 修改权限 后台 accountController->List 方法里 用户权限的增删改查
     2. 对修改动作 做权限控制
        1. 要不要显示给 User的用户 
        2. 提交删除请求的时候 ，查一下 当前操作用户的权限是不是admin
  4. 翻页功能
  5. 欠下的
  6. bootstrap - table 替换现有 的表格
  7. 修改头像 上传图片
     1. 表里 添加一个字段（图片的完整 url 路径,相对路径（文件名））
     2. 前端页面 修改头像的功能
     3. 有个默认头像 （如果表字段里没有图 显示默认）
  8. 一定要有自学的能力



