package com.renren.android.photos;

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

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;

public class PhotosCommentsActivity extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private ImageView mShare;
	private ImageView mMenu;
	private ListView mDisplay;
	private EditText mComment;
	private PhotosCommentsAdapter mAdapter;

	private View mHead;
	private ImageView mHead_Avatar;
	private TextView mHead_Name;
	private TextView mHead_Caption;
	private ImageView mHead_Img;
	private TextView mHead_AlbumName;
	private TextView mHead_Time;
	private TextView mHead_CommentCount;

	private int mUid;
	private long mAid;
	private long mPid;
	private String mHeadurl;
	private String mName;
	private String mCaption;
	private String mUrl;
	private String mAlbumName;
	private String mTime;
	private int mCommentCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoscomments);
		mApplication = (BaseApplication) getApplication();
		mHead = LayoutInflater.from(this).inflate(R.layout.photoscomments_head,
				null);
		findViewById();
		setListener();
		init();
		getPhotosComments();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photoscomments_back);
		mShare = (ImageView) findViewById(R.id.photoscomments_share);
		mMenu = (ImageView) findViewById(R.id.photoscomments_menu);
		mDisplay = (ListView) findViewById(R.id.photoscomments_display);
		mComment = (EditText) findViewById(R.id.photoscomments_comment);

		mHead_Avatar = (ImageView) mHead
				.findViewById(R.id.photoscomments_head_avatar);
		mHead_Name = (TextView) mHead
				.findViewById(R.id.photoscomments_head_name);
		mHead_Caption = (TextView) mHead
				.findViewById(R.id.photoscomments_head_caption);
		mHead_Img = (ImageView) mHead
				.findViewById(R.id.photoscomments_head_img);
		mHead_AlbumName = (TextView) mHead
				.findViewById(R.id.photoscomments_head_albumname);
		mHead_Time = (TextView) mHead
				.findViewById(R.id.photoscomments_head_time);
		mHead_CommentCount = (TextView) mHead
				.findViewById(R.id.photoscomments_head_commentcount);
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

			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});
		mComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PhotosCommentsActivity.this,
						PhotosAddCommentActivity.class);
				intent.putExtra("title", "ÆÀÂÛ");
				intent.putExtra("hint", "Ìí¼ÓÆÀÂÛ");
				intent.putExtra("aid", mAid);
				intent.putExtra("pid", mPid);
				intent.putExtra("uid", mUid);
				intent.putExtra("rid", 0);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		mUid = intent.getIntExtra("uid", 0);
		mAid = intent.getLongExtra("aid", 0);
		mPid = intent.getLongExtra("pid", 0);
		mHeadurl = intent.getStringExtra("headurl");
		mName = intent.getStringExtra("name");
		mCaption = intent.getStringExtra("caption");
		mUrl = intent.getStringExtra("url");
		mAlbumName = intent.getStringExtra("albumName");
		mTime = intent.getStringExtra("time");
		mCommentCount = intent.getIntExtra("comment_count", 0);
		mApplication.mHeadBitmap.display(mHead_Avatar, mHeadurl);
		mHead_Name.setText(mName);
		if (mCaption != null && !mCaption.equals("")) {
			mHead_Caption.setVisibility(View.VISIBLE);
			mHead_Caption.setText(mCaption);
		} else {
			mHead_Caption.setVisibility(View.GONE);
		}
		mApplication.mPhotoBitmap.display(mHead_Img, mUrl);
		mHead_AlbumName.setText(mAlbumName);
		mHead_Time.setText(mTime);
		mHead_CommentCount.setText(mCommentCount + "");

		mDisplay.addHeaderView(mHead);
		mAdapter = new PhotosCommentsAdapter(mApplication,PhotosCommentsActivity.this,
				PhotosCommentsActivity.this, mApplication.mRenRen, mUid, mAid,
				mPid);
		mDisplay.setAdapter(mAdapter);
	}

	private void getPhotosComments() {
		GetPhotosCommentsRequestParam param = new GetPhotosCommentsRequestParam(
				mApplication.mRenRen, mApplication.mRenRen.getUserId(), mAid,
				mPid, null, "50");
		RequestListener<GetPhotosCommentsResponseBean> listener = new RequestListener<GetPhotosCommentsResponseBean>() {

			public void onStart() {

			}

			public void onComplete(GetPhotosCommentsResponseBean bean) {
				bean.Resolve(false);
				handler.sendEmptyMessage(0);
			}
		};
		mApplication.mAsyncRenRen.getPhotosComments(param, listener);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (resultCode) {
		case 0:
			break;

		case 1:
			getPhotosComments();
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
