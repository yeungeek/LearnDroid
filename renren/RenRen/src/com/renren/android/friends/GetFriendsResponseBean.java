package com.renren.android.friends;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import com.renren.android.RenRenData;
import com.renren.android.ResponseBean;
import com.renren.android.util.Text_Util;

public class GetFriendsResponseBean extends ResponseBean {

	private Text_Util mText_Util;
	private static final String FORMAT = "^[a-z,A-Z].*$";

	public GetFriendsResponseBean(String response) {
		super(response);
		RenRenData.mFriendsResults.clear();
		RenRenData.mFriendsMap.clear();
		RenRenData.mFriendsSections.clear();
		RenRenData.mFriendsPositions.clear();
		RenRenData.mFriendsIndexer.clear();
		mText_Util = new Text_Util();
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONArray array = new JSONArray(response);
			FriendsResult result = null;
			for (int i = 0; i < array.length(); i++) {
				result = new FriendsResult();
				result.setId(array.getJSONObject(i).getInt("id"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setSex(array.getJSONObject(i).getString("sex"));
				result.setTinyurl(array.getJSONObject(i).getString("tinyurl"));
				result.setHeadurl(array.getJSONObject(i).getString("headurl"));
				result.setPinyin_name(mText_Util.getStringPinYin(result
						.getName()));
				if (result.getPinyin_name() != null
						&& result.getPinyin_name().length() > 0) {
					result.setFirst_name(result.getPinyin_name()
							.substring(0, 1).toUpperCase());
				}
				RenRenData.mFriendsResults.add(result);

				if (result.getFirst_name().matches(FORMAT)) {
					if (RenRenData.mFriendsSections.contains(result
							.getFirst_name())) {
						RenRenData.mFriendsMap.get(result.getFirst_name()).add(
								RenRenData.mFriendsResults.get(i));
					} else {
						RenRenData.mFriendsSections.add(result.getFirst_name());
						List<FriendsResult> list = new ArrayList<FriendsResult>();
						list.add(RenRenData.mFriendsResults.get(i));
						RenRenData.mFriendsMap
								.put(result.getFirst_name(), list);
					}
				} else {
					if (RenRenData.mFriendsSections.contains("#")) {
						RenRenData.mFriendsMap.get("#").add(
								RenRenData.mFriendsResults.get(i));
					} else {
						RenRenData.mFriendsSections.add("#");
						List<FriendsResult> list = new ArrayList<FriendsResult>();
						list.add(RenRenData.mFriendsResults.get(i));
						RenRenData.mFriendsMap.put("#", list);
					}
				}
			}
			Collections.sort(RenRenData.mFriendsSections);
			int position = 0;
			for (int i = 0; i < RenRenData.mFriendsSections.size(); i++) {
				Map<String, Integer> map = new HashMap<String, Integer>();

				for (int j = 0; j < RenRenData.mFriendsMap.get(
						RenRenData.mFriendsSections.get(i)).size(); j++) {
					FriendsResult result2 = RenRenData.mFriendsMap.get(
							RenRenData.mFriendsSections.get(i)).get(j);
					if (!map.containsKey(result2.getName().substring(0, 1))) {
						map.put(result2.getName().substring(0, 1),
								(position + j));
					}
				}
				RenRenData.mFriendsFirstNamePosition.put(
						RenRenData.mFriendsSections.get(i), map);
				RenRenData.mFriendsIndexer.put(
						RenRenData.mFriendsSections.get(i), position);
				RenRenData.mFriendsPositions.add(position);
				position += RenRenData.mFriendsMap.get(
						RenRenData.mFriendsSections.get(i)).size();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
