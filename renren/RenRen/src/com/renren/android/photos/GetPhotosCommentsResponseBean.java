package com.renren.android.photos;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetPhotosCommentsResponseBean extends ResponseBean {
	private String response = null;
	public boolean isOver = false;

	public GetPhotosCommentsResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mPhotosCommentsResults = new ArrayList<PhotosCommentsResult>();
		}
		try {
			JSONArray array = new JSONArray(response);
			PhotosCommentsResult result = null;
			if (array.length() < 10) {
				isOver = true;
			}
			for (int i = array.length() - 1; i >= 0; i--) {
				result = new PhotosCommentsResult();
				result.setIs_whisper(array.getJSONObject(i)
						.getInt("is_whisper"));
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setComment_id(array.getJSONObject(i).getLong(
						"comment_id"));
				result.setText(array.getJSONObject(i).getString("text"));
				result.setTime(array.getJSONObject(i).getString("time"));
				result.setSource(array.getJSONObject(i).getString("source"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setHeadurl(array.getJSONObject(i).getString("headurl"));
				RenRenData.mPhotosCommentsResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
