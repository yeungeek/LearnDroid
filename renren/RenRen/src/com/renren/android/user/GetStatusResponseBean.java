package com.renren.android.user;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;

public class GetStatusResponseBean extends ResponseBean {
	private String response = null;
	public boolean isOver = false;

	public GetStatusResponseBean(String response) {
		super(response);
		this.response = response;
	}

	public void Resolve(boolean isAdd) {
		if (!isAdd) {
			RenRenData.mStatusResults = new ArrayList<StatusResult>();
		}
		try {
			JSONArray array = new JSONArray(response);
			StatusResult result = null;
			if (array.length() < 20) {
				isOver = true;
			}
			for (int i = 0; i < array.length(); i++) {
				result = new StatusResult();
				result.setUid(array.getJSONObject(i).getInt("uid"));
				result.setComment_count(array.getJSONObject(i).getInt(
						"comment_count"));
				result.setSource_url(array.getJSONObject(i).getString(
						"source_url"));
				result.setStatus_id(array.getJSONObject(i).getInt("status_id"));
				result.setMessage(array.getJSONObject(i).getString("message"));
				result.setTime(array.getJSONObject(i).getString("time"));
				result.setForward_count(array.getJSONObject(i).getInt(
						"forward_count"));
				result.setSource_name(array.getJSONObject(i).getString(
						"source_name"));
				if (array.getJSONObject(i).has("place")) {
					result.setLocation(array.getJSONObject(i)
							.getJSONObject("place").getString("location"));
					result.setName(array.getJSONObject(i)
							.getJSONObject("place").getString("name"));
					result.setLbs_id(array.getJSONObject(i)
							.getJSONObject("place").getInt("lbs_id"));
					result.setLongitude(array.getJSONObject(i)
							.getJSONObject("place").getString("longitude"));
					result.setLatitude(array.getJSONObject(i)
							.getJSONObject("place").getString("latitude"));
				}
				if (array.getJSONObject(i).has("forward_message")) {
					result.setForward_message(array.getJSONObject(i).getString(
							"forward_message"));
				}
				if (array.getJSONObject(i).has("root_uid")) {
					result.setRoot_uid(array.getJSONObject(i)
							.getInt("root_uid"));
				}
				if (array.getJSONObject(i).has("root_status_id")) {
					result.setRoot_status_id(array.getJSONObject(i).getInt(
							"root_status_id"));
				}
				if (array.getJSONObject(i).has("root_message")) {
					result.setRoot_message(array.getJSONObject(i).getString(
							"root_message"));
				}
				if (array.getJSONObject(i).has("root_username")) {
					result.setRoot_username(array.getJSONObject(i).getString(
							"root_username"));
				}
				RenRenData.mStatusResults.add(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
