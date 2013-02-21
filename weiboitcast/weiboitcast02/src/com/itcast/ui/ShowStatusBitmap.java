package com.itcast.ui;

import java.util.HashMap;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;

public class ShowStatusBitmap extends WeiboActivity {
	private ImageView iv;
	private String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		iv=new ImageView(this);
		url=this.getIntent().getExtras().getString("url");
		this.setContentView(iv);
	}

	@Override
	public void init() {
		HashMap hm=new HashMap();
		hm.put("url", url);
		Task ts=new Task(TaskType.TS_GET_STATUS_PIC_ORI,hm);
		MainService.newTask(ts);
	}

	@Override
	public void refresh(Object... param) {
		 BitmapDrawable bd=(BitmapDrawable)param[1];
		 iv.setBackgroundDrawable(bd);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
    
}
