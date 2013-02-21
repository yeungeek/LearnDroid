package com.renren.android.page;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class GetPageInfoResponseBean extends ResponseBean {
	private String response = null;

	public GetPageInfoResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(PageInfo pageInfo) {
		try {
			JSONObject object = new JSONObject(response);
			pageInfo.setPage_id(object.getInt("page_id"));
			pageInfo.setName(object.getString("name"));
			pageInfo.setHeadurl(object.getString("headurl"));
			pageInfo.setMainurl(object.getString("mainurl"));
			pageInfo.setStatus_id(object.getJSONObject("status").getInt(
					"status_id"));
			pageInfo.setContent(object.getJSONObject("status").getString(
					"content"));
			pageInfo.setTime(object.getJSONObject("status").getString("time"));
			pageInfo.setFans_count(object.getInt("fans_count"));
			pageInfo.setAlbums_count(object.getInt("albums_count"));
			pageInfo.setBlogs_count(object.getInt("blogs_count"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
