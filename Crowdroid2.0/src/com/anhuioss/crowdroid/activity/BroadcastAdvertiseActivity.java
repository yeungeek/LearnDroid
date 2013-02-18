package com.anhuioss.crowdroid.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.data.info.BroadcastInfo;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.settings.BrowseModeActivity;
import com.anhuioss.crowdroid.util.FileService;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

public class BroadcastAdvertiseActivity extends Activity implements
		OnClickListener, OnTouchListener {

	private Context mContext;

	private TextView txtTitle;

	private WebView webContents;

	private WebView webPicture;

	private TextView txtTime;

	private CheckBox checkShowNext;

	private Button btnHistory;

	private Button btnOk;

	private BroadcastInfo broadcastInfo = null;

	private String broadcastType = "0";

	private String picUrlData = "";

	private FileService fileService = null;

	private String fileName = "notify.txt";

	private String historyMessage = "";

	private boolean isFromHistory = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_broadcast_advertisement);

		// Find Views
		txtTitle = (TextView) findViewById(R.id.broadcast_title);
		webContents = (WebView) findViewById(R.id.broadcast_content);
		webPicture = (WebView) findViewById(R.id.broadcast_picture);
		txtTime = (TextView) findViewById(R.id.broadcast_time);
		checkShowNext = (CheckBox) findViewById(R.id.broadcast_check);

		btnHistory = (Button) findViewById(R.id.broadcast_history);
		btnOk = (Button) findViewById(R.id.broadcast_ok);
		btnHistory.setOnClickListener(this);
		btnOk.setOnClickListener(this);

		// Web Settings
		webContents.setBackgroundColor(Color.TRANSPARENT);
		webContents.setVerticalScrollBarEnabled(false);
		webContents.setHorizontalScrollBarEnabled(false);

		webPicture.setBackgroundColor(Color.TRANSPARENT);
		webPicture.setVerticalScrollBarEnabled(false);
		webPicture.setHorizontalScrollBarEnabled(false);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		fileService = new FileService(getApplicationContext());

		isFromHistory = getIntent().getBooleanExtra(
				"broadcast-is-from-history", false);

		// if (!isFromHistory) {
		// read file
		readFile();
		// save file
		String saveMessage = getIntent().getExtras().getString(
				"broadcast-message");

		if (saveMessage != null && !saveMessage.equals("")) {
			saveFile(saveMessage);
		}
		// }
		broadcastInfo = (BroadcastInfo) getIntent().getSerializableExtra(
				"broadcast-info");
		if (broadcastInfo != null) {
			initViewContent();
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	private void readFile() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			try {
				if (fileIsExists()) {
					historyMessage = fileService.read(fileName);// read histroy
				} else {
					fileService.saveToSD(fileName, "");// 文件不存在则创建到sd
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveFile(String content) {
		try {
			// 判断SDCard是否存在并且可以读写
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {

				if (historyMessage.equals("")) {
					historyMessage = "[" + content + "]";
					fileService.save(fileName, historyMessage);
				} else if (!historyMessage.equals("")
						&& !historyMessage.contains(content)) {
					historyMessage = "[" + content + ","
							+ historyMessage.substring(1);

					fileService.save(fileName, historyMessage);
				}
			} else {
				Toast.makeText(getApplicationContext(), R.string.success,
						Toast.LENGTH_LONG).show();
			}

		} catch (FileNotFoundException e) {
			Toast.makeText(getApplicationContext(), R.string.error,
					Toast.LENGTH_LONG).show();
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.broadcast_ok: {
			close();
			break;
		}
		case R.id.broadcast_history: {
			Intent intent = new Intent(this, BroadcastHistoryActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("broadcast-history-message", historyMessage);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (v.getId()) {
		case R.id.broadcast_picture: {
			if (event.getAction() == MotionEvent.ACTION_UP) {

				Intent intent = new Intent();
				intent.setClass(BroadcastAdvertiseActivity.this,
						PreviewImageActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				Bundle bundle = new Bundle();
				bundle.putBoolean("broadcast-flag", true);
				bundle.putString("url", picUrlData.replace("small", "large"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		}
		}
		return false;
	}

	private void initViewContent() {

		if (isFromHistory) {
			btnHistory.setVisibility(View.INVISIBLE);
			btnHistory.setEnabled(false);
			btnOk.setText(R.string.trends_back);
		}

		broadcastType = broadcastInfo.getAdsType();

		checkShowNext.setVisibility(View.GONE);

		txtTitle.setText(broadcastInfo.getAdsTitle());

		String time = broadcastInfo.getAdsId();
		String formatTime = time.substring(0, 4) + "-" + time.substring(4, 6)
				+ "-" + time.substring(6, 8) + " " + time.substring(8, 10)
				+ ":" + time.substring(10, 12) + ":" + time.substring(12, 14);
		txtTime.setText(formatTime);

		webContents.loadDataWithBaseURL("about:blank",
				broadcastInfo.getAdsContent() + "<br/><br/>", "text/html",
				"utf-8", "");
		// webContents
		// .loadDataWithBaseURL(
		// "about:blank",
		// "iPhone 不单纯是具有电话功能的 iPod，它的有许多有魅力的地方。作为一个应用程序的开发者，我们首先来看看它的这些特征。首先，手机上的按键没有了，应用程序的按键，开关与电话的按键统一了起来。取而代之的是触摸屏技术，应用程序的开发者从此不再受按键的限制，可以自由的设计UI的风格。喜欢把按键放哪就放哪，即使不用按键，类似于PC应用程序的滚动条/拖动条等也可以在触摸屏上实现。iPhone 中已经提供了从按键到滚动条等一系列的UI控件。当然，你也可以不使用这些控件，而是自己自作独特的UI控件。制作iPhone应用程序需要的东西首先是开发环境，标注的配置是以下的环境： Mac 电脑（CPU要是Intel的）   Mac OS X v10.5（Leopard）或以上的版本 一台 iPhone 或者 iPod touch当然你也可以在windows或者linux上安装开发环境。iphonedevonlinux有详细的介绍，使用toolchain在Cygwin或者linux下进行iPhone的开发，如果你想省些钞票或者想更深入的了解 iPhone OS 那么不妨试试它。这一部分，我在以后的章节中会专门介绍。另外，开发所必要的软件，SDK,IDE等可以免费从Apple的网上下载。如果你想开发有GPS和照相机或者重力传感器功能的程序，那么需要iPhone 3G，其他一般的程序iPod touch就可以了。另外，如果想要把做成的程序发布出去，还需要到Apple Developer Connection花99美金登录。ocoa Touch与XcodeCocoa Touch刚才介绍了开发iPhone应用程序的时候，可以选择许多现成的UI控件。实际使用的过程当中，使用叫做「Cocoa Touch」的程序开发组件库，它类似于windows下开发时所用的MFC，.NET FrameWork。利用 Cocoa Touch，开发者不用考虑设备的特性和画面特性，就可以简单的构筑GUI。另外，利用Cocoa Touch，可以开发具有以下iPhone功能的应用程序。相功能利用照相机拍照，并将照片保存到iPhone。前位置取得功能要用iPhone取得当前位置，可以使用 GPS，无线LAN，基站等3种方式。应用程序不用考虑使用哪种方式，系统通过最佳的方式取得现在的位置信息。XcodeXcode是Mac OS X下的IDE开发环境，也是免费的。主要包含有GUI设计用的「Interface Builder」，iPhone应用程序模拟器，编译器等。Objective-C语言iPhone开发的时候，基本使用Objective-C语言。它是扩充C的面向对象编程语言，也是创建Mac OS X应用程序的首选语言。如果你会C或者C++，理解Objective-C应该很快。并且因为Objective-C可以在gcc运作的系统写和编译，你也可以混合Objective-C和C/C++来写程序，或者使用原先的C/C++库。并且使用Cocoa Touch来开发程序，自己写的代码量也会相应减少，应为大部分的算法，逻辑部分都被程序库吸收了。现在就开始开发iPhone应用程序准备好了所有的工具，我们就可以开始开发iPhone应用程序了。当然学习Cocoa Touch，Objective-C语言也是很重要的，这些我以后会有专门介绍。在开发自己的应用程序之前，最重要的是什么呢？ 是开发程序的知识和技能吗？不是的，最重要的是开发者的激情和创意。有了激情，我们能产生好的创意，有了好的创意，我们才能写出优秀的应用程序。",
		// "text/html", "utf-8", "");
		String picUrl = broadcastInfo.getAdsPicUrl();
		picUrlData = getHtmlData(picUrl);
		if (!picUrl.equals("")) {
			webPicture.setOnTouchListener(this);
			webPicture.loadDataWithBaseURL("", picUrlData, "text/html",
					"utf-8", "");
		}
	}

	// load picture html data
	private String getHtmlData(String picUrl) {
		StringBuffer htmlData = new StringBuffer();
		htmlData.append("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'><html xmlns='http://www.w3.org/1999/xhtml'><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /></head><body>");
		htmlData.append("<center>");
		htmlData.append(
				"<img style='max-width:'+(240)+'px;max-height:'+(320)+'px;'  src='")
				.append(picUrl).append("' />");
		htmlData.append("<br/><br/><br/>");
		htmlData.append("<center></body></html>");
		htmlData.append("");
		return htmlData.toString();
	}

	// close broadcast operate
	private void close() {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = prefs.edit();

		if ("0".equals(broadcastType)) {
			editor.putBoolean("is-read-broadcast-once", false);
		}

		if ("1".equals(broadcastType)) {
			editor.putBoolean("is-read-broadcast-multi", false);
		}
		editor.commit();
		finish();
	}

	public boolean fileIsExists() {
		try {
			File f = new File("/mnt/sdcard/crowdroid/notify.txt");
			if (!f.exists()) {
				return false;
			}

		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
