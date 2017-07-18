package com.miaogou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miaogou.dao.IUserDao;
import com.miaogou.service.IUserService;

/**
 * 
 * @author weicc
 *
 */

@Service("UserService")
public class UserImpl implements IUserService{

   
		@Resource
	    IUserDao userDao;


		@Override
		@Transactional
		public Map<String, Object> findGoodsByClass(String goodsClass,
				int pageSize, int pageNum) throws Exception{
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("goods_class", goodsClass);
			
			PageHelper.startPage(pageNum,pageSize);
			List<Map<String,String>> li=userDao.findGoodsByClass(pa);
			
			PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(li);
	        long total = pageInfo.getTotal(); //获取总条数
	        int pages=pageInfo.getPages(); //获取总页数
	        
	        retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        retMap.put("data", li);
	        retMap.put("total", total);
	        retMap.put("pages", pages);
			
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getBannerAd() throws Exception{
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			List<Map<String,String>> li=userDao.getBannerAd();
			
	    	retMap.put("errcode", "0");
		    retMap.put("errmsg", "OK");
		    retMap.put("data", li);
			
		    return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> addDeliveryAddress(String openId,
				String name, String phone, String area, String address,
				String postCode) throws Exception{
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("code", "s_addr"+userDao.nextval("s_addr"));
			pa.put("name", name);
			pa.put("phone", phone);
			pa.put("area", area);
			pa.put("address", address);
			pa.put("postCode", postCode);
			
			if(userDao.addDeliveryAddress(pa)!=1) throw new Exception();
			
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getDeliveryAddress(String openId)
				throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("openId", openId);
			
			List<Map<String,String>> li=userDao.getDeliveryAddress(pa);
			retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			retMap.put("data", li);
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> delDeliveryAddress(String openId, String code)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("openId", openId);
		    pa.put("code", code);
		    
		    if(userDao.delDeliveryAddress(pa)!=1) throw new Exception("删除失败");
		    
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> setDefaultDeliveryAddress(String openId,
				String code) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("openId", openId);
		    pa.put("code", code);
		    
		    //首先设置所有的收货地址都不为默认的  isdefault=0
		    
		    if(userDao.setAllNotDefaultAddress(pa)<=0) throw new Exception("操作失败!");
		    if(userDao.setDefaultDeliveryAddress(pa)!=1)  throw new Exception("操作失败!");
		    
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getBestSalesGoods(int pageSize, int pageNum)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			PageHelper.startPage(pageNum,pageSize);
			List<Map<String,String>> li=userDao.getBestSalesGoods();
			
			PageInfo<Map<String,String>> pageInfo = new PageInfo<Map<String,String>>(li);
	        long total = pageInfo.getTotal(); //获取总条数
	        int pages=pageInfo.getPages(); //获取总页数
	        
	        retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        retMap.put("data", li);
	        retMap.put("total", total);
	        retMap.put("pages", pages);
	        return retMap;
		}
		
}
