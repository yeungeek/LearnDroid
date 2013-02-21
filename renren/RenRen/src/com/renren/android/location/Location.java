package com.renren.android.location;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.location.SaxParser.OnSaxParserListener;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;

public class Location {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mLocation;

	private ImageView mFlip;
	private ImageView mCheckIn;
	private LinearLayout mNearBy;
	private LinearLayout mStart;
	private LinearLayout mEnd;
	private Button mCount;

	private OnOpenListener mOnOpenListener;
	private LocationClient mClient;
	private LocationClientOption mOption;

	public Location(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mLocation = LayoutInflater.from(context).inflate(R.layout.location,
				null);
		initLBS();
		findViewById();
		setListener();
	}

	private void findViewById() {
		mFlip = (ImageView) mLocation.findViewById(R.id.location_flip);
		mCheckIn = (ImageView) mLocation.findViewById(R.id.location_checkin);
		mNearBy = (LinearLayout) mLocation.findViewById(R.id.location_nearby);
		mStart = (LinearLayout) mLocation.findViewById(R.id.location_start);
		mEnd = (LinearLayout) mLocation.findViewById(R.id.location_end);
		mCount = (Button) mLocation.findViewById(R.id.location_nearbycount);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mCheckIn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mContext.startActivity(new Intent(mContext, CurrentLocation.class));
				mActivity.overridePendingTransition(R.anim.roll_up,
						R.anim.roll);
			}
		});
		mNearBy.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEnd.isShown()) {
					mContext.startActivity(new Intent(mContext, NearBy.class));
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
				}
			}
		});
		mClient.registerLocationListener(new BDLocationListener() {

			public void onReceivePoi(BDLocation arg0) {

			}

			public void onReceiveLocation(BDLocation arg0) {
				mApplication.mLocation = arg0.getAddrStr();
				mApplication.mLatitude = arg0.getLatitude();
				mApplication.mLongitude = arg0.getLongitude();
			}
		});
	}

	public void init() {
		if (RenRenData.mNearByResults.size() == 0) {
			new Thread(new Runnable() {
				public void run() {
					if (mApplication.mLatitude == 0
							&& mApplication.mLongitude == 0
							&& mApplication.mLocation == null) {
						mClient.start();
						mClient.requestLocation();
					}
					saxParser();
				}
			}).start();
		} else {
			handler.sendEmptyMessage(1);
		}
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
		mClient = new LocationClient(mContext, mOption);

	}

	private void saxParser() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();
			XMLReader reader = saxParser.getXMLReader();
			SaxParser parser = new SaxParser();
			parser.setOnSaxParserListener(new OnSaxParserListener() {

				public void start() {
					handler.sendEmptyMessage(0);
				}

				public void end() {
					handler.sendEmptyMessage(1);
				}
			});
			reader.setContentHandler(parser);
			InputStream inputStream = mContext.getAssets()
					.open("dailydeal.xml");
			reader.parse(new InputSource(inputStream));
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mStart.setVisibility(View.VISIBLE);
				mEnd.setVisibility(View.GONE);
				break;

			case 1:
				mStart.setVisibility(View.GONE);
				mEnd.setVisibility(View.VISIBLE);
				mCount.setText(RenRenData.mNearByResults.size() + "");
				break;
			}
		}

	};

	public View getView() {
		return mLocation;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
