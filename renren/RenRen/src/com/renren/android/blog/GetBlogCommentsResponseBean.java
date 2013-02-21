package com.renren.android.blog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetBlogCommentsResponseBean extends ResponseBean{
	private String response = null;
	public boolean isOver = false;
	public GetBlogCommentsResponseBean(String response) {
		super(response);
		this.response=response;
	}
	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mBlogCommentsResults = new ArrayList<BlogCommentsResult>();
		}
		try {
			JSONArray array = new JSONArray(response);
			BlogCommentsResult result = null;
			if (array.length() < 10) {
				isOver = true;
			}
			for (int i = array.length() - 1; i >= 0; i--) {
				result = new BlogCommentsResult();
				result.setIs_whisper(array.getJSONObject(i)
						.getInt("is_whisper"));
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setContent(array.getJSONObject(i).getString("content"));
				result.setId(array.getJSONObject(i).getInt("id"));
				result.setTime(array.getJSONObject(i).getString("time"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setHeadurl(array.getJSONObject(i).getString("headurl"));
				RenRenData.mBlogCommentsResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
