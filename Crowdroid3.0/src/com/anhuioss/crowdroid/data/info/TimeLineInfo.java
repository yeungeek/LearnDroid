package com.anhuioss.crowdroid.data.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.util.TagAnalysis;

//-----------------------------------------------------------------------------------
/**
 * This class is use for store the message from the xml files.<br>
 * You can get the value using getXXX() method.<br>
 * You can store the value using setXXX(String value) method.
 */
// -----------------------------------------------------------------------------------
public class TimeLineInfo extends BasicInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 201202291324L;

	private UserInfo retweetUserInfo = null;

	public static final int TYPE_SIZE = 0;

	public static final int TYPE_DATA_IMAGE = 1;

	public static final int TYPE_DATA_STATUS = 2;

	public static final int TYPE_DATA_RETWEET_STATUS = 3;

	public static final int TYPE_DATA_IMAGE_URLS = 4;

	public static final int TYPE_DATA_IMAGE_URL_FOR_STATUS = 5;

	public static final int TYPE_DATA_IMAGE_URL_FOR_RETWEET = 6;

	private boolean isRetweeted;

	private String statusId = "";

	private int importantLevel = 0;
	
	private String moodNum = "";
	
	public void setmoodNum(String num) {
		moodNum = num;
	}

	public String getmoodNum() {
		return moodNum;
	}
	
	public void setImportantLevel(int level) {
		importantLevel = level;
	}

	public int getImportantLevel() {
		return importantLevel;
	}

	public static String testStr = null;

	public TimeLineInfo() {

	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setRetweetUserInfo(UserInfo retweetUserInfo) {
		this.retweetUserInfo = retweetUserInfo;
	}

	public UserInfo getRetweetUserInfo() {
		return retweetUserInfo;
	}

	public void setRetweeted(boolean whether) {
		this.isRetweeted = whether;
	}

	public boolean isRetweeted() {
		return isRetweeted;
	}

	public String getImageInformationForWebView(Context context, int type) {

		float scale = ((CrowdroidApplication) context.getApplicationContext())
				.getScaleDensity();

		StringBuilder result = new StringBuilder();
		ArrayList<String> urlDataList = new ArrayList<String>();
		ArrayList<String> imageUrlList = new ArrayList<String>();
		ArrayList<String> thumnailUrlList = new ArrayList<String>();
		String status = getStatus();
		String retweetStatus = "";
		if (isRetweeted()) {
			retweetStatus = "<a onclick='jumpToProifle(\""
					+ getUserInfo().getRetweetedScreenName() + "\");' href=''>"
					+ "@" + getUserInfo().getRetweetedScreenName()
					+ "</a>:<br>" + getRetweetedStatus();
		}

		Pattern p = Pattern.compile("http://[^ ^,^!^;^`^~^\n^，^！^；]*");
		Matcher m;
		if (type == TYPE_DATA_IMAGE_URL_FOR_STATUS) {
			m = p.matcher(status);
		} else if (type == TYPE_DATA_IMAGE_URL_FOR_RETWEET) {
			m = p.matcher(retweetStatus);
		} else {
			m = p.matcher(retweetStatus + " " + status);
		}
		// if(type == TYPE_DATA_RETWEET_STATUS) {
		// m = p.matcher(retweetStatus);
		// }else {
		// m = p.matcher(status);
		// }
		while (m.find()) {
			if (!urlDataList.contains(m.group())) {
				urlDataList.add(m.group());
			}
		}

		for (String urlData : urlDataList) {
			// if service = twitpic
			// (format:http://twitpic.com/show/<size>/<image-id>0)
			if (urlData.contains("twitpic.com")) {
				imageUrlList.add(urlData);
				String id = urlData.substring(urlData.lastIndexOf("/"),
						urlData.length());
				String thumnailUrl = "http://twitpic.com/show/mini" + id;
				thumnailUrlList.add(thumnailUrl);
			} else if (urlData.contains("pic.twitter.com")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			} else if (urlData.contains("yfrog.com")) {
				imageUrlList.add(urlData);
				String thumnailUrl = urlData + ":small";
				thumnailUrlList.add(thumnailUrl);
			} else if (urlData.contains("ow.ly/i")) {
				// At this time, it only supports jpg images.
				// If it supports more types in the future, we will change.
				imageUrlList.add(urlData);
				String id = urlData.substring(urlData.lastIndexOf("/"),
						urlData.length());
				String thumnailUrl = "http://static.ow.ly/photos/thumb" + id
						+ ".jpg";
				thumnailUrlList.add(thumnailUrl);

			} else if (urlData.contains("img.ly")) {
				imageUrlList.add(urlData);
				String id = urlData.substring(urlData.lastIndexOf("/"),
						urlData.length());
				String thumnailUrl = "http://img.ly/show/mini" + id;
				thumnailUrlList.add(thumnailUrl);
			} else if (urlData.contains("pk.gd")) {
				imageUrlList.add(urlData);
				String id = urlData.substring(urlData.lastIndexOf("/"),
						urlData.length());
				String subId = id.substring(1);
				String thumnailUrl = "http://img.pikchur.com/pic_" + subId
						+ "_l.jpg";
				thumnailUrlList.add(thumnailUrl);
			} else if (urlData.contains("instagr.am/p/")) {
				imageUrlList.add(urlData);
				String thumnailUrl = urlData + "media/?size=l";
				thumnailUrlList.add(thumnailUrl);
			} else if(urlData.contains("twitgoo.com")){
				imageUrlList.add(urlData);
				String thumnailUrl = urlData + "/img";
				thumnailUrlList.add(thumnailUrl);
			} else if (urlData.contains("p.twipple.jp")) {
				imageUrlList.add(urlData);
				String id = urlData.substring(urlData.lastIndexOf("/"),
						urlData.length());
				char[] charId = id.toCharArray();
				StringBuffer strBufId = new StringBuffer();
				for (int i = 1; i < id.length(); i++) {
					strBufId.append("/" + charId[i]);
				}
				String thumnaiUrl;
				if (urlData.contains(".jpg")) {
					thumnaiUrl = urlData;
				} else {
					thumnaiUrl = "http://p.twipple.jp/data"
							+ strBufId.toString() + ".jpg";
				}
				thumnailUrlList.add(thumnaiUrl);
			} else if (urlData.contains("app.qpic.cn")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			}  else if (urlData.contains("t3.qpic.cn")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			} else if (urlData.contains("fl5.me")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			}// urlData.contains("crowdroid_business")
			else if (urlData.contains(".jpeg") || urlData.contains(".jpg")
					|| urlData.contains(".png") || urlData.contains(".bmp")
					|| urlData.contains(".gif")
					|| urlData.contains("crowdroid_business")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			}// 附件显示图片
			else if (urlData.contains("/files/")) {
				imageUrlList.add(urlData);
				thumnailUrlList.add(urlData);
			}
		}

		// style=\"float:left; height='160px'; width='160px'; border='1px';
		// margin='3px';\"

		// result.append("<center>");
		// result.append("<div style='height:"
		// + (imageUrlList.size() > 0 ? String.valueOf(176 * scale) : "0") +
		// "px; width:" + 176 * scale
		// * imageUrlList.size() + "px;'>");
		// for (int i = 0; i < imageUrlList.size(); i++) {
		// result.append("<img style=\"float:left; max-height:" + (160 * scale)
		// + "px; max-width:" + (160 * scale) +
		// "px; border:1px; margin:3px;\" onclick=\"setUrl('" +
		// imageUrlList.get(i)
		// + "')\" src='" + thumnailUrlList.get(i) + "'>");
		// }
		// result.append("</div>");
		// result.append("</center>");

		result.append("<center>");
		for (int i = 0; i < imageUrlList.size(); i++) {
			result.append("<img style=\"max-height:" 
					+ (300*scale) +"px; max-width:"
					+ (160*scale) +"px;\" onclick=\"setUrl('" + imageUrlList.get(i)
					+ "')\" src='" + thumnailUrlList.get(i) + "'>");
			if (i != imageUrlList.size() - 1) {
				result.append("<hr>");
			}
		}
		result.append("</center>");

		String phrase = null;
		for (EmotionInfo emotion : SendMessageActivity.emotionList) {

			phrase = emotion.getPhrase();
			if (phrase != null && status.contains(phrase)) {
				status = status.replace(
						phrase,
						"<img width=\"22\" height=\"22\" src='"
								+ emotion.getUrl() + "'>");
			}
			if (phrase != null && retweetStatus.contains(phrase)) {
				retweetStatus = retweetStatus.replace(
						phrase,
						"<img width=\"22\" height=\"22\" src='"
								+ emotion.getUrl() + "'>");
			}

		}

		for (String imageUrl : urlDataList) {
			if (type == TYPE_DATA_STATUS) {
				status = status.replace(imageUrl, "<a href='" + imageUrl
						+ "'/>" + imageUrl + " </a>");
			} else {
				retweetStatus = retweetStatus.replace(imageUrl, "<a href='"
						+ imageUrl + "'/>" + imageUrl + " </a>");
				// retweetStatus = retweetStatus.replace(imageUrl, "<a href='" +
				// imageUrl+ "' target='"+"_blank'"+">"+imageUrl+" </a>");
			}
		}

		if (type == TYPE_SIZE) {
			return String.valueOf(imageUrlList.size());
		} else if (type == TYPE_DATA_IMAGE) {
			if (imageUrlList.size() == 0) {
				return "";
			} else {
				return result.toString();
			}
		} else if (type == TYPE_DATA_STATUS) {
			return setStatusText(status);
		} else if (type == TYPE_DATA_RETWEET_STATUS) {
			return setStatusText(retweetStatus);
		} else if (type == TYPE_DATA_IMAGE_URLS
				|| type == TYPE_DATA_IMAGE_URL_FOR_STATUS
				|| type == TYPE_DATA_IMAGE_URL_FOR_RETWEET) {
			if (thumnailUrlList.size() == 0) {
				return "";
			}
			StringBuffer imageUrlStringBuffer = new StringBuffer();
			for (String url : thumnailUrlList) {
				imageUrlStringBuffer.append(url).append(";");
			}
			String imageUrlString = imageUrlStringBuffer.toString();
			imageUrlString = imageUrlString.substring(0,
					imageUrlString.length() - 1);
			return imageUrlString;
		} else {
			return null;
		}

	}

	// -----------------------------------------------------------------------------
	/**
	 * Set Status Text
	 */
	// -----------------------------------------------------------------------------
	private String setStatusText(String text) {

		String result = text + " ";
		// Extract Hash
		result = result.replaceAll("\r", "");
		if (result == null) {
			result = "";
		}

		// Hash Tag
		ArrayList<String> oldStringList = new ArrayList<String>();
		ArrayList<String> newStringList = new ArrayList<String>();

		ArrayList<String> indexHashFlag = TagAnalysis.getIndexForSina(result,
				"#");
		int number = indexHashFlag.size();

		for (int i = 0; i < number / 2; i++) {

			int start = Integer.valueOf(indexHashFlag.get(i * 2));
			int end = Integer.valueOf(indexHashFlag.get(i * 2 + 1));

			String oldString = result.substring(start, end);
			String newString = "<font color='blue'><u><a onclick='hashTag(\""
					+ oldString + "\")'>" + oldString + "</a></u></font>";

			if (!oldStringList.contains(oldString)) {
				oldStringList.add(oldString);
				newStringList.add(newString);
			}

		}

		// At Tag
		ArrayList<String> indexAtFlag = TagAnalysis
				.getIndexForSina(result, "@");
		int numverAtFlag = indexAtFlag.size();

		for (int i = 0; i < numverAtFlag / 2; i++) {

			int start = Integer.valueOf(indexAtFlag.get(i * 2));
			int end = Integer.valueOf(indexAtFlag.get(i * 2 + 1));

			String oldString = result.substring(start, end);
			String newString = "<font color='blue'><u><a onclick='userName(\""
					+ oldString + "\")'>" + oldString + "</a></u></font>";

			if (!oldStringList.contains(oldString)) {
				oldStringList.add(oldString);
				newStringList.add(newString);
			}

		}

		for (int i = 0; i < oldStringList.size(); i++) {
			result = result.replace(oldStringList.get(i), newStringList.get(i));
		}

		return result;

	}

}
