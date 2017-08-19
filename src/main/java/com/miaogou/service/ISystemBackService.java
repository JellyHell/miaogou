package com.miaogou.service;

import java.util.Map;

import org.springframework.web.multipart.commons.CommonsMultipartFile;



public interface ISystemBackService {

	Map<String, Object> getGoodsList(String key, int pageSize, int currentPage);

	boolean userExists(String username);

	boolean passwordRight(String username, String password);

	Map<String, Object> GoodsDel(String goods_code) throws Exception;

	Map<String, Object> GoodsSaleStatusChange(String goods_code, String sale) throws Exception;

	Map<String, Object> getOrderList(int pageSize, int currentPage) throws Exception;

	Map<String, Object> getDictionaryData(String dicCode) throws Exception;

	Map<String, Object> getrefundList(int pageSize, int currentPage);

	Map<String, Object> addGoods(String goods_name, String goods_class,
			String sale, String price,String[] spec_sku, String[] spec_name,
			String[] spec_price, CommonsMultipartFile[] specImgfile,
			String brand, String firstBrand, String secondBrand,
			String introduceUrl, String introducePrice, String introduce,
			CommonsMultipartFile iconImgfile, CommonsMultipartFile[] imgListfile)throws Exception;

	Map<String, Object> getGoodsData(String goods_code) throws Exception;

	



}
