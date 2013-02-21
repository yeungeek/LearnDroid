package com.itcast.ui;

import java.util.HashMap;
import java.util.List;

import weibo4j.model.Status;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.ui.adapter.WeiboAdapter;

public class HuatiActivity extends WeiboActivity{
    private ListView lv;
    private String htmsg;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 lv=new ListView(this);
		 htmsg=this.getIntent().getData().getPath();
		this.setContentView(lv);
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		HashMap hm=new HashMap();
		hm.put("ht", htmsg.substring(1));
		Task ts=new Task(TaskType.TS_GET_HUATI,hm);
		MainService.newTask(ts);
	}

	@Override
	public void refresh(Object... param) {
		List<Status> alls=(List<Status>)param[1];
		WeiboAdapter wa=new WeiboAdapter(alls,this);
		lv.setAdapter(wa);
	}

}
