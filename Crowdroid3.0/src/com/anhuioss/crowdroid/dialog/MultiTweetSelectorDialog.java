package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.AccountData;

public class MultiTweetSelectorDialog extends AlertDialog {
	
	private ArrayList<String> multiTweetAccountSelected;
	
	private ArrayList<AccountData> multiTweetAccount;
	
	private LinearLayout root;
	
	private Context mContext;
	
	// ----------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------
	public MultiTweetSelectorDialog(Context context) {
		super(context);
		mContext = context;

	}


	public MultiTweetSelectorDialog(Context context, ArrayList<AccountData> multiTweetAccount) {
		super(context);
		
		mContext = context;
		this.multiTweetAccount = multiTweetAccount;
		multiTweetAccountSelected = new ArrayList<String>();
		
		//Set Title
		setTitle(R.string.multi_tweet);
		
		//Set View
		LayoutInflater inflater = LayoutInflater.from(context);
		View layoutView = inflater.inflate(R.layout.dialog_multi_tweet_selector, null);
		setView(layoutView);
		
		root = (LinearLayout) layoutView.findViewById(R.id.dialog_root);
		
		MarginLayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		lp.setMargins(30, 0, 0, 0);
		
		CheckBox checkBox;
		
		// CFB
		ArrayList<AccountData> cfbMultiAccount = getSubList(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
		if(cfbMultiAccount.size() > 0) {
			checkBox = new CheckBox(context);
			checkBox.setText(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
			checkBox.setTag(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
			checkBox.setChecked(true);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					CheckBox view;
					for(int i = 0; i < root.getChildCount(); i++) {
						view = (CheckBox) root.getChildAt(i);
						if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS)) {
							view.setChecked(isChecked);
						}
					}
					
				}
			});
			root.addView(checkBox);
		}
		for(AccountData account : cfbMultiAccount) {
			checkBox = new CheckBox(context);
			checkBox.setButtonDrawable(R.drawable.check);
			checkBox.setText(account.getUserScreenName());
			checkBox.setTag(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS);
			checkBox.setChecked(true);
			root.addView(checkBox, lp);
		}
		
		// Sina
		ArrayList<AccountData> sinaMultiAccount = getSubList(IGeneral.SERVICE_NAME_SINA);
		if(sinaMultiAccount.size() > 0) {
			checkBox = new CheckBox(context);
			checkBox.setText(IGeneral.SERVICE_NAME_SINA);
			checkBox.setTag(IGeneral.SERVICE_NAME_SINA);
			checkBox.setChecked(true);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					CheckBox view;
					for(int i = 0; i < root.getChildCount(); i++) {
						view = (CheckBox) root.getChildAt(i);
						if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_SINA)) {
							view.setChecked(isChecked);
						}
					}
					
				}
			});
			root.addView(checkBox);
		}
		for(AccountData account : sinaMultiAccount) {
			checkBox = new CheckBox(context);
			checkBox.setButtonDrawable(R.drawable.check);
			checkBox.setText(account.getUserScreenName());
			checkBox.setTag(IGeneral.SERVICE_NAME_SINA);
			checkBox.setChecked(true);
			root.addView(checkBox, lp);
		}
		
		// Tencent
		ArrayList<AccountData> tencentMultiAccount = getSubList(IGeneral.SERVICE_NAME_TENCENT);
		if(tencentMultiAccount.size() > 0) {
			checkBox = new CheckBox(context);
			checkBox.setText(IGeneral.SERVICE_NAME_TENCENT);
			checkBox.setTag(IGeneral.SERVICE_NAME_TENCENT);
			checkBox.setChecked(true);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					CheckBox view;
					for(int i = 0; i < root.getChildCount(); i++) {
						view = (CheckBox) root.getChildAt(i);
						if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_TENCENT)) {
							view.setChecked(isChecked);
						}
					}
					
				}
			});
			root.addView(checkBox);
		}
		for(AccountData account : tencentMultiAccount) {
			checkBox = new CheckBox(context);
			checkBox.setButtonDrawable(R.drawable.check);
			checkBox.setText(account.getUserScreenName());
			checkBox.setTag(IGeneral.SERVICE_NAME_TENCENT);
			checkBox.setChecked(true);
			root.addView(checkBox, lp);
		}
		
		// Sohu
		ArrayList<AccountData> sohuMultiAccount = getSubList(IGeneral.SERVICE_NAME_SOHU);
		if(sohuMultiAccount.size() > 0) {
			checkBox = new CheckBox(context);
			checkBox.setText(IGeneral.SERVICE_NAME_SOHU);
			checkBox.setTag(IGeneral.SERVICE_NAME_SOHU);
			checkBox.setChecked(true);
			checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					
					CheckBox view;
					for(int i = 0; i < root.getChildCount(); i++) {
						view = (CheckBox) root.getChildAt(i);
						if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_SOHU)) {
							view.setChecked(isChecked);
						}
					}
					
				}
			});
			root.addView(checkBox);
		}
		for(AccountData account : sohuMultiAccount) {
			checkBox = new CheckBox(context);
			checkBox.setButtonDrawable(R.drawable.check);
			checkBox.setText(account.getUserScreenName());
			checkBox.setTag(IGeneral.SERVICE_NAME_SOHU);
			checkBox.setChecked(true);
			root.addView(checkBox, lp);
		}
		
		if(IGeneral.APPLICATION_MODE == 0) {
			// Twitter
			ArrayList<AccountData> twitterMultiAccount = getSubList(IGeneral.SERVICE_NAME_TWITTER);
			if(twitterMultiAccount.size() > 0) {
				checkBox = new CheckBox(context);
				checkBox.setText(IGeneral.SERVICE_NAME_TWITTER);
				checkBox.setTag(IGeneral.SERVICE_NAME_TWITTER);
				checkBox.setChecked(true);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						CheckBox view;
						for(int i = 0; i < root.getChildCount(); i++) {
							view = (CheckBox) root.getChildAt(i);
							if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_TWITTER)) {
								view.setChecked(isChecked);
							}
						}
						
					}
				});
				root.addView(checkBox);
			}
			for(AccountData account : twitterMultiAccount) {
				checkBox = new CheckBox(context);
				checkBox.setButtonDrawable(R.drawable.check);
				checkBox.setText(account.getUserScreenName());
				checkBox.setTag(IGeneral.SERVICE_NAME_TWITTER);
				checkBox.setChecked(true);
				root.addView(checkBox, lp);
			}
			
			// Twitter-Proxy
			ArrayList<AccountData> twitterProxyMultiAccount = getSubList(IGeneral.SERVICE_NAME_TWITTER_PROXY);
			if(twitterProxyMultiAccount.size() > 0) {
				checkBox = new CheckBox(context);
				checkBox.setText(IGeneral.SERVICE_NAME_TWITTER_PROXY);
				checkBox.setTag(IGeneral.SERVICE_NAME_TWITTER_PROXY);
				checkBox.setChecked(true);
				checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						
						CheckBox view;
						for(int i = 0; i < root.getChildCount(); i++) {
							view = (CheckBox) root.getChildAt(i);
							if(view.getTag() != null && view.getTag().equals(IGeneral.SERVICE_NAME_TWITTER_PROXY)) {
								view.setChecked(isChecked);
							}
						}
						
					}
				});
				root.addView(checkBox);
			}
			for(AccountData account : twitterProxyMultiAccount) {
				checkBox = new CheckBox(context);
				checkBox.setButtonDrawable(R.drawable.check);
				checkBox.setText(account.getUserScreenName());
				checkBox.setTag(IGeneral.SERVICE_NAME_TWITTER_PROXY);
				checkBox.setChecked(true);
				root.addView(checkBox, lp);
			}
		}
		
		//Confirm
		setButton(DialogInterface.BUTTON_POSITIVE,mContext.getString(R.string.confirm), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				CheckBox view;
				for(int i = 0; i < root.getChildCount(); i++) {
					view = (CheckBox) root.getChildAt(i);
					if(!(IGeneral.SERVICE_NAME_CROWDROID_FOR_BUSINESS.equals(view.getText())
							|| IGeneral.SERVICE_NAME_SINA.equals(view.getText())
							|| IGeneral.SERVICE_NAME_TENCENT.equals(view.getText())
							|| IGeneral.SERVICE_NAME_SOHU.equals(view.getText())
							|| IGeneral.SERVICE_NAME_TWITTER.equals(view.getText())
							|| IGeneral.SERVICE_NAME_TWITTER_PROXY.equals(view.getText())
							) && view.isChecked()) {
						multiTweetAccountSelected.add(view.getText().toString() + view.getTag().toString());
					}
				}
				SendMessageActivity.setMultiTweetSelected(multiTweetAccountSelected);
			}

		});
		
		//Cancel
		setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		
	}
	
	private void changeMultiTweetAccount(ArrayList<HashMap<String, String>> multiMessages) {
		
		ArrayList<Integer> indexs = new ArrayList<Integer>();
		AccountData account;
		for(HashMap<String, String> item : multiMessages) {
			for(int i = 0; i < multiTweetAccount.size(); i++) {
				account = multiTweetAccount.get(i);
				if(account.getUserScreenName().equals(item.get("name"))
						&& account.getService().equals(item.get("server"))) {
					indexs.add(i);
				}
			}
		}
		for(Integer i : indexs) {
			multiTweetAccount.remove(i);
		}
		
	}
	
	private ArrayList<AccountData> getSubList(String server) {
		
		ArrayList<AccountData> result = new ArrayList<AccountData>();
		for(AccountData account : multiTweetAccount) {
			if(account.getService().equals(server)) {
				result.add(account);
			}
		}
		return result;
		
	}

}
