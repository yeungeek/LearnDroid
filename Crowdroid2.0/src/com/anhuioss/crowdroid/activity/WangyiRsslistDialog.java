package com.anhuioss.crowdroid.activity;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.sax.StartElementListener;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class WangyiRsslistDialog extends AlertDialog {
	private ArrayAdapter<Spanned> RssAdapter;

	Spanned s1 = Html.fromHtml("<fort color='white'>微博精选</font>");
	Spanned s2 = Html.fromHtml("<fort color='white'>网易特色</font>");
	Spanned s3 = Html.fromHtml("<fort color='white'>娱乐生活</font>");
	Spanned s4 = Html.fromHtml("<fort color='white'>社会人文</font>");
	Spanned s5 = Html.fromHtml("<fort color='white'>商业经济</font>");
	Spanned s6 = Html.fromHtml("<fort color='white'>科技数码</font>");

	// private Spanned[] Rsslist={s1,s2,s3,s4,s5,s6};
	private Spanned[] Rsslist = { s1, s2 };
	private String[] Rssid = { "1", "2", "3", "4", "10", "11", "12", "13",
			"14", "19", "20", "21", "16", "17", "18", "7", "8" };
	private String[] rssid1 = { "1", "2" };
	private String[] rssid2 = { "3", "4" };
	private String[] rssid3 = { "10", "11", "12", "13", "14" };
	private String[] rssid4 = { "19", "20", "21" };
	private String[] rssid5 = { "16", "17", "18" };
	private String[] rssid6 = { "7", "8" };

	private CharSequence[] choose1 = { "每日精选", "每周精选" };
	private CharSequence[] choose2 = { "网易有态度", "网易新闻" };
	private CharSequence[] choose3 = { "影视", "音乐", "原创段子", "情感", "摄影" };
	private CharSequence[] choose4 = { "文化", "历史", "文学" };
	private CharSequence[] choose5 = { "经济", "管理", "投资理财" };
	private CharSequence[] choose6 = { "IT互联网", "数码" };

	private Context mcontext;

	private ListView listview;

	private StatusData statusData;

	private String show;

	private String title;

	private String rss_id;

	private boolean isRunning = false;

	private HandleProgressDialog progress;
	private Bundle bundle = new Bundle();

	public WangyiRsslistDialog(Context context) {
		super(context);

		mcontext = context;
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.dialog_trends, null);

		setView(view);
		setTitle(R.string.wangyi_rss);

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();

		listview = (ListView) view.findViewById(R.id.dialog_trends_list);
		view.findViewById(R.id.dialog_trends_type).setVisibility(View.GONE);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {
				if (!RssAdapter.isEmpty()) {
					switch (position) {
					case 0: {
						openchoose(choose1, s1, rssid1);
						break;
					}
					case 1: {
						openchoose(choose2, s2, rssid2);
						break;
					}
					case 2: {
						openchoose(choose3, s3, rssid3);
						break;
					}
					case 3: {
						openchoose(choose4, s4, rssid4);
						break;
					}
					case 4: {
						openchoose(choose5, s5, rssid5);
						break;
					}
					case 5: {
						openchoose(choose6, s6, rssid6);
						break;
					}
					default:
						break;
					}
				}
			}

		});
		setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});
		RssAdapter = new ArrayAdapter<Spanned>(getContext(),
				android.R.layout.simple_list_item_1, Rsslist);

		listview.setAdapter(RssAdapter);
	}

	private void openchoose(final CharSequence[] choose, Spanned s,
			final String[] rssid) {

		title = (String) choose[0];
		rss_id = (String) rssid[0];
		new AlertDialog.Builder(mcontext).setTitle(s)
				.setSingleChoiceItems(choose, 0, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (which) {
						case 0: {
							// bundle.putString("title", (String)choose[0]);
							// bundle.putString("rss_id", rssid[0]);
							// Log.v("bundle",(String)bundle.get("title"));
							break;
						}
						case 1: {
							title = (String) choose[1];
							rss_id = (String) rssid[1];
							// bundle.putString("title", (String)choose[1]);
							// bundle.putString("rss_id", rssid[1]);
							break;
						}
						case 2: {
							title = (String) choose[2];
							rss_id = (String) rssid[2];
							// bundle.putString("title", (String)choose[2]);
							// bundle.putString("rss_id", rssid[2]);
							break;
						}
						case 3: {
							title = (String) choose[3];
							rss_id = (String) rssid[3];
							// bundle.putString("title", (String)choose[3]);
							// bundle.putString("rss_id", rssid[3]);
							break;
						}
						default:
							break;
						}
					}
				}).setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						// TODO Auto-generated method stub

						showProgressDialog();
						new Thread() {
							public void run() {
								try {
									sleep(2000);
								} catch (Exception e) {
									e.printStackTrace();
								} finally {
									closeProgressDialog();
								}
							}
						}.start();
						bundle.putString("title", title);
						bundle.putString("rss_id", rss_id);
						Intent intent = new Intent();
						intent.setClass(getContext(),
								WangyiRssTimelineActivity.class);
						intent.putExtras(bundle);
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(intent);

					}
				}).show();

	}

	private void showProgressDialog() {
		// if (!isRunning) {
		// return;
		// }
		if (progress == null) {
			progress = new HandleProgressDialog(getContext());
		}
		progress.show();
	}

	private void closeProgressDialog() {
		// if (!isRunning) {
		// return;
		// }
		if (progress != null) {
			progress.dismiss();
		}
	}

	public Bundle getbundle() {
		return bundle;
	}
}
