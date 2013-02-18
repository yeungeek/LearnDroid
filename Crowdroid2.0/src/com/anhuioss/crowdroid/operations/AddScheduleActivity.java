package com.anhuioss.crowdroid.operations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.CalendarInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.DateTimeFormat;
import com.anhuioss.crowdroid.util.ErrorMessage;
import com.anhuioss.crowdroid.util.NumberPicker;
import com.anhuioss.crowdroid.util.NumberPicker.OnChangedListener;

public class AddScheduleActivity extends Activity implements OnClickListener,
		OnTouchListener, OnItemClickListener, ServiceConnection {

	private ApiServiceInterface apiServiceInterface;

	private int commType;

	private StatusData statusData;

	private SettingData settingData;

	private AccountData accountData;

	private static boolean isRunning = true;

	private HandleProgressDialog progress;

	private Button headBack = null;

	private Button headHome = null;

	private TextView headName = null;

	private EditText title;

	private Spinner type;

	private EditText decripe;

	private Spinner sHour;

	private Spinner sMinter;

	private Button startTime;
	private Button endTime;

	private Spinner eHour;

	private Spinner eMinter;

	private EditText select;

	private TextView author;

	private Button add;

	/** Title */
	private TextView dTitle;

	/** ProgressBar */
	private ProgressBar progressBar;

	/** Prev */
	private Button prevButton;

	/** Next */
	private Button nextButton;

	private boolean statusFlag = false;

	/** Close */
	private Button closeButton;

	/** List View */
	private ListView listView;

	private ArrayAdapter<String> userAdapter;

	private ArrayList<String> userData = new ArrayList<String>();

	private AlertDialog userSelectDialog;

	private ArrayList<String> userNameList = new ArrayList<String>();
	private ArrayList<String> userIdList = new ArrayList<String>();

	private String[] hourStr = { "00", "01", "02", "03", "04", "05", "06",
			"07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17",
			"18", "19", "20", "21", "22", "23" };
	private String[] minterStr = { "00", "30" };

	private ArrayAdapter<String> hourAdapter;

	private ArrayAdapter<String> minterAdapter;

	private ArrayList<CalendarInfo> calendarInfoList;

	private ArrayList<UserInfo> userInfoList;

	private ArrayAdapter<String> statesAdapter;

	private List<String> statesData = new ArrayList<String>();

	private String calendartype;

	private String starthour = "00";
	private String startminute = "00";

	private String endhour = "00";
	private String endminute = "00";

	private StringBuffer name;

	private StringBuffer ids = new StringBuffer();

	private Calendar calendar_btn;

	private final int CHECK_INIT_START_DATE = 0;

	private final int CHECK_INIT_END_DATE = 1;

	// 数据库更新变量
	static int iYear;
	static int iMonth;
	static int iDay;

	String strValue;
	int dataId;

	// 日期的区间
	int startYear;
	int startMonth;
	int endYear;
	int endMonth;

	public int leftNumber = 0;
	public int middleNumber = 0;
	public int rightNumber = 0;

	public int leftExtraNumber = 0;
	public int rightExtraNumber = 0;

	NumberPicker mPickerLeft;
	NumberPicker mPickerMiddle;
	NumberPicker mPickerRight;
	public TextView viewPoint;

	NumberPicker mPickerLeftExtra;
	NumberPicker mPickerRightExtra;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& !"[]".equals(message)) {

				closeProgressDialog();

				if (type == CommHandler.TYPE_GET_CFB_SCADULE_TYPE) {
					ParseHandler parseHandler = new ParseHandler();
					calendarInfoList = (ArrayList<CalendarInfo>) parseHandler
							.parser(service, type, statusCode, message);

					if (calendarInfoList != null && calendarInfoList.size() > 0) {
						createTypeSpinner(calendarInfoList);
					}
				}
				if (type == CommHandler.TYPE_GET_CFB_USER) {
					ParseHandler parseHandler = new ParseHandler();
					userInfoList = (ArrayList<UserInfo>) parseHandler.parser(
							service, type, statusCode, message);

					progressBar.setVisibility(View.GONE);

					if (userInfoList != null && userInfoList.size() > 0) {
						createUserSpinner(userInfoList);
					}
				}
				if (type == CommHandler.TYPE_CFB_UPDATE_SCADULE) {
					Intent intent = new Intent(AddScheduleActivity.this,
							ScheduleMonthActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			} else {
				Toast.makeText(
						AddScheduleActivity.this,
						ErrorMessage.getErrorMessage(AddScheduleActivity.this,
								statusCode), Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		setContentView(R.layout.activity_add_schedule);

		findView();
	}

	private void findView() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

		Date curDate = new Date(System.currentTimeMillis());
		String btnTime = formatter.format(curDate);

		headBack = (Button) findViewById(R.id.head_back);
		headHome = (Button) findViewById(R.id.head_refresh);
		headHome.setBackgroundResource(R.drawable.main_home);
		headName = (TextView) findViewById(R.id.head_Name);
		headName.setText(getString(R.string.schedule_add));

		title = (EditText) findViewById(R.id.title);
		type = (Spinner) findViewById(R.id.type);
		decripe = (EditText) findViewById(R.id.discribe);
		startTime = (Button) findViewById(R.id.startTime);
		endTime = (Button) findViewById(R.id.endTime);
		sHour = (Spinner) findViewById(R.id.sHour);
		sMinter = (Spinner) findViewById(R.id.sMinite);
		eHour = (Spinner) findViewById(R.id.eHour);
		eMinter = (Spinner) findViewById(R.id.eMinite);
		select = (EditText) findViewById(R.id.users);
		author = (TextView) findViewById(R.id.author);
		add = (Button) findViewById(R.id.add);

		author.setText(accountData.getUserScreenName());

		hourAdapter = new ArrayAdapter<String>(AddScheduleActivity.this,
				android.R.layout.simple_spinner_item, hourStr);
		hourAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		minterAdapter = new ArrayAdapter<String>(AddScheduleActivity.this,
				android.R.layout.simple_spinner_item, minterStr);
		minterAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sHour.setAdapter(hourAdapter);
		sMinter.setAdapter(minterAdapter);

		eHour.setAdapter(hourAdapter);
		eMinter.setAdapter(minterAdapter);

		startTime.setOnClickListener(this);
		endTime.setOnClickListener(this);

		startTime.setText(btnTime);
		endTime.setText(btnTime);
		add.setOnClickListener(this);
		select.setOnTouchListener(this);
		select.setOnClickListener(this);
		headBack.setOnClickListener(this);
		headHome.setOnClickListener(this);
		sHour.setOnItemSelectedListener(selectionListenerShour);
		sMinter.setOnItemSelectedListener(selectionListenerSMinter);
		eHour.setOnItemSelectedListener(selectionListenerEhour);
		eMinter.setOnItemSelectedListener(selectionListenerEMinter);
		type.setOnItemSelectedListener(selectionListenerType);
		ids.append(";");

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent comment = new Intent(AddScheduleActivity.this,
					HomeTimelineActivity.class);
			startActivity(comment);
			break;
		}
		case R.id.ok_button: {
			name = new StringBuffer();
			if (!userNameList.isEmpty()) {
				for (int i = 0; i < userNameList.size(); i++) {
					name.append(userNameList.get(i) + ";");
					for (UserInfo userInfo : userInfoList) {
						if (userInfo.getScreenName()
								.equals(userNameList.get(i))) {
							userIdList.add(userInfo.getUid());
						}
					}
				}
				select.setText(name);

			}

			if (!userIdList.isEmpty()) {

				for (int i = 0; i < userIdList.size(); i++) {
					ids.append(userIdList.get(i) + ";");
				}
			}

			userSelectDialog.dismiss();
			break;
		}
		case R.id.add: {
			if (commpareTime()) {
				String new_IDs = "";
				if (ids.equals(";")) {
					new_IDs = ";";
				} else {
					new_IDs = ids.toString();

				}

				Map<String, Object> parameters = new HashMap<String, Object>();
				parameters.put("calendartitle", title.getText().toString());
				parameters.put("calendartype", calendartype);
				parameters.put("calendarcontent", decripe.getText().toString());
				parameters.put("startdate", startTime.getText().toString());
				parameters.put("starthour", starthour);
				parameters.put("startminute", startminute);
				parameters.put("enddate", endTime.getText().toString());
				parameters.put("endthour", endhour);
				parameters.put("endminute", endminute);
				parameters.put("participants", new_IDs);
				showProgressDialog();
				try {
					apiServiceInterface.request(statusData.getCurrentService(),
							CommHandler.TYPE_CFB_UPDATE_SCADULE,
							apiServiceListener, parameters);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			break;
		}
		case R.id.startTime: {
			openInsertDateDialog(0);
			break;
		}
		case R.id.endTime: {
			openInsertDateDialog(1);
			break;
		}

		default:
			break;
		}

	}

	protected void setCommType(int commType) {

		this.commType = commType;

	}

	@Override
	public void onStart() {
		super.onStart();
		isRunning = true;

		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_CFB_SCADULE_TYPE, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
	}

	private OnItemSelectedListener selectionListenerType = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			calendartype = type.getSelectedItem().toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private OnItemSelectedListener selectionListenerShour = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			starthour = sHour.getSelectedItem().toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private OnItemSelectedListener selectionListenerSMinter = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			startminute = sMinter.getSelectedItem().toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private OnItemSelectedListener selectionListenerEhour = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			endhour = eHour.getSelectedItem().toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};
	private OnItemSelectedListener selectionListenerEMinter = new OnItemSelectedListener() {// 月对应的日期数

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			endminute = eMinter.getSelectedItem().toString();

		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {

		}
	};

	private boolean commpareTime() {

		boolean flag = false;
		long sta = Long.valueOf(startTime.getText().toString().replace("-", "")
				+ starthour + startminute);
		long end = Long.valueOf(endTime.getText().toString().replace("-", "")
				+ endhour + endminute);

		if (sta >= end) {
			flag = false;
			Toast.makeText(AddScheduleActivity.this,
					getResources().getString(R.string.schedule_time_tip),
					Toast.LENGTH_SHORT).show();
		}

		if ("".equals(title.getText().toString())) {
			Toast.makeText(AddScheduleActivity.this,
					getResources().getString(R.string.schedule_title_tip),
					Toast.LENGTH_SHORT).show();
		} else if ("".equals(decripe.getText().toString())) {
			Toast.makeText(AddScheduleActivity.this,
					getResources().getString(R.string.schedule_detail_tip),
					Toast.LENGTH_SHORT).show();
		}
		if (sta < end && !"".equals(title.getText().toString())
				&& !"".equals(decripe.getText().toString())) {
			flag = true;
		}
		return flag;
	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(this);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
		case R.id.users: {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				createDialog();
				break;
			}
		}
		default:
			break;
		}
		return false;
	}

	private void createDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				AddScheduleActivity.this);
		// dialog.setContentView(R.layout.at_dialog_user_select);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View view = mInflater.inflate(R.layout.at_dialog_user_select, null);
		builder.setView(view);
		userSelectDialog = builder.show();

		// Find Views
		dTitle = (TextView) view.findViewById(R.id.dialog_title);
		progressBar = (ProgressBar) view.findViewById(R.id.dialog_progress_bar);
		prevButton = (Button) view.findViewById(R.id.prev_button);
		nextButton = (Button) view.findViewById(R.id.next_button);
		closeButton = (Button) view.findViewById(R.id.ok_button);
		listView = (ListView) view.findViewById(R.id.user_select_listview);
		prevButton.setEnabled(false);
		nextButton.setEnabled(false);
		dTitle.setText(getString(R.string.select_user_schedule));
		// Set Click Listener
		prevButton.setOnClickListener(this);
		nextButton.setOnClickListener(this);
		closeButton.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		Map<String, Object> parameters = new HashMap<String, Object>();
		try {
			apiServiceInterface.request(statusData.getCurrentService(),
					CommHandler.TYPE_GET_CFB_USER, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	protected void createTypeSpinner(ArrayList<CalendarInfo> calendarInfoList2) {
		statesData.clear();
		for (CalendarInfo calendarInfo : calendarInfoList2) {
			statesData.add(calendarInfo.gerName());
		}
		// Create State Adapter
		statesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, statesData);
		statesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Set State Adapter
		type.setAdapter(statesAdapter);
	}

	protected void createUserSpinner(ArrayList<UserInfo> userInfoList) {

		userData.clear();
		for (UserInfo userInfo : userInfoList) {
			userData.add(userInfo.getScreenName());
		}
		// Set Adapter
		userAdapter = new ArrayAdapter<String>(AddScheduleActivity.this,
				android.R.layout.simple_list_item_checked, userData);
		listView.setAdapter(userAdapter);
		listView.setItemsCanFocus(false);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long id) {

		if (userNameList
				.contains(((CheckedTextView) view).getText().toString())) {
			userNameList.remove(((CheckedTextView) view).getText().toString());
		} else {
			userNameList.add(((CheckedTextView) view).getText().toString());
		}

	}

	private void openInsertDateDialog(final int type) {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(
				AddScheduleActivity.this);

		alertDialog
				.setCustomTitle(getDialogTitle(getString(R.string.setting_date)));

		alertDialog.setView(dateDialogView(false));

		alertDialog.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {
						switch (type) {
						case 0: {
							// checkDateHandler.sendEmptyMessage(CHECK_INIT_START_DATE);

							String month = String.valueOf(iMonth);
							if (iMonth < 10) {
								month = "0" + month;
							}
							String day = String.valueOf(iDay);
							if (iDay < 10) {
								day = "0" + day;
							}
							startTime.setText(iYear + "-" + month + "-" + day);
							break;
						}
						case 1: {
							// checkDateHandler.sendEmptyMessage(CHECK_INIT_END_DATE);
							String month = String.valueOf(iMonth);
							if (iMonth < 10) {
								month = "0" + month;
							}
							String day = String.valueOf(iDay);
							if (iDay < 10) {
								day = "0" + day;
							}
							endTime.setText(iYear + "-" + month + "-" + day);
							break;
						}
						}
					}

				});
		alertDialog.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface paramDialogInterface,
							int paramInt) {

					}

				});

		alertDialog.show();
	}

	/**
	 * 显示日期的dateDialogView
	 */
	public View dateDialogView(boolean hasData) {
		View datePickerView = LayoutInflater.from(this).inflate(
				R.layout.data_setting_picker, null);

		mPickerLeft = (NumberPicker) datePickerView
				.findViewById(R.id.dialog_data_left);
		mPickerMiddle = (NumberPicker) datePickerView
				.findViewById(R.id.dialog_data_middle);
		viewPoint = (TextView) datePickerView
				.findViewById(R.id.dialog_data_point);
		mPickerRight = (NumberPicker) datePickerView
				.findViewById(R.id.dialog_data_right);

		viewPoint.setVisibility(View.GONE);

		mPickerLeft.setRange(2000, 2021);
		mPickerMiddle.setRange(1, 12);

		Calendar calendar = Calendar.getInstance();

		if (!hasData) {
			if (calendar.get(Calendar.YEAR) < 2000
					|| calendar.get(Calendar.YEAR) > 2021) {
				leftNumber = 2011;
				Toast.makeText(AddScheduleActivity.this, "shijian",
						Toast.LENGTH_SHORT).show();
			} else {
				leftNumber = calendar.get(Calendar.YEAR);
			}
			middleNumber = calendar.get(Calendar.MONTH) + 1;
			rightNumber = calendar.get(Calendar.DAY_OF_MONTH);
		}

		iYear = leftNumber;
		iMonth = middleNumber;
		iDay = rightNumber;

		mPickerRight.setRange(1,
				DateTimeFormat.getLastDayOfMonth(iYear, iMonth));

		mPickerLeft.setCurrent(leftNumber);
		mPickerMiddle.setCurrent(middleNumber);
		mPickerRight.setCurrent(rightNumber);

		mPickerLeft.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				iYear = newVal;

				int maxDay = DateTimeFormat.getLastDayOfMonth(iYear, iMonth);
				mPickerRight.setRange(1, maxDay);
				if (maxDay < iDay) {
					iDay = maxDay;
				}
				mPickerRight.setCurrent(iDay);
			}

		});

		mPickerMiddle.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				iMonth = newVal;

				int maxDay = DateTimeFormat.getLastDayOfMonth(iYear, iMonth);
				mPickerRight.setRange(1, maxDay);

				if (maxDay < iDay) {
					iDay = maxDay;
				}

				mPickerRight.setCurrent(iDay);
			}

		});

		mPickerRight.setOnChangeListener(new OnChangedListener() {

			@Override
			public void onChanged(NumberPicker picker, int oldVal, int newVal) {
				iDay = newVal;
			}

		});

		return datePickerView;
	}

	/**
	 * 定义dialog的头
	 * 
	 * @param title
	 * @return
	 */
	private View getDialogTitle(String title) {
		TextView titleView = new TextView(AddScheduleActivity.this);
		titleView.setText(title);
		titleView.setTextSize(20);
		titleView.setGravity(Gravity.CENTER);
		titleView.setTextColor(Color.WHITE);

		return titleView;
	}

}
