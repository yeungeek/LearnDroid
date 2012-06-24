package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;
import com.anhuioss.crowdroid.data.StatusData;

public class AddTwitterAccountActivity extends BasicAddPreferenceActivity{
	
	AccountList accountList;
	
	private StatusData statusData;
	
	/**
	 * Add Clicked Listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			Intent intent = new Intent(AddTwitterAccountActivity.this, RegisterTwitterAccountActivity.class);
			startActivityForResult(intent, 0);

			return true;
		}
	};

	/**
	 * Item Clicked Listener
	 */
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {	
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			final int id = Integer.valueOf(preference.getKey());

	    	AlertDialog dlg = new AlertDialog.Builder(AddTwitterAccountActivity.this)
	        .setTitle(R.string.confirm)
	        .setMessage(R.string.whether_delete)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	            	AccountData account = accountList.getAccountByService(IGeneral.SERVICE_NAME_TWITTER).get(id);
	            	accountList.removeAccount(account);
					createItemList();
					
	            }
	        })
	        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	            }
	        })
	        .create();
	        dlg.show();

			return true;
		}
	};

	@Override
	public void onStart() {
		super.onStart();
		
		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList();
		statusData = crowdroidApplication.getStatusData();
		
		// Init Preference Activity
		initView(getString(R.string.add_twitter_account), "", getString(R.string.twitter_account_list), null, addClickedListener,
				itemClickedListener);

		createItemList();
	}

	@Override
	protected void createItemList() {
		// Create Item List
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		// Add items from DataList
		for(AccountData account : accountList.getAccountByService(IGeneral.SERVICE_NAME_TWITTER)){
			
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserName());
			item.put("summary", account.getService());
			itemList.add(item);
			
		}

		refresh(itemList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if(resultCode == 0 && data != null){
			Bundle bundle = data.getExtras();
			if(bundle != null){
				//Get Data
				String userName = bundle.getString("userName");
				String uid = bundle.getString("uid");
				String accessToken = bundle.getString("accessToken");
				String tokenSecret = bundle.getString("tokenSecret");
				String userFollowerCount = bundle.getString("followers_count");
				//Add Account
        		AccountData account = new AccountData();
            	account.setUserName(userName);
            	account.setUserScreenName(userName);
            	account.setUid(uid);
            	account.setService(IGeneral.SERVICE_NAME_TWITTER);
            	account.setAccessToken(accessToken);
            	account.setTokenSecret(tokenSecret);
            	account.setLastUserFollowerCount(userFollowerCount);
//            	statusData.setLastUserFollowerCount(userFollowerCount);
            	accountList.addAccount(account);
            	//Refresh
            	createItemList();
            	
            	finish();
            	
			}
		}
	}

}
