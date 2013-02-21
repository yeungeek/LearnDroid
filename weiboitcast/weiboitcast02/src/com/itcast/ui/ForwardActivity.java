package com.itcast.ui;

import java.util.HashMap;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;

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


public class ForwardActivity extends Activity implements TextWatcher, OnClickListener{
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
		try{
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
		titleTv.setText("转发微博");
		back = (Button)findViewById(R.id.title_bt_left);
		back.setText(R.string.imageviewer_back);
		back.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});
		
		send = (Button)findViewById(R.id.title_bt_right);
		send.setText(R.string.title_button_send);
		send.setOnClickListener(this);
		editText = (EditText)findViewById(R.id.etCmtReason);
		editText.addTextChangedListener(this);
		tvCmtLabel =(TextView)findViewById(R.id.tvCmtLabel);
		tvCmtLabel.setText("剩余字数140");

		rb_forward = (RadioButton)findViewById(R.id.rb_forward);
		rb_forward.setText("作为评论发布");
	}

	



	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		int i=140-arg0.length();
		tvCmtLabel.setText("剩余字数"+i);
	}


	public void onClick(View v) {
        HashMap hm=new HashMap();
        String comment = editText.getText().toString();
		hm.put("msg", comment);
		hm.put("id", String.valueOf(id));
		if(rb_forward.isChecked()){
		  hm.put("newc",new Boolean(true));
		}else
		{ hm.put("newc",new Boolean(false));
		}
		Task ts=new Task(TaskType.TASK_WEIBO_FORWARD,hm);
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
}
