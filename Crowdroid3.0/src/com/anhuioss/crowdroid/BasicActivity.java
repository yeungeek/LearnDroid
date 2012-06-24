package com.anhuioss.crowdroid;

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
import android.widget.Gallery;
import android.widget.LinearLayout;

import com.anhuioss.crowdroid.activity.DiscoveryActivity;
import com.anhuioss.crowdroid.activity.MoreFunctionActivity;
import com.anhuioss.crowdroid.activity.SendMessageActivity;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.data.StatusData;
import com.anhuioss.crowdroid.settings.SettingsActivity;
import com.anhuioss.crowdroid.sns.operations.SNSDiscoveryActivity;

public abstract class BasicActivity extends Activity {

	/** LineraLayout */
	private LinearLayout linearLayout;

	/** Gallery */
	private Gallery gallery;

	public boolean flag = true;

	private static final int MENU_SETTING = 1;

	private static final int MENU_UPDATE = 2;

	private static final int MENU_DISCOVERY = 3;

	private static final int MENU_REFRESH = 4;

	private static final int MENU_LOGOUT = 5;

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

		menu.add(0, MENU_SETTING, 0,
				getResources().getString(R.string.menu_setting)).setIcon(
				R.drawable.menu_setting);
		menu.add(0, MENU_UPDATE, 1,
				getResources().getString(R.string.menu_new_tweet)).setIcon(
				R.drawable.menu_new);
		// if (flag) {
		menu.add(0, MENU_DISCOVERY, 2,
				getResources().getString(R.string.more_app)).setIcon(
				R.drawable.theme);
		// } else {
		// menu.add(0, MENU_GALLERY, 2,
		// getResources().getString(R.string.menu_display_tabs)).setIcon(
		// android.R.drawable.ic_menu_gallery);
		// }
		menu.add(0, MENU_REFRESH, 4,
				getResources().getString(R.string.menu_refresh)).setIcon(
				R.drawable.menu_refresh);
		menu.add(0, MENU_LOGOUT, 5,
				getResources().getString(R.string.menu_logout)).setIcon(
				R.drawable.newlogout);
		setMenuBackground();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		super.onPrepareOptionsMenu(menu);

		menu.clear();

		menu.add(0, MENU_SETTING, 0,
				getResources().getString(R.string.menu_setting)).setIcon(
				R.drawable.menu_setting);
		menu.add(0, MENU_UPDATE, 1,
				getResources().getString(R.string.menu_new_tweet)).setIcon(
				R.drawable.menu_new);
		// if (flag) {
		// menu.add(0, MENU_GALLERY, 2,
		// getResources().getString(R.string.menu_fullscreen)).setIcon(
		// android.R.drawable.ic_menu_gallery);
		// } else {
		menu.add(0, MENU_DISCOVERY, 2,
				getResources().getString(R.string.more_app)).setIcon(
				R.drawable.theme);
		// }
		menu.add(0, MENU_REFRESH, 4,
				getResources().getString(R.string.menu_refresh)).setIcon(
				R.drawable.menu_refresh);
		menu.add(0, MENU_LOGOUT, 5,
				getResources().getString(R.string.menu_logout)).setIcon(
				R.drawable.newlogout);

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
		case MENU_SETTING: {

			// Intent
			Intent intent = new Intent(this, MoreFunctionActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		}
		case MENU_UPDATE: {

			// Intent
			Intent intent = new Intent(this, SendMessageActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("action", "new");
			bundle.putString("target", "");
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		}
		case MENU_DISCOVERY: {
			Intent intent;

			if (statusData.getCurrentService().equals(
					IGeneral.SERVICE_NAME_RENREN)) {
				intent = new Intent(this, SNSDiscoveryActivity.class);
			} else {
				intent = new Intent(this, DiscoveryActivity.class);
			}
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;

		}
		case MENU_REFRESH: {

			// Refresh
			refreshByMenu();
			break;

		}
		case MENU_LOGOUT: {

			// Logout
			confirmLogoutDialog();
			break;

		}
		default: {
			break;
		}
		}
		return true;
	}

	protected abstract void refreshByMenu();

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

	// ----------------------------------------------------------------
	/**
	 * Add Gallery To LineraLayout
	 */
	// ----------------------------------------------------------------
	// private void setGallaryContentView() {
	//
	// // Get A Instance Of Gallery
	// gallery = new TabSelector(this);
	//
	// // Get The View Which Is The Parent Of Gallery
	// linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
	//
	// // Add Gallery
	// linearLayout.addView(gallery, 0);
	//
	// }
	//
	// // ----------------------------------------------------------------
	// /**
	// * Set Gallery's Position
	// */
	// // ----------------------------------------------------------------
	// private void setGalleryPosition(int position) {
	// if (position > -1) {
	// gallery.setSelection(position
	// + TabSelector.getImageimageResLength());
	// }
	// }

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

	private void confirmLogoutDialog() {
		AlertDialog.Builder dlg = new AlertDialog.Builder(this);
		dlg.setTitle(R.string.logout);
		dlg.setMessage(getResources().getString(R.string.wheter_to_logout))
				.setPositiveButton(getResources().getString(R.string.ok),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
								notificationManager.cancelAll();

								Intent i = new Intent(BasicActivity.this,
										LoginActivity.class);
								i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								i.putExtra("autoLogin", false);
								startActivity(i);
//								android.os.Process
//										.killProcess(android.os.Process.myPid());
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).create().show();
	}

	protected void setMenuBackground() {
		BasicActivity.this.getLayoutInflater().setFactory(
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
