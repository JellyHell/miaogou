package com.miaogou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.miaogou.dao.ISystemBackDao;
import com.miaogou.dao.IUserDao;
import com.miaogou.service.ISystemBackService;
import com.miaogou.util.FastdfsUtils;

/**
 * 
 * @author weicc
 *
 */

@Service
public class SystemBackImpl implements ISystemBackService{
   
		@Resource
	    ISystemBackDao systembackDao;
		
		@Resource
	    IUserDao userDao;

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

		@Override
		@Transactional
		public Map<String, Object> addGoods(String goods_name, String price,
				String goods_class, String brand, String firstBrand,
				String secondBrand, String introduceUrl, String introducePrice,
				String introduce, CommonsMultipartFile iconImgfile,
				CommonsMultipartFile bigImgfile,
				CommonsMultipartFile[] imgListfile) throws Exception {
			
			Map<String,Object> retMap=new HashMap<String,Object>();
			Map<String,String> pa=new HashMap<String,String>();
			String code="s_goods"+userDao.nextval("s_goods");
			pa.put("goods_code", code);
			pa.put("goods_name", goods_name);
			pa.put("goods_class", goods_class);
			pa.put("price", price);
			pa.put("brand", brand);
			pa.put("firstBrand", firstBrand);
			pa.put("secondBrand", secondBrand);
			pa.put("introduceUrl", introduceUrl);
			pa.put("introducePrice", introducePrice);
			pa.put("introduce", introduce);
			
			//上传图标  并插入mg_goods表
			if(iconImgfile!=null){
					String filename=iconImgfile.getOriginalFilename();
					String [] arr=FastdfsUtils.uploadFile(iconImgfile.getBytes(), filename.substring(filename.indexOf(".")+1), null);
					
					if(arr!=null&&arr.length==3){
						pa.put("iconImg", arr[2]);
						pa.put("group", arr[0]);
						pa.put("remote", arr[1]);
						if(systembackDao.inserIntoGoods(pa)!=1) throw new Exception("插入数据库表失败!");
					}else{
						throw new Exception("上传icon图片失败");
					}
				
			}
			//插入细表
			if(systembackDao.inserIntoGoodsDetails(pa)!=1) throw new Exception("插入数据库表失败!");
			
			
			//上传大图 并插入附件表
			if(bigImgfile!=null){
				String filename=bigImgfile.getOriginalFilename();
				String [] arr=FastdfsUtils.uploadFile(bigImgfile.getBytes(), filename.substring(filename.indexOf(".")+1), null);
				
				if(arr!=null&&arr.length==3){
					
					String attaCode="s_atta"+userDao.nextval("s_atta");
					pa.put("code", attaCode);
					pa.put("tabCode", code);
					pa.put("url", arr[2]);
					pa.put("groupName", arr[0]);
					pa.put("remoteName", arr[1]);
					
					if(systembackDao.inserIntoAttachmentbigImg(pa)!=1) throw new Exception("插入数据库表失败!");
					
				}else{
					throw new Exception("上传icon图片失败");
				}
		      }
			
			//上传图片列表
			if(imgListfile!=null&&imgListfile.length>0){
				for(int i=0;i<imgListfile.length;i++){
					String filename=imgListfile[i].getOriginalFilename();
					String [] arr=FastdfsUtils.uploadFile(imgListfile[i].getBytes(), filename.substring(filename.indexOf(".")+1), null);
					
					if(arr!=null&&arr.length==3){
						
						String attaCode="s_atta"+userDao.nextval("s_atta");
						pa.put("code", attaCode);
						pa.put("tabCode", code);
						pa.put("url", arr[2]);
						pa.put("groupName", arr[0]);
						pa.put("remoteName", arr[1]);
						
						if(systembackDao.inserIntoAttachment(pa)!=1) throw new Exception("插入数据库表失败!");
					}
				}
			}
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			return retMap;
		}

		


		
		
}
