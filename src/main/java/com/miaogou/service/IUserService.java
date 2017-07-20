package com.miaogou.service;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;


public interface IUserService {

	Map<String, Object> findGoodsByClass(String goodsClass, int pageSize, int pageNum) throws Exception;

	Map<String, Object> getBannerAd() throws Exception;

	Map<String, Object> addDeliveryAddress(String openId, String name,
			String phone, String area, String address, String postCode) throws Exception;

	Map<String, Object> getDeliveryAddress(String openId) throws Exception;

	Map<String, Object> delDeliveryAddress(String openId, String code) throws Exception;

	Map<String, Object> setDefaultDeliveryAddress(String openId, String code) throws Exception;

	Map<String, Object> getBestSalesGoods(int pageSize, int pageNum) throws Exception;

	Map<String, Object> register(String openId) throws Exception;

	Map<String, Object> getRegisterStatus(String openId) throws Exception;

	Map<String, Object> getScoreList(String openId, int pageSize, int pageNum) throws Exception;

	Map<String, Object> uploadWishList(String openId,
			CommonsMultipartFile[] files, String goodsName, String url) throws Exception;

	Map<String, Object> getWishList(String openId, int pageSize, int pageNum) throws Exception;

	Map<String, Object> addToShoppingCart(String openId, String goodsCode,
			String goodsNum) throws Exception;

	Map<String, Object> getShoppingCartList(String openId) throws Exception;



}
