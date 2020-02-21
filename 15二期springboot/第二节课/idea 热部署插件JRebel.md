# idea 热部署插件JRebel

​		当开始开发web项目的时候，需要频繁的修改web页面，此时如果频繁的重启变得很麻烦，因此，可以在idea中集成JRebel插件，改动代码之后不需要重新启动应用程序。

### 1、安装JRebel

​		（1）在IDEA中一次点击 File->Settings->Plugins->Brows Repositories
​		（2）在搜索框中输入JRebel进行搜索
​		（3）找到JRebel for intellij
​		（4）install
​		（5）安装好之后需要restart IDEA

### 2、激活JRebel

​		JRebel并非是免费的插件，需要激活之后才可以使用

​		（1）生成一个GUID：https://www.guidgen.com/

​		（2）根据反向代理服务器地址拼接激活地址： https://jrebel.qekang.com/{GUID}

​		（3）打开JRebel激活面板，选择Connect to online licensing service.

​		（4）点击work offline

