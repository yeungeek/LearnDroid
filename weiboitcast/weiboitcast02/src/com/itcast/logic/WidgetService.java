package com.itcast.logic;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weibo4j.Weibo;
import weibo4j.http.AccessToken;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

import com.itcast.db.TokenUtil;
import com.itcast.util.GetPicFromURL;
import android.app.Service;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class WidgetService extends Service implements Runnable{
	 public boolean isrun;
	 public Weibo weibo;
	 public  ArrayList<Task> allTask =new ArrayList<Task>();
	 public  HashMap<String,BitmapDrawable> allIcon=new HashMap<String,BitmapDrawable>();
	   
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		System.setProperty("weibo4j.oauth.consumerKey", Weibo.CONSUMER_KEY);
//	    System.setProperty("weibo4j.oauth.consumerSecret", Weibo.CONSUMER_SECRET);
	    AccessToken at=TokenUtil.readAccessToken(this);
        weibo=new Weibo();
//	    weibo.setToken(at.getAccessToken(),
//                            at.getExpireIn()); 
	   
		Task ts=new Task(TaskType.TS_GET_USER_HOMETIMELINE,null);
		this.allTask.add(ts);
		isrun=true;
		Thread t=new Thread(this);
		t.start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isrun=false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void run() {
		Task ts;

  	  Log.e("service","service is run.........................");
		while(isrun)
		{
			synchronized(allTask)
			{
			//是否有任务
             if(allTask.size()>0)
             {
			//执行任务
              ts=allTask.get(0);
              try{
              doTask(ts);
              }catch(WeiboException e){
            	  //异常处理
            	  Log.e("error",""+e);
              }catch(Exception e){
            	  Log.e("error",""+e);
              }
             }
			}
			try{
			Thread.sleep(30000);}catch(Exception e){}
		}
	}
	public int weiboIndex=0;
  //执行任务
	public void doTask(Task ts) throws Exception
	{  
	  	switch(ts.getTaskID())
		{
		
		case TaskType.TS_GET_USER_HOMETIMELINE:
			  Log.e("service","doTask is run.........................");
				
			 //获取微博信息
		  Paging page=new Paging(1,5);
//		  ArrayList<Status>	alls=(ArrayList<Status>)weibo.getFriendsTimeline(page);
//		  for(Status st:alls)
//		  {
//			  if(allIcon.get(st.getUser().getId())==null)
//			  {Task tsgeticon=new Task();
//			   tsgeticon.taskID=TaskType.TS_GET_USER_ICON;
//			   HashMap hm=new HashMap();
//			   hm.put("user", st.getUser());
//			   tsgeticon.setTaskParam(hm);
//			   MainService.newTask(tsgeticon);
//			  }
//		  }
		   //更新
//		  UpdateUI(alls);
		  break;
		case TaskType.TS_GET_USER_ICON:
			User iu=(User)ts.getTaskParam().get("user");
			BitmapDrawable bd=GetPicFromURL.getPic(iu.getProfileImageURL());
			if(bd!=null)
			{ //将用户的头像保存到集合
			  allIcon.put(iu.getId(), bd);	
			}
			allTask.remove(ts);
			UpdateUI(bd);
			break;
		}
		}
  public void UpdateUI(ArrayList<Status> st)
  {  Log.e("service","UpdateUI is run.........................");
	
	  //发送一个以广播给Widget对象                                         action
		Intent batteryIntent=new Intent("weibo.statues.new");
		batteryIntent.putExtra("status",st);
		sendBroadcast(batteryIntent);
  }
  public void UpdateUI(BitmapDrawable bd)
  {
	  //发送一个以广播给Widget对象                                         action
		Intent batteryIntent=new Intent("weibo.bitmap.new");
		batteryIntent.putExtra("bmp", bd.getBitmap());
		sendBroadcast(batteryIntent);
  }
}
