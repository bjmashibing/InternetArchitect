

# MYSQL5.7详细安装步骤：

### 0、更换yum源

1、打开 mirrors.aliyun.com，选择centos的系统，点击帮助

2、执行命令：yum install wget -y

3、改变某些文件的名称

```
mv /etc/yum.repos.d/CentOS-Base.repo /etc/yum.repos.d/CentOS-Base.repo.backup
```

4、执行更换yum源的命令

```
wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-6.repo
```

5、更新本地缓存

yum clean all

yum makecache

### 1、查看系统中是否自带安装mysql

```shell
yum list installed | grep mysql
```

![1570541665646](E:\lian\oracle\typora-user-images\1570541665646.png)

### 2、删除系统自带的mysql及其依赖（防止冲突）

```shell
yum -y remove mysql-libs.x86_64
```

![1570541838485](E:\lian\oracle\typora-user-images\1570541838485.png)

### 3、安装wget命令

```
yum install wget -y 
```

![1570541946471](E:\lian\oracle\typora-user-images\1570541946471.png)

### 4、给CentOS添加rpm源，并且选择较新的源

```
wget dev.mysql.com/get/mysql-community-release-el6-5.noarch.rpm
```

![1570542045332](E:\lian\oracle\typora-user-images\1570542045332.png)

### 5、安装下载好的rpm文件

```
 yum install mysql-community-release-el6-5.noarch.rpm -y
```

![1570542254949](E:\lian\oracle\typora-user-images\1570542254949.png)

### 6、安装成功之后，会在/etc/yum.repos.d/文件夹下增加两个文件

![1570542341604](E:\lian\oracle\typora-user-images\1570542341604.png)

### 7、修改mysql-community.repo文件

原文件：

![1570542415955](E:\lian\oracle\typora-user-images\1570542415955.png)

修改之后：

![1570542471948](E:\lian\oracle\typora-user-images\1570542471948.png)

### 8、使用yum安装mysql

```
yum install mysql-community-server -y
```

![1570542688796](E:\lian\oracle\typora-user-images\1570542688796.png)

### 9、启动mysql服务并设置开机启动

```shell
#启动之前需要生成临时密码，需要用到证书，可能证书过期，需要进行更新操作
yum update -y
#启动mysql服务
service mysqld start
#设置mysql开机启动
chkconfig mysqld on
```

### 10、获取mysql的临时密码

```shell
grep "password" /var/log/mysqld.log
```

![1570604493708](E:\lian\oracle\typora-user-images\1570604493708.png)

### 11、使用临时密码登录

```shell
mysql -uroot -p
#输入密码
```

### 12、修改密码

```sql
set global validate_password_policy=0;
set global validate_password_length=1;
ALTER USER 'root'@'localhost' IDENTIFIED BY '123456';
```

### 13、修改远程访问权限

```sql
grant all privileges on *.* to 'root'@'%' identified by '123456' with grant option;
flush privileges;
```

### 14、设置字符集为utf-8

```shell
#在[mysqld]部分添加：
character-set-server=utf8
#在文件末尾新增[client]段，并在[client]段添加：
default-character-set=utf8
```

