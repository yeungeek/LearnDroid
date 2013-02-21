package com.renren.android.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;

public class Blog extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private TextView mTitleName;
	private ImageView mShare;
	private ImageView mMenu;
	private ScrollView mScrollView;
	private ImageView mAvatar;
	private TextView mName;
	private TextView mTitle;
	private TextView mTime;
	private TextView mViewCount;
	private LinearLayout mError;
	private WebView mWebView;
	private LinearLayout mCommentLayout;
	private EditText mComment;
	private Button mCommentCount;
	private WebSettings mSettings;

	private int mId;
	private int mUid;
	private String mAvatarString;
	private String mNameString;
	private String mTitleString;
	private String mDescriptionString;
	private String mTimeString;
	private String mType;
	private int mCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blog);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.blog_back);
		mTitleName = (TextView) findViewById(R.id.blog_titlename);
		mShare = (ImageView) findViewById(R.id.blog_share);
		mMenu = (ImageView) findViewById(R.id.blog_menu);
		mError = (LinearLayout) findViewById(R.id.blog_error);
		mScrollView = (ScrollView) findViewById(R.id.blog_scroll);
		mAvatar = (ImageView) findViewById(R.id.blog_avatar);
		mName = (TextView) findViewById(R.id.blog_name);
		mTitle = (TextView) findViewById(R.id.blog_title);
		mTime = (TextView) findViewById(R.id.blog_time);
		mViewCount = (TextView) findViewById(R.id.blog_viewcount);
		mWebView = (WebView) findViewById(R.id.blog_web);
		mCommentLayout = (LinearLayout) findViewById(R.id.blog_comment_layout);
		mComment = (EditText) findViewById(R.id.blog_comment);
		mCommentCount = (Button) findViewById(R.id.blog_commentcount);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mShare.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(Blog.this, "锟斤拷时锟睫凤拷锟结供锟剿癸拷锟斤拷", Toast.LENGTH_SHORT)
						.show();
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(Blog.this, "锟斤拷时锟睫凤拷锟结供锟剿癸拷锟斤拷", Toast.LENGTH_SHORT)
						.show();
			}
		});
		mComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(Blog.this, "锟斤拷时锟睫凤拷锟结供锟剿癸拷锟斤拷", Toast.LENGTH_SHORT)
						.show();
			}
		});
		mComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(Blog.this, BlogAddCommment.class);
				intent.putExtra("title", "锟斤拷锟斤拷");
				intent.putExtra("hint", "锟斤拷锟斤拷锟斤拷锟");
				intent.putExtra("id", mId);
				intent.putExtra("uid", mUid);
				intent.putExtra("rid", 0);
				intent.putExtra("type", mType);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mCommentCount.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mCount != 0) {
					Intent intent = new Intent();
					intent.setClass(Blog.this, BlogComments.class);
					intent.putExtra("id", mId);
					intent.putExtra("uid", mUid);
					intent.putExtra("avatar", mAvatarString);
					intent.putExtra("name", mNameString);
					intent.putExtra("title", mTitleString);
					intent.putExtra("description", mDescriptionString);
					intent.putExtra("time", mTimeString);
					intent.putExtra("count", mCount);
					intent.putExtra("type", mType);
					startActivity(intent);
					overridePendingTransition(R.anim.roll_up, R.anim.roll);
				} else {
					Intent intent = new Intent();
					intent.setClass(Blog.this, BlogAddCommment.class);
					intent.putExtra("title", "锟斤拷锟斤拷");
					intent.putExtra("hint", "锟斤拷锟斤拷锟斤拷锟");
					intent.putExtra("id", mId);
					intent.putExtra("uid", mUid);
					intent.putExtra("rid", 0);
					intent.putExtra("type", mType);
					startActivityForResult(intent, 0);
					overridePendingTransition(R.anim.roll_up, R.anim.roll);
				}
			}
		});
	}

	private void init() {
		mId = getIntent().getIntExtra("id", 0);
		mUid = getIntent().getIntExtra("uid", 0);
		mType = getIntent().getStringExtra("type");
		mDescriptionString = getIntent().getStringExtra("description");
		mScrollView.setVerticalFadingEdgeEnabled(false);
		mSettings = mWebView.getSettings();
		mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
		mWebView.setHorizontalScrollBarEnabled(false);
		mSettings.setLoadsImagesAutomatically(true);
		mSettings.setPluginsEnabled(true);
		mSettings.setUseWideViewPort(false);
		mSettings.setJavaScriptEnabled(true);
		mSettings.setSupportZoom(false);
		mSettings.setCacheMode(WebSettings.LOAD_NORMAL);
//		mSettings.setNavDump(true);
		mSettings.setMinimumFontSize(8);
		mSettings.setMinimumLogicalFontSize(8);
		mSettings.setDefaultFontSize(16);
		mSettings.setDefaultFixedFontSize(13);
		mSettings.setTextSize(WebSettings.TextSize.NORMAL);
		mSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
		mSettings.setAllowFileAccess(true);
		mSettings.setBuiltInZoomControls(false);
		mSettings.setDefaultTextEncodingName("utf-8");

		getBlog();
	}

	private void getBlog() {
		GetBlogRequestParam param;
		if ("user".equals(mType)) {
			param = new GetBlogRequestParam(mApplication.mRenRen, mId, mUid, 0);
		} else {
			param = new GetBlogRequestParam(mApplication.mRenRen, mId, 0, mUid);
		}

		RequestListener<GetBlogResponseBean> listener = new RequestListener<GetBlogResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetBlogResponseBean bean) {
				if (bean.error) {
					handler.sendEmptyMessage(0);
				} else {
					Message msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = bean.result;
					handler.sendMessage(msg);
				}
			}
		};
		mApplication.mAsyncRenRen.getBlog(param, listener);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mError.setVisibility(View.VISIBLE);
				mScrollView.setVisibility(View.GONE);
				mCommentLayout.setVisibility(View.GONE);
				break;

			case 1:
				mError.setVisibility(View.GONE);
				mScrollView.setVisibility(View.VISIBLE);
				mCommentLayout.setVisibility(View.VISIBLE);
				BlogResult result = (BlogResult) msg.obj;
				mAvatarString = result.getHeadurl();
				mNameString = result.getName();
				mTitleString = result.getTitle();
				mTimeString = result.getTime();
				mCount = result.getComment_count();
				mTitleName.setText(mNameString + "锟斤拷锟斤拷志");
				mApplication.mHeadBitmap.display(mAvatar, mAvatarString);
				mName.setText(mNameString);
				mTitle.setText(mTitleString);
				mTime.setText(mTimeString);
				mViewCount.setText("锟斤拷锟" + result.getView_count());
				mCommentCount.setText(mCount + "");
				mWebView.loadData(result.getContent(), "text/html", "utf-8");
				break;
			}
		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
