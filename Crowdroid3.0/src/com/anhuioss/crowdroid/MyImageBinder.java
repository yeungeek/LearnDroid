package com.anhuioss.crowdroid;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SimpleAdapter.ViewBinder;

import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;

public class MyImageBinder implements ViewBinder {
	int color;
	int size;
	ArrayList<String> tagList;
	private boolean isRetweeted = false;
	private String service;

	private Context mContext;

	private String message = "";

	// private static String html =
	// "<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>"
	// + "<html xmlns='http://www.w3.org/1999/xhtml'>"
	// + "<head>"
	// + "<meta http-equiv='Content-Type' content='text/html; charset=utf-8' />"
	// + "<script src='javascript/process.js' type='text/javascript'>"
	// + "</script>"
	// + "<style>"
	// + "body {"
	// + "margin-left: 0px;"
	// + "margin-right: 0px;"
	// + "}"
	// + "</style>"
	// + "<title>Activity</title>"
	// + "</head>"
	// + "<body bgcolor='#f2f2f2'>";
	//
	/**
	 * this method is public handler the color value
	 */
	//

	@SuppressWarnings("static-access")
	public MyImageBinder(String colorString, String sizeString,
			ArrayList<String> tagList, Context context) {
		this.tagList = tagList;
		Integer it = new Integer(0);
		color = it.parseInt(colorString);
		size = Integer.valueOf(sizeString);
		mContext = context;
	}

