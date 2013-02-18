package com.anhuioss.crowdroid.map;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class MyLocationActivity extends BasicMenuActivity implements
		LocationListener, OnClickListener {

	MapController mMapController;// 得到mMapView的控制权,可以用它控制和驱动平移和

	GeoPoint geoPoint;

	MKSearch mKSearch;

	LocationListener mLocationListener;

	MapView mMapView;// 地图控件

	ImageButton showMap;

	LinearLayout top;

	TextView searchPoi;

	TextView searchRoute;

	String tag;

	String startAdd;

	String endAdd;

	SharedPreferences currentMsg;

	SharedPreferences.Editor editor;

	private String currentLocAddress;

	Bitmap bitmap;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	ProgressDialog processDialog;

	private BMapManager mapManager = null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		// full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setLayoutView(R.layout.activity_mylocation);

		super.initMapActivity(CrowdroidApplication.getInstance()
				.getMapManager());

		// CrowdroidApplication.getInstance().getMapManager().start();
		findViews();
		mapManager = CrowdroidApplication.getInstance().getMapManager();
		if (mapManager != null) {
			mapManager.start();
			// mapManager.getLocationManager().enableProvider(
			// (int) MKLocationManager.MK_GPS_PROVIDER);
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

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		// mapManager = CrowdroidApplication.getInstance().getMapManager();
		// if (mapManager != null) {
		// mapManager.getLocationManager().requestLocationUpdates(
		// mLocationListener);
		// mapManager.start();
		// mapManager.getLocationManager().enableProvider(
		// (int) MKLocationManager.MK_GPS_PROVIDER);
		// initMap();
		// }

	}

	public void initMap() {
		processDialog = new ProgressDialog(MyLocationActivity.this);
		processDialog
				.setMessage(getResources().getString(R.string.loc_loading));
		processDialog.show();
		mMapController = mMapView.getController();
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapView.setDrawOverlayWhenZooming(true);
		mMapView.setSatellite(false);// 设置卫星图
		mMapView.setDragMode(0);// 设置0：拖拽有动画
		mMapController.setZoom(16); // 设置地图zoom级别
		// mMapView.setTraffic(true);// 实时交通信息

		mKSearch = new MKSearch();
		mKSearch.init(CrowdroidApplication.getInstance().getMapManager(),
				new MySearchListener());
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					if (processDialog != null && processDialog.isShowing()) {
						processDialog.dismiss();
					}
					geoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
							(int) (loc.getLongitude() * 1E6));

					mKSearch.reverseGeocode(geoPoint);
					mMapController.animateTo(geoPoint);
					mMapController.setCenter(geoPoint); // 设置地图中心点

					mMapView.getOverlays().clear();

					addOverLay();
					// 添加到MapView的覆盖物中
					// mMapView.getOverlays().add(new MyOverlay());
					Log.e("baidu_lat", String.valueOf(loc.getLatitude()));
					Log.e("baidu_lon", String.valueOf(loc.getLongitude()));

				}

			}
		};
		currentMsg = getSharedPreferences("currentMsg", 0);
		editor = currentMsg.edit();

	}

	@Override
	protected void onPause() {
		// CrowdroidApplication.getInstance().getMapManager().getLocationManager()
		// .removeUpdates(mLocationListener);
		// CrowdroidApplication.getInstance().getMapManager().stop();
		if (mapManager != null) {
			mapManager.getLocationManager().removeUpdates(mLocationListener);
			mapManager.stop();
		}
		TimelineActivity.isBackgroundNotificationFlag = true;
		super.onPause();
	}

	@Override
	protected void onResume() {
		// CrowdroidApplication.getInstance().getMapManager().getLocationManager()
		// .requestLocationUpdates(mLocationListener);
		// CrowdroidApplication.getInstance().getMapManager().start();
		if (mapManager != null) {
			mapManager.getLocationManager().requestLocationUpdates(
					mLocationListener);
			mapManager.start();
		}
		TimelineActivity.isBackgroundNotificationFlag = false;
		super.onResume();
	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo info, int error) {
			if (error != 0) {
				String str = String.format("错误号：%d", error);
				Log.e("error", str);
				return;
			}
			Log.e("current_lat", String.valueOf(info.geoPt.getLatitudeE6()));
			Log.e("current_lon", String.valueOf(info.geoPt.getLongitudeE6()));

			editor.putInt("lat", info.geoPt.getLatitudeE6());
			editor.putInt("lon", info.geoPt.getLongitudeE6());
			editor.putString("city", info.addressComponents.city);
			editor.putString("district", info.addressComponents.district);
			editor.putString("street", info.addressComponents.street);
			editor.putString("completeAdd", info.addressComponents.city
					+ info.addressComponents.district
					+ info.addressComponents.street);
			editor.commit();
			String myStreet;
			if (info.addressComponents.street != null) {
				myStreet = info.addressComponents.street;
			} else {
				myStreet = "";
			}
			currentLocAddress = String.format("位置：%s\r\n",
					info.addressComponents.city
							+ info.addressComponents.district + myStreet);
			Toast.makeText(MyLocationActivity.this, currentLocAddress,
					Toast.LENGTH_LONG).show();
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
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
			// TODO Auto-generated method stub

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
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		location.getLatitude();
		location.getLongitude();

	}

	/*
	 * VISIBLE:0 意思是可见的 INVISIBILITY:4 意思是不可见的，但还占着原来的空间 GONE:8
	 * 意思是不可见的，不占用原来的布局空间
	 */

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
			i = new Intent(MyLocationActivity.this, SearchDialogActivity.class);
			i.putExtra("flag", "");
			i.putExtra("startOrendDialogFlag", "");
			i.putExtra("map", "");
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			// openRounterDialog();
			break;
		}
		case R.id.searchPoi: {
			i = new Intent(MyLocationActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			i = new Intent(MyLocationActivity.this, HomeTimelineActivity.class);
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
		RouteDialog routeDialog = new RouteDialog(MyLocationActivity.this, "",
				"", "");
		Window window = routeDialog.getWindow();
		window.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = LayoutParams.FILL_PARENT;
		window.setAttributes(lp);
		routeDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		routeDialog.show();
	}

	void addOverLay() {
		List<Overlay> overlays = mMapView.getOverlays();
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
		MyOverlay overlay = new MyOverlay(MyLocationActivity.this, mMapView);
		overlay.enableCompass();
		overlay.enableMyLocation();
		overlays.add(overlay);// 添加自定义overlay
	}

	public class MyOverlay extends MyLocationOverlay {

		public MyOverlay(Context arg0, MapView arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void drawMyLocation(Canvas canvas, MapView mapView,
				Location lastFix, GeoPoint myLocation, long when) {
			try {
				Projection projection = mapView.getProjection();
				Point point = new Point();
				projection.toPixels(myLocation, point);

				int x = point.x - bitmap.getWidth() / 2;
				int y = point.y - bitmap.getHeight();
				canvas.drawBitmap(bitmap, x, y, new Paint());

			} catch (Exception e) {
				super.drawMyLocation(canvas, mapView, lastFix, myLocation, when);
			}
		}
	};

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case MapConstant.MAP_MENU_SHARE: {

			try {
				String screenShotPath = shareScreenShot(mMapView);
				Toast.makeText(MyLocationActivity.this,
						R.string.share + screenShotPath, Toast.LENGTH_LONG)
						.show();
				if (!"".equals(screenShotPath)) {
					Intent intent = new Intent(MyLocationActivity.this,
							SendMessageActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("action", "new");
					bundle.putString("target", "");
					bundle.putString("filePath", screenShotPath);
					bundle.putString("location-message", "当前所在"
							+ currentLocAddress);
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
			i = new Intent(MyLocationActivity.this, SearchDialogActivity.class);
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
			Intent i = new Intent(MyLocationActivity.this, PoiActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			break;

		}
		case MapConstant.MAP_MENU_CURRENT: {
			break;
		}
		case MapConstant.MAP_MENU_CLEAR: {

			break;

		}
		case MapConstant.MAP_MENU_BACK: {
			// Logout
			Intent home = new Intent(MyLocationActivity.this,
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
