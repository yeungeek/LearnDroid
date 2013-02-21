package com.itcast.ui;


import weibo4j.model.User;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.itcast.logic.MainService;
import com.itcast.logic.WeiboActivity;


public class UserInfoActivity extends WeiboActivity implements OnClickListener{


	private User user;
	private View title;
	private Button back,home;
	private TextView titleTv;
	//				  头像					名字后面的小图片  默认 android:visibility="invisible"
	private ImageView ivItemPortrait,ivItemGender;
	//				名字
	private TextView tvItemName;
	//名字和地址中间的默认 android:visibility="invisible"
	private TextView tvItemProvince;//用来显示账号信息
//	地址 默认 android:visibility="invisible"
	private TextView tvItemAccount;
//	个性签名 默认 android:visibility="invisible"
	private TextView tvItemContent;
//	关注他   默认 android:visibility="gone"
	private Button btn_atten;
//	发私信 默认 android:visibility="gone"
	private Button btn_message;
//	@他 默认 android:visibility="gone"
	private Button btn_at;
//	<!-- 用来显示 微博数量 关注数量 粉丝数量 默认 android:visibility="gone" --> 
//	total下面有显示用的6个TextViw  现在用不到  暂时不列出
	private View total;
//	显示下面按钮的 默认为 android:visibility="invisible"
	private View usrinfo_detail_block;
//	 <!-- 按钮第一排 --> <!-- 默认android:visibility="gone" -->
	private View first_btn_group;
//	 <!-- 按钮微博 -->
	private TextView tvLeft;
//	<!-- 按钮粉丝-->
	private TextView tvRight;
	private ImageView ivLeft,ivRight;
//	<!-- 按钮第二排 --><!-- 默认android:visibility="gone" -->
	private View second_btn_group;
//	 <!--按钮第三排，主要是当显示自己的资料时显示一个收藏按钮  --> <!-- 默认android:visibility="gone" -->
	private View third_btn_group;
	private TextView second_tvLeft;//tvLeft
	private TextView second_tvRight;
	private ImageView second_ivLeft,second_ivRight;
	private String sex; // 性别 没有找到获取的方法
	private static final int ATTENTIONHIM = 0;//关注他
	private static final int ATHIM = 1;//@他
	private static final int WEIBO = 2;//微博
	private static final int FANS = 3;//粉丝
	private static final int ATTENTION = 4;//关注
	private static final int TOPIC = 5;//话题
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    user=MainService.showus;
	    if(user==null)
	    user=MainService.nowu;
		setContentView(R.layout.userinfo);
		title = findViewById(R.id.title);
		titleTv = (TextView)title.findViewById(R.id.textView);
		titleTv.setText(user.getName());
		back = (Button)title.findViewById(R.id.title_bt_left);
		back.setText(R.string.imageviewer_back);
//		back.setOnClickListener();
		home = (Button)title.findViewById(R.id.title_bt_right);
		home.setBackgroundResource(R.drawable.title_home);
		back.setText(R.string.imageviewer_back);
		ivItemPortrait = (ImageView)findViewById(R.id.ivItemPortrait);
		ivItemPortrait.setVisibility(View.VISIBLE);
		if(MainService.allicon.get(user.getId())!=null){
			ivItemPortrait.setImageDrawable(
					MainService.allicon.get(user.getId()));
		}
		tvItemName = (TextView)findViewById(R.id.tvItemName);
		tvItemName.setVisibility(View.VISIBLE);
		tvItemName.setText(user.getName());
		tvItemAccount = (TextView)findViewById(R.id.tvItemAccount);
		tvItemAccount.setVisibility(View.VISIBLE);
		tvItemAccount.setText(user.getLocation());
		tvItemContent = (TextView)findViewById(R.id.tvItemContent);
		String content=user.getDescription();
		if(content==null||"".equals(content)){
			content="这家伙很懒呦~什么也没有留下！！";
		}
		tvItemContent.setVisibility(View.VISIBLE);
		ivItemGender = (ImageView)findViewById(R.id.ivItemGender);
		tvItemContent.setText(content);
		if("m".equals(user.getGender())){
			sex = "他";
			ivItemGender.setImageResource(R.drawable.icon_male);
		}else{
			sex = "她";
			ivItemGender.setImageResource(R.drawable.icon_female);
		}
		ivItemGender.setVisibility(View.VISIBLE);
		btn_atten =(Button)findViewById(R.id.btn_atten);
		btn_atten.setVisibility(View.VISIBLE);
		btn_atten.setText("关注"+sex);
//		btn_atten.setOnClickListener(this);
		btn_atten.setTag(ATTENTIONHIM);
		if(user.isFollowing()){
			btn_atten.setText("取消关注");
		}
		btn_at =(Button)findViewById(R.id.btn_at);
		btn_at.setVisibility(View.VISIBLE);
		btn_at.setText("@"+sex);
		btn_at.setOnClickListener(this);
		btn_at.setTag(ATHIM);
		
		usrinfo_detail_block = findViewById(R.id.usrinfo_detail_block);
		usrinfo_detail_block.setVisibility(View.VISIBLE);
		
