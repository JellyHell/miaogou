package com.miaogou.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.miaogou.service.IUserService;
import com.miaogou.util.HttpRequestUtil;


/**
 * 
 * @author weicc
 *
 */
@Controller
@RequestMapping(value = "User")
public class UserController {
	
	
	@Resource
	IUserService UserService;
	
	@Value("${appid}")
    private String appid;
	
	@Value("${secret}")
    private String secret;
	
	
	/**
	 * 根据login获取到的code和appid，secret 获取openid和session_key
	 */
	private final static String jscode2session="https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code"; 
	
	/**
	 * 根据login获取到的code和appid，secret 获取openid和session_key 并生成第三方session作为key保存至本地session
	 * @param code
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/Login", method = RequestMethod.GET)
	public Map<String, Object> getDeptList(String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			String url=jscode2session.replace("APPID", appid).replace("SECRET", secret).replace("JSCODE", code);
			JSONObject obj=HttpRequestUtil.httpRequest(url, "GET", null);
			
			if(!obj.has("errcode")){
				
				//获取接口返回的openid 和 session_key
				String openid=obj.getString("openid");
				String session_key=obj.getString("session_key");
				
				//生成第三方session key
				String _3rd_session=UUID.randomUUID().toString();
				Map<String,String> map=new HashMap<String,String>();
				map.put("openid", openid); //oYk__0HbSPT3uWpCyLcEkOfRT2LA
				map.put("session_key", session_key);  //mnbwt+r+pt6n6J5Aq+Lpug==
				
				//存入session  并设置超时时间为  一个小时
				request.getSession().setAttribute(_3rd_session, map);
				request.getSession().setMaxInactiveInterval(60*60);
				
				retMap.put("errcode", "0");
				retMap.put("errmsg", "ok");
				retMap.put("_3rd_session", _3rd_session);
				
				Map<String,String> t=(Map<String, String>) request.getSession().getAttribute(_3rd_session);
			}else{
				retMap.put("errcode", obj.getInt("errcode"));
				retMap.put("errmsg", obj.getString("errmsg"));
			}
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	public static void main(String[] args) {
		UUID uuid = UUID.randomUUID();
		System.out.println(uuid);
		System.out.println("013Ar3IQ1TErC61xVGJQ1uNMHQ1Ar3IU");
		String url=jscode2session.replace("APPID", "wxf044dd5db8e29d36").
				     replace("SECRET", "eccb013f72f3882394c40eba57cfc7bd").
				     replace("JSCODE", "013JN7cA1Pjs7h066HcA1HVocA1JN7cJ");
		JSONObject obj=HttpRequestUtil.httpRequest(url, "GET", null);
		System.out.println(obj);
	}
}
