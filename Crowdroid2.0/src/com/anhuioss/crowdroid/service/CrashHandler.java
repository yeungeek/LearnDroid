package com.anhuioss.crowdroid.service;

import java.lang.Thread.UncaughtExceptionHandler;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class CrashHandler implements UncaughtExceptionHandler {

	private final static String TAG = "UncaughtExceptionHandler";
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static CrashHandler mInstance;
	private Context mContext;
	private int index = 0;

	private CrashHandler() {
	}

	/** 获取CrashHandler实例 ,单例模式 */
	public static CrashHandler getInstance() {
		if (mInstance == null)
			mInstance = new CrashHandler();
		return mInstance;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable throwable) {
		boolean handleException = handleException(throwable);
		if (!handleException && mDefaultHandler != null) {
			// 如果用户没有处理则让系统默认的异常处理器来处理
			// mDefaultHandler.uncaughtException(thread, throwable);
		} else {
			// Sleep一会后结束程序
			// 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
			System.gc();
			ActivityManager activityManager = (ActivityManager) mContext
					.getSystemService(Context.ACTIVITY_SERVICE);

			activityManager.restartPackage(mContext.getPackageName());
			activityManager.killBackgroundProcesses(mContext.getPackageName());
			android.os.Process.killProcess(android.os.Process.myPid());

			System.exit(0);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return true;
		}
		final String msg = ex.getLocalizedMessage();
		// 使用Toast来显示异常信息
		// new Thread() {
		// @Override
		// public void run() {
		// if (index == 0) {
		// // Toast 显示需要出现在一个线程的消息队列中
		// // Looper.prepare();
		// // Toast.makeText(mContext, "Exception:" + msg,
		// // Toast.LENGTH_LONG).show();
		// // Toast.makeText(mContext, "1",
		// // Toast.LENGTH_LONG).show();
		// // ActivityManager activityMgr = (ActivityManager) mContext
		// // .getSystemService(Context.ACTIVITY_SERVICE);
		// //
		// // activityMgr.restartPackage(mContext.getPackageName());
		// //
		// // activityMgr.killBackgroundProcesses(mContext.getPackageName());
		// // android.os.Process.killProcess(android.os.Process.myPid());
		// // System.exit(0);
		// // showDialog();
		// // Looper.loop();
		// }
		// }
		// }.start();
		return true;
	}

	protected void showDialog() {
		// TODO Auto-generated method stub
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
				.setTitle("Alert").setMessage("异常问题")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ActivityManager activityMgr = (ActivityManager) mContext
								.getSystemService(Context.ACTIVITY_SERVICE);
						activityMgr.restartPackage(mContext.getPackageName());
						activityMgr.killBackgroundProcesses(mContext
								.getPackageName());
						android.os.Process.killProcess(android.os.Process
								.myPid());
						System.exit(0);
					}
				}).create();
		alertDialog.show();
		index = 1;
	}

}