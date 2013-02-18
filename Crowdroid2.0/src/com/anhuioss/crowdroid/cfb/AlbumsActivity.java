package com.anhuioss.crowdroid.cfb;

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
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.BasicActivity;
import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.communication.ApiServiceInterface;
import com.anhuioss.crowdroid.communication.ApiServiceListener;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.dialog.HandleProgressDialog;
import com.anhuioss.crowdroid.service.CommHandler;
import com.anhuioss.crowdroid.service.ParseHandler;


public class AlbumsActivity extends BasicActivity implements ServiceConnection,
		OnClickListener {

	private ScrollLayout mScrollLayout;

	private PageControlView pageControl;

	private static final float APP_PAGE_SIZE = 9.0f;

	private Context mContext;

	private Button headerBack = null;

	private TextView headerName = null;

	private Button headerHome = null;

	// Progress Dialog
	private HandleProgressDialog progress = null;

	private static boolean isRunning = true;

	private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

	private StatusData statusData;

	private AccountData currentAccount;

	private CrowdroidApplication crowdroidApplication;

	private ArrayList<TimeLineInfo> timelineInfoList = new ArrayList<TimeLineInfo>();

	private boolean refreshFlag = false;

	private Intent intent;

	private Bundle bundle = null;

	private String[] items = null;

	private AlbumsAdapter adapter;

	GridView appPage;

	private int mark;

	private int TYPE;

	private ApiServiceInterface apiServiceInterface;

	private ApiServiceListener.Stub apiServiceListener = new ApiServiceListener.Stub() {

		@SuppressWarnings("unchecked")
		@Override
		public void requestCompleted(String service, int type,
				String statusCode, String message) throws RemoteException {
			closeProgressDialog();
			if (statusCode != null && statusCode.equals("200")
					&& message != null) {
				if (type == CommHandler.TYPE_CFB_DEL_ALBUM) {
					Toast.makeText(AlbumsActivity.this, getResources().getString(R.string.delPhotoSuccessTip),
							Toast.LENGTH_SHORT).show();
				} else {
					// if (timelineInfoList != null) {
					// timelineInfoList.clear();
					//
					// }
					refreshFlag = true;
					// Parser
					// timelineInfoList.clear();
					ParseHandler parseHandler = new ParseHandler();
					timelineInfoList = (ArrayList<TimeLineInfo>) parseHandler
							.parser(service, TYPE, statusCode, message);

					if (timelineInfoList != null && timelineInfoList.size() > 0) {
						
						ArrayList<TimeLineInfo> infos = new ArrayList<TimeLineInfo>();
						for(int i = timelineInfoList.size()-1; i >= 0; i--){
							Log.v("size", String.valueOf(timelineInfoList.size()));
							infos.add(timelineInfoList.get(i));
						}
						timelineInfoList.clear();
						timelineInfoList = infos;
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
		mContext = this;

		mScrollLayout = (ScrollLayout) findViewById(R.id.ScrollLayoutTest);
		headerBack = (Button) findViewById(R.id.head_back);
		headerName = (TextView) findViewById(R.id.head_Name);
		headerHome = (Button) findViewById(R.id.head_refresh);
		headerHome.setBackgroundResource(R.drawable.refresh);

		headerBack.setOnClickListener(this);
		headerHome.setOnClickListener(this);
		bundle = new Bundle();

	}

	@Override
	public void onStart() {
		isRunning = true;
		crowdroidApplication = (CrowdroidApplication) getApplicationContext();

		statusData = crowdroidApplication.getStatusData();
		currentAccount = crowdroidApplication.getAccountList()
				.getCurrentAccount();

		Bundle intentBundle = getIntent().getExtras();
		mark = intentBundle.getInt("mark");
		switch (mark) {
		case 1: {
			TYPE = CommHandler.TYPE_GET_ALBUMS_TIMELINE;
			headerName.setText(getResources().getString(R.string.photoAlpum));
			break;
		}
		case 2: {
			TYPE = CommHandler.TYPE_CFB_GET_DOCUMENT;
			headerName.setText(getResources().getString(R.string.docAlbum));
			break;
		}
		case 3: {
			TYPE = CommHandler.TYPE_CFB_GET_VEDIO;
			headerName.setText(getResources().getString(R.string.vedioAlbum));
			break;
		}
		}

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
	}

	@Override
	protected void onDestroy() {
		// final GridView grid = appPage;
		//
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
	public void onResume() {
		data.clear();
		refreshFlag = false;
		timelineInfoList.clear();
		if (mScrollLayout.getChildCount() != 0) {
			mScrollLayout.removeAllViews();
		}
		super.onResume();
	}

	@Override
	protected void onRestart() {
		data.clear();
		refreshFlag = false;
		timelineInfoList.clear();
		if (mScrollLayout.getChildCount() != 0) {
			mScrollLayout.removeAllViews();
		}
		super.onRestart();
	}

	@Override
	protected void refreshByMenu() {
		// TODO Auto-generated method stub

	}

	private void createGridView() {
		final int PageCount = (int) Math.ceil(timelineInfoList.size()
				/ APP_PAGE_SIZE);
		Log.e("tag", "size:" + timelineInfoList.size() + " page:" + PageCount);
		for (int i = 0; i < PageCount; i++) {
			appPage = new GridView(this);
			adapter = new AlbumsAdapter(this, timelineInfoList, i, appPage,
					mark);
			if (adapter != null) {
				adapter.notifyDataSetChanged();
			}
			// get the "i" page data
			appPage.setAdapter(adapter);

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
			openAlbumManager((TimeLineInfo) parent.getItemAtPosition(pos));

		}
	};

	private void openAlbumManager(final TimeLineInfo info) {

		if (currentAccount.getUid().equals(info.getUserInfo().getUid())) {
			switch (mark) {
			case 1: {
				
				items=getResources().getStringArray(R.array.cfb_photo_album_del_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传图片到专辑", "创建专辑",
//						"删除专辑" };
				break;
			}
			case 2: {
				items=getResources().getStringArray(R.array.cfb_doc_album_del_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传文档到专辑", "创建专辑",
//						"删除专辑" };
				break;
			}
			case 3: {
				items=getResources().getStringArray(R.array.cfb_vedio_album_del_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传视频到专辑", "创建专辑",
//						"删除专辑" };
				break;
			}
			}

		} else {
			switch (mark) {
			case 1: {
				items=getResources().getStringArray(R.array.cfb_photo_album_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传图片到专辑", "创建专辑" };
				break;
			}
			case 2: {
				items=getResources().getStringArray(R.array.cfb_doc_album_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传文档到专辑", "创建专辑" };
				break;
			}
			case 3: {
				items=getResources().getStringArray(R.array.cfb_vedio_album_dialog);
//				items = new String[] { "查看专辑详细信息", "查看专辑内容", "上传视频到专辑", "创建专辑" };
				break;
			}
			}
		}

		AlertDialog dialog = new AlertDialog.Builder(AlbumsActivity.this)
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int position) {
						switch (position) {
						case 0: {
							intent = new Intent(AlbumsActivity.this,
									AlbumDetailActivity.class);

							bundle.putSerializable("album", info);
							bundle.putInt("mark", mark);
							intent.putExtras(bundle);
							startActivity(intent);

							break;
						}
						case 1: {
							if (info.getCommentCount().equals("0")) {
								switch (mark) {
								case 1: {
									Toast.makeText(AlbumsActivity.this,
											getResources().getString(R.string.photoTip), Toast.LENGTH_SHORT)
											.show();
									break;
								}
								case 2: {
									Toast.makeText(AlbumsActivity.this,
											getResources().getString(R.string.docTip), Toast.LENGTH_SHORT)
											.show();
									break;
								}
								case 3: {
									Toast.makeText(AlbumsActivity.this,
											getResources().getString(R.string.vedioTip), Toast.LENGTH_SHORT)
											.show();
									break;
								}
								}

							} else {
								intent = new Intent(AlbumsActivity.this,
										PhotosActivity.class);
								intent.putExtra("mark", mark);
								intent.putExtra("album_id", info.getStatusId());
								startActivity(intent);
							}

							break;
						}
						case 2: {
							intent = new Intent(AlbumsActivity.this,
									UploadPicActivity.class);

							intent.putExtra("mark", mark);
							intent.putExtra("album_id", info.getStatusId());
							startActivity(intent);
							break;
						}
						case 3: {

							intent = new Intent(AlbumsActivity.this,
									CreateAlbumActivity.class);

							intent.putExtra("mark", mark);
							startActivity(intent);

							break;
						}
						case 4: {

							delAlbumDialog(info);

							break;
						}
						default:
							break;
						}

					}
				}).create();
		dialog.show();
	}

	private void delAlbumDialog(final TimeLineInfo info) {
		AlertDialog dialog = new AlertDialog.Builder(AlbumsActivity.this)
				.setTitle(getResources().getString(R.string.delAlbum))
				.setMessage(getResources().getString(R.string.delAlbumTip))
				.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {

							showProgressDialog();
							Map<String, Object> map;
							map = new HashMap<String, Object>();
							map.put("album_id", info.getStatusId());

							// Request
							apiServiceInterface.request(
									statusData.getCurrentService(),
									CommHandler.TYPE_CFB_DEL_ALBUM,
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
			// intent = new Intent(AlbumsActivity.this,
			// DiscoveryActivity.class);
			// startActivity(intent);

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

				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_ALBUMS_TIMELINE,
						apiServiceListener, null);
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

				// Request
				apiServiceInterface.request(statusData.getCurrentService(),
						CommHandler.TYPE_GET_ALBUMS_TIMELINE,
						apiServiceListener, null);
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
			progress = new HandleProgressDialog(AlbumsActivity.this);
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
}
