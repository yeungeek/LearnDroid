package com.anhuioss.crowdroid.dialog;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.TencentFamouslistActivity;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.sns.operations.PublicPageListActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class TencentFamouslistDialog extends AlertDialog {

	private ArrayAdapter<Spanned> famousAdapter;

	Spanned s1 = Html.fromHtml("<font color='white'>娱乐明星</font>");
	Spanned s2 = Html.fromHtml("<font color='white'>体育明星</font>");
	Spanned s3 = Html.fromHtml("<font color='white'>生活时尚</font>");
	Spanned s4 = Html.fromHtml("<font color='white'>财经</font>");
	Spanned s5 = Html.fromHtml("<font color='white'>科技网络</font>");
	Spanned s6 = Html.fromHtml("<font color='white'>文化出版</font>");
	Spanned s7 = Html.fromHtml("<font color='white'>汽车</font>");
	Spanned s8 = Html.fromHtml("<font color='white'>动漫</font>");
	Spanned s9 = Html.fromHtml("<font color='white'>游戏</font>");
	Spanned s10 = Html.fromHtml("<font color='white'>星座命理</font>");
	Spanned s11 = Html.fromHtml("<font color='white'>教育</font>");
	Spanned s12 = Html.fromHtml("<font color='white'>企业品牌</font>");
	Spanned s13 = Html.fromHtml("<font color='white'>酷站汇</font>");
	Spanned s14 = Html.fromHtml("<font color='white'>腾讯产品</font>");
	Spanned s15 = Html.fromHtml("<font color='white'>营销广告</font>");
	Spanned s16 = Html.fromHtml("<font color='white'>广播</font>");
	Spanned s17 = Html.fromHtml("<font color='white'>电视</font>");
	Spanned s18 = Html.fromHtml("<font color='white'>报纸</font>");
	Spanned s19 = Html.fromHtml("<font color='white'>杂志</font>");
	Spanned s20 = Html.fromHtml("<font color='white'>网络媒体</font>");
	Spanned s21 = Html.fromHtml("<font color='white'>通讯社</font>");
	Spanned s22 = Html.fromHtml("<font color='white'>有趣用户</font>");
	Spanned s23 = Html.fromHtml("<font color='white'>传媒领袖</font>");
	Spanned s24 = Html.fromHtml("<font color='white'>名编名记</font>");
	Spanned s25 = Html.fromHtml("<font color='white'>主持人</font>");
	Spanned s26 = Html.fromHtml("<font color='white'>传媒学者</font>");
	Spanned s27 = Html.fromHtml("<font color='white'>专栏评论</font>");
	Spanned s28 = Html.fromHtml("<font color='white'>政府机构</font>");
	Spanned s29 = Html.fromHtml("<font color='white'>公益慈善</font>");
	Spanned s30 = Html.fromHtml("<font color='white'>公务人员</font>");
	Spanned s31 = Html.fromHtml("<font color='white'>快乐女声</font>");
	Spanned s32 = Html.fromHtml("<font color='white'>公共名人</font>");
	Spanned s33 = Html.fromHtml("<font color='white'>花儿朵朵</font>");

	private Spanned[] famousList = { s1, s2, s3, s4, s5,

	s6, s7, s8, s9, s10, s11, s12, s13, s14, s15, s16, s17, s18, s19, s20, s21,
			s22, s23, s24, s25, s26, s27, s28, s29, s30, s31, s32, s33 };

	private String[] famousListIds = { "101", "102", "103", "104 ", "105",
			"106 ", "108", "109", "110", "111 ", "112", "114", "115", "116",
			"267", "subclass_959", "subclass_960", "subclass_961",
			"subclass_962", "subclass_963 ", "subclass_964", "288 ",
			"subclass_953", "subclass_955", "subclass_956", "subclass_957 ",
			"subclass_958 ", "304", "363", "945 ", "949", "950", "951 " };
	private Context mContext;

	private StatusData statusData;

	private ListView famousListView;

	private String show;

	public TencentFamouslistDialog(Context context) {
		super(context);

		mContext = context;

		// Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_trends, null);
		setView(layoutView);

		setTitle(mContext.getString(R.string.discovery_famous_list));

		CrowdroidApplication app = (CrowdroidApplication) getContext()
				.getApplicationContext();
		statusData = app.getStatusData();

		// Find Views
		famousListView = (ListView) layoutView
				.findViewById(R.id.dialog_trends_list);
		layoutView.findViewById(R.id.dialog_trends_type).setVisibility(
				View.GONE);

		// Set Click Listener
		famousListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int position, long arg3) {

				if (!famousAdapter.isEmpty()) {

					// Get List Information
					if (IGeneral.SERVICE_NAME_TENCENT.equals(statusData
							.getCurrentService())) {
						Intent i = new Intent(getContext(),
								TencentFamouslistActivity.class);
						Bundle bundle = new Bundle();

						if (famousList[position].equals(s16)
								|| famousList[position].equals(s17)
								|| famousList[position].equals(s18)
								|| famousList[position].equals(s19)
								|| famousList[position].equals(s20)
								|| famousList[position].equals(s21)) {
							bundle.putString("classid", "268");
							bundle.putString("subclassid",
									famousListIds[position]);

							show = "name:" + famousList[position]
									+ "classid:268 " + " subclassid:"
									+ famousListIds[position];
							// Toast.makeText(getContext(), show,
							// Toast.LENGTH_LONG).show();
						} else if (famousList[position].equals(s23)
								|| famousList[position].equals(s24)
								|| famousList[position].equals(s25)
								|| famousList[position].equals(s26)
								|| famousList[position].equals(s27)) {
							bundle.putString("classid", "294");
							bundle.putString("subclassid",
									famousListIds[position]);

							show = "name:" + famousList[position]
									+ "classid:294 " + "subclassid:"
									+ famousListIds[position];
							// Toast.makeText(getContext(), show,
							// Toast.LENGTH_LONG).show();
						} else {
							bundle.putString("classid", famousListIds[position]);
							bundle.putString("subclassid", "");

							show = "name:" + famousList[position] + " classid:"
									+ famousListIds[position];
							// Toast.makeText(getContext(), show,
							// Toast.LENGTH_LONG).show();
						}
						bundle.putString("name",
								famousList[position].toString());
						i.putExtras(bundle);
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						getContext().startActivity(i);

					}

				}
			}
		});

		// OK
		setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.trends_back),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});

		// Set Adapter
		famousAdapter = new ArrayAdapter<Spanned>(getContext(),
				android.R.layout.simple_list_item_1, famousList);

		famousListView.setAdapter(famousAdapter);
	}

}
