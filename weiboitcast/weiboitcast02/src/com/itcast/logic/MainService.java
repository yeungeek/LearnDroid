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
    public static User showus;//��ǰ�û�
    //                  �û�id    ͷ��
    public static HashMap<String,BitmapDrawable> allicon
                           =new HashMap<String,BitmapDrawable>();
    @Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
    //��Ӵ��ڵ�������
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
	//�������
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
		     //ִ������
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
	    //2.���û�������Ȩ��	 
		// String pin=weibo.getOAuthPin(us, ps, rt.getToken());
	   //3.ʹ��RequestToken����Ȩ���ȡAccessToken
		//AccessToken at= rt.getAccessToken(pin);
		return null;//at;
	}
	private void doTask(Task ts) throws Exception
	{   Message message=hand.obtainMessage();
		message.what=ts.getTaskID();
	    switch(ts.getTaskID())
		{
	    case TaskType.TASK_WEIBO_FORWARD://ת��΢��
		       String id02=(String)ts.getTaskParam().get("id");
		       String commmsg02=(String)ts.getTaskParam().get("msg");
		       String encocom02 = URLEncoder.encode(commmsg02 , "UTF-8");
		   	   Log.d("forward", "----------"+id02+" comm"+encocom02);
		       //�Ƿ���Ϊ����������
		       boolean replay02=(Boolean)ts.getTaskParam().get("newc");
		       if(replay02)
		       {   //        3��΢����Ա΢������������
//		    	   weibo.reply(id02, encocom02,1);
//		    	   weibo.updateComment(commmsg02, id02,null);
		 	      
		       }else
		       {
//		    	     weibo.reply(id02, encocom02,0);
//			    
		       }
	    	break;
	    case TaskType.TASK_NEW_WEIBO_COMMENT://��������
	       String id=(String)ts.getTaskParam().get("id");
	       String commmsg=(String)ts.getTaskParam().get("msg");
//	       weibo.updateComment(commmsg, id,null);
	       //�Ƿ�ת�����ҵ�΢��
	       boolean replay=(Boolean)ts.getTaskParam().get("news");
	       if(replay)
	       {  String encocom = URLEncoder.encode(commmsg , "UTF-8");
			
//	    	   weibo.reply(id, encocom,0);
	       }
	       
	       break;
	    case TaskType.TS_GET_USER_HOMETIMELINE_MORE://��ȡ��ҳ
	    	int nowpage=(Integer)ts.getTaskParam().get("nowpage");
	    	int pagesize=(Integer)ts.getTaskParam().get("pagesize");
	    //	List<Status> allsmore=weibo.getFriendsTimeline(new Paging(nowpage,pagesize));
	    //	message.obj=allsmore;
	    	break;
	    case TaskType.TS_GET_USER_ICON://����ͷ��
	    	User u=(User)ts.getTaskParam().get("us");
		    //��ȡ���û���ͷ��
	    	BitmapDrawable bd=GetPicFromURL.getPic(u.getProfileImageURL());
	    	if(bd!=null)
	    	{
	    		if(Cocos2dxSimpleGameForAndroid.imicon==null)
	    			Cocos2dxSimpleGameForAndroid.imicon=
	    				Bitmap.createBitmap(bd.getBitmap());
	    	      allicon.put(u.getId(), bd);	
	    	}
	    	break;
	    case TaskType.TS_GET_HUATI://��ȡ����
	    	 String tname=(String)ts.getTaskParam().get("ht"); 
			  String msght = URLEncoder.encode(tname , "UTF-8");
//			 List<Status> ls=weibo.getTrendTimeline(msght);
//			 message.obj=ls;
	    	  break;
		case TaskType.TS_NEW_WEIBO://����΢��
			String msg=(String)ts.getTaskParam().get("msg");
		  	weibo.UpdateStatus(msg);
		    message.obj=1;//1��ʾ����ɹ�
		      break;
		case TaskType.TS_NEW_WEIBO_PIC://����ͼƬ΢��
			String msg1=(String)ts.getTaskParam().get("msg");
		    byte picdat[]=(byte[])ts.getTaskParam().get("picdata");
		    Log.d("picblog", "picdat------------------"+picdat.length);
		    ImageItem item=new ImageItem("pic",picdat) ;
//			weibo.uploadStatus(msg1, item);
	        message.obj=1;//1��ʾ����ɹ�
			break;
		case TaskType.TS_NEW_WEIBO_GPS://����GPS΢��
			double lat=(Double)ts.getTaskParam().get("lat");
			double lon=(Double)ts.getTaskParam().get("lon");
			String status=(String)ts.getTaskParam().get("msg");
//			weibo.updateStatus(status, lat, lon);
			message.obj=1;
			break;
		case TaskType.TS_GET_USER_HOMETIMELINE://��ȡ�û���ҳ
			Paging p=new Paging(1,5);
				StatusWapper astatus = weibo.getHomeTimeline();
			   List<Status> alls=astatus.getStatuses();
			message.obj=alls;
			break;
		 case TaskType.TS_GET_STATUS_PIC_ORI://��ȡԭʼͼƬ
		case TaskType.TS_GET_STATUS_PIC://��ȡ΢����ͼƬ����
			String url=(String)ts.getTaskParam().get("url");
			Log.d("url", "--------------"+url);
			BitmapDrawable bd02=GetPicFromURL.getPic(new URL(url));
			message.obj=bd02;
			break;
		 case TaskType.TS_USER_LOGIN://�û���¼
			 HashMap hm=ts.getTaskParam();
			 AccessToken at=null;
			 if(hm!=null)//��Ҫ��ȡAccessToken
			 {String code=(String)hm.get("code");
			 Oauth oauth = new Oauth();	 
			   at=oauth.getAccessTokenByCode(code);
			   appToken=at;
			  //���浽�ֻ�
			  TokenUtil.saveAccessToken(this, at);
			 }
			//��ȡ�û�����
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
	    //֪ͨ���̸߳���UI
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
          	   
           case TaskType.TS_GET_HUATI://����
        	    MainService.getActivityByName("HuatiActivity")
        	    .refresh(msg.what,msg.obj);
        	    break;
           case TaskType.TS_USER_LOGIN://��¼���
                MainService.getActivityByName("LoginActivity")
                 .refresh(msg.what,msg.obj);
                break;
           case TaskType.TS_GET_USER_HOMETIMELINE_MORE:
           case TaskType.TS_GET_USER_HOMETIMELINE://��ȡ��ҳ���
            	MainService.getActivityByName("HomeActivity")
            	.refresh(msg.what,msg.obj);
            	break;
            case TaskType.TS_GET_USER_ICON://���µ�ͷ�����سɹ���
            	MainService.getActivityByName("HomeActivity")
            	.refresh(msg.what);
            	break;
            case TaskType.TS_GET_STATUS_PIC:
            case TaskType.TS_COMMENT_WEIBO://����΢��
            case TaskType.TASK_WEIBO_FORWARD://ת��΢��
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
    //��ʾ�û������쳣	
	public static void alertNetError(final Context context)
	{
		AlertDialog.Builder ab=new AlertDialog.Builder(context);
		ab.setTitle("�����쳣");
		ab.setMessage("���������쳣��������������˳�����");
	    ab.setPositiveButton("��������", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				context.startActivity(new 
			             Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS)); 
	    	    dialog.cancel();
			}
		});
	    ab.setNegativeButton("�˳�����", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//ֹͣ����
			    Intent it=new Intent(context,MainService.class);
			    context.stopService(it);
			    android.os.Process.killProcess(android.os.Process.myPid());
			}
		});
	    ab.create().show();
	}
}
