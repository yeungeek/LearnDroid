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
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.AccountList;

public class AddSohuAccountActivity extends BasicAddPreferenceActivity {
	
	private AccountList accountList;

	private Preference.OnPreferenceClickListener addSohuClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			Intent intent = new Intent(AddSohuAccountActivity.this, RegisterSohuAccountActivity.class);
			startActivityForResult(intent, 0);

			return true;
		}
	};

	private Preference.OnPreferenceClickListener itemSohuClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			final int id = Integer.valueOf(preference.getKey());
			
			AlertDialog dialog = new AlertDialog.Builder(
					AddSohuAccountActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
										AccountData account = accountList.getAccountByService(IGeneral.SERVICE_NAME_SOHU).get(id);
										accountList.removeAccount(account);
										createItemList();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).create();
			dialog.show();

			return true;
		}
	};
	
	

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
            	account.setService(IGeneral.SERVICE_NAME_SOHU);
            	account.setAccessToken(accessToken);
            	account.setTokenSecret(tokenSecret);
            	account.setLastUserFollowerCount(userFollowerCount);
            	accountList.addAccount(account);
            	//Refresh
            	createItemList();
            	
            	finish();
            	
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		
		SohuOAuthConstant.clear();
	}

	@Override
	protected void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		accountList = crowdroidApplication.getAccountList(); //
		
		initView(getString(R.string.add_sohu_account), "",
				getString(R.string.sohu_account_list), null,
				addSohuClickedListener, itemSohuClickedListener);
		
		createItemList();
	}

	@Override
	protected void createItemList() {
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();
		
		for (AccountData account : accountList.getAccountByService(IGeneral.SERVICE_NAME_SOHU)) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", account.getUserScreenName());
			item.put("summary", account.getService());
			itemList.add(item);
		}
		
		//
		refresh(itemList);
	}
	


}
