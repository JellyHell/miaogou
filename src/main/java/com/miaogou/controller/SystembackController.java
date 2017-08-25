package com.miaogou.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.github.pagehelper.PageInfo;
import com.miaogou.bean.SysUser;
import com.miaogou.service.ISystemBackService;
import com.miaogou.service.IUserService;
import com.miaogou.util.PayUtil;
import com.miaogou.util.UUIDHexGenerator;

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
	 * 查询退款
	 */
	private static String refundQuery="https://api.mch.weixin.qq.com/pay/refundquery";
	
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
	 * 获取退款列表
	 * @param pageSize
	 * @param currentPage
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getrefundList", method = RequestMethod.GET)
	public Map<String, Object> getrefundList(int pageSize,int currentPage,HttpServletRequest request,HttpServletResponse response){
		
		Map<String,Object> retMap=new HashMap<String,Object>();
		
		Map<String, String> result=new HashMap<String,String>();
		
		try {
			retMap=systembackService.getrefundList(pageSize,currentPage);
			
			//查询退款状况
			List<Map<String,Object>> li=((PageInfo<Map<String,Object>>)retMap.get("pageInfo")).getList();
			
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					Map<String,Object> item=li.get(i);
					if(!"1".equals(item.get("state"))){  //退款申请成功的才 查询退款状况
						Map<String,Object> pa=new HashMap<String,Object>();
						pa.put("appid", appid);
						pa.put("mch_id", mch_id);
						pa.put("nonce_str", UUIDHexGenerator.generate());
						pa.put("transaction_id", item.get("transaction_id"));
						pa.put("sign", PayUtil.getSign(pa,key));
                        
						StringBuffer requestXml=new StringBuffer("<xml>");
						for(String k:pa.keySet())
							requestXml.append("<"+k+">"+pa.get(k)+"</"+k+">");
						requestXml.append("</xml>");
						
						// 将解析结果存储在HashMap中
						result =PayUtil.httpRequest(refundQuery, "POST", requestXml.toString());
						
						//打印返回内容
						System.out.println("===================================");
					    for(String key:result.keySet()){
					    	System.out.println(key+" : "+result.get(key));
					    }
					    System.out.println("===================================");
					    
					    //验证退款是否成功  验证每个   退款笔数 状态 都成功  才是退款成功
					    if("SUCCESS".equals(result.get("return_code"))&&"SUCCESS".equals(result.get("result_code"))){
					    	int refund_count=Integer.parseInt(result.get("refund_count"));
					    	boolean re=true;
					    	for(int j=0;j<refund_count;j++){
					    		if(!"SUCCESS".equals(result.get("refund_status_"+j))){
					    			re=false;
					    		}
					    	}
					    	
					    	//更新数据库
					    	if(re){
					    		UserService.updateFreundto1(item);
					    	}
					    }
					}
				}
			}
			
			//重新查询
			retMap=systembackService.getrefundList(pageSize,currentPage);
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
	public Map<String, Object> addGoods(String goods_name,String goods_class,String sale,String price,
			                            String[] spec_sku,String [] spec_name,String []spec_price,
			                            String brand,String firstBrand,String secondBrand,
			                            String introduceUrl,String introducePrice,String introduce,
			                            @RequestParam("iconImgfile") CommonsMultipartFile iconImgfile,
			                            @RequestParam("specImgFile") CommonsMultipartFile[] specImgfile,
			                            @RequestParam("imgListfile") CommonsMultipartFile[] imgListfile,
			                          HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			retMap=systembackService.addGoods(goods_name,goods_class,sale,price,spec_sku,spec_name,spec_price,specImgfile,
					                brand,firstBrand,secondBrand,
					           introduceUrl,introducePrice,introduce,iconImgfile,imgListfile);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/editGoods", method = RequestMethod.POST)
	public Map<String, Object> editGoods(String goods_code,String goods_name,String goods_class,String sale,String price,
			                            String[] spec_sku,String [] spec_name,String []spec_price,String []uploadedImg,
			                            String brand,String firstBrand,String secondBrand,
			                            String introduceUrl,String introducePrice,String introduce,
			                            String iconImgUploaded,String []uploadedBigImg,
			                            @RequestParam(required=false,value="iconImgfile") CommonsMultipartFile iconImgfile,
			                            @RequestParam(required=false,value="specImgFile") CommonsMultipartFile[] specImgfile,
			                            @RequestParam(required=false,value="imgListfile") CommonsMultipartFile[] imgListfile,
			                          HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			System.out.println(111);
			retMap=systembackService.editGoods(goods_code,goods_name,goods_class,sale,price,spec_sku,spec_name,
					                spec_price,uploadedImg,
					                brand,firstBrand,secondBrand,
					           introduceUrl,introducePrice,introduce,iconImgUploaded,
					           uploadedBigImg,iconImgfile,specImgfile,imgListfile);
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
	 * 登出
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/loginout", method = RequestMethod.GET)
	public void loginout(HttpServletRequest request,HttpServletResponse response){
		request.getSession().removeAttribute("loginInfo");
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
	
	/**
	 * 获取商品信息
	 * @param goods_code
	 * @param request
	 * @param response
	 */
	@ResponseBody
	@RequestMapping(value = "/getGoodsData", method = RequestMethod.GET)
	public Map<String, Object> getGoodsData(String goods_code,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.getGoodsData(goods_code);
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	/**
	 * 获取sku序列
	 * @param goods_class
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/getSkuSeq", method = RequestMethod.GET)
	public Map<String, Object> getSkuSeq(String goods_class,HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			retMap=systembackService.getSkuSeq(goods_class);
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
