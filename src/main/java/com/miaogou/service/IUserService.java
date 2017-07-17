package com.miaogou.service;

import java.util.Map;


public interface IUserService {

	Map<String, Object> findGoodsByClass(String goodsClass, int pageSize, int pageNum) throws Exception;

	Map<String, Object> getBannerAd() throws Exception;



}
