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

	Map<String, String> getGoodsItem(Map<String, String> pa);

	int attachemntDelBytabCode(Map<String, String> pa);

	int delGoodsDetails(Map<String, String> pa);

	int delGoods(Map<String, String> pa);


}
