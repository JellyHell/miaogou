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
import com.miaogou.util.StringUtil;

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
			
			//解析出图片列表
			
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					
					//图片列表
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
					      
					    //规格列表
					    pa.put("goods_code", (String) li.get(i).get("goods_code"));
					    List<Map<String,String>> specList=systembackDao.getGoodsSpecList(pa);
						li.get(i).put("speclist", specList);
							   }
					   }
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("pageInfo", pageInfo);
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> addGoods(String goods_name, String goods_class,
				String sale, String price,String[] spec_sku, String[] spec_name,
				String[] spec_price, CommonsMultipartFile[] specImgfile,
				String brand, String firstBrand, String secondBrand,
				String introduceUrl, String introducePrice, String introduce,
				CommonsMultipartFile iconImgfile, CommonsMultipartFile[] imgListfile) throws Exception {
			
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			/**
			 * 1 先进行简单的判断 sku数组不能重复   数字大小必须一致
			 */
			
			if(!StringUtil.checkRepeat(spec_sku)){
				retMap.put("errcode", "-3");
				retMap.put("errmsg", "SKU不能重复");
				return retMap;
			}
			
			
			Map<String,String> pa=new HashMap<String,String>();
			String code="s_goods"+userDao.nextval("s_goods");
			pa.put("goods_code", code);
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
			
			/**
			 * 2判断数据库中sku是否重复
			 */
			for(String sku:spec_sku){
				pa.put("sku", sku);
				if(systembackDao.SkuExits(pa)>0) {
					retMap.put("errcode", "-3");
					retMap.put("errmsg", "SKU不能重复");
					return retMap;
				}
			}
			/**
			 * 3上传图标  并插入mg_goods表
			 */
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
			/**
			 * 4插入细表
			 */
			if(systembackDao.inserIntoGoodsDetails(pa)!=1) throw new Exception("插入数据库表失败!");
			
			
			/**
			 * 5 插入规格表
			 */
			
			for(int i=0;i<spec_sku.length;i++){
				//上传相应的规格图片
				String filename=specImgfile[i].getOriginalFilename();
				String [] arr=FastdfsUtils.uploadFile(specImgfile[i].getBytes(), filename.substring(filename.indexOf(".")+1), null);
				
				if(arr!=null&&arr.length==3){
					
					
					pa.put("sku", spec_sku[i]);
					pa.put("spec_name", spec_name[i]);
					pa.put("price", spec_price[i]);
					pa.put("url", arr[2]);
					pa.put("group", arr[0]);
					pa.put("remote", arr[1]);
					
					if(systembackDao.inserIntoGoodsSpec(pa)!=1) throw new Exception("插入数据库表失败!");
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
			
			Map<String,Object> pa2=new HashMap<String,Object>();
			pa2.put("goods_code", goods_code);
			
			
			//先查询出图片列表  删除图片使用
			List<Map<String,String>> li=systembackDao.getAttachmentBytabCode(pa);
			//查询出  商品信息  删除图片使用
			Map<String,String> goodsItem=systembackDao.getGoodsItem(pa2);
			
			
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

		@Override
		@Transactional
		public Map<String, Object> getDictionaryData(String dicCode)
				throws Exception {
			    Map<String,Object> retMap=new HashMap<String,Object>();
				
				Map<String,String> pa=new HashMap<String,String>();
				pa.put("dicCode", dicCode);
				
				
				List<Map<String,Object>> li=systembackDao.getDictionaryData(pa);
				
				retMap.put("errcode", "0");
				retMap.put("errmsg", "ok");
				retMap.put("data", li);
				return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getrefundList(int pageSize, int currentPage) {
			
            Map<String,Object> retMap=new HashMap<String,Object>();
			
            PageHelper.startPage(currentPage,pageSize);
			
			List<Map<String,Object>> li=systembackDao.getrefundList();
			
			PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(li);
			
			
			
			retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("pageInfo", pageInfo);
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getGoodsData(String goods_code)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
			Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("goods_code", goods_code);
		    
		    /**
		     * 获取mg_goods表中的信息
		     */
		    Map<String,String> goodsInfo=systembackDao.getGoodsItem(pa);
		    
		    /**
		     * 获取规格列表
		     */
		    List<Map<String,String>> specList=systembackDao.getSpecList(pa);
		    
		    /**
		     * 获取图片列表
		     */
		    List<Map<String,String>> imgList=userDao.getAttachmentList(pa);
		    
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("goodsInfo", goodsInfo);
			retMap.put("specList", specList);
			retMap.put("imgList", imgList);
		    
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getSkuSeq(String goods_class) {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("name", goods_class);
		    
		    //查看是否有该序列
		    String seq=userDao.nextval(goods_class);
		    if("0".equals(seq)){
		    	userDao.insertintoSeq(pa);
		    }
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "ok");
			retMap.put("seq", goods_class.toUpperCase()+userDao.nextval(goods_class));
		    
		    return retMap;
		}


		
		
}
