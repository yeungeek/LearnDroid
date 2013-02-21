package com.renren.android.blog;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class GetBlogResponseBean extends ResponseBean {
	public boolean error;
	public BlogResult result = new BlogResult();

	public GetBlogResponseBean(String response) {
		super(response);
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("request_args")) {
				error = true;
			} else {
				error = false;
				result.setId(object.getInt("id"));
				result.setUid(object.getInt("uid"));
				result.setVisable(object.getInt("visable"));
				result.setComment_count(object.getInt("comment_count"));
				result.setContent(object.getString("content"));
				result.setTime(object.getString("time"));
				result.setTitle(object.getString("title"));
				result.setShare_count(object.getInt("share_count"));
				result.setName(object.getString("name"));
				result.setView_count(object.getInt("view_count"));
				result.setHeadurl(object.getString("headurl").replace(
						"http://head.xiaonei.com/photos/", ""));
			}
		} catch (JSONException e) {
			error = true;
		}
	}
}
