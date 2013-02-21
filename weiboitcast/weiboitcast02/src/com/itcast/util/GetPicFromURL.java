package com.itcast.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.drawable.BitmapDrawable;

public class GetPicFromURL {

	public static BitmapDrawable getPic(URL url)
	{
		BitmapDrawable bd=null;
		try{
		  //建立网络连接
		  HttpURLConnection hc=(HttpURLConnection)url.openConnection();
		  //获取图片输入流
		  InputStream is=hc.getInputStream();
		  bd=new BitmapDrawable(is);//使用输入流对象创建一个bitmapdrawable
		  hc.disconnect();//关闭网络连接
		}catch(Exception e){}
		return bd;
	}
	
}
