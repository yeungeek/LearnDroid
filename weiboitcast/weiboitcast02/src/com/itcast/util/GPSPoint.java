package com.itcast.util;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

public class GPSPoint {

	public static double[] getGPSPoint(Context context)
	{
		//�õ�GPS�豸�ķ���
		LocationManager locationManager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		  Criteria mCriteria01 = new Criteria();
	      mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
	      mCriteria01.setAltitudeRequired(false);
	      mCriteria01.setBearingRequired(false);
	      mCriteria01.setCostAllowed(true);
	      mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
////
		String provider = locationManager.getBestProvider(mCriteria01, false);
////
		Location location = locationManager.getLastKnownLocation(provider);
//		��ȡGPS��������������Ϣ
		Double latitude = location.getLatitude() ;
		Double longitude = location.getLongitude() ;
		double point[]={latitude,
        		longitude};
        return point;
	}
}
