package com.miaogou.service;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


public interface IUserService {

	Map<String, Object> findGoodsByClass(String goodsClass, int pageSize, int pageNum) throws Exception;

	Map<String, Object> getBannerAd() throws Exception;

	Map<String, Object> addDeliveryAddress(String userId, String name,
			String phone, String area, String address, String postCode) throws Exception;

	Map<String, Object> getDeliveryAddress(String userId) throws Exception;

	Map<String, Object> delDeliveryAddress(String userId, String code) throws Exception;

	Map<String, Object> setDefaultDeliveryAddress(String userId, String code) throws Exception;

	Map<String, Object> getBestSalesGoods(int pageSize, int pageNum) throws Exception;

	Map<String, Object> register(String userId) throws Exception;

	Map<String, Object> getRegisterStatus(String userId) throws Exception;

	Map<String, Object> getScoreList(String userId, int pageSize, int pageNum) throws Exception;

	Map<String, Object> uploadWishList(String userId,
			CommonsMultipartFile[] files, String goodsName, String url) throws Exception;

	Map<String, Object> getWishList(String openId, int pageSize, int pageNum) throws Exception;

	Map<String, Object> addToShoppingCart(String userId, String goodsCode,
			String goodsNum) throws Exception;

	Map<String, Object> getShoppingCartList(String userId) throws Exception;

	Map<String, Object> addWxUser(String userId, String nickName,
			String avatarUrl, String gender, String province, String city,
			String country) throws Exception;

	Map<String, Object> insertOrSelectUserId(String openid) throws Exception;

	Map<String, Object> getDetails(String goods_code) throws Exception;

	int createOrder(Map<String, Object> pa);

	int modifytotlfee(Map<String, Object> resultMap);

	boolean orderisResolved(Map<String, Object> resultMap);

	int Resolveorder(Map<String, Object> resultMap);

	String gettransaction_idByprepay_id(String prepay_id);

	String getout_trade_noByprepay_id(String prepay_id);

	int updateOrderState(String prepay_id);

	Map<String, Object> reduce1FromShoppingCart(String userId, String goodsCode) throws Exception;

	Map<String, Object> delFromShoppingCart(String code) throws Exception;



}
