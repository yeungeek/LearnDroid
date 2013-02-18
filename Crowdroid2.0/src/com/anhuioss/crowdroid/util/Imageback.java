package com.anhuioss.crowdroid.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

public class Imageback implements AsyncImageLoad.ImageCallback {
	private ImageView imageView;
	private Bitmap bitmap;
	private int size = 0;

	public Imageback(ImageView imageView) {
		super();
		this.imageView = imageView;
	}

	@Override
	public void imageLoaded(Bitmap imageDrawable, String imageurl) {
		imageView.setImageBitmap(imageDrawable);
		bitmap = imageDrawable;
		if (bitmap != null)
			size = size + bitmap.getWidth() * bitmap.getHeight() * 4;
	}

	public int getTotalBitmapSize() {
		return size;
	}
}