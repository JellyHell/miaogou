package com.miaogou.dao;

import java.util.List;
import java.util.Map;





public interface ISystemBackDao {

	List<Map<String, Object>> getGoodsList(Map<String, String> pa);

	int inserIntoGoods(Map<String, String> pa);

	int inserIntoGoodsDetails(Map<String, String> pa);

	int inserIntoAttachmentbigImg(Map<String, String> pa);

	int inserIntoAttachment(Map<String, String> pa);


}
