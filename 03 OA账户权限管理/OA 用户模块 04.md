# OA 用户模块 04

[TOC]



## 多表增删改查

多对多数据显示不全 只显示一条

```
com.mashibing.springboot.entity.Account@4e4ac998[id=1,loginName=yimingge,password=123,nickName=gege,age=18,location=<null>,role=user,roleList=[Role [Hash = 31358035, id=1, name=管理员, serialVersionUID=1]],permissionList=<null>]
```

因为 字段有重复

解决方案：

- 删掉重复字段
- 修改一个别名
  - resultMap里的映射关系也得改一下



### 单表修改资源载入与数据JSON异步提交

### 多表关联查询 一次性载入用户角色和权限

```sql
SELECT
	a.id as aid, 
	a.login_name ,
	a.password,
	a.location,
	r.id as rid,
	r.name as rname,
    p.id as pid,
    p.uri,
    p.c,
    p.u,
    p.d,
    p.r,
    p.name as pname
FROM account as a 
	inner join account_role as ar 
		on a.id = ar.account_id
    inner join role as r 
		on ar.role_id = r.id
        
	left join role_permission rp 
		on r.id =rp.role_id
	left join permission p 
		on p.id = rp.permission_id
```



### 角色与权限关联更新

## 表单校验

## 代码生成

# 作业:

- 1.修改 和添加搞定
  - 找一个前端框架 美化一下
- 2. crud中的数据带过来
  3. 多表关联查询的sql 写了
  4. account 一次取3个表的值 



单点crud -> 网络 lvs  -> session共享 ->Nginx -> 缓存



dubbo

Springcloud

