package com.miaogou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

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
			List<Map<String,Object>> li=systembackDao.getGoodsList();
			
			//解析出图片列表
			
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					String imglist=(String) li.get(i).get("imglist");
					String[] imgListarr=imglist.split(",");
					
					   JSONArray arr=new JSONArray();
					   for(int j=0;j<imgListarr.length;j++){
						   String code=imgListarr[j].split("#")[0];
						   String url=imgListarr[j].split("#")[1];
						   String isbigimg=imgListarr[j].split("#")[2];
						   
						   if("1".equals(isbigimg)){
							   li.get(i).put("bigImg", url);
						    }else{
						    	JSONObject obj=new JSONObject();
						    	obj.put("pid", code);
						    	obj.put("src", url);
						    	arr.add(obj);
						    }
						   
						   
						  }
					      li.get(i).put("imglist", arr);
					   }
				}
			
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("data", li);
			return retMap;
		}

		


		
		
}
