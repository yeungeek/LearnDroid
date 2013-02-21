package com.renren.android.photos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.BaseApplication;
import com.renren.android.R;
import com.renren.android.RequestListener;

public class PhotosCreateAlbum extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private Button mCreate;
	private EditText mName;
	private TextView mCompetence;
	private LinearLayout mPasswordLayout;
	private EditText mPassword;
	private String[] mCompetence_items = { "所有人可见", "好友可见", "仅自己可见", "使用密码访问" };
	private String[] mCompetence_Ids = { "99", "1", "-1", "99" };
	private int mPosition;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoscreatealbum);
		mApplication = (BaseApplication) getApplication();
		findViewById();
		setListener();
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.createalbum_back);
		mCreate = (Button) findViewById(R.id.createalbum_create);
		mName = (EditText) findViewById(R.id.createalbum_name);
		mCompetence = (TextView) findViewById(R.id.createalbum_competence);
		mPasswordLayout = (LinearLayout) findViewById(R.id.createalbum_password_layout);
		mPassword = (EditText) findViewById(R.id.createalbum_password);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setResult(0);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mCreate.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String name = mName.getText().toString().trim();
				String visible = mCompetence_Ids[mPosition];
				String password = mPassword.getText().toString().trim();
				if (name.length() == 0 || "".equals(name)) {
					Toast.makeText(PhotosCreateAlbum.this, "请输入名称",
							Toast.LENGTH_SHORT).show();
				} else if (mPosition == 3
						&& (password.length() == 0 || "".equals(password))) {
					Toast.makeText(PhotosCreateAlbum.this, "请输入密码",
							Toast.LENGTH_SHORT).show();
				} else {
					if (mPosition == 3) {
						createAlbum(name, visible, password);
					} else {
						createAlbum(name, visible, null);
					}
				}
			}
		});
		mCompetence.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dialog();
			}
		});
	}

	private void createAlbum(String name, String visible, String password) {
		PhotosCreateAlbumRequestParam param = new PhotosCreateAlbumRequestParam(
				mApplication.mRenRen, name, visible, password);
		RequestListener<PhotosCreateAlbumResponseBean> listener = new RequestListener<PhotosCreateAlbumResponseBean>() {

			public void onStart() {

			}

			public void onComplete(PhotosCreateAlbumResponseBean bean) {
				Intent intent = new Intent();
				intent.putExtra("aid", bean.aid);
				intent.putExtra("name", mName.getText().toString().trim());
				setResult(1, intent);
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		};
		mApplication.mAsyncRenRen.PhotosCreateAlbum(param, listener);
	}

	private void dialog() {
		final int position = mPosition;
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("选择权限");
		builder.setSingleChoiceItems(mCompetence_items, mPosition,
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						mPosition = which;
					}
				});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				mCompetence.setText(mCompetence_items[mPosition]);
				if (mPosition == 3) {
					mPasswordLayout.setVisibility(View.VISIBLE);
					mPassword.setText("");
				} else {
					mPasswordLayout.setVisibility(View.GONE);
				}
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			public void onCancel(DialogInterface dialog) {
				mPosition = position;
			}
		});
		builder.create().show();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(0);
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