	// ---------------------------------------------------------------------------------------
	/**
	 * Customized for Setting Bitmap to Image View.
	 */
	// ---------------------------------------------------------------------------------------
	@Override
	public boolean setViewValue(View view, Object data,
			String textRepresentation) {

		TextView tv;

		if ((view instanceof ImageView) & (data instanceof Bitmap)) {

			ImageView iv = (ImageView) view;

			Bitmap bm = (Bitmap) data;
			iv.setImageBitmap(bm);
			return true;
		} else if (view.getId() == R.id.text_retweet_count
				|| view.getId() == R.id.text_comment_count) {
			TextView v = (TextView) view;
			if (String.valueOf(color).contains("-")) {
				v.setTextColor(color);
			} else {
				v.setTextColor(mContext.getResources().getColor(color));
			}
			v.setTextSize(size);
			LinearLayout parent = (LinearLayout) view.getParent();
			if (data != null && !data.equals("")) {
				if (data.equals(mContext.getString(R.string.retweet_count)
						+ "(" + ")")
						|| data.equals(mContext
								.getString(R.string.comment_count) + "(" + ")")) {
					parent.findViewById(R.id.text_retweet_count).setVisibility(
							View.GONE);
					parent.findViewById(R.id.text_comment_count).setVisibility(
							View.GONE);
					parent.setVisibility(View.GONE);
				} else {
					parent.findViewById(R.id.text_retweet_count).setVisibility(
							View.VISIBLE);
					parent.findViewById(R.id.text_comment_count).setVisibility(
							View.VISIBLE);
				}

			} else {
				parent.setVisibility(View.GONE);
				// parent.findViewById(R.id.text_retweet_count).setVisibility(View.GONE);
				// parent.findViewById(R.id.text_comment_count).setVisibility(View.GONE);
			}
		} else if (view.getId() == R.id.text_get_more_tweets) {
			LinearLayout parent = (LinearLayout) view.getParent();
			LinearLayout itemLayout = (LinearLayout) parent
					.findViewById(R.id.linear_layout_time_line_list_item);
			LinearLayout retComLayout = (LinearLayout) parent
					.findViewById(R.id.linear_time_retweet_comment_layout);
			if (data == mContext.getString(R.string.get_more_tweets)) {
				itemLayout.setVisibility(View.GONE);
				retComLayout.setVisibility(View.GONE);
				view.setVisibility(View.VISIBLE);
			} else {
				itemLayout.setVisibility(View.VISIBLE);
				retComLayout.setVisibility(View.VISIBLE);
				view.setVisibility(View.GONE);
			}
		} else if (view.getId() == R.id.important_level_view) {
			ImageView iv = (ImageView) view;
			if (data != null) {
				int resourceId = (Integer) data;
				if (resourceId != 0) {
					iv.setVisibility(View.VISIBLE);
					iv.setBackgroundResource(resourceId);
				}
			} else {
				iv.setVisibility(View.GONE);
			}
			return true;
		} else if (view.getId() == R.id.retweeted_screen_name_status) {
			if (data == null) {
				LinearLayout parent = (LinearLayout) view.getParent();
				parent.setVisibility(View.GONE);
				LinearLayout grandParent = (LinearLayout) parent.getParent();
				grandParent.findViewById(R.id.retweet_top).setVisibility(
						View.GONE);
				grandParent.findViewById(R.id.retweet_bottom).setVisibility(
						View.GONE);
				// view.setVisibility(View.GONE);
			} else {
				LinearLayout parent = (LinearLayout) view.getParent();
				parent.setVisibility(View.VISIBLE);
				LinearLayout grandParent = (LinearLayout) parent.getParent();
				grandParent.findViewById(R.id.retweet_top).setVisibility(
						View.VISIBLE);
				grandParent.findViewById(R.id.retweet_bottom).setVisibility(
						View.VISIBLE);
				// view.setVisibility(View.VISIBLE);
			}
		} else if ((view instanceof TextView) & (data instanceof String)) {

			tv = (TextView) view;
			if (String.valueOf(color).contains("-")) {
				tv.setTextColor(color);
			} else {
				tv.setTextColor(mContext.getResources().getColor(color));
			}
			tv.setTextSize(size);
			String ms = (String) data;

			// ArrayList<String> indexHashFlag = TagAnalysis.getIndex(ms, "#");
			// int number = indexHashFlag.size();
			// for (int j = 0; j < number / 2; j++) {
			// String tag = ms.substring(Integer.valueOf(indexHashFlag.get(j *
			// 2)),
			// Integer.valueOf(indexHashFlag.get(j * 2 + 1)));

			if (tagList != null)
				for (String tag : tagList) {
					Matcher m = Pattern.compile(tag, Pattern.CASE_INSENSITIVE)
							.matcher(ms);
					String replacetext = "";
					for (int i = 0; i < ms.length(); i++) {
						String tmp = "#";
						replacetext = replacetext + tmp;
					}
					ms = m.replaceAll(replacetext);
				}
			// }
			tv.setText(ms);

			if (view.getId() == R.id.screen_name) {
				TextView v = (TextView) view;
				String str = String.valueOf(data);
				int count = 0;
				String alertStr = mContext
						.getString(R.string.twitter_alert_retweet_of_me);

				for (int i = 0; i < str.length(); i++) {
					Character c = str.charAt(i);
					if ("@".equals(String.valueOf(str.charAt(i)))) {
						count++;
					}
				}
				if (str.contains("@") && count == 1) {
					SpannableString spanString = new SpannableString(
							String.valueOf(data));
					spanString.setSpan(new RelativeSizeSpan(1.0f), 0,
							str.indexOf("@") - 1,
							Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					if (str.contains("[QT")) {
						spanString.setSpan(new RelativeSizeSpan(0.9f),
								str.indexOf("@"), str.indexOf("[QT") - 1,
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
						spanString.setSpan(new RelativeSizeSpan(0.9f),
								str.indexOf("[QT"), str.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					} else {
						spanString.setSpan(new RelativeSizeSpan(0.9f),
								str.indexOf("@"), str.length(),
								Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					}
					v.setText(spanString);
					// v.setMovementMethod(LinkMovementMethod.getInstance());
					// ((Object) view).setCharSequence(R.id.screen_name,
					// "setText", spanString);
					// ComponentName com = new ComponentName("com.jftt.widget",
					// "com.jftt.widget.MyWidgetProvider");
					// appWidgetManager.updateAppWidget(com, view);
				} else if (str.contains(alertStr)) {
					SpannableString spanString = new SpannableString(
							String.valueOf(data));
					spanString
							.setSpan(
									new RelativeSizeSpan(1.0f),
									0,
									str.indexOf(mContext
											.getString(R.string.twitter_alert_retweet_of_me)) - 1,
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					spanString
							.setSpan(
									new RelativeSizeSpan(0.7f),
									str.indexOf(mContext
											.getString(R.string.twitter_alert_retweet_of_me)),
									str.length(),
									Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
					v.setText(spanString);
				} else {
					v.setText(String.valueOf(data));
				}

			}

			return true;
		} else if (view.getId() == R.id.sina_user_verified) {
			if (data != null && data.equals(true)) {
				CrowdroidApplication crowdroidApplication = (CrowdroidApplication) mContext
						.getApplicationContext();
				if (IGeneral.SERVICE_NAME_TENCENT.equals(crowdroidApplication
						.getStatusData().getCurrentService())) {
					((ImageView) view)
							.setBackgroundResource(R.drawable.tencent_user_verified);
					view.setBackgroundDrawable(mContext.getResources()
							.getDrawable(R.drawable.tencent_user_verified));
				} else if (IGeneral.SERVICE_NAME_SINA
						.equals(crowdroidApplication.getStatusData()
								.getCurrentService())) {
					view.setBackgroundDrawable(mContext.getResources()
							.getDrawable(R.drawable.sina_user_verified));
				} else if (IGeneral.SERVICE_NAME_SOHU
						.equals(crowdroidApplication.getStatusData()
								.getCurrentService())) {
					view.setBackgroundDrawable(mContext.getResources()
							.getDrawable(R.drawable.sohu_user_verified));
				}
				view.setVisibility(View.VISIBLE);
			} else {
				view.setVisibility(View.GONE);
			}
			return true;
		} else if (view.getId() == R.id.web_view_status) {

			WebView web = (WebView) view;
			web.setFocusable(false);
			web.setClickable(false);

			String imageUrlString = (String) data;
			if (imageUrlString == null || "".equals(imageUrlString)) {
				web.setVisibility(View.GONE);
				return true;
			}

			web.setBackgroundColor(Color.TRANSPARENT);
			web.setVisibility(View.VISIBLE);

			web.setVerticalScrollBarEnabled(false);
			web.setHorizontalScrollBarEnabled(false);

			String[] imageUrls = imageUrlString.split(";");
			StringBuffer htmlData = new StringBuffer(
					"<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
			if (imageUrls.length > 0) {
				htmlData.append("<center>");
				for (int i = 0; i < imageUrls.length; i++) {
					String imageUrl = imageUrls[i];
					if (imageUrl.contains("/files/")) {
//						imageUrl = "http://icons.iconarchive.com/icons/deleket/sleek-xp-basic/72/Attach-icon.png";
						imageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
					}
					htmlData.append(
							"<img style='max-height: 100px; max-width:60px; margin-top:4px;' src='")
							.append(imageUrl).append("' />");
					if (i != imageUrls.length - 1) {
						htmlData.append("<br>");
					}
				}
				htmlData.append("<center></body></html>");
			}
			web.loadDataWithBaseURL("", htmlData.toString(), "text/html",
					"utf-8", "");
			return true;

		} else if (view.getId() == R.id.web_view_retweet_status) {

			WebView web = (WebView) view;
			web.setFocusable(false);
			web.setClickable(false);

			String imageUrlString = (String) data;
			if (imageUrlString == null || "".equals(imageUrlString)) {
				web.setVisibility(View.GONE);
				return true;
			}

			web.setBackgroundColor(Color.TRANSPARENT);
			web.setVisibility(View.VISIBLE);

			web.setVerticalScrollBarEnabled(false);
			web.setHorizontalScrollBarEnabled(false);

			String[] imageUrls = imageUrlString.split(";");
			StringBuffer htmlData = new StringBuffer();
			if (imageUrls.length > 0) {
				htmlData.append("<center>");
				for (int i = 0; i < imageUrls.length; i++) {
					String imageUrl = imageUrls[i];
					if (imageUrl.contains("/files/")) {
//						imageUrl = "http://icons.iconarchive.com/icons/deleket/sleek-xp-basic/72/Attach-icon.png";
						imageUrl = "http://www.iconpng.com/png/coquette/attachment3.png";
					}
					htmlData.append(
							"<img style='max-height: 100px; max-width:60px; margin-top:4px;' src='")
							.append(imageUrl).append("' />");
					if (i != imageUrls.length - 1) {
						htmlData.append("<br>");
					}
				}
				htmlData.append("<center>");
			}

			web.loadDataWithBaseURL("", htmlData.toString(), "text/html",
					"utf-8", "");

			return true;

		} else if (view.getId() == R.id.web_status
				|| view.getId() == R.id.web_retweet_status) {

			WebView web = (WebView) view;
			web.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			web.setFocusable(false);
			web.setClickable(false);

			String status = (String) data;
			if (status == null || "".equals(status)) {
				web.setVisibility(View.GONE);
				return true;
			}

			web.setBackgroundColor(Color.TRANSPARENT);
			web.setVisibility(View.VISIBLE);
			web.setVerticalScrollBarEnabled(false);
			web.setHorizontalScrollBarEnabled(false);

			web.clearView();
			web.loadUrl("");
			if (status.contains("mat1.gtimg.com/www/mb/images/tFace")) {
				status = "<img style='max-height: 100px; max-width: 60px;' src='"
						+ status.substring(status.lastIndexOf(";") + 1)
						+ "' />"
						+ status.substring(0, status.lastIndexOf(";") - 1);
			}
			
			if (status != null && status.length() > 0) {
				web.loadDataWithBaseURL("about:blank",
						replaceEmotionStringToUrl(status), "text/html",
						"utf-8", "");
			}
			return true;

		} else if (view.getId() == R.id.user_image
				|| view.getId() == R.id.sina_user_image) {

			final WebView web = (WebView) view;
			web.setFocusable(false);
			web.setClickable(true);
			web.setBackgroundColor(Color.TRANSPARENT);
			web.setVisibility(View.VISIBLE);

			web.setVerticalScrollBarEnabled(false);
			web.setHorizontalScrollBarEnabled(false);

			String status = (String) data;

			// RenRen
			RelativeLayout parent = (RelativeLayout) view.getParent();
			if (data == null || data.equals("")) {
				parent.setVisibility(View.GONE);
			} else {
				parent.setVisibility(View.VISIBLE);
			}
			try {
				int resourceId = Integer.valueOf(status);
				web.clearView();
				web.setBackgroundResource(resourceId);
				return true;
			} catch (NumberFormatException e) {

			}

			web.setBackgroundResource(R.drawable.default_user_image);
			web.setWebViewClient(new WebViewClient() {
				@Override
				public void onPageFinished(WebView view, String url) {
					web.setBackgroundResource(0);
					super.onPageFinished(view, url);
				}

			});

			status = "<img style='max-height: 100px; max-width: 60px;' src='"
					+ status + "' />";

			if (status != null && status.length() > 0) {
				web.loadDataWithBaseURL("about:blank", status, "text/html",
						"utf-8", "");
			}
			return true;

		}

		return false;

	}

	public void setRetweeted(boolean isRetweeted) {
		this.isRetweeted = isRetweeted;
	}

	public boolean isRetweeted() {
		return isRetweeted;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getService() {
		return service;
	}

	private String replaceEmotionStringToUrl(String status) {

		String phrase = null;
		for (EmotionInfo emotion : SendMessageActivity.emotionList) {

			phrase = emotion.getPhrase();
			if (phrase != null && status.contains(phrase)) {
				status = status.replace(
						phrase,
						"<img width=\"22\" height=\"22\" src='"
								+ emotion.getUrl() + "'>");
			}

		}
		return "<font size='" + size / 4.7 + "' color='" + toHexString(color)
				+ "'>" + status + "</font>";

	}

	private String toHexString(int color) {
		String hexString = "";
		if (String.valueOf(color).contains("-")) {
			hexString = Integer.toHexString(color);
		} else {
			hexString = Integer.toHexString(mContext.getResources().getColor(
					color));
		}
		if (hexString.length() > 2) {
			hexString = hexString.substring(2, hexString.length());
			return "#" + hexString;
		}
		return "#000000";
	}

}
