package com.anhuioss.crowdroid;

import java.util.Locale;

import com.anhuioss.crowdroid.LoginActivity;
import com.anhuioss.crowdroid.R;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_splash);

		ImageView imageView = (ImageView) findViewById(R.id.splash_image);
		String language = Locale.getDefault().getLanguage();
		if (language.equals("zh")) {
//			imageView.setImageResource(R.drawable.icon_login_cn);
			imageView.setImageResource(R.drawable.splash_description_zh);
		} else if (language.equals("en")) {
//			imageView.setImageResource(R.drawable.icon_login_en);
			imageView.setImageResource(R.drawable.splash_description_en);
		} else if (language.equals("ja")) {
//			imageView.setImageResource(R.drawable.icon_login_jp);
			imageView.setImageResource(R.drawable.splash_description_jp);
		} else {
//			imageView.setImageResource(R.drawable.icon_login_en);
			imageView.setImageResource(R.drawable.splash_description_en);
		}

		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				Intent intent = new Intent(SplashActivity.this,
						LoginActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}

		}, 2500);
	}

}
