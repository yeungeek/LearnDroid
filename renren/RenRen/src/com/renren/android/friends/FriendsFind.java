package com.renren.android.friends;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;

public class FriendsFind extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private EditText mSearch;
	private ImageButton mClear;
	private Button mSearchResult;
	private TextView mId;
	private TextView mSearchContacts;
	private TextView mSearchClassmate;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (BaseApplication) getApplication();
		setContentView(R.layout.friendsfind);
		findViewById();
		setListener();
		mId.setText("我的人人ID : " + mApplication.mRenRen.getUserId());
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.friendsfind_back);
		mSearch = (EditText) findViewById(R.id.friendsfind_search);
		mClear = (ImageButton) findViewById(R.id.friendsfind_searchclear);
		mSearchResult = (Button) findViewById(R.id.friendsfind_search_result);
		mId = (TextView) findViewById(R.id.friendsfind_id);
		mSearchContacts = (TextView) findViewById(R.id.friendsfind_search_contacts);
		mSearchClassmate = (TextView) findViewById(R.id.friendsfind_search_classmate);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mSearch.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mSearch.getText().length() > 0) {
					mClear.setVisibility(View.VISIBLE);
				} else {
					mClear.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (mSearch.getText().length() > 0) {
					mClear.setVisibility(View.VISIBLE);
				} else {
					mClear.setVisibility(View.INVISIBLE);
				}
			}

			public void afterTextChanged(Editable s) {

			}
		});
		mClear.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mSearch.setText("");
			}
		});
		mSearchResult.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String searchInfo = mSearch.getText().toString().trim();
				if (searchInfo.length() == 0 || searchInfo.equals("")) {
					Toast.makeText(FriendsFind.this, "请输入搜索内容",
							Toast.LENGTH_SHORT).show();
				}else {
					Intent intent=new Intent();
					intent.setClass(FriendsFind.this, FriendsFindSearch.class);
					intent.putExtra("searchinfo", searchInfo);
					startActivity(intent);
					overridePendingTransition(R.anim.roll_up, R.anim.roll);
				}
			}
		});
		mSearchContacts.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog();
			}
		});
		mSearchClassmate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(FriendsFind.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("通讯录找好友");
		builder.setMessage("为了方便您找到更多人人网好友,需要上传您的通讯录(会以加密方式上传和保存)");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Toast.makeText(FriendsFind.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
