package com.anhuioss.crowdroid.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.HomeTimelineActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.baidu.mapapi.GeoPoint;
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
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.PoiOverlay;

public class PoiMapActivity extends BasicMenuActivity implements
		OnClickListener {

	MapController mMapController;// 得到mMapView的控制权,可以用它控制和驱动平移和

	private TextView alertMessage;

	GeoPoint geoPoint;

	MKSearch mKSearch;

	MapView mMapView;// 地图控件

	ImageButton showMap;

	LinearLayout top;

	TextView searchPoi;

	TextView searchRoute;
	Bundle bundle;
	// 关键字
	String type;
	// 搜索范围
	int rang;
	// 当前位置的经纬度
	double lon;

	double lat;
	// 确定地址的经纬度
	int geoLon;

	int geoLat;
	// 城市
	String city;

	String name;

	View mPopView = null; //

	TextView addName;

	List<MKPoiInfo> geolist = new ArrayList<MKPoiInfo>();

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_mylocation);
		CrowdroidApplication.getInstance().getMapManager().start();
		super.initMapActivity(CrowdroidApplication.getInstance()
				.getMapManager());

		findViews();
		initMap();
	}

	public void findViews() {
		alertMessage = (TextView) findViewById(R.id.msg);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		showMap = (ImageButton) findViewById(R.id.showMap);
		searchRoute = (TextView) findViewById(R.id.searchRoute);
		searchPoi = (TextView) findViewById(R.id.searchPoi);
		searchRoute.setText(R.string.menu_search_route);
		searchPoi.setText(R.string.menu_poi_search);
		top = (LinearLayout) findViewById(R.id.top);
		showMap.setOnClickListener(this);
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

		// 气泡
		mPopView = super.getLayoutInflater()
				.inflate(R.layout.map_popview, null);
		mMapView.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.CENTER));
		mPopView.setVisibility(View.GONE);

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
		public void onGetPoiResult(MKPoiResult res, int arg1, int error) {
			if (error != 0 || res == null) {

				return;
			}

			if (res.getCurrentNumPois() > 0) {
				// PoiOverlay是baidu map api提供的用于显示POI的Overlay
				PoiOverItemT poioverlay = new PoiOverItemT(PoiMapActivity.this,
						mMapView);
				// 设置搜索到的POI数据
				geolist = res.getAllPoi();
				poioverlay.setData(res.getAllPoi());
				mMapView.getOverlays().clear();
				// 在地图上显示PoiOverlay（将搜索到的兴趣点标注在地图上）
				mMapView.getOverlays().add(poioverlay);

				// 刷新地图
				mMapView.invalidate();

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

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub

		bundle = getIntent().getExtras();

		if (bundle.containsKey("type")) {
			type = bundle.getString("type");

		}
		if (bundle.containsKey("rang")) {
			rang = bundle.getInt("rang");

		}
		if (bundle.containsKey("geoLat")) {
			geoLat = bundle.getInt("geoLat");

		}
		if (bundle.containsKey("geoLon")) {
			geoLon = bundle.getInt("geoLon");

		}
		if (bundle.containsKey("lat")) {
			lat = bundle.getDouble("lat");

		}
		if (bundle.containsKey("lon")) {
			lon = bundle.getDouble("lon");

		}
		if (bundle.containsKey("city")) {
			city = bundle.getString("city");

		}
		if (bundle.containsKey("name")) {
			name = bundle.getString("name");

		}
		geoPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));

		mKSearch.poiSearchNearBy(type, geoPoint, rang);
		if (geoLat != 0 && geoLon != 0) {
			GeoPoint pt = new GeoPoint(geoLat, geoLon);
			mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, pt,
					MapView.LayoutParams.BOTTOM_CENTER));
			addName = (TextView) mPopView.findViewById(R.id.addname);
			addName.setText(name);
			mPopView.setVisibility(View.VISIBLE);
			mMapView.getController().animateTo(pt);
			alertMessage.setVisibility(View.VISIBLE);
			alertMessage.setText(type + ":" + name);
		}
		super.onStart();
	}

	@Override
	protected void onPause() {
		CrowdroidApplication.getInstance().getMapManager().stop();
		super.onPause();
	}

	@Override
	protected void onResume() {
		CrowdroidApplication.getInstance().getMapManager().start();
		super.onResume();
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
			i = new Intent(PoiMapActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case R.id.searchPoi: {
			i = new Intent(PoiMapActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			i = new Intent(PoiMapActivity.this, HomeTimelineActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(i);
			finish();
			break;
		}
		default:
			break;
		}
	}

	private void openRounterDialog() {
		RouteDialog routeDialog = new RouteDialog(PoiMapActivity.this, "", "",
				"");
		Window window = routeDialog.getWindow();
		window.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = LayoutParams.FILL_PARENT;
		window.setAttributes(lp);
		routeDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		routeDialog.show();
	}

	class PoiOverItemT extends PoiOverlay {

		private PoiMapActivity poi;

		public PoiOverItemT(PoiMapActivity arg0, MapView arg1) {
			super(arg0, arg1);
			this.poi = arg0;

		}

		@Override
		protected boolean onTap(int index) {

			GeoPoint pt = geolist.get(index).pt;
			poi.mMapView.updateViewLayout(poi.mPopView,
					new MapView.LayoutParams(MapView.LayoutParams.WRAP_CONTENT,
							MapView.LayoutParams.WRAP_CONTENT, pt,
							MapView.LayoutParams.BOTTOM_CENTER));
			poi.mPopView.setVisibility(View.VISIBLE);
			addName = (TextView) poi.mPopView.findViewById(R.id.addname);
			addName.setText(geolist.get(index).name);
			alertMessage.setVisibility(View.VISIBLE);
			alertMessage.setText(type + ";" + geolist.get(index).name + "");
			return true;
		}

		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {

			poi.mPopView.setVisibility(View.GONE);

			return super.onTap(arg0, arg1);
		}

		@Override
		public void setData(ArrayList<MKPoiInfo> data) {

			super.setData(data);
		}

	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MapConstant.MAP_MENU_SHARE: {

			try {
				String screenShotPath = shareScreenShot(mMapView);
				Toast.makeText(PoiMapActivity.this,
						R.string.share + screenShotPath, Toast.LENGTH_LONG)
						.show();
				if (!"".equals(screenShotPath)) {
					Intent intent = new Intent(PoiMapActivity.this,
							SendMessageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("action", "new");
					bundle.putString("target", "");
					bundle.putString("filePath", screenShotPath);
					bundle.putString("location-message", alertMessage.getText()
							.toString());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		case MapConstant.MAP_MENU_ROUNTER: {
			// 寻找路线
			Intent i;
			i = new Intent(PoiMapActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			// openRounterDialog();
			break;
		}
		case MapConstant.MAP_MENU_NEAR: {
			// 附近查找
			Intent i = new Intent(PoiMapActivity.this, PoiActivity.class);
			startActivity(i);
			break;

		}
		case MapConstant.MAP_MENU_CURRENT: {
			// 附近查找
			Intent i = new Intent(PoiMapActivity.this, MyLocationActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case MapConstant.MAP_MENU_CLEAR: {
			mMapView.removeView(mPopView);
			mMapView.getOverlays().clear();
			alertMessage.setVisibility(View.GONE);

			break;

		}
		case MapConstant.MAP_MENU_BACK: {
			// Logout
			Intent home = new Intent(PoiMapActivity.this,
					HomeTimelineActivity.class);
			home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(home);
			overridePendingTransition(R.anim.in_from_left, R.anim.out_to_right);
			finish();
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
