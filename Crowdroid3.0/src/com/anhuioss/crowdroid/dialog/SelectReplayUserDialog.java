package com.anhuioss.crowdroid.dialog;

import java.util.ArrayList;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.SendDMActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;

public class SelectReplayUserDialog extends AlertDialog {
	private Context mContext;
	private ListView replyNameListView;
	private CheckBox replyAllCheckBox;
	private String[] mReplyList;
//	private String[] replyNameData = { "@a", "@b", "@c", "@d", "@e", "@f" };
	private ArrayList<String> selectedNameList = new ArrayList<String>();

	public SelectReplayUserDialog(Context context , String[] replyList, final String messageId) {
		super(context);
		mContext = context;
		mReplyList = replyList;
		// Set View
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View layoutView = inflater.inflate(R.layout.dialog_select_replay_user, null);
		setView(layoutView);

		// Set Title
		setTitle(R.string.reply);

		// List View
		replyNameListView = (ListView) layoutView.findViewById(R.id.dialog_select_replay_user_listView);
		replyNameListView.setAdapter(new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_checked, mReplyList));
		replyNameListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		replyNameListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {

				if (selectedNameList.contains(((CheckedTextView) arg1).getText().toString())) {
					selectedNameList.remove(((CheckedTextView) arg1).getText().toString());
				} else {
					selectedNameList.add(((CheckedTextView) arg1).getText().toString());
				}

			}
		});

		// Check Box
		replyAllCheckBox = (CheckBox) layoutView.findViewById(R.id.dialog_select_replay_all_user_checkBox);
		replyAllCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						replyNameListView.setEnabled(!isChecked);
					}
				});

		// OK Button
		
		setButton(DialogInterface.BUTTON_POSITIVE, mContext.getString(R.string.ok), new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Start Send Message Activity
				Intent intent = new Intent(mContext, SendMessageActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("action", "new");
				bundle.putString("target", getReplyNameString());
				bundle.putString("message_id", messageId);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
				dismiss();
			}
		});

		// Cancel Button
		setButton(DialogInterface.BUTTON_NEGATIVE, mContext.getString(R.string.cancel),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dismiss();
					}
				});

	}
	
	private String getReplyNameString() {
		
		StringBuffer replyNameString = new StringBuffer();
		if(replyAllCheckBox.isChecked()) {
			for(String name : mReplyList) {
				replyNameString.append(name).append(" ");
			}
		} else {
			if(selectedNameList.size() > 0) {
				for (String selectedName : selectedNameList) {
					replyNameString.append(selectedName).append(" ");
				}
			} else {
				return mReplyList[0] + " ";
			}
		}
		return replyNameString.toString();
		
	}	
}
