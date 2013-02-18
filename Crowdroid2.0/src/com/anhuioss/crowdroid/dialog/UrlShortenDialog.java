package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.util.ErrorMessage;

public class UrlShortenDialog extends Dialog implements ServiceConnection {

	private EditText target;

	private EditText longUrlText;
	private EditText shortenUrlText;

	private Button btnConvert;

	private Button okButton;

	private Context mContext;

	private TextView title;

	private HandleProgressDialog progress;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			setProgressEnable(false);
			if (statusCode != null && statusCode.equals("200")) {
				String shortenUrl = CfbParseHandler.parseShortUrl(message);
				if (shortenUrl != null) {
					shortenUrlText.setText(shortenUrl);
				}
			} else {
				Toast.makeText(mContext,
						ErrorMessage.getErrorMessage(mContext, statusCode),
						Toast.LENGTH_SHORT).show();
			}

		}
	};

	@Override
	public void setTitle(CharSequence title) {
		this.title.setText(title);
	}

	/** Handler */
	Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			// Set to Shorten URL Area
			Bundle data = msg.getData();
			String shortenUrl = data.getString("new-url");
			if (shortenUrl != null) {
				shortenUrlText.setText(shortenUrl);
			}

		}
	};

	@Override
	public void onStart() {
		super.onStart();

		// Set Title
		setTitle(R.string.shorten_url_title);

		// Bind Service
		Intent intent = new Intent(mContext, ApiService.class);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();

		// Unbind Service
		mContext.unbindService(this);

	}

	// -------------------------------------------------------------------
	/**
	 * Constructor
	 */
	// -------------------------------------------------------------------
	public UrlShortenDialog(Context context, EditText target) {
		super(context);

		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		super.setContentView(R.layout.dialog_url_shorten);

		mContext = context;

		this.target = target;

		// Find Views
		title = (TextView) findViewById(R.id.dialog_title);

		// Long URL
		longUrlText = (EditText) findViewById(R.id.willconvert);
		longUrlText.setEnabled(false);

		// Short URL
		shortenUrlText = (EditText) findViewById(R.id.haveconverted);
		shortenUrlText.setEnabled(false);

		// Convert Button
		btnConvert = (Button) findViewById(R.id.convert);
		btnConvert.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				String longUrl = longUrlText.getText().toString();
				if (longUrl == null || longUrl.length() == 0) {
					return;
				}
				getShortenUrl();
			}
		});

		// OK Button
		okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String sortenUrl = shortenUrlText.getText().toString();
				if (sortenUrl == null || sortenUrl.length() == 0) {
					return;
				}

				replaceUrl();
				dismiss();
			}
		});

		// Cancel Button
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		// Extract URL
		extractUrl();

	}

	private void setProgressEnable(boolean flag) {

		if (flag) {
			showProgressDialog();
		} else {
			closeProgressDialog();
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Extract URL pattern and set to longUrlText Area.
	 */
	// -------------------------------------------------------------------
	private void extractUrl() {

		// Get Message
		String message = target.getText().toString();

		// Extract http://---
		ArrayList<String> urlList = new ArrayList<String>();
		Pattern pattern = Pattern.compile(
				"(http://|https://){1}[\\w\\.\\-/:\\#\\?\\=\\&\\;\\%\\~\\+]+",
				Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(message);
		while (matcher.find()) {
			urlList.add(matcher.group());
		}

		// Set to Text View
		String longUrl = null;
		if (urlList.size() > 0) {
			longUrl = urlList.get(0); // Get first URL pattern
		}

		if (longUrl != null) {
			longUrlText.setText(longUrl);
		} else {
			longUrlText.setText("");
		}
	}

	// -------------------------------------------------------------------
	/**
	 * Get Shorten Url
	 */
	// -------------------------------------------------------------------
	private void getShortenUrl() {

		// //Thread
		// Thread th = new Thread(new Runnable() {
		// public void run() {
		// String longUrl = longUrlText.getText().toString();
		// // UrlShortenHandler handler = new UrlShortenHandler();
		// // String shortenUrl = handler.getShortUrl(longUrl);
		//
		// Message msg = new Message();
		// Bundle data = new Bundle();
		// // data.putString("new-url", shortenUrl);
		// msg.setData(data);
		//
		// myHandler.sendMessage(msg);
		//
		// }
		// }, "requesting shorten url");
		//
		// th.start();

		setProgressEnable(true);

		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("longUrl", longUrlText.getText().toString());

		try {
			apiServiceInterface.request(
					IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS,
					CommHandler.TYPE_GET_SHORT_URL, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// -------------------------------------------------------------------
	/**
	 * Replace with new URL
	 */
	// -------------------------------------------------------------------
	private void replaceUrl() {
		String originalMsg = target.getText().toString();
		String longUrl = longUrlText.getText().toString();
		String shortenUrl = shortenUrlText.getText().toString();

		/*
		 * Pattern pattern = Pattern.compile("<.*?>"); Matcher matcher
		 * =pattern.matcher(longUrl); while (matcher.find()) {
		 * matcher.replaceAll("\\?"); }
		 */
		// String tmp = longUrl.replaceAll("[?]", "\\?");
		longUrl = longUrl.replaceAll("\\?", "\\\\?");
		// System.out.print(tmp);

		String replacedMsg = originalMsg.replaceFirst(longUrl, shortenUrl);
		target.setText(replacedMsg);
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		Log.i("Activity", "onServiceDisconnected");
		apiServiceInterface = null;

	}

	private void showProgressDialog() {
		if (progress == null) {
			progress = new HandleProgressDialog(mContext);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (progress != null) {
			progress.dismiss();
		}
	}

}
