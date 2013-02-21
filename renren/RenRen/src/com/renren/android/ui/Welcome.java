package com.renren.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.renren.android.BaseApplication;
import com.renren.android.R;

/**
 * ��ӭ����
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
		 * ����һ���ӳ��߳�
		 */
		new Thread(this).start();
	}

	public void run() {
		try {
			/**
			 * �ӳ�һ��ʱ��
			 */
			Thread.sleep(1000);
			/**
			 * ��ѯ����û�������½����AccessToken��������Ч,��ֱ�ӵ�ת����ʾ����,�����ת����������
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
