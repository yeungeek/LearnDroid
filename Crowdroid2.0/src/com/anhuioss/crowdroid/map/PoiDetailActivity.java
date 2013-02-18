package com.anhuioss.crowdroid.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiInfo;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;

public class PoiDetailActivity extends MapActivity implements LocationListener,
		OnClickListener {

	// 搜索范围下拉列表
	TextView range;

	ImageButton pop;

	int rang = 5000;

	// POI搜索结果列表
	ListView poiList;

	ArrayList<MKPoiInfo> searchPoiList = new ArrayList<MKPoiInfo>();

	MKPoiInfo poiInfo = new MKPoiInfo();

	ArrayList<Map<String, Object>> addDatas;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private SimpleAdapter adapter;

	LocationListener mLocationListener;

	GeoPoint geoPoint;

	MKSearch mKSearch;
	// POI搜索关键字
	String type;

	SharedPreferences currentMsg;

	double lat = 0;

	double lon = 0;

	private ProgressDialog progerssDialog;

	DiatanceMethed distance;

	double poiDistance;

	double poiDistance2;

	TextView phone;

	TextView postCode;

	TextView selectLoc;
	// 目的地
	TextView targetTest;

	String selectMode = "";
	// 地图上选择的POI点
	int poiPickPointLat;

	int poiPickPointLon;

	String poiCity;

	String poiDistrict;

	String poiStreet;

	String poiCompleteAdd;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_poidetail);
		CrowdroidApplication.getInstance().getMapManager().start();
		findViews();
	}

	@Override
	protected void onStart() {
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("type")) {
			type = bundle.getString("type");
		}
		if (bundle.containsKey("poiLat")) {
			poiPickPointLat = bundle.getInt("poiLat");
		}
		if (bundle.containsKey("poiLon")) {
			poiPickPointLon = bundle.getInt("poiLon");
		}
		if (bundle.containsKey("poiCity")) {
			poiCity = bundle.getString("poiCity");
		}
		if (bundle.containsKey("poiDistrict")) {
			poiDistrict = bundle.getString("poiDistrict");
		}
		if (bundle.containsKey("poiStreet")) {
			poiStreet = bundle.getString("poiStreet");
		}
		if (bundle.containsKey("poiCompleteAdd")) {
			poiCompleteAdd = bundle.getString("poiCompleteAdd");
		}
		if (bundle.containsKey("selectMode")) {
			selectMode = bundle.getString("selectMode");

		}
		if (selectMode.equals(getResources().getString(R.string.menu_my_loc))) {
			selectLoc.setText(getResources().getString(R.string.menu_my_loc));

			geoPoint = new GeoPoint(currentMsg.getInt("lat", 0),
					currentMsg.getInt("lon", 0));
		} else if (selectMode.equals(getResources().getString(
				R.string.poi_type_map))) {
			selectLoc.setText(poiStreet);
			geoPoint = new GeoPoint(poiPickPointLat, poiPickPointLon);
		}

		targetTest.setText(type);
		mKSearch.poiSearchNearBy(type, geoPoint, rang);
		// progerssDialog = new ProgressDialog(PoiDetailActivity.this);
		// progerssDialog.show();
		super.onStart();
	}

	@Override
	protected void onPause() {
		CrowdroidApplication.getInstance().getMapManager().getLocationManager()
				.removeUpdates(mLocationListener);
		CrowdroidApplication.getInstance().getMapManager().stop();
		TimelineActivity.isBackgroundNotificationFlag=true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		CrowdroidApplication.getInstance().getMapManager().getLocationManager()
				.requestLocationUpdates(mLocationListener);
		CrowdroidApplication.getInstance().getMapManager().start();
		TimelineActivity.isBackgroundNotificationFlag=false;
		super.onResume();
	}

	public void findViews() {
		selectLoc = (TextView) findViewById(R.id.select_loc);
		targetTest = (TextView) findViewById(R.id.targetTest);
		range = (TextView) findViewById(R.id.range);
		pop = (ImageButton) findViewById(R.id.popupwindow);

		// range = (Spinner) findViewById(R.id.range);
		poiList = (ListView) findViewById(R.id.list_view);
		mKSearch = new MKSearch();
		mKSearch.init(CrowdroidApplication.getInstance().getMapManager(),
				new MySearchListener());

		currentMsg = getSharedPreferences("currentMsg", 0);

		distance = new DiatanceMethed();

		phone = (TextView) findViewById(R.id.phone);

		postCode = (TextView) findViewById(R.id.postcode);
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					lat = loc.getLatitude();
					lon = loc.getLongitude();
					Log.e("_lat", String.valueOf(loc.getLatitude()));
					Log.e("_lon", String.valueOf(loc.getLongitude()));

				}

			}
		};
		pop.setOnClickListener(this);

	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			if (error != 0 || res == null) {
				Toast.makeText(PoiDetailActivity.this, R.string.poi_result_tip,
						Toast.LENGTH_LONG).show();
				return;
			}
			if (res.getCurrentNumPois() > 0) {

				searchPoiList.clear();
				searchPoiList = res.getAllPoi();
				data.clear();
				addDatas = new ArrayList<Map<String, Object>>();

				for (MKPoiInfo info : searchPoiList) {
					Map<String, Object> map;
					map = new HashMap<String, Object>();
					map.put("name", info.name);
					map.put("add", "地址：" + info.address);
					if (!("").equals(info.phoneNum)) {

						map.put("phone", "电话号码：" + info.phoneNum);

					}
					if (!("").equals(info.postCode)) {

						map.put("postcode", "邮编号码：" + info.postCode);
					}

					if (selectMode.equals(getResources().getString(
							R.string.menu_my_loc))) {
						poiDistance = distance.GetShortDistance(
								currentMsg.getInt("lon", 0) / 1E6,
								currentMsg.getInt("lat", 0) / 1E6,
								info.pt.getLongitudeE6() / 1E6,
								info.pt.getLatitudeE6() / 1E6);
					} else if (selectMode.equals(getResources().getString(
							R.string.poi_type_map))) {
						poiDistance = distance.GetShortDistance(
								poiPickPointLon / 1E6, poiPickPointLat / 1E6,
								info.pt.getLongitudeE6() / 1E6,
								info.pt.getLatitudeE6() / 1E6);
					}

					map.put("distance", distance.formatDistance(poiDistance));
					addDatas.add(map);
				}

				for (Map<String, Object> addData : addDatas) {

					data.add(addData);
				}
				showPoi();

			} else if (res.getCityListNum() > 0) {

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
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		//
	}

	public void showPoi() {

		adapter = new SimpleAdapter(
				this,
				data,
				R.layout.poidetail_item,
				new String[] { "name", "add", "phone", "postCode", "distance" },
				new int[] { R.id.name, R.id.add, R.id.phone, R.id.postcode,
						R.id.distance });
		poiList.setAdapter(adapter);
		poiList.setOnItemClickListener(listClickListener);

	}

	private OnItemClickListener listClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent i = new Intent(PoiDetailActivity.this, PoiMapActivity.class);
			i.putExtra("type", type);// 关键字
			i.putExtra("rang", rang);// 范围
			i.putExtra("geoLat", searchPoiList.get(arg2).pt.getLatitudeE6());// 选中位置的维度
			i.putExtra("geoLon", searchPoiList.get(arg2).pt.getLongitudeE6());// 选中位置的径度
			i.putExtra("name", searchPoiList.get(arg2).name);// 位置信息
			if (selectMode.equals(getResources()
					.getString(R.string.menu_my_loc))) {
				i.putExtra("lat", lat);// 起点纬度
				i.putExtra("lon", lon);// 起点径度
			} else if (selectMode.equals(getResources().getString(
					R.string.poi_type_map))) {
				i.putExtra("lat", poiPickPointLat / 1E6);// 起点纬度
				i.putExtra("lon", poiPickPointLon / 1E6);// 起点径度
			}

			i.putExtra("city", currentMsg.getString("city", ""));// 当前城市
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);

		}
	};

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		location.getLatitude();
		location.getLongitude();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		openRange();
	}

	private void openRange() {
		String[] items = { "默认", "500米", "1000米", "1500米", "2000米" };

		AlertDialog dialog = new AlertDialog.Builder(PoiDetailActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							rang = 5000;
							range.setText("默认");
							mKSearch.poiSearchNearBy(type, geoPoint, rang);
							break;
						}
						case 1: {
							rang = 500;
							range.setText("500");
							mKSearch.poiSearchNearBy(type, geoPoint, rang);
							break;
						}
						case 2: {
							rang = 1000;
							range.setText("1000");
							mKSearch.poiSearchNearBy(type, geoPoint, rang);
							break;
						}
						case 3: {
							rang = 1500;
							range.setText("1500");
							mKSearch.poiSearchNearBy(type, geoPoint, rang);
							break;
						}
						case 4: {
							rang = 2000;
							range.setText("2000");
							mKSearch.poiSearchNearBy(type, geoPoint, rang);
							break;
						}

						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

}
