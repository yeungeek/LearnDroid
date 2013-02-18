package com.anhuioss.crowdroid.util;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.anhuioss.crowdroid.R;
import com.anhuioss.crowdroid.activity.PictureshowActivity;
import com.anhuioss.crowdroid.util.AsyncImageLoad.ImageCallback;

public class ImageAdapter extends BaseAdapter {
	// 定义Context

	private Handler handler = new Handler();
	private ExecutorService executorService = Executors.newFixedThreadPool(400);
	private Context mContext;
	private ArrayList<String> imagelsit;
	public static ArrayList<Integer> choosed;
	// ImageView imagechoose=null;
	// 定义整型数组 即图片源
	private int index;
	private int count = 0;

	AsyncImageLoad imageLoader = new AsyncImageLoad();

	public ImageAdapter(Context c) {
		mContext = c;
	}

	public ImageAdapter(Context context, ArrayList<String> imagelist,
			ArrayList<Integer> choosed) {
		mContext = context;
		this.imagelsit = imagelist;
		this.choosed = choosed;
	}

	// 获取图片的个数
	public int getCount() {
		return imagelsit.size();
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	public static ArrayList<Integer> getchoosed() {
		return choosed;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = null;
		v = LayoutInflater.from(mContext).inflate(R.layout.pic_gridview_item,
				null);

		final ImageView imageitem = (ImageView) v.findViewById(R.id.pic_item);
		final ImageView imagechoose1 = (ImageView) v
				.findViewById(R.id.pic_item_choose_no);
		final ImageView imagechoose2 = (ImageView) v
				.findViewById(R.id.pic_item_choose_ok);

		if (choosed.get(position) == 1) {
			imagechoose2.setVisibility(View.VISIBLE);
			imagechoose1.setVisibility(View.GONE);
		}
		convertView = v;
		loadImage(imagelsit.get(position), imageitem);

		index = position;
		imagechoose1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (count < 10) {
					imagechoose2.setVisibility(View.VISIBLE);
					imagechoose1.setVisibility(View.GONE);
					choosed.set(position, 1);
					count++;
					PictureshowActivity.okenable();
				} else {
					Toast.makeText(
							mContext,
							mContext.getResources().getString(
									R.string.more_disable), Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
		imagechoose2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				imagechoose2.setVisibility(View.GONE);
				imagechoose1.setVisibility(View.VISIBLE);
				choosed.set(position, 0);
				PictureshowActivity.okenable();
				count--;
			}
		});
		return v;
	}

	private void loadImage(final String url, final ImageView image) {
		// 如果缓存过就会从缓存中取出图像，ImageCallback接口中方法也不会被执行
		ImageView imageView = image;
		Imageback callbackImpl = new Imageback(imageView);
		Bitmap cacheImage = imageLoader.loadBitmap(url, callbackImpl);
		if (cacheImage != null) {
			imageView.setImageBitmap(cacheImage);
		}
	}

}
