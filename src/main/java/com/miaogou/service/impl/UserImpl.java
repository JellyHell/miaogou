package com.miaogou.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.miaogou.dao.IUserDao;
import com.miaogou.service.IUserService;
import com.miaogou.util.FastdfsUtils;
import com.miaogou.util.PayUtil;

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
		public Map<String, Object> insertOrSelectUserId(String openid) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("openId", openid);
			
			String  userId=userDao.selectUserIdByopenId(pa);
			
			if(userId==null||"".equals(userId)){
				userId="s_wxUser"+userDao.nextval("s_wxUser");
				pa.put("userId", userId);
				if(userDao.insertWxUser(pa)!=1) throw new Exception();
			}
			
			retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        retMap.put("userId", userId);
	      
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
		public Map<String, Object> addDeliveryAddress(String userId,
				String name, String phone, String area, String address,
				String postCode) throws Exception{
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("code", "s_addr"+userDao.nextval("s_addr"));
			pa.put("userId", userId);
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
		public Map<String, Object> getDeliveryAddress(String userId)
				throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("userId", userId);
			
			List<Map<String,String>> li=userDao.getDeliveryAddress(pa);
			retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			retMap.put("data", li);
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> delDeliveryAddress(String userId, String code)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("userId", userId);
		    pa.put("code", code);
		    
		    if(userDao.delDeliveryAddress(pa)!=1) throw new Exception("删除失败");
		    
		    retMap.put("errcode", "0");
			retMap.put("errmsg", "OK");
			return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> setDefaultDeliveryAddress(String userId,
				String code) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("userId", userId);
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
		public Map<String, Object> register(String userId) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
		    Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("userId", userId);
		    
		    
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
		public Map<String, Object> getRegisterStatus(String userId)
				throws Exception {
			  Map<String,Object> retMap=new HashMap<String,Object>();
				
			  Map<String,Object> pa=new HashMap<String,Object>();
	          pa.put("userId", userId);
			    
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
		public Map<String, Object> getScoreList(String userId,int pageSize,int pageNum) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("userId", userId);
	        
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
		public Map<String, Object> uploadWishList(String userId,
				CommonsMultipartFile[] files, String goodsName, String url)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("userId", userId);
	        
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
	        pa.put("userId", userId);
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
		public Map<String, Object> getWishList(String userId, int pageSize,
				int pageNum) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
	        pa.put("userId", userId);
	        
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
		public Map<String, Object> addToShoppingCart(String userId,
				String goodsCode, String goodsNum) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			
	        pa.put("userId", userId);
	        pa.put("goodsCode", goodsCode);
	        pa.put("goodsNum", goodsNum);
	        pa.put("state", "0");
	        
	        //查看该用户下这个商品是否在 购物车中 
	        
	        if(userDao.isGoodsInShoppingCart(pa)>0){ 
	        	if(userDao.updateShoppingCartGoodsNum(pa)!=1) throw new Exception();
	        }else{
	        	pa.put("code", "s_shopcart"+userDao.nextval("s_shopcart"));
	        	if(userDao.addToShoppingCart(pa)!=1) throw new Exception();
	        }
	        
	        retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        return retMap;
		}


		@Override
		@Transactional
		public Map<String, Object> getShoppingCartList(String userId)
				throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
				
			Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("userId", userId);
		    
		    List<Map<String,String>> li=userDao.getShoppingCartList(pa);
		    
		    retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        retMap.put("data", li);
	        return retMap;
		    
		}


		@Override
		@Transactional
		public Map<String, Object> addWxUser(String userId, String nickName,
				String avatarUrl, String gender, String province, String city,
				String country) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
		    pa.put("userId", userId);
		    pa.put("nickName", nickName);
		    pa.put("avatarUrl", avatarUrl);
		    pa.put("gender", gender);
		    pa.put("province", province);
		    pa.put("city", city);
		    pa.put("country", country);
		    
		    //查看是否存在
		    
		    //存在   update
		    if(userDao.isWxUserExits(pa)==1){
		    	if(userDao.updateWxUser(pa)!=1) throw new Exception();
		    }else{ //不存在  insert
		    	throw new Exception("没有找到该用户");
		    }
		    retMap.put("errcode", "0");
	        retMap.put("errmsg", "OK");
	        return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getDetails(String goods_code)
				throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("goods_code", goods_code);
			
			//details info 
			Map<String,String> info=userDao.getGoodsInfo(pa);
			//bigimg
			String bigImg=userDao.getgetBigImg(pa);
			//img list
			List<String> li=userDao.getImgList(pa);
			
			retMap.put("detailInfo", info);
			retMap.put("bigImg", bigImg);
			retMap.put("imgList", li);
			retMap.put("errcode", "0");
		    retMap.put("errmsg", "OK");
			return retMap;
		}

		@Override
		@Transactional
		public void createOrder(Map<String, Object> pa) throws Exception {
			
			 //插入订单表
			if(userDao.createOrder(pa)!=1) throw new Exception();
			
			//插入联系地址表
			if(userDao.createOrderDeliveryAddress(pa)!=1) throw new Exception();
			
			JSONObject obj=new JSONObject((String)pa.get("detail"));
			
			JSONArray arr=obj.getJSONArray("goods_detail");
			
			if(arr!=null&&arr.length()>0){
				for(int i=0;i<arr.length();i++){
					pa.put("goodsCode", arr.getJSONObject(i).getString("goodsCode"));
					pa.put("goodsNum", arr.getJSONObject(i).getInt("goodsNum"));
					if(userDao.createOrderGoods(pa)!=1) throw new Exception();
				}
			}
			
		}

		@Override
		@Transactional
		public int modifytotlfee(Map<String, Object> pa) {
			
			return userDao.modifytotlfee(pa);
		}

		@Override
		@Transactional
		public boolean orderisResolved(Map<String, Object> pa) {
			
			return userDao.orderisResolved(pa)==1;
		}

		@Override
		@Transactional
		public int Resolveorder(Map<String, Object> pa) {
			
			return  userDao.Resolveorder(pa);
		}

		@Override
		@Transactional
		public String gettransaction_idByprepay_id(String prepay_id) {
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("prepay_id", prepay_id);
			return userDao.gettransaction_idByprepay_id(pa);
		}

		@Override
		@Transactional
		public String getout_trade_noByprepay_id(String prepay_id) {
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("prepay_id", prepay_id);
			return userDao.getout_trade_noByprepay_id(pa);
		}

		@Override
		@Transactional
		public int updateOrderState(String prepay_id) {
			Map<String,String> pa=new HashMap<String,String>();
			pa.put("prepay_id", prepay_id);
			pa.put("state", "0");
			return userDao.updateOrderStateByPrePar_Id(pa);
		}

		@Override
		@Transactional
		public Map<String, Object> reduce1FromShoppingCart(String userId,
				String goodsCode) throws Exception {
			    Map<String,Object> retMap=new HashMap<String,Object>();
				
				Map<String,Object> pa=new HashMap<String,Object>();
				pa.put("userId", userId);
				pa.put("goodsCode", goodsCode);
				
				//查看该商品在购物车中的数量
				int goodsNum=userDao.getGoodsNumInshoppingCart(pa);
				
				if(goodsNum==1){   //数量为1  删除
					if(userDao.deletefromShopingByuserIdAndGoodsCode(pa)!=1) throw new Exception();
				}else{    //数量大于1 减少1 
					if(userDao.reduce1FromShoppingCart(pa)!=1) throw new Exception();
				}
					
				retMap.put("errcode", "0");
			    retMap.put("errmsg", "OK");
				return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> delFromShoppingCart(String code) throws Exception {
			Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("code", code);
			
			
			if(userDao.delFromShoppingCart(pa)!=1) throw new Exception();
			
			retMap.put("errcode", "0");
		    retMap.put("errmsg", "OK");
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, Object> getOrderList(String userId,String state) throws Exception {
            Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("userId", userId);
			pa.put("state", state);
			
			List<Map<String,Object>> li=userDao.getOrderList(pa);
			
			//删除过期的订单
			if(li!=null&&li.size()>0&&"-1".equals(state)){
				for(int i=li.size()-1;i>=0;i--){
					Long ttl=(Long) li.get(i).get("ttl");
					if(ttl<0) li.remove(i);
				}
			}
			
			if(li!=null&&li.size()>0){
				for(int i=0;i<li.size();i++){
					String detail=(String) li.get(i).get("detail");
					if(detail==null) continue;
					String[] items=detail.split(",");
					List<Map<String,String>> goodslist=new ArrayList<Map<String,String>>();
					if(items!=null&&items.length>0){
						for(int j=0;j<items.length;j++){
							Map<String,String> map=new HashMap<String,String>();
							map.put("goodsCode",items[j].split("#")[0]);
							map.put("goodsNum",items[j].split("#")[1]);
							map.put("iconImg",items[j].split("#")[2]);
							map.put("goodsName",items[j].split("#")[3]);
							goodslist.add(map);
						}
					}
					li.get(i).put("goodsList", goodslist);
					li.get(i).remove("detail");
				}
			}
			retMap.put("errcode", "0");
		    retMap.put("errmsg", "OK");
		    retMap.put("data", li);
			return retMap;
		}

		@Override
		@Transactional
		public Map<String, String> getTotalFeeBytransaction_id(Map<String, Object> pa)
				throws Exception {
			
			return userDao.getTotalFeeBytransaction_id(pa);
		}

		@Override
		@Transactional
		public int updateFreundto0(Map<String, Object> pa) {
			
			return userDao.updateFreundto0(pa);
		}

		@Override
		@Transactional
		public Map<String, Object> askForRefund(String out_trade_no,String reason)
				throws Exception {
			    Map<String,Object> retMap=new HashMap<String,Object>();
				
				Map<String,Object> pa=new HashMap<String,Object>();
				pa.put("out_refund_no", PayUtil.create_out_refund_no());
				pa.put("out_trade_no", out_trade_no);
				pa.put("reason", reason);
				
			
				if(userDao.insertintoRefund(pa)!=1) throw new Exception();
				
				retMap.put("errcode", "0");
			    retMap.put("errmsg", "OK");
				return retMap;
		}

		@Override
		@Transactional
		public int updaterefundErrmsg(Map<String, Object> pa) {
			
			return userDao.updaterefundErrmsg(pa);
		}

		@Override
		@Transactional
		public Map<String, Object> findGoodsBykey(String key, int pageSize,
				int pageNum) throws Exception {
			
           Map<String,Object> retMap=new HashMap<String,Object>();
			
			Map<String,Object> pa=new HashMap<String,Object>();
			pa.put("key", key);
			
			PageHelper.startPage(pageNum,pageSize);
			List<Map<String,String>> li=userDao.findGoodsBykey(pa);
			
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
