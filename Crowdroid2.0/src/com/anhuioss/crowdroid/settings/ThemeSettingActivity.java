package com.anhuioss.crowdroid.settings;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.SettingData;
import com.anhuioss.crowdroid.settings.SelectColorDialog;
import com.anhuioss.crowdroid.settings.SelectThemeActivity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class ThemeSettingActivity extends Activity implements OnClickListener {

	private Button btn_back;

	private RadioGroup selectItem;

	private RadioButton set_background;

	private RadioButton set_color;

	private RadioButton themesload;

	private CrowdroidApplication arowdroidApplaication;

	private SettingData settingData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_themes_setting);
		arowdroidApplaication = (CrowdroidApplication) getApplicationContext();
		settingData = arowdroidApplaication.getSettingData();

		btn_back = (Button) findViewById(R.id.back);
		selectItem = (RadioGroup) findViewById(R.id.selectitem);
		set_background = (RadioButton) findViewById(R.id.picture);
		set_color = (RadioButton) findViewById(R.id.color);
		themesload = (RadioButton) findViewById(R.id.theme);

		btn_back.setOnClickListener(this);
		selectItem.setOnClickListener(this);
		set_background.setOnClickListener(this);
		set_color.setOnClickListener(this);
		themesload.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.picture: {
			Intent picture_select = new Intent();
			picture_select.setType("image/*");
			picture_select.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(picture_select, 1);
			break;
		}
		case R.id.color: {
			SelectColorDialog colorDialog = new SelectColorDialog(
					ThemeSettingActivity.this, "wallpaper");
			colorDialog.setTitle(getString(R.string.setting_wallpaper));
			colorDialog.show();
			break;
		}
		case R.id.theme: {
			Intent intent = new Intent();
			intent.setClass(ThemeSettingActivity.this,
					SelectThemeActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.back: {
			finish();
			break;
		}
		default:
			break;
		}

	}

	// ---------------------------------------------------------
	/**
	 * Get Real Path of Image file from URI data
	 */
	// ---------------------------------------------------------
	public String getRealPathFromURI(Uri contentUri) {

		String path;

		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		path = cursor.getString(column_index);
		cursor.close();

		return path;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			Uri uri = data.getData();
			String path = getRealPathFromURI(uri);
			settingData.setWallpaperPath(path);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
