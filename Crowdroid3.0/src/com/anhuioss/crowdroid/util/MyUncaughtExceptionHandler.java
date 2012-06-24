package com.anhuioss.crowdroid.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler {
	
	private static final String BUG_REPORT_URL = "http://bugreport.crowdroid.com/bugreport.php";
	
	private static File BUG_REPORT_FILE = null;
	static {
		String sdcard = Environment.getExternalStorageDirectory().getPath();
		String path = sdcard + File.separator + "bug.txt";
		BUG_REPORT_FILE = new File(path);
	}

	private static Context sContext;
	private static PackageInfo sPackInfo;
	private UncaughtExceptionHandler mDefaultUEH;
	private static HandleProgressDialog progressDialog;
	
	static long start;
	static long end;
	
	private static Handler handler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			
			progressDialog.dismiss();
			end = System.currentTimeMillis();
			Log.i("**********************", String.valueOf(end - start));
		}
	};

	
	//-----------------------------------------------------------------
	/**
	 * Constructor
	 */
	//-----------------------------------------------------------------
	public MyUncaughtExceptionHandler(Context context) {
		sContext = context;
		try {
			//Get Package Info
			sPackInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		mDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Called when Exception has occurred.
	 */
	//-----------------------------------------------------------------
	public void uncaughtException(Thread th, Throwable t) {
		try {
			
			//Sabe Stack Trace to local filehandler
			saveState(t);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDefaultUEH.uncaughtException(th, t);
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Save Status to file.
	 */
	//-----------------------------------------------------------------
	private void saveState(Throwable e) throws FileNotFoundException {
		StackTraceElement[] stacks = e.getStackTrace();
		File file = BUG_REPORT_FILE;
        PrintWriter pw = null;
        pw = new PrintWriter(new FileOutputStream(file));
        StringBuilder sb = new StringBuilder();
        int len = stacks.length;
        for (int i = 0; i < len; i++) {
            StackTraceElement stack = stacks[i];
            sb.setLength(0);
            sb.append(stack.getClassName()).append("#");
            sb.append(stack.getMethodName()).append(":");
            sb.append(stack.getLineNumber());
            pw.println(sb.toString());
        }
        pw.close();
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Display Dialog if Error log has exist.
	 */
	//-----------------------------------------------------------------
	public static final void showBugReportDialogIfExist(final Context context) {
		
		File file = BUG_REPORT_FILE;
		if (file != null & file.exists()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(sContext);
			builder.setTitle(R.string.bug_report);
			builder.setMessage(R.string.whether_report_to_developer);
			builder.setNegativeButton(android.R.string.cancel, new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					//Intent n= new Intent(sContext,);
					//sContext.startActivity(n);
					finish(dialog);
				}});
			builder.setPositiveButton(R.string.send, new OnClickListener(){
				public void onClick(DialogInterface dialog, int which) {
					
					postBugReportInBackground(context);//Report
					
					dialog.dismiss();
				}});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Send Bug Report in background.
	 */
	//-----------------------------------------------------------------
	private static void postBugReportInBackground(Context context) {
		
		//Progress Dialog
		progressDialog = new HandleProgressDialog(context);
		progressDialog.show();
		start = System.currentTimeMillis();
		new Thread(new Runnable(){
			public void run() {
				postBugReport();
				final File file = BUG_REPORT_FILE;
				if (file != null && file.exists()) {
					file.delete();
				}
				
				handler.sendEmptyMessage(0);
			}}).start();
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Send Bug Report.
	 */
	//-----------------------------------------------------------------
	private static void postBugReport() {
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        String bug = getFileBody(BUG_REPORT_FILE);
        nvps.add(new BasicNameValuePair("device", Build.DEVICE));
        nvps.add(new BasicNameValuePair("model", Build.MODEL));
        nvps.add(new BasicNameValuePair("sdk-version", Build.VERSION.SDK));
        nvps.add(new BasicNameValuePair("apk-version", sPackInfo.versionName));
        nvps.add(new BasicNameValuePair("bug", bug));
        
        try {
        	HttpPost httpPost = new HttpPost(BUG_REPORT_URL);
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
            DefaultHttpClient httpClient = new DefaultHttpClient();
           
            HttpParams params = httpClient.getParams();  
    	    HttpConnectionParams.setConnectionTimeout(params, 5000); //Set Connection Time Out  
    	    HttpConnectionParams.setSoTimeout(params, 5000); //Set Data Require Time Out
            
            httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Get File Contents.
	 */
	//-----------------------------------------------------------------
	private static String getFileBody(File file) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while((line = br.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	
	//-----------------------------------------------------------------
	/**
	 * Finish BugReport Process.
	 */
	//-----------------------------------------------------------------
	private static void finish(DialogInterface dialog) {
		File file = BUG_REPORT_FILE;
		if (file.exists()) {
			file.delete();
		}
		dialog.dismiss();
	}
}