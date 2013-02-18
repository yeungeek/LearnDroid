package com.anhuioss.crowdroid.settings;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.settings.AddKeywordFilterActivity;
import com.anhuioss.crowdroid.settings.AddUserFilterActivity;
import com.anhuioss.crowdroid.settings.SelectColorDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class BrowseModeActivity extends Activity implements
		android.view.View.OnClickListener {

	private Button btn_back;

	private RadioGroup selectItem;

	private RadioButton fontSize;

	private RadioButton fontColor;

	private RadioButton keywordfilter;

	private RadioButton userfilter;

	private RadioButton browseMode;

	private static int id = 0;

	public static final String select[] = { "DISPLAY_ALL_IMAGE",
			"DISABLE_IMAGES_PREVIEW", "DISABLE_ALL_IMAGES" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_mode);

		btn_back = (Button) findViewById(R.id.back);
		selectItem = (RadioGroup) findViewById(R.id.selectitem);
		fontSize = (RadioButton) findViewById(R.id.fontSize);
		fontColor = (RadioButton) findViewById(R.id.fontColor);
		keywordfilter = (RadioButton) findViewById(R.id.keyword_filter);
		userfilter = (RadioButton) findViewById(R.id.user_filter);
		browseMode = (RadioButton) findViewById(R.id.browse_mode);

		btn_back.setOnClickListener(this);
		selectItem.setOnClickListener(this);
		fontSize.setOnClickListener(this);
		fontColor.setOnClickListener(this);
		keywordfilter.setOnClickListener(this);
		userfilter.setOnClickListener(this);
		browseMode.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {

		case R.id.back: {
			finish();
			break;
		}
		case R.id.fontSize: {
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(BrowseModeActivity.this);

			int choice = prefs.getInt("dialog_choince_id", 0);
			final String[] items = getResources().getStringArray(
					R.array.font_size);
			AlertDialog.Builder dlg = new AlertDialog.Builder(
					BrowseModeActivity.this);
			dlg.setTitle(R.string.setting_font_size)
					.setSingleChoiceItems(items, choice, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							id = which;
						}
					})
					.setPositiveButton(R.string.ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(BrowseModeActivity.this);
							prefs.edit()
									.putString("fontsizesettings", items[id])
									.commit();
							prefs.edit().putInt("dialog_choince_id", id)
									.commit();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create().show();
			break;
		}
		case R.id.fontColor: {
			SelectColorDialog colorDialog = new SelectColorDialog(
					BrowseModeActivity.this, "fontcolorsettings");
			colorDialog.setTitle(getString(R.string.setting_font_color));
			colorDialog.show();
			break;
		}
		case R.id.keyword_filter: {
			Intent intent = new Intent();
			intent.setClass(this, AddKeywordFilterActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.user_filter: {
			Intent intent = new Intent();
			intent.setClass(this, AddUserFilterActivity.class);
			startActivity(intent);
			break;
		}
		case R.id.browse_mode: {

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(BrowseModeActivity.this);

			int choice = prefs.getInt("dialog_choince_id", 0);
			final CharSequence[] items = getResources().getStringArray(
					R.array.image_show_select);
			AlertDialog.Builder dlg = new AlertDialog.Builder(
					BrowseModeActivity.this);
			dlg.setTitle(R.string.setting_display_image)
					.setSingleChoiceItems(items, choice, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							id = which;
						}
					})
					.setPositiveButton(R.string.ok, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(BrowseModeActivity.this);
							prefs.edit().putString("imagepreview", select[id])
									.commit();
							prefs.edit().putInt("dialog_choince_id", id)
									.commit();
						}
					})
					.setNegativeButton(R.string.cancel, new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					}).create().show();
			break;

		}

		default:
			break;
		}

	}
}
