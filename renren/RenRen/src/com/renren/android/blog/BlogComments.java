package com.renren.android.blog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;

public class BlogComments extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ImageView mShare;
	private ImageView mMenu;
	private ListView mDisplay;
	// private ProgressBar mBar;
	private EditText mCommment;
	private View mHead;
	private ImageView mHeadAvatar;
	private TextView mHeadName;
	private TextView mHeadTitle;
	private TextView mHeadDescription;
	private TextView mHeadTime;
	private TextView mHeadCommentCount;

	private BlogCommmentsAdapter mAdapter;
	private int mId;
	private int mUid;
	private String mAvatar;
	private String mName;
	private String mTitle;
	private String mDescription;
	private String mTime;
	private String mType;
	private int mCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blogcomments);
		mApplication = (BaseApplication) getApplication();
		mHead = LayoutInflater.from(this).inflate(R.layout.blogcomments_head,
				null);
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.blogcomments_back);
		mShare = (ImageView) findViewById(R.id.blogcomments_share);
		mMenu = (ImageView) findViewById(R.id.blogcomments_menu);
		mDisplay = (ListView) findViewById(R.id.blogcomments_display);
		// mBar = (ProgressBar) findViewById(R.id.blogcomments_progressbar);
		mCommment = (EditText) findViewById(R.id.blogcomments_comment);

		mHeadAvatar = (ImageView) mHead
				.findViewById(R.id.blogcomments_head_avatar);
		mHeadName = (TextView) mHead.findViewById(R.id.blogcomments_head_name);
		mHeadTitle = (TextView) mHead
				.findViewById(R.id.blogcomments_head_title);
		mHeadDescription = (TextView) mHead
				.findViewById(R.id.blogcomments_head_description);
		mHeadTime = (TextView) mHead.findViewById(R.id.blogcomments_head_time);
		mHeadCommentCount = (TextView) mHead
				.findViewById(R.id.blogcomments_head_commentcount);
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
				Toast.makeText(BlogComments.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(BlogComments.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mCommment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				Intent intent = new Intent();
				intent.setClass(BlogComments.this, BlogAddCommment.class);
				intent.putExtra("title", "评论");
				intent.putExtra("hint", "添加评论");
				intent.putExtra("id", mId);
				intent.putExtra("uid", mUid);
				intent.putExtra("rid", 0);
				intent.putExtra("type", mType);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
	}

	private void init() {
		mId = getIntent().getIntExtra("id", 0);
		mUid = getIntent().getIntExtra("uid", 0);
		mAvatar = (getIntent().getStringExtra("avatar"));
		mName = getIntent().getStringExtra("name");
		mTitle = getIntent().getStringExtra("title");
		mDescription = getIntent().getStringExtra("description");
		mTime = getIntent().getStringExtra("time");
		mCount = getIntent().getIntExtra("count", 0);
		mType = getIntent().getStringExtra("type");

		mApplication.mHeadBitmap.display(mHeadAvatar, mAvatar);
		mApplication.mText_Util.addIntentLinkToFriendInfo(this, this,
				mHeadName, mName, 0, mName.length(), mUid);
		mApplication.mText_Util.addIntentLinkToBlog(this, this, mHeadTitle,
				mTitle, 0, mTitle.length(), mId, mUid, mName, mDescription,
				mType, mCount);
		mHeadDescription.setText(mDescription);
		mHeadTime.setText(mTime);
		mHeadCommentCount.setText(mCount + "");
		mDisplay.addHeaderView(mHead);
		mAdapter = new BlogCommmentsAdapter(mApplication, this, this, mId,
				mUid, mType);
		mDisplay.setAdapter(mAdapter);
		getBlogComments();
	}

	private void getBlogComments() {
		GetBlogCommentsRequestParam param;
		if ("user".equals(mType)) {
			param = new GetBlogCommentsRequestParam(mApplication.mRenRen, mId,
					mUid, 0, "1", "50");
		} else {
			param = new GetBlogCommentsRequestParam(mApplication.mRenRen, mId,
					0, mUid, "1", "50");
		}

		RequestListener<GetBlogCommentsResponseBean> listener = new RequestListener<GetBlogCommentsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetBlogCommentsResponseBean bean) {
				bean.Resolve(false);
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.getBlogComments(param, listener);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			break;

		case 1:
			getBlogComments();
			break;
		}
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mAdapter.notifyDataSetChanged();
				break;

			default:
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
