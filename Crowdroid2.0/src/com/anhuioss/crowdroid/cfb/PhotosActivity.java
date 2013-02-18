package com.anhuioss.crowdroid.cfb;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;

public class PhotosActivity extends BasicActivity implements ServiceConnection,
		OnClickListener {

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	private ScrollLayout mScrollLayout;

	private PageControlView pageControl;

	private static final float APP_PAGE_SIZE = 9.0f;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private boolean refreshFlag = false;

	private Intent intent;

	private Bundle bundle = null;

	private String filePath = "/sdcard/";

	private String fileName = null;

	private String album_id = null;

	GridView appPage;

	private int mark;

	private int TYPE;
	
	private int RT_TYPE;

	AutoCompleteTextView edit;

	private StatusData statusData;

	private CrowdroidApplication crowdroidApplication;

	private AccountData currentAccount;

	private ArrayList<TimeLineInfo> timelineInfoList;

	private int photoPos = 0;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				if (type == CommHandler.TYPE_CFB_DEL_ALBUM_PHOTO) {
					Toast.makeText(
							PhotosActivity.this,
							getResources().getString(
									R.string.delPhotoSuccessTip),
							Toast.LENGTH_SHORT).show();
					finish();
				} else if (type == CommHandler.TYPE_CFB_RT_PHOTO
						|| type == CommHandler.TYPE_CFB_RT_DOCUMENT
						|| type == CommHandler.TYPE_CFB_RT_VIDEO) {
					Toast.makeText(PhotosActivity.this,
							getResources().getString(R.string.rtsuccessTip),
							Toast.LENGTH_SHORT).show();
					// finish();
				} else {

					refreshFlag = true;
					// Parser
					timelineInfoList = new ArrayList<TimeLineInfo>();
					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, type, statusCode, message);

					if (timelineInfoList != null && timelineInfoList.size() > 0) {

						mScrollLayout.removeView(appPage);
						createGridView();
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
		setContentView(R.layout.scroll_layout);

		findViews();
	}

	@Override
	public void onStart() {
		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		Bundle bundle = getIntent().getExtras();
		album_id = bundle.getString("album_id");
		mark = bundle.getInt("mark");
		switch (mark) {
		case 1: {
			TYPE = CommHandler.TYPE_GET_ALBUM_PHOTOS;
			headerName.setText(getResources().getString(R.string.photoList));
			break;
		}
		case 2: {
			TYPE = CommHandler.TYPE_GET_ALBUM_DOCUMENT;
			headerName.setText(getResources().getString(R.string.docList));
			break;
		}
		case 3: {
			TYPE = CommHandler.TYPE_GET_ALBUM_VIDEO;
			headerName.setText(getResources().getString(R.string.vedioList));
			break;
		}
		}

		edit = new AutoCompleteTextView(crowdroidApplication);
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
	}

	public void findViews() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayoutTest);

		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerHome.setBackgroundResource(R.drawable.refresh);

		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void createGridView() {
		final int PageCount = (int) Math.ceil(timelineInfoList.size()
				/ APP_PAGE_SIZE);

		for (int i = 0; i < PageCount; i++) {
			appPage = new GridView(this);
			// get the "i" page data
			appPage.setAdapter(new AlbumsAdapter(this, timelineInfoList, i,
					appPage, mark));

			appPage.setNumColumns(3);
			appPage.setVerticalSpacing(20);
			appPage.setOnItemClickListener(listener);
			mScrollLayout.addView(appPage);
		}
		pageControl = (PageControlView) findViewById(R.id.pageControl);
		pageControl.bindScrollViewGroup(mScrollLayout);
	}

	public OnItemClickListener listener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int pos,
				long arg3) {
			if (mark == 1)
				openPhotoManager((TimeLineInfo) parent.getItemAtPosition(pos));

			else if (mark == 2)
				openManager((TimeLineInfo) parent.getItemAtPosition(pos));

			else if (mark == 3)
				openVideoManager((TimeLineInfo) parent.getItemAtPosition(pos));
		}
	};

	private void openPhotoManager(final TimeLineInfo info) {
		bundle = new Bundle();
		String[] items = null;
		if (currentAccount.getUid().equals(info.getUserInfo().getUid())) {
			items = getResources().getStringArray(R.array.cfb_photo_del_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论", "设图片为专辑封面", "保存图片",
			// "删除" };
		} else {
			items = getResources().getStringArray(R.array.cfb_photo_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论", "设图片为专辑封面", "保存图片"
			// };
		}

		AlertDialog dialog = new AlertDialog.Builder(PhotosActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {

							intent = new Intent(PhotosActivity.this,
									AlbumDetailActivity.class);

							bundle.putString("album_id", info.getMessageId());
							bundle.putString("id", info.getStatusId());
							bundle.putString("photo", "photo");
							bundle.putInt("mark", mark);
							intent.putExtras(bundle);
							startActivity(intent);

							break;
						}
						case 1: {
							photoPos = 1;
							tweet(info);
							// new
							// DownloadFileAsync().execute(info.getUserInfo()
							// .getUserImageURL());

							break;
						}
						case 2: {
							intent = new Intent(PhotosActivity.this,
									CommentPhotoActivity.class);

							intent.putExtra("album_id", info.getStatusId());
							intent.putExtra("url", info.getUserInfo()
									.getUserImageURL());
							intent.putExtra("user_id", info.getUserInfo()
									.getUid());
							intent.putExtra("mark", mark);
							startActivity(intent);
							break;
						}
						case 3: {

							intent = new Intent(PhotosActivity.this,
									SetAlbumCoverActivity.class);

							intent.putExtra("album_id", info.getStatusId());
							startActivity(intent);

							break;
						}
						case 4: {
							photoPos = 4;
							new DownloadFileAsync().execute(info.getUserInfo()
									.getUserImageURL());

							break;
						}
						case 5: {

							delPhotoDialog(info);

							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	private void openManager(final TimeLineInfo info) {
		bundle = new Bundle();
		String[] items = null;
		if (currentAccount.getUid().equals(info.getUserInfo().getUid())) {
			items = getResources().getStringArray(R.array.cfb_doc_del_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论", "下载", "删除" };
		} else {
			items = getResources().getStringArray(R.array.cfb_doc_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论", "下载" };
		}

		AlertDialog dialog = new AlertDialog.Builder(PhotosActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							intent = new Intent(PhotosActivity.this,
									AlbumDetailActivity.class);

							bundle.putString("album_id", info.getMessageId());
							bundle.putString("id", info.getStatusId());
							bundle.putInt("mark", mark);
							bundle.putString("doc", "doc");
							intent.putExtras(bundle);
							startActivity(intent);

							break;
						}
						case 1: {

							tweet(info);
							// new
							// DownloadFileAsync().execute(info.getUserInfo()
							// .getUserImageURL());

							break;
						}
						case 2: {
							intent = new Intent(PhotosActivity.this,
									CommentPhotoActivity.class);

							intent.putExtra("album_id", info.getStatusId());
							intent.putExtra("url", info.getUserInfo()
									.getUserImageURL());
							intent.putExtra("user_id", info.getUserInfo()
									.getUid());
							intent.putExtra("mark", mark);
							startActivity(intent);
							break;
						}
						case 3: {

							new DownloadFileAsync().execute(info.getUserInfo()
									.getUserImageURL());

							break;
						}
						case 4: {

							delPhotoDialog(info);

							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	private void openVideoManager(final TimeLineInfo info) {
		bundle = new Bundle();
		String[] items = null;
		if (currentAccount.getUid().equals(info.getUserInfo().getUid())) {
			items = getResources().getStringArray(R.array.cfb_vedio_del_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论", "删除" };
		} else {
			items = getResources().getStringArray(R.array.cfb_vedio_dialog);
			// items = new String[] { "查看详细信息", "转发", "评论" };
		}

		AlertDialog dialog = new AlertDialog.Builder(PhotosActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							intent = new Intent(PhotosActivity.this,
									AlbumDetailActivity.class);
							bundle.putString("album_id", info.getMessageId());
							bundle.putString("id", info.getStatusId());
							bundle.putString("video", "video");
							bundle.putInt("mark", mark);
							intent.putExtras(bundle);
							startActivity(intent);

							break;
						}
						case 1: {

							tweet(info);

							break;
						}
						case 2: {
							intent = new Intent(PhotosActivity.this,
									CommentPhotoActivity.class);

							intent.putExtra("album_id", info.getStatusId());
							intent.putExtra("url", info.getUserInfo()
									.getUserImageURL());
							intent.putExtra("user_id", info.getUserInfo()
									.getUid());
							intent.putExtra("mark", mark);
							startActivity(intent);
							break;
						}
						case 3: {

							delPhotoDialog(info);

							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	private void delPhotoDialog(final TimeLineInfo info) {
		AlertDialog dialog = new AlertDialog.Builder(PhotosActivity.this)
				.setTitle(getResources().getString(R.string.delPhoto))
				.setMessage(getResources().getString(R.string.delPhotoTip))
				.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {

							showProgressDialog();
							Map<String, Object> map;
							map = new HashMap<String, Object>();
							map.put("albumId", info.getMessageId());
							map.put("fileId", info.getStatusId());

							// Request
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_CFB_DEL_ALBUM_PHOTO,
									apiServiceListener, map);
						} catch (RemoteException e) {
							e.printStackTrace();
						}

					}
				})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}

						}).create();
		dialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.head_back: {
			finish();
			break;
		}
		case R.id.head_refresh: {
			try {
				if (timelineInfoList != null) {
					timelineInfoList.clear();
					timelineInfoList = null;
				}
				if (appPage != null) {
					appPage = null;
				}
				if (pageControl != null) {
					pageControl = null;
				}
				if (mScrollLayout.getChildCount() != 0) {
					mScrollLayout.removeAllViews();
				}

				showProgressDialog();

				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("id", album_id);
				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						TYPE, apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}

		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		apiServiceInterface = ApiServiceInterface.Stub.asInterface(service);
		if (data.isEmpty() && refreshFlag == false) {
			try {

				showProgressDialog();
				Map<String, Object> parameters;
				parameters = new HashMap<String, Object>();
				parameters.put("id", album_id);
				// parameters.put("album_mark", "2");

				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						TYPE, apiServiceListener, parameters);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

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
			progress = new HandleProgressDialog(PhotosActivity.this);
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

	class DownloadFileAsync extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... aurl) {
			fileName = aurl[0].substring(aurl[0].lastIndexOf("/") + 1,
					aurl[0].length());

			int count;
			try {

				URL conurl = new URL(aurl[0]);
				File file = new File(filePath + fileName);

				InputStream input = new BufferedInputStream(conurl.openStream());
				OutputStream output = new FileOutputStream(file);
				byte data[] = new byte[1024];

				while ((count = input.read(data)) != -1) {

					output.write(data, 0, count);
				}
				output.flush();
				output.close();
				input.close();

			} catch (Exception e) {
				Log.e("error", e.getMessage().toString());
			}
			return filePath + fileName;
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);

		}

		@Override
		protected void onPostExecute(String result) {
			intent = new Intent(PhotosActivity.this, SendMessageActivity.class);

			if (mark == 1 && photoPos == 1) {
				intent.putExtra("filePath", result);

				startActivity(intent);
			} else {
				Toast.makeText(PhotosActivity.this, getResources().getString(R.string.downFileTip) + result,
						Toast.LENGTH_SHORT).show();
			}

		}

	}

	private void tweet(final TimeLineInfo info) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		// EditText edit = new EditText(crowdroidApplication);

		alert.setTitle(getResources().getString(R.string.retweet_count))
				.setMessage(getResources().getString(R.string.rtTip))
				.setView(edit)
				.setPositiveButton(getResources().getString(R.string.retweet_count), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						switch (mark) {
						case 1: {
							RT_TYPE = CommHandler.TYPE_CFB_RT_PHOTO;
							break;
						}
						case 2: {
							RT_TYPE = CommHandler.TYPE_CFB_RT_DOCUMENT;
							break;
						}
						case 3: {
							RT_TYPE = CommHandler.TYPE_CFB_RT_VIDEO;
							break;
						}
						}
						try {

							showProgressDialog();
							Map<String, Object> parameters;
							parameters = new HashMap<String, Object>();
							parameters.put("id", info.getStatusId());
							parameters.put("content", edit.getText());

							// Request
							apiServiceInterface.request(
									statusData.getCurrentService(), RT_TYPE,
									apiServiceListener, parameters);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

				})
				.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub

					}
				}).create();
		alert.show();
	}

	@Override
	protected void onDestroy() {
		// final GridView grid = appPage;
		// final int count = grid.getChildCount();
		// ImageView v = null;
		// for (int i = 0; i < count; i++) {
		// v = (ImageView) grid.getChildAt(i);
		// ((BitmapDrawable) v.getDrawable()).setCallback(null);
		// Log.e("photo", "photo");
		// }

		super.onDestroy();
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

}
