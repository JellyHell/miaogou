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
        
		
		
		
}
