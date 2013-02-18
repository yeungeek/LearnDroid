package com.anhuioss.crowdroid.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

public class TagAnalysis {

	// -----------------------------------------------------------------------------
	/**
	 * analysis the status to get index of HashTag
	 */
	// -----------------------------------------------------------------------------
	public static ArrayList<String> getIndex(String status, String interval) {

		ArrayList<String> result = new ArrayList<String>();

		String s1 = status;
		Pattern p = Pattern
				.compile(interval
						+ "[^\n^,^.^ ^　^:^@^#^，^。^；^：^;^＠^＃^[\u2E80-\u9FFF]+$^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5]*");
		Matcher m = p.matcher(s1);
		while (m.find()) {
			result.add(String.valueOf(m.start()));
			result.add(String.valueOf(m.end()));
		}

		return result;

	}

	public static ArrayList<String> getHashTagIndex(String status,
			String service) {

		ArrayList<String> result = new ArrayList<String>();

		Pattern p = null;
		if (service.equals(IGeneral.SERVICE_NAME_TWITTER)
				|| service.equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
			p = Pattern.compile("#" + "[^,^.^ ^　^:^@^#^，^。^；^：^＠^＃]*");
		} else {
			p = Pattern
					.compile("#"
							+ "[^\n^,^.^ ^　^:^@^#^，^。^；^：^＠^＃^[\u2E80-\u9FFF]+$^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5]*");
		}

		String s1 = status;
		Matcher m = p.matcher(s1);
		while (m.find()) {
			result.add(String.valueOf(m.start()));
			result.add(String.valueOf(m.end()));
		}

		return result;
	}

	// -----------------------------------------------------------------------------
	/**
	 * analysis the status to get index of HashTag Just for Sina
	 */
	// -----------------------------------------------------------------------------
	public static ArrayList<String> getIndexForSina(String status,
			String interval) {

		ArrayList<String> result = new ArrayList<String>();

		String s1 = status;
		Pattern p = null;
		if (interval.equals("#")) {
			p = Pattern.compile(interval + "[^#^＃]*#");
		} else {
			p = Pattern.compile(interval + "[^,^.^ ^;^:^@^#^,^。^；^：^＠^＃^<]*");
		}
		Matcher m = p.matcher(s1);
		while (m.find()) {
			result.add(String.valueOf(m.start()));
			result.add(String.valueOf(m.end()));
		}

		return result;

	}

	public static String clearImageUrls(String status, String urls) {

		if (status == null || status.length() == 0 || urls == null
				|| urls.length() == 0) {
			return status;
		}
		String[] imageUrls = urls.split(";");
		if (imageUrls.length > 0) {
			for (String imageUrl : imageUrls) {
				if (imageUrl.contains("show/mini")) {
					imageUrl = imageUrl.replace("show/mini/", "");
				}
				status = status.replace(imageUrl, "");
			}

		}
		// clear attach file url
		if (status.contains("/files/")) {
			ArrayList<String> urlDataList = new ArrayList<String>();

			Pattern p = Pattern.compile("http://[^ ^,^!^;^`^~^\n^，^！^；]*");
			Matcher m = p.matcher(status);
			while (m.find()) {
				if (!urlDataList.contains(m.group())) {
					urlDataList.add(m.group());
				}
			}
			for (String urlData : urlDataList) {
				status = status.replace(urlData, "");
			}
		}
		return status;

	}

	public static boolean isShowSelectReplayUserDialog(String status) {

		ArrayList<String> replyNameList = new ArrayList<String>();
		Pattern p = Pattern
				.compile("@[^\n^,^.^ ^　^:^@^#^，^。^；^：^＠^＃^[\u2E80-\u9FFF]+$^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5]*");
		Matcher m = p.matcher(status);
		while (m.find()) {
			if (!replyNameList.contains(m.group()) && m.group().length() > 1) {
				replyNameList.add(m.group());
			}
		}
		if (replyNameList.size() > 2) {
			return true;
		}
		return false;

	}

	public static String[] getReplyUserScreenName(String status) {

		ArrayList<String> replyNameList = new ArrayList<String>();
		Pattern p = Pattern
				.compile("@[^\n^,^.^ ^　^:^@^#^，^。^；^：^＠^＃^[\u2E80-\u9FFF]+$^\uFE30-\uFFA0^\uFF00-\uFFFF^\u4E00-\u9FA5]*");
		Matcher m = p.matcher(status);
		while (m.find()) {
			if (!replyNameList.contains(m.group()) && m.group().length() > 1) {
				replyNameList.add(m.group());
			}
		}
		String[] result = new String[replyNameList.size()];
		for (int i = 0; i < replyNameList.size(); i++) {
			result[i] = replyNameList.get(i);
		}
		return result;
	}

	public static ArrayList<String> getEmotionsIndexFlag(String status,
			String service) {
		ArrayList<String> result = new ArrayList<String>();
		String parse = null;
		int start = 0;
		int end = 0;
		for (EmotionInfo emotion : SendMessageActivity.emotionList) {
			if (service.equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)
					|| service.equals(IGeneral.SERVICE_NAME_SINA)
					|| service.equals(IGeneral.SERVICE_NAME_TENCENT)
					|| service.equals(IGeneral.SERVICE_NAME_SOHU)
					|| service.equals(IGeneral.SERVICE_NAME_RENREN)) {
				parse = emotion.getPhrase();
				if (parse != null && status.contains(parse)) {

					int size = 0;
					while (size < status.length()) {
						if (size == -1) {
							break;
						} else {
							start = status.indexOf(parse, size);// 使用返回从指定位置开始第一次搜索到指定字符串的索引的方法
							// 再从第一次搜索到的地方再向后搜索
							size = size + parse.length();// 找到的是出现该字符串的首位置，所以还得加上要搜索的字符串的长度
							end = start + parse.length();
							if (start == -1) {
								break;
							} else {
								result.add(String.valueOf(start));
								result.add(String.valueOf(end));
							}
						}
					}
				}
			}
		}
		return result;
	}
}