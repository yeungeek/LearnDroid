package com.anhuioss.crowdroid.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Toast;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.CommentActivity;
import com.anhuioss.crowdroid.activity.LBSUpdateMessageActivity;
import com.anhuioss.crowdroid.activity.RetweetMessageActivity;
import com.anhuioss.crowdroid.activity.SendDMActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.sns.operations.PreviewAlbumPhotosActivity;
import com.anhuioss.crowdroid.sns.operations.UpdateBlogActivity;

class MyView {
	WebView webView;
}

public class GridViewAdapter extends BaseAdapter {
	private List<HashMap<String, String>> list;
	private Context mContext;
	private AlertDialog alertDialog;
	private String action;
	private ArrayList<TimeLineInfo> timelineInfoList;
	private int commType = 0;

	public GridViewAdapter(Context context, AlertDialog alertDialog,
			List<HashMap<String, String>> list) {
		// TODO Auto-generated constructor stub
		this.list = list;
		mContext = context;
		this.alertDialog = alertDialog;
	}

	public GridViewAdapter(Context context, AlertDialog alertDialog,
			List<HashMap<String, String>> list, String action) {
		// TODO Auto-generated constructor stub
		this.list = list;
		mContext = context;
		this.alertDialog = alertDialog;
		this.action = action;
	}

