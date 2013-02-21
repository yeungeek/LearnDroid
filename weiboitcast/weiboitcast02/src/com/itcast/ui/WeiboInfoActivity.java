package com.itcast.ui;

import java.util.Date;
import java.util.HashMap;

import weibo4j.model.Status;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.util.TextViewLink;


public class WeiboInfoActivity extends WeiboActivity implements OnClickListener{
	private View title;
	private Button back, home;
	//��ʾ�û��Ĳ���
	private RelativeLayout tweet_profile;
	// ������ĸ�ѡ��
	private TextView tvRefresh, tvComment, tvForward, tvFav, tvMore;
	// �û����� ��������
	private TextView tweet_profile_name, tweet_message;
	// �û�ͷ�� �Ƿ�VIP
	private ImageView tweet_profile_preview, tweet_profile_vip;
	// ת�����ݵĲ��� Ĭ��Ϊgone
	private View src_text_block;
	// ת������
	private TextView tweet_oriTxt;
	// ת�����ݵ�ͼƬ Ĭ��Ϊgone
	private ImageView tweet_upload_pic2;
	//ת�����������ͼƬ �������ò���� Ĭ��Ϊgone
	private ImageView tweet_upload_pic;
	//ͷ�� �Ͳ������ݵ�BITMAP
	private Bitmap portrait,contextBt,retweetedBt;
	
	// 					ʱ��              		 ����				ת��				 ����
	private TextView tweet_updated, tweet_comment, tweet_redirect, tweet_via;
	private static final int BACK= -1;
	private static final int REFRESH = 0;
	private static final int COMMENT = 1;//����
	private static final int FORWARD = 2;
	private static final int FAV = 3;
	private static final int MORE = 4;
	private static final int USER = 5;
	private static final int COMMENTLIST = 6;//�����б�
	private Status status;
	


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//��ȡ���ݹ�����΢����Ϣ
		status=(Status)this.getIntent().getSerializableExtra("status");
		   
		setContentView(R.layout.detailweibo);
		//Ϊ�û�������Ӽ����¼�
		tweet_profile = (RelativeLayout) findViewById(R.id.tweet_profile);
		tweet_profile.setTag(USER);
		tweet_profile.setOnClickListener(this);
		title = findViewById(R.id.detailweibo_title);
		back = (Button) title.findViewById(R.id.title_bt_left);
	    back.setOnClickListener(this);
	    back.setTag(BACK);
		back.setText(R.string.imageviewer_back);
		home = (Button) title.findViewById(R.id.title_bt_right);
		
		home.setBackgroundResource(R.drawable.title_home);
		home.setOnClickListener(this);
		((TextView) title.findViewById(R.id.textView))
				.setText(R.string.title_mblog_content);
		if(status.getRetweetedStatus()!=null){
			src_text_block = findViewById(R.id.src_text_block);
			src_text_block.setVisibility(View.VISIBLE);
			tweet_oriTxt = (TextView)src_text_block.findViewById(R.id.tweet_oriTxt);
			
			//tweet_oriTxt.setText(status.getRetweeted_status().getText());
			TextViewLink.addURLSpan(status.getRetweetedStatus().getText(), tweet_oriTxt);
			tweet_upload_pic2 = (ImageView)src_text_block.findViewById(R.id.tweet_upload_pic2);
		}
		
		tweet_profile_name = (TextView) findViewById(R.id.tweet_profile_name);
		tweet_profile_name.setText(status.getUser().getName());
		tweet_message = (TextView) findViewById(R.id.tweet_message);
		//tweet_message.setText(status.getText());
		//���廰��ľֲ�����
	    TextViewLink.addURLSpan(status.getText(), tweet_message);
		 
