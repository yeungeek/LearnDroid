package com.anhuioss.crowdroid.dialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;

public class LongTweetDialog extends Dialog implements ServiceConnection {
	
	private EditText originalMsg;
	
	private Button btnTwitLonger;
	
	private EditText shrinkedMsg;
	
	private Button btnOk;
	
	private Button btnCancel;
	
	private EditText target;
	
	/** Progress Bar */
//	private ProgressBar progressBar;
	
	//Progress Dialog
	private HandleProgressDialog progress;
	
	/** Title */
	private TextView title;
	
	private boolean isShrinking = false;
	
	/** Crowdroid Account */
	private AccountData accountData;
	
	private Context mContext;

	/** API Service Interface */
	private ApiServiceInterface apiServiceInterface;

	//--------------------------------------------------------
	/**
	 * API Service Listener
	 */
	//--------------------------------------------------------
	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {

			setProgressEnable(false);
			
			if(statusCode != null && statusCode.equals("200")) {
				
				if(message != null){
					
					ParseHandler parseHandler = new ParseHandler();
					String url = (String) parseHandler.parser(service, type, statusCode, message);
					
					shrinkedMsg.setText(combine(originalMsg.getText().toString(), url));
					
				}

			}else{
				Toast.makeText(mContext, R.string.error, Toast.LENGTH_SHORT).show();
			}

		}
	};
	
	
	//----------------------------------------------------------
	/**
	 * Constructor
	 */
	//----------------------------------------------------------
	public LongTweetDialog(Context context, EditText target) {
	    super(context);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_action);
		
	    super.setContentView(R.layout.dialog_long_tweet);
	     
	    this.target = target;
	    this.mContext = context;
	    
		//Title
		title = (TextView)findViewById(R.id.dialog_title);
		
		//Progress Bar
//		progressBar = (ProgressBar) findViewById(R.id.dialog_progress_bar);
		setProgressEnable(false);

	    
	    //------------------
	    // Init View
	    //------------------
	    title.setText(mContext.getString(R.string.dialog_long_tweet_twitlonger));
	    
	    //Original Message
	    originalMsg = (EditText) findViewById(R.id.originalmsg);
	    originalMsg.setText(target.getText().toString());
	    originalMsg.setEnabled(false);
	    
	    //Twit Longer Button
	    btnTwitLonger = (Button) findViewById(R.id.twitlonger);
	    btnTwitLonger.setOnClickListener(new View.OnClickListener() {
	    	public void onClick(View v) {
	    		if(!isShrinking){
	    			shrinkMessage();
	    		}
	    	}
	    });
	    
	    //Shorten Message
	    shrinkedMsg = (EditText) findViewById(R.id.respondmsg);
	    shrinkedMsg.setEnabled(false);
	    
	    
	    //OK
	    btnOk = (Button) findViewById(R.id.twitlongerok);
	    btnOk.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				String message = shrinkedMsg.getText().toString();
				if(message == null || message.length() == 0){
					return;
				}
				replaceText();
				dismiss();
			}
		});
	    
	    //Cancel
	    btnCancel = (Button) findViewById(R.id.twitlongercancel);
	    btnCancel.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	
	//----------------------------------------------------------
	/**
	 * Shrink a long message and set to 
	 */
	//----------------------------------------------------------
	private void shrinkMessage() {
		
		// Flag
		isShrinking = true;
		
		setProgressEnable(true);
		
		// Prepare Parameters
		Map<String, Object> parameters;
		parameters = new HashMap<String, Object>();
		parameters.put("userName", accountData.getUserName());
		parameters.put("message", originalMsg.getText().toString());

		try {
			apiServiceInterface.request(accountData.getService(),
					CommHandler.TYPE_REGISTER_MESSAGE_TO_API, apiServiceListener,
					parameters);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//----------------------------------------------------------
	/**
	 * Replace target Text
	 */
	//----------------------------------------------------------
	private void replaceText(){		
		String message = shrinkedMsg.getText().toString();
		target.setText(message);
	}
	
	//--------------------------------------------------------------------------
	/**
	 *  Conbine informatio and create 140 new Message.
	 */
	//--------------------------------------------------------------------------
	private String combine(String message, String shortenURL){
		
		String connectString = "... ";
		
		int reduceCount = message.length() + connectString.length() + shortenURL.length() - 140;
		if(reduceCount <= 0){
			String newMessage = message + connectString + shortenURL;
			return newMessage;
		}else{
			String newMessage = message.substring(0, message.length() - reduceCount) + connectString + shortenURL;
			return newMessage;
		}
		
	}
	
	
	private void setProgressEnable(boolean flag) {

		if (flag) {
//			progressBar.setVisibility(View.VISIBLE);
			showProgressDialog();
		} else {
//			progressBar.setVisibility(View.GONE);
			closeProgressDialog();
		}

	}
	
	
	//--------------------------------------------------------
	/**
	 * On Start
	 */
	//--------------------------------------------------------
	@Override
	public void onStart() {
		super.onStart();

		// Account Data
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) mContext
				.getApplicationContext();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

		// Bind Service
		Intent intent = new Intent(mContext, ApiService.class);
		mContext.bindService(intent, this, Context.BIND_AUTO_CREATE);

	}

	//--------------------------------------------------------
	/**
	 * On Stop
	 */
	//--------------------------------------------------------
	@Override
	public void onStop() {
		super.onStop();

		closeProgressDialog();
		
		// Unbind Service
		mContext.unbindService(this);
		
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
	
	
	private void showProgressDialog(){
		if(progress == null) {
			progress = new HandleProgressDialog(getContext());
		}
		progress.show();
	}
	
	private void closeProgressDialog(){
		if(progress != null) {
			progress.dismiss();
		}
	}
	
	


}
