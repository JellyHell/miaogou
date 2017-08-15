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

	int updateOrderStateByPrePar_Id(Map<String, String> pa);

	int isGoodsInShoppingCart(Map<String, Object> pa);

	int updateShoppingCartGoodsNum(Map<String, Object> pa);

	int getGoodsNumInshoppingCart(Map<String, Object> pa);

	int deletefromShopingByuserIdAndGoodsCode(Map<String, Object> pa);

	int reduce1FromShoppingCart(Map<String, Object> pa);

	int delFromShoppingCart(Map<String, Object> pa);

	int createOrderDeliveryAddress(Map<String, Object> pa);

	int createOrderGoods(Map<String, Object> pa);

	List<Map<String, Object>> getOrderList(Map<String, Object> pa);

	Map<String, String> getTotalFeeBytransaction_id(Map<String, Object> pa);

	int updateFreundto0(Map<String, Object> pa);

	int insertintoRefund(Map<String, Object> pa);

	int updaterefundErrmsg(Map<String, Object> pa);

	List<Map<String, String>> findGoodsBykey(Map<String, Object> pa);

	int isOrderPayed(Map<String, Object> pa);



}
