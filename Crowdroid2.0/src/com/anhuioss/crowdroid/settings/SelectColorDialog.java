package com.anhuioss.crowdroid.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.anhuioss.crowdroid.CrowdroidApplication;
import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.SettingData;

public class SelectColorDialog extends Dialog implements
		android.view.View.OnClickListener {

	private int selectedColor = 0;
	private String key;
	private TextView colorTextView;
	private Context mContext;

	private CrowdroidApplication crowdroidApplaication;
	private SettingData settingData;

	// -------------------------------------------------------------------
	/** Constructor */
	// -------------------------------------------------------------------
	public SelectColorDialog(Context context, String key) {
		super(context);
		this.key = key;
		mContext = context;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		// getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// android.R.drawable.ic_menu_more);
		setContentView(R.layout.dialog_setting_select_color);
		crowdroidApplaication = (CrowdroidApplication) mContext
				.getApplicationContext();
		settingData = crowdroidApplaication.getSettingData();
		initView();
	}

	// -------------------------------------------------------------------
	/** Inflate the customized dialog */
	// -------------------------------------------------------------------
	protected void initView() {

		// final ImageView colorTextView = (ImageView)
		// findViewById(R.id.color_imageview);
		colorTextView = (TextView) findViewById(R.id.color_textview);

		ImageView i1_Button = (ImageView) findViewById(R.id.i1);
		ImageView i2_Button = (ImageView) findViewById(R.id.i2);
		ImageView i3_Button = (ImageView) findViewById(R.id.i3);
		ImageView i4_Button = (ImageView) findViewById(R.id.i4);
		ImageView i5_Button = (ImageView) findViewById(R.id.i5);
		ImageView i6_Button = (ImageView) findViewById(R.id.i6);
		ImageView i7_Button = (ImageView) findViewById(R.id.i7);
		ImageView i8_Button = (ImageView) findViewById(R.id.i8);
		ImageView i9_Button = (ImageView) findViewById(R.id.i9);
		ImageView i10_Button = (ImageView) findViewById(R.id.i10);
		ImageView i11_Button = (ImageView) findViewById(R.id.i11);
		ImageView i12_Button = (ImageView) findViewById(R.id.i12);
		ImageView i13_Button = (ImageView) findViewById(R.id.i13);
		ImageView i14_Button = (ImageView) findViewById(R.id.i14);
		ImageView i15_Button = (ImageView) findViewById(R.id.i15);
		ImageView i16_Button = (ImageView) findViewById(R.id.i16);
		ImageView i17_Button = (ImageView) findViewById(R.id.i17);
		ImageView i18_Button = (ImageView) findViewById(R.id.i18);
		ImageView i19_Button = (ImageView) findViewById(R.id.i19);
		ImageView i20_Button = (ImageView) findViewById(R.id.i20);
		ImageView i21_Button = (ImageView) findViewById(R.id.i21);
		ImageView i22_Button = (ImageView) findViewById(R.id.i22);
		ImageView i23_Button = (ImageView) findViewById(R.id.i23);
		ImageView i24_Button = (ImageView) findViewById(R.id.i24);
		i1_Button.setOnClickListener(this);
		i2_Button.setOnClickListener(this);
		i3_Button.setOnClickListener(this);
		i4_Button.setOnClickListener(this);
		i5_Button.setOnClickListener(this);
		i6_Button.setOnClickListener(this);
		i7_Button.setOnClickListener(this);
		i8_Button.setOnClickListener(this);
		i9_Button.setOnClickListener(this);
		i10_Button.setOnClickListener(this);
		i11_Button.setOnClickListener(this);
		i12_Button.setOnClickListener(this);
		i13_Button.setOnClickListener(this);
		i14_Button.setOnClickListener(this);
		i15_Button.setOnClickListener(this);
		i16_Button.setOnClickListener(this);
		i17_Button.setOnClickListener(this);
		i18_Button.setOnClickListener(this);
		i19_Button.setOnClickListener(this);
		i20_Button.setOnClickListener(this);
		i21_Button.setOnClickListener(this);
		i22_Button.setOnClickListener(this);
		i23_Button.setOnClickListener(this);
		i24_Button.setOnClickListener(this);

		// Set Default color

		int color = Integer.valueOf(getValue());
		if (color != 0) {
			colorTextView.setBackgroundColor(mContext.getResources().getColor(
					color));
		} else {
			colorTextView.setBackgroundColor(mContext.getResources().getColor(
					R.color.white));
		}

		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (selectedColor != 0) {
					if ("fontcolorsettings".equals(key)) {
						settingData.setFontColor(String.valueOf(selectedColor));
					} else if ("wallpaper".equals(key)) {
						settingData.setWallpaperPath(String
								.valueOf(selectedColor));
					}
					/*
					 * SharedPreferences pref = PreferenceManager
					 * .getDefaultSharedPreferences(getContext()); Editor editor
					 * = pref.edit(); editor.putString(key,
					 * String.valueOf(selectedColor)); editor.commit();
					 */
				}

				dismiss();
			}
		});

		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.i1: {

			colorTextView.setBackgroundResource(R.color.n_black);

			selectedColor = R.color.n_black;
			break;

		}
		case R.id.i2: {
			colorTextView.setBackgroundResource(R.color.dimgray);
			selectedColor = R.color.dimgray;
			break;

		}
		// if(value.contains("-1")){
		// value.replace("-1", "");
		//
		case R.id.i3: {
			colorTextView.setBackgroundResource(R.color.slategray);
			selectedColor = R.color.slategray;
			break;

		}
		case R.id.i4: {
			colorTextView.setBackgroundResource(R.color.gainsboro);
			selectedColor = R.color.gainsboro;
			break;

		}
		case R.id.i5: {
			colorTextView.setBackgroundResource(R.color.mediumblue);
			selectedColor = R.color.mediumblue;
			break;

		}
		case R.id.i6: {
			colorTextView.setBackgroundResource(R.color.royalblue);
			selectedColor = R.color.royalblue;
			break;

		}
		case R.id.i7: {
			colorTextView.setBackgroundResource(R.color.dodgerblue);
			selectedColor = R.color.dodgerblue;
			break;

		}
		case R.id.i8: {
			colorTextView.setBackgroundResource(R.color.lightskyblue);
			selectedColor = R.color.lightskyblue;
			break;

		}
		case R.id.i9: {
			colorTextView.setBackgroundResource(R.color.aqua);
			selectedColor = R.color.aqua;
			break;

		}
		case R.id.i10: {
			colorTextView.setBackgroundResource(R.color.mediumturquoise);
			selectedColor = R.color.mediumturquoise;
			break;

		}
		case R.id.i11: {
			colorTextView.setBackgroundResource(R.color.paleturquoise);
			selectedColor = R.color.paleturquoise;
			break;

		}
		case R.id.i12: {
			colorTextView.setBackgroundResource(R.color.lightcyan);
			selectedColor = R.color.lightcyan;
			break;

		}
		case R.id.i13: {
			colorTextView.setBackgroundResource(R.color.limegreen);
			selectedColor = R.color.limegreen;
			break;

		}
		case R.id.i14: {
			colorTextView.setBackgroundResource(R.color.lime);
			selectedColor = R.color.lime;
			break;

		}
		case R.id.i15: {
			colorTextView.setBackgroundResource(R.color.greenyellow);
			selectedColor = R.color.greenyellow;
			break;

		}
		case R.id.i16: {
			colorTextView.setBackgroundResource(R.color.palegreen);
			selectedColor = R.color.palegreen;
			break;

		}
		case R.id.i17: {
			colorTextView.setBackgroundResource(R.color.crimson);
			selectedColor = R.color.crimson;
			break;

		}
		case R.id.i18: {
			colorTextView.setBackgroundResource(R.color.mediumvioletred);
			selectedColor = R.color.mediumvioletred;
			break;

		}
		case R.id.i19: {
			colorTextView.setBackgroundResource(R.color.hotpink);
			selectedColor = R.color.hotpink;
			break;

		}
		case R.id.i20: {
			colorTextView.setBackgroundResource(R.color.lightpink);
			selectedColor = R.color.lightpink;
			break;

		}
		case R.id.i21: {
			colorTextView.setBackgroundResource(R.color.darkorange);
			selectedColor = R.color.darkorange;
			break;

		}
		case R.id.i22: {
			colorTextView.setBackgroundResource(R.color.yellow);
			selectedColor = R.color.yellow;
			break;

		}
		case R.id.i23: {
			colorTextView.setBackgroundResource(R.color.cornsilk);
			selectedColor = R.color.cornsilk;
			break;

		}
		case R.id.i24: {
			colorTextView.setBackgroundResource(R.color.white);
			selectedColor = R.color.white;
			// selectedColor = Color.WHITE;
			//
			// Toast.makeText(mContext, selectedColor,
			// Toast.LENGTH_SHORT).show();
			break;

		}

		default:
			break;
		}

	}

	// -------------------------------------------------------------------
	/** Get Value */
	// -------------------------------------------------------------------
	private String getValue() {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(getContext());
		String value = pref.getString(key, String.valueOf(R.color.white));
		if (value.contains("/")) {
			value = String.valueOf(R.color.white);
		}
		if (value.contains("-1")) {
			value.replace("-1", "");
		}
		return value;
	}

}
