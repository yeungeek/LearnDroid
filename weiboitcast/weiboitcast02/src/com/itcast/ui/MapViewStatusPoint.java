package com.itcast.ui;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.itcast.logic.MainService;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.TextView;

public class MapViewStatusPoint extends MapActivity{
    private MapController mc;
    private double lat;
    private double lon;
    private Bitmap userIcon;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		 lat=this.getIntent().getExtras().getDouble("lat");
		 lon=this.getIntent().getExtras().getDouble("lon");
		 int uid=this.getIntent().getExtras().getInt("uid");
		 //��ʼ���û���ͷ��
		 BitmapDrawable bd=MainService.allicon.get(uid);
		 if(bd!=null)
		 {
			 userIcon=bd.getBitmap();
		 }else
		 {
			 userIcon=BitmapFactory.decodeResource(this.getResources(), R.drawable.portrait);
		 }
		MapView mv=new MapView(this,"0l_Vj_oqst7nljeSnvfxtPqmnaMCAWsQKQU_u8g");
		this.setContentView(mv);
	    mv.setTraffic(true);
	    mv.setBuiltInZoomControls(true);
	    mv.setClickable(true);
	    //�趨��ͼ��ʾ��ǰ��γ��
		GeoPoint gp=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
	    mc=mv.getController();
	    mc.setZoom(12);
	    mc.animateTo(gp);
	    //ʵ�ָ�������ʾ�û���ͷ��
	    mv.getOverlays().add(new userIconOverlayer());
	}
	class userIconOverlayer extends Overlay
	{

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			super.draw(canvas, mapView, shadow);
			GeoPoint gp=new GeoPoint((int)(lat*1E6),(int)(lon*1E6));
			Point p=new Point(); 
			//��ȫ��GPS����ת��Ϊ��ͼ����������
			mapView.getProjection().toPixels(gp, p);
			//�ڵ�ͼ�������������ͷ��
			canvas.drawBitmap(userIcon, p.x,p.y,new Paint());
		}
		
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
    
}
