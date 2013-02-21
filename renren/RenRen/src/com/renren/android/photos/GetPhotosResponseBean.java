package com.renren.android.photos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetPhotosResponseBean extends ResponseBean {

	private String response = null;
	public boolean isOver = false;

	public GetPhotosResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mPhotosResults = new ArrayList<PhotosResult>();
		}
		try {
			JSONArray array = new JSONArray(response);
			PhotosResult result = null;
			if (array.length() < 30) {
				isOver = true;
			}
			for (int i = 0; i < array.length(); i++) {
				result = new PhotosResult();
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setAid(array.getJSONObject(i).getLong("aid"));
				result.setPid(array.getJSONObject(i).getLong("pid"));
				result.setTime(array.getJSONObject(i).getString("time"));
				result.setUrl_tiny(array.getJSONObject(i).getString("url_tiny"));
				result.setUrl_head(array.getJSONObject(i).getString("url_head"));
				result.setUrl_main(array.getJSONObject(i).getString("url_main"));
				result.setUrl_large(array.getJSONObject(i).getString(
						"url_large"));
				result.setCaption(array.getJSONObject(i).getString("caption"));
				result.setView_count(array.getJSONObject(i)
						.getInt("view_count"));
				result.setComment_count(array.getJSONObject(i).getInt(
						"comment_count"));
				RenRenData.mPhotosResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
