package com.miaogou.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miaogou.dao.IUserDao;
import com.miaogou.service.IUserService;
import com.miaogou.util.FastdfsUtils;

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


		@Override
		@Transactional
		public Map<String, Object> register(String openId) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("openId", openId);
		    
		    
		    //首先查看是否已经签到了 今天
		    if(userDao.isRegister(pa)>=1){
		    	retMap.put("errcode", "-1");
				retMap.put("errmsg", "今天已经签到过了 ,大哥！");
			    return retMap;
		    }
		    
		    //签到
		    if(userDao.register(pa)!=1)  throw new Exception("操作失败!");
		    
		    //签到获取积分
		    pa.put("code", "s_score"+userDao.nextval("s_score"));
		    pa.put("score", "1");
		    pa.put("reason", "签到奖励");

		    if(userDao.addScore(pa)!=1) throw new Exception("增加签到积分失败!");
		    
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getRegisterStatus(String openId)
				throws Exception {
			  Map<String,Object> retMap=new HashMap<String,Object>();
				
			  Map<String,Object> pa=new HashMap<String,Object>();
	          pa.put("openId", openId);
			    
			  String status="0";
			  //首先查看是否已经签到了 今天
			  if(userDao.isRegister(pa)>=1){
			   status="1";
			  }
			  retMap.put("errcode", "0");
		      retMap.put("errmsg", "OK");
			  retMap.put("status", status);
			  return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getScoreList(String openId,int pageSize,int pageNum) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("openId", openId);
	        
	        PageHelper.startPage(pageNum,pageSize);
	        List<Map<String,String>> li=userDao.getScoreList(pa);
			
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

        /**
         * 新增心愿单
         */
		@Override
		@Transactional
		public Map<String, Object> uploadWishList(String openId,
				CommonsMultipartFile[] files, String goodsName, String url)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("openId", openId);
	        
	        String wishCode="s_wish"+userDao.nextval("s_wish");
	        //先上传附件  新增至附件表  mg_attachment
	        if(files!=null&&files.length>0){
				for(int i=0;i<files.length;i++){
					String filename=files[i].getOriginalFilename();
					String [] arr=FastdfsUtils.uploadFile(files[i].getBytes(), filename.substring(filename.indexOf(".")+1), null);
					
					if(arr!=null&&arr.length==3){
						pa.put("code", "s_atta"+userDao.nextval("s_atta"));
						pa.put("tabCode", wishCode);
						pa.put("groupName", arr[0]);
						pa.put("remoteName", arr[1]);
						pa.put("url", arr[2]);
						
						if(userDao.insertAttachment(pa)!=1) throw new Exception();
					}
				}
			}else{
				throw new Exception();
			}
	        
	        //新增心愿表  mg_wish
	        pa.put("code", wishCode);
	        pa.put("openId", openId);
	        pa.put("goodsName", goodsName);
	        pa.put("goodsUrl", url);
	        pa.put("state", "00");  //心愿单状态 00 未确认  01 已确认 02 已上架
	         
	        if(userDao.insertWish(pa)!=1) throw new Exception();
	        
	        retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getWishList(String openId, int pageSize,
				int pageNum) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("openId", openId);
	        
	        PageHelper.startPage(pageNum,pageSize);
	        
	        List<Map<String,Object>> li=userDao.getWishList(pa);
			
	        //获取每个心愿单的上传的照片
	        if(li!=null&&li.size()>0){
	        	for(int i=0;i<li.size();i++){
	        		pa.put("tabCode", li.get(i).get("code"));
	        		li.get(i).put("attachmentList", userDao.getAttachmentList(pa));
	        	}
	        }
			PageInfo<Map<String,Object>> pageInfo = new PageInfo<Map<String,Object>>(li);
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
		public Map<String, Object> addToShoppingCart(String openId,
				String goodsCode, String goodsNum) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("code", "s_shopcart"+userDao.nextval("s_shopcart"));
	        pa.put("openId", openId);
	        pa.put("goodsCode", goodsCode);
	        pa.put("goodsNum", goodsNum);
	        pa.put("state", "0");
	        
	        if(userDao.addToShoppingCart(pa)!=1) throw new Exception();
	        
	        retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getShoppingCartList(String openId)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
			Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("openId", openId);
		    
		    
			return null;
		}
		
}
