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
	//				  ͷ��					���ֺ����СͼƬ  Ĭ�� android:visibility="invisible"
	private ImageView ivItemPortrait,ivItemGender;
	//				����
	private TextView tvItemName;
	//���ֺ͵�ַ�м��Ĭ�� android:visibility="invisible"
	private TextView tvItemProvince;//������ʾ�˺���Ϣ
//	��ַ Ĭ�� android:visibility="invisible"
	private TextView tvItemAccount;
//	����ǩ�� Ĭ�� android:visibility="invisible"
	private TextView tvItemContent;
//	��ע��   Ĭ�� android:visibility="gone"
	private Button btn_atten;
//	��˽�� Ĭ�� android:visibility="gone"
	private Button btn_message;
//	@�� Ĭ�� android:visibility="gone"
	private Button btn_at;
//	<!-- ������ʾ ΢������ ��ע���� ��˿���� Ĭ�� android:visibility="gone" --> 
//	total��������ʾ�õ�6��TextViw  �����ò���  ��ʱ���г�
	private View total;
//	��ʾ���水ť�� Ĭ��Ϊ android:visibility="invisible"
	private View usrinfo_detail_block;
//	 <!-- ��ť��һ�� --> <!-- Ĭ��android:visibility="gone" -->
	private View first_btn_group;
//	 <!-- ��ť΢�� -->
	private TextView tvLeft;
//	<!-- ��ť��˿-->
	private TextView tvRight;
	private ImageView ivLeft,ivRight;
//	<!-- ��ť�ڶ��� --><!-- Ĭ��android:visibility="gone" -->
	private View second_btn_group;
//	 <!--��ť�����ţ���Ҫ�ǵ���ʾ�Լ�������ʱ��ʾһ���ղذ�ť  --> <!-- Ĭ��android:visibility="gone" -->
	private View third_btn_group;
	private TextView second_tvLeft;//tvLeft
	private TextView second_tvRight;
	private ImageView second_ivLeft,second_ivRight;
	private String sex; // �Ա� û���ҵ���ȡ�ķ���
	private static final int ATTENTIONHIM = 0;//��ע��
	private static final int ATHIM = 1;//@��
	private static final int WEIBO = 2;//΢��
	private static final int FANS = 3;//��˿
	private static final int ATTENTION = 4;//��ע
	private static final int TOPIC = 5;//����
	
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
			content="��һ������~ʲôҲû�����£���";
		}
		tvItemContent.setVisibility(View.VISIBLE);
		ivItemGender = (ImageView)findViewById(R.id.ivItemGender);
		tvItemContent.setText(content);
		if("m".equals(user.getGender())){
			sex = "��";
			ivItemGender.setImageResource(R.drawable.icon_male);
		}else{
			sex = "��";
			ivItemGender.setImageResource(R.drawable.icon_female);
		}
		ivItemGender.setVisibility(View.VISIBLE);
		btn_atten =(Button)findViewById(R.id.btn_atten);
		btn_atten.setVisibility(View.VISIBLE);
		btn_atten.setText("��ע"+sex);
//		btn_atten.setOnClickListener(this);
		btn_atten.setTag(ATTENTIONHIM);
		if(user.isFollowing()){
			btn_atten.setText("ȡ����ע");
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
		tvLeft.setText("΢��("+user.getStatusesCount()+")");
		tvRight = (TextView)first_btn_group.findViewById(R.id.tvRight);
		tvRight.setText("��˿("+user.getFollowersCount()+")");
		ivLeft = (ImageView)first_btn_group.findViewById(R.id.ivLeft);
		ivLeft.setBackgroundResource(R.drawable.usrinfo_weibo);
		ivRight = (ImageView)first_btn_group.findViewById(R.id.ivRight);;
		ivRight.setBackgroundResource(R.drawable.usrinfo_fans);
		
		second_btn_group = findViewById(R.id.third_btn_group);
		second_btn_group.setVisibility(View.VISIBLE);
		second_tvLeft = (TextView)second_btn_group.findViewById(R.id.tvLeft);
		second_tvLeft.setText("��ע("+user.getFriendsCount()+")");
		second_tvRight = (TextView)second_btn_group.findViewById(R.id.tvRight);
		second_tvRight.setText("�ղ�("+user.getFavouritesCount()+")");
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
			Toast.makeText(this, "���Ѿ���ע���û���", 3000).show();
			user.setFollowing(true);
			btn_atten.setText("ȡ����ע");
			
		}else{
			user.setFollowing(false);
			Toast.makeText(this, "���Ѿ�ȡ����ע���û���", 3000).show();
			
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
			content="��һ������~ʲôҲû�����£���";
		}
		tvItemContent.setVisibility(View.VISIBLE);
		tvItemContent.setText(content);
		if("m".equals(user.getGender())){
			sex = "��";
			ivItemGender.setImageResource(R.drawable.icon_male);
		}else{
			sex = "��";
			ivItemGender.setImageResource(R.drawable.icon_female);
		}
		ivItemGender.setVisibility(View.VISIBLE);
		btn_atten.setVisibility(View.VISIBLE);
		btn_atten.setText("��ע"+sex);
//		btn_atten.setOnClickListener(this);
		btn_atten.setTag(ATTENTIONHIM);
		if(user.isFollowing()){
			btn_atten.setText("ȡ����ע");
		}
		btn_at.setVisibility(View.VISIBLE);
		btn_at.setText("@"+sex);
		btn_at.setOnClickListener(this);
		btn_at.setTag(ATHIM);
		
		usrinfo_detail_block.setVisibility(View.VISIBLE);
		
		first_btn_group.setVisibility(View.VISIBLE);
		tvLeft.setText("΢��("+user.getStatusesCount()+")");
		tvRight.setText("��˿("+user.getFollowersCount()+")");
		ivLeft.setBackgroundResource(R.drawable.usrinfo_weibo);
		ivRight.setBackgroundResource(R.drawable.usrinfo_fans);
		
		second_btn_group.setVisibility(View.VISIBLE);
		second_tvLeft.setText("��ע("+user.getFriendsCount()+")");
		second_tvRight.setText("�ղ�("+user.getFavouritesCount()+")");
		second_ivLeft.setBackgroundResource(R.drawable.usrinfo_focus);
		second_ivRight.setBackgroundResource(R.drawable.usrinfo_topic);
		
	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
		
	}

}
