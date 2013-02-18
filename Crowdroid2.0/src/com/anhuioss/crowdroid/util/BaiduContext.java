package com.anhuioss.crowdroid.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;

public class BaiduContext extends Application {

	private static BaiduContext instance;
	BMapManager mBMapMan = null;
	String mStrKey = "06EF79A09782875EFB91C8F4182FADD491A6259B";
	boolean m_bKeyRight = true;

	public BMapManager getMapManager() {
		return mBMapMan;
	}

	public static class MyGeneralListener implements MKGeneralListener {
		@Override
		public void onGetNetworkState(int iError) {
			Log.d("MyGeneralListener", "onGetNetworkState error is " + iError);
			Toast.makeText(BaiduContext.getInstance(), "GetNetworkState",
					Toast.LENGTH_LONG).show();
		}

		@Override
		public void onGetPermissionState(int iError) {
			Log.d("MyGeneralListener", "onGetPermissionState error is "
					+ iError);
			if (iError == MKEvent.ERROR_PERMISSION_DENIED) {
				Toast.makeText(BaiduContext.getInstance(), "permissionDenied",
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		if (mBMapMan != null) {
			mBMapMan.destroy();
			mBMapMan = null;
		}
		super.onTerminate();
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		mBMapMan = new BMapManager(this);
		mBMapMan.init(this.mStrKey, new MyGeneralListener());
	}

	public static BaiduContext getInstance() {
		return instance;
	}

	// public Document loadXML(String xmlFileName) {
	// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	//
	// try {
	// DocumentBuilder db = dbf.newDocumentBuilder();
	// InputStream is = getAssets().open(xmlFileName);
	// Document doc = db.parse(is);
	// is.close();
	// return doc;
	// } catch (ParserConfigurationException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// } catch (SAXException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	// public String ReadFile(String fileName) {
	//
	// InputStream inputStream;
	// try {
	// inputStream = getAssets().open(fileName);
	// InputStreamReader inputreader = new InputStreamReader(inputStream);
	// BufferedReader buffreader = new BufferedReader(inputreader);
	// String line;
	// StringBuilder text = new StringBuilder();
	//
	// try {
	// while ((line = buffreader.readLine()) != null) {
	// text.append(line);
	// }
	// } catch (IOException e) {
	// return null;
	// }
	// return text.toString();
	//
	// } catch (IOException e1) {
	// // TODO Auto-generated catch block
	// e1.printStackTrace();
	// }
	// return null;
	// }

	// public Bitmap getImageFromAssetFile(String fileName) {
	// Bitmap image = null;
	// try {
	// AssetManager am = getAssets();
	// InputStream is = am.open(fileName);
	// image = BitmapFactory.decodeStream(is);
	// is.close();
	// } catch (Exception e) {
	// Log.d("missing", e.getMessage());
	// e.printStackTrace();
	// }
	// return image;
	// }
}
