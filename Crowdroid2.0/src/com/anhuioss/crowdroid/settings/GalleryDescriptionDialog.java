package com.anhuioss.crowdroid.settings;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.anhuioss.crowdroid.R;

public class GalleryDescriptionDialog extends Dialog {

	private int title;
	private int description;
	private int image;

	// -------------------------------------------------------------------
	/** Constructor */
	// -------------------------------------------------------------------
	public GalleryDescriptionDialog(Context context, int title,
			int description, int image) {
		super(context);
		this.title = title;
		this.description = description;
		this.image = image;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.dialog_gallery_description);
		// getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
		// android.R.drawable.ic_menu_more);
		setView();
	}

	// -------------------------------------------------------------------
	/** Inflate the customized dialog */
	// -------------------------------------------------------------------
	protected void setView() {

		// Title
		setTitle(getContext().getString(title));

		// Descctiprion
		TextView descriptionText = (TextView) findViewById(R.id.description);
		descriptionText.setText(description);

		// Image
		ImageView imageView = (ImageView) findViewById(R.id.image);
		imageView.setImageResource(image);

		// OK Button
		Button okButton = (Button) findViewById(R.id.okButton);
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();

			}
		});
	}

}
