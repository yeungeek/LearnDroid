package com.renren.android.friends;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class FriendsFindResponseBean extends ResponseBean {
	public String response = null;
	public boolean isOver = false;

	public FriendsFindResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mFriendsFindResults = new ArrayList<FriendsFindResult>();
		}
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("friends")) {
				JSONArray array = object.getJSONArray("friends");
				FriendsFindResult result = null;
				if (array.length() < 20) {
					isOver = true;
				}
				for (int i = 0; i < array.length(); i++) {
					result = new FriendsFindResult();
					result.setId(array.getJSONObject(i).getInt("id"));
					result.setName(array.getJSONObject(i).getString("name"));
					result.setTinyurl(array.getJSONObject(i).getString("tinyurl"));
					result.setInfo(array.getJSONObject(i).getString("info"));
					result.setIsFriend(array.getJSONObject(i).getInt("isFriend"));
					RenRenData.mFriendsFindResults.add(result);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
