package com.renren.android.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import com.renren.android.friends.FriendsFindSearch;
import com.renren.android.ui.base.FlipperLayout.OnOpenListener;

public class Search {
	private BaseApplication mApplication;
	private Context mContext;
	private Activity mActivity;
	private View mSearch;

	private ImageView mFlip;
	private EditText mSearcheEditText;
	private ImageButton mClear;
	private Button mSearchResult;
	private TextView mId;
	private TextView mSearchContacts;
	private TextView mSearchClassmate;

	private OnOpenListener mOnOpenListener;

	public Search(BaseApplication application, Context context,
			Activity activity) {
		mApplication = application;
		mContext = context;
		mActivity = activity;
		mSearch = LayoutInflater.from(context).inflate(R.layout.search, null);
		findViewById();
		setListener();
		mId.setText("我的人人ID : " + mApplication.mRenRen.getUserId());

	}

	private void findViewById() {
		mFlip = (ImageView) mSearch.findViewById(R.id.search_flip);
		mSearcheEditText = (EditText) mSearch.findViewById(R.id.search_search);
		mClear = (ImageButton) mSearch.findViewById(R.id.search_searchclear);
		mSearchResult = (Button) mSearch
				.findViewById(R.id.search_search_result);
		mId = (TextView) mSearch.findViewById(R.id.search_id);
		mSearchContacts = (TextView) mSearch
				.findViewById(R.id.search_search_contacts);
		mSearchClassmate = (TextView) mSearch
				.findViewById(R.id.search_search_classmate);
	}

	private void setListener() {
		mFlip.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		mSearcheEditText.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (mSearcheEditText.getText().length() > 0) {
					mClear.setVisibility(View.VISIBLE);
				} else {
					mClear.setVisibility(View.INVISIBLE);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				if (mSearcheEditText.getText().length() > 0) {
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
				mSearcheEditText.setText("");
			}
		});
		mSearchResult.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String searchInfo = mSearcheEditText.getText().toString()
						.trim();
				if (searchInfo.length() == 0 || searchInfo.equals("")) {
					Toast.makeText(mContext, "请输入搜索内容", Toast.LENGTH_SHORT)
							.show();
				} else {
					Intent intent = new Intent();
					intent.setClass(mContext, FriendsFindSearch.class);
					intent.putExtra("searchinfo", searchInfo);
					mContext.startActivity(intent);
					mActivity.overridePendingTransition(R.anim.roll_up,
							R.anim.roll);
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
				Toast.makeText(mContext, "暂时无法提供此功能", Toast.LENGTH_SHORT)
						.show();
			}
		});
	}

	private void dialog() {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("通讯录找好友");
		builder.setMessage("为了方便您找到更多人人网好友,需要上传您的通讯录(会以加密方式上传和保存)");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Toast.makeText(mContext, "暂时无法提供此功能", Toast.LENGTH_SHORT)
						.show();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	public View getView() {
		return mSearch;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}
}
