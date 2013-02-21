package com.renren.android.photos;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class PhotosAddCommentResponseBean extends ResponseBean{
	public int code;
	public PhotosAddCommentResponseBean(String response) {
		super(response);
		Resolve(response);
	}
	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("result")) {
				code = object.getInt("result");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
