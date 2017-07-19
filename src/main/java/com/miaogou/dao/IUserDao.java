package com.miaogou.dao;

import java.util.List;
import java.util.Map;




public interface IUserDao {

	List<Map<String, String>> findGoodsByClass(Map<String, Object> pa);

	List<Map<String, String>> getBannerAd();

	String nextval(String string);

	int addDeliveryAddress(Map<String, Object> pa);

	List<Map<String, String>> getDeliveryAddress(Map<String, Object> pa);

	int delDeliveryAddress(Map<String, Object> pa);

	int setAllNotDefaultAddress(Map<String, Object> pa);

	int setDefaultDeliveryAddress(Map<String, Object> pa);

	List<Map<String, String>> getBestSalesGoods();

	int register(Map<String, Object> pa);

	int isRegister(Map<String, Object> pa);

	int addScore(Map<String, Object> pa);

	List<Map<String, String>> getScoreList(Map<String, Object> pa);

	int insertAttachment(Map<String, Object> pa);

	int insertWish(Map<String, Object> pa);

	List<Map<String, Object>> getWishList(Map<String, Object> pa);

	List<Map<String, String>> getAttachmentList(Map<String, Object> pa);



}