		tweet_profile_preview = (ImageView) findViewById(R.id.tweet_profile_preview);
		tweet_upload_pic = (ImageView) findViewById(R.id.tweet_upload_pic);
		tweet_updated =(TextView) findViewById(R.id.tweet_updated);
		tweet_profile_vip = (ImageView) findViewById(R.id.tweet_profile_vip);
		if(status.getUser().isVerified()){//�����VIP
			tweet_profile_vip.setVisibility(View.VISIBLE);
		}
		//���ͷ���Ѿ�����
	    if(MainService.allicon.get(status.getUser().getId())!=null)
        {	
	    	tweet_profile_preview.setImageDrawable(MainService.allicon.get(
	    			status.getUser().getId()));
        }else
        {// �趨ȱʡ��ͼƬ
        	tweet_profile_preview.setImageResource(R.drawable.portrait);	
        }
		
//		DateFormat.format("", inDate)
		Date date = status.getCreatedAt();
		int month = date.getMonth()+1;
		int day = date.getDate();
		int hours = date.getHours();
		int minute = date.getMinutes();
		tweet_updated.setText(month+"-"+day+"  "+hours+"��"+minute);
		tweet_comment =(TextView) findViewById(R.id.tweet_comment);
		tweet_comment.setText("����[δ֪]");//
		tweet_comment.setTag(COMMENTLIST);
		tweet_comment.setOnClickListener(this);
		tweet_redirect =(TextView) findViewById(R.id.tweet_redirect);
		tweet_redirect.setText("ת��[δ֪]");//
		tweet_via =(TextView) findViewById(R.id.tweet_via);
		//��ʾHTMl
		tweet_via.setText("���ԣ�"+Html.fromHtml(status.getSource().toString()));
		tvRefresh = (TextView) findViewById(R.id.tvReload);
		tvRefresh.setTag(REFRESH);
		tvRefresh.setOnClickListener(this);
		tvComment = (TextView) findViewById(R.id.tvComment);
		tvComment.setTag(COMMENT);
		tvComment.setOnClickListener(this);
		tvForward = (TextView) findViewById(R.id.tvForward);
		tvForward.setTag(FORWARD);
		tvForward.setOnClickListener(this);
		tvFav = (TextView) findViewById(R.id.tvFav);
		tvFav.setTag(FAV);
		tvFav.setOnClickListener(this);
		tvMore = (TextView) findViewById(R.id.tvMore);
		tvMore.setTag(MORE);
		tvMore.setOnClickListener(this);
		
