package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.Preference;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.ListInfoList;
import com.anhuioss.crowdroid.data.info.ListInfo;
import com.anhuioss.crowdroid.dialog.AddListDialog;

public class AddListActivity extends BasicAddPreferenceActivity {

	private AccountData account;

	private ListInfoList lists;

	CrowdroidApplication crowdroidApplication;

	/**
	 * Add clicked listener
	 */
	private Preference.OnPreferenceClickListener addClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {

			int size = lists.getListsByUserName(account.getUserName()).size();

			if (size < 5) {
				new AddListDialog(AddListActivity.this, account.getUserName())
						.show();
			} else {
				Toast.makeText(
						AddListActivity.this,
						getResources()
								.getString(R.string.setting_list_warning1),
						Toast.LENGTH_SHORT).show();
			}

			return true;

		}
	};

	/**
	 * Item clicked listener
	 */
	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			final String key = preference.getKey();
			AlertDialog dialog = new AlertDialog.Builder(AddListActivity.this)
					.setTitle(R.string.confirm)
					.setMessage(R.string.whether_delete)
					.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									lists.removeList(key);
									createItemList();
								}
							})
					.setNegativeButton(R.string.cancel,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							}).create();
			dialog.show();

			return true;
		}
	};

	/**
	 * onStart()
	 */
	@Override
	public void onStart() {
		super.onStart();

		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		account = crowdroidApplication.getAccountList().getCurrentAccount();
		lists = crowdroidApplication.getListInfoList();

		initView(getResources().getString(R.string.setting_list_add), "",
				getResources().getString(R.string.setting_list_my_all_list),
				null, addClickedListener, itemClickedListener);

		createItemList();
	}

	/**
	 * Create item list
	 */
	@Override
	protected void createItemList() {
		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		for (ListInfo listInfo : lists
				.getListsByUserName(account.getUserName())) {
			HashMap<String, String> item = new HashMap<String, String>();
			item.put("title", listInfo.getName() + "(" + listInfo.getMode()
					+ ")");
			item.put("summary", listInfo.getDescription());
			item.put("key", listInfo.getId());
			itemList.add(item);
		}

		// crowdroidApplication.refreshImageRes();

		refresh(itemList);
	}

}
