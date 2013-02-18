package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.info.RoadInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.data.info.linesInfo;
import com.anhuioss.crowdroid.data.info.transfersInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.sinaLBS.LBSlocationActivity;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RoadDialog extends Dialog implements OnItemClickListener,
		OnClickListener {

	private Context mContext;

	private String message = "";

	private Window mWindow;

	private Button prevButton;

	/** Title */
	private TextView title;

	/** ProgressBar */
	private ProgressBar progressBar;

	/** Next */
	private Button nextButton;

	private boolean statusFlag = false;

	/** true = A/false = B */
	private boolean listFlag = true;

	private int limitNumver = 25;

	/** Close */
	private Button closeButton;

	/** List View */
	private ListView listView;

	ArrayList<String> data = new ArrayList<String>();

	ArrayAdapter<String> adapter;

	public RoadDialog(Context context, int type) {
		super(context);
		// TODO Auto-generated constructor stub

		// Get Window
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);

		// Set Layout
		setContentView(R.layout.dialog_way);
		mContext = context;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);

		if (type == CommHandler.TYPE_LBS_GET_WAY_BUS) {
			title.setText(R.string.bus_route);
		} else if (type == CommHandler.TYPE_LBS_GET_WAY_CAR) {
			title.setText(R.string.car_route);
		}

		closeButton = (Button) findViewById(R.id.close_button);
		listView = (ListView) findViewById(R.id.user_select_listview);

		// Set Click Listener
		// prevButton.setOnClickListener(this);
		// nextButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		// prevButton.setEnabled(false);

		// Set Adapter
		adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_list_item_1, data);
		if (type == CommHandler.TYPE_LBS_GET_WAY_CAR) {
			createListView1(LBSlocationActivity.RoadDataList);
		} else if (type == CommHandler.TYPE_LBS_GET_WAY_BUS) {
			createListView2(LBSlocationActivity.transfersDataList);
		}

		listView.setAdapter(adapter);
		// listView.setItemsCanFocus(false);
		// listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		// nextCursor = -1;
		// preCursor = -1;

		// Set Progress Bar
		// setProgressEnable(false);

	}

	// public RoadDialog(Context context) {
	// super(context);
	// // TODO Auto-generated constructor stub
	// }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.close_button: {
			this.dismiss();
		}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		// Intent

		// Dismiss

	}

	private void createListView1(ArrayList<RoadInfo> RoadInfoList) {

		// Initial Next And Previous Button
		// changeStatus();

		// Clear Data
		data.clear();

		// Change Data
		for (RoadInfo roadInfo : RoadInfoList) {

			data.add(roadInfo.getroad_name() + ":"
					+ roadInfo.getnavigation_tag());

		}

		// Notify
		adapter.notifyDataSetChanged();

	}

	private void createListView2(ArrayList<transfersInfo> transfersInfoList) {

		// Initial Next And Previous Button
		// changeStatus();

		// Clear Data
		data.clear();

		// Change Data
		for (transfersInfo transfersinfo : transfersInfoList) {

			String line = "";
			// data.add(roadInfo.getroad_name()+":"+roadInfo.getnavigation_tag());
			for (linesInfo lineinfo : transfersinfo.getlines()) {
				if (lineinfo.getstations() != null)
					line = line
							+ lineinfo.getname()
							+ "from"
							+ lineinfo.getstations().get(0).getname()
							+ "to"
							+ lineinfo.getstations()
									.get(lineinfo.getstations().size() - 1)
									.getname() + ";";
			}
			data.add(line);

		}

		// Notify
		adapter.notifyDataSetChanged();

	}

}
