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

	int addToShoppingCart(Map<String, Object> pa);

	List<Map<String, String>> getShoppingCartList(Map<String, Object> pa);

	int isWxUserExits(Map<String, Object> pa);

	int updateWxUser(Map<String, Object> pa);

	int insertWxUser(Map<String, Object> pa);

	String selectUserIdByopenId(Map<String, Object> pa);

	Map<String, String> getGoodsInfo(Map<String, Object> pa);

	String getgetBigImg(Map<String, Object> pa);

	List<String> getImgList(Map<String, Object> pa);

	int createOrder(Map<String, Object> pa);

	int modifytotlfee(Map<String, Object> pa);

	int orderisResolved(Map<String, Object> pa);

	int Resolveorder(Map<String, Object> pa);

	String gettransaction_idByprepay_id(Map<String, String> pa);

	String getout_trade_noByprepay_id(Map<String, String> pa);



}
