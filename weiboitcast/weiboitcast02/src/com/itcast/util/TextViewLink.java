package com.itcast.util;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.widget.TextView;

public class TextViewLink {
	public static char strarray[];
	public static void addURLSpan(String str,TextView textView)
	{  SpannableString ss=new SpannableString(str); 
	    strarray=str.toCharArray();
	    int l=str.length()-10;
	    for(int i=0;i<l;i++)
	    {
	    if((i+7<l)&&strarray[i]=='h'&&strarray[i+1]=='t'
	    &&strarray[i+2]=='t'
	    &&strarray[i+3]=='p'
	    &&strarray[i+4]==':'
	    &&strarray[i+5]=='/'
	    &&strarray[i+6]=='/')
	    {
	    StringBuffer sb=new StringBuffer("http://");
	    for(int j=i+7;true;j++)
	    {
//	    if(j>=l){break;} 
	    if(strarray[j]==' '||j==str.length()-1)
	    {   Log.d("http", sb.toString());
	    ss.setSpan(new URLSpan(sb.toString()),i, j, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);  
	        i=j;
	        break;
	    }else
	    {
	    	sb.append(strarray[j]);
	    }
	    }
	    }
	    }
	    l=str.length();
	    StringBuffer sb=null;
	    boolean start=false;
	    int     startIndex=0;
	    for(int i=0;i<l;i++)
	    {    
	    if(strarray[i]=='@')
	    {start=true;
	     sb=new StringBuffer("weibo20://view/");
	     startIndex=i;
	    }
	    else{
	     if(start)
	     {
	     if(strarray[i]==':')
	     {
	      ss.setSpan(new URLSpan(sb.toString()),  startIndex,i, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);      
	      sb=null;
	      start=false;
	     }else{
	    sb.append(strarray[i]); 
	     }
	     }
	    }
	    
	    }
	    //将话题的字符内容实现局部连接
	    start=false;
	     startIndex=0;
	    for(int i=0;i<l;i++)
	    {//如果第i个字符是#    
	    if(strarray[i]=='#')
	    {if(!start)//如果不是开头
	     {
	       start=true;
	       sb=new StringBuffer("weibohuati://view/");
	       startIndex=i;//记住当前话题开头的字符串起始位置
	     }else
	     {    sb.append('#');
	      //设定局部连接
	      ss.setSpan(new URLSpan(sb.toString()), 
	    		  startIndex,i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);      
	      sb=null;
	      start=false; 
	     }
	    }
	    else{
	         if(start)//如果话题开始话题字符逐个添加到StringBuffer中
	         {sb.append(strarray[i]); }
	     }
	    }
	   
	    textView.setText(ss);  
	    textView.setMovementMethod(LinkMovementMethod.getInstance());
	    strarray=null;
	    }

}
