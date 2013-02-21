package com.itcast.logic;

public class TaskType {
  public static final int TS_USER_LOGIN=1;//用户登录
  public static final int TS_GET_USER_HOMETIMELINE=2;//获取用户首页信息
  public static final int TS_NEW_WEIBO=3;//发表微博
  public static final int TS_NEW_WEIBO_PIC=31;//发表图片微博
  public static final int TS_NEW_WEIBO_GPS=32;//发表GPS微博
  public static final int TS_NEW_WEIBO_PIC_GPS=33;//发表图片和GPS微博
  
  public static final int TS_COMMENT_WEIBO=4;//评论微博
  public static final int TS_GET_USER_ICON=5;//获取用户头像
  public static final int TS_GET_USER_HOMETIMELINE_MORE=6;//获取下一页信息
  public static final int TS_GET_STATUS_PIC=7;//下载微博图片
  public static final int TS_GET_STATUS_PIC_ORI=8;//获取原始图片
  public static final int TS_GET_HUATI=9;//获取话题
  public static final int TASK_NEW_WEIBO_COMMENT=10;//发表评论
  public static final int TASK_WEIBO_FORWARD=11;//转发微博
}
