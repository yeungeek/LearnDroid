package com.renren.android.blog;

import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.ResponseBean;

public class BlogAddCommentResponseBean extends ResponseBean {
	public int code;

	public BlogAddCommentResponseBean(String response) {
		super(response);
		System.out.println(response);
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONObject object = new JSONObject(response);
			if (object.has("result")) {
				code = object.getInt("result");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
