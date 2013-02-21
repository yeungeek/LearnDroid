package com.itcast.ui;

import java.io.IOException;
import java.io.InputStream;

import weibo4j.util.WeiboConfig;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

public class LogoActivity extends Activity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		InputStream is=getResources().openRawResource(R.raw.config);
	   	try {
			WeiboConfig.props.load(is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//全屏幕显示
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setContentView(R.layout.log);
		//显示3秒钟后自动消失
	    ImageView iv01=(ImageView)this.findViewById(R.id.logo_bg);	
	    //定义一个3秒钟的渐变动画
	    AlphaAnimation aa=new AlphaAnimation(0.1f,1.0f);
	    aa.setDuration(3000);
	    iv01.startAnimation(aa);//开始播放动画   
	    aa.setAnimationListener(new AnimationListener()
	    {

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Intent it=new Intent(LogoActivity.this,LoginActivity.class);
			    LogoActivity.this.startActivity(it);
			    finish();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
	    	
	    }
	    );
	}

}
