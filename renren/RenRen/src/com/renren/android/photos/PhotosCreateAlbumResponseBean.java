package com.renren.android.photos;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class PhotosCreateAlbumResponseBean extends ResponseBean{

	public long aid;
	public PhotosCreateAlbumResponseBean(String response) {
		super(response);
		Resolve(response);
	}
	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("aid")) {
				aid = object.getLong("aid");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
