package com.miaogou.util;

import java.util.HashSet;
import java.util.Set;

public class StringUtil {
	//判断数组中是否有重复值
	public static boolean checkRepeat(String[] array){
	  Set<String> set = new HashSet<String>();
	  for(String str : array){
	    set.add(str);
	  }
	  if(set.size() != array.length){
	    return false;//有重复
	  }else{
	    return true;//不重复
	  }
	}
}
