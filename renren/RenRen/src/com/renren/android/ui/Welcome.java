package com.renren.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.renren.android.BaseApplication;
import com.renren.android.R;

/**
 * 欢迎界面
 * 
 * @author rendongwei
 * 
 */
public class Welcome extends Activity implements Runnable {
	private BaseApplication mApplication;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		mApplication=(BaseApplication) getApplication();
		/**
		 * 启动一个延迟线程
		 */
		new Thread(this).start();
	}

	public void run() {
		try {
			/**
			 * 延迟一秒时间
			 */
			Thread.sleep(1000);
			/**
			 * 查询如果用户曾经登陆过且AccessToken存在且有效,则直接调转到显示界面,否则调转到引导界面
			 */
			if (mApplication.mRenRen.isAccessTokenExist()) {
				startActivity(new Intent(Welcome.this, DesktopActivity.class));
			} else {
				startActivity(new Intent(Welcome.this, Welcome_Guide.class));
			}
			finish();
		} catch (InterruptedException e) {

		}
	}
}
