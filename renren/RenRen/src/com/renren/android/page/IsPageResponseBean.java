package com.renren.android.page;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class IsPageResponseBean extends ResponseBean {

	public int mIspage;

	public IsPageResponseBean(String response) {
		super(response);
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			mIspage = object.getInt("result");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
