package com.itcast.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weibo4j.model.Status;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

import com.itcast.logic.MainService;
import com.itcast.logic.Task;
import com.itcast.logic.TaskType;
import com.itcast.logic.WeiboActivity;
import com.itcast.ui.adapter.WeiboAdapter;

public class HomeActivity extends WeiboActivity{
    public ListView lv;
	public View progress;
	public int nowpage=1;
	public int pagesize=5;
    @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.home);
		lv=(ListView)this.findViewById(R.id.freelook_listview);
	    progress=this.findViewById(R.id.progress);
	    View title=this.findViewById(R.id.freelook_title);
	    Button btleft=(Button)title.findViewById(R.id.title_bt_left);
	    Button btright=(Button)title.findViewById(R.id.title_bt_right);
	    btleft.setBackgroundResource(R.drawable.title_new);
	    btleft.setOnClickListener(new OnClickListener()
	    {
		   @Override
			public void onClick(View v) {
			 Intent it=new Intent(HomeActivity.this,NewWeiboActivity.class);
			 HomeActivity.this.startActivity(it);
			}
	    	
	    });
	    btright.setBackgroundResource(R.drawable.title_reload);
	    TextView tv=(TextView)title.findViewById(R.id.textView);
	    tv.setText(MainService.nowu.getName());
	    //绑定上下问菜单
	    this.registerForContextMenu(lv);
	    //加入对列表条目单击事件的侦听
	    lv.setOnItemClickListener(new OnItemClickListener()
	    {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//			  Toast.makeText(HomeActivity.this, "您选中了 postion="
//					  +position
//					  +" id="+id, 300).show();
			  if(id==0)//刷新
			  {//发送刷新任务
				  Task ts=new Task(TaskType.TS_GET_USER_HOMETIMELINE,null);
					MainService.newTask(ts);
			  }else if(id==-1)//更多
			  {//发送获取下一页的任务
				nowpage++;
				HashMap hm=new HashMap();
				hm.put("nowpage", nowpage);
				hm.put("pagesize", pagesize);
				Task ts=new Task(TaskType.TS_GET_USER_HOMETIMELINE_MORE,hm);
				MainService.newTask(ts);
			  }
			  else{
				Intent it=new Intent(HomeActivity.this,WeiboInfoActivity.class);
				Status st=(Status)parent.getItemAtPosition(position);
				it.putExtra("status", st);
				HomeActivity.this.startActivityForResult(it, 0);
			  }
			}
	    	
	    }
	    );
    }

    
    
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		   AdapterContextMenuInfo ami=(AdapterContextMenuInfo)item.getMenuInfo();
		
		switch(item.getItemId())
		{case 1://转发
		  	Toast.makeText(this, "您要转发"+ami.id, 300).show();
		  	break;
		 case 2://评论
			 break;
		 case 5://查看地图
			 Intent it=new Intent(this,MapViewStatusPoint.class);
			 Status st=(Status)ami.targetView.getTag();
			 it.putExtra("lat", st.getLatitude());
			 it.putExtra("lon", st.getLongitude());
			 it.putExtra("uid", st.getUser().getId());
			 this.startActivity(it);
		}
		return super.onContextItemSelected(item);
	}



	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo acm=(AdapterContextMenuInfo)menuInfo;
		if(acm.id==0||acm.id==-1)
		{
			return ;
		}
		menu.setHeaderTitle("微博功能");
		menu.add(1, 1, 1, "转发");
		menu.add(1, 2, 2, "评论");
		menu.add(1, 3, 3, "收藏");
		menu.add(1, 4, 4, "查看个人资料");
		//判断该条微博是否包含GPS坐标
		Status st=(Status)acm.targetView.getTag();
		if(st.getLatitude()>0)
		{ 
			menu.add(1,5,5,"查看地图");
		}
	}



	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void init() {
		
		Task ts=new Task(TaskType.TS_GET_USER_HOMETIMELINE,null);
		MainService.newTask(ts);
	}

	@Override
	public void refresh(Object... param) {
		int type=(Integer)param[0];
		switch(type)
		{
		case TaskType.TS_GET_USER_HOMETIMELINE_MORE://新的数据
			WeiboAdapter wa02=(WeiboAdapter)lv.getAdapter();
		    List<Status> ls=(List<Status>)param[1];
			wa02.addNewData(ls);
			break;
		 case TaskType.TS_GET_USER_ICON:
		    //刷新列表
			 WeiboAdapter nowwa=(WeiboAdapter)lv.getAdapter();
			 nowwa.notifyDataSetChanged();
			break;
		 case TaskType.TS_GET_USER_HOMETIMELINE://更新首页微博
			 progress.setVisibility(View.GONE);
			 lv.setVisibility(View.VISIBLE);
			List<Status> alls=(List<Status>)param[1];
			if(alls!=null)
			{List<Map<String,Object>> items=new ArrayList<Map<String,Object>>();
//			 for(Status st:alls)
//			 { HashMap hm=new HashMap();
//			   hm.put("name", st.getUser().getName());
//			   hm.put("text", st.getText());
//				 items.add(hm);
//			 }
//			 //定义适配器
//			 SimpleAdapter sa=new SimpleAdapter(this,items,
//					 R.layout.itemview
//					 ,new String[]{"name","text"}
//			         ,new int[]{R.id.tvItemName,R.id.tvItemContent});
			WeiboAdapter wa=new WeiboAdapter(alls,this);
			lv.setAdapter(wa); 
			}
		}
	}

}
