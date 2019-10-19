/**
 * TODO :
 * @author  : yiming 
 * 2016年9月19日 上午10:02:40 / 精致科技 copyright	
 */
package com.msb.db1.entity;

import weixin.popular.api.TicketAPI;
import weixin.popular.support.TokenManager;
import weixin.popular.util.JsUtil;

public class WxConfig {

	private static boolean debug = Boolean.TRUE;

	
	public static String getWebConfig(String url) {

		String jsticket = TicketAPI.ticketGetticket(TokenManager.getDefaultToken()).getTicket();
		
		
		String config;
		if (debug) {

			config = JsUtil.generateConfigJson(jsticket, Boolean.FALSE, "wx711871aa2b926a2d", url, "getLocation", "startRecord", "stopRecord", "onVoiceRecordEnd", "playVoice", "pauseVoice", "stopVoice", "onVoicePlayEnd", "uploadVoice");
		} else {
			config = JsUtil.generateConfigJson(jsticket, Boolean.FALSE, "wx0b173fa0035cca01", url, "getLocation", "startRecord", "stopRecord", "onVoiceRecordEnd", "playVoice", "pauseVoice", "stopVoice", "onVoicePlayEnd", "uploadVoice");
		}
		return config;
	}

	public static String getTokenString() {

		if (debug) {
			return "piziniao2019";
		} else {
			return "microlife";
		}
	}

	public static String getAppId() {
		
		if (debug) {
			return "wx711871aa2b926a2d";
		} else {
			return "wx0b173fa0035cca01";
		}
	}

	public static String getAppPass() {

		if(debug){
			return "b0d0f3b4cdd5ba3ef3751e771c689278";
		}else{
			
			return "29067bd1d9546c5107d38b1f0d39cb4d";
		}
	}

	public static String getDomain() {

		if(debug){
			return "http://demo.duozuiyu.com";
		}else{
			
			return "http://silianchedui.duozuiyu.com";
		}
	}
}
