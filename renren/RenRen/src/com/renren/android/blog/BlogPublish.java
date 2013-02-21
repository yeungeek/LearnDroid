package com.renren.android.blog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.RequestListener;
import com.renren.android.emoticons.EmoticonsAdapter;
import com.renren.android.util.Text_Util;

public class BlogPublish extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ImageView mPublish;
	private EditText mTitle;
	private EditText mContent;
	private LinearLayout mCountLayout;
	private TextView mCount;
	private ImageView mVoice;
	private ImageView mEmoticon;
	private GridView mEmoticons;
	private EmoticonsAdapter mAdapter;
	private ProgressDialog mPublishDialog;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blogpublish);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		setListener();
		mAdapter = new EmoticonsAdapter(this);
		mEmoticons.setAdapter(mAdapter);
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.blogpublish_back);
		mPublish = (ImageView) findViewById(R.id.blogpublish_publish);
		mTitle = (EditText) findViewById(R.id.blogpublish_title);
		mContent = (EditText) findViewById(R.id.blogpublish_content);
		mCountLayout = (LinearLayout) findViewById(R.id.blogpublish_count_layout);
		mCount = (TextView) findViewById(R.id.blogpublish_count);
		mVoice = (ImageView) findViewById(R.id.blogpublish_voice);
		mEmoticon = (ImageView) findViewById(R.id.blogpublish_emoticon);
		mEmoticons = (GridView) findViewById(R.id.blogpublish_emoticons);
	}

	/**
	 * 
	 */
	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mContent.getText().toString().trim().length() > 0) {
					backDialog();
				} else {
					finish();
					overridePendingTransition(0, R.anim.roll_down);
				}
			}
		});
		mPublish.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String title = mTitle.getText().toString().trim();
				String content = mContent.getText().toString().trim();
				if (title.length() == 0 || title.equals("")) {
					Toast.makeText(BlogPublish.this, "您还未输入标题,请输入后重试",
							Toast.LENGTH_SHORT).show();
				} else if (content.length() == 0 || content.equals("")) {
					Toast.makeText(BlogPublish.this, "您还未输入内容,请输入后重试",
							Toast.LENGTH_SHORT).show();
				} else {
					publishBolg(title, content);
				}
			}
		});
		mTitle.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mEmoticon
							.setImageResource(R.drawable.v5_0_1_publisher_emotion_button_disable);
					mEmoticon.setEnabled(false);
					mCountLayout.setVisibility(View.GONE);
					if (mEmoticons.isShown()) {
						mEmoticons.setVisibility(View.GONE);
						mEmoticon
								.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
					}
				}
			}
		});
		mContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				mEmoticon
						.setImageResource(R.drawable.v5_0_1_publisher_emotion_button);
				mEmoticon.setEnabled(true);
				mCountLayout.setVisibility(View.VISIBLE);
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
				if (temp.length() > 20000) {
					s.delete(selectionStart - 1, selectionEnd);
					int tempSelection = selectionEnd;
					mContent.setText(s);
					mContent.setSelection(tempSelection);
				}
			}
		});
		mVoice.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(BlogPublish.this, "暂时无法提供此功能",
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
							.hideSoftInputFromWindow(BlogPublish.this
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
								.getEmotion().length() <= 20000) {
					mContent.setText(new Text_Util().replace(mContent.getText()
							.toString()
							+ RenRenData.mEmoticonsResults.get(position)
									.getEmotion()));
				}
			}
		});
	}

	private void publishBolg(String title, String content) {
		BlogPublishRequestParam param = new BlogPublishRequestParam(
				mApplication.mRenRen, title, content);
		RequestListener<BlogPublishResponseBean> listener = new RequestListener<BlogPublishResponseBean>() {

			public void onStart() {
				handler.sendEmptyMessage(0);
			}

			public void onComplete(BlogPublishResponseBean bean) {
				Message msg = handler.obtainMessage(1, bean.code);
				handler.sendMessage(msg);
			}
		};
		mApplication.mAsyncRenRen.publishBlog(param, listener);
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
					mContent.setText("");
					Toast.makeText(BlogPublish.this, "发布成功", Toast.LENGTH_SHORT)
							.show();
					setResult(1);
					finish();
					overridePendingTransition(0, R.anim.roll_down);
					break;

				case 10304:
					Toast.makeText(BlogPublish.this, "发表的日志可能含有非法信息",
							Toast.LENGTH_SHORT).show();
					break;
				case 10305:
					Toast.makeText(BlogPublish.this, "日志标题和内容不能为空或不能过长",
							Toast.LENGTH_SHORT).show();
					break;
				default:
					Toast.makeText(BlogPublish.this, "发布失败", Toast.LENGTH_SHORT)
							.show();
					break;
				}
				break;
			}
		}
	};

	private void backDialog() {
		AlertDialog.Builder builder = new Builder(BlogPublish.this);
		builder.setTitle("退出日志");
		builder.setMessage("是否保存为草稿?");
		builder.setPositiveButton("是", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		builder.setNegativeButton("否", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		builder.create().show();
	}

	private void publishDialogShow() {
		if (mPublishDialog == null) {
			mPublishDialog = new ProgressDialog(BlogPublish.this);
			mPublishDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			mPublishDialog.setMessage("正在发布");
		}
		mPublishDialog.show();
	}

	private void publishDialogDismiss() {
		if (mPublishDialog != null && mPublishDialog.isShowing()) {
			mPublishDialog.dismiss();
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mContent.getText().toString().trim().length() > 0) {
				backDialog();
			} else {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
