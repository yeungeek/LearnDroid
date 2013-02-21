package com.renren.android.photos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetAlbumsResponseBean extends ResponseBean {
	private String response = null;
	public boolean isOver = false;

	public GetAlbumsResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mAlbumsResults = new ArrayList<AlbumsResult>();
		}
		try {
			JSONArray array = new JSONArray(response);
			AlbumsResult result = null;
			if (array.length() < 10) {
				isOver = true;
			}
			for (int i = 0; i < array.length(); i++) {
				result = new AlbumsResult();
				result.setAid(array.getJSONObject(i).getLong("aid"));
				result.setUrl(array.getJSONObject(i).getString("url"));
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setCreate_time(array.getJSONObject(i).getString(
						"create_time"));
				result.setUpdate_time(array.getJSONObject(i).getString(
						"update_time"));
				result.setDescription(array.getJSONObject(i).getString(
						"description"));
				result.setLoaction(array.getJSONObject(i).getString("location"));
				result.setSize(array.getJSONObject(i).getInt("size"));
				result.setVisible(array.getJSONObject(i).getInt("visible"));
				result.setComment_count(array.getJSONObject(i).getInt(
						"comment_count"));
				result.setType(array.getJSONObject(i).getInt("type"));
				RenRenData.mAlbumsResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
