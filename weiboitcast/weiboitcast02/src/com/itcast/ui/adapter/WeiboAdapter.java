package com.itcast.ui.adapter;

import java.util.HashMap;
import java.util.List;

import weibo4j.model.Status;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.ui.R;
import com.itcast.util.DateUtil;

public class WeiboAdapter extends BaseAdapter{
	public List<Status> allstatus;
	public Context context;
    public WeiboAdapter(List<Status> ls,Context con)
    {this.allstatus=ls;
     this.context=con;	
    }
	@Override //返回当前数据适配器中记录的个数
	public int getCount() {
		// TODO Auto-generated method stub
		return allstatus.size()+2;
	} 
    public void addNewData(List<Status> news)
    {
    	allstatus.addAll(news);
    	this.notifyDataSetChanged();
    }
	@Override //返回当前列表对应的条目信息
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position>0&&position<this.getCount()-1)
		{
			return allstatus.get(position-1);
		}
		return null;
	}
   
    @Override//返回当前列表条目的编号
	public long getItemId(int position) {
		//返回当前条的微博编号
		if(position==0)
			 return 0;//刷新
		if(position==this.getCount()-1)//选中最后一条
			 return -1;//更多

		return allstatus.get(position-1).getIdstr();
	}

	
	private  ViewHolder vh=new ViewHolder();
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(position==0)//刷新
		{
	    View weiboitem=LayoutInflater.from(context).inflate(R.layout.moreitemsview,null);				
	 	TextView tv=(TextView)weiboitem.findViewById(R.id.tvitemtitle);
	     tv.setText("刷新");	
		 return weiboitem;
		}else if(position==this.getCount()-1)//更多
		{
			 View weiboitem=LayoutInflater.from(context).inflate(R.layout.moreitemsview,null);				
			 	TextView tv=(TextView)weiboitem.findViewById(R.id.tvitemtitle);
			     tv.setText("更多");	
				 return weiboitem;
		}else
		{
		View weiboitem=null;
		
		if(convertView!=null&&convertView.findViewById(R.id.tvitemtitle)==null)
	     {Log.d("getview", "doGetView-------get TextView-----------"+position);
			weiboitem=convertView;
	      }else
		{Log.d("getview", "doGetView-------new TextView-----------"+position);
		//把xml布局文件变成View对象
		  weiboitem=LayoutInflater.from(context).inflate(R.layout.itemview, null);
		  }
		  //昵称
			vh.ivItemPortrait = (ImageView)weiboitem.findViewById(R.id.ivItemPortrait);
			vh.tvItemName = (TextView)weiboitem.findViewById(R.id.tvItemName);
			vh.ivItemV = (ImageView)weiboitem.findViewById(R.id.ivItemV);
			vh.tvItemDate = (TextView)weiboitem.findViewById(R.id.tvItemDate);
			vh.ivItemPic = (ImageView)weiboitem.findViewById(R.id.ivItemPic);
			vh.tvItemContent = (TextView)weiboitem.findViewById(R.id.tvItemContent);
			vh.contentPic = (ImageView)weiboitem.findViewById(R.id.contentPic);
			vh.subLayout = weiboitem.findViewById(R.id.subLayout);
			vh.tvItemSubContent = (TextView)vh.subLayout.findViewById(R.id.tvItemSubContent);
			vh.subContentPic = (ImageView)vh.subLayout.findViewById(R.id.subContentPic);
			//vh.ivItemV =(ImageView)vh.subLayout.findViewById(R.id.ivItemV);
			
//		tv.setText(+":"
//				+allstatus.get(position).getText());
	
	    vh.tvItemName.setText(allstatus.get(position-1).getUser().getName());
	    vh.tvItemContent.setText(allstatus.get(position-1).getText());
	    //微博内容
//	    TextViewLink.addURLSpan(allstatus.get(position-1).getText(), vh.tvItemContent);
     //转发内容
	    if(allstatus.get(position-1).getRetweetedStatus()!=null){
			vh.subLayout.setVisibility(View.VISIBLE);
			String txt=allstatus.get(  position-1)
			.getRetweetedStatus().getText();
			int len=0;
			if(txt.length()>15)
			{len=14;}else{
				len=txt.length();
			}
			vh.tvItemSubContent.setText(txt.substring(0, len));
//			TextViewLink.addURLSpan(" "
//					+allstatus.get(position-1)
//					.getRetweeted_status().getText(), vh.tvItemSubContent);
//			 vh.tvItemSubContent.setFocusable(false);
//			
		}else{
			vh.subLayout.setVisibility(View.GONE);
		}
	    vh.tvItemDate.setText(DateUtil.getCreateAt(allstatus.get(position-1).getCreatedAt()));
		//是否实名认证
	    if(allstatus.get(position-1).getUser().isVerified())
	    {  Log.d("ok","ok isVerified");
	    	vh.ivItemV.setVisibility(View.VISIBLE); 
	    }else
	    {
	       vh.ivItemV.setVisibility(View.GONE);	
	    }
	    //判断有没有图片
	    if(allstatus.get(position-1).getThumbnailPic()!=null)
	    {
	    	vh.ivItemPic.setVisibility(View.VISIBLE);
	    }else
	    {
	    	vh.ivItemPic.setVisibility(View.GONE);
	    }
	    //头像
	    //如果头像已经下载
	    if(MainService.allicon.get(allstatus.get(position-1).getUser().getId())!=null)
        {	
	     vh.ivItemPortrait.setImageDrawable(MainService.allicon.get(
	    		 allstatus.get(position-1).getUser().getId()));
        }else
        {// 设定缺省的图片
         vh.ivItemPortrait.setImageResource(R.drawable.portrait);	
       //获取头像
		 HashMap hm=new HashMap();
		 hm.put("us", allstatus.get(position-1).getUser());
		 Task ts=new Task(TaskType.TS_GET_USER_ICON,hm);	
		 MainService.newTask(ts);
        }
	    weiboitem.setTag(allstatus.get(position-1));
	    return weiboitem;
		}
	}
	
	
	
	
}
