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
import com.miaogou.util.RedisUtils;


/**
 * 
 * @author weicc
 *
 */
@Controller
@RequestMapping(value = "")
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
	@RequestMapping(value = "User/Login", method = RequestMethod.GET)
	public Map<String, Object> Login(String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			String url=jscode2session.replace("APPID", appid).replace("SECRET", secret).replace("JSCODE", code);
			JSONObject obj=HttpRequestUtil.httpRequest(url, "GET", null);
			
			if(!obj.has("errcode")){
				
				//获取接口返回的openid 和 session_key
				String openid=obj.getString("openid");
				String session_key=obj.getString("session_key");
				System.out.println("openid:============"+openid);
				//生成第三方session key
				String _3rd_session=UUID.randomUUID().toString();
				Map<String,String> map=new HashMap<String,String>();
				map.put("openid", openid); //oYk__0HbSPT3uWpCyLcEkOfRT2LA
				map.put("session_key", session_key);  //mnbwt+r+pt6n6J5Aq+Lpug==
				
				//存入redis  并设置超时时间为  一个小时
				RedisUtils.addMap(_3rd_session, map, 60*60);
				
				
				retMap.put("errcode", "0");
				retMap.put("errmsg", "ok");
				retMap.put("_3rd_session", _3rd_session);
				
				
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
	
	/**
	 * 根据商品分类查询相对应的商品列表（分页）
	 * @param goodsClass 商品分类code
	 * @param pageSize 每页大小
	 * @param pageNum 第几页
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/findGoodsByClass", method = RequestMethod.GET)
	public Map<String, Object> findGoodsByClass(String goodsClass,int pageSize,int pageNum,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=UserService.findGoodsByClass(goodsClass,pageSize,pageNum);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		
		return retMap;
	}
	
	
	/**
	 * 获取首页banner广告
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getBannerAd", method = RequestMethod.GET)
	public Map<String, Object> getBannerAd(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=UserService.getBannerAd();
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		
		return retMap;
	}
	
	/**
	 * 增加收货地址
	 * @param _3rd_session user/login  接口返回的后台维护的第三方session   里面保存了这个用户的openId
	 * @param name  收件人姓名
	 * @param phone 收件人电话
	 * @param area 收件人地区
	 * @param address 收件人详细地址
	 * @param postCode 收件人邮编
	 * @param request
	 * @param response
	 * @return  如果errcode 为-5  则说明session过期需要重新 调用user/login 接口重新获取 _3rd_session
	 */
	@SuppressWarnings("unchecked")
	@ResponseBody
	@RequestMapping(value = "addDeliveryAddress", method = RequestMethod.POST)
	public Map<String, Object> addDeliveryAddress(
			 String _3rd_session,String name,String phone,String area,String address,String postCode,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		String openId=new String("");
		try {
			Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.addDeliveryAddress(openId,name,phone,area,address,postCode);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		
		return retMap;
	}
	
	/**
	 * 获取收货地址列表
	 * @param _3rd_session
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getDeliveryAddress", method = RequestMethod.GET)
	public Map<String, Object> getDeliveryAddress(
			 String _3rd_session,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		String openId=new String("");
		
		try {
			
			Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.getDeliveryAddress(openId);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 删除收货地址
	 * @param _3rd_session session key
	 * @param code   收货地址主键
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "delDeliveryAddress", method = RequestMethod.GET)
	public Map<String, Object> delDeliveryAddress(
			 String _3rd_session,String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.delDeliveryAddress(openId,code);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 设置默认收货地址
	 * @param _3rd_session session key
	 * @param code   收货地址主键
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "setDefaultDeliveryAddress", method = RequestMethod.GET)
	public Map<String, Object> setDefaultDeliveryAddress(
			 String _3rd_session,String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.setDefaultDeliveryAddress(openId,code);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	public static void main(String[] args) throws Exception {
		/*UUID uuid = UUID.randomUUID();
		System.out.println(uuid);
		System.out.println("013Ar3IQ1TErC61xVGJQ1uNMHQ1Ar3IU");
		String url=jscode2session.replace("APPID", "wxf044dd5db8e29d36").
				     replace("SECRET", "eccb013f72f3882394c40eba57cfc7bd").
				     replace("JSCODE", "013JN7cA1Pjs7h066HcA1HVocA1JN7cJ");
		JSONObject obj=HttpRequestUtil.httpRequest(url, "GET", null);
		System.out.println(obj);*/
		Map<String,String> map=new HashMap<String,String>();
		System.out.println(RedisUtils.getttl("c3666e5d-9169-44c6-832b-0a7e17182f57"));
	}
}
