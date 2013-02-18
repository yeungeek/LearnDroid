package com.anhuioss.crowdroid.map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.anhuioss.crowdroid.R;

public class RouteDialog extends Dialog implements OnClickListener {

	private Button swap;

	private Button setStartOrEnd;

	private Button setEndOrStart;

	private Button bus;

	private Button route;

	private Button walk;

	private Button searchRoute;

	private EditText startOrEnd;

	private EditText endOrStart;

	private String find_the_way = "";

	private String start = "";

	private String end = "";

	private Intent i;

	private Context mContext;

	String flag = "";// 设置弹出Dialog的标识

	String startOrendDialogFlag = "";// 设置起/终点标识

	private Window mWindow;

	String drivingPolicy = "";

	AlertDialog dialog;

	String startOrendFlag = "";

	SharedPreferences pickPoint;

	int lat;

	int lon;

	String completeAdd;

	String map;

	public RouteDialog(Context context, String flag,
			String startOrendDialogFlag, String map) {
		super(context);

		mContext = context;

		this.flag = flag;
		this.startOrendDialogFlag = startOrendDialogFlag;
		this.map = map;
		mWindow = getWindow();
		mWindow.requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.routedialog);
		swap = (Button) findViewById(R.id.swap);
		setStartOrEnd = (Button) findViewById(R.id.setStartOrEnd);
		setEndOrStart = (Button) findViewById(R.id.setEndOrStart);
		bus = (Button) findViewById(R.id.bus);
		route = (Button) findViewById(R.id.route);
		walk = (Button) findViewById(R.id.walk);
		searchRoute = (Button) findViewById(R.id.searchRoute);
		startOrEnd = (EditText) findViewById(R.id.startOrend);
		endOrStart = (EditText) findViewById(R.id.endOrstart);
		if (flag.equals("")) {
			startOrEnd.setHint("我的位置");
			startOrEnd.setHintTextColor(Color.BLUE);
			endOrStart.setHint("输入终点....");
			endOrStart.setHintTextColor(null);
		} else if (flag.equals("pick")) {
			if (startOrendDialogFlag.equals("start")) {
				startOrEnd.setHint("地图上的点");
				startOrEnd.setHintTextColor(Color.BLUE);
				if (map.equals("map")) {
					endOrStart.setHint("地图上的点");
					endOrStart.setHintTextColor(Color.BLUE);
				} else if (map.equals("myLoc")) {
					endOrStart.setHint("我的位置");
					endOrStart.setHintTextColor(Color.BLUE);
				} else {
					endOrStart.setHint("输入终点....");
					endOrStart.setHintTextColor(null);
				}
			} else if (startOrendDialogFlag.equals("end")) {
				endOrStart.setHint("地图上的点");
				endOrStart.setHintTextColor(Color.BLUE);
				if (map.equals("map")) {
					startOrEnd.setHint("地图上的点");
					startOrEnd.setHintTextColor(Color.BLUE);
				} else if (map.equals("myLoc")) {
					startOrEnd.setHint("我的位置");
					startOrEnd.setHintTextColor(Color.BLUE);
				} else {
					startOrEnd.setHint("输入起点....");
					startOrEnd.setHintTextColor(null);
				}
			}

		}

		bus.setClickable(true);
		swap.setOnClickListener(this);
		setStartOrEnd.setOnClickListener(this);
		setEndOrStart.setOnClickListener(this);
		bus.setOnClickListener(this);
		route.setOnClickListener(this);
		walk.setOnClickListener(this);
		searchRoute.setOnClickListener(this);

