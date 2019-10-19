# Dubbo 02 微信开发

## Dubbo Admin

https://github.com/apache/dubbo-admin



## 原系统微服务改造

### mvc层排除数据源检查

Application 入口程序添加

```
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
```



## 新增微信接口微服务

功能：微信登录

前置条件：微信开放平台

https://open.weixin.qq.com/

可以获取snsapi_login

开发测试环境：公众号

公众号（公众平台）获取的scope只包括两种：snsapi_base 和snsapi_userinfo



### 环境搭建

#### 获取测试账号

https://mp.weixin.qq.com

注册登录后使用测试账号开发

#### 反向代理服务器

主要用于开发中内网穿透

http://www.ngrok.cc/

http://www.natapp.cc/

### API

#### 微信公众平台开发者文档

https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1445241432

#### 微信开放平台（公众号第三方平台开发）

https://open.weixin.qq.com/cgi-bin/showdocument?action=dir_list&t=resource/res_list&verify=1&lang=zh_CN

#### 微信小程序开发文档

https://developers.weixin.qq.com/miniprogram/dev/framework/

#### 微信商户服务中心

https://mp.weixin.qq.com/cgi-bin/readtemplate?t=business/faq_tmpl&lang=zh_CN

#### 微信支付商户平台开发者文档

https://pay.weixin.qq.com/wiki/doc/api/index.html

#### 微信支付H5

https://pay.weixin.qq.com/wiki/doc/api/H5.php?chapter=15_1

#### 微信支付代扣费

https://pay.weixin.qq.com/wiki/doc/api/pap.php?chapter=17_1

#### 微信支付单品优惠

https://pay.weixin.qq.com/wiki/doc/api/danpin.php?chapter=9_201&index=3

### 开发框架

https://github.com/liyiorg/weixin-popular

- TokenAPI access_token 获取
- MediaAPI 多媒体上传下载(临时素材)
- MaterialAPI 永久素材
- MenuAPI 菜单、个性化菜单
- MessageAPI 信息发送（客服消息、群发消息、模板消息）
- PayMchAPI 支付订单、红包、企业付款、委托代扣、代扣费(商户平台版)、分账
- QrcodeAPI 二维码
- SnsAPI 网签授权
- UserAPI 用户管理、分组、标签、黑名单
- ShorturlAPI 长链接转短链接
- TicketAPI JSAPI ticket
- ComponentAPI 第三方平台开发
- CallbackipAPI 获取微信服务器IP地址
- ClearQuotaAPI 接口调用频次清零
- PoiAPI 微信门店 @Moyq5 (贡献)
- CardAPI 微信卡券 @Moyq5 (贡献)Shak
- earoundAPI 微信摇一摇周边 @Moyq5 (贡献)
- DatacubeAPI 数据统计 @Moyq5 (贡献)
- CustomserviceAPI 客服功能 @ConciseA (贡献)
- WxaAPI 微信小程序
- WxopenAPI 微信小程序
- CommentAPI 文章评论留言
- OpenAPI 微信开放平台帐号管理
- BizwifiAPI 微信连WiFi
- ScanAPI 微信扫一扫
- SemanticAPI 微信智能

```
<dependency>
  <groupId>com.github.liyiorg</groupId>
  <artifactId>weixin-popular</artifactId>
  <version>2.8.28</version>
</dependency>
```

## 入口层 -> 域名与高并发

在入口层 加入CDN技术可以提高用户响应时间 让系统能够承受更高并发，分发请求

尤其对 全网加速（海外用户）效果明显

![img](C:\Users\Administrator\Desktop\tmp\域名\01jpg)



### 域名

### DNS

domain name system

DNS是应用层协议，事实上他是为其他应用层协议工作的，包括不限于HTTP和SMTP以及FTP，用于将用户提供的主机名解析为ip地址。

dns集群

![img](https://pic2.zhimg.com/80/607e9d15fd6d5f9d02f6f4b0adb261b9_hd.jpg)

### CDN

![img](https://pic2.zhimg.com/80/607e9d15fd6d5f9d02f6f4b0adb261b9_hd.jpg)

## 微信开发

### 私服验证

```

```



### 菜单管理

创建菜单

```json
{
    "button": [
        {
            "type": "click", 
            "name": "今日歌曲", 
            "key": "V1001_TODAY_MUSIC"
        }, 
        {
            "name": "菜单", 
            "sub_button": [
                {
                    "type": "view", 
                    "name": "搜索", 
                    "url": "http://www.soso.com/"
                }, 
                {
                    "type": "miniprogram", 
                    "name": "wxa", 
                    "url": "http://mp.weixin.qq.com", 
                    "appid": "wx286b93c14bbf93aa", 
                    "pagepath": "pages/lunar/index"
                }, 
                {
                    "type": "click", 
                    "name": "赞一下我们", 
                    "key": "V1001_GOOD"
                }
            ]
        }
    ]
}
```



### 消息回复

文本

```java
XMLTextMessage xmlTextMessage = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), "hi");

xmlTextMessage.outputStreamWrite(outputStream);
```

图

```
String mediaId= "YiHQtRD_fDKEG3-yTOwiGWlqv56-SUW5vfEDeEuAKx9a78337LKlSUmI4T-Cj8ij";
XMLImageMessage xmlImageMessage = new XMLImageMessage(eventMessage.getFromUserName(),eventMessage.getToUserName(),mediaId);
xmlImageMessage.outputStreamWrite(outputStream);
```

连接

```

XMLTextMessage xmlTextMessage2 = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(), "请先<a href='"+wxConf.getAppDomain()+"/h5/account/register'>完善一下信息</a>");
	
```



```
	TemplateMessage msg = new TemplateMessage();

		msg.setTouser("oStlBwHto08mKRIVUod5IHyevJyE");
		msg.setUrl("http://baidu.com");
		msg.setTemplate_id("gj4jA7HoS-1bmGyBK8VedBBQAXAboRJfWxUpbA8HlvM");

		LinkedHashMap<String, TemplateMessageItem> items = new LinkedHashMap<>();

		// 填充模板内容
		items.put("content", new TemplateMessageItem(" 宝宝，你好。", "#000000"));
		msg.setData(items);

		// 发送提醒
		MessageAPI.messageTemplateSend(TokenManager.getToken(wxConf.getAppID()), msg);

```

