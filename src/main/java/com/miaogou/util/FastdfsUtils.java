package com.miaogou.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;

import org.csource.common.NameValuePair;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.FileInfo;
import org.csource.fastdfs.ProtoCommon;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;

public class FastdfsUtils {
	
	 public static TrackerClient tracker = null;

	   static 
	  {
	    try
	    { 
	      String cp="/com/miaogou/util/fdfs_client.conf";
	      String path=FastdfsUtils.class.getResource(cp).getFile(); 
	      File file = new File(path);

	      if ((!file.exists()) || (!file.isFile())) {
	    	  System.out.println(".........");
	      }

	      ClientGlobal.init(file.getAbsolutePath());

	      tracker = new TrackerClient();

	    } catch (Exception e) {
	    	e.printStackTrace();
	    }
	  }

	  public static String[] uploadFile(byte[] file_buff, String file_ext_name, NameValuePair[] meta_list)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      StorageClient client = new StorageClient(trackerServer, null);
	      String[] res = client.upload_file(file_buff, file_ext_name, meta_list);
	      if (res == null) {
	        throw new Exception("upload file fail, error code: " + client.getErrorCode());
	      }
	      String[] arrayOfString1 = { res[0], res[1], getFileUrl(res[0], res[1], trackerServer) };
	      return arrayOfString1;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }

	  public static String[] uploadFile(String local_file_name, String file_ext_name, NameValuePair[] meta_list)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      StorageClient client = new StorageClient(trackerServer, null);
	      String[] res = client.upload_file(local_file_name, file_ext_name, meta_list);
	      if (res == null) {
	        throw new Exception("upload file fail, error code: " + client.getErrorCode());
	      }
	      String[] arrayOfString1 = { res[0], res[1], getFileUrl(res[0], res[1], trackerServer) };
	      return arrayOfString1;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }

	  public static NameValuePair[] getFileMetadata(String group_name, String remote_filename)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      StorageClient client = new StorageClient(trackerServer, null);
	      NameValuePair[] res = client.get_metadata(group_name, remote_filename);
	      if (res == null) {
	        throw new Exception("get file metadata fail, error code: " + client.getErrorCode());
	      }
	      NameValuePair[] arrayOfNameValuePair1 = res;
	      return arrayOfNameValuePair1;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }

	  public static FileInfo getFileInfo(String group_name, String remote_filename)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      StorageClient client = new StorageClient(trackerServer, null);
	      FileInfo res = client.get_file_info(group_name, remote_filename);
	      if (res == null) {
	        throw new Exception("get file info fail, error code: " + client.getErrorCode());
	      }
	      FileInfo localFileInfo1 = res;
	      return localFileInfo1;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }

	  public static String getFileUrl(String group_name, String remote_filename)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      String str = getFileUrl(group_name, remote_filename, trackerServer);
	      return str;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }

	  private static String getFileUrl(String group_name, String remote_filename, TrackerServer trackerServer)
	    throws Exception
	  {
	    String file_id = group_name + "/" + remote_filename;

	    InetSocketAddress inetSockAddr = trackerServer.getInetSocketAddress();
	    String file_url = "http://" + inetSockAddr.getAddress().getHostAddress();
	    if (ClientGlobal.g_tracker_http_port != 80) {
	      file_url = file_url + ":" + ClientGlobal.g_tracker_http_port;
	    }
	    file_url = file_url + "/" + file_id;
	    if (ClientGlobal.g_anti_steal_token) {
	      int ts = (int)(System.currentTimeMillis() / 1000L);
	      String token = ProtoCommon.getToken(file_id, ts, ClientGlobal.g_secret_key);
	      file_url = file_url + "?token=" + token + "&ts=" + ts;
	    }

	    return file_url;
	  }

	  public static int deleteFile(String group_name, String remote_filename)
	    throws Exception
	  {
	    TrackerServer trackerServer = tracker.getConnection();
	    try {
	      StorageClient client = new StorageClient(trackerServer, null);
	      
	      int i = client.delete_file(group_name, remote_filename);
	      return i;
	    } finally {
	      if (trackerServer != null)
	        trackerServer.close();
	    }
	  }
public static void main(String[] args) throws FileNotFoundException, IOException, Exception {
	
	String[] arr=new String[]{};
	System.out.println(1);
	arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//1.jpg","jpg",null);
	for(int i=0;i<arr.length;i++)
		System.out.println(arr[i]);
	
	
	System.out.println(1);
	arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//2.jpg","jpg",null);
	for(int i=0;i<arr.length;i++)
		System.out.println(arr[i]);
	
	 System.out.println(3);
	arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//3.jpg","jpg",null);
	for(int i=0;i<arr.length;i++)
		System.out.println(arr[i]);
	
	 System.out.println(4);
	 arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//4.jpg","jpg",null);
		for(int i=0;i<arr.length;i++)
			System.out.println(arr[i]);
	 
		 System.out.println(5);
	 arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//5.jpg","jpg",null);
		for(int i=0;i<arr.length;i++)
			System.out.println(arr[i]);
	 
	
	 
	
	 
	 System.out.println("icon");
	 arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//icon.jpg","jpg",null);
	 for(int i=0;i<arr.length;i++)
		System.out.println(arr[i]);
	 
	 System.out.println("大图");
	 arr=uploadFile("D://temp//香水//产品图片//香水-香奈儿//大图.jpg","jpg",null);
	 for(int i=0;i<arr.length;i++)
		System.out.println(arr[i]);
	}
	/*group1
	M00/00/00/CodDhlldo4qATjECAABErKcqlSY380.jpg
	http://123.207.40.218:8080/group1/M00/00/00/CodDhlldo4qATjECAABErKcqlSY380.jpg
	jpg*/
	/*int cnt=deleteFile("group1", "M00/00/00/CodDhlldo4qATjECAABErKcqlSY380.jpg");
	System.out.println(cnt);*/
	
	//http://119.29.101.199/group1/M00/00/00/CroLi1k4tUmAVNERAABErKcqlSY460.jpg
	/*String cp="/com/cumt/cs/bl/util/fdfs_client.conf";
	System.out.println(FastdfsUtils.class.getResource(cp).getFile()); 
	
	ClientGlobal.init(FastdfsUtils.class.getResource(cp).getFile());
	
	TrackerClient trackerClient = new TrackerClient();
	TrackerServer trackerServer = trackerClient.getConnection();
	//StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
	StorageServer storageServer = null;
	StorageClient1 storageClient1 = new StorageClient1(trackerServer, storageServer);
	
	String upload_file1 = storageClient1.upload_file1(FastdfsUtils.class.getResource(cp).getFile(), "conf", null);
	
	System.out.println(upload_file1);*/


}
