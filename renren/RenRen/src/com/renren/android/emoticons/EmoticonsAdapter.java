package com.renren.android.emoticons;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.renren.android.R;
import com.renren.android.RenRenData;
import com.renren.android.util.Text_Util;

public class EmoticonsAdapter extends BaseAdapter {

	private Context mContext;

	public EmoticonsAdapter(Context context) {
		mContext = context;
		if (RenRenData.mEmoticonsResults.size() == 0) {
			String json = new Text_Util().readFromFile(
					EmoticonsResponseBean.FILENAME,
					EmoticonsResponseBean.FILEPATH);
			if (json != null) {
				EmoticonsHelper helper = new EmoticonsHelper();
				RenRenData.mEmoticonsResults = helper.Resolve(json);
				notifyDataSetChanged();
			}
		}
	}

	public int getCount() {
		return RenRenData.mEmoticonsResults.size();
	}

	public Object getItem(int position) {
		return RenRenData.mEmoticonsResults.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.emoticons, null);
			holder = new ViewHolder();
			holder.mEmotcon = (ImageView) convertView
					.findViewById(R.id.emotcons_item_img);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		EmoticonsResult result = RenRenData.mEmoticonsResults.get(position);
		Bitmap bitmap = getEmotcon(result.getEmotion());
		holder.mEmotcon.setImageBitmap(bitmap);

		return convertView;
	}

	class ViewHolder {
		ImageView mEmotcon;
	}

	private Bitmap getEmotcon(String imageName) {
		File cacheDir = new File("/sdcard/RenRenForAndroid/Emoticons/");
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}

		File[] cacheFiles = cacheDir.listFiles();
		int i = 0;
		if (cacheFiles != null) {
			for (; i < cacheFiles.length; i++) {
				if (imageName.equals(cacheFiles[i].getName())) {
					break;
				}
			}
		}
		if (i < cacheFiles.length) {
			return BitmapFactory
					.decodeFile("/sdcard/RenRenForAndroid/Emoticons/"
							+ imageName);
		}
		return null;
	}
}
