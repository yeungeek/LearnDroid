package com.itcast.logic;

import com.itcast.ui.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

public abstract class WeiboActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		MainService.addActivity(this);
	}
    public abstract void init();
    public abstract void refresh(Object ... param);
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		MainService.removeActivity(this);
	}
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		init();
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			AlertDialog.Builder  ab=new AlertDialog.Builder(this);
			ab.setTitle("退出提示");
			ab.setMessage("您真的要退出吗?");
			ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
			       Intent it=new Intent(WeiboActivity.this,MainService.class);
			       WeiboActivity.this.stopService(it);
				   finish();
			       android.os.Process.killProcess(android.os.Process.myPid());
				}
			});
			ab.setNegativeButton("取消",null);
			ab.create().show();
			
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(1, 1, 1, "设置").setIcon(R.drawable.setting);
		menu.add(1, 2, 2, "账号");
		menu.add(1, 3, 3, "官方微博");
		menu.add(2, 4, 4, "意见反馈").setIcon(R.drawable.menu_contact);
		menu.add(2, 5, 5, "关于").setIcon(R.drawable.aboutweibo);
		menu.add(2, 6, 6, "退出").setIcon(R.drawable.menu_exit);
		
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		Toast.makeText(this, "您选中了"+item.getItemId(), 500).show();
		switch(item.getItemId())
		{case 1://设置
		 case 2://账号
		 case 3://官方微博
		 case 4://意见反馈
		 case 5://关于
		 case 6://退出 
		}
		return super.onMenuItemSelected(featureId, item);
	}
	
	

}
