package com.miaogou.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;



public class PayUtil {
	 /**
     * 生成6位或10位随机数 param codeLength(多少位)
     * @return
     */
    public static String createCode(int codeLength) {
        String code = "";
        for (int i = 0; i < codeLength; i++) {
            code += (int) (Math.random() * 9);
        }
        return code;
    }
    /**
     * 生成商户订单号
     * @return
     */
    public static String create_out_trade_no(){
    	return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+createCode(8);
    }
    /**
     * 获取签名
     * @param pa
     * @param key
     * @return
     */
	public static String getSign(Map<String, Object> pa, String keyValue) {
		
		
		//去除为空的key
		List<String> keyli=new ArrayList<String>();
		for (String key : pa.keySet()) {  
		    if(!"".equals(pa.get(key)))
		    	keyli.add(key);
		} 
		
		//key ascii排序
		Collections.sort(keyli);
		StringBuffer stringA=new StringBuffer();
		
		for(String t:keyli){
			stringA.append(t+"="+pa.get(t)+"&");
		}
		stringA.append("key="+keyValue);
		String stringSignTemp=MD5Utils.getMD52(stringA.toString()).toUpperCase();
		return stringSignTemp;
	}
	
	/**
     * 
     * @param requestUrl请求地址
     * @param requestMethod请求方法
     * @param outputStr参数
	 * @throws DocumentException 
     */
    public static Map<String, String> httpRequest(String requestUrl,String requestMethod,String outputStr) throws DocumentException{
        // 创建SSLContext
        StringBuffer buffer=null;
        try{
        URL url = new URL(requestUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.connect();

        //往服务器端写内容
        if(null !=outputStr){
            OutputStream os=conn.getOutputStream();
            os.write(outputStr.getBytes("utf-8"));
            os.close();
        }
        // 读取服务器端返回的内容
        InputStream is = conn.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader br = new BufferedReader(isr);
        buffer = new StringBuffer();
        String line = null;
        while ((line = br.readLine()) != null) {
                      buffer.append(line);
        }
        }catch(Exception e){
            e.printStackTrace();
        }
        
        Map<String, String> map = new HashMap<String, String>();
        InputStream in=new ByteArrayInputStream(buffer.toString().getBytes());  
        // 读取输入流
        SAXReader reader = new SAXReader();
        Document document = reader.read(in);
        // 得到xml根元素
        Element root = document.getRootElement();
        // 得到根元素的所有子节点
        @SuppressWarnings("unchecked")
        List<Element> elementList = root.elements();
        for (Element element : elementList) {
            map.put(element.getName(), element.getText());
        }
        
        return map;
        }   
    public static String urlEncodeUTF8(String source){
        String result=source;
        try {
            result=java.net.URLEncoder.encode(source, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }
    /**
     * 验证返回的sign是否正确
     * @param resultMap
     * @return
     */
    public static boolean modifyNotifySign(Map<String, Object> pa,String keyValue) {
    			List<String> keyli=new ArrayList<String>();
    			for (String key : pa.keySet()) {  
    				if(!"sign".equals(key))
    			    	keyli.add(key);
    			} 
    			
    			//key ascii排序
    			Collections.sort(keyli);
    			StringBuffer stringA=new StringBuffer();
    			
    			for(String t:keyli){
    				stringA.append(t+"="+pa.get(t)+"&");
    			}
    			stringA.append("key="+keyValue);
    			String stringSignTemp=MD5Utils.getMD52(stringA.toString()).toUpperCase();
    			return pa.get("sign").equals(stringSignTemp);    
		
	}
	public static void main(String[] args) {
		String t="dwedwed#";
		System.out.println(t.substring(0, t.length()-1));
	}
	
}