		pickPoint = mContext.getSharedPreferences("pickPoint", 0);
		lat = pickPoint.getInt("pickPointLat", 0);
		lon = pickPoint.getInt("pickPointLon", 0);
		completeAdd = pickPoint.getString("completeAdd", "");

	}

	@Override
	protected void onStart() {
		// if (flag.equals("pick")) {
		// if (startOrendDialogFlag.equals("start")) {
		// startOrEnd.setHint("地图上的点");
		// startOrEnd.setHintTextColor(Color.BLUE);
		// if (endOrStart.getHint().equals("我的位置")) {
		//
		// endOrStart.setHint("我的位置");
		// endOrStart.setHintTextColor(Color.BLUE);
		// } else {
		// endOrStart.requestFocus();
		// endOrStart.setHint("输入终点....");
		// endOrStart.setHintTextColor(null);
		// }
		// } else if (startOrendDialogFlag.equals("end")) {
		// endOrStart.setHint("地图上的点");
		// endOrStart.setHintTextColor(Color.BLUE);
		// if (startOrEnd.getHint().equals("我的位置")) {
		//
		// startOrEnd.setHint("我的位置");
		// startOrEnd.setHintTextColor(Color.BLUE);
		// } else {
		// startOrEnd.requestFocus();
		// startOrEnd.setHint("输入起点....");
		// startOrEnd.setHintTextColor(null);
		// }
		// }
		// }

		super.onStart();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.swap: {
			// 标识互换
			if (startOrEnd.getHint().equals("我的位置")
					&& endOrStart.getHint().equals("输入终点....")) {
				startOrEnd.setHint("输入起点....");
				endOrStart.setHint("我的位置");
				endOrStart.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(null);
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals("输入起点....")
					&& endOrStart.getHint().equals("我的位置")) {
				startOrEnd.setHint("我的位置");
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint("输入终点....");
				endOrStart.setHintTextColor(null);
			} else if (startOrEnd.getHint().equals("地图上的点")
					&& endOrStart.getHint().equals("我的位置")) {
				startOrEnd.setHint("我的位置");
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint("地图上的点");
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals("我的位置")
					&& endOrStart.getHint().equals("地图上的点")) {
				startOrEnd.setHint("地图上的点");
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint("我的位置");
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals("地图上的点")
					&& endOrStart.getHint().equals("输入终点....")) {
				startOrEnd.setHint("输入起点....");
				startOrEnd.requestFocus();
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(null);
				endOrStart.setHint("地图上的点");
				endOrStart.setHintTextColor(Color.BLUE);
			} else if (startOrEnd.getHint().equals("输入起点....")
					&& endOrStart.getHint().equals("地图上的点")) {
				startOrEnd.setHint("地图上的点");
				startOrEnd.setSelectAllOnFocus(true);
				startOrEnd.setHintTextColor(Color.BLUE);
				endOrStart.setHint("输入终点....");
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
			if (startOrEnd.getHint().equals("我的位置")// 起点是我的位置，终点自定义
					&& ("").equals(startOrEnd.getText().toString())
					&& !("").equals(endOrStart.getText().toString())) {

				end = endOrStart.getText().toString();
				start = "";

			} else if (endOrStart.getHint().equals("我的位置")// 起点自定义，终点是我的位置
					&& ("").equals(endOrStart.getText().toString())
					&& !("").equals(startOrEnd.getText().toString())) {

				start = startOrEnd.getText().toString();
				end = "";

			} else if (endOrStart.getHint().equals("我的位置")// 起点是地图上的点，终点是我的位置
					&& ("").equals(endOrStart.getText().toString())
					&& ("").equals(startOrEnd.getText().toString())
					&& startOrEnd.getHint().equals("地图上的点")) {

				start = "pick";
				end = "myLoc";
			} else if (startOrEnd.getHint().equals("我的位置")// 起点是我的位置，终点是地图上的点
					&& ("").equals(endOrStart.getText().toString())
					&& ("").equals(startOrEnd.getText().toString())
					&& endOrStart.getHint().equals("地图上的点")) {

				start = "myLoc";
				end = "pick";
			} else if (!("").equals(startOrEnd.getText().toString())// 起点自定义，终点是地图上的点
					&& ("").equals(endOrStart.getText().toString())
					&& endOrStart.getHint().equals("地图上的点")) {

				start = startOrEnd.getText().toString();
				end = "pick";
			} else if (!("").equals(endOrStart.getText().toString())// 起点是地图上的点，终点自定义
					&& ("").equals(startOrEnd.getText().toString())
					&& startOrEnd.getHint().equals("地图上的点")) {

				end = endOrStart.getText().toString();
				start = "pick";
			} else {// 起/终点均自定义
				start = startOrEnd.getText().toString();
				end = endOrStart.getText().toString();
			}
			if ("".equals(find_the_way)) {
				Toast.makeText(mContext, "请选择路线搜索方式！", Toast.LENGTH_SHORT)
						.show();
			} else {
				i = new Intent(mContext, RouteActivity.class);
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
				if (flag.equals("pick")) {
					i.putExtra("pickLat", lat);
					i.putExtra("pickLon", lon);
					i.putExtra("pick", "pick");
					i.putExtra("completeAdd", completeAdd);
				}

				if (("").equals(start) && ("").equals(end)) {
					Toast.makeText(mContext, "请输入值", Toast.LENGTH_LONG).show();
				} else {
					i.putExtra("start", start);
					i.putExtra("end", end);

					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					mContext.startActivity(i);
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
			dialogTitle = "设置起点";
		} else if (flag.equals("end")) {
			dialogTitle = "设置终点";
		}
		String[] items = { "使用我的位置", "在地图上选取" };
		AlertDialog dialog = new AlertDialog.Builder(mContext)
				.setTitle(dialogTitle)
				.setItems(items, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case 0: {
							if (flag.equals("start")) {
								startOrEnd.setHint("我的位置");
								startOrEnd.setHintTextColor(Color.BLUE);
								startOrEnd.setSelectAllOnFocus(true);
								if (endOrStart.getHint().equals("地图上的点")) {
									endOrStart.setHint("地图上的点");

									endOrStart.setHintTextColor(Color.BLUE);
								} else {
									endOrStart.requestFocus();
									endOrStart.setHint("输入终点....");
									endOrStart.setHintTextColor(null);
								}

							} else if (flag.equals("end")) {
								endOrStart.setHint("我的位置");
								endOrStart.setHintTextColor(Color.BLUE);
								endOrStart.setSelectAllOnFocus(true);
								if (startOrEnd.getHint().equals("地图上的点")) {
									startOrEnd.setHint("地图上的点");

									startOrEnd.setHintTextColor(Color.BLUE);
								} else {
									startOrEnd.requestFocus();
									startOrEnd.setHint("输入起点....");
									startOrEnd.setHintTextColor(null);
								}

							}

							break;
						}
						case 1: {
							i = new Intent(mContext, PickupMapActivity.class);
							if (startOrEnd.getHint().equals("地图上的点")
									|| endOrStart.getHint().equals("地图上的点")) {
								i.putExtra("flag", startOrendFlag);
								i.putExtra("mapFlag", "map");
								mContext.startActivity(i);
							} else if (startOrEnd.getHint().equals("我的位置")
									|| endOrStart.getHint().equals("我的位置")) {
								i.putExtra("flag", startOrendFlag);
								i.putExtra("mapFlag", "myLoc");
								mContext.startActivity(i);
							} else {
								i.putExtra("flag", startOrendFlag);
								mContext.startActivity(i);
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
		LayoutInflater lay = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.bus_spinner, null);
		if (flag.equals("bus")) {
			dialogTitle = "设置公交路线规划策略";
		} else if (flag.equals("route")) {
			dialogTitle = "设置驾车路线规划策略";
		}
		dialog = new AlertDialog.Builder(mContext).setTitle(dialogTitle)
				.setView(v)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialogfer, int which) {
						if (dialog != null && dialog.isShowing()) {
							dialog.dismiss();
						}

					}
				}).create();

		dialog.show();

		final Spinner bus_spinner = (Spinner) v.findViewById(R.id.bus_spinner);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
				android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		adapter.add("时间优先");
		if (flag.equals("bus")) {

			adapter.add("少换乘");
			adapter.add("少步行");
			adapter.add("非地铁 ");
		} else if (flag.equals("route")) {

			adapter.add("距离最短");
			adapter.add("费用最少");
		}

		bus_spinner.setAdapter(adapter);
		bus_spinner.setSelection(0);
		bus_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				drivingPolicy = bus_spinner.getItemAtPosition(arg2).toString();
				// if (dialog != null && dialog.isShowing()) {
				// dialog.dismiss();
				// }
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});

	}

}
