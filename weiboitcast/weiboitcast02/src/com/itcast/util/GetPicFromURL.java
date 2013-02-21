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
		  //������������
		  HttpURLConnection hc=(HttpURLConnection)url.openConnection();
		  //��ȡͼƬ������
		  InputStream is=hc.getInputStream();
		  bd=new BitmapDrawable(is);//ʹ�����������󴴽�һ��bitmapdrawable
		  hc.disconnect();//�ر���������
		}catch(Exception e){}
		return bd;
	}
	
}
