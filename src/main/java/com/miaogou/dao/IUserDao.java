package com.miaogou.dao;

import java.util.List;
import java.util.Map;




public interface IUserDao {

	List<Map<String, String>> findGoodsByClass(Map<String, Object> pa);



}