		tweet_upload_pic.setOnClickListener(new OnClickListener()
		{
        	@Override
			public void onClick(View v) {
		      Intent it=new Intent(WeiboInfoActivity.this,ShowStatusBitmap.class);
		      it.putExtra("url", status.getOriginalPic());
		      WeiboInfoActivity.this.startActivity(it);
			}
		}
		);
	}

	

	public void onClick(View v) {
		int tag = (Integer) v.getTag();
		Intent it = null;
		switch (tag) {
		case BACK:
			 finish();
			 break;
		case REFRESH:
			Toast.makeText(this, "REFRESH", 3000).show();
			break;
		case COMMENT:
			it = new Intent(this,CommentActivity.class);
			it.putExtra("id", status.getId());
			startActivity(it);
			break;
		case FORWARD:
			it = new Intent(this,ForwardActivity.class);
			it.putExtra("id", status.getId());
			startActivity(it);
			break;
		case FAV:
			Toast.makeText(this, "FAV", 3000).show();
			break;
		case MORE:
			//Toast.makeText(this, "MORE", 3000).show();
			//ͨ�����ŷ�ʽ��������΢������
			Uri uri = Uri.parse("smsto://");
		    Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
		    if(this.status.getText().length()>70)
		    {
		    	 intent.putExtra("sms_body", this.status.getText().substring(0,66)+"...");
		    }else
		    {	
		    intent.putExtra("sms_body", this.status.getText());
		    } 
		    startActivity(intent);	
			break;
		case USER:
			MainService.showus=status.getUser();
			MainActivity.mainUI.th.setCurrentTabByTag("TS_USER");
			MainActivity.mainUI.bt02.setChecked(true);
			finish();
			break;
		case COMMENTLIST:
			Toast.makeText(this, "COMMENTLIST", 3000).show();
			break;
		}
	}
	

	public void init() {
		tweet_profile = (RelativeLayout) findViewById(R.id.tweet_profile);
		tweet_profile.setTag(USER);
		tweet_profile.setOnClickListener(this);
		
		title = findViewById(R.id.detailweibo_title);
		back = (Button) title.findViewById(R.id.title_bt_left);
	    back.setOnClickListener(this);
	    back.setTag(BACK);
		back.setText(R.string.imageviewer_back);
		home = (Button) title.findViewById(R.id.title_bt_right);
		
		home.setBackgroundResource(R.drawable.title_home);
		home.setOnClickListener(this);
		((TextView) title.findViewById(R.id.textView))
				.setText(R.string.title_mblog_content);
		if(status.getRetweetedStatus()!=null){
			src_text_block = findViewById(R.id.src_text_block);
			src_text_block.setVisibility(View.VISIBLE);
			tweet_oriTxt = (TextView)src_text_block.findViewById(R.id.tweet_oriTxt);
			tweet_oriTxt.setText(status.getRetweetedStatus().getText());
			tweet_upload_pic2 = (ImageView)src_text_block.findViewById(R.id.tweet_upload_pic2);
		}
		
		tweet_profile_name = (TextView) findViewById(R.id.tweet_profile_name);
		tweet_profile_name.setText(status.getUser().getName());
		tweet_message = (TextView) findViewById(R.id.tweet_message);
		tweet_message.setText(status.getText());
	    TextViewLink.addURLSpan(status.getText(), tweet_message);
		 
		tweet_profile_preview = (ImageView) findViewById(R.id.tweet_profile_preview);
		tweet_upload_pic = (ImageView) findViewById(R.id.tweet_upload_pic);
		tweet_updated =(TextView) findViewById(R.id.tweet_updated);
		tweet_profile_vip = (ImageView) findViewById(R.id.tweet_profile_vip);
		if(status.getUser().isVerified()){//�����VIP
			tweet_profile_vip.setVisibility(View.VISIBLE);
		}
		//���ͷ���Ѿ�����
	    if(MainService.allicon.get(status.getUser().getId())!=null)
        {	
	    	tweet_profile_preview.setImageDrawable(MainService.allicon.get(
	    			status.getUser().getId()));
        }else
        {// �趨ȱʡ��ͼƬ
        	tweet_profile_preview.setImageResource(R.drawable.portrait);	
        }
		
//		DateFormat.format("", inDate)
		Date date = status.getCreatedAt();
		int month = date.getMonth()+1;
		int day = date.getDate();
		int hours = date.getHours();
		int minute = date.getMinutes();
		tweet_updated.setText(month+"-"+day+"  "+hours+"��"+minute);
		tweet_comment =(TextView) findViewById(R.id.tweet_comment);
		tweet_comment.setText("����[δ֪]");//
		tweet_comment.setTag(COMMENTLIST);
		tweet_comment.setOnClickListener(this);
		tweet_redirect =(TextView) findViewById(R.id.tweet_redirect);
		tweet_redirect.setText("ת��[δ֪]");//
		tweet_via =(TextView) findViewById(R.id.tweet_via);
		//��ʾHTMl
		tweet_via.setText("���ԣ�"+Html.fromHtml(status.getSource().toString()));
		
		
		tvRefresh = (TextView) findViewById(R.id.tvReload);
		tvRefresh.setTag(REFRESH);
		tvRefresh.setOnClickListener(this);
		tvComment = (TextView) findViewById(R.id.tvComment);
		tvComment.setTag(COMMENT);
		tvComment.setOnClickListener(this);
		tvForward = (TextView) findViewById(R.id.tvForward);
		tvForward.setTag(FORWARD);
		tvForward.setOnClickListener(this);
		tvFav = (TextView) findViewById(R.id.tvFav);
		tvFav.setTag(FAV);
		tvFav.setOnClickListener(this);
		tvMore = (TextView) findViewById(R.id.tvMore);
		tvMore.setTag(MORE);
		tvMore.setOnClickListener(this);		
		//�Ƿ����ͼƬ
		if(status.getThumbnailPic()!=null&&status.getThumbnailPic().length()>0)
		{//������������ͼƬ
			HashMap hm=new HashMap();
			hm.put("url", status.getThumbnailPic());
			Task ts=new Task(TaskType.TS_GET_STATUS_PIC,hm);
			MainService.newTask(ts);
		}
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	   if(keyCode==KeyEvent.KEYCODE_BACK)
	   {
		   finish();
		   return true;
	   }
		return super.onKeyDown(keyCode, event);
	}
	public void refresh(Object... param) {
		int type=(Integer)param[0];
		if(type==TaskType.TS_COMMENT_WEIBO)
		{
		   Toast.makeText(this,"���۷���ɹ�",500).show();	
		}else if(type==TaskType.TASK_WEIBO_FORWARD)
		{
			   Toast.makeText(this,"ת��΢���ɹ�",500).show();	
			}
		else if(type==TaskType.TS_GET_STATUS_PIC)
		{
			this.contextBt=((BitmapDrawable)param[1]).getBitmap();
			tweet_upload_pic.setImageBitmap(contextBt);
			tweet_upload_pic.setVisibility(View.VISIBLE);
			Toast t=Toast.makeText(this, "��ȡͼƬ�ɹ�",500);
			ImageView iv=new ImageView(this);
			iv.setImageBitmap(contextBt);
			t.setView(iv);
			t.show();
		}
	}

}
