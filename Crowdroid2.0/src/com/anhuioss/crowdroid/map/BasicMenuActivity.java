package com.anhuioss.crowdroid.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.TimelineActivity;
import com.anhuioss.crowdroid.data.AccountData;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.util.Constants;
import com.baidu.mapapi.MapActivity;

public class BasicMenuActivity extends MapActivity {

	public boolean flag = true;
	private StatusData statusData;
	private AccountData accountData;
	private SettingData settingData;

	@Override
	protected void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		settingData = crowdroidApplication.getSettingData();
		statusData = crowdroidApplication.getStatusData();
		accountData = crowdroidApplication.getAccountList().getCurrentAccount();

	}

	@Override
	protected void onPause() {
		super.onPause();
		TimelineActivity.isBackgroundNotificationFlag=true;

	}

	@Override
	protected void onResume() {
		super.onResume();
		TimelineActivity.isBackgroundNotificationFlag=false;

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Create Menu.
	 */
	// -----------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		menu.add(0, MapConstant.MAP_MENU_SHARE, 0, R.string.menu_share_loc)
				.setIcon(android.R.drawable.ic_menu_share);
		menu.add(0, MapConstant.MAP_MENU_ROUNTER, 1, R.string.menu_search_route)
				.setIcon(android.R.drawable.ic_menu_directions);
		menu.add(0, MapConstant.MAP_MENU_NEAR, 2, R.string.menu_poi_search)
				.setIcon(android.R.drawable.ic_menu_mapmode);
		menu.add(0, MapConstant.MAP_MENU_CURRENT, 4, R.string.menu_my_loc)
				.setIcon(android.R.drawable.ic_menu_compass);
		menu.add(0, MapConstant.MAP_MENU_CLEAR, 5, R.string.menu_clearn_loc)
				.setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, MapConstant.MAP_MENU_BACK, 6, R.string.menu_return_home)
				.setIcon(android.R.drawable.ic_menu_revert);
		// setMenuBackground();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);

		menu.clear();
		menu.add(0, MapConstant.MAP_MENU_SHARE, 0, R.string.menu_share_loc)
				.setIcon(android.R.drawable.ic_menu_share);
		menu.add(0, MapConstant.MAP_MENU_ROUNTER, 1, R.string.menu_search_route)
				.setIcon(android.R.drawable.ic_menu_directions);
		menu.add(0, MapConstant.MAP_MENU_NEAR, 2, R.string.menu_poi_search)
				.setIcon(android.R.drawable.ic_menu_mapmode);
		menu.add(0, MapConstant.MAP_MENU_CURRENT, 4, R.string.menu_my_loc)
				.setIcon(android.R.drawable.ic_menu_compass);
		menu.add(0, MapConstant.MAP_MENU_CLEAR, 5, R.string.menu_clearn_loc)
				.setIcon(android.R.drawable.ic_menu_delete);
		menu.add(0, MapConstant.MAP_MENU_BACK, 6, R.string.menu_return_home)
				.setIcon(android.R.drawable.ic_menu_revert);
		return true;
	}

	// -----------------------------------------------------------------------------
	/**
	 * Called when Menu Item is selected
	 */
	// -----------------------------------------------------------------------------
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {

		switch (item.getItemId()) {
		case MapConstant.MAP_MENU_SHARE: {
			break;
		}
		case MapConstant.MAP_MENU_ROUNTER: {
			break;

		}
		case MapConstant.MAP_MENU_NEAR: {
			break;

		}
		case MapConstant.MAP_MENU_CURRENT: {

			// Refresh
			break;

		}
		case MapConstant.MAP_MENU_CLEAR: {

			break;

		}
		case MapConstant.MAP_MENU_BACK: {

			// Logout
			break;

		}
		default: {
			break;
		}
		}
		return true;
	}

	/**
	 * Set content view Id
	 */
	protected void setLayoutView(int resId) {
		setContentView(resId);
	}

	protected void setMenuBackground() {
		BasicMenuActivity.this.getLayoutInflater().setFactory(
				new android.view.LayoutInflater.Factory() {

					@Override
					public View onCreateView(String name, Context context,
							AttributeSet attrs) {
						// 指定自定义inflate的对象
						if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")
								|| name.equalsIgnoreCase("com.android.internal.view.menu.ListMenuItemView")) {
							try {
								LayoutInflater f = getLayoutInflater();
								final View view = f.createView(name, null,
										attrs);
								new Handler().post(new Runnable() {
									public void run() {
										// 设置背景图片
										view.setBackgroundResource(R.drawable.tools_bottom_background);
									}
								});
								return view;
							} catch (InflateException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}
						}
						return null;
					}
				});
	}

	public String shareScreenShot(View view) throws Exception {
		// view.setDrawingCacheEnabled(true);
		// view.buildDrawingCache();
		// Bitmap b = view.getDrawingCache();
		// FileOutputStream fos = null;
		// try {
		// File file = new File("/mnt/sdcard/media");
		// if (!file.exists()) {
		// file.mkdirs();
		// }
		// String str = String.valueOf(System.currentTimeMillis());
		// Log.e("path", name + str + ".png");
		// File picFile = new File(file, str + ".png");
		// fos = new FileOutputStream(picFile);
		// if (fos != null) {
		// b.compress(Bitmap.CompressFormat.PNG, 90, fos);
		// fos.close();
		// }
		// } catch (Exception e) {
		// Log.e("tag2", TAG);
		//
		// }
		// ---------------------------------------------------------------------

		// create bitmap screen capture
		Bitmap bitmap;
		View v1 = view;
		v1.setDrawingCacheEnabled(true);
		bitmap = Bitmap.createBitmap(v1.getDrawingCache());
		v1.setDrawingCacheEnabled(false);
		// Drawable draw = new BitmapDrawable(bitmap);
		return saveMyBitmap(bitmap);
		// OutputStream fout = null;
		// File imageFile = new File(mPath);
		// try {
		// fout = new FileOutputStream(imageFile);
		// bitmap.compress(Bitmap.CompressFormat.PNG, 90, fout);
		// fout.flush();
		// fout.close();
		//
		// } catch (FileNotFoundException e) {
		// // TODO Auto-generated catch block
		// Log.e("tag3", TAG);
		// e.printStackTrace();
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// Log.e("tag4", TAG);
		// e.printStackTrace();
		// }
	}

	private String saveMyBitmap(Bitmap mBitmap) {
		String fileName = String.valueOf(System.currentTimeMillis()) + ".png";
		File file = new File(Constants.DEFAULT_SAVE_FILE_PATH);
		if (!file.exists()) {
			file.mkdir();
		}
		File f = new File(file, fileName);
		FileOutputStream fOut = null;
		try {
			f.createNewFile();
			fOut = new FileOutputStream(f);
		} catch (IOException e) {
			Toast.makeText(BasicMenuActivity.this, R.string.no_sdcard_mounted,
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return "";
		}
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
		try {
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f.getAbsolutePath();
	}
}
