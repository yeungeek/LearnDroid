package com.itcast.logic;

import java.util.ArrayList;
import java.util.List;

import weibo4j.http.AccessToken;
import weibo4j.model.Status;

import com.itcast.db.TokenUtil;
import com.itcast.ui.MainActivity;
import com.itcast.ui.NewWeiboActivity;
import com.itcast.ui.R;
import com.itcast.util.NetUtil;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.RemoteViews;

public class MainWeiboWidget extends AppWidgetProvider{
    public ArrayList<Status> st;
    public Bitmap bmp;
    public int statusIndex;//当前是第几条
    public int statusSize; //一共有多少条
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Auto-generated method stub
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		// TODO Auto-generated method stub
		super.onDisabled(context);
		 Intent it=new Intent(context,WidgetService.class);
		 context.stopService(it);
	}

	@Override
	public void onEnabled(Context context) {
		// TODO Auto-generated method stub
		super.onEnabled(context);
		//如果网络正常
	      if(NetUtil.checkNet(context))    	
	      { 
	    	//检测用户是否已经登录
	    	  AccessToken at=TokenUtil.readAccessToken(context);
	    	  if(at!=null)
	    	  { 
		    		 Intent it=new Intent(context,WidgetService.class);
		    		 context.startService(it);
		    	    
	    	  }
	    	 
	      }
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);
		
		String action=intent.getAction();
		Log.d("receive","onReceive------------------action="+action);
		
		if(action.equals("weibo.statues.point"))
		{	this.statusIndex=intent.getIntExtra("point",0);
		Log.d("receive","onReceive------------------statusIndex="+statusIndex);
		
		   AppWidgetManager appWidgetManager = 
			AppWidgetManager.getInstance(context);
		   int[] appWidgetIds=appWidgetManager.getAppWidgetIds(
				new ComponentName(context, MainWeiboWidget.class));
		   //更新所有的Widget实例
		   onUpdate(context, appWidgetManager, appWidgetIds);
		}
		//接收到新的图片
		if(action.equals("weibo.bitmap.new"))
		{	this.bmp=(Bitmap)intent.getParcelableExtra("bmp");
		}
		//接收到新的微博
		if(action.equals("weibo.statues.new")){
			st=(ArrayList<Status>)intent.getSerializableExtra("status");
		   this.statusSize=st.size();
			Log.d("receive","receive"+st);
		if(st!=null)
			Log.d("receive","receive");
		AppWidgetManager appWidgetManager = 
				AppWidgetManager.getInstance(context);
			int[] appWidgetIds=appWidgetManager.getAppWidgetIds(
					new ComponentName(context, MainWeiboWidget.class));
			//更新所有的Widget实例
			onUpdate(context, appWidgetManager, appWidgetIds);
		}
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		if(st==null)return ;
		Log.d("receive","update------------------statusIndex="+statusIndex);
		Log.d("receive","update------------------status size="+st.size());
		
		final int N = appWidgetIds.length;
     // 为每一个属于本provider的App Widget执行此循环程序
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
      //为 App Widget 取得布局并且拴一个on-click 监听器到该 button 上
            RemoteViews views = new RemoteViews(context.getPackageName(),
            		   R.layout.weibowidget1);                  
            //                                                获取当前statusIndex对应的微博信息
            views.setTextViewText(R.id.widget_usr_name,this.st.get(statusIndex).getUser().getName());
            views.setTextViewText(R.id.widget_blog_content,st.get(statusIndex).getText());
          
            //定义对按下上或者按下下的按钮处理
//          Intent itup=new Intent("weibo.status.point");
//          int n=(statusIndex-1);
//          if(n<0){n=statusSize-1;}
//          itup.putExtra("point",n );//保存当前微博的编号
//          PendingIntent piup=PendingIntent.getBroadcast(context, 0,itup,Intent.FLAG_RECEIVER_REPLACE_PENDING);
//          
//          Intent itdown=new Intent("weibo.statues.point");
//          itdown.putExtra("point", (statusIndex+1)%this.statusSize);//保存当前微博的编号
//          PendingIntent pidown=PendingIntent.getBroadcast(context, 0,itdown,Intent.FLAG_RECEIVER_REPLACE_PENDING);
//      
//          views.setOnClickPendingIntent(R.id.widget_btn_up,   piup);
//          views.setOnClickPendingIntent(R.id.widget_btn_down, pidown);
          
            
            //发表微博
             PendingIntent pnewbolg=PendingIntent.getActivity(context,
            		 0,new Intent(context,NewWeiboActivity.class),Intent.FLAG_ACTIVITY_NEW_TASK);
             views.setOnClickPendingIntent(R.id.widget_btn_new_blog, pnewbolg);
            // 告诉 AppWidgetManager 在当前App Widget上 执行一次更新
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
	}

	
}
