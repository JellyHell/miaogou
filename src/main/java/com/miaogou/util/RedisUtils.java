package com.miaogou.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 * 
 * @author weicc
 *
 */
public class RedisUtils {

    private static JedisPool jedisPool = null;
    /**
     * 初始化Redis连接池
     */
    static {
        try {
            //加载redis配置文件
            ResourceBundle bundle = ResourceBundle.getBundle("init");

            if (bundle == null) {
                throw new IllegalArgumentException("[init.properties] is not found!");
            }

            int maxIdle = Integer.valueOf(bundle.getString("redis.maxIdle"));
            long maxWait = Long.valueOf(bundle.getString("redis.maxWait"));
            boolean testOnBorrow = Boolean.valueOf(bundle.getString("redis.testOnBorrow"));
            int database = Integer.valueOf(bundle.getString("redis.database"));
            int timeout =  Integer.valueOf(bundle.getString("redis.timeout"));
//            String password = bundle.getString("redis.password");

            //创建jedis池配置实例
            JedisPoolConfig config = new JedisPoolConfig();
            //设置池配置项值
            config.setMaxIdle(maxIdle);
            config.setTestOnBorrow(testOnBorrow);
            config.setMaxWaitMillis(maxWait);

            jedisPool = new JedisPool(config, bundle.getString("redis.host"), Integer.valueOf(bundle.getString("redis.port")),timeout,null,database); //无密码登录
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Jedis实例
     * @return
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 释放jedis资源
     * @param jedis
     */
    public static void returnResource(final Jedis jedis) {
        if (jedis != null) {
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 查询数据
     */
    public static String  find(String key,String value){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
           // jedis.hm
            return jedis.get(key);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 获取map
     * @param key
     * @return
     */
    public static Map<String,String> findMap(String key){
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            Map<String,String> retMap=new HashMap<String,String>();
            Set<String> set=jedis.hkeys(key);
            if(set!=null&&set.size()>0){
            	for(String k:set){
            		retMap.put(k, jedis.hmget(key, k).get(0));
            	}
            }else{
            	return null;
            }
            return retMap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }
    /**
     * 查询特定字符串
     */
    public static String findSubStr(String key,Integer startOffset,Integer endOffset){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.getrange(key, startOffset, endOffset);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }
    /**
     * 向缓存中设置字符串内容 新增数据|修改
     * @param key key
     * @param value value
     * @return
     * @throws Exception
     */
    public static int add(String key,String value) throws Exception{
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.set(key, value);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }

    public static Long getttl(String key) throws Exception{
    	Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            
            return jedis.ttl(key);
        } catch (Exception e) {
            e.printStackTrace();
            return (long)-1;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }
    /**
     * 增加map  
     * @param key
     * @param map
     * @param expire  过期时间  -1 位不过期  单位秒
     * @return
     */
    public static int addMap(String key,Map<String,String> map,int expire){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hmset(key,map);
            if(expire!=-1) jedis.expire(key, expire);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }

    /**
     * 删除缓存中得对象，根据key
     * @param key
     * @return
     */
    public static int del(String key){
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }finally{
            jedisPool.returnResource(jedis);
        }
    }
   public static void main(String[] args) throws Exception {
	   Map<String,String> map=new HashMap<String,String>();
	   map.put("name", "weichun");
	   map.put("age", "18");
	  // RedisUtils.addMap("map", map,100);
	   System.out.println(RedisUtils.findMap("map"));
	   System.out.println(RedisUtils.getttl("map"));
 }
}
