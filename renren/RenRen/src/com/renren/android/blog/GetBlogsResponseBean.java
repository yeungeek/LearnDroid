package com.renren.android.blog;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetBlogsResponseBean extends ResponseBean {
	private String response = null;
	public boolean isOver=false;
	public GetBlogsResponseBean(String response) {
		super(response);
		this.response=response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mBlogResults = new ArrayList<BlogsResult>();
		}
		try {
			JSONObject object = new JSONObject(response);
			BlogsResult result = null;
			if (object.has("blogs")) {
				JSONArray array = object.getJSONArray("blogs");
				if (array.length()<20) {
					isOver=true;
				}
				for (int i = 0; i < array.length(); i++) {
					result = new BlogsResult();
					result.setUid(object.getInt("uid"));
					result.setName(object.getString("name"));
					result.setTotal(object.getInt("total"));
					result.setVisable(array.getJSONObject(i).getInt("visable"));
					result.setComment_count(array.getJSONObject(i).getInt(
							"comment_count"));
					result.setContent(array.getJSONObject(i).getString(
							"content"));
					result.setId(array.getJSONObject(i).getInt("id"));
					result.setTitle(array.getJSONObject(i).getString("title"));
					result.setTime(array.getJSONObject(i).getString("time"));
					result.setCate(array.getJSONObject(i).getString("cate"));
					result.setShare_count(array.getJSONObject(i).getInt(
							"share_count"));
					result.setView_count(array.getJSONObject(i).getInt(
							"view_count"));
					RenRenData.mBlogResults.add(result);
				}
			} else {
				result = new BlogsResult();
				result.setUid(object.getInt("uid"));
				result.setName(object.getString("name"));
				result.setTotal(object.getInt("total"));
				RenRenData.mBlogResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
