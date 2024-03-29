package com.miaogou.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;

import org.apache.commons.codec.digest.DigestUtils;


public class MD5Utils {
	
	 public static String getMD5(String str) {
	        try {
	            // 生成一个MD5加密计算摘要
	            MessageDigest md = MessageDigest.getInstance("MD5");
	            // 计算md5函数
	            md.update(str.getBytes());
	            // digest()最后确定返回md5 hash值，返回值为8为字符串。因为md5 hash值是16位的hex值，实际上就是8位的字符
	            // BigInteger函数则将8位的字符串转换成16位hex值，用字符串来表示；得到字符串形式的hash值
	            return new BigInteger(1, md.digest()).toString(16);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return str;
	        }
	    }
	 
	 public static String getMD52(String str) {
		 return DigestUtils.md5Hex(getContentBytes(str, "utf-8"));
	    }
	 
	 public static byte[] getContentBytes(String content, String charset) {
	        if (charset == null || "".equals(charset)) {
	            return content.getBytes();
	        }
	        try {
	            return content.getBytes(charset);
	        } catch (UnsupportedEncodingException e) {
	            throw new RuntimeException("MD5签名过程中出现错误,指定的编码集不对,您目前指定的编码集是:" + charset);
	        }
	    }
	 public static void main(String[] args) {
		System.out.println(getMD5("Migu123!"));
	}
}
