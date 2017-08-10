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
			
			retMap=systembackService.getGoodsList(key,pageSize,currentPage);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	@ResponseBody
	@RequestMapping(value = "/addGoods", method = RequestMethod.POST)
	public Map<String, Object> addGoods(String goods_name,String price,String goods_class,
			                            String brand,String firstBrand,String secondBrand,
			                            String introduceUrl,String introducePrice,String introduce,
			                            @RequestParam("iconImgfile") CommonsMultipartFile iconImgfile,
			                            @RequestParam("bigImgfile") CommonsMultipartFile bigImgfile,
			                            @RequestParam("imgListfile") CommonsMultipartFile[] imgListfile,
			                          HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			retMap=systembackService.addGoods(goods_name,price,goods_class,brand,firstBrand,secondBrand,
					           introduceUrl,introducePrice,introduce,iconImgfile,bigImgfile,imgListfile);
			
		} catch (Exception e) {
			e.printStackTrace();
			retMap.put("errcode", "-2");
			retMap.put("errmsg", "系统异常请稍后重试!");
		}
		return retMap;
	}
	
	public static void main(String[] args) throws Exception {
		
	}
}
