package com.anhuioss.crowdroid.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;

public class SearchDialogActivity extends Activity implements OnClickListener {

	private Button swap;

	private Button setStartOrEnd;

	private Button setEndOrStart;

	private Button bus;

	private Button route;

	private Button walk;

	private Button searchRoute;

	private EditText startOrEnd;

	private EditText endOrStart;

	private String find_the_way = "";// 搜索方式

	private String start = "";

	private String end = "";

	private Intent i;

	String dialogFlag = "";// 设置弹出Dialog的标识

	String startOrendDialogFlag = "";// 设置起/终点标识

	private Window mWindow;

	String drivingPolicy = "";// 搜索策略

	AlertDialog dialog;

	String startOrendFlag = "";

	// 地图上的第一个点
	int pickPointLat;

	int pickPointLon;

	String pickPointCompleteAdd = null;

	// 地图上的第二个点
	int latSecend;

	int lonSecend;

	String completeAddSecend;

	String map;// 是否是在地图上选取的点的标识

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		mWindow.setGravity(Gravity.TOP);
		setContentView(R.layout.routedialog);
		findViews();
	}

	public void findViews() {

		swap = (Button) findViewById(R.id.swap);
		swap.setText(R.string.route_dialog_swap);
		setStartOrEnd = (Button) findViewById(R.id.setStartOrEnd);
		setStartOrEnd.setText(R.string.route_dialog_set_start);
		setEndOrStart = (Button) findViewById(R.id.setEndOrStart);
		setEndOrStart.setText(R.string.route_dialog_set_end);
		bus = (Button) findViewById(R.id.bus);
		bus.setText(R.string.route_dialog_transit);
		route = (Button) findViewById(R.id.route);
		route.setText(R.string.route_dialog_driving);
		walk = (Button) findViewById(R.id.walk);
		walk.setText(R.string.route_dialog_walk);
		searchRoute = (Button) findViewById(R.id.searchRoute);
		searchRoute.setText(R.string.search);
		startOrEnd = (EditText) findViewById(R.id.startOrend);
		endOrStart = (EditText) findViewById(R.id.endOrstart);

		bus.setClickable(true);
		swap.setOnClickListener(this);
		setStartOrEnd.setOnClickListener(this);
		setEndOrStart.setOnClickListener(this);
		bus.setOnClickListener(this);
		route.setOnClickListener(this);
		walk.setOnClickListener(this);
		searchRoute.setOnClickListener(this);
	}

	@Override
	protected void onStart() {

		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("flag")) {
			dialogFlag = bundle.getString("flag");
		}
		if (bundle.containsKey("startOrendDialogFlag")) {
			startOrendDialogFlag = bundle.getString("startOrendDialogFlag");
		}
		if (bundle.containsKey("map")) {
			map = bundle.getString("map");
		}
		// 第一个地图上的点
		if (bundle.containsKey("lat")) {
			pickPointLat = bundle.getInt("lat");

		}
		if (bundle.containsKey("lon")) {
			pickPointLon = bundle.getInt("lon");

		}

		if (bundle.containsKey("completeAdd")) {
			pickPointCompleteAdd = bundle.getString("completeAdd");

		}
		// 第二个地图上的点
		if (bundle.containsKey("secendLat")) {
			latSecend = bundle.getInt("secendLat");

		}
		if (bundle.containsKey("secendLon")) {
			lonSecend = bundle.getInt("secendLon");

		}

		if (bundle.containsKey("secendCompleteAdd")) {
			completeAddSecend = bundle.getString("secendCompleteAdd");

		}
		if (dialogFlag.equals("")) {
			startOrEnd.setHint(getResources().getString(R.string.menu_my_loc));
			startOrEnd.setHintTextColor(Color.BLUE);
			endOrStart.setHint(getResources().getString(
					R.string.route_dialog_edit_end));
			endOrStart.setHintTextColor(null);
		} else if (dialogFlag.equals("pick")) {
			if (startOrendDialogFlag.equals("start")) {
				startOrEnd.setHint(getResources().getString(
						R.string.route_dialog_map));
				startOrEnd.setHintTextColor(Color.BLUE);
				if (map.equals("map")) {
					endOrStart.setHint(getResources().getString(
							R.string.route_dialog_map));
					endOrStart.setHintTextColor(Color.BLUE);
				} else if (map.equals("myLoc")) {
					endOrStart.setHint(getResources().getString(
							R.string.menu_my_loc));
					endOrStart.setHintTextColor(Color.BLUE);
				} else {
					endOrStart.setHint(getResources().getString(
							R.string.route_dialog_edit_end));
					endOrStart.setHintTextColor(null);
				}
			} else if (startOrendDialogFlag.equals("end")) {
				endOrStart.setHint(getResources().getString(
						R.string.route_dialog_map));
				endOrStart.setHintTextColor(Color.BLUE);
				if (map.equals("map")) {
					startOrEnd.setHint(getResources().getString(
							R.string.route_dialog_map));
					startOrEnd.setHintTextColor(Color.BLUE);
				} else if (map.equals("myLoc")) {
					startOrEnd.setHint(getResources().getString(
							R.string.menu_my_loc));
					startOrEnd.setHintTextColor(Color.BLUE);
				} else {
					startOrEnd.setHint(getResources().getString(
							R.string.route_dialog_edit_start));
					startOrEnd.setHintTextColor(null);
				}
			}

		}

		super.onStart();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag=true;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag=false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.swap: {
			// 标识互换
			if (startOrEnd.getHint().equals(
					getResources().getString(R.string.menu_my_loc))
					&& endOrStart.getHint().equals(
							getResources().getString(
									R.string.route_dialog_edit_end))) {
				startOrEnd.setHint(getResources().getString(
						R.string.route_dialog_edit_start));
				endOrStart.setHint(getResources().getString(
						R.string.menu_my_loc));
				endOrStart.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(null);
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.route_dialog_edit_start))
					&& endOrStart.getHint().equals(
							getResources().getString(R.string.menu_my_loc))) {
				startOrEnd.setHint(getResources().getString(
						R.string.menu_my_loc));
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint(getResources().getString(
						R.string.route_dialog_edit_end));
				endOrStart.setHintTextColor(null);
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.route_dialog_map))
					&& endOrStart.getHint().equals(
							getResources().getString(R.string.menu_my_loc))) {
				startOrEnd.setHint(getResources().getString(
						R.string.menu_my_loc));
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint(getResources().getString(
						R.string.route_dialog_map));
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.menu_my_loc))
					&& endOrStart.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {
				startOrEnd.setHint(getResources().getString(
						R.string.route_dialog_map));
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint(getResources().getString(
						R.string.menu_my_loc));
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.route_dialog_map))
					&& endOrStart.getHint().equals(
							getResources().getString(
									R.string.route_dialog_edit_end))) {
				startOrEnd.setHint(getResources().getString(
						R.string.route_dialog_edit_start));
				startOrEnd.requestFocus();
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(null);
				endOrStart.setHint(getResources().getString(
						R.string.route_dialog_map));
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.route_dialog_edit_start))
					&& endOrStart.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {
				startOrEnd.setHint(getResources().getString(
						R.string.route_dialog_map));
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint(getResources().getString(
						R.string.route_dialog_edit_end));
				endOrStart.requestFocus();
				endOrStart.setHintTextColor(null);
			}

			break;
		}
		case R.id.setStartOrEnd: {
			startOrendFlag = "start";
			createSetDialog(startOrendFlag);
			break;
		}

		case R.id.setEndOrStart: {
			startOrendFlag = "end";
			createSetDialog(startOrendFlag);
			break;
		}

		case R.id.bus: {
			find_the_way = "bus";
			bus.setTextColor(0xffffffff);
			route.setTextColor(0xff000000);
			walk.setTextColor(0xff000000);
			createDialog("bus");
			break;
		}

		case R.id.route: {
			find_the_way = "route";
			bus.setTextColor(0xff000000);
			route.setTextColor(0xffffffff);
			walk.setTextColor(0xff000000);
			createDialog("route");
			break;
		}

		case R.id.walk: {
			find_the_way = "walk";
			bus.setTextColor(0xff000000);
			route.setTextColor(0xff000000);
			walk.setTextColor(0xffffffff);
			break;
		}
		case R.id.searchRoute: {
			// 传递参数
			if (startOrEnd.getHint().equals(
					getResources().getString(R.string.menu_my_loc))// 起点是我的位置，终点自定义
					&& ("").equals(startOrEnd.getText().toString())
					&& !("").equals(endOrStart.getText().toString())) {

				end = endOrStart.getText().toString();
				start = "";

			} else if (endOrStart.getHint().equals(
					getResources().getString(R.string.menu_my_loc))// 起点自定义，终点是我的位置
					&& ("").equals(endOrStart.getText().toString())
					&& !("").equals(startOrEnd.getText().toString())) {

				start = startOrEnd.getText().toString();
				end = "";

			} else if (endOrStart.getHint().equals(
					getResources().getString(R.string.menu_my_loc))// 起点是地图上的点，终点是我的位置
					&& ("").equals(endOrStart.getText().toString())
					&& ("").equals(startOrEnd.getText().toString())
					&& startOrEnd.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {

				start = "pick";
				end = "myLoc";
			} else if (startOrEnd.getHint().equals(
					getResources().getString(R.string.menu_my_loc))// 起点是我的位置，终点是地图上的点
					&& ("").equals(endOrStart.getText().toString())
					&& ("").equals(startOrEnd.getText().toString())
					&& endOrStart.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {

				start = "myLoc";
				end = "pick";
			} else if (!("").equals(startOrEnd.getText().toString())// 起点自定义，终点是地图上的点
					&& ("").equals(endOrStart.getText().toString())
					&& endOrStart.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {

				start = startOrEnd.getText().toString();
				end = "pick";
			} else if (!("").equals(endOrStart.getText().toString())// 起点是地图上的点，终点自定义
					&& ("").equals(startOrEnd.getText().toString())
					&& startOrEnd.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))) {

				end = endOrStart.getText().toString();
				start = "pick";
			} else if (endOrStart.getHint().equals(
					getResources().getString(R.string.route_dialog_map))
					&& ("").equals(endOrStart.getText().toString())// 起点是地图上的点，终点是地图上的点
					&& startOrEnd.getHint()
							.equals(getResources().getString(
									R.string.route_dialog_map))
					&& ("").equals(startOrEnd.getText().toString())) {

				end = "pick";
				start = "pick";
			} else {// 起/终点均自定义
				start = startOrEnd.getText().toString();
				end = endOrStart.getText().toString();
			}
			if ("".equals(find_the_way)) {
				Toast.makeText(SearchDialogActivity.this, R.string.policy_tip,
						Toast.LENGTH_SHORT).show();
			} else {
				i = new Intent(SearchDialogActivity.this, RouteActivity.class);
				if (bus.isClickable() == false && route.isClickable() == false
						&& walk.isClickable() == false) {
					i.putExtra("find_the_way", "bus");
				}
				// 方式的选择
				if (("bus").equals(find_the_way)) {
					i.putExtra("find_the_way", "bus");
					i.putExtra("drivingPolicy", drivingPolicy);

				} else if ("route".equals(find_the_way)) {
					i.putExtra("find_the_way", "route");
					i.putExtra("drivingPolicy", drivingPolicy);

				} else if ("walk".equals(find_the_way)) {
					i.putExtra("find_the_way", "walk");
				}
				if (dialogFlag.equals("pick")) {
					if (map.equals("myLoc") || map.equals("")) {
						i.putExtra("pickLat", pickPointLat);
						i.putExtra("pickLon", pickPointLon);
						i.putExtra("pick", "pick");
						i.putExtra("map", map);
						i.putExtra("completeAdd", pickPointCompleteAdd);
					} else if (map.equals("map")) {
						i.putExtra("pickLat", pickPointLat);
						i.putExtra("pickLon", pickPointLon);
						i.putExtra("pick", "pick");
						i.putExtra("completeAdd", pickPointCompleteAdd);
						i.putExtra("pickSecendLat", latSecend);
						i.putExtra("pickSecendLon", lonSecend);
						i.putExtra("map", map);
						i.putExtra("completeAddSecend", completeAddSecend);
					}
				}

				if (("").equals(start) && ("").equals(end)) {
					Toast.makeText(SearchDialogActivity.this,
							R.string.route_dialog_edit_tip, Toast.LENGTH_LONG)
							.show();
				} else {
					i.putExtra("start", start);
					i.putExtra("end", end);

					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					SearchDialogActivity.this.startActivity(i);

				}
			}
			break;
		}

		default:
			break;
		}
	}

	public void createSetDialog(final String flag) {
		String dialogTitle = null;
		if (flag.equals("start")) {
			dialogTitle = getResources().getString(
					R.string.route_dialog_set_start);
		} else if (flag.equals("end")) {
			dialogTitle = getResources().getString(
					R.string.route_dialog_edit_end);
		}
		String[] items = { getResources().getString(R.string.menu_my_loc),
				getResources().getString(R.string.poi_type_map) };
		AlertDialog dialog = new AlertDialog.Builder(SearchDialogActivity.this)
				.setTitle(dialogTitle)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: {
							if (flag.equals("start")) {
								startOrEnd.setHint(getResources().getString(
										R.string.menu_my_loc));
								startOrEnd.setHintTextColor(Color.BLUE);
								startOrEnd.setSelectAllOnFocus(true);
								if (endOrStart.getHint().equals(
										getResources().getString(
												R.string.route_dialog_map))) {
									endOrStart.setHint(getResources()
											.getString(
													R.string.route_dialog_map));

									endOrStart.setHintTextColor(Color.BLUE);
								} else {
									endOrStart.requestFocus();
									endOrStart
											.setHint(getResources()
													.getString(
															R.string.route_dialog_edit_end));
									endOrStart.setHintTextColor(null);
								}

							} else if (flag.equals("end")) {
								endOrStart.setHint(getResources().getString(
										R.string.menu_my_loc));
								endOrStart.setHintTextColor(Color.BLUE);
								endOrStart.setSelectAllOnFocus(true);
								if (startOrEnd.getHint().equals(
										getResources().getString(
												R.string.route_dialog_map))) {
									startOrEnd.setHint(getResources()
											.getString(
													R.string.route_dialog_map));

									startOrEnd.setHintTextColor(Color.BLUE);
								} else {
									startOrEnd.requestFocus();
									startOrEnd
											.setHint(getResources()
													.getString(
															R.string.route_dialog_edit_start));
									startOrEnd.setHintTextColor(null);
								}

							}

							break;
						}
						case 1: {
							if (dialogFlag.equals("pick")) {// 获取地图上第二个点
								i = new Intent(SearchDialogActivity.this,
										PickSecendMapActivity.class);
							} else {// 获取地图上第一个点
								i = new Intent(SearchDialogActivity.this,
										PickupMapActivity.class);
							}

							if (startOrEnd.getHint().equals(
									getResources().getString(
											R.string.route_dialog_map))
									&& startOrendFlag.equals("end")
									|| endOrStart.getHint().equals(
											getResources().getString(
													R.string.route_dialog_map))
									&& startOrendFlag.equals("start")) {
								i.putExtra("flag", startOrendFlag);
								i.putExtra("mapFlag", "map");
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								SearchDialogActivity.this.startActivity(i);
							} else if (startOrEnd.getHint().equals(
									getResources().getString(
											R.string.menu_my_loc))
									&& startOrendFlag.equals("end")
									|| endOrStart.getHint().equals(
											getResources().getString(
													R.string.menu_my_loc))
									&& startOrendFlag.equals("start")) {
								i.putExtra("flag", startOrendFlag);
								i.putExtra("mapFlag", "myLoc");
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								SearchDialogActivity.this.startActivity(i);
							} else {
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("flag", startOrendFlag);
								SearchDialogActivity.this.startActivity(i);
							}

							break;
						}

						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	public void createDialog(String flag) {
		String dialogTitle = null;
		LayoutInflater lay = (LayoutInflater) SearchDialogActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.bus_spinner, null);
		if (flag.equals("bus")) {
			dialogTitle = getResources().getString(R.string.set_transitpolicy);
		} else if (flag.equals("route")) {
			dialogTitle = getResources().getString(R.string.set_drivingpolicy);
		}
		dialog = new AlertDialog.Builder(SearchDialogActivity.this)
				.setTitle(dialogTitle)
				.setView(v)
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialogfer,
									int which) {
								if (dialog != null && dialog.isShowing()) {
									dialog.dismiss();
								}

							}
						}).create();

		dialog.show();

		final Spinner bus_spinner = (Spinner) v.findViewById(R.id.bus_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				SearchDialogActivity.this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add(getResources().getString(R.string.policy_EBUS_TIME_FIRST));
		if (flag.equals("bus")) {

			adapter.add(getResources().getString(
					R.string.policy_EBUS_TRANSFER_FIRST));
			adapter.add(getResources().getString(
					R.string.policy_EBUS_WALK_FIRST));
			adapter.add(getResources()
					.getString(R.string.policy_EBUS_NO_SUBWAY));
		} else if (flag.equals("route")) {

			adapter.add(getResources()
					.getString(R.string.policy_ECAR_DIS_FIRST));
			adapter.add(getResources()
					.getString(R.string.policy_ECAR_FEE_FIRST));
		}

		bus_spinner.setAdapter(adapter);
		bus_spinner.setSelection(0);
		bus_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				drivingPolicy = bus_spinner.getItemAtPosition(arg2).toString();

			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}
