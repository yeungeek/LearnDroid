package com.renren.android.user;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetVisitorResponseBean extends ResponseBean {

	private String response = null;
	public boolean isOver = false;

	public GetVisitorResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mVisitorResults = new ArrayList<VisitorResult>();
		}
		try {
			JSONObject object = new JSONObject(response);
			JSONArray array = object.getJSONArray("visitors");
			VisitorResult result = null;
			if (array.length() < 20) {
				isOver = true;
			}
			for (int i = 0; i < array.length(); i++) {
				result = new VisitorResult();
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setTime(array.getJSONObject(i).getString("time"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setHeadurl(array.getJSONObject(i).getString("headurl"));
				RenRenData.mVisitorResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
