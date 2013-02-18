package com.anhuioss.crowdroid.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.info.LocationInfo;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLine;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKRoute;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionInfo;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRoutePlan;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;

public class RouteActivity extends BasicMenuActivity implements OnClickListener {

	MapController mMapController;// 得到mMapView的控制权,可以用它控制和驱动平移和

	MKSearch mKSearch;

	MapView mMapView;// 地图控件

	ImageButton showMap;

	LinearLayout top;

	TextView searchPoi;

	TextView searchRoute;
	// 搜索方式
	String tag;

	String startAdd;

	String endAdd;

	Bundle bundle;

	MKPlanNode startNode = new MKPlanNode();
	MKPlanNode endNode = new MKPlanNode();

	double gecodeLat = 0;

	double gecodeLon = 0;

	private ListView list;
	ArrayList<MKPoiInfo> sugesstionList = new ArrayList<MKPoiInfo>();

	MKPoiInfo sugesstionInfo = new MKPoiInfo();

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	private List<LocationInfo> rounterLineInfoList = new ArrayList<LocationInfo>();

	private SimpleAdapter adapter;

	SharedPreferences currentMsg;

	AlertDialog dialog;

	GeoPoint geoPoint;

	String dialogTitle = "";
	// 方法调用标识
	int flag;
	// 起始点标识
	int startOrend;

	private List<Map<String, Object>> transitData = new ArrayList<Map<String, Object>>();

	ArrayList<MKTransitRoutePlan> transitList = new ArrayList<MKTransitRoutePlan>();

	MKTransitRoutePlan plan = new MKTransitRoutePlan();

	BusAdapter busAdapter;
	// 设置路线规划策略
	String drivingPolicy;

	int drivingPolicy_int;
	// 路线距离显示信息
	LinearLayout routemsg;

	private TextView msg;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private String rounterMessage = "";
	// 地图上的第一个点
	int pickLat;

	int pickLon;

	String completePickAdd;// 地图上点的地址
	// 地图上的第二个点
	int pickSecendLat;

	int pickSecendLon;

	String completeAddSecend;// 地图上点的地址
	// 标识是否选用了地图上的点
	String pick = "";

	int pickFlag = 3;// 地图上的点的起/终点标识

	String map = "";// 标识选择了几个地图上的点（“”/myLoc--> 选取了地图上的一个点；map-->选取了地图上的两个点）

