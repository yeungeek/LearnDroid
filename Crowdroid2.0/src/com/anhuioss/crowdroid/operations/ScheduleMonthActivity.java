package com.anhuioss.crowdroid.operations;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.CommentActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.info.CalendarInfo;
import com.anhuioss.crowdroid.data.info.EmotionInfo;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.UserInfo;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;
import com.anhuioss.crowdroid.util.DayCell;
import com.anhuioss.crowdroid.util.ErrorMessage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout.LayoutParams;

public class ScheduleMonthActivity extends ScheduleBasicActivity implements
		OnClickListener, ServiceConnection, OnItemClickListener {

	//
	Button btnPrev = null;
	Button btnToday = null;
	Button btnNext = null;

	// head
	private Button headerBack;
	private Button headerRefresh;
	private TextView headName;

	private TextView scheduleTitle;

	private TextView week1;
	private TextView week2;
	private TextView week3;
	private TextView week4;
	private TextView week5;
	private TextView week6;

	private Button closeButton;

	private ListView listView;

	private LinearLayout mainlayout;

	private ApiServiceInterface apiServiceInterface;

	private AccountData accountData;

	private CrowdroidApplication crowdroidApplication;

	ArrayList<CalendarInfo> calendarInfoList;

	private ArrayList<DayCell> days = new ArrayList<DayCell>();

	private Calendar calStartDate = Calendar.getInstance();
	private Calendar calToday = Calendar.getInstance();
	private Calendar calCalendar = Calendar.getInstance();
	private Calendar calSelected = Calendar.getInstance();
	//

	private int iFirstDayOfWeek = Calendar.SUNDAY;
	private int iMonthViewCurrentMonth = 0;
	private int iMonthViewCurrentYear = 0;
	private int iDayCellSize;
	private int iDayHeaderHeight = 0;

	private int mYear;
	private int mMonth;
	private int mDay;

	private String message = "";

	private boolean initViewFlag = false;

	private boolean initDataFlag = false;

	private Timer timer;

	private String startDate = "";

	private String endDate = "";

	private String userName = "";

	private String messageIds[];

	private AlertDialog monthDialog;

	private ArrayAdapter<String> monthAdapter;

	private ArrayList<String> monthData = new ArrayList<String>();

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			if (statusCode != null && statusCode.equals("200")
					&& message != null && !message.equals("[null]")) {

				calendarInfoList = new ArrayList<CalendarInfo>();
				if (type == CommHandler.TYPE_GET_CFB_MONTH_SCHEDULE) {

					ParseHandler parseHandler = new ParseHandler();
					calendarInfoList = (ArrayList<CalendarInfo>) parseHandler
							.parser(service, type, statusCode, message);
					Log.e("result", message);
					calStartDate = getCalendarStartDate();
					if (calendarInfoList != null && calendarInfoList.size() > 0) {
						DayCell daySelected = refreshCalendar(calendarInfoList);
						if (daySelected != null) {
							daySelected.requestFocus();
						}
					}

				}
			}
		}
	};

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏

		iFirstDayOfWeek = Calendar.SUNDAY;
		mYear = calSelected.get(Calendar.YEAR);
		mMonth = calSelected.get(Calendar.MONTH);
		mDay = calSelected.get(Calendar.DAY_OF_MONTH);

		// ----------------------------------------
		WindowManager manager = getWindowManager();
		int width = (int) (manager.getDefaultDisplay().getWidth());
		iDayCellSize = width / 7;
		// ----------------------------------------

		setContentView(R.layout.activity_schedule_month);

		initView();

		mainlayout = (LinearLayout) findViewById(R.id.linear_main);
		mainlayout.setPadding(0, 0, 0, 0);
		mainlayout.setBackgroundColor(0x2e3CD2F0);

		final Handler myHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					if (mainlayout.getWidth() != 0) {
						iDayHeaderHeight = mainlayout.getHeight() / 6;

						// 日历主部分
						if (iDayHeaderHeight > 0) {
							mainlayout.addView(getCalendarBody());
							calStartDate = getCalendarStartDate();
							DayCell daySelected = updateCalendar();
							if (daySelected != null) {
								daySelected.requestFocus();
							}
							// 获取日历的起止时间
							if (!initViewFlag) {
								getScheduleData();
							}
							// 取消定时器
							timer.cancel();

						}

					}
				}
			}
		};

		timer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 1;
				myHandler.sendMessage(message);
			}
		};
		// 延迟每次延迟10 毫秒 隔1秒执行一次
		timer.schedule(task, 10, 1000);

	}

	private void initView() {
		// head
		headerBack = (Button) findViewById(R.id.head_back);
		headerRefresh = (Button) findViewById(R.id.head_refresh);
		headName = (TextView) findViewById(R.id.head_Name);

		scheduleTitle = (TextView) findViewById(R.id.schedule_month_title);
		// week number
		week1 = (TextView) findViewById(R.id.week1);
		week2 = (TextView) findViewById(R.id.week2);
		week3 = (TextView) findViewById(R.id.week3);
		week4 = (TextView) findViewById(R.id.week4);
		week5 = (TextView) findViewById(R.id.week5);
		week6 = (TextView) findViewById(R.id.week6);

		int firstWeek = getWeekOfYear();
		String week = getString(R.string.week);
		week1.setText(String.valueOf(firstWeek - 1) + week);
		week2.setText(String.valueOf(firstWeek) + week);
		week3.setText(String.valueOf(firstWeek + 1) + week);
		week4.setText(String.valueOf(firstWeek + 2) + week);
		week5.setText(String.valueOf(firstWeek + 3) + week);
		week6.setText(String.valueOf(firstWeek + 4) + week);

		mYear = calSelected.get(Calendar.YEAR);// 2012
		mMonth = calSelected.get(Calendar.MONTH) + 1;// 06
		mDay = calSelected.get(Calendar.DAY_OF_MONTH);// 20

		headerRefresh.setBackgroundResource(R.drawable.main_home);
		headName.setText(getString(R.string.schedule));

		headerBack.setOnClickListener(this);
		headerRefresh.setOnClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();
		// Bind Api Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
		refreshCalendar(calendarInfoList);
		// updateCalendar();
	}

	private void getScheduleData() {
		initViewFlag = true;
		try {
			String userId = accountData.getUid();
			userName = accountData.getUserScreenName();
			Map<String, Object> parameters;
			parameters = new HashMap<String, Object>();
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				userId = bundle.getString("userId");
				userName = bundle.getString("userName");
			}

			parameters.put("userId", userId);
			parameters.put("startDate", startDate);
			parameters.put("endDate", endDate);
			// Request
			if (apiServiceInterface != null) {
				apiServiceInterface.request(
						IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS,
						CommHandler.TYPE_GET_CFB_MONTH_SCHEDULE,
						apiServiceListener, parameters);
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}
		scheduleTitle.setText(getString(R.string.calendar) + "-"
				+ getString(R.string.schedule_month) + "：" + "[" + userName
				+ "] " + mYear + "-" + mMonth + "-" + mDay);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	/**
	 * 获得日历的头部
	 * 
	 * @return
	 */
	private View getCalendarHear() {
		// 日历头部的布局
		return getLayoutInflater().inflate(R.layout.calendar_header, null);
	}

	/**
	 * 获得日历的内容
	 * 
	 * @return
	 */
	private View getCalendarBody() {

		// 日历整体布局
		LinearLayout calendarBody = new LinearLayout(this);
		calendarBody.setOrientation(LinearLayout.VERTICAL);
		calendarBody.setPadding(2, 2, 2, 2);
		calendarBody.setBackgroundColor(0xee3CD2F0);

		// 日历的行
		LinearLayout calendarRows = new LinearLayout(this);
		calendarRows.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams row = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT, 1.0f);
		calendarRows.setLayoutParams(row);
		days.clear();

		// 日历的列
		LinearLayout calendarColumns;
		DayCell dayCell;
		for (int iRow = 0; iRow < 6; iRow++) {
			calendarColumns = new LinearLayout(this);
			for (int iDay = 0; iDay < 7; iDay++) {
				dayCell = new DayCell(this, iDayCellSize, iDayHeaderHeight);
				dayCell.setItemClick(mOnDayCellClick);
				days.add(dayCell);
				// 添加日期单元到行
				calendarColumns.addView(dayCell);
			}
			// 添加日历的列到行
			calendarRows.addView(calendarColumns);
		}
		// 添加日历元素
		calendarBody.addView(calendarRows);
		return calendarBody;
	}

	/**
	 * 获得开始的Calendar引用
	 * 
	 * @return
	 */
	private Calendar getCalendarStartDate() {

		calToday.setTimeInMillis(System.currentTimeMillis());
		calToday.setFirstDayOfWeek(iFirstDayOfWeek);
		calStartDate.setTimeInMillis(calSelected.getTimeInMillis());
		calStartDate.setFirstDayOfWeek(iFirstDayOfWeek);

		updateStartDateForMonth();

		return calStartDate;

	}

	/**
	 * 刷新日历的显示和更新引用
	 * 
	 * @return
	 */
	private DayCell updateCalendar() {

		DayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = true;// (calSelected.getTimeInMillis() !=
											// 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		int size = days.size();// 42个表格
		for (int i = 0; i < size; i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DayCell dayCell = days.get(i);// 填充每一个数字
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check holiday
			boolean bHoliday = false;
			boolean bHolidaySUNDAY = false;
			if ((iDayOfWeek == Calendar.SATURDAY)) {
				bHoliday = true;
			}
			if ((iDayOfWeek == Calendar.SUNDAY)) {
				bHolidaySUNDAY = true;
			}

			bSelected = false;
			if (bIsSelection) {
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			}

			// 获得到当前日历的第一个日期
			if (i == 0) {
				startDate = String.valueOf(iYear) + "-"
						+ String.valueOf(iMonth + 1) + "-"
						+ String.valueOf(iDay);
			}
			// 获得当前日历的最后一个数据
			if (i == 41) {
				endDate = String.valueOf(iYear) + "-"
						+ String.valueOf(iMonth + 1) + "-"
						+ String.valueOf(iDay);
			}

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					bHolidaySUNDAY, iMonthViewCurrentMonth, "");
			dayCell.setSelected(bSelected);
			if (bSelected) {
				daySelected = dayCell;
			}
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		mainlayout.invalidate();
		return daySelected;

	}

	private DayCell refreshCalendar(ArrayList<CalendarInfo> calendarInfoList) {

		DayCell daySelected = null;
		boolean bSelected = false;
		final boolean bIsSelection = true;// (calSelected.getTimeInMillis() !=
											// 0);
		final int iSelectedYear = calSelected.get(Calendar.YEAR);
		final int iSelectedMonth = calSelected.get(Calendar.MONTH);
		final int iSelectedDay = calSelected.get(Calendar.DAY_OF_MONTH);
		calCalendar.setTimeInMillis(calStartDate.getTimeInMillis());
		int size = days.size();// 42个表格
		for (int i = 0; i < size; i++) {
			final int iYear = calCalendar.get(Calendar.YEAR);
			final int iMonth = calCalendar.get(Calendar.MONTH);
			final int iDay = calCalendar.get(Calendar.DAY_OF_MONTH);
			final int iDayOfWeek = calCalendar.get(Calendar.DAY_OF_WEEK);
			DayCell dayCell = days.get(i);// 填充每一个数字
			// check today
			boolean bToday = false;
			if (calToday.get(Calendar.YEAR) == iYear)
				if (calToday.get(Calendar.MONTH) == iMonth)
					if (calToday.get(Calendar.DAY_OF_MONTH) == iDay)
						bToday = true;
			// check holiday
			boolean bHoliday = false;
			boolean bHolidaySUNDAY = false;
			if ((iDayOfWeek == Calendar.SATURDAY)) {
				bHoliday = true;
			}
			if ((iDayOfWeek == Calendar.SUNDAY)) {
				bHolidaySUNDAY = true;
			}

			bSelected = false;
			if (bIsSelection) {
				if ((iSelectedDay == iDay) && (iSelectedMonth == iMonth)
						&& (iSelectedYear == iYear)) {
					bSelected = true;
				}
			}

			// 获得到当前日历的第一个日期
			if (i == 0) {
				startDate = String.valueOf(iYear) + "-"
						+ String.valueOf(iMonth + 1) + "-"
						+ String.valueOf(iDay);
			}
			String month = String.valueOf(iMonth + 1);
			String day = String.valueOf(iDay);
			if (iMonth + 1 < 10) {
				month = "0" + month;
			}
			if (iDay < 10) {
				day = "0" + iDay;
			}
			String caIndex = String.valueOf(iYear) + month + day;
			StringBuffer status = new StringBuffer();
			StringBuffer ids = new StringBuffer();

			status.append("");
			ids.append("");
			if (calendarInfoList != null) {
				for (CalendarInfo calendarInfo : calendarInfoList) {
					if (caIndex.equals(calendarInfo.getIndex())) {
						status.append("■" + calendarInfo.getTitle());
						ids.append(calendarInfo.getMessageId() + ";");
					}
				}
			}
			message = status.toString();

			dayCell.setData(iYear, iMonth, iDay, bToday, bHoliday,
					bHolidaySUNDAY, iMonthViewCurrentMonth, message,
					ids.toString());
			dayCell.setSelected(bSelected);
			if (bSelected) {
				daySelected = dayCell;
			}
			calCalendar.add(Calendar.DAY_OF_MONTH, 1);
		}
		mainlayout.invalidate();
		return daySelected;

	}

	private void updateStartDateForMonth() {

		iMonthViewCurrentMonth = calStartDate.get(Calendar.MONTH);
		iMonthViewCurrentYear = calStartDate.get(Calendar.YEAR);
		calStartDate.set(Calendar.DAY_OF_MONTH, 1);
		updateCurrentMonthDisplay();

		// update days for week
		int iDay = 0;
		int iStartDay = iFirstDayOfWeek;//

		if (iStartDay == Calendar.SUNDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
		}
		if (iStartDay == Calendar.SATURDAY) {
			iDay = calStartDate.get(Calendar.DAY_OF_WEEK) - Calendar.SATURDAY;
		}
		calStartDate.add(Calendar.DAY_OF_WEEK, -iDay);

	}

	/**
	 * 刷新显示的内容
	 */
	private void updateCurrentMonthDisplay() {

		String s = calStartDate.get(Calendar.YEAR) + "年"
				+ (format(calStartDate.get(Calendar.MONTH) + 1)) + "月";
		// btnToday.setText(s);
		mYear = calStartDate.get(Calendar.YEAR);

	}

	/**
	 * 格式化Integer到String
	 * 
	 * @param x
	 * @return 如果Integer是一位数字，格式化成“0x”的形式；否则直接返回格式化后的字符串
	 */
	private String format(int x) {

		String s = "" + x;
		if (s.length() == 1) {
			s = "0" + s;
		}
		return s;
	}

	/* 判断某日期是该年的第几周 */
	public static int getWeekOfYear() {

		try {
			Calendar ca = Calendar.getInstance();
			ca.set(Calendar.DAY_OF_MONTH, 1);
			Date firstDate = ca.getTime();
			ca.setTime(firstDate);
			return ca.get(Calendar.WEEK_OF_YEAR);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_refresh: {
			Intent comment = new Intent(ScheduleMonthActivity.this,
					HomeTimelineActivity.class);
			comment.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(comment);
			break;
		}
		case R.id.close_button: {
			monthDialog.dismiss();
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		default:
			break;
		}
	}

	/**
	 * 点击事件
	 */
	private com.anhuioss.crowdroid.util.DayCell.OnItemClick mOnDayCellClick = new DayCell.OnItemClick() {// 设置对每一天的按钮监听

		public void OnClick(DayCell item) {
			calSelected.setTimeInMillis(item.getDate().getTimeInMillis());
			item.setSelected(true);
			if (!"".equals(item.getMessageIds())
					&& !"".equals(item.getMessage())) {
				createDialog(item.getMessage(), item.getMessageIds());
			}
			if (calendarInfoList != null) {
				refreshCalendar(calendarInfoList);
			}
		}
	};

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;
	}

	private void createDialog(String message, String idStr) {

		messageIds = idStr.split(";");

		AlertDialog.Builder builder = new AlertDialog.Builder(
				ScheduleMonthActivity.this);
		LayoutInflater mInflater = LayoutInflater.from(this);
		View view = mInflater.inflate(R.layout.at_dialog_user_select, null);
		builder.setView(view);
		monthDialog = builder.show();

		/* set dialog window layout */

		monthDialog.getWindow().setLayout(
				android.view.WindowManager.LayoutParams.FILL_PARENT,
				android.view.WindowManager.LayoutParams.WRAP_CONTENT);

		// Find Views
		TextView dTitle = (TextView) view.findViewById(R.id.dialog_title);
		view.findViewById(R.id.dialog_progress_bar).setVisibility(View.GONE);
		view.findViewById(R.id.ok_button).setVisibility(View.GONE);
		view.findViewById(R.id.prev_button).setVisibility(View.GONE);
		view.findViewById(R.id.next_button).setVisibility(View.GONE);
		dTitle.setText(getString(R.string.schedule_detail_list));
		closeButton = (Button) view.findViewById(R.id.close_button);
		listView = (ListView) view.findViewById(R.id.user_select_listview);

		closeButton.setText(R.string.close);
		closeButton.setTextColor(Color.WHITE);
		closeButton.setBackgroundResource(R.drawable.selector_button_add);
		closeButton.setVisibility(View.VISIBLE);
		closeButton.setOnClickListener(this);
		listView.setOnItemClickListener(this);

		monthData.clear();
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map;

		String[] status = message.split("■");
		for (int i = 0; i < status.length - 1; i++) {
			monthData.add(status[i + 1]);
			if (calendarInfoList != null && calendarInfoList.size() > 0) {
				for (CalendarInfo calendarInfo : calendarInfoList) {
					if (status[i + 1].equals(calendarInfo.getTitle())
							&& messageIds[i]
									.equals(calendarInfo.getMessageId())) {
						map = new HashMap<String, Object>();
						map.put("title", calendarInfo.getTitle());
						map.put("time",
								calendarInfo.getStartTime().substring(0, 16));
						list.add(map);
					}
					continue;
				}
			}
		}

		// Set Adapter
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.layout_listview_item_2,
				new String[] { "title", "time" }, new int[] {
						R.id.list_content, R.id.list_time });
		listView.setAdapter(adapter);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		message = "";

		String title = monthData.get(position).replace("■", "");

		String messageId = messageIds[position];
		if (calendarInfoList != null && calendarInfoList.size() > 0) {
			for (CalendarInfo calendarInfo : calendarInfoList) {
				if (title.equals(calendarInfo.getTitle())
						&& messageId.equals(calendarInfo.getMessageId())) {
					Intent i = new Intent(ScheduleMonthActivity.this,
							ScheduleDetailActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
					Bundle b = new Bundle();
					b.putSerializable("calendarInfo", calendarInfo);
					i.putExtras(b);
					startActivity(i);
					monthDialog.dismiss();
					continue;
				}

			}
		}
	}
}
