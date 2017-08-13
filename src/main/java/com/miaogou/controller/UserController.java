package com.miaogou.controller;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.miaogou.dao.NotifyRet;
import com.miaogou.service.IUserService;
import com.miaogou.util.HttpRequestUtil;
import com.miaogou.util.PayUtil;
import com.miaogou.util.RedisUtils;
import com.miaogou.util.UUIDHexGenerator;
import com.miaogou.util.XmlUtils;


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
	 * 商户id
	 */
	@Value("${mch_id}")
    private String mch_id;
	
	/**
	 * 签名时用到的key
	 */
	@Value("${key}")
    private String key;
	
	/**
	 * 微信支付回调地址
	 */
	@Value("${notify_url}")
    private String notify_url;
	
	/**
	 * 退款时证书物理路径
	 */
	@Value("${p12_position}")
    private String p12_position;
	
	private static Object lock=new Object();
	
	/**
	 * 根据login获取到的code和appid，secret 获取openid和session_key
	 */
	private final static String jscode2session="https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code"; 
	
	/**
	 * 统一下单
	 */
	private final static String unifiedorder_url="https://api.mch.weixin.qq.com/pay/unifiedorder";
	/**
	 * 查询订单
	 */
	private final static String orderquery="https://api.mch.weixin.qq.com/pay/orderquery";
	
	/**
	 * 关闭订单
	 */
	private final static String closeorder="https://api.mch.weixin.qq.com/pay/closeorder";
	
	/**
	 * 退款
	 */
	private final static String refund="https://api.mch.weixin.qq.com/secapi/pay/refund";
	
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
	 * 获取接口详情
	 * @param goods_code 商品code
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/getDetails", method = RequestMethod.GET)
	public Map<String, Object> getDetails(String goods_code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=UserService.getDetails(goods_code);
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
		String ee=request.getParameter("userId");
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
	 * @param userId
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
			retMap=UserService.getWishList(userId,pageSize,pageNum);
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
			 String userId,String goodsCode,String goodsNum,HttpServletRequest request,HttpServletResponse response){
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
			retMap=UserService.addToShoppingCart(userId,goodsCode,goodsNum);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 指定商品从该用户的购物车中减去1个  如果本来还剩一个则进行删除操作
	 * @param userId  用户userId
	 * @param goodsCode  商品code
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/reduce1FromShoppingCart", method = RequestMethod.POST)
	public Map<String, Object> reduce1FromShoppingCart(
			 String userId,String goodsCode,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.reduce1FromShoppingCart(userId,goodsCode);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
		
	}
	
	/**
	 * 根据购物车code删除该购物车该商品  
	 * @param code   购物车列表中返回的code
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "goods/delFromShoppingCart", method = RequestMethod.POST)
	public Map<String, Object> delFromShoppingCart(
			 String code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.delFromShoppingCart(code);
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
			retMap=UserService.getShoppingCartList(userId);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 新增微信用户
	 * @param userId
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
			 String userId,String nickName,String avatarUrl,String gender ,String province
			 ,String city,String country,HttpServletRequest request,HttpServletResponse response){
		
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
			retMap=UserService.addWxUser(userId,nickName,avatarUrl,gender,province,city,country);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 统一下单(预支付)
	 * @param openid  
	 * @param detail 商品信息json格式字符串  exp:  
	 *                               {
									    "goods_detail": [
									        {
									            "goodsCode": "s_goods10001",
									            "goodsNum": 2
									        },
									        {
									            "goodsCode": "s_goods10001",
									            "goodsNum": 3
									        }
									    ]
									 }
	 * @param name 寄件人姓名
	 * @param phone 电话
	 * @param area 地区
	 * @param address 详细地址
	 * @param postCode 邮编
	 * @param total_fee  总费用  单位 ‘分’
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@ResponseBody
	@RequestMapping(value = "pay/unifiedorder", method = RequestMethod.GET)
	public Map<String, Object> unifiedorder(String openid,String detail,String name,
			String phone,String area,String address,String postCode,int total_fee,
			HttpServletRequest request,HttpServletResponse response) throws UnsupportedEncodingException{
		
		//中文编码问题
		name=java.net.URLDecoder.decode(name, "utf-8");
		area=java.net.URLDecoder.decode(area, "utf-8");
		address=java.net.URLDecoder.decode(address, "utf-8");
		
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		Map<String, String> result=new HashMap<String,String>();
		
		Map<String,Object> pa=new HashMap<String,Object>();
		try {
			
		
		//统一下单请求参数
		
		pa.put("appid", appid);   //小程序ID
		pa.put("mch_id", mch_id); //商户号
		pa.put("openid", openid); //openid
		pa.put("nonce_str", UUIDHexGenerator.generate()); //随机字符串
		pa.put("body", "喵购平台商品购买");  //商品描述
		pa.put("out_trade_no", PayUtil.create_out_trade_no()); //商户订单号
		pa.put("total_fee", total_fee);   //总金额
		pa.put("spbill_create_ip", request.getRemoteAddr());  //终端IP
		pa.put("notify_url", notify_url); //通知地址
		pa.put("trade_type", "JSAPI");  //交易类型
		
		//生成签名
		String sign=PayUtil.getSign(pa,key);
		pa.put("sign", sign);
		
		StringBuffer requestXml=new StringBuffer("<xml>");
		for(String k:pa.keySet())
			requestXml.append("<"+k+">"+pa.get(k)+"</"+k+">");
		requestXml.append("</xml>");
		
		result =PayUtil.httpRequest(unifiedorder_url, "POST", requestXml.toString());
	        // 将解析结果存储在HashMap中
	        
		
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		pa.put("prepay_id", result.get("prepay_id"));
		//生产订单成功 插入数据库
		if("SUCCESS".equals(result.get("result_code"))&&"SUCCESS".equals(result.get("return_code"))){
			pa.put("detail", detail);
			pa.put("name", name);
			pa.put("phone", phone);
			pa.put("area", area);
			pa.put("address", address);
			pa.put("postCode", postCode);
			
			try {
				UserService.createOrder(pa);
			} catch (Exception e) {
				e.printStackTrace();
				retMap.put("errcode", "-1");
				retMap.put("errmsg", "操作数据库失败");
				return retMap;
			}
			
				
		}
		
		
		retMap.put("errcode", "0");
		retMap.put("errmsg", "OK");
		retMap.put("data", result);
		return retMap;
	}
	/**
	 * 支付完成回调函数
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 */
	@ResponseBody
	@RequestMapping(value = "pay/notify_url",produces={"application/xml;chrset=UTF-8"}, method = RequestMethod.POST)
	public void unifiedorder(
			HttpServletRequest request,HttpServletResponse response) throws IOException, DocumentException{
		NotifyRet ret=new NotifyRet();
		
		response.setHeader("Content-type", "text/plain;charset=UTF-8");
		
		InputStream inStream = request.getInputStream();
        int _buffer_size = 1024;
        if (inStream != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] tempBytes = new byte[_buffer_size];
            int count = -1;
            while ((count = inStream.read(tempBytes, 0, _buffer_size)) != -1) {
                outStream.write(tempBytes, 0, count);
            }
            tempBytes = null;
            outStream.flush();
            //将流转换成字符串
            String result = new String(outStream.toByteArray(), "UTF-8");
            //将字符串解析成XML
            Document doc = DocumentHelper.parseText(result);
            //将XML格式转化成MAP格式数据
            Map<String, Object> resultMap = XmlUtils.Dom2Map(doc);
            
            //验证金额是否相等
            if(UserService.modifytotlfee(resultMap)!=1){
            	ret.setReturn_code("FAIL");
            	ret.setReturn_msg("金额不相符");
            	response.getWriter().write(NotifyRetToXml(ret));
            	return;
            }
            
            //验证 sign
            if(!PayUtil.modifyNotifySign(resultMap,key)){
            	ret.setReturn_code("FAIL");
            	ret.setReturn_msg("签名验证失败");
            	response.getWriter().write(NotifyRetToXml(ret));
            	return;
            }
            //同步 处理订单状态
            synchronized (lock) {
            	if(UserService.orderisResolved(resultMap)){
            		ret.setReturn_code("SUCCESS");
            	}else{
            		if(UserService.Resolveorder(resultMap)==1){
            			ret.setReturn_code("SUCCESS");
            		}else{
            			ret.setReturn_code("FAIL");
                    	ret.setReturn_msg("系统数据更新失败");
            		}
            	}
            	
			}
        }else{
        	ret.setReturn_code("FAIL");
        	ret.setReturn_msg("获取数据为空");
        }
    	response.getWriter().write(NotifyRetToXml(ret));
	}
	
	/**
	 * 查询订单状态  (用于支付完成后立马查询使用)
	 * 该接口提供所有微信支付订单的查询，商户可以通过查询订单接口主动查询订单状态，完成下一步的业务逻辑。
	 *	 需要调用查询接口的情况：
	 *	◆ 当商户后台、网络、服务器等出现异常，商户系统最终未接收到支付通知；
	 *	◆ 调用支付接口后，返回系统错误或未知交易状态情况；
	 *	◆ 调用被扫支付API，返回USERPAYING的状态；
	 *	◆  调用关单或撤销接口API之前，需确认支付状态；
	 * @param prepay_id 统一下单返回的prepay_id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "pay/orderquery", method = RequestMethod.GET)
	public Map<String, Object> orderquery(String prepay_id,
			HttpServletRequest request,HttpServletResponse response){
		
        Map<String,Object> retMap=new HashMap<String,Object>();
		
		Map<String, String> result=new HashMap<String,String>();
		
		Map<String,Object> pa=new HashMap<String,Object>();
		try {
	   
	    //根据prepay_id 查询 异步回调函数返回的 微信端 transaction_id 订单号  因为是异步调用不一定会有transaction_id
	    // 如果三次都没有获取到  则使用自己这边的订单 out_trade_no
		
		String transaction_id="";
		for(int i=0;i<3;i++){
			transaction_id=UserService.gettransaction_idByprepay_id(prepay_id);
			//等待一秒  等待异步调用update  transaction_id
			Thread.currentThread().sleep(1000);
		}
		//
		String out_trade_no="";
		if(transaction_id==null||"".equals(transaction_id)){
			out_trade_no=UserService.getout_trade_noByprepay_id(prepay_id);
		}
		
		//查询订单
		pa.put("appid", appid);   //小程序ID
		pa.put("mch_id", mch_id); //商户号
		pa.put("nonce_str", UUIDHexGenerator.generate()); //随机字符串
		pa.put("transaction_id", transaction_id);  //微信订单号
		pa.put("out_trade_no", out_trade_no);//商户订单号
		
		//生成签名
		String sign=PayUtil.getSign(pa,key);
		pa.put("sign", sign);
		
		StringBuffer requestXml=new StringBuffer("<xml>");
		for(String k:pa.keySet())
			requestXml.append("<"+k+">"+pa.get(k)+"</"+k+">");
		requestXml.append("</xml>");
		
		result =PayUtil.httpRequest(orderquery, "POST", requestXml.toString());
	        // 将解析结果存储在HashMap中
	        
		
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		retMap.put("errcode", "0");
		retMap.put("errmsg", "OK");
		retMap.put("data", result);
		return retMap;
		
	}
	
	/**
	 * 关闭订单
	 * 以下情况需要调用关单接口：商户订单支付失败需要生成新单号重新发起支付，要对原订单号调用关单，避免重复支付；
	 * 系统下单后，用户支付超时，系统退出不再受理，避免用户继续，请调用关单接口。
                    注意：订单生成后不能马上调用关单接口，最短调用时间间隔为5分钟。
	 * @param prepay_id 统一下单返回的prepay_id
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "pay/closeorder", method = RequestMethod.GET)
	public Map<String, Object> closeorder(String prepay_id,
			HttpServletRequest request,HttpServletResponse response){
		
        Map<String,Object> retMap=new HashMap<String,Object>();
		
		Map<String, String> result=new HashMap<String,String>();
		
		Map<String,Object> pa=new HashMap<String,Object>();
		try {
	   
	    
		//商户订单号
		String out_trade_no=UserService.getout_trade_noByprepay_id(prepay_id);
		
		
		//查询订单
		pa.put("appid", appid);   //小程序ID
		pa.put("mch_id", mch_id); //商户号
		pa.put("nonce_str", UUIDHexGenerator.generate()); //随机字符串
		pa.put("out_trade_no", out_trade_no);//商户订单号
		
		//生成签名
		String sign=PayUtil.getSign(pa,key);
		pa.put("sign", sign);
		
		StringBuffer requestXml=new StringBuffer("<xml>");
		for(String k:pa.keySet())
			requestXml.append("<"+k+">"+pa.get(k)+"</"+k+">");
		requestXml.append("</xml>");
		
		result =PayUtil.httpRequest(closeorder, "POST", requestXml.toString());
	        
		
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		//更新订单表状态为0 取消订单 
		if("SUCCESS".equals(result.get("return_code"))&&"SUCCESS".equals(result.get("result_code")))
			UserService.updateOrderState(prepay_id);
		
		retMap.put("errcode", "0");
		retMap.put("errmsg", "OK");
		retMap.put("data", result);
		return retMap;
		
	}
	
	/**
	 * 买家申请退款
	 * @param out_trade_no 商家订单号
	 * @param reason  退款原因
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping(value = "pay/askForRefund", method = RequestMethod.POST)
	public Map<String, Object> askForRefund(String out_trade_no,String reason,
			HttpServletRequest request,HttpServletResponse response) throws Exception{
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.askForRefund(out_trade_no,reason);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		return retMap;
	}
	
	/**
	 * 退款
	 * @param out_refund_no 商户退款单号
	 * @param request
	 * @param response
	 * @return
	 * @throws KeyStoreException 
	 * @throws FileNotFoundException 
	 */
	@ResponseBody
	@RequestMapping(value = "pay/refund", method = RequestMethod.GET)
	public Map<String, Object> refund(String out_refund_no,
			HttpServletRequest request,HttpServletResponse response) throws Exception{
		
		 Map<String,Object> retMap=new HashMap<String,Object>();
		 
		 Map<String, String> result=new HashMap<String,String>();
		 
		 Map<String,Object> pa=new HashMap<String,Object>();
		 
		 //证书部分
		 KeyStore keyStore  = KeyStore.getInstance("PKCS12");
	     FileInputStream instream = new FileInputStream(new File(p12_position));
	     try {
	            keyStore.load(instream, mch_id.toCharArray());
	        } finally {
	            instream.close();
	        }

	        // Trust own CA and all self-signed certs
	      SSLContext sslcontext = SSLContexts.custom()
	                .loadKeyMaterial(keyStore, mch_id.toCharArray())
	                .build();
	        // Allow TLSv1 protocol only
	      SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
	                sslcontext,
	                new String[] { "TLSv1" },
	                null,
	                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
	      CloseableHttpClient httpclient = HttpClients.custom()
	                .setSSLSocketFactory(sslsf)
	                .build();
		 
		try {
			pa.put("appid", appid);   //小程序ID
			pa.put("mch_id", mch_id); //商户号
			pa.put("nonce_str", UUIDHexGenerator.generate()); //随机字符串
			pa.put("out_refund_no", out_refund_no); //商户退款单号
			
			//查询该订单总金额 和退款金额  目前 是全额退款   后面有时间 再写非全额退款
			Map<String,String> refund_info=UserService.getTotalFeeBytransaction_id(pa);
			
			pa.put("transaction_id", refund_info.get("transaction_id"));//微信订单号
			pa.put("total_fee", refund_info.get("total_fee"));   //订单金额
			pa.put("refund_fee", refund_info.get("refund_fee"));  //退款金额
			pa.put("op_user_id", mch_id);  //操作员帐号, 默认为商户号
			
			//生成签名
			String sign=PayUtil.getSign(pa,key);
			pa.put("sign", sign);
			
			StringBuffer requestXml=new StringBuffer("<xml>");
			for(String k:pa.keySet())
				requestXml.append("<"+k+">"+pa.get(k)+"</"+k+">");
			requestXml.append("</xml>");
			
			System.out.println(requestXml.toString());
			
			HttpPost httppost=new HttpPost(refund);
			
			StringEntity en=new StringEntity(requestXml.toString());
			httppost.setEntity(en);

            CloseableHttpResponse resp = httpclient.execute(httppost);
			
			Map<String,String> map=getMapFromCloseableHttpResponse(resp);
			
			//退款申请成功
			if("SUCCESS".equals(map.get("return_code"))&&"SUCCESS".equals(map.get("result_code"))){
				 //将退款单状态修改为  提交退款成功状态
				 pa.put("refund_id", map.get("refund_id"));
				 UserService.updateFreundto0(pa);
			}else{
				retMap.put("errcode", "1");
				retMap.put("errmsg", "提交退款失败 return_msg="+map.get("return_msg")+" err_code="+map.get("err_code")+" err_code_des="+map.get("err_code_des"));
				
				pa.put("errmsg", retMap.get("errmsg"));
				UserService.updaterefundErrmsg(pa);
				
				return retMap;
			}
			
			 
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试! ");
			return retMap;
		}
		
		retMap.put("errcode", "0");
		retMap.put("errmsg", "OK");
		retMap.put("data", result);
		return retMap;
	}
	
	private Map<String, String> getMapFromCloseableHttpResponse(
			CloseableHttpResponse response) throws IOException, DocumentException {
		 try {
             HttpEntity entity = response.getEntity();

             System.out.println("----------------------------------------");
             System.out.println(response.getStatusLine());
             if (entity != null) {
            	 
                 String tt=new String(input2byte(entity.getContent()),"utf-8");
                 
                 InputStream in=new ByteArrayInputStream(tt.getBytes());  
                 Map<String, String> map = new HashMap<String, String>();
                 // 读取输入流
                 SAXReader reader = new SAXReader();
                 Document document = reader.read(in);
                 // 得到xml根元素
                 Element root = document.getRootElement();
                 // 得到根元素的所有子节点
                 @SuppressWarnings("unchecked")
                 List<Element> elementList = root.elements();
                 for (Element element : elementList) {
                	 System.out.println(element.getName()+":"+element.getText());
                     map.put(element.getName(), element.getText());
                 }
                 
                 return map;
                
             }
             EntityUtils.consume(entity);
         } finally {
             response.close();
         }
		return null;
	}

	/**
	 * 查询未支付订单
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "order/unpaid", method = RequestMethod.GET)
	public Map<String, Object> unpaidOrder(String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.getOrderList(userId,"-1");
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		return retMap;
	}
	
	/**
	 * 支付成功 尚未发货
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "order/paidNotExpress", method = RequestMethod.GET)
	public Map<String, Object> paidNotExpress(String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.getOrderList(userId,"1");
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		return retMap;
	}
	
	/**
	 * 已经寄件  买家尚未收货
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "order/Expressing", method = RequestMethod.GET)
	public Map<String, Object> ExpressedNotTake(String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.getOrderList(userId,"2");
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		return retMap;
	}
	
	/**
	 * 订单完成
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "order/completion", method = RequestMethod.GET)
	public Map<String, Object> completion(String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.getOrderList(userId,"3");
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		return retMap;
	}
	
	/**
	 * 订单取消
	 * @param userId
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "order/cancel", method = RequestMethod.GET)
	public Map<String, Object> ordercancel(String userId,
			HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		try {
			retMap=UserService.getOrderList(userId,"0");
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
			return retMap;
		}
		
		return retMap;
	}
	
	
	
	@ResponseBody
	@RequestMapping(value = "test", method = RequestMethod.GET)
	public void test(String json,
			HttpServletRequest request,HttpServletResponse response) throws IOException{
		JSONObject obj=new JSONObject(json);
		
		JSONArray arr=obj.getJSONArray("goods_detail");
		 
		for(int i=0;i<arr.length();i++){
			System.out.println(arr.getJSONObject(i).get("goodsCode"));
			System.out.println(arr.getJSONObject(i).get("goodsNum"));
		}
		System.out.println(obj);
		System.out.println(arr);
		System.out.println("1111111");
	}
	
	public static String NotifyRetToXml(NotifyRet ret) throws UnsupportedEncodingException{
		StringBuffer bu=new StringBuffer("<xml>");
		if(ret.getReturn_code()!=null)
			 bu.append("<return_code><![CDATA["+ret.getReturn_code()+"]]></return_code>");
		
		if(ret.getReturn_msg()!=null)
			 bu.append("<return_msg><![CDATA["+ret.getReturn_msg()+"]]></return_msg>");
		
		return bu.toString();  
	}
	
	public static final byte[] input2byte(InputStream inStream)  
            throws IOException {  
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();  
        byte[] buff = new byte[100];  
        int rc = 0;  
        while ((rc = inStream.read(buff, 0, 100)) > 0) {  
            swapStream.write(buff, 0, rc);  
        }  
        byte[] in2b = swapStream.toByteArray();  
        return in2b;  
    } 
	public static void main(String[] args) throws Exception {
		
	}
}
