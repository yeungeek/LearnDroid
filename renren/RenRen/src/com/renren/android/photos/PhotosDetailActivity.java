package com.renren.android.photos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.util.AsyncImageLoader;
import com.renren.android.util.AsyncImageLoader.ImageCallBack;

public class PhotosDetailActivity extends Activity {
	private BaseApplication mApplication;
	private LinearLayout mTop;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mShare;
	private ImageView mMenu;
	private LinearLayout mBottom;
	private TextView mCaption;
	private TextView mViewCount;
	private EditText mComment;
	private Button mCommentCount;
	private ProgressBar mBar;
	private PhotosGallery mGallery;
	private PhotosGalleryAdapter mAdapter;

	public static int mScreenWidth;
	public static int mScreenHeight;

	private String mAlbumName;
	private int mCurrentItem;
	private int mTotalCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosdetail);
		mApplication = (BaseApplication) getApplication();
		Display display = getWindowManager().getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mTop = (LinearLayout) findViewById(R.id.photosdetail_top);
		mBack = (ImageView) findViewById(R.id.photosdetail_back);
		mTitle = (TextView) findViewById(R.id.photosdetail_title);
		mShare = (ImageView) findViewById(R.id.photosdetail_share);
		mMenu = (ImageView) findViewById(R.id.photosdetail_menu);
		mBottom = (LinearLayout) findViewById(R.id.photosdetail_bottom);
		mCaption = (TextView) findViewById(R.id.photosdetail_caption);
		mViewCount = (TextView) findViewById(R.id.photosdetail_viewcount);
		mComment = (EditText) findViewById(R.id.photosdetail_comment);
		mCommentCount = (Button) findViewById(R.id.photosdetail_commentcount);
		mBar = (ProgressBar) findViewById(R.id.photosdetail_progressbar);
		mGallery = (PhotosGallery) findViewById(R.id.photosdetail_gallery);
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
				Toast.makeText(PhotosDetailActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosDetailActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mComment.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PhotosDetailActivity.this,
						PhotosAddCommentActivity.class);
				intent.putExtra("title", "评论");
				intent.putExtra("hint", "添加评论");
				intent.putExtra("aid",
						RenRenData.mPhotosResults.get(mCurrentItem).getAid());
				intent.putExtra("pid",
						RenRenData.mPhotosResults.get(mCurrentItem).getPid());
				intent.putExtra("uid",
						RenRenData.mPhotosResults.get(mCurrentItem).getUid());
				intent.putExtra("rid", 0);
				startActivityForResult(intent, 0);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mCommentCount.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(PhotosDetailActivity.this,
						PhotosCommentsActivity.class);
				intent.putExtra("uid",
						RenRenData.mPhotosResults.get(mCurrentItem).getUid());
				intent.putExtra("aid",
						RenRenData.mPhotosResults.get(mCurrentItem).getAid());
				intent.putExtra("pid",
						RenRenData.mPhotosResults.get(mCurrentItem).getPid());
				intent.putExtra("headurl",
						mApplication.mRenRen.getUserHeadUrl_Main());
				intent.putExtra("name", mApplication.mRenRen.getUserName());
				intent.putExtra("caption",
						RenRenData.mPhotosResults.get(mCurrentItem)
								.getCaption());
				intent.putExtra("url",
						RenRenData.mPhotosResults.get(mCurrentItem)
								.getUrl_large());
				intent.putExtra("albumName", mAlbumName);
				intent.putExtra("time",
						RenRenData.mPhotosResults.get(mCurrentItem).getTime());
				intent.putExtra("comment_count",
						RenRenData.mPhotosResults.get(mCurrentItem)
								.getComment_count());
				startActivity(intent);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mGallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mTop.isShown() && mBottom.isShown()) {
					Animation anim = AnimationUtils.loadAnimation(
							PhotosDetailActivity.this, R.anim.fade_out);
					mTop.setAnimation(anim);
					mBottom.setAnimation(anim);
					mTop.setVisibility(View.GONE);
					mBottom.setVisibility(View.GONE);
				} else {
					Animation anim = AnimationUtils.loadAnimation(
							PhotosDetailActivity.this, R.anim.fade_in);
					mTop.setAnimation(anim);
					mBottom.setAnimation(anim);
					mTop.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.VISIBLE);
				}
			}
		});
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentItem = position;
				setValueToView();
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		mTotalCount = intent.getIntExtra("count", 0);
		mCurrentItem = intent.getIntExtra("position", 0);
		mAlbumName = intent.getStringExtra("albumName");
		mAdapter = new PhotosGalleryAdapter(PhotosDetailActivity.this);
		mGallery.setVerticalFadingEdgeEnabled(false);
		mGallery.setHorizontalFadingEdgeEnabled(false);
		mGallery.setAdapter(mAdapter);
		mGallery.setSelection(mCurrentItem);
	}

	private void setValueToView() {
		mTitle.setText((mCurrentItem + 1) + "/" + mTotalCount);
		mViewCount.setText("浏览"
				+ RenRenData.mPhotosResults.get(mCurrentItem).getView_count());
		mCommentCount.setText(RenRenData.mPhotosResults.get(mCurrentItem)
				.getComment_count() + "");
		if (RenRenData.mPhotosResults.get(mCurrentItem).getCaption() == null
				|| RenRenData.mPhotosResults.get(mCurrentItem).getCaption()
						.length() == 0) {
			mCaption.setVisibility(View.GONE);
		} else {
			mCaption.setVisibility(View.VISIBLE);
			mCaption.setText(RenRenData.mPhotosResults.get(mCurrentItem)
					.getCaption());
		}
	}

	public class PhotosGalleryAdapter extends BaseAdapter {
		private Context mContext;
		private AsyncImageLoader mLoader;

		PhotosGalleryAdapter(Context context) {
			mContext = context;
			mLoader = new AsyncImageLoader();
		}

		public int getCount() {
			return RenRenData.mPhotosResults.size();
		}

		public Object getItem(int position) {
			return RenRenData.mPhotosResults.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, final View convertView,
				ViewGroup parent) {
			PhotosImageView view = null;
			if (convertView == null) {
				view = new PhotosImageView(mContext);
				view.setLayoutParams(new Gallery.LayoutParams(mScreenWidth,
						mScreenHeight));
			} else {
				view = (PhotosImageView) convertView;
			}
			PhotosResult result = RenRenData.mPhotosResults.get(position);
			String imageName = result.getUrl_large().substring(
					result.getUrl_large().lastIndexOf("/") + 1,
					result.getUrl_large().length());
			Bitmap bitmap = mLoader.loadBitmap(result.getUrl_large(), view,
					imageName, new ImageCallBack() {

						public void imageLoad(ImageView imageView, Bitmap bitmap) {
							mBar.setVisibility(View.GONE);
							float scale = getScale(bitmap);
							int bitmapWidth = (int) (bitmap.getWidth() * scale);
							int bitmapHeight = (int) (bitmap.getHeight() * scale);

							Bitmap zoomBitmap = Bitmap.createScaledBitmap(
									bitmap, bitmapWidth, bitmapHeight, true);
							((PhotosImageView) imageView)
									.setImageWidth(bitmapWidth);
							((PhotosImageView) imageView)
									.setImageHeight(bitmapHeight);
							((PhotosImageView) imageView)
									.setImageBitmap(zoomBitmap);
						}
					});
			if (bitmap == null) {
				mBar.setVisibility(View.VISIBLE);
			} else {
				mBar.setVisibility(View.GONE);
				float scale = getScale(bitmap);
				int bitmapWidth = (int) (bitmap.getWidth() * scale);
				int bitmapHeight = (int) (bitmap.getHeight() * scale);

				Bitmap zoomBitmap = Bitmap.createScaledBitmap(bitmap,
						bitmapWidth, bitmapHeight, true);
				view.setImageWidth(bitmapWidth);
				view.setImageHeight(bitmapHeight);
				view.setImageBitmap(zoomBitmap);
			}
			return view;
		}
	}

	private float getScale(Bitmap bitmap) {
		float scaleWidth = mScreenWidth / (float) bitmap.getWidth();
		float scaleHeight = mScreenHeight / (float) bitmap.getHeight();
		return Math.min(scaleWidth, scaleHeight);
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