	private BMapManager mapManager = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_mylocation);
		findViews();
		super.initMapActivity(CrowdroidApplication.getInstance()
				.getMapManager());
		mapManager = CrowdroidApplication.getInstance().getMapManager();
		if (mapManager != null) {
			mapManager.start();
			initMap();
		}
	}

	public void findViews() {
		mMapView = (MapView) findViewById(R.id.bmapsView);
		showMap = (ImageButton) findViewById(R.id.showMap);
		searchRoute = (TextView) findViewById(R.id.searchRoute);
		searchPoi = (TextView) findViewById(R.id.searchPoi);
		searchRoute.setText(R.string.menu_search_route);
		searchPoi.setText(R.string.menu_poi_search);
		top = (LinearLayout) findViewById(R.id.top);
		showMap.setOnClickListener(this);
		routemsg = (LinearLayout) findViewById(R.id.routemag);
		msg = (TextView) findViewById(R.id.msg);
		searchRoute.setOnClickListener(this);
		searchPoi.setOnClickListener(this);

		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);

		headerHome.setOnClickListener(this);
		headerBack.setOnClickListener(this);

		headerName.setText(R.string.discovery_lbs_name);
		headerHome.setBackgroundResource(R.drawable.main_home);

	}

	public void initMap() {

		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapView.setDrawOverlayWhenZooming(true);
		mMapController.setZoom(16); // 设置地图zoom级别
		// mMapView.setTraffic(true);// 实时交通信息

		mKSearch = new MKSearch();
		mKSearch.init(CrowdroidApplication.getInstance().getMapManager(),
				new MySearchListener());
		currentMsg = getSharedPreferences("currentMsg", 0);

	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo info, int error) {
			if (error != 0) {
				String str = String.format("错误号：%d", error);
				Log.e("error", str);
				return;
			}
			gecodeLat = info.geoPt.getLatitudeE6() / 1e6;

			gecodeLon = info.geoPt.getLongitudeE6() / 1e6;

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(RouteActivity.this, R.string.poi_result_tip,
						Toast.LENGTH_SHORT).show();
				return;
			}

			MKRoute route = res.getPlan(0).getRoute(0);
			int distanceM = route.getDistance();
			String distanceKm = String.valueOf(distanceM / 1000) + "."
					+ String.valueOf(distanceM % 1000);
			// if (startAdd.equals("")) {
			// startAdd = "当前位置";
			// }
			// if (endAdd.equals("")) {
			// endAdd = "当前位置";
			// }
			rounterMessage = "驾车路线：" + startAdd + "-->" + endAdd + "("
					+ drivingPolicy + ")距离:" + distanceKm + "公里";
			routemsg.setVisibility(View.VISIBLE);
			msg.setText(rounterMessage);
			RouteOverlay routeOverlay = new RouteOverlay(RouteActivity.this,
					mMapView);

			routeOverlay.setData(route);
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.invalidate();

			mMapView.getController().animateTo(res.getStart().pt);

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int arg1, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(RouteActivity.this, R.string.poi_result_tip,
						Toast.LENGTH_LONG).show();
				return;
			}
			routemsg.setVisibility(View.GONE);
			if (res.getCurrentNumPois() > 0) {

				sugesstionList.clear();
				sugesstionList = res.getAllPoi();
				data.clear();
				ArrayList<Map<String, Object>> addDatas = new ArrayList<Map<String, Object>>();
				for (MKPoiInfo info : sugesstionList) {
					Map<String, Object> map;
					map = new HashMap<String, Object>();
					map.put("name", info.name);
					map.put("add", info.address);
					map.put("geopoint", info.pt);
					addDatas.add(map);
				}
				for (Map<String, Object> addData : addDatas) {
					data.add(addData);
				}
				// // Notify
				// adapter.notifyDataSetChanged();

				popAwindow("poi");

			} else {

			}

		}

		@Override
		public void onGetRGCShareUrlResult(String arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int error) {
			if (error != 0 || result == null) {
				Toast.makeText(RouteActivity.this, R.string.poi_result_tip,
						Toast.LENGTH_SHORT).show();
				return;
			}
			routemsg.setVisibility(View.GONE);
			rounterLineInfoList.clear();
			busAdapter = new BusAdapter(RouteActivity.this, result);
			popAwindow("bus");
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(RouteActivity.this, R.string.poi_result_tip,
						Toast.LENGTH_SHORT).show();
				return;
			}
			MKRoute route = res.getPlan(0).getRoute(0);
			int distanceM = route.getDistance();
			String distanceKm = String.valueOf(distanceM / 1000) + "."
					+ String.valueOf(distanceM % 1000);
			// if (startAdd.equals("")) {
			// startAdd = "当前位置";
			// }
			// if (endAdd.equals("")) {
			// endAdd = "当前位置";
			// }
			rounterMessage = "步行路线：" + startAdd + "-->" + endAdd + "(距离:"
					+ distanceKm + "公里)";
			routemsg.setVisibility(View.VISIBLE);
			msg.setText(rounterMessage);

			RouteOverlay routeOverlay = new RouteOverlay(RouteActivity.this,
					mMapView);

			routeOverlay.setData(res.getPlan(0).getRoute(0));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.invalidate();

			mMapView.getController().animateTo(res.getStart().pt);

		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		bundle = getIntent().getExtras();
		if (bundle.containsKey("find_the_way")) {
			tag = bundle.getString("find_the_way");

		}
		if (bundle.containsKey("start")) {
			startAdd = bundle.getString("start");

		}
		if (bundle.containsKey("end")) {
			endAdd = bundle.getString("end");

		}
		if (bundle.containsKey("drivingPolicy")) {
			drivingPolicy = bundle.getString("drivingPolicy");
			if (drivingPolicy.equals(getResources().getString(
					R.string.policy_EBUS_TIME_FIRST))) {
				drivingPolicy_int = MKSearch.ECAR_TIME_FIRST;
			} else if (drivingPolicy.equals(getResources().getString(
					R.string.policy_ECAR_DIS_FIRST))) {
				drivingPolicy_int = MKSearch.ECAR_DIS_FIRST;
			} else if (drivingPolicy.equals(getResources().getString(
					R.string.policy_ECAR_FEE_FIRST))) {
				drivingPolicy_int = MKSearch.ECAR_FEE_FIRST;
			} else if (drivingPolicy.equals(getResources().getString(
					R.string.policy_EBUS_TRANSFER_FIRST))) {
				drivingPolicy_int = MKSearch.EBUS_TRANSFER_FIRST;
			} else if (drivingPolicy.equals(getResources().getString(
					R.string.policy_EBUS_WALK_FIRST))) {
				drivingPolicy_int = MKSearch.EBUS_WALK_FIRST;
			} else if (drivingPolicy.equals(getResources().getString(
					R.string.policy_EBUS_NO_SUBWAY))) {
				drivingPolicy_int = MKSearch.EBUS_NO_SUBWAY;
			}

		}
		if (bundle.containsKey("pickLat")) {
			pickLat = bundle.getInt("pickLat");

		}
		if (bundle.containsKey("pickLon")) {
			pickLon = bundle.getInt("pickLon");

		}

		if (bundle.containsKey("completeAdd")) {
			completePickAdd = bundle.getString("completeAdd");

		}
		if (bundle.containsKey("pickSecendLat")) {
			pickSecendLat = bundle.getInt("pickSecendLat");

		}
		if (bundle.containsKey("pickSecendLon")) {
			pickSecendLon = bundle.getInt("pickSecendLon");

		}

		if (bundle.containsKey("completeAddSecend")) {
			completeAddSecend = bundle.getString("completeAddSecend");

		}
		if (bundle.containsKey("pick")) {
			pick = bundle.getString("pick");
		}
		if (bundle.containsKey("map")) {
			map = bundle.getString("map");
		}
		if (pick.equals("")) {
			if (endAdd.equals("")) {
				endAdd = "当前位置";
				dialogTitle = getResources().getString(
						R.string.route_dialog_edit_start);
				flag = 0;
				startOrend = 0;
				mKSearch.poiSearchInCity("", startAdd);
			} else if (startAdd.equals("")) {
				startAdd = "当前位置";
				dialogTitle = getResources().getString(
						R.string.route_dialog_edit_end);
				flag = 0;
				startOrend = 1;
				mKSearch.poiSearchInCity("", endAdd);
			} else {
				dialogTitle = getResources().getString(
						R.string.route_dialog_edit_start);
				flag = 1;
				startOrend = 2;
				mKSearch.poiSearchInCity("", startAdd);
			}
		} else if (pick.equals("pick")) {
			if (startAdd.equals("pick") && endAdd.equals("myLoc")) {// 起点是地图上的点，终点是我的位置
				startNode.pt = new GeoPoint(pickLat, pickLon);
				startNode.name = completePickAdd;
				endNode.pt = new GeoPoint(currentMsg.getInt("lat", 0),
						currentMsg.getInt("lon", 0));
				endNode.name = currentMsg.getString("completeAdd", "");
				startAdd = startNode.name;
				// endAdd = endNode.name;
				endAdd = "当前位置";
				if (tag.equals("bus")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.transitSearch(currentMsg.getString("city", ""),
							startNode, endNode);
				} else if (tag.equals("route")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.drivingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				} else if (tag.equals("walk")) {
					mKSearch.walkingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				}
			} else if (startAdd.equals("myLoc") && endAdd.equals("pick")) {// 起点是我的位置，终点是地图上的点

				startNode.pt = new GeoPoint(currentMsg.getInt("lat", 0),
						currentMsg.getInt("lon", 0));
				startNode.name = currentMsg.getString("completeAdd", "");
				endNode.pt = new GeoPoint(pickLat, pickLon);
				endNode.name = completePickAdd;
				// startAdd = startNode.name;
				startAdd = "当前位置";
				endAdd = endNode.name;
				if (tag.equals("bus")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.transitSearch(currentMsg.getString("city", ""),
							startNode, endNode);
				} else if (tag.equals("route")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.drivingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				} else if (tag.equals("walk")) {
					mKSearch.walkingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				}
			} else if (startAdd.equals("pick") && !endAdd.equals("pick")) {// 起点是地图上的位置，终点自定义

				startNode.pt = new GeoPoint(pickLat, pickLon);
				startNode.name = completePickAdd;
				startAdd = startNode.name;

				dialogTitle = getResources().getString(
						R.string.route_dialog_edit_end);
				pickFlag = 1;
				mKSearch.poiSearchInCity("", endAdd);

			} else if (endAdd.equals("pick") && !startAdd.equals("pick")) {// 起点自定义，终点地图上的点

				endNode.pt = new GeoPoint(pickLat, pickLon);
				endNode.name = completePickAdd;
				endAdd = endNode.name;
				dialogTitle = getResources().getString(
						R.string.route_dialog_edit_start);
				pickFlag = 0;
				mKSearch.poiSearchInCity("", startAdd);

			} else if (startAdd.equals("pick") && endAdd.equals("pick")) {// 起点自定义，终点地图上的点
				startNode.name = completePickAdd;
				startNode.pt = new GeoPoint(pickLat, pickLon);
				endNode.pt = new GeoPoint(pickSecendLat, pickSecendLon);
				endNode.name = completeAddSecend;
				startAdd = startNode.name;
				endAdd = endNode.name;
				if (tag.equals("bus")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.transitSearch(currentMsg.getString("city", ""),
							startNode, endNode);
				} else if (tag.equals("route")) {
					mKSearch.setTransitPolicy(drivingPolicy_int);
					mKSearch.drivingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				} else if (tag.equals("walk")) {
					mKSearch.walkingSearch(currentMsg.getString("city", ""),
							startNode, currentMsg.getString("city", ""),
							endNode);

				}

			}
		}

	}

	@Override
	protected void onPause() {

		if (mapManager != null) {
			mapManager.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {

		if (mapManager != null) {
			mapManager.start();
		}
		super.onResume();
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		Intent i;
		switch (v.getId()) {
		case R.id.showMap: {
			if (top.getVisibility() == 0) {
				showMap.setImageResource(R.drawable.handle_down);
				top.setVisibility(View.GONE);
			} else if (top.getVisibility() == 8) {
				showMap.setImageResource(R.drawable.handle_up);
				top.setVisibility(View.VISIBLE);
			}
			break;
		}
		case R.id.searchRoute: {
			i = new Intent(RouteActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			break;

		}
		case R.id.searchPoi: {
			i = new Intent(RouteActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			i = new Intent(RouteActivity.this, HomeTimelineActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
			finish();
			break;
		}
		default:
			break;
		}
	}

	private void openSearchRounterDialog() {
		RouteDialog routeDialog = new RouteDialog(RouteActivity.this, "", "",
				"");

		Window window = routeDialog.getWindow();
		window.setGravity(Gravity.TOP);

		WindowManager.LayoutParams lp = window.getAttributes();

		lp.width = lp.FILL_PARENT;

		window.setAttributes(lp);

		routeDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		routeDialog.show();
	}

	private void popAwindow(String flag) {

		LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = lay.inflate(R.layout.pop, null);
		if (flag.equals("bus")) {
			dialogTitle = getResources().getString(R.string.bus_scheme) + "("
					+ drivingPolicy + ")";
		}
		dialog = new AlertDialog.Builder(RouteActivity.this)
				.setTitle(dialogTitle).setView(v).create();
		dialog.show();

		list = (ListView) v.findViewById(R.id.pop_list);
		if (flag.equals("poi")) {
			adapter = new SimpleAdapter(this, data, R.layout.pop_list_item,
					new String[] { "name", "add" }, new int[] { R.id.name,
							R.id.add });
			list.setAdapter(adapter);
		} else if (flag.equals("bus")) {

			list.setAdapter(busAdapter);
		}

		list.setOnItemClickListener(listClickListener);

	}

	OnItemClickListener listClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long arg3) {
			if (startOrend == 0 || startOrend == 1) {
				startNode.name = sugesstionList.get(position).name;
				startNode.pt = sugesstionList.get(position).pt;
				geoPoint = new GeoPoint(currentMsg.getInt("lat", 0),
						currentMsg.getInt("lon", 0));
				endNode.name = currentMsg.getString("completeAdd", "");
				endNode.pt = geoPoint;
				Log.e("start", startNode.name);
				Log.e("end", endNode.name);

			} else if (startOrend == 2 && flag == 1) {
				startNode.name = sugesstionList.get(position).name;
				startNode.pt = sugesstionList.get(position).pt;
				Log.e("startName", startNode.name);
				Log.e("startPt", startNode.pt.toString());
			}
			if (pickFlag == 1) {// 地图上一点的标识
				endNode.pt = sugesstionList.get(position).pt;
				endNode.name = sugesstionList.get(position).name;
				endAdd = endNode.name;
				flag = 0;
				startOrend = 1;
			} else if (pickFlag == 0) {
				startNode.pt = sugesstionList.get(position).pt;
				startNode.name = sugesstionList.get(position).name;
				startAdd = startNode.name;
				flag = 0;
				startOrend = 0;
				Log.e("startOrendPick", String.valueOf(startOrend));
			}
			if (tag.equals("bus")) {
				if (flag == 0) {
					Log.e("startOrend", String.valueOf(startOrend));
					mKSearch.setTransitPolicy(drivingPolicy_int);
					if (startOrend == 0) {// 起点自定义，终点当前位置/地图上的点
						mKSearch.transitSearch(
								currentMsg.getString("city", ""), startNode,
								endNode);
					} else if (startOrend == 1) {// 终点自定义，起点当前位置/地图上的点
						mKSearch.transitSearch(
								currentMsg.getString("city", ""), endNode,
								startNode);
					} else if (startOrend == 2) {// 起点，终点均自定义

						endNode.name = sugesstionList.get(position).name;
						endNode.pt = sugesstionList.get(position).pt;
						Log.e("endName", endNode.name);
						Log.e("endPt", endNode.pt.toString());
						mKSearch.transitSearch(
								currentMsg.getString("city", ""), startNode,
								endNode);
					}

				} else if (flag == 1) {
					dialogTitle = getResources().getString(
							R.string.route_dialog_edit_end);
					flag = 0;
					mKSearch.poiSearchInCity("", endAdd);

				}

			} else if (tag.equals("route")) {
				if (flag == 0) {
					mKSearch.setDrivingPolicy(drivingPolicy_int);
					if (startOrend == 0) {
						mKSearch.drivingSearch(
								currentMsg.getString("city", ""), startNode,
								currentMsg.getString("city", ""), endNode);
					} else if (startOrend == 1) {
						mKSearch.drivingSearch(
								currentMsg.getString("city", ""), endNode,
								currentMsg.getString("city", ""), startNode);
					} else if (startOrend == 2) {// 起点，终点均自定义

						endNode.name = sugesstionList.get(position).name;
						endNode.pt = sugesstionList.get(position).pt;
						Log.e("endName", endNode.name);
						Log.e("endPt", endNode.pt.toString());

						mKSearch.drivingSearch(
								currentMsg.getString("city", ""), startNode,
								currentMsg.getString("city", ""), endNode);

					}

				} else if (flag == 1) {
					dialogTitle = getResources().getString(
							R.string.route_dialog_edit_end);
					flag = 0;
					mKSearch.poiSearchInCity("", endAdd);
				}

			} else if (tag.equals("walk")) {
				if (flag == 0) {
					if (startOrend == 0) {
						mKSearch.walkingSearch(
								currentMsg.getString("city", ""), startNode,
								currentMsg.getString("city", ""), endNode);
					} else if (startOrend == 1) {
						mKSearch.walkingSearch(
								currentMsg.getString("city", ""), endNode,
								currentMsg.getString("city", ""), startNode);
					} else if (startOrend == 2) {// 起点，终点均自定义

						endNode.name = sugesstionList.get(position).name;
						endNode.pt = sugesstionList.get(position).pt;
						Log.e("endName", endNode.name);
						Log.e("endPt", endNode.pt.toString());
						mKSearch.walkingSearch(
								currentMsg.getString("city", ""), startNode,
								currentMsg.getString("city", ""), endNode);
					}

				} else if (flag == 1) {
					dialogTitle = getResources().getString(
							R.string.route_dialog_edit_end);
					flag = 0;
					mKSearch.poiSearchInCity("", endAdd);
				}

			}

			if (dialog != null) {
				dialog.dismiss();
			}
		}
	};

	class BusAdapter extends BaseAdapter {
		private MKTransitRouteResult res;
		private LayoutInflater mInflater;

		public BusAdapter(Context context, MKTransitRouteResult res) {
			this.res = res;
			this.mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return res.getNumPlan();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder = null;
			if (convertView == null) {
				holder = new HolderView();
				convertView = mInflater.inflate(R.layout.bus_list_item, null);
				holder.txt = (TextView) convertView
						.findViewById(R.id.list_item_txt);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}
			String lineInfo = "";
			// 得到解决方案
			MKTransitRoutePlan routePlan = res.getPlan(position);
			// 公交线路
			MKLine mkLine = routePlan.getLine(0);
			lineInfo += "乘坐：" + mkLine.getTitle();
			// 公交车上下车站点
			MKPoiInfo mkOnPoiInfo = mkLine.getGetOnStop();
			MKPoiInfo mkOffPoiInfo = mkLine.getGetOffStop();

			lineInfo += "\n\t\t\t\t从<font color='red'>" + mkOnPoiInfo.name
					+ "</font>上车，在<font color='red'>" + mkOffPoiInfo.name
					+ "</font>下车";

			String rounterStr = "公交路线：乘坐"
					+ mkLine.getTitle().substring(0,
							mkLine.getTitle().indexOf("(")) + "["
					+ mkOnPoiInfo.name + "->" + mkOffPoiInfo.name + "]";

			if (routePlan.getNumLines() > 0) {
				// 循环当前方案公交路线
				for (int i = 1; i < routePlan.getNumLines(); i++) {
					// 公交线路
					mkLine = routePlan.getLine(i);
					lineInfo += "\n换乘：" + mkLine.getTitle();
					mkOnPoiInfo = mkLine.getGetOnStop();
					mkOffPoiInfo = mkLine.getGetOffStop();

					lineInfo += "\n\t\t\t\t从<font color='red'>"
							+ mkOnPoiInfo.name
							+ "</font>上车，在<font color='red'>"
							+ mkOffPoiInfo.name + "</font>下车";
					rounterStr = rounterStr
							+ ";换乘"
							+ mkLine.getTitle().substring(0,
									mkLine.getTitle().indexOf("(")) + "["
							+ mkOnPoiInfo.name + "->" + mkOffPoiInfo.name + "]";
				}
			}

			LocationInfo locationInfo = new LocationInfo();
			locationInfo.setRounterLine(rounterStr);
			rounterLineInfoList.add(locationInfo);

			holder.txt.setText(Html.fromHtml(lineInfo));
			convertView.setOnClickListener(new BusListOnClick(position, res));
			return convertView;
		}
	}

	class HolderView {
		public TextView txt;
	}

	class BusListOnClick implements OnClickListener {
		private int index;
		private MKTransitRouteResult res;

		public BusListOnClick(int index, MKTransitRouteResult res) {
			this.index = index;
			this.res = res;
		}

		@Override
		public void onClick(View v) {
			TransitOverlay routeOverlay = new TransitOverlay(
					RouteActivity.this, mMapView);
			routeOverlay.setData(res.getPlan(index));
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(routeOverlay);
			mMapView.invalidate();
			mMapView.getController().animateTo(res.getStart().pt);

			rounterMessage = rounterLineInfoList.get(index).getRounterLine();
			routemsg.setVisibility(View.VISIBLE);
			msg.setText(rounterMessage);
			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

		}
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MapConstant.MAP_MENU_SHARE: {

			try {
				String screenShotPath = shareScreenShot(mMapView);
				Toast.makeText(RouteActivity.this,
						R.string.share + screenShotPath, Toast.LENGTH_LONG)
						.show();
				Intent intent = new Intent(RouteActivity.this,
						SendMessageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("action", "new");
				bundle.putString("target", "");
				bundle.putString("filePath", screenShotPath);
				bundle.putString("location-message", rounterMessage);
				intent.putExtras(bundle);
				startActivity(intent);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		}
		case MapConstant.MAP_MENU_ROUNTER: {
			// 路綫寻找
			Intent i;
			i = new Intent(RouteActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			openSearchRounterDialog();
			break;
		}
		case MapConstant.MAP_MENU_NEAR: {
			// 附近查找
			Intent i = new Intent(RouteActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case MapConstant.MAP_MENU_CURRENT: {
			// 附近查找
			Intent i = new Intent(RouteActivity.this, MyLocationActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case MapConstant.MAP_MENU_CLEAR: {
			mMapView.getOverlays().clear();
			routemsg.setVisibility(View.GONE);
			break;

		}
		case MapConstant.MAP_MENU_BACK: {
			// Logout
			Intent home = new Intent(RouteActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
			break;

		}
		default: {
			break;
		}
		}
		return super.onMenuItemSelected(featureId, item);
	}

}
