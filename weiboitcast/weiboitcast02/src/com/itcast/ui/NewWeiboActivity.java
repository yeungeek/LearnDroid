package com.itcast.ui;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.ui.camer.CamerActivity;
import com.itcast.util.GPSPoint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NewWeiboActivity extends WeiboActivity{
    private EditText etBlog;
    private Button  btBack;
    private Button  btSend;
    private Button btPic;
    private Button btGPS;
    private ImageView ivpic;
    private byte picdat[];//图片字节流
    private static final int BT_TEXT=1;//文字微博
    private static final int BT_PIC=2;//图片微博
    private static final int BT_GPS=3;//GPS微博
    private static final int BT_PIC_GPS=4;//图片和GPS微博
    private double gpspoint[];
    private int blogType=BT_TEXT;//微博类型
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.newblog);
		ivpic=(ImageView)this.findViewById(R.id.ivCameraPic);
		btPic=(Button)this.findViewById(R.id.btGallery);
		btPic.setOnClickListener(new OnClickListener()
		{
         	@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		      Intent it=new Intent("android.media.action.IMAGE_CAPTURE");
		      NewWeiboActivity.this.startActivityForResult(it, 0);
//         		Intent it=new Intent(NewWeiboActivity.this,CamerActivity.class);
//         		NewWeiboActivity.this.startActivityForResult(it,22);
			}
		}
		);
		btGPS=(Button)this.findViewById(R.id.btGPS);
		btGPS.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				gpspoint=GPSPoint.getGPSPoint(NewWeiboActivity.this);
			   Toast.makeText(NewWeiboActivity.this, "获取当前位置\n精度"
			   +gpspoint[0]+"\n纬度"+gpspoint[1],500).show();
			   blogType=BT_GPS;
			}
			
		}
		);
		
		View title=this.findViewById(R.id.title);
		btBack=(Button)title.findViewById(R.id.title_bt_left);
	   btBack.setBackgroundResource(R.drawable.title_back);
	   btBack.setText("返回");
	   btBack.setOnClickListener(new OnClickListener()
	   {
       	@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	   }
	   );
	   etBlog=(EditText)this.findViewById(R.id.etBlog);
	   btSend=(Button)title.findViewById(R.id.title_bt_right);
	   btSend.setBackgroundResource(R.drawable.title_new);
	   btSend.setText("发表");
	   btSend.setOnClickListener(new OnClickListener()
	   {
     	@Override
		public void onClick(View v) {
     		if(etBlog.length()>140)
     		{
     		Toast.makeText(NewWeiboActivity.this, "字数超过140，无法发送", 400).show();
     		return;
     		}
			 HashMap hm=new HashMap();
			 hm.put("msg", etBlog.getText().toString());
			 int tsType=0;
			 switch(blogType)
			 {case BT_TEXT:
				  tsType=TaskType.TS_NEW_WEIBO;
				  break;
			  case BT_PIC:
				  tsType=TaskType.TS_NEW_WEIBO_PIC;
				  hm.put("picdata", picdat);
				  break;
			  case BT_GPS:
				  tsType=TaskType.TS_NEW_WEIBO_GPS;
				  hm.put("lat",gpspoint[0]);
				  hm.put("lon",gpspoint[1]);
				  break;
			  case BT_PIC_GPS:
				  tsType=TaskType.TS_NEW_WEIBO_PIC_GPS;
				  hm.put("picdata", picdat);
				  hm.put("lat",0);
				  hm.put("lon",0);
				  break;
			 }
			 Task ts=new Task(tsType,hm);
			 MainService.newTask(ts);//添加发表微博的任务
		}
		   
	   }
	   );
	   //字数提醒
	  final TextView tvlabel=(TextView)this.findViewById(R.id.tvLabel);
	   tvlabel.setText("剩余文字140");
	   //
	   etBlog.addTextChangedListener(new TextWatcher()
	   {
     	@Override
		public void afterTextChanged(Editable s) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
            tvlabel.setText("剩余字数"+(140-etBlog.length()));
		}
	   }
	   );
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
		int type=(Integer)param[0];
		int result=(Integer)param[1];
		if(result==1)
		{
			Toast.makeText(this, "微博发表成功", 500).show();
			finish();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Bitmap bmp=(Bitmap)data.getExtras().get("data");
		
//		if(data.getByteArrayExtra("pic")!=null)
//		{  byte dat[]=data.getByteArrayExtra("pic");
//	    	Log.d("ok", "----------------ok1"+dat.length);
//			Bitmap bm=BitmapFactory.decodeByteArray(dat, 0,dat.length);
//			ivpic.setImageBitmap(bm);
//			ivpic.setVisibility(View.VISIBLE);
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.JPEG, 30, bos);
			this.picdat=bos.toByteArray();//将图片转化为字节数组
			this.blogType=BT_PIC;
//		}
		
		
	}
 
	
}
