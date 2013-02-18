package com.anhuioss.crowdroid.cfb;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.data.info.TimeLineInfo;
import com.anhuioss.crowdroid.util.AsyncDataLoad;
import com.anhuioss.crowdroid.util.AsyncDataLoad.ImageCallback;

public class AlbumsAdapter extends BaseAdapter {
	private ArrayList<TimeLineInfo> albumInfoPageList = null;
	private Context context;
	public static final int APP_PAGE_SIZE = 9;
	private AsyncDataLoad imageLoader;
	private PhotoItem photoItem;
	private LayoutInflater layoutInflater;
	private TimeLineInfo albumInfo = null;
	private GridView gridView;
	Drawable cachedImage;
	private int mark;

	public AlbumsAdapter(Context context, ArrayList<TimeLineInfo> list,
			int page, GridView gridView, int mark) {
		this.context = context;
		this.gridView = gridView;
		this.mark = mark;
		albumInfoPageList = new ArrayList<TimeLineInfo>();
		int i = page * APP_PAGE_SIZE;
		int iEnd = i + APP_PAGE_SIZE;
		while ((i < list.size()) && (i < iEnd)) {
			albumInfoPageList.add(list.get(i));
			i++;
		}
		this.layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	@Override
	public int getCount() {

		return albumInfoPageList.size();

	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return albumInfoPageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		photoItem = new PhotoItem();
		if (convertView == null) {

			albumInfo = albumInfoPageList.get(position);
			convertView = layoutInflater.inflate(
					R.layout.activity_discovery_gridview_item, null);

			photoItem.photoIcon = (ImageView) convertView
					.findViewById(R.id.ItemImage);
			photoItem.photoName = (TextView) convertView
					.findViewById(R.id.ItemText);
			photoItem.photoIcon.setTag(albumInfo.getUserInfo()
					.getUserImageURL());

			if (albumInfo != null) {
				switch (mark) {
				case 1: {
					imageLoader = new AsyncDataLoad();
					if (albumInfo.getRetweetCount().equals("")) {// 专辑显示
						photoItem.photoName.setVisibility(View.VISIBLE);
						photoItem.photoName.setText(albumInfo.getFavorite()
								+ "( " + albumInfo.getCommentCount() + " )");
					} else {// 图片显示
						photoItem.photoName.setVisibility(View.GONE);
					}
					Drawable drawable = getDrawable(imageLoader, albumInfo
							.getUserInfo().getUserImageURL(),
							photoItem.photoIcon);
					photoItem.photoIcon.setImageDrawable(drawable);
					break;
				}
				case 2: {
					if (albumInfo.getRetweetCount().equals("")) {// 专辑显示
						photoItem.photoName.setVisibility(View.VISIBLE);
						photoItem.photoName.setText(albumInfo.getFavorite()
								+ "( " + albumInfo.getCommentCount() + " )");
						photoItem.photoIcon
								.setImageResource(R.drawable.default_doc);
					} else {// 文档显示
						String a[] = albumInfo.getUserInfo().getUserImageURL()
								.split("/");
						photoItem.photoName.setVisibility(View.VISIBLE);
						photoItem.photoName.setText(a[a.length - 1]);
						photoItem.photoIcon
								.setImageResource(R.drawable.file_icon);
					}

					break;
				}
				case 3: {
					if (albumInfo.getRetweetCount().equals("")) {// 专辑显示
						photoItem.photoName.setVisibility(View.VISIBLE);
						photoItem.photoName.setText(albumInfo.getFavorite()
								+ "( " + albumInfo.getCommentCount() + " )");
						photoItem.photoIcon
								.setImageResource(R.drawable.default_video);
					} else {// 视频显示
						
						photoItem.photoName.setVisibility(View.VISIBLE);
						photoItem.photoName.setText(albumInfo.getStatus());
						photoItem.photoIcon
								.setImageResource(R.drawable.default_video);
					}

					break;
				}
				}

			}
			convertView.setTag(photoItem);

		} else {
			photoItem = (PhotoItem) convertView.getTag();
		}

		return convertView;
	}

	public Drawable getDrawable(AsyncDataLoad asyncImageLoader,
			String imageUrl, final ImageView imageView) {
		Drawable drawable = asyncImageLoader.loadDrawable(imageUrl,
				new ImageCallback() {
					@Override
					public void imageLoaded(Drawable imageDrawable,
							String imageUrl) {
						if (imageDrawable != null)
							imageView.setImageDrawable(imageDrawable);
						else
							imageView.setImageResource(R.drawable.icon);
					}
				});
		return drawable;
	}

	static class PhotoItem {
		ImageView photoIcon;
		TextView photoName;
	}

}
