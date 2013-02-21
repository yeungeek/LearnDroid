package com.renren.android.photos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.emoticons.EmoticonsAdapter;
import com.renren.android.util.Text_Util;

public class PhotosAddCommentActivity extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mPublish;
	private EditText mContent;
	private TextView mCount;
	private CheckBox mType;
	private ImageButton mVoice;
	private ImageButton mAt;
	private ImageButton mEmoticon;
	private GridView mEmoticons;
	private EmoticonsAdapter mAdapter;
	
	private String mTitleValue;
	private String mHint;
	private int mUid;
	private long mAid;
	private long mPid;
	private int mRid;
	private int mTypeValue;

	private ProgressDialog mPublishDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosaddcomment);
		mApplication=(BaseApplication) getApplication();
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photosaddcomment_back);
		mTitle = (TextView) findViewById(R.id.photosaddcomment_title);
		mPublish = (ImageView) findViewById(R.id.photosaddcomment_publish);
		mContent = (EditText) findViewById(R.id.photosaddcomment_content);
		mCount = (TextView) findViewById(R.id.photosaddcomment_count);
		mType = (CheckBox) findViewById(R.id.photosaddcomment_type);
		mVoice = (ImageButton) findViewById(R.id.photosaddcomment_voice);
		mAt = (ImageButton) findViewById(R.id.photosaddcomment_at);
		mEmoticon = (ImageButton) findViewById(R.id.photosaddcomment_emoticon);
		mEmoticons = (GridView) findViewById(R.id.photosaddcomment_emoticons);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() > 0) {
					backDialog();
				} else {
					setResult(0);
					finish();
					overridePendingTransition(0, R.anim.roll_down);
				}
			}
		});
		mPublish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() == 0) {
					Toast.makeText(PhotosAddCommentActivity.this,
							"您还未输入内容,请输入后重试", Toast.LENGTH_SHORT).show();
				} else {
					photosAddComment(mContent.getText().toString().trim());
				}
			}
		});
		mContent.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEmoticons.isShown()) {
					mEmoticons.setVisibility(View.GONE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
				}
			}
		});
		mContent.addTextChangedListener(new TextWatcher() {
			private CharSequence temp;
			private int selectionStart;
			private int selectionEnd;

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				temp = s;
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				int number = s.length();
				mCount.setText(String.valueOf(number));
				selectionStart = mContent.getSelectionStart();
				selectionEnd = mCount.getSelectionEnd();
				if (temp.length() > 140) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mContent.setText(s);
					mContent.setSelection(tempSelection);
				}
			}
		});
		mType.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					Toast.makeText(PhotosAddCommentActivity.this, "悄悄话里不能@好友哦",
							Toast.LENGTH_SHORT).show();
					mTypeValue = 1;
					mAt.setImageResource(R.drawable.v5_0_1_publisher_at_button_disable);
					mAt.setEnabled(false);
				} else {
					mTypeValue = 0;
					mAt.setImageResource(R.drawable.v5_0_1_publisher_at_button);
					mAt.setEnabled(true);
				}
			}
		});
		mVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosAddCommentActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mAt.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosAddCommentActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mEmoticon.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mEmoticons.isShown()) {
					mEmoticons.setVisibility(View.GONE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
				} else {
					mEmoticons.setVisibility(View.VISIBLE);
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_pad_button);
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(
									PhotosAddCommentActivity.this
											.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
				}
			}
		});
		mEmoticons.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mContent.getText().length()
						+ RenRenData.mEmoticonsResults.get(position)
								.getEmotion().length() <= 140) {
					mContent.setText(new Text_Util().replace(mContent.getText()
							.toString()
							+ RenRenData.mEmoticonsResults.get(position)
									.getEmotion()));
				}
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		mTitleValue = intent.getStringExtra("title");
		mHint = intent.getStringExtra("hint");
		mUid = intent.getIntExtra("uid", 0);
		mAid = intent.getLongExtra("aid", 0);
		mPid = intent.getLongExtra("pid", 0);
		mRid = intent.getIntExtra("rid", 0);
		mTitle.setText(mTitleValue);
		mContent.setHint(mHint);
		mAdapter = new EmoticonsAdapter(this);
		mEmoticons.setAdapter(mAdapter);
	}

	private void photosAddComment(String content) {
		PhotosAddCommentRequestParam param = new PhotosAddCommentRequestParam(
				mApplication.mRenRen, content, mUid, mAid, mPid, mRid, mTypeValue);
		RequestListener<PhotosAddCommentResponseBean> listener = new RequestListener<PhotosAddCommentResponseBean>() {

			public void onStart() {
				handler.sendEmptyMessage(0);
			}

			public void onComplete(PhotosAddCommentResponseBean bean) {
				Message msg = handler.obtainMessage(1, bean.code);
				handler.sendMessage(msg);
			}
		};
		mApplication.mAsyncRenRen.addPhotosComment(param, listener);
	}

	private void backDialog() {
		AlertDialog.Builder builder = new Builder(PhotosAddCommentActivity.this);
		builder.setTitle("提示");
		builder.setMessage("是否取消发布?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	private void publishDialogShow() {
		if (mPublishDialog == null) {
			mPublishDialog = new ProgressDialog(PhotosAddCommentActivity.this);
			mPublishDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mPublishDialog.setMessage("发布中...");
		}
		mPublishDialog.show();
	}

	private void publishDialogDismiss() {
		if (mPublishDialog != null && mPublishDialog.isShowing()) {
			mPublishDialog.dismiss();
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				publishDialogShow();
				break;
			case 1:
				publishDialogDismiss();
				switch (Integer.parseInt(msg.obj.toString())) {
				case 0:
					Toast.makeText(PhotosAddCommentActivity.this, "评论失败",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					mContent.setText("");
					Toast.makeText(PhotosAddCommentActivity.this, "评论成功",
							Toast.LENGTH_SHORT).show();
					setResult(1);
					finish();
					overridePendingTransition(0, R.anim.roll_down);
					break;
				case 10509:
					Toast.makeText(PhotosAddCommentActivity.this, "你没有权限评论此照片",
							Toast.LENGTH_SHORT).show();
					break;
				case 10510:
					Toast.makeText(PhotosAddCommentActivity.this,
							"你发表的评论含有违禁信息", Toast.LENGTH_SHORT).show();
					break;
				}
				break;

			default:
				break;
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mContent.getText().toString().trim().length() > 0) {
				backDialog();
			} else {
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
