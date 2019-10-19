package com.mashibing.springboot.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mashibing.springboot.config.WxConfig;
import com.mashibing.springboot.filter.WxAuthFilter;

import weixin.popular.api.MessageAPI;
import weixin.popular.api.QrcodeAPI;
import weixin.popular.api.SnsAPI;
import weixin.popular.bean.message.templatemessage.TemplateMessage;
import weixin.popular.bean.message.templatemessage.TemplateMessageResult;
import weixin.popular.bean.qrcode.QrcodeTicket;
import weixin.popular.bean.sns.SnsToken;
import weixin.popular.bean.user.User;
import weixin.popular.support.TokenManager;

@RestController
@RequestMapping("/msg")
public class MsgController {
	

	private static final Logger logger = LoggerFactory.getLogger(WxAuthFilter.class);

	@Autowired
	WxConfig wxConf;
	
	@RequestMapping("")
	public Object list(@RequestParam Map<String, String> param,HttpServletRequest request,HttpServletResponse resp) throws Exception{
	/**
	 * : eventMessage:weixin.popular.bean.message.EventMessage@4e781af2[toUserName=gh_a32b261573f7,fromUserName=oDk2XxH7AfjF7pW6MvC7a2lXKgWg,createTime=1565789934,msgType=event,event=CLICK,eventKey=V1001_TODAY_MUSIC,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:38:58.214  INFO 22868 --- [nio-8080-exec-8] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD

2019-08-14 21:41:39.567  INFO 22868 --- [nio-8080-exec-8] c.m.springboot.filter.WxAuthFilter       : user:weixin.popular.bean.user.User@6f0be558[subscribe=<null>,openid=oDk2XxLJuc0usaoVYDz_QiYot-is,nickname=倾听&雨落￡,nickname_emoji=<null>,sex=1,language=zh_CN,city=成都,province=四川,country=中国,headimgurl=http://thirdwx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTKepZsBOziaLbfCsR8lv5ic8fNRDKib5ibibBssLHMFiaDda3M7UhxSyQ0LSmkSDUfJUea8xv6iabrkCiafVg/132,subscribe_time=<null>,privilege={},unionid=<null>,groupid=<null>,remark=<null>,tagid_list=<null>,subscribe_scene=<null>,qr_scene=<null>,qr_scene_str=<null>,errcode=<null>,errmsg=<null>]
2019-08-14 21:41:42.165  INFO 22868 --- [nio-8080-exec-9] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@5e0a4e77[toUserName=gh_a32b261573f7,fromUserName=oDk2XxB_cxBciteUsNEDHZWacx2A,createTime=1565790098,msgType=event,event=CLICK,eventKey=V1001_GOOD,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:41:42.165  INFO 22868 --- [nio-8080-exec-9] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD



2019-08-14 21:39:02.842  INFO 22868 --- [nio-8080-exec-6] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD
2019-08-14 21:39:04.335  INFO 22868 --- [nio-8080-exec-7] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@32c441f[toUserName=gh_a32b261573f7,fromUserName=oDk2XxLPZKMtaH61dnXRrU6h2VAU,createTime=1565789940,msgType=event,event=CLICK,eventKey=V1001_TODAY_MUSIC,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=<null>,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:39:04.335  INFO 22868 --- [nio-8080-exec-7] c.m.s.controller.WxServiceController     : 24_7mgB663yK
	 */
		
//		TemplateMessage templateMessage = new TemplateMessage();
//		templateMessage.setUrl("http://weixin.duozuiyu.com/profile/my");
//		templateMessage.setTemplate_id("LprHgNECv6J5lCnnZUu6EcB_Q9hTNlT5R29pGkzxP6Y");
//		templateMessage.setTouser("oDk2XxLJuc0usaoVYDz_QiYot-is");
//		TemplateMessageResult messageTemplateSend = MessageAPI.messageTemplateSend(TokenManager.getDefaultToken(), templateMessage );
//	
		/**
		 * O 22868 --- [nio-8080-exec-7] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@521232f9[toUserName=gh_a32b261573f7,fromUserName=oDk2XxEl0HIQy5tAA16Xn76TtJJw,createTime=1565790909,msgType=event,event=SCAN,eventKey=user_id=10000,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=gQHm7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyTEpRa2h5ZWRlbjQxa3RkdDF0Yy0AAgSdElRdAwSAOgkA,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:55:13.809  INFO 22868 --- [nio-8080-exec-7] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD
2019-08-14 21:55:15.226  INFO 22868 --- [nio-8080-exec-8] c.m.s.controller.WxServiceController     : eventMessage:weixin.popular.bean.message.EventMessage@75a3582d[toUserName=gh_a32b261573f7,fromUserName=oDk2XxGS1PIwmOrzEm_eFfpqqo4Q,createTime=1565790911,msgType=event,event=SCAN,eventKey=user_id=10000,msgId=<null>,content=<null>,picUrl=<null>,mediaId=<null>,format=<null>,recognition=<null>,thumbMediaId=<null>,location_X=<null>,location_Y=<null>,scale=<null>,label=<null>,title=<null>,description=<null>,url=<null>,ticket=gQHm7zwAAAAAAAAAAS5odHRwOi8vd2VpeGluLnFxLmNvbS9xLzAyTEpRa2h5ZWRlbjQxa3RkdDF0Yy0AAgSdElRdAwSAOgkA,latitude=<null>,longitude=<null>,precision=<null>,status=<null>,totalCount=<null>,filterCount=<null>,sentCount=<null>,errorCount=<null>,copyrightCheckResult=<null>,expiredTime=<null>,failTime=<null>,failReason=<null>,uniqId=<null>,poiId=<null>,result=<null>,msg=<null>,chosenBeacon=<null>,aroundBeacons=<null>,lotteryId=<null>,money=<null>,bindTime=<null>,connectTime=<null>,expireTime=<null>,vendorId=<null>,shopId=<null>,deviceNo=<null>,keyStandard=<null>,keyStr=<null>,country=<null>,province=<null>,city=<null>,sex=<null>,scene=<null>,regionCode=<null>,reasonMsg=<null>,otherElements=<null>]
2019-08-14 21:55:15.226  INFO 22868 --- [nio-8080-exec-8] c.m.s.controller.WxServiceController     : 24_7mgB663yKAWJUN5qxeIvgsp4oSzqKKbzW8aEw9nHs81Vr5ERH676SO1SZLXn5e3F3ASE-WTCgb143srIBnf6qiX5wgYsZmXNQV8DF_dbY2VDbWv309Gul7lRbL2zh025cp5rmQ404VpQTqFDSBXcAFAYHD

		 * 
		 * 
		 * 
		 */
		
		QrcodeTicket qrcodeCreateTemp = QrcodeAPI.qrcodeCreateTemp(TokenManager.getDefaultToken(), 604800, "user_id=10000");
		System.out.println("qrcodeCreateTemp:" + ToStringBuilder.reflectionToString(qrcodeCreateTemp));
		BufferedImage showqrcode = QrcodeAPI.showqrcode(qrcodeCreateTemp.getTicket());
		
		ByteArrayOutputStream os=new ByteArrayOutputStream();//新建流。
		ImageIO.write(showqrcode, "png", os);//利用ImageIO类提供的write方法，将bi以png图片的数据模式写入流。
		byte b[]=os.toByteArray();//从流中获取数据数组。
		
		
		
		
		resp.getOutputStream().write(b);
		
		return qrcodeCreateTemp;
	}

}
