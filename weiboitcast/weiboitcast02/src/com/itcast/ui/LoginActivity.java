package com.itcast.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import weibo4j.Oauth;
import weibo4j.Timeline;
import weibo4j.http.AccessToken;
import weibo4j.model.Status;
import weibo4j.model.User;
import weibo4j.model.WeiboException;
import weibo4j.util.WeiboConfig;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.itcast.db.TokenUtil;
import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.util.NetUtil;

public class LoginActivity extends WeiboActivity{
    public ProgressDialog pd;
    public WebView wb;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		View title=this.findViewById(R.id.title);
		Button btleft=(Button)title.findViewById(R.id.title_bt_left);
	    btleft.setVisibility(View.GONE);
	    Button btright=(Button)title.findViewById(R.id.title_bt_right);
	    btright.setVisibility(View.GONE);
	    TextView tv=(TextView)title.findViewById(R.id.textView);
	    tv.setText("微博用户登录");
	    //---------------------------------------
	    wb=(WebView)this.findViewById(R.id.web01);
	    wb.getSettings().setJavaScriptEnabled(true);//允许JS执行
	    Button bt01=(Button)this.findViewById(R.id.bt01);
	    bt01.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				try{
					
					
					Oauth oauth = new Oauth();
					wb.loadUrl(oauth.authorize("code"));
					Log.d("ok", "link url"+oauth.authorize("code"));
				}catch(Exception e){
				}
			}
	    	
	    });
	    
	    WebViewClient mc=new WebViewClient(){
       	 public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);//点击超链接的时候重新在原来进程上加载URL
                return true;
            }
			@Override
			public void onPageFinished(WebView view, String url) {
				// TODO Auto-generated method stub
				super.onPageFinished(view, url);
				Log.d("ok","-----------------onPageFinished"+url);
				
			}
        		@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				super.onPageStarted(view, url, favicon);
				Log.d("ok","-----------------onPageStarted"+url);
				if(url.indexOf("http://weibo.eoe.com")==0){
					wb.setVisibility(View.GONE);
					String code=url.substring(url.indexOf("code=")+5);
					Log.d("ok","-----------------code"+code);
					   HashMap hm=new HashMap();
				       hm.put("code", code);
				       
					   Task ts=new Task(TaskType.TS_USER_LOGIN,hm);
					   MainService.newTask(ts);
					   if(pd==null)
					   {
						   pd=new ProgressDialog(LoginActivity.this);
						   pd.setTitle("用户登录");
					   }
					   pd.setMessage("正在登录请稍后....");
					   pd.show();
					
				}
			}

			@Override
			public void onFormResubmission(WebView view, Message dontResend,
					Message resend) {
				// TODO Auto-generated method stub
				super.onFormResubmission(view, dontResend, resend);
			}
       	
       };
       wb.setWebViewClient(mc);
	    
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void init() {
	    if(!NetUtil.checkNet(this))//没有网络
	    {
	    	MainService.alertNetError(this);
	    	
	    }else
	    {//启动系统服务
	    if(!MainService.isrun)
		{  //加载配置信息
	    	
		
			Intent it=new Intent(this,MainService.class);
			this.startService(it);
		}
	    
	    // 是否登录过
		AccessToken at=TokenUtil.readAccessToken(this);
		if(at!=null)
		{   MainService.appToken=at;
//			MainService.weibo.setToken(at.getToken(), at.getTokenSecret());
			//添加登录任务
			Task ts=new Task(TaskType.TS_USER_LOGIN,null);
		    MainService.newTask(ts);
		    //显示自动登录进度条
		    if(pd==null)
		    {
		    	pd=new ProgressDialog(this);
		    	pd.setTitle("登录");
		    }
		    pd.setMessage("正在自动登录,请稍后...");
		    pd.show();
		}else{
			
		}
	    }
	}

	@Override
	public void refresh(Object... param) {
		// TODO Auto-generated method stub
        
		User u=(User)param[1];
		if(u!=null)
		{   pd.cancel();
			Toast.makeText(this, "登录成功欢迎"+u.getName(), 1000).show();
			Intent it=new Intent(this,MainActivity.class);
			this.startActivity(it);
			finish();
		}
	}

}
