package com.renren.android.util;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 超链接
 * 
 * @author rendongwei
 * 
 */
public class IntentSpan extends ClickableSpan {
	private final OnClickListener listener;

	public IntentSpan(View.OnClickListener listener) {
		this.listener = listener;
	}

	public void onClick(View view) {
		listener.onClick(view);
	}

	public void updateDrawState(TextPaint ds) {
		super.updateDrawState(ds);
		/**
		 * 不显示下划线
		 */
		ds.setUnderlineText(false);
	}
}
