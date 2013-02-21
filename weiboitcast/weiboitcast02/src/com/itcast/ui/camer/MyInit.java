package com.itcast.ui.camer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;

public class MyInit extends Activity{
    ImageView iv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		iv=new ImageView(this);
		this.setContentView(iv);
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		Log.d("ok", "----------------ok1");
		if(data.getByteArrayExtra("pic")!=null)
		{  byte dat[]=data.getByteArrayExtra("pic");
		Log.d("ok", "----------------ok1"+dat.length);
			Bitmap bm=BitmapFactory.decodeByteArray(dat, 0,dat.length);
			iv.setImageBitmap(bm);
			setContentView(iv);
		}
//		Bundle extras = data.getExtras();
//	    Bitmap b = (Bitmap) extras.get("data");
//		ImageView img = new ImageView(this);
//		img.setImageBitmap(b);
		

	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		Intent it=new Intent(this,CamerActivity.class);
		this.startActivityForResult(it,22);
//		
//		Intent i = new Intent("android.media.action.IMAGE_CAPTURE");
//		startActivityForResult(i, Activity.DEFAULT_KEYS_DIALER);
		return super.onKeyDown(keyCode, event);
		

	}
	
	

}
