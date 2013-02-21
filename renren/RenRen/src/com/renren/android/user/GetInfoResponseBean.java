package com.renren.android.user;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.ResponseBean;

public class GetInfoResponseBean extends ResponseBean {
	private String response = null;

	public GetInfoResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(UserInfo userInfo) {
		try {
			JSONArray array = new JSONArray(response);
			if (array.getJSONObject(0).has("mainurl")) {
				userInfo.setMainurl(array.getJSONObject(0).getString("mainurl"));
			}
			userInfo.setZidou(array.getJSONObject(0).getInt("zidou"));
			userInfo.setVip(array.getJSONObject(0).getInt("vip"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
