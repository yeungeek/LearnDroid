package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.UserFilterData;
import com.anhuioss.crowdroid.data.UserFilterList;

public class AddUserFilterActivity extends BasicAddPreferenceActivity{

	UserFilterList userFilterList;
	/**
	 * Add Clicked Listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@SuppressWarnings("unused")
		@Override
		public boolean onPreferenceClick(Preference preference) {

	    	LayoutInflater factory = LayoutInflater.from(AddUserFilterActivity.this);
			final View textEntryView = factory.inflate(R.layout.dialog_add_user_filter, null);
			final EditText userName = (EditText) textEntryView.findViewById(R.id.edit_text_user_name);
	    	final Spinner service = (Spinner) textEntryView.findViewById(R.id.spinner_service);
	    	ArrayAdapter<CharSequence> adapter;
	    	if (IGeneral.APPLICATION_MODE == 0) {
	    		adapter = ArrayAdapter.createFromResource(
		        		AddUserFilterActivity.this, R.array.services, android.R.layout.simple_spinner_item);
	    	} else if (IGeneral.APPLICATION_MODE == 1) {
	    		adapter = ArrayAdapter.createFromResource(
		        		AddUserFilterActivity.this, R.array.service_less, android.R.layout.simple_spinner_item);
	    	}
	        
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        service.setAdapter(adapter);
	        AlertDialog dlg = new AlertDialog.Builder(AddUserFilterActivity.this)
	        .setTitle(R.string.add)
	        .setView(textEntryView)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	         	
	            	UserFilterData userFilterData = new UserFilterData();
	            	userFilterData.setUserName(userName.getText().toString());
	            	userFilterData.setService(service.getSelectedItem().toString());
	            	userFilterList.addUserFilter(userFilterData);
	            	
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

	/**
	 * Item Clicked Listener
	 */
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {	
		
		@Override
		public boolean onPreferenceClick(Preference preference) {
			
			final int id = Integer.valueOf(preference.getKey());

	    	AlertDialog dlg = new AlertDialog.Builder(AddUserFilterActivity.this)
	        .setTitle(R.string.confirm)
	        .setMessage(R.string.whether_delete)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	            	UserFilterData userFilter = userFilterList.getAllUserFilter().get(id);
	            	userFilterList.removeUserFilter(userFilter);
					createItemList();;
					
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
		userFilterList = crowdroidApplication.getUserFilterList();
		
		// Init Preference Activity
		initView(getResources().getString(R.string.setting_add_user_filter), "",getResources().getString(R.string.setting_user_filter_list), null, addClickedListener,
				itemClickedListener);

		createItemList();
	}

	@Override
	protected void createItemList() {
		// Create Item List
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		// Add items from DataList
		for(UserFilterData userFilter : userFilterList.getAllUserFilter()){
			
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", userFilter.getUserName());
			item.put("summary", userFilter.getService());
			itemList.add(item);
			
		}

		refresh(itemList);
	}

		
}
