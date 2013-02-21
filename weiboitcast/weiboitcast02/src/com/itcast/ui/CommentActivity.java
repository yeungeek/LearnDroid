package com.itcast.ui;

import java.util.HashMap;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


public class CommentActivity extends WeiboActivity implements TextWatcher, OnClickListener{
	private View title;
	private Button back,send;
	private TextView titleTv;
	private EditText editText;
	private TextView tvCmtLabel;
	private RadioButton rb_forward;
	private long id =0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		try{//获取要评论微博的ID
			id = getIntent().getExtras().getLong("id");
			if(id==0){
				finish();
			}
		}catch(Exception e){
			Log.e("CommentActivity","onCreate");
			finish();
		}
		
		setContentView(R.layout.comment);
		title = findViewById(R.id.title);
		titleTv = (TextView)findViewById(R.id.textView);
		titleTv.setText("微博评论");
		back = (Button)findViewById(R.id.title_bt_left);
		back.setText("返回");
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		send = (Button)findViewById(R.id.title_bt_right);
		send.setText("发表");
		send.setOnClickListener(this);
		editText = (EditText)findViewById(R.id.etCmtReason);
		editText.addTextChangedListener(this);
		tvCmtLabel =(TextView)findViewById(R.id.tvCmtLabel);
		tvCmtLabel.setText("剩余字数140");
		rb_forward = (RadioButton)findViewById(R.id.rb_forward);
		rb_forward.setText("同时转发到我的微博");
	}

	



	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int i=140-arg0.length();
		tvCmtLabel.setText("剩余字数"+i);
	}


	public void onClick(View v) {
        HashMap hm=new HashMap();
        //获取评论内容
        String comment = editText.getText().toString();
		hm.put("msg", comment);
		hm.put("id", String.valueOf(id));
		//是否要发表到我的微博 (转发)
		if(rb_forward.isChecked()){
		  hm.put("news",new Boolean(true));
		}else
		{
			hm.put("news", new Boolean(false));
		}
		Task ts=new Task(TaskType.TASK_NEW_WEIBO_COMMENT,hm);
		MainService.newTask(ts);
		finish();
	}
	

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}


	public void afterTextChanged(Editable arg0) {
	}


	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
	}





	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}





	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
		
	}
}
