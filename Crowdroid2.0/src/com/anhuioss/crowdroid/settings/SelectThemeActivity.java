package com.anhuioss.crowdroid.settings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.communication.ApiService;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.data.info.TipInfo;
import com.anhuioss.crowdroid.dialog.PreviewPicDialog;
import com.anhuioss.crowdroid.service.cfb.CfbCommHandler;
import com.anhuioss.crowdroid.service.cfb.CfbParseHandler;
import com.anhuioss.crowdroid.util.CommResult;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.preference.Preference;
import android.widget.Toast;

public class SelectThemeActivity extends BasicAddPreferenceActivity implements
		ServiceConnection {

	CommResult comResult = null;

	ArrayList<TipInfo> tipInfoList = new ArrayList<TipInfo>();

	@Override
	protected void onStart() {
		super.onStart();
		// Bind Service
		Intent intent = new Intent(this, ApiService.class);
		bindService(intent, this, Context.BIND_AUTO_CREATE);

		initView(getString(R.string.setting_wallpaper_theme), "",
				getString(R.string.setting_wallpaper_theme_list), null, null,
				itemClickedListener);

		createItemList();
	}

	private Preference.OnPreferenceClickListener itemClickedListener = new Preference.OnPreferenceClickListener() {

		@Override
		public boolean onPreferenceClick(Preference preference) {
			final int id = Integer.valueOf(preference.getKey());
			String picUrl = tipInfoList.get(id).getDownLoadUrl();

			String themeName = tipInfoList.get(id).getVersionName();

			PreviewPicDialog preDialog = new PreviewPicDialog(
					SelectThemeActivity.this, picUrl, themeName);
			preDialog.show();
			return true;
		}

	};

	@Override
	protected void onStop() {
		super.onStop();
		// Unbind Service
		unbindService(this);
	}

	@Override
	protected void createItemList() {

		ArrayList<HashMap<String, String>> itemList = new ArrayList<HashMap<String, String>>();

		CommResult comResult = new CommResult();

		Map<String, Object> map = new HashMap<String, Object>();

		comResult = CfbCommHandler.getThemeMessage(map);

		if (comResult.getMessage() != null) {
			tipInfoList = CfbParseHandler.parseThemeMessage(comResult
					.getMessage());
			for (TipInfo tipInfo : tipInfoList) {
				HashMap<String, String> item = new HashMap<String, String>();
				item.put("title", tipInfo.getVersionName());
				item.put(
						"summary",
						getString(R.string.setting_wallpaper_theme_resolution)
								+ ":"
								+ tipInfo.getResolution()
								+ " "
								+ getString(R.string.setting_wallpaper_theme_size)
								+ ":"
								+ tipInfo.getThemeSize()
								+ " "
								+ getString(R.string.setting_wallpaper_theme_description)
								+ ":" + tipInfo.getText());
				itemList.add(item);
			}
			refresh(itemList);
		} else {
			Toast.makeText(SelectThemeActivity.this,
					getString(R.string.error_msg_503), Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {

	}

	@Override
	public void onServiceDisconnected(ComponentName name) {

	}

}
