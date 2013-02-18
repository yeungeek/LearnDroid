package com.anhuioss.crowdroid.map;

import java.util.List;

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
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.Overlay;
import com.baidu.mapapi.Projection;

public class PickSecendMapActivity extends MapActivity implements
		LocationListener, OnClickListener {

	MapController mMapController;// 得到mMapView的控制权,可以用它控制和驱动平移和

	GeoPoint geoPoint;

	MKSearch mKSearch;

	RelativeLayout relative;

	Button back;

	Bitmap bitmap;

	GeoPoint pt;

	LocationListener mLocationListener;

	Projection projection;

	GeoPoint pickpt;

	View mPopView = null; //

	TextView popmsg;

	String startOrendFlag = "";

	PickupMapView mapview;

	int pickSecentPointLat;

	int pickSecentPointLon;

	String map = "";

	String pickSecentPointCity = "";

	String pickSecentPointDistrict = "";

	String pickSecentPointStreet = "";

	String pickSecentPointCompleteAdd = "";

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_pickmap);
		findViews();
		CrowdroidApplication.getInstance().getMapManager().start();
		super.initMapActivity(CrowdroidApplication.getInstance()
				.getMapManager());
		initMap();

	}

	public void findViews() {
		mapview = new PickupMapView(PickSecendMapActivity.this);
		projection = mapview.getProjection();
		relative = (RelativeLayout) findViewById(R.id.relative);
		relative.addView(mapview, new RelativeLayout.LayoutParams(
				LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);

	}

	public void initMap() {
		bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.pin);
		mMapController = mapview.getController();
		mapview.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mapview.setDrawOverlayWhenZooming(true);
		mapview.setSatellite(false);// 设置卫星图
		mapview.setDragMode(0);// 设置0：拖拽有动画
		mMapController.setZoom(16); // 设置地图zoom级别

		mKSearch = new MKSearch();
		mKSearch.init(CrowdroidApplication.getInstance().getMapManager(),
				new MySearchListener());
		mLocationListener = new LocationListener() {

			public void onLocationChanged(Location loc) {
				if (loc != null) {
					geoPoint = new GeoPoint((int) (loc.getLatitude() * 1E6),
							(int) (loc.getLongitude() * 1E6));

					mMapController.animateTo(geoPoint);
					mMapController.setCenter(geoPoint); // 设置地图中心点

					mapview.getOverlays().clear();

					addOverLay(geoPoint);

				}

			}
		};

		// 气泡
		mPopView = super.getLayoutInflater()
				.inflate(R.layout.pickmap_pop, null);
		mapview.addView(mPopView, new MapView.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, null,
				MapView.LayoutParams.CENTER));
		mPopView.setVisibility(View.GONE);

	}

	public class MySearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo info, int error) {
			if (error != 0) {
				String str = String.format("错误号：%d", error);
				Log.e("error", str);
				return;
			}
			if (info.addressComponents.city == null) {
				pickSecentPointCity = "";
			} else {
				pickSecentPointCity = info.addressComponents.city;

			}
			if (info.addressComponents.district == null) {
				pickSecentPointDistrict = "";
			} else {
				pickSecentPointDistrict = info.addressComponents.district;

			}
			if (info.addressComponents.street == null) {
				pickSecentPointStreet = "";
			} else {
				pickSecentPointStreet = info.addressComponents.street;
			}

			pickSecentPointLat = info.geoPt.getLatitudeE6();
			pickSecentPointLon = info.geoPt.getLongitudeE6();
			pickSecentPointCompleteAdd = pickSecentPointCity
					+ pickSecentPointDistrict + pickSecentPointStreet;

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
	protected void onStart() {
		Bundle bundle = getIntent().getExtras();
		if (bundle.containsKey("flag")) {
			startOrendFlag = bundle.getString("flag");
		}
		if (bundle.containsKey("mapFlag")) {
			map = bundle.getString("mapFlag");
		}
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

	void addOverLay(GeoPoint pt) {
		List<Overlay> overlays = mapview.getOverlays();
		MyOverlay overlay = new MyOverlay(mapview, bitmap);
		overlay.setGeoPoint(pt);
		overlays.add(overlay);// 添加自定义overlay
	}

	public class MyOverlay extends Overlay {
		private MapView mapView;
		private Projection projection;
		private Point point;// 屏幕对象的点
		private Bitmap bitmap;

		private GeoPoint geoPoint;// 经纬度点

		public void setGeoPoint(GeoPoint geoPoint) {
			this.geoPoint = geoPoint;
		}

		public MyOverlay(MapView mapView, Bitmap bitmap) {
			super();
			this.mapView = mapView;
			this.bitmap = bitmap;
		}

		@Override
		public void draw(Canvas canvas, MapView arg1, boolean arg2) {
			projection = mapView.getProjection();
			point = new Point();
			projection.toPixels(geoPoint, point);// 将GeoPoint 转换成point.
			int x = point.x - bitmap.getWidth() / 2;
			int y = point.y - bitmap.getHeight();

			canvas.drawBitmap(bitmap, x, y, new Paint());
		}

		@Override
		public boolean onTap(GeoPoint point, MapView arg1) {
			mKSearch.reverseGeocode(point);
			mapview.updateViewLayout(mPopView, new MapView.LayoutParams(
					MapView.LayoutParams.WRAP_CONTENT,
					MapView.LayoutParams.WRAP_CONTENT, point,
					MapView.LayoutParams.BOTTOM_CENTER));
			mPopView.setVisibility(View.VISIBLE);
			popmsg = (TextView) mPopView.findViewById(R.id.popmsg);
			if (startOrendFlag.equals("start")) {
				popmsg.setText(R.string.pick_start);
			} else {
				popmsg.setText(R.string.pick_end);
			}
			popmsg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent i = new Intent(PickSecendMapActivity.this,
							SearchDialogActivity.class);
					i.putExtra("flag", "pick");
					i.putExtra("startOrendDialogFlag", startOrendFlag);
					i.putExtra("map", map);
					i.putExtra("secendLat", pickSecentPointLat);
					i.putExtra("secendLon", pickSecentPointLon);
					i.putExtra("secendCompleteAdd", pickSecentPointCompleteAdd);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
					// openRounterDialog();

				}
			});

			return true;
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back: {
			finish();
			break;
		}

		default:
			break;
		}

	}

	private void openRounterDialog() {
		RouteDialog routeDialog = new RouteDialog(PickSecendMapActivity.this,
				"pick", startOrendFlag, map);
		Window window = routeDialog.getWindow();
		window.setGravity(Gravity.TOP);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.width = LayoutParams.FILL_PARENT;
		window.setAttributes(lp);
		routeDialog.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		routeDialog.show();

	}

	class PickupMapView extends MapView {

		public PickupMapView(Context arg0, AttributeSet arg1, int arg2) {
			super(arg0, arg1, arg2);
			// TODO Auto-generated constructor stub
		}

		public PickupMapView(Context arg0, AttributeSet arg1) {
			super(arg0, arg1);
			// TODO Auto-generated constructor stub
		}

		public PickupMapView(Context arg0) {
			super(arg0);
			// TODO Auto-generated constructor stub
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {
			// 获得屏幕点击的位置
			int x = (int) event.getX();
			int y = (int) event.getY();
			// 将像素坐标转为地址坐标
			pickpt = projection.fromPixels(x, y);
			mapview.getOverlays().clear();
			addOverLay(pickpt);
			return super.onTouchEvent(event);
		}

	}

}
