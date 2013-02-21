package com.renren.android.photos;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Gallery;

public class PhotosGallery extends Gallery {
	private PhotosImageView imageView;

	public PhotosGallery(Context context) {
		super(context);
	}

	public PhotosGallery(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(new OnTouchListener() {

			float baseValue;
			float originalScale;

			public boolean onTouch(View v, MotionEvent event) {
				View view = PhotosGallery.this.getSelectedView();
				if (view instanceof PhotosImageView) {
					imageView = (PhotosImageView) view;

					if (event.getAction() == MotionEvent.ACTION_DOWN) {
						baseValue = 0;
						originalScale = imageView.getScale();
					}
					if (event.getAction() == MotionEvent.ACTION_MOVE) {
						if (event.getPointerCount() == 2) {
							float x = event.getX(0) - event.getX(1);
							float y = event.getY(0) - event.getY(1);
							float value = (float) Math.sqrt(x * x + y * y);
							if (baseValue == 0) {
								baseValue = value;
							} else {
								float scale = value / baseValue;
								imageView.zoomTo(originalScale * scale, x
										+ event.getX(1), y + event.getY(1));
							}
						}
					}
				}
				return false;
			}
		});
	}

	public PhotosGallery(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		View view = PhotosGallery.this.getSelectedView();
		if (view instanceof PhotosImageView) {
			imageView = (PhotosImageView) view;

			float v[] = new float[9];
			Matrix m = imageView.getImageMatrix();
			m.getValues(v);
			float left, right;
			float width, height;
			width = imageView.getScale() * imageView.getImageWidth();
			height = imageView.getScale() * imageView.getImageHeight();
			if ((int) width <= PhotosDetailActivity.mScreenWidth
					&& (int) height <= PhotosDetailActivity.mScreenHeight) {
				super.onScroll(e1, e2, distanceX, distanceY);
			} else {
				left = v[Matrix.MTRANS_X];
				right = left + width;
				Rect r = new Rect();
				imageView.getGlobalVisibleRect(r);

				if (distanceX > 0) {
					if (r.left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (right < PhotosDetailActivity.mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				} else if (distanceX < 0) {
					if (r.right < PhotosDetailActivity.mScreenWidth) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else if (left > 0) {
						super.onScroll(e1, e2, distanceX, distanceY);
					} else {
						imageView.postTranslate(-distanceX, -distanceY);
					}
				}

			}

		} else {
			super.onScroll(e1, e2, distanceX, distanceY);
		}
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			View view = PhotosGallery.this.getSelectedView();
			if (view instanceof PhotosImageView) {
				imageView = (PhotosImageView) view;
				float width = imageView.getScale() * imageView.getImageWidth();
				float height = imageView.getScale()
						* imageView.getImageHeight();
				if ((int) width <= PhotosDetailActivity.mScreenWidth
						&& (int) height <= PhotosDetailActivity.mScreenHeight) {
					break;
				}
				float v[] = new float[9];
				Matrix m = imageView.getImageMatrix();
				m.getValues(v);
				float top = v[Matrix.MTRANS_Y];
				float bottom = top + height;
				if (top > 0) {
					imageView.postTranslateDur(-top, 200f);
				}
				if (bottom < PhotosDetailActivity.mScreenHeight) {
					imageView.postTranslateDur(
							PhotosDetailActivity.mScreenHeight - bottom, 200f);
				}

			}
			break;
		}
		return super.onTouchEvent(event);
	}
}
