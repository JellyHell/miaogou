package com.miaogou.dao;

import java.util.List;
import java.util.Map;




public interface IUserDao {

	List<Map<String, String>> findGoodsByClass(Map<String, Object> pa);

	List<Map<String, String>> getBannerAd();

	String nextval(String string);

	int addDeliveryAddress(Map<String, Object> pa);

	List<Map<String, String>> getDeliveryAddress(Map<String, Object> pa);



}
