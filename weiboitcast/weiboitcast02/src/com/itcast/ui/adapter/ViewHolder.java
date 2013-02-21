package com.itcast.ui.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public  class ViewHolder{
	ImageView ivItemPortrait;//头像 有默认值
	TextView tvItemName;//昵称
	ImageView ivItemV;//新浪认证 默认gone
	TextView tvItemDate;//时间
	ImageView ivItemPic;//时间图片 不用修改
	TextView tvItemContent;//内容
	ImageView contentPic;//自己增加的内容图片显示的imgView
	View subLayout;//回复默认gone
	TextView tvItemSubContent;//回复内容 subLayout显示才可以显示
	ImageView subContentPic;//自己增加的主要显示回复内容的图片。subLayout显示才可以显示
	}