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

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miaogou.dao.ISystemBackDao;
import com.miaogou.dao.IUserDao;
import com.miaogou.service.ISystemBackService;
import com.miaogou.util.FastdfsUtils;
import com.miaogou.util.MD5Utils;

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
		public Map<String, Object> getGoodsList(String key, int pageSize, int currentPage) {
		    
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("key", key);
			
			PageHelper.startPage(currentPage,pageSize);
			
			List<Map<String,Object>> li=systembackDao.getGoodsList(pa);
			
			PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(li);
	        long total = pageInfo.getTotal(); //获取总条数
	        int pages=pageInfo.getPages(); //获取总页数
			
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
			retMap.put("pageInfo", pageInfo);
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> addGoods(String sku,String goods_name, String price,
				String goods_class,String sale, String brand, String firstBrand,
				String secondBrand, String introduceUrl, String introducePrice,
				String introduce, CommonsMultipartFile iconImgfile,
				CommonsMultipartFile bigImgfile,
				CommonsMultipartFile[] imgListfile) throws Exception {
			
			Map<String,Object> retMap=new HashMap<String,Object>();
			Map<String,String> pa=new HashMap<String,String>();
			String code="s_goods"+userDao.nextval("s_goods");
			pa.put("goods_code", code);
			pa.put("sku", sku);
			pa.put("goods_name", goods_name);
			pa.put("goods_class", goods_class);
			pa.put("sale", sale);
			pa.put("price", price);
			pa.put("brand", brand);
			pa.put("firstBrand", firstBrand);
			pa.put("secondBrand", secondBrand);
			pa.put("introduceUrl", introduceUrl);
			pa.put("introducePrice", "".equals(introducePrice)?"0":introducePrice);
			pa.put("introduce", introduce);
			
			//判断sku是否重复
			if(systembackDao.SkuExits(pa)>0) {
				retMap.put("errcode", "-3");
				retMap.put("errmsg", "SKU不能重复");
				return retMap;
			}
			
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

		@Override
		@Transactional
		public boolean userExists(String username) {
			return systembackDao.userExists(username)>0;
		}


		@Override
		public boolean passwordRight(String username, String password) {
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("username", username);
			pa.put("password", MD5Utils.getMD5(password));
			return systembackDao.passwordRight(pa)>0;
		}

		@Override
		@Transactional
		public Map<String, Object> GoodsDel(String goods_code) throws Exception {
            
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("goods_code", goods_code);
			
			
			//先查询出图片列表  删除图片使用
			List<Map<String,String>> li=systembackDao.getAttachmentBytabCode(pa);
			//查询出  商品信息  删除图片使用
			Map<String,String> goodsItem=systembackDao.getGoodsItem(pa);
			
			
			//删除数据表 1 mg_attachemnt
			if(li!=null&&li.size()>0)
			    if(systembackDao.attachemntDelBytabCode(pa)<=0) 
			            throw new Exception();
			
			//删除数据表 2 mg_goodsdetails
			if(systembackDao.delGoodsDetails(pa)!=1) throw new Exception();
			
			//删除数据表 3 mg_goods
			if(systembackDao.delGoods(pa)!=1) throw new Exception();
			
			//删除服务器上的图片  有异常也不抛出    不回滚
			try {
				if(li!=null&&li.size()>0){
					for(int i=0;i<li.size();i++)
						 FastdfsUtils.deleteFile(li.get(i).get("groupName"), li.get(i).get("remoteName"));
				}
				
				FastdfsUtils.deleteFile(goodsItem.get("group"), goodsItem.get("remote"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> GoodsSaleStatusChange(String goods_code,
				String sale) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("goods_code", goods_code);
			pa.put("sale", sale);
			
			if(systembackDao.GoodsSaleStatusChange(pa)!=1) throw new Exception();
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getOrderList(int pageSize, int currentPage)
				throws Exception {
			
			Map<String,Object> retMap=new HashMap<String,Object>();
			
            PageHelper.startPage(currentPage,pageSize);
			
			List<Map<String,Object>> li=systembackDao.getOrderList();
			
			PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(li);
			
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					String imglist=(String) li.get(i).get("orderGoodsList");
					if(imglist==null||"".equals(imglist)) continue;
					String[] imgListarr=imglist.split(",");
					
					   JSONArray arr=new JSONArray();
					   for(int j=0;j<imgListarr.length;j++){
						   String goods_code=imgListarr[j].split("##")[0];
						   String sku=imgListarr[j].split("##")[1];
						   String goods_name=imgListarr[j].split("##")[2];
						   String goods_num=imgListarr[j].split("##")[3];
						   
						   
						    	JSONObject obj=new JSONObject();
						    	obj.put("goods_code", goods_code);
						    	obj.put("sku", sku);
						    	obj.put("goods_name", goods_name);
						    	obj.put("goods_num", goods_num);
						    	arr.add(obj);
						  }
					      li.get(i).put("orderGoodsList", arr);
					   }
				}
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("pageInfo", pageInfo);
			return retMap;
		}


		
		
}
