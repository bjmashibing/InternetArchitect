package com.mashibing.springboot.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mashibing.springboot.config.WxConfig;

import weixin.popular.api.MenuAPI;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.message.EventMessage;
import weixin.popular.bean.xmlmessage.XMLImageMessage;
import weixin.popular.bean.xmlmessage.XMLTextMessage;
import weixin.popular.support.ExpireKey;
import weixin.popular.support.TokenManager;
import weixin.popular.support.expirekey.DefaultExpireKey;
import weixin.popular.util.SignatureUtil;
import weixin.popular.util.XMLConverUtil;

@Controller
@RequestMapping("/wxService")
public class WxServiceController {
	// 重复通知过滤
	private static ExpireKey expireKey = new DefaultExpireKey();
	
	@Autowired
	WxConfig wxConfig;
	
	Logger logger = LoggerFactory.getLogger(WxServiceController.class);
	
	
	@RequestMapping("createMenu")
	@ResponseBody
	public BaseResult createMenu() {
		
		String MenuString = "{\r\n" + 
				"     \"button\":[\r\n" + 
				"     {    \r\n" + 
				"          \"type\":\"click\",\r\n" + 
				"          \"name\":\"今日歌曲\",\r\n" + 
				"          \"key\":\"V1001_TODAY_MUSIC\"\r\n" + 
				"      },\r\n" + 
				"      {\r\n" + 
				"           \"name\":\"菜单\",\r\n" + 
				"           \"sub_button\":[\r\n" + 
				"           {    \r\n" + 
				"               \"type\":\"view\",\r\n" + 
				"               \"name\":\"搜索\",\r\n" + 
				"               \"url\":\"http://www.soso.com/\"\r\n" + 
				"            },\r\n" + 
				"            {\r\n" + 
				"                 \"type\":\"miniprogram\",\r\n" + 
				"                 \"name\":\"wxa\",\r\n" + 
				"                 \"url\":\"http://mp.weixin.qq.com\",\r\n" + 
				"                 \"appid\":\"wx286b93c14bbf93aa\",\r\n" + 
				"                 \"pagepath\":\"pages/lunar/index\"\r\n" + 
				"             },\r\n" + 
				"            {\r\n" + 
				"               \"type\":\"click\",\r\n" + 
				"               \"name\":\"赞一下我们\",\r\n" + 
				"               \"key\":\"V1001_GOOD\"\r\n" + 
				"            }]\r\n" + 
				"       }]\r\n" + 
				" }";
		
		String menuString2 = "{\r\n" + 
				"    \"button\": [\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"扫码\",\r\n" + 
				"            \"sub_button\": [\r\n" + 
				"                {\r\n" + 
				"                    \"type\": \"scancode_waitmsg\",\r\n" + 
				"                    \"name\": \"扫码带提示\",\r\n" + 
				"                    \"key\": \"rselfmenu_0_0\",\r\n" + 
				"                    \"sub_button\": []\r\n" + 
				"                },\r\n" + 
				"                {\r\n" + 
				"                    \"type\": \"scancode_push\",\r\n" + 
				"                    \"name\": \"扫码推事件\",\r\n" + 
				"                    \"key\": \"rselfmenu_0_1\",\r\n" + 
				"                    \"sub_button\": []\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"发图\",\r\n" + 
				"            \"sub_button\": [\r\n" + 
				"                {\r\n" + 
				"                    \"type\": \"pic_sysphoto\",\r\n" + 
				"                    \"name\": \"系统拍照发图\",\r\n" + 
				"                    \"key\": \"rselfmenu_1_0\",\r\n" + 
				"                    \"sub_button\": []\r\n" + 
				"                },\r\n" + 
				"                {\r\n" + 
				"                    \"type\": \"pic_photo_or_album\",\r\n" + 
				"                    \"name\": \"拍照或者相册发图\",\r\n" + 
				"                    \"key\": \"rselfmenu_1_1\",\r\n" + 
				"                    \"sub_button\": []\r\n" + 
				"                },\r\n" + 
				"                {\r\n" + 
				"                    \"type\": \"pic_weixin\",\r\n" + 
				"                    \"name\": \"微信相册发图\",\r\n" + 
				"                    \"key\": \"rselfmenu_1_2\",\r\n" + 
				"                    \"sub_button\": []\r\n" + 
				"                }\r\n" + 
				"            ]\r\n" + 
				"        },\r\n" + 
				"        {\r\n" + 
				"            \"name\": \"发送位置\",\r\n" + 
				"            \"type\": \"location_select\",\r\n" + 
				"            \"key\": \"rselfmenu_2_0\"\r\n" + 
				"        }\r\n" + 
				"    ]\r\n" + 
				"}";
		
		BaseResult result = MenuAPI.menuCreate(TokenManager.getDefaultToken(), menuString2);
		return result;
	}
	
	
	/**
	 * 2019-08-12 21:56:25.768  INFO 17076 --- [-nio-700-exec-3] c.m.s.controller.WxServiceController     : -------java.util.LinkedHashMap@2f2abfc5[accessOrder=false,threshold=6,loadFactor=0.75]
2019-08-12 21:56:25.769  INFO 17076 --- [-nio-700-exec-3] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@c630e26[toUserName=gh_d147868bd782,fromUserName=oTfG5uLQfrCXyqWBsJrKiYCzSP7M,createTime=1565618183,msgType=event,event=pic_sysphoto,eventKey=rselfmenu_1_0,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=[[SendPicsInfo: null]]]

2019-08-12 21:56:27.365  INFO 17076 --- [-nio-700-exec-5] c.m.s.controller.WxServiceController     : -------java.util.LinkedHashMap@3de4b61b[accessOrder=false,threshold=6,loadFactor=0.75]
2019-08-12 21:56:27.366  INFO 17076 --- [-nio-700-exec-5] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@4d0c41ed[toUserName=gh_d147868bd782,fromUserName=oTfG5uLQfrCXyqWBsJrKiYCzSP7M,createTime=1565618184,msgType=image,event=<null>,eventKey=<null>,msgId=22414265262016918,content=<null>,
picUrl=http://mmbiz.qpic.cn/mmbiz_jpg/SOtsOG8InB22cOpTg1QibBPuvDd925wiaFlBYI2u73jATVQiavMhSfDqClVNXtmekFNcsPHSJdgqJwnXxM7ibYJ3ibQ/0
,mediaId=nyuaazGe91AH09MVYXQGpIJ_ZOlYci6AkuzmJs_quJmAkidVeugeNf8NwkrxnaBK,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]

	 * @param param
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@RequestMapping("sig")
	@ResponseBody
	public void sig(@RequestParam Map<String, String> param , HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		ServletInputStream inputStream = request.getInputStream();
		ServletOutputStream outputStream = response.getOutputStream();

		// 算出来的签名
		String signature = param.get("signature");
		String echostr = param.get("echostr");
		String timestamp = param.get("timestamp");
		String nonce = param.get("nonce");
		
		// 对称加密  本地
		String token = wxConfig.getTokenString();

		if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(timestamp)) {
			outputStreamWrite(outputStream, "faild request");
			return;
		}

		if (echostr != null) {
			outputStreamWrite(outputStream, echostr);
			return;
		}

		// 验证请求签名
		if (!signature.equals(SignatureUtil.generateEventMessageSignature(token, timestamp, nonce))) {
			System.out.println("The request signature is invalid");
			return;
		}

		if (inputStream != null) {
			// 转换XML
			EventMessage eventMessage = XMLConverUtil.convertToObject(EventMessage.class, inputStream);

			logger.info("eventMessage:" + ToStringBuilder.reflectionToString(eventMessage));
			String key = eventMessage.getFromUserName() + "__" + eventMessage.getToUserName() + "__" + eventMessage.getMsgId() + "__" + eventMessage.getCreateTime();


			if (expireKey.exists(key)) {
				// 重复通知不作处理
				System.err.println("重复通知不作处理");
				return;
			} else {
				expireKey.add(key);
			}
/**
 * erviceController     : eventMessage:weixin.popular.bean.message.EventMessage@2d2a1bd[toUserName=gh_d147868bd782,fromUserName=oTfG5uBhe66GO2jHtQ77OnDuenoY,createTime=1565618365,msgType=event,event=pic_weixin,eventKey=rselfmenu_1_2,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=[[SendPicsInfo: null]]]
2019-08-12 21:59:28.026  INFO 17076 --- [-nio-700-exec-3] c.m.s.controller.WxServiceController     : -------java.util.LinkedHashMap@401988b[accessOrder=false,threshold=6,loadFactor=0.75]
 * 
 * 
 * 
 * 
 * 2019-08-14 21:53:15.270  INFO 22868 --- [io-8080-exec-10] c.m.s.controller
 * 
 * .WxServiceController     : 
 * eventMessage:weixin.popular.bean.message.EventMessage@68e70c39[
 * 
 * toUserName=gh_a32b261573f7,fromUserName=oDk2XxEl0HIQy5tAA16Xn76TtJJw,
 * createTime=1565790791,
 * msgType=event,event=SCAN,eventKey=123,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=gQEt7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAya2lIdmdoZWRlbjQxbHRjdGh0Y0IAAgTdEVRdAwSAOgkA,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]







2019-08-14 21:53:15.270  INFO 22868 --- [io-8080-exec-10] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD
2019-08-14 21:53:17.981  INFO 22868 --- [nio-8080-exec-9] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@36043ec2[toUserName=gh_a32b261573f7,fromUserName=oDk2XxG30De_sf6pmvDBCJKu153Q,createTime=1565790793,msgType=event,event=SCAN,eventKey=123,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=gQEt7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAya2lIdmdoZWRlbjQxbHRjdGh0Y0IAAgTdEVRdAwSAOgkA,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:53:17.981  INFO 22868 --- [nio-8080-exec-9] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD

 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
2019-08-12 21:59:28.027  INFO 17076 --- [-nio-700-exec-3] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@29111c23[toUserName=gh_d147868bd782,fromUserName=oTfG5uBhe66GO2jHtQ77OnDuenoY,createTime=1565618365,msgType=image,event=<null>,eventKey=<null>,msgId=22414264928317727,content=<null>,picUrl=http://mmbiz.qpic.cn/mmbiz_jpg/06ib7Uood3MG6tlVHVWuXgGvia91dVGT802vGk6TaT72picWUdN5LEFCa3V0iaQla8VfAQ5icbCrZvjvxKjOiaE3FLBg/0,mediaId=zYNNuHMhJ0dstBmlXEiBHmo69BZnjFKp6J6MNP8i9ZtLrJFnZKBYE9qEn3Bu2Xp8,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-12 21:59:37.481  INFO 17076 --- [-nio-700-exec-5] c.m.s.controller.WxServiceController     : -------java.util.LinkedHashMap@5e541c85[accessOrder=false,threshold=6,loadFactor=0.75]
 */
		
			
			logger.info(TokenManager.getDefaultToken());
			
			XMLTextMessage xmlTextMessage2 = new XMLTextMessage(eventMessage.getFromUserName(), eventMessage.getToUserName(),
					"请先<a href='http://weixin.duozuiyu.com/profile/my'>完善一下信息</a>");
			xmlTextMessage2.outputStreamWrite(outputStream);
		
			
//			XMLImageMessage xmlImageMessage = new XMLImageMessage(eventMessage.getFromUserName(),eventMessage.getToUserName() , "zYNNuHMhJ0dstBmlXEiBHmo69BZnjFKp6J6MNP8i9ZtLrJFnZKBYE9qEn3Bu2Xp8");
//			
//			xmlImageMessage.outputStreamWrite(outputStream);
			return;
		}
	}
	
	private boolean outputStreamWrite(OutputStream outputStream, String text) {
		try {
			outputStream.write(text.getBytes("utf-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
