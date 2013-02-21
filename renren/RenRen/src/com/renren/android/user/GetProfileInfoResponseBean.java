package com.renren.android.user;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class GetProfileInfoResponseBean extends ResponseBean {

	private String response = null;

	public GetProfileInfoResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(UserInfo userInfo) {
		try {
			JSONObject object = new JSONObject(response);
			userInfo.setUid(object.getInt("uid"));
			userInfo.setContent(object.getJSONObject("status").getString(
					"content"));
			userInfo.setTime(object.getJSONObject("status").getString("time"));
			userInfo.setVisitors_count(object.getInt("visitors_count"));
			userInfo.setStar(object.getInt("star"));
			userInfo.setNetwork_name(object.getString("network_name"));
			userInfo.setHeadurl(object.getString("headurl"));
			userInfo.setStatus_count(object.getInt("status_count"));
			userInfo.setAlbums_count(object.getInt("albums_count"));
			userInfo.setGuestbook_count(object.getInt("guestbook_count"));
			userInfo.setBlogs_count(object.getInt("blogs_count"));
			userInfo.setFriends_count(object.getInt("friends_count"));
			userInfo.setName(object.getString("name"));
			if (object.has("base_info")) {
				userInfo.setBirth_year(object.getJSONObject("base_info")
						.getJSONObject("birth").getString("birth_year"));
				userInfo.setBirth_mouth(object.getJSONObject("base_info")
						.getJSONObject("birth").getString("birth_month"));
				userInfo.setBirth_day(object.getJSONObject("base_info")
						.getJSONObject("birth").getString("birth_day"));
				userInfo.setProvince(object.getJSONObject("base_info")
						.getJSONObject("hometown").getString("province"));
				userInfo.setCity(object.getJSONObject("base_info")
						.getJSONObject("hometown").getString("city"));
				userInfo.setGender(object.getJSONObject("base_info").getInt(
						"gender"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
