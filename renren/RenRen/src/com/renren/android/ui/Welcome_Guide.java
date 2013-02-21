package com.renren.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.renren.android.R;

/**
 * 引导界面
 * 
 * @author rendongwei
 * 
 */
public class Welcome_Guide extends Activity {
	private ImageView mShowPicture;
	private TextView mShowText;
	private Button mRegister;
	private Button mWhoIKnow;
	private Button mLogin;
	/**
	 * 三个切换的动画
	 */
	private Animation mFadeIn;
	private Animation mFadeInScale;
	private Animation mFadeOut;
	/**
	 * 三个图片
	 */
	private Drawable mPicture_1;
	private Drawable mPicture_2;
	private Drawable mPicture_3;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_guide);
		findViewById();
		init();
		setListener();
	}

	/**
	 * 绑定UI
	 */
	private void findViewById() {
		mShowPicture = (ImageView) findViewById(R.id.guide_picture);
		mShowText = (TextView) findViewById(R.id.guide_content);
		mRegister = (Button) findViewById(R.id.guide_register);
		mWhoIKnow = (Button) findViewById(R.id.guide_who_i_know);
		mLogin = (Button) findViewById(R.id.guide_login);
	}

	/**
	 * 监听事件
	 */
	private void setListener() {
		/**
		 * 动画切换原理:开始时是用第一个渐现动画,当第一个动画结束时开始第二个放大动画,当第二个动画结束时调用第三个渐隐动画,
		 * 第三个动画结束时修改显示的内容并且重新调用第一个动画,从而达到循环效果
		 */
		mFadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeInScale);
			}
		});
		mFadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				mShowPicture.startAnimation(mFadeOut);
			}
		});
		mFadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				/**
				 * 这里其实有些写的不好,还可以采用更多的方式来判断当前显示的是第几个,从而修改数据,
				 * 我这里只是简单的采用获取当前显示的图片来进行判断。
				 */
				if (mShowPicture.getDrawable().equals(mPicture_1)) {
					mShowText.setText("同学情,请珍藏");
					mShowPicture.setImageDrawable(mPicture_2);
				} else if (mShowPicture.getDrawable().equals(mPicture_2)) {
					mShowText.setText("共奋斗,同分享");
					mShowPicture.setImageDrawable(mPicture_3);
				} else if (mShowPicture.getDrawable().equals(mPicture_3)) {
					mShowText.setText("儿时友,莫相忘");
					mShowPicture.setImageDrawable(mPicture_1);
				}
				mShowPicture.startAnimation(mFadeIn);
			}
		});
		mRegister.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(Welcome_Guide.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mWhoIKnow.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(Welcome_Guide.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mLogin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(Welcome_Guide.this, Auth.class));
				finish();
			}
		});
	}

	/**
	 * 初始化
	 */
	private void init() {
		initAnim();
		initPicture();
		/**
		 * 界面刚开始显示的内容
		 */
		mShowPicture.setImageDrawable(mPicture_1);
		mShowText.setText("儿时友,莫相忘");
		mShowPicture.startAnimation(mFadeIn);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		mFadeIn = AnimationUtils.loadAnimation(Welcome_Guide.this,
				R.anim.v5_0_1_guide_welcome_fade_in);
		mFadeIn.setDuration(1000);
		mFadeInScale = AnimationUtils.loadAnimation(Welcome_Guide.this,
				R.anim.v5_0_1_guide_welcome_fade_in_scale);
		mFadeInScale.setDuration(6000);
		mFadeOut = AnimationUtils.loadAnimation(Welcome_Guide.this,
				R.anim.v5_0_1_guide_welcome_fade_out);
		mFadeOut.setDuration(1000);
	}

	/**
	 * 初始化图片
	 */
	private void initPicture() {
		mPicture_1 = getResources().getDrawable(R.drawable.v5_0_1_guide_pic1);
		mPicture_2 = getResources().getDrawable(R.drawable.v5_0_1_guide_pic2);
		mPicture_3 = getResources().getDrawable(R.drawable.v5_0_1_guide_pic3);
	}
}
