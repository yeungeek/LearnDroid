package com.itcast.ui;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.itcast.logic.WeiboActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class MoreActivity extends WeiboActivity implements OnItemClickListener {
	private ListView listView;
	private List<Map<String, Object>> list;
	SimpleAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.moreitems);
		listView = (ListView)findViewById(R.id.moreItemsListView);
		list = new ArrayList<Map<String, Object>>();
		listAdd(R.drawable.moreitems_setting_icon,"设置");
		listAdd(R.drawable.moreitems_accountmanage_icon,"账号管理");
		listAdd(R.drawable.moreitems_officialweibo_icon,"官方微博");
		listAdd(R.drawable.moreitems_feedback_icon,"意见反馈");
		listAdd(R.drawable.moreitems_about_icon,"关于");
		adapter = new SimpleAdapter(this,list,R.layout.moreitemsview,new String[]{"icon","text"},new int[]{R.id.ImageView01,R.id.TextView01});
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
	}
	private void listAdd(int icon,String text){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("icon", icon);
		map.put("text", text);
		list.add(map);
	}
	public void success() {

	}
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch(position){
		case 0 :
			Toast.makeText(this, "设置", 3000).show();
			break;
		case 1:
			Toast.makeText(this, "账号管理", 3000).show();
			break;
		case 2:
			Toast.makeText(this, "官方微博", 3000).show();
			break;
		case 3:
			Intent it = new Intent(this,NewWeiboActivity.class);
			it.putExtra("context", "#Android客服端意见反馈#");
			startActivity(it);
			Toast.makeText(this, "意见反馈", 3000).show();
			break;
		case 4:
			Toast.makeText(this, "关于", 3000).show();
			break;
		}
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
