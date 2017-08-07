package com.miaogou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.miaogou.dao.ISystemBackDao;
import com.miaogou.service.ISystemBackService;

/**
 * 
 * @author weicc
 *
 */

@Service
public class SystemBackImpl implements ISystemBackService{
   
		@Resource
	    ISystemBackDao systembackDao;

		@Override
		@Transactional
		public Map<String, Object> getGoodsList() {
		    
			Map<String,Object> retMap=new HashMap<String,Object>();
			List<Map<String,String>> li=systembackDao.getGoodsList();
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("data", li);
			return retMap;
		}

		


		
		
}
