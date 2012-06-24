package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.Preference;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.KeywordFilterData;
import com.anhuioss.crowdroid.data.KeywordFilterList;

public class AddKeywordFilterActivity extends BasicAddPreferenceActivity {

	KeywordFilterList keywordFilterList;
	/**
	 * Add Clicked Listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

	    	LayoutInflater factory = LayoutInflater.from(AddKeywordFilterActivity.this);
			final View textEntryView = factory.inflate(R.layout.dialog_add_keyword_filter, null);
	        AlertDialog dlg = new AlertDialog.Builder(AddKeywordFilterActivity.this)
	        .setTitle(R.string.add)
	        .setView(textEntryView)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	
	            	EditText keywordEditText = (EditText) textEntryView.findViewById(R.id.edit_text_keyword);
	            	KeywordFilterData keywordFilterData = new KeywordFilterData();
	            	keywordFilterData.setKeyword(keywordEditText.getText().toString());
	            	keywordFilterList.addKeyword(keywordFilterData);
	            	
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

	    	AlertDialog dlg = new AlertDialog.Builder(AddKeywordFilterActivity.this)
	        .setTitle(R.string.confirm)
	        .setMessage(R.string.whether_delete)
	        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {

	            	KeywordFilterData keyword = keywordFilterList.getAllKeywords().get(id);
	            	keywordFilterList.removeKeyword(keyword);
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
		keywordFilterList = crowdroidApplication.getKeywordFilterList();
		
		// Init Preference Activity
		initView(getResources().getString(R.string.setting_add_keyword_filter), "", getResources().getString(R.string.setting_keyword_filter_list), null, addClickedListener,
				itemClickedListener);

		createItemList();
	}

	@Override
	protected void createItemList() {
		// Create Item List
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		// Add items from DataList
		for(KeywordFilterData keywordFilter : keywordFilterList.getAllKeywords()){
			
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", keywordFilter.getKeyword());
			item.put("summary", "");
			itemList.add(item);
			
		}

		refresh(itemList);
	}

}