	public GridViewAdapter(Context context,
			ArrayList<TimeLineInfo> timelineInfoList,
			ArrayList<HashMap<String, String>> list, int commType) {
		mContext = context;
		this.list = list;
		this.timelineInfoList = timelineInfoList;
		this.commType = commType;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		MyView view;
		if (convertView == null) {
			View v = LayoutInflater.from(mContext).inflate(
					R.layout.gridview_emotion_item, null);
			view = new MyView();
			view.webView = (WebView) v
					.findViewById(R.id.gridView_tweet_emotion_item);
			v.setTag(view);
			convertView = v;
		} else {
			view = (MyView) convertView.getTag();
		}
		String image_path = list.get(position).get("itemImage");

		view.webView.setFocusable(false);
		view.webView.setClickable(false);
		view.webView.setBackgroundColor(Color.TRANSPARENT);
		view.webView.setVisibility(View.VISIBLE);
		view.webView.setVerticalScrollBarEnabled(false);
		view.webView.setHorizontalScrollBarEnabled(false);
		view.webView.clearView();
		//renren 浏览相册
		if (commType == CommHandler.TYPE_GET_ALBUM_PHOTOS) {
			StringBuffer htmlData = new StringBuffer();
			htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
			htmlData.append("<center>");
			htmlData.append(
					"<img style='max-width:85px;max-height:85px'  src='")
					.append(image_path.subSequence(0, image_path.indexOf(";")))
					.append("' />");
			htmlData.append("<center></body></html>");
			view.webView.loadDataWithBaseURL("about:blank",
					htmlData.toString(), "text/html", "utf-8", "");
		} else {
			//emotions
			StringBuffer htmlData = new StringBuffer();
			htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
			htmlData.append("<center>");
			htmlData.append(
					"<img style='max-width:22px;max-height:22px'  src='")
					.append(image_path).append("' />");
			htmlData.append("<center></body></html>");
			view.webView.loadDataWithBaseURL("about:blank",
					htmlData.toString(), "text/html", "utf-8", "");
		}

		view.webView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int actionFalg = 0;
				if (Integer.valueOf(android.os.Build.VERSION.SDK) > 11) {
					actionFalg = MotionEvent.ACTION_UP;
				} else if (Integer.valueOf(android.os.Build.VERSION.SDK) < 11) {
					actionFalg = MotionEvent.ACTION_DOWN;
				}
				if (event.getAction() == actionFalg) {
					Intent intent = null;
					if (commType == CommHandler.TYPE_GET_ALBUM_PHOTOS) {
						Toast.makeText(
								mContext,
								timelineInfoList
										.get(position)
										.getStatus()
										.substring(
												timelineInfoList.get(position)
														.getStatus()
														.indexOf(";") + 1),
								Toast.LENGTH_SHORT).show();
						
						intent = new Intent(mContext, PreviewAlbumPhotosActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						Bundle bundle = new Bundle();
						bundle.putString("currentUrl", timelineInfoList
										.get(position)
										.getStatus()
										.substring(
												timelineInfoList.get(position)
														.getStatus()
														.indexOf(";") + 1));
						bundle.putInt("position", position);
						bundle.putSerializable("timelineInfoList", timelineInfoList);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
						
						
					} else {
						// send message
						if (mContext instanceof SendMessageActivity) {
							intent = new Intent(mContext,
									SendMessageActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Bundle bundle = new Bundle();
							bundle.putString("emotionName", list.get(position)
									.get("itemText"));
							bundle.putString("emotionUrl", list.get(position)
									.get("itemImage"));
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
						// update blog
						else if (mContext instanceof UpdateBlogActivity) {
							intent = new Intent(mContext,
									UpdateBlogActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							Bundle bundle = new Bundle();
							bundle.putString("emotionName", list.get(position)
									.get("itemText"));
							bundle.putString("emotionUrl", list.get(position)
									.get("itemImage"));
							bundle.putString("action", "blog");
							intent.putExtras(bundle);
							mContext.startActivity(intent);
						}
						// update lbs message
						else if (mContext instanceof LBSUpdateMessageActivity) {
							SharedPreferences sharedPreferences = mContext
									.getSharedPreferences(
											"SHARE_INIT_UPDATE_LBS_STATUS", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("SHARE_INIT_UPDATE_LBS_STATUS", list
									.get(position).get("itemText"));
							editor.putBoolean(
									"WHEATHER_FROM_LBS_OUTSIDE_INSERT",
									true);
							editor.commit();
							alertDialog.dismiss();
						}
						// retweet message
						else if (mContext instanceof RetweetMessageActivity) {
							SharedPreferences sharedPreferences = mContext
									.getSharedPreferences(
											"SHARE_INIT_RETWEET_STATUS", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("SHARE_INIT_RETWEET_STATUS", list
									.get(position).get("itemText"));
							editor.putBoolean(
									"WHEATHER_FROM_RETWEET_OUTSIDE_INSERT",
									true);
							editor.commit();
							alertDialog.dismiss();
						}
						// comment message
						else if (mContext instanceof CommentActivity) {
							SharedPreferences sharedPreferences = mContext
									.getSharedPreferences(
											"SHARE_INIT_COMMENT_STATUS", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("SHARE_INIT_COMMENT_STATUS", list
									.get(position).get("itemText"));
							editor.putBoolean(
									"WHEATHER_FROM_COMMENT_OUTSIDE_INSERT",
									true);
							editor.commit();
							alertDialog.dismiss();
						}
						// direct message
						else if (mContext instanceof SendDMActivity) {
							SharedPreferences sharedPreferences = mContext
									.getSharedPreferences(
											"SHARE_INIT_DM_STATUS", 0);
							SharedPreferences.Editor editor = sharedPreferences
									.edit();
							editor.putString("SHARE_INIT_DM_STATUS",
									list.get(position).get("itemText"));
							editor.putBoolean(
									"WHEATHER_FROM_DM_OUTSIDE_INSERT", true);
							editor.commit();
							alertDialog.dismiss();
						}
					}
				}

				return false;
			}
		});

		return convertView;
	}

	public synchronized Bitmap getBitMap(Context c, String url) {
		URL myFileUrl = null;
		Bitmap bitmap = null;
		try {
			myFileUrl = new URL(url);
		} catch (MalformedURLException e) {
			// 当网络连接异常后,给个默认图片
			return bitmap;
		}
		try {
			// 打开网络连接
			HttpURLConnection conn = (HttpURLConnection) myFileUrl
					.openConnection();
			conn.setDoInput(true);
			conn.connect();
			InputStream is = conn.getInputStream(); // 把得到的内容转换成流
			int length = (int) conn.getContentLength(); // 获取文件的长度
			if (length != -1) {
				byte[] imgData = new byte[length];
				byte[] temp = new byte[512];
				int readLen = 0;
				int destPos = 0;
				while ((readLen = is.read(temp)) > 0) {
					System.arraycopy(temp, 0, imgData, destPos, readLen);
					destPos += readLen;
				}

				bitmap = BitmapFactory.decodeByteArray(imgData, 0,
						imgData.length);
			}

		} catch (IOException e) {

			return bitmap;
		}

		return bitmap;
	}

}
