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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.miaogou.service.IUserService;
import com.miaogou.util.FastdfsUtils;
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
				
				retMap=UserService.insertOrSelectUserId(openid);
				
				//存入redis  并设置超时时间为  一个小时
				//RedisUtils.addMap(_3rd_session, map, 60*60);
				
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
	 * 获取销量榜
	 * @param pageSize 每页大小
	 * @param pageNum  页数
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/getBestSalesGoods", method = RequestMethod.GET)
	public Map<String, Object> getBestSalesGoods(int pageSize,int pageNum,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=UserService.getBestSalesGoods(pageSize,pageNum);
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
	@RequestMapping(value = "ad/getBannerAd", method = RequestMethod.GET)
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
	 * @param userId 
	 * @param name  收件人姓名
	 * @param phone 收件人电话
	 * @param area 收件人地区
	 * @param address 收件人详细地址
	 * @param postCode 收件人邮编
	 * @param request
	 * @param response
	 * @return  如果errcode 为-5  则说明session过期需要重新 调用user/login 接口重新获取 _3rd_session
	 */
	@ResponseBody
	@RequestMapping(value = "addr/addDeliveryAddress", method = RequestMethod.POST)
	public Map<String, Object> addDeliveryAddress(
			 String userId,String name,String phone,String area,String address,String postCode,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		//String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.addDeliveryAddress(userId,name,phone,area,address,postCode);
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
	@RequestMapping(value = "addr/getDeliveryAddress", method = RequestMethod.GET)
	public Map<String, Object> getDeliveryAddress(
			 String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		String openId=new String("");
		
		try {
			
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.getDeliveryAddress(userId);
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
	@RequestMapping(value = "addr/delDeliveryAddress", method = RequestMethod.GET)
	public Map<String, Object> delDeliveryAddress(
			 String userId,String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.delDeliveryAddress(userId,code);
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
	 * @return errcode=-5 session 过期
	 */
	@ResponseBody
	@RequestMapping(value = "addr/setDefaultDeliveryAddress", method = RequestMethod.POST)
	public Map<String, Object> setDefaultDeliveryAddress(
			 String userId,String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.setDefaultDeliveryAddress(userId,code);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 签到
	 * @param _3rd_session
	 * @param request
	 * @param response
	 * @return   errcode=-5 session 过期  ，errcode=-1 已经签过了
	 */
	@ResponseBody
	@RequestMapping(value = "register", method = RequestMethod.POST)
	public Map<String, Object> register(
			 String userId,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.register(userId);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 获取今天签到状态
	 * @param _3rd_session
	 * @param request
	 * @param response
	 * @return status 0 未签到, 1  已签到
	 */
	@ResponseBody
	@RequestMapping(value = "getRegisterStatus", method = RequestMethod.GET)
	public Map<String, Object> getRegisterStatus(
			 String userId,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.getRegisterStatus(userId);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 获取积分列表信息
	 * @param _3rd_session
	 * @param pageSize
	 * @param pageNum
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getScoreList", method = RequestMethod.GET)
	public Map<String, Object> getScoreList(
			 String userId,int pageSize,int pageNum,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.getScoreList(userId,pageSize,pageNum);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 新增心愿单
	 * @param files 附件
	 * @param _3rd_session
	 * @param goodsName 商品名称
	 * @param url 商品示例url
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "upload/wishList", method = RequestMethod.POST)
	public Map<String, Object> uploadWishList(@RequestParam("file") CommonsMultipartFile[] files,
			 String userId,String  goodsName,String url,HttpServletRequest request,HttpServletResponse response) throws Exception{
		
        Map<String,Object> retMap=new HashMap<String,Object>();
		
		String openId=new String("");
		try {
			/*Map<String,String> t=RedisUtils.findMap(_3rd_session);
			openId=t.get("openid");*/
		} catch (Exception e) {
			retMap.put("errcode", "-5");
			retMap.put("errmsg", "该session不存在或者过期,请重新获取_3rd_session");
			return retMap;
		}
		
		try {
			retMap=UserService.uploadWishList(userId,files,goodsName,url);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
		
	}
	
	/**
	 * 获取心愿单列表
	 * @param _3rd_session
	 * @param pageSize
	 * @param pageNum
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "getWishList", method = RequestMethod.GET)
	public Map<String, Object> getWishList(
			 String _3rd_session,int pageSize,int pageNum,HttpServletRequest request,HttpServletResponse response){
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
			retMap=UserService.getWishList(openId,pageSize,pageNum);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 放入购物车
	 * @param _3rd_session
	 * @param goodsCode  商品编码
	 * @param goodsNum   商品数量
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/addToShoppingCart", method = RequestMethod.POST)
	public Map<String, Object> addToShoppingCart(
			 String _3rd_session,String goodsCode,String goodsNum,HttpServletRequest request,HttpServletResponse response){
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
			retMap=UserService.addToShoppingCart(openId,goodsCode,goodsNum);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 获取购物车列表
	 * @param _3rd_session
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/getShoppingCartList", method = RequestMethod.GET)
	public Map<String, Object> getShoppingCartList(
			 String _3rd_session,HttpServletRequest request,HttpServletResponse response){
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
			retMap=UserService.getShoppingCartList(openId);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 新增微信用户
	 * @param _3rd_session
	 * @param nickName  昵称
	 * @param avatarUrl 头像
	 * @param gender 性别
	 * @param province 省份
	 * @param city 城市
	 * @param country 国家
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "User/addWxUser", method = RequestMethod.POST)
	public Map<String, Object> addWxUser(
			 String _3rd_session,String nickName,String avatarUrl,String gender ,String province
			 ,String city,String country,HttpServletRequest request,HttpServletResponse response){
		
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
			retMap=UserService.addWxUser(openId,nickName,avatarUrl,gender,province,city,country);
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
		System.out.println(RedisUtils.getttl("c3666e5d-9169-44c6-832b-0a7e17182f57"));
	}
}
