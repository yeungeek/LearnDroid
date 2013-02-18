package com.anhuioss.crowdroid.cfb;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.StaticLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;

public class UploadPicActivity extends Activity implements ServiceConnection,
		OnClickListener {

	private int mark;

	private int TYPE;

	private String album_id;

	private String filePath0;

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private CheckBox retweetBox;

	private ImageView showImage;

	private TextView t_d;

	private EditText description;

	private TextView t_a;

	private EditText urlText;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private Button up;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				Toast.makeText(UploadPicActivity.this,
						getResources().getString(R.string.success),
						Toast.LENGTH_SHORT).show();
				finish();
			} else if (statusCode != null && statusCode.equals("501")) {
				switch (mark) {
				case 1: {

					openSelectDialog(1);
					break;
				}
				case 2: {
					openSelectDialog(2);
					break;
				}

				}
			}

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.upload_photo_album);

		showImage = (ImageView) findViewById(R.id.image);
		t_d = (TextView) findViewById(R.id.t_d);
		description = (EditText) findViewById(R.id.description);
		up = (Button) findViewById(R.id.up);
		retweetBox = (CheckBox) findViewById(R.id.retweetBox);
		showImage.setVisibility(View.GONE);
		description.setVisibility(View.GONE);
		up.setVisibility(View.GONE);
		t_d.setVisibility(View.GONE);
		retweetBox.setVisibility(View.GONE);

		t_a = (TextView) findViewById(R.id.textView1);
		urlText = (EditText) findViewById(R.id.editText1);
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerBack.setVisibility(View.GONE);
		headerName.setVisibility(View.GONE);
		headerHome.setVisibility(View.GONE);

		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);

		up.setOnClickListener(this);
		Bundle bundle = getIntent().getExtras();
		album_id = bundle.getString("album_id");
		mark = bundle.getInt("mark");

		switch (mark) {
		case 1: {
			TYPE = CommHandler.TYPE_CFB_UPLOAD_PHOTO_ALBUM;
			Intent intentimage = new Intent();
			intentimage.setType("image/*");
			intentimage.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentimage, 1);
			break;
		}
		case 2: {
			TYPE = CommHandler.TYPE_CFB_UPLOAD_DOCUMENT_ALBUM;
			Intent intentdoc = new Intent();
			intentdoc.setType("sdcard/*");
			// intent.setDataAndType(Uri.fromFile(new File("/sdcard")), "*/*");
			// // 设置起始文件夹和文件类型
			intentdoc.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intentdoc, 2);
			break;
		}
		case 3: {
			// showImage.setVisibility(View.VISIBLE);
			description.setVisibility(View.VISIBLE);
			up.setVisibility(View.VISIBLE);
			t_d.setVisibility(View.VISIBLE);
			t_a.setVisibility(View.VISIBLE);
			urlText.setVisibility(View.VISIBLE);
			headerBack.setVisibility(View.VISIBLE);
			headerHome.setVisibility(View.VISIBLE);
			headerName.setVisibility(View.VISIBLE);
			TYPE = CommHandler.TYPE_CFB_UPLOAD_VIDEO_ALBUM;
			// Intent intent = new Intent();
			// intent.setType("/*");
			// intent.setAction(Intent.ACTION_GET_CONTENT);
			// startActivityForResult(intent, 1);
			headerName.setText(getResources().getString(R.string.uploadVedio));
			break;
		}
		}

	}

	@Override
	public void onStart() {
		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		statusData = crowdroidApplication.getStatusData();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();

		// Unbind Service
		unbindService(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		isRunning = false;
		if (progress != null) {
			progress.dismiss();
		}
		TimelineActivity.isBackgroundNotificationFlag = true;
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 1) {

				showImage.setVisibility(View.VISIBLE);
				description.setVisibility(View.VISIBLE);
				up.setVisibility(View.VISIBLE);
				t_d.setVisibility(View.VISIBLE);
				headerBack.setVisibility(View.VISIBLE);
				headerHome.setVisibility(View.VISIBLE);
				headerName.setVisibility(View.VISIBLE);
				headerName.setText(getResources().getString(
						R.string.uploadPhoto));
				headerHome.setBackgroundResource(R.drawable.main_app);
				Uri selectedImageUri = data.getData();

				// if (selectedImageUri.toString().contains("file://"))
				// filePath0 = selectedImageUri.toString().substring(7,
				// selectedImageUri.toString().length());
				// if (selectedImageUri.toString().contains("content://"))
				// filePath0 = selectedImageUri.toString().substring(9,
				// selectedImageUri.toString().length());
				String[] projection = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(selectedImageUri, projection,
						null, null, null);
				if (cursor != null) {
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					String selectedImagePath = cursor.getString(column_index);

					filePath0 = selectedImagePath;
					if (filePath0.equals("") || filePath0 == null) {
						Toast.makeText(UploadPicActivity.this, "还没有选择文件！",
								Toast.LENGTH_LONG).show();
					}

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 2;
					Bitmap bm = BitmapFactory.decodeFile(filePath0, options);
					showImage.setImageBitmap(bm);

				}

			}
			if (requestCode == 2) {

				String url = Uri.decode(data.getDataString());
				showImage.setVisibility(View.VISIBLE);
				description.setVisibility(View.VISIBLE);
				up.setVisibility(View.VISIBLE);
				t_d.setVisibility(View.VISIBLE);
				headerBack.setVisibility(View.VISIBLE);
				headerHome.setVisibility(View.VISIBLE);
				headerName.setVisibility(View.VISIBLE);
				headerName
						.setText(getResources().getString(R.string.uploadDoc));
				headerHome.setBackgroundResource(R.drawable.main_app);
				showImage.setImageResource(R.drawable.file_icon);
				// Uri selectedImageUri = data.getData();
				Uri selectedImageUri = Uri.parse(url);

				if (selectedImageUri.toString().contains("file://"))
					filePath0 = selectedImageUri.toString().substring(7,
							selectedImageUri.toString().length());
				if (selectedImageUri.toString().contains("content://"))
					filePath0 = selectedImageUri.toString().substring(28,
							selectedImageUri.toString().length());
				if (filePath0.equals("") || filePath0 == null) {
					Toast.makeText(UploadPicActivity.this, "还没有选择文件！",
							Toast.LENGTH_LONG).show();
				}
				// String[] projection = { MediaStore.Images.Media.DATA };
				// Cursor cursor = managedQuery(selectedImageUri, projection,
				// null, null, null);
				// if (cursor != null) {
				// int column_index = cursor
				// .getColumnIndexOrThrow(MediaStore..Media.DATA);
				// cursor.moveToFirst();
				// String selectedImagePath = cursor.getString(column_index);
				//
				// filePath0 = selectedImagePath;
				// BitmapFactory.Options options = new BitmapFactory.Options();
				// options.inSampleSize = 2;
				// Bitmap bm = BitmapFactory.decodeFile(filePath0, options);
				// showImage.setImageBitmap(bm);
				//
				// }
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.up: {
			if (filePath0 != null
					|| TYPE == CommHandler.TYPE_CFB_UPLOAD_VIDEO_ALBUM) {
				try {

					showProgressDialog();
					String descriptionText = description.getText().toString() != "" ? description
							.getText().toString() : "";
					Map<String, Object> parameters;
					parameters = new HashMap<String, Object>();
					parameters.put("id", album_id);
					parameters.put("description", descriptionText);
					parameters.put("filePath", filePath0);

					parameters.put("url", urlText.getText().toString());
					// Request
					apiServiceInterface.request(statusData.getCurrentService(),
							TYPE, apiServiceListener, parameters);

				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}

			break;
		}
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			Intent intent = new Intent(UploadPicActivity.this,
					DiscoveryActivity.class);
			startActivity(intent);
			break;
		}

		default:
			break;
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		apiServiceInterface = null;

	}

	private void showProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress == null) {
			progress = new HandleProgressDialog(UploadPicActivity.this);
		}
		progress.show();
	}

	private void closeProgressDialog() {
		if (!isRunning) {
			return;
		}
		if (progress != null) {
			progress.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void openSelectDialog(final int mark) {
		String msg = "";
		if (mark == 1) {
			msg = getResources().getString(R.string.uploadPhotoDialogTip);
		} else if (mark == 2) {
			msg = getResources().getString(R.string.uploadDocDialogTip);
		}
		AlertDialog dialog = new AlertDialog.Builder(UploadPicActivity.this)
				.setMessage(msg)
				.setPositiveButton("ok", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (mark == 1) {
							TYPE = CommHandler.TYPE_CFB_UPLOAD_PHOTO_ALBUM;
							Intent intentimage = new Intent();
							intentimage.setType("image/*");
							intentimage.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentimage, 1);
						} else if (mark == 2) {
							TYPE = CommHandler.TYPE_CFB_UPLOAD_DOCUMENT_ALBUM;
							Intent intentdoc = new Intent();
							intentdoc.setType("sdcard/*");
							// intent.setDataAndType(Uri.fromFile(new
							// File("/sdcard")), "*/*");
							// // 设置起始文件夹和文件类型
							intentdoc.setAction(Intent.ACTION_GET_CONTENT);
							startActivityForResult(intentdoc, 2);
						}

					}
				})
				.setNegativeButton("cancel",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								finish();

							}
						}).create();
		dialog.show();
	}

}
