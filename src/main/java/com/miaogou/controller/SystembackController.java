package com.miaogou.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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
	
	@ResponseBody
	@RequestMapping(value = "/getGoodsList", method = RequestMethod.GET)
	public Map<String, Object> getGoodsList(HttpServletRequest request,HttpServletResponse response){
		Map<String,Object> retMap=new HashMap<String,Object>();
		try {
			
			retMap=systembackService.getGoodsList();
			
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
