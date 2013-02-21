package com.itcast.logic;

import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.cocos2dx.Cocos2dxSimpleGameForAndroid.Cocos2dxSimpleGameForAndroid;

import weibo4j.Account;
import weibo4j.Oauth;
import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.http.AccessToken;
import weibo4j.http.ImageItem;
import weibo4j.model.Paging;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.org.json.JSONObject;

import com.itcast.db.TokenUtil;
import com.itcast.util.GetPicFromURL;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class MainService extends Service implements Runnable{
    public static boolean isrun=false;
    private static ArrayList<Task> allTask=new ArrayList<Task>();
	private static ArrayList<WeiboActivity> allActivity=new ArrayList<WeiboActivity>();
    public static AccessToken appToken;
    public Timeline weibo ;
	
    public static User nowu;
    public static User showus;//当前用户
    //                  用户id    头像
    public static HashMap<String,BitmapDrawable> allicon
                           =new HashMap<String,BitmapDrawable>();
    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    //添加窗口到集合中
    public static void addActivity(WeiboActivity wa)
    {
    	allActivity.add(wa);
    }
    public static void removeActivity(WeiboActivity wa)
    {
    	allActivity.remove(wa);
    }
    public static WeiboActivity getActivityByName(String aname)
    {for(WeiboActivity wa:allActivity)
    {
    	if(wa.getClass().getName().indexOf(aname)>=0)
    	{
    		return wa;
    	}
    	
    }
    return null;
    }
	//添加任务
    public static void newTask(Task ts)
    {
        allTask.add(ts);	
    }
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
        isrun=false;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
		isrun=true;
		Thread t=new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(isrun)
		{  try{
			if(allTask.size()>0)
			{
		     //执行任务
            doTask(allTask.get(0));
			}else{
				try{
				Thread.sleep(2000);}catch(Exception e){}
			}
		}catch(Exception e){
			if(allTask.size()>0)
			allTask.remove(allTask.get(0));
			Log.d("error","------------------"+e);
		}
		}
	}
	private AccessToken getAccessToken(String us,String ps) throws Exception
	{
		//com.itcast.net.http.RequestToken rt=weibo.getOAuthRequestToken();
	    //2.让用户输入授权码	 
		// String pin=weibo.getOAuthPin(us, ps, rt.getToken());
	   //3.使用RequestToken和授权码获取AccessToken
		//AccessToken at= rt.getAccessToken(pin);
		return null;//at;
	}
	private void doTask(Task ts) throws Exception
	{   Message message=hand.obtainMessage();
		message.what=ts.getTaskID();
	    switch(ts.getTaskID())
		{
	    case TaskType.TASK_WEIBO_FORWARD://转发微博
		       String id02=(String)ts.getTaskParam().get("id");
		       String commmsg02=(String)ts.getTaskParam().get("msg");
		       String encocom02 = URLEncoder.encode(commmsg02 , "UTF-8");
		   	   Log.d("forward", "----------"+id02+" comm"+encocom02);
		       //是否作为评论来发表
		       boolean replay02=(Boolean)ts.getTaskParam().get("newc");
		       if(replay02)
		       {   //        3对微博和员微博都发表评论
//		    	   weibo.reply(id02, encocom02,1);
//		    	   weibo.updateComment(commmsg02, id02,null);
		 	      
		       }else
		       {
//		    	     weibo.reply(id02, encocom02,0);
//			    
		       }
	    	break;
	    case TaskType.TASK_NEW_WEIBO_COMMENT://发表评论
	       String id=(String)ts.getTaskParam().get("id");
	       String commmsg=(String)ts.getTaskParam().get("msg");
//	       weibo.updateComment(commmsg, id,null);
	       //是否转发到我的微博
	       boolean replay=(Boolean)ts.getTaskParam().get("news");
	       if(replay)
	       {  String encocom = URLEncoder.encode(commmsg , "UTF-8");
			
//	    	   weibo.reply(id, encocom,0);
	       }
	       
	       break;
	    case TaskType.TS_GET_USER_HOMETIMELINE_MORE://获取下页
	    	int nowpage=(Integer)ts.getTaskParam().get("nowpage");
	    	int pagesize=(Integer)ts.getTaskParam().get("pagesize");
	    //	List<Status> allsmore=weibo.getFriendsTimeline(new Paging(nowpage,pagesize));
	    //	message.obj=allsmore;
	    	break;
	    case TaskType.TS_GET_USER_ICON://下载头像
	    	User u=(User)ts.getTaskParam().get("us");
		    //获取该用户的头像
	    	BitmapDrawable bd=GetPicFromURL.getPic(u.getProfileImageURL());
	    	if(bd!=null)
	    	{
	    		if(Cocos2dxSimpleGameForAndroid.imicon==null)
	    			Cocos2dxSimpleGameForAndroid.imicon=
	    				Bitmap.createBitmap(bd.getBitmap());
	    	      allicon.put(u.getId(), bd);	
	    	}
	    	break;
	    case TaskType.TS_GET_HUATI://获取话题
	    	 String tname=(String)ts.getTaskParam().get("ht"); 
			  String msght = URLEncoder.encode(tname , "UTF-8");
//			 List<Status> ls=weibo.getTrendTimeline(msght);
//			 message.obj=ls;
	    	  break;
		case TaskType.TS_NEW_WEIBO://发表微博
			String msg=(String)ts.getTaskParam().get("msg");
		  	weibo.UpdateStatus(msg);
		    message.obj=1;//1表示发表成功
		      break;
		case TaskType.TS_NEW_WEIBO_PIC://发表图片微博
			String msg1=(String)ts.getTaskParam().get("msg");
		    byte picdat[]=(byte[])ts.getTaskParam().get("picdata");
		    Log.d("picblog", "picdat------------------"+picdat.length);
		    ImageItem item=new ImageItem("pic",picdat) ;
//			weibo.uploadStatus(msg1, item);
	        message.obj=1;//1表示发表成功
			break;
		case TaskType.TS_NEW_WEIBO_GPS://发表GPS微博
			double lat=(Double)ts.getTaskParam().get("lat");
			double lon=(Double)ts.getTaskParam().get("lon");
			String status=(String)ts.getTaskParam().get("msg");
//			weibo.updateStatus(status, lat, lon);
			message.obj=1;
			break;
		case TaskType.TS_GET_USER_HOMETIMELINE://获取用户首页
			Paging p=new Paging(1,5);
				StatusWapper astatus = weibo.getHomeTimeline();
			   List<Status> alls=astatus.getStatuses();
			message.obj=alls;
			break;
		 case TaskType.TS_GET_STATUS_PIC_ORI://获取原始图片
		case TaskType.TS_GET_STATUS_PIC://获取微博的图片内容
			String url=(String)ts.getTaskParam().get("url");
			Log.d("url", "--------------"+url);
			BitmapDrawable bd02=GetPicFromURL.getPic(new URL(url));
			message.obj=bd02;
			break;
		 case TaskType.TS_USER_LOGIN://用户登录
			 HashMap hm=ts.getTaskParam();
			 AccessToken at=null;
			 if(hm!=null)//需要获取AccessToken
			 {String code=(String)hm.get("code");
			 Oauth oauth = new Oauth();	 
			   at=oauth.getAccessTokenByCode(code);
			   appToken=at;
			  //保存到手机
			  TokenUtil.saveAccessToken(this, at);
			 }
			//读取用户资料
			 Account am = new Account();
		     am.client.setToken(appToken.getAccessToken());
			 Users um = new Users();
			 um.client.setToken(appToken.getAccessToken());
			 JSONObject uid =am.getUid();
			User user = um.showUserById(uid.get("uid").toString());
			weibo = new Timeline();
			weibo.setToken(appToken.getAccessToken());
			weibo.client.setToken(appToken.getAccessToken());
		  	
			message.obj=user;
			nowu=user;		 
	        break;
		}
	    allTask.remove(ts);
	    //通知主线程更新UI
		hand.sendMessage(message);
	}
	private Handler hand=new Handler()
	{
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
           switch(msg.what)
           {
          	   
           case TaskType.TS_GET_HUATI://话题
        	    MainService.getActivityByName("HuatiActivity")
        	    .refresh(msg.what,msg.obj);
        	    break;
           case TaskType.TS_USER_LOGIN://登录结果
                MainService.getActivityByName("LoginActivity")
                 .refresh(msg.what,msg.obj);
                break;
           case TaskType.TS_GET_USER_HOMETIMELINE_MORE:
           case TaskType.TS_GET_USER_HOMETIMELINE://获取首页结果
            	MainService.getActivityByName("HomeActivity")
            	.refresh(msg.what,msg.obj);
            	break;
            case TaskType.TS_GET_USER_ICON://有新的头像下载成功了
            	MainService.getActivityByName("HomeActivity")
            	.refresh(msg.what);
            	break;
            case TaskType.TS_GET_STATUS_PIC:
            case TaskType.TS_COMMENT_WEIBO://评论微博
            case TaskType.TASK_WEIBO_FORWARD://转发微博
              	MainService.getActivityByName("WeiboInfoActivity")
            	.refresh(msg.what,msg.obj);
            	break;
            case TaskType.TS_GET_STATUS_PIC_ORI:
            	MainService.getActivityByName("ShowStatusBitmap")
            	 .refresh(msg.what,msg.obj);
            	break;
            	
            case TaskType.TS_NEW_WEIBO:
            case TaskType.TS_NEW_WEIBO_PIC:
            case TaskType.TS_NEW_WEIBO_GPS:
            	MainService.getActivityByName("NewWeiboActivity")
            	 .refresh(msg.what,msg.obj);
           }
		}
	};
    //提示用户网络异常	
	public static void alertNetError(final Context context)
	{
		AlertDialog.Builder ab=new AlertDialog.Builder(context);
		ab.setTitle("网络异常");
		ab.setMessage("网络连接异常，请设置网络或退出程序");
	    ab.setPositiveButton("设置网络", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new 
			             Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)); 
	    	    dialog.cancel();
			}
		});
	    ab.setNegativeButton("退出程序", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//停止服务
			    Intent it=new Intent(context,MainService.class);
			    context.stopService(it);
			    android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	    ab.create().show();
	}
}