		first_btn_group = findViewById(R.id.first_btn_group);
		first_btn_group.setVisibility(View.VISIBLE);
		tvLeft = (TextView)first_btn_group.findViewById(R.id.tvLeft);
		tvLeft.setText("微博("+user.getStatusesCount()+")");
		tvRight = (TextView)first_btn_group.findViewById(R.id.tvRight);
		tvRight.setText("粉丝("+user.getFollowersCount()+")");
		ivLeft = (ImageView)first_btn_group.findViewById(R.id.ivLeft);
		ivLeft.setBackgroundResource(R.drawable.usrinfo_weibo);
		ivRight = (ImageView)first_btn_group.findViewById(R.id.ivRight);;
		ivRight.setBackgroundResource(R.drawable.usrinfo_fans);
		
		second_btn_group = findViewById(R.id.third_btn_group);
		second_btn_group.setVisibility(View.VISIBLE);
		second_tvLeft = (TextView)second_btn_group.findViewById(R.id.tvLeft);
		second_tvLeft.setText("关注("+user.getFriendsCount()+")");
		second_tvRight = (TextView)second_btn_group.findViewById(R.id.tvRight);
		second_tvRight.setText("收藏("+user.getFavouritesCount()+")");
		second_ivLeft = (ImageView)second_btn_group.findViewById(R.id.ivLeft);
		second_ivLeft.setBackgroundResource(R.drawable.usrinfo_focus);
		second_ivRight = (ImageView)second_btn_group.findViewById(R.id.ivRight);;
		second_ivRight.setBackgroundResource(R.drawable.usrinfo_topic);
		
	}
	
	public void onClick(View v) {
		int tag = (Integer)v.getTag();
		switch(tag){
			case ATTENTIONHIM:
				setFollowing();
				break;
			case ATHIM:
				Intent it = new Intent(this,NewWeiboActivity.class);
				it.putExtra("context", "@"+user.getName()+":");
				startActivity(it);
				break;
			case WEIBO:
				break;
			case FANS:
				break;
			case ATTENTION:
				break;
			case TOPIC:
				break;
		}
	}
	public void setFollowing(){
		if(user.isFollowing()){
			Toast.makeText(this, "您已经关注此用户！", 3000).show();
			user.setFollowing(true);
			btn_atten.setText("取消关注");
			
		}else{
			user.setFollowing(false);
			Toast.makeText(this, "您已经取消关注此用户！", 3000).show();
			
		}
	}
    
	@Override
	public void init() {
		user=MainService.showus;
	    if(user==null)
	    user=MainService.nowu;
		titleTv.setText(user.getName());
		back.setText(R.string.imageviewer_back);
//		back.setOnClickListener();
		home.setBackgroundResource(R.drawable.title_home);
		back.setText(R.string.imageviewer_back);
		ivItemPortrait.setVisibility(View.VISIBLE);
		if(MainService.allicon.get(user.getId())!=null){
			ivItemPortrait.setImageDrawable(
					MainService.allicon.get(user.getId()));
		}
		tvItemName.setVisibility(View.VISIBLE);
		tvItemName.setText(user.getName());
		tvItemAccount.setVisibility(View.VISIBLE);
		tvItemAccount.setText(user.getLocation());
		String content=user.getDescription();
		if(content==null||"".equals(content)){
			content="这家伙很懒呦~什么也没有留下！！";
		}
		tvItemContent.setVisibility(View.VISIBLE);
		tvItemContent.setText(content);
		if("m".equals(user.getGender())){
			sex = "他";
			ivItemGender.setImageResource(R.drawable.icon_male);
		}else{
			sex = "她";
			ivItemGender.setImageResource(R.drawable.icon_female);
		}
		ivItemGender.setVisibility(View.VISIBLE);
		btn_atten.setVisibility(View.VISIBLE);
		btn_atten.setText("关注"+sex);
//		btn_atten.setOnClickListener(this);
		btn_atten.setTag(ATTENTIONHIM);
		if(user.isFollowing()){
			btn_atten.setText("取消关注");
		}
		btn_at.setVisibility(View.VISIBLE);
		btn_at.setText("@"+sex);
		btn_at.setOnClickListener(this);
		btn_at.setTag(ATHIM);
		
		usrinfo_detail_block.setVisibility(View.VISIBLE);
		
		first_btn_group.setVisibility(View.VISIBLE);
		tvLeft.setText("微博("+user.getStatusesCount()+")");
		tvRight.setText("粉丝("+user.getFollowersCount()+")");
		ivLeft.setBackgroundResource(R.drawable.usrinfo_weibo);
		ivRight.setBackgroundResource(R.drawable.usrinfo_fans);
		
		second_btn_group.setVisibility(View.VISIBLE);
		second_tvLeft.setText("关注("+user.getFriendsCount()+")");
		second_tvRight.setText("收藏("+user.getFavouritesCount()+")");
		second_ivLeft.setBackgroundResource(R.drawable.usrinfo_focus);
		second_ivRight.setBackgroundResource(R.drawable.usrinfo_topic);
		
	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
		
	}

}
