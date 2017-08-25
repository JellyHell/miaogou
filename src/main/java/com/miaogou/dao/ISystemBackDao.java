package com.miaogou.dao;

import java.util.List;
import java.util.Map;





public interface ISystemBackDao {

	List<Map<String, Object>> getGoodsList(Map<String, String> pa);

	int inserIntoGoods(Map<String, String> pa);

	int inserIntoGoodsDetails(Map<String, String> pa);

	int inserIntoAttachmentbigImg(Map<String, String> pa);

	int inserIntoAttachment(Map<String, String> pa);

	int userExists(String username);

	int passwordRight(Map<String, String> pa);

	List<Map<String, String>> getAttachmentBytabCode(Map<String, String> pa);

	Map<String, String> getGoodsItem(Map<String, Object> pa);

	int attachemntDelBytabCode(Map<String, String> pa);

	int delGoodsDetails(Map<String, String> pa);

	int delGoods(Map<String, String> pa);

	int GoodsSaleStatusChange(Map<String, String> pa);

	int SkuExits(Map<String, String> pa);

	List<Map<String, Object>> getOrderList();

	List<Map<String, Object>> getDictionaryData(Map<String, String> pa);

	List<Map<String, Object>> getrefundList();

	int inserIntoGoodsSpec(Map<String, String> pa);

	List<Map<String, String>> getGoodsSpecList(Map<String, String> pa);

	List<Map<String, String>> getSpecList(Map<String, Object> pa);

	Map<String, String> getGoodsItem2(Map<String, Object> pa);

	int updateMgGoods(Map<String, String> pa);

	int updateMgGoodsDetails(Map<String, String> pa);

	int delgoodsSpec(Map<String, String> pa);

	int delAttachment(Map<String, String> pa);

}
