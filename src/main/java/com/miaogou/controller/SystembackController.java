package com.miaogou.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.miaogou.bean.SysUser;
import com.miaogou.service.ISystemBackService;

/**
 * 
 * @author weicc
 *
 */
@Controller
@RequestMapping(value = "back/SystemBack")
public class SystembackController {
	
	
	@Resource
	ISystemBackService systembackService;
	
	/**
	 * 查询商品列表
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getGoodsList", method = RequestMethod.GET)
	public Map<String, Object> getGoodsList(String key,int pageSize,int currentPage,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			key=java.net.URLDecoder.decode(key, "utf-8");
			retMap=systembackService.getGoodsList(key,pageSize,currentPage);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 查询订单列表
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getOrderList", method = RequestMethod.GET)
	public Map<String, Object> getOrderList(int pageSize,int currentPage,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.getOrderList(pageSize,currentPage);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 添加商品
	 * @param sku
	 * @param goods_name
	 * @param price
	 * @param goods_class
	 * @param sale
	 * @param brand
	 * @param firstBrand
	 * @param secondBrand
	 * @param introduceUrl
	 * @param introducePrice
	 * @param introduce
	 * @param iconImgfile
	 * @param bigImgfile
	 * @param imgListfile
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/addGoods", method = RequestMethod.POST)
	public Map<String, Object> addGoods(String sku,String goods_name,String price,String goods_class,String sale,
			                            String brand,String firstBrand,String secondBrand,
			                            String introduceUrl,String introducePrice,String introduce,
			                            @RequestParam("iconImgfile") CommonsMultipartFile iconImgfile,
			                            @RequestParam("bigImgfile") CommonsMultipartFile bigImgfile,
			                            @RequestParam("imgListfile") CommonsMultipartFile[] imgListfile,
			                          HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			retMap=systembackService.addGoods(sku,goods_name,price,goods_class,sale,brand,firstBrand,secondBrand,
					           introduceUrl,introducePrice,introduce,iconImgfile,bigImgfile,imgListfile);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	/**
	 * login
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/loginin", method = RequestMethod.POST)
	public Map<String, Object> loginin(String username,String password,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			//验证该用户是否存在
			
			if(!systembackService.userExists(username)){
				retMap.put("errcode", "-1");
				retMap.put("errmsg", "用户不存在！");
				return retMap;
			}
			//判断密码是否正确
			if(!systembackService.passwordRight(username,password)){
				retMap.put("errcode", "-3");
				retMap.put("errmsg", "密码不正确！");
				return retMap;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		//存入session
		SysUser user=new SysUser();
		user.setUsername(username);
		request.getSession().putValue("loginInfo", user);
		
		retMap.put("errcode", "0");
		retMap.put("errmsg", "OK");
		return retMap;
	}
	
	/**
	 * 单纯验证登陆
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/logincheck", method = RequestMethod.GET)
	public void logincheck(HttpServletRequest request,HttpServletResponse response){
		
	}
	
	/**
	 * 商品 删除
	 * @param goods_code
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/GoodsDel", method = RequestMethod.GET)
	public Map<String, Object> GoodsDel(String goods_code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.GoodsDel(goods_code);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 上架或下架
	 * @param goods_code
	 * @param sale
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/GoodsSaleStatusChange", method = RequestMethod.GET)
	public Map<String, Object> GoodsSaleStatusChange(String goods_code,String sale,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.GoodsSaleStatusChange(goods_code,sale);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	/**
	 * 获取数据字典
	 * @param dicCode
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getDictionaryData", method = RequestMethod.GET)
	public Map<String, Object> getDictionaryData(String dicCode,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.getDictionaryData(dicCode);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(java.net.URLDecoder.decode("%E6%9B%B9%E5%90%8E%E7%BA%A2", "utf-8"));
	}
}
