package com.anhuioss.crowdroid.operations;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.IGeneral;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.dialog.SelectUserScheduleDialog;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;

public abstract class ScheduleBasicActivity extends Activity {

	private static final int MENU_MONTH = 1;

	private static final int MENU_USERS = 2;

	private static final int MENU_ADD = 3;

	// private static final int MENU_ADDDAY = 4;

	private StatusData statusData;

	@Override
	public void onStart() {
		super.onStart();

		CrowdroidApplication crowdroidApplication = (CrowdroidApplication) getApplicationContext();
		SettingData settingData = crowdroidApplication.getSettingData();
		String imagePath = settingData.getWallpaper();
		statusData = crowdroidApplication.getStatusData();
		// Set Background
		loadBackGroundImage(imagePath);

	}

	@Override
	public void onPause() {
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();

	}

	// -----------------------------------------------------------------------------
	/**
	 * Create Menu.
	 */
	// -----------------------------------------------------------------------------
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);

		menu.add(0, MENU_MONTH, 0, getString(R.string.schedule_month)).setIcon(
				R.drawable.menu_month_schedule);
		menu.add(0, MENU_USERS, 1, getString(R.string.schedule_user)).setIcon(
				R.drawable.menu_user_schedule);

		menu.add(0, MENU_ADD, 2, getString(R.string.schedule_add)).setIcon(
				R.drawable.menu_new);
		// menu.add(0, MENU_ADDDAY, 4, "添加日程").setIcon(R.drawable.menu_new);
		setMenuBackground();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);

		menu.clear();

		menu.add(0, MENU_MONTH, 0, getString(R.string.schedule_month)).setIcon(
				R.drawable.menu_month_schedule);
		menu.add(0, MENU_USERS, 1, getString(R.string.schedule_user)).setIcon(
				R.drawable.menu_user_schedule);

		menu.add(0, MENU_ADD, 2, getString(R.string.schedule_add)).setIcon(
				R.drawable.menu_new);
		// menu.add(0, MENU_ADDDAY, 4, "添加日程").setIcon(R.drawable.menu_new);

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
		case MENU_MONTH: {

			Intent intent;
			intent = new Intent(this, ScheduleMonthActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		}
		case MENU_USERS: {

			SelectUserScheduleDialog userDialog;
			userDialog = new SelectUserScheduleDialog(
					ScheduleBasicActivity.this);
			userDialog.setTitle(getString(R.string.search_user_schedule));
			userDialog.show();
			break;
		}
		case MENU_ADD: {

			Intent intent = new Intent(this, AddScheduleActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
		// case MENU_ADDDAY: {
		// break;
		// }

		default: {
			break;
		}
		}
		return true;
	}

	// ----------------------------------------------------------------
	/**
	 * Set Layout Resource Id
	 */
	// ----------------------------------------------------------------
	protected void setLayoutResId(int resId) {

		setContentView(resId);
		// setGallaryContentView();

	}

	// ----------------------------------------------------------------
	/**
	 * Set Gallery Visible Or Gone
	 */
	// ----------------------------------------------------------------
	protected void setGalleryVisibility(boolean visibility) {

		if (visibility) {
			// Visible
			findViewById(R.id.linear_layout).setVisibility(View.VISIBLE);
		} else {
			// Gone
			findViewById(R.id.linear_layout).setVisibility(View.GONE);
		}
	}

	// -----------------------------------------------------------------------------
	/**
	 * Set BackGrouned Image.
	 */
	// -----------------------------------------------------------------------------
	private void loadBackGroundImage(String path) {

		if (path == null || path.equals("0")) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("-") == 0) {
			getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		} else if (path.indexOf("/") == -1) {
			getWindow().setBackgroundDrawableResource(Integer.valueOf(path));
		} else {
			File file = new File(path);
			FileInputStream input = null;
			if (file.exists() && file.canRead() && file.isFile()) {
				try {
					input = new FileInputStream(file);

					// Get BitMap and set to background
					BitmapDrawable drawable = new BitmapDrawable(input);
					getWindow().setBackgroundDrawable(drawable);
				} catch (Exception e) {

				} catch (OutOfMemoryError e) {

				} finally {
					if (input != null) {
						try {
							input.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	protected void setMenuBackground() {
		ScheduleBasicActivity.this.getLayoutInflater().setFactory(
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

}
