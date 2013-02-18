package com.anhuioss.crowdroid.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;

public class LicenseDialog extends Dialog {

	private Context mContext;

	private TextView versionName;

	private TextView developName;

	private Button btnClose;

	private Button btnCheckUpdate;

	// ----------------------------------------------------------------------------
	/**
	 * Constructor
	 * 
	 * @param Context
	 *            context
	 */
	// ----------------------------------------------------------------------------
	public LicenseDialog(Context context) {
		super(context);
		mContext = context;

	}

	// ----------------------------------------------------------------------------
	/**
	 * onCreate Method
	 * 
	 * @param Bundle
	 *            savedInstanceState
	 */
	// ----------------------------------------------------------------------------
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.dialog_license);

		versionName = (TextView) findViewById(R.id.version_name);
		developName = (TextView) findViewById(R.id.developer_name);
		btnClose = (Button) findViewById(R.id.close_dialog);
		btnCheckUpdate = (Button) findViewById(R.id.btn_check_apk_version);

		String name = null;
		try {
			PackageManager pm = mContext.getPackageManager();
			PackageInfo info = pm.getPackageInfo("com.anhuioss.crowdroid", 0);
			name = info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		setTitle(R.string.license);

		versionName.setText(mContext.getString(R.string.version_name) + name);

		developName.setText(Html.fromHtml("<u>"
				+ mContext.getString(R.string.anhuioss_website) + "</u>"));

		btnClose.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});

		developName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Uri uri = null;
				String language = mContext.getApplicationContext()
						.getResources().getConfiguration().locale.getLanguage();
				if (language != null && language.equals("zh")) {
					uri = Uri.parse("http://www.anhuioss.com/cn/index.html");
				} else if (language != null && language.equals("ja")) {
					uri = Uri.parse("http://www.anhuioss.com/index.html");
				} else {
					uri = Uri.parse("http://www.anhuioss.com/en/index.html");
				}
				Intent i = new Intent(Intent.ACTION_VIEW, uri);
				mContext.startActivity(i);

			}
		});

		btnCheckUpdate.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// LayoutInflater inflater = getLayoutInflater();
				// View layout =
				// inflater.inflate(R.layout.dialog_download_update_apk,null);
				// AlertDialog downloadUpdateDialog= new
				// AlertDialog.Builder(mContext).create();
				// downloadUpdateDialog.show();
				// downloadUpdateDialog.getWindow().setContentView(layout);
				new DownloadUpdateDialog(mContext).show();
			}

		});
	}

}
