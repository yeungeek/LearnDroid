package com.renren.android.newsfeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class NewsFeedResponseBean extends ResponseBean {
	public String response = null;
	public boolean isOver = false;

	public NewsFeedResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd, boolean isAll) {
		if (!isAdd) {
			if (isAll) {
				RenRenData.mNewsFeedAllResults = new ArrayList<NewsFeedResult>();
			} else {
				RenRenData.mNewsFeedResults = new ArrayList<NewsFeedResult>();
			}
		}
		try {
			JSONArray array = new JSONArray(response);
			JSONObject object = null;
			NewsFeedResult result = null;
			if (array.length() < 30) {
				isOver = true;
			}
			for (int i = 0; i < array.length(); i++) {
				result = new NewsFeedResult();
				object = array.getJSONObject(i);
				result.setActor_id(object.getInt("actor_id"));
				result.setActor_type(object.getString("actor_type"));
				result.setSource_id(object.getInt("source_id"));
				result.setFeed_type(object.getInt("feed_type"));
				result.setPost_id(object.getInt("post_id"));
				result.setHead_url(object.getString("headurl"));
				if (object.has("message")) {
					result.setMessage(object.getString("message"));
				}
				result.setTitle(object.getString("title"));
				result.setUpdate_time(object.getString("update_time"));
				result.setLikes_friend_count(object.getJSONObject("likes")
						.getInt("friend_count"));
				result.setLikes_user_like(object.getJSONObject("likes").getInt(
						"user_like"));
				result.setLikes_total_count(object.getJSONObject("likes")
						.getInt("total_count"));
				result.setName(object.getString("name"));
				result.setPrefix(object.getString("prefix"));
				if (object.has("source")) {
					result.setSource_text(object.getJSONObject("source")
							.getString("text"));
					result.setSource_href(object.getJSONObject("source")
							.getString("href"));
				}
				if (object.has("description")) {
					result.setDescription(object.getString("description"));
				}
				if (object.has("trace")) {
					result.setTrace_text(object.getJSONObject("trace")
							.getString("text"));
					List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
					for (int j = 0; j < object.getJSONObject("trace")
							.getJSONArray("node").length(); j++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("id", object.getJSONObject("trace")
								.getJSONArray("node").getJSONObject(j)
								.getString("id"));
						map.put("name", object.getJSONObject("trace")
								.getJSONArray("node").getJSONObject(j)
								.getString("name"));
						list.add(map);
					}
					result.setTrace_node(list);
				}

				result.setAttachment_count(object.getJSONArray("attachment")
						.length());
				if (object.getJSONArray("attachment").length() > 0) {
					for (int j = 0; j < object.getJSONArray("attachment")
							.length(); j++) {
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("content")) {
							result.setAttachment_content(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("content"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("raw_src")) {
							result.setAttachment_raw_src(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("raw_src"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("src")) {
							result.setAttachment_src(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("src"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("href")) {
							result.setAttachment_href(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("href"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("photo_count")) {
							result.setAttachment_photo_count(object
									.getJSONArray("attachment")
									.getJSONObject(j).getInt("photo_count"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("media_type")) {
							result.setAttachment_media_type(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("media_type"));
						}

						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("media_id")) {
							result.setAttachment_media_id(object
									.getJSONArray("attachment")
									.getJSONObject(j).getInt("media_id"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("owner_name")) {
							result.setAttachment_owner_name(object
									.getJSONArray("attachment")
									.getJSONObject(j).getString("owner_name"));
						}
						if (object.getJSONArray("attachment").getJSONObject(j)
								.has("owner_id")) {
							result.setAttachment_owner_id(object
									.getJSONArray("attachment")
									.getJSONObject(j).getInt("owner_id"));
						}

					}
				}

				result.setComments_count(object.getJSONObject("comments")
						.getInt("count"));
				if (object.getJSONObject("comments").has("comment")) {
					List<Map<String, Object>> commentList = new ArrayList<Map<String, Object>>();

					for (int j = 0; j < object.getJSONObject("comments")
							.getJSONArray("comment").length(); j++) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("uid", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getInt("uid"));
						map.put("comment_id", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getInt("comment_id"));
						map.put("time", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getString("time"));
						map.put("text", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getString("text"));
						map.put("name", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getString("name"));
						map.put("headurl", object.getJSONObject("comments")
								.getJSONArray("comment").getJSONObject(j)
								.getString("headurl"));
						commentList.add(map);
					}
					result.setComments(commentList);
				}
				if (isAll) {
					RenRenData.mNewsFeedAllResults.add(result);
				} else {
					RenRenData.mNewsFeedResults.add(result);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
