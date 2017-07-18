package com.miaogou.service;

import java.util.List;
import java.util.Map;


public interface IUserService {

	Map<String, Object> findGoodsByClass(String goodsClass, int pageSize, int pageNum) throws Exception;

	Map<String, Object> getBannerAd() throws Exception;

	Map<String, Object> addDeliveryAddress(String openId, String name,
			String phone, String area, String address, String postCode) throws Exception;

	Map<String, Object> getDeliveryAddress(String openId) throws Exception;

	Map<String, Object> delDeliveryAddress(String openId, String code) throws Exception;



}
