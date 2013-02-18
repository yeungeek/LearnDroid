package com.anhuioss.crowdroid.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ProgressBar;

public class HandleProgressDialog extends Dialog {

	public HandleProgressDialog(Context context) {
		super(context);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		ProgressBar progressBar = new ProgressBar(context);
		progressBar.setPadding(5, 5, 5, 5);
		setContentView(progressBar);
	}

}
