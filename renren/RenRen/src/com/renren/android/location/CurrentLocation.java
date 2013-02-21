package com.renren.android.location;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;

public class CurrentLocation extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private EditText mSearch;
	private ListView mDisplay;
	private CurrentLocationAdapter mAdapter;
	private LocationClient mClient;
	private LocationClientOption mOption;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.currentlocation);
		mApplication = (BaseApplication) getApplication();
		initLBS();
		findViewById();
		setListener();
		mAdapter = new CurrentLocationAdapter(this);
		mDisplay.setAdapter(mAdapter);
		if (RenRenData.mCurrentLocationResults.isEmpty()) {
			if (mApplication.mLatitude == 0 && mApplication.mLongitude == 0
					&& mApplication.mLocation == null) {
				mClient.start();
				mClient.requestLocation();
			} else {
				new Async().execute();
			}
		}
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.currentlocation_back);
		mSearch = (EditText) findViewById(R.id.currentlocation_search);
		mDisplay = (ListView) findViewById(R.id.currentlocation_display);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});
		mClient.registerLocationListener(new BDLocationListener() {

			public void onReceivePoi(BDLocation arg0) {

			}

			public void onReceiveLocation(BDLocation arg0) {
				mApplication.mLocation = arg0.getAddrStr();
				mApplication.mLatitude = arg0.getLatitude();
				mApplication.mLongitude = arg0.getLongitude();
				new Async().execute();
			}
		});
	}

	private void initLBS() {
		mOption = new LocationClientOption();
		mOption.setOpenGps(true);
		mOption.setCoorType("bd09ll");
		mOption.setAddrType("all");
		mOption.setScanSpan(100);
		mOption.disableCache(true);
		mOption.setPoiNumber(20);
		mOption.setPoiDistance(1000);
		mOption.setPoiExtraInfo(true);
		mClient = new LocationClient(this, mOption);

	}

	private class Async extends AsyncTask<String, Integer, String> {
		String httpurl = "http://api.map.baidu.com/place/search?&query=%E9%93%B6%E8%A1%8C&location="
				+ mApplication.mLatitude
				+ ","
				+ mApplication.mLongitude
				+ "&radius=1000&output=json&key=37492c0ee6f924cb5e934fa08c6b1676";

		protected void onPreExecute() {
			super.onPreExecute();
		}

		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONObject object = new JSONObject(result);
				JSONArray array = object.getJSONArray("results");
				CurrentLocationResult results = null;
				for (int i = 0; i < array.length(); i++) {
					results = new CurrentLocationResult();
					results.setName(array.getJSONObject(i).getString("name"));
					results.setLatitude(array.getJSONObject(i)
							.getJSONObject("location").getDouble("lat"));
					results.setLongitude(array.getJSONObject(i)
							.getJSONObject("location").getDouble("lng"));
					results.setAddress(array.getJSONObject(i).getString(
							"address"));
					results.setCount((int) (Math.random() * 10000));
					RenRenData.mCurrentLocationResults.add(results);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			mAdapter.notifyDataSetChanged();
		}

		protected String doInBackground(String... params) {
			return httpConnection(httpurl);
		}
	}

	private String httpConnection(String httpUrl) {
		String result = "";
		try {
			URL url = new URL(httpUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());
			BufferedReader buffer = new BufferedReader(in);
			String inputLine = null;
			while ((inputLine = buffer.readLine()) != null) {
				result += inputLine;
			}
			in.close();
			conn.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
		}
		return super.onKeyDown(keyCode, event);
	}
}
