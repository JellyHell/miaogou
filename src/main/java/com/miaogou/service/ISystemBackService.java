package com.miaogou.service;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;



public interface ISystemBackService {

	Map<String, Object> getGoodsList();

	Map<String, Object> addGoods(String goods_name, String price,
			String goods_class, String brand, String firstBrand,
			String secondBrand, String introduceUrl, String introducePrice,
			String introduce, CommonsMultipartFile iconImgfile,
			CommonsMultipartFile bigImgfile, CommonsMultipartFile[] imgListfile) throws Exception;

	



}
