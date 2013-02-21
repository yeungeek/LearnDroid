package com.renren.android.emoticons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.renren.android.RequestListener;
import com.renren.android.util.Util;

public class EmoticonsHelper {
	/**
	 * 异步获取表情
	 * 
	 * @param renren
	 * @param pool
	 * @param param
	 * @param listener
	 */
	public void asyncGet(Executor pool, final EmoticonsRequestParam param,
			final RequestListener<EmoticonsResponseBean> listener) {
		pool.execute(new Runnable() {

			public void run() {
				listener.onStart();
				EmoticonsResponseBean bean = get(param);
				listener.onComplete(bean);
			}
		});
	}

	/**
	 * 同步获取表情
	 * 
	 * @param renren
	 * @param param
	 * @return
	 */
	public EmoticonsResponseBean get(EmoticonsRequestParam param) {
		String response = null;
		response = Util.GetJson(param.getParams());
		return new EmoticonsResponseBean(response);
	}

	/**
	 * 解析Json数据
	 * 
	 * @param json
	 * @return
	 */
	public List<EmoticonsResult> Resolve(String json) {
		List<EmoticonsResult> resultList = new ArrayList<EmoticonsResult>();
		JSONArray array;
		try {
			array = new JSONArray(json);
			JSONObject object = null;
			EmoticonsResult result = null;
			for (int i = 0; i < array.length(); i++) {
				object = array.getJSONObject(i);
				result = new EmoticonsResult();
				result.setEmotion(object.getString("emotion"));
				result.setIcon(object.getString("icon"));
				resultList.add(result);
			}
		} catch (JSONException e) {
			return resultList;
		}
		return resultList;
	}

	/**
	 * 下载表情符号到本地
	 * 
	 * @param pool
	 * @param url
	 * @param name
	 */
	public void downloadEmotcons(final List<EmoticonsResult> resultList) {
		for (int i = 0; i < resultList.size(); i++) {
			EmoticonsResult result = resultList.get(i);
			InputStream bitmapIs = null;
			Bitmap bitmap = null;
			try {
				URL url = new URL(result.getIcon());
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				bitmapIs = connection.getInputStream();
				bitmap = BitmapFactory.decodeStream(bitmapIs);
				bitmapIs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			File dir = new File("/sdcard/RenRenForAndroid/Emoticons/");
			if (!dir.exists()) {
				dir.mkdirs();
			}
			File bitmapFile = new File("/sdcard/RenRenForAndroid/Emoticons/"
					+ result.getEmotion());
			if (!bitmapFile.exists()) {
				try {
					bitmapFile.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			FileOutputStream fos;
			try {
				fos = new FileOutputStream(bitmapFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
				fos.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
