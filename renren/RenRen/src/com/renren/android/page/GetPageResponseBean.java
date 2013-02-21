package com.renren.android.page;

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

public class GetPageResponseBean extends ResponseBean {
	private Text_Util mText_Util;
	private static final String FORMAT = "^[a-z,A-Z].*$";

	public GetPageResponseBean(String response) {
		super(response);
		RenRenData.mPageResults.clear();
		RenRenData.mPageMap.clear();
		RenRenData.mPageSections.clear();
		RenRenData.mPagePositions.clear();
		RenRenData.mPageIndexer.clear();
		mText_Util = new Text_Util();
		Resolve(response);
	}

	private void Resolve(String response) {
		try {
			JSONArray array = new JSONArray(response);
			PageResult result = null;
			for (int i = 0; i < array.length(); i++) {
				result = new PageResult();
				result.setPageId(array.getJSONObject(i).getInt("page_id"));
				result.setName(array.getJSONObject(i).getString("name"));
				result.setHeadurl(array.getJSONObject(i).getString("headurl"));
				if (array.getJSONObject(i).has("desc")) {
					result.setDesc(array.getJSONObject(i).getString("desc"));
				}
				result.setCategory_id(array.getJSONObject(i).getInt(
						"category_id"));
				result.setCategory_name(array.getJSONObject(i).getString(
						"category_name"));
				result.setFansCount(array.getJSONObject(i).getInt("fans_count"));
				result.setIsFan(array.getJSONObject(i).getInt("is_fan"));
				result.setPinyin_name(mText_Util.getStringPinYin(result
						.getName()));
				if (result.getPinyin_name() != null
						&& result.getPinyin_name().length() > 0) {
					result.setFirst_name(result.getPinyin_name()
							.substring(0, 1).toUpperCase());
				}

				RenRenData.mPageResults.add(result);

				if (result.getFirst_name().matches(FORMAT)) {
					if (RenRenData.mPageSections.contains(result
							.getFirst_name())) {
						RenRenData.mPageMap.get(result.getFirst_name()).add(
								RenRenData.mPageResults.get(i));
					} else {
						RenRenData.mPageSections.add(result.getFirst_name());
						List<PageResult> list = new ArrayList<PageResult>();
						list.add(RenRenData.mPageResults.get(i));
						RenRenData.mPageMap.put(result.getFirst_name(), list);
					}
				} else {
					if (RenRenData.mPageSections.contains("#")) {
						RenRenData.mPageMap.get("#").add(
								RenRenData.mPageResults.get(i));
					} else {
						RenRenData.mPageSections.add("#");
						List<PageResult> list = new ArrayList<PageResult>();
						list.add(RenRenData.mPageResults.get(i));
						RenRenData.mPageMap.put("#", list);
					}
				}
			}
			Collections.sort(RenRenData.mPageSections);
			int position = 0;
			for (int i = 0; i < RenRenData.mPageSections.size(); i++) {
				Map<String, Integer> map = new HashMap<String, Integer>();

				for (int j = 0; j < RenRenData.mPageMap.get(
						RenRenData.mPageSections.get(i)).size(); j++) {
					PageResult result2 = RenRenData.mPageMap.get(
							RenRenData.mPageSections.get(i)).get(j);
					if (!map.containsKey(result2.getName().substring(0, 1))) {
						map.put(result2.getName().substring(0, 1),
								(position + j));
					}
				}
				RenRenData.mPageFirstNamePosition.put(
						RenRenData.mPageSections.get(i), map);
				RenRenData.mPageIndexer.put(RenRenData.mPageSections.get(i),
						position);
				RenRenData.mPagePositions.add(position);
				position += RenRenData.mPageMap.get(
						RenRenData.mPageSections.get(i)).size();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
