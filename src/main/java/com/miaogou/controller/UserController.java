package com.miaogou.controller;

import java.util.HashMap;
import java.util.Map;

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
			
			String url=jscode2session.replace("APPID", appid).replace("SECRET", secret);
			JSONObject obj=HttpRequestUtil.httpRequest(url, "GET", null);
			
			if(!obj.has("errcode")){
				
			}else{
				retMap.put("errcode", "-2");
				retMap.put("errmsg", "系统异常请稍后重试!");
			}
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
}
