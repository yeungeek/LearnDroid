package com.itcast.ui;

import org.cocos2dx.Cocos2dxSimpleGameForAndroid.Cocos2dxSimpleGameForAndroid;
import org.cocos2dx.lib.Cocos2dxActivity;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class MainActivity extends TabActivity{
    public TabHost th;
    public static MainActivity mainUI;
    public RadioButton bt02;
    public MainActivity()
    {
    	mainUI=this;
    }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.maintabs);
		bt02=(RadioButton)this.findViewById(R.id.radio_button2);
		th=this.getTabHost();
		//��ӵ�һ����ҳ Home
		th.addTab(th.newTabSpec("TS_HOME")
				    .setIndicator("TS_HOME")
				    .setContent(new Intent(this,HomeActivity.class))
		          );
		//��ӵڶ�����ҳMSG
		th.addTab(th.newTabSpec("TS_MSG")
			    .setIndicator("TS_MSG")
			    .setContent(new Intent(this,MSGActivity.class))
	          );
	   //��ӵ�������ҳ
		th.addTab(th.newTabSpec("TS_USER")
			    .setIndicator("TS_USER")
			    .setContent(new Intent(this,UserInfoActivity.class))
	          );
		//��ӵ��ĸ���ҳ
		th.addTab(th.newTabSpec("TS_GAME")
			    .setIndicator("TS_GAME")
			    .setContent(new Intent(this,Cocos2dxSimpleGameForAndroid.class))
	          );
		
		//��ӵ������ҳ
		th.addTab(th.newTabSpec("TS_MORE")
			    .setIndicator("TS_MORE")
			    .setContent(new Intent(this,MoreActivity.class))
	          );
		RadioGroup rg=(RadioGroup)this.findViewById(R.id.main_radio);
	    rg.setOnCheckedChangeListener(new OnCheckedChangeListener()
	    {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
              // Toast.makeText(MainActivity.this,"��ѡ����"+checkedId, 1000).show();
				switch(checkedId)
				{case R.id.radio_button0://��ҳ
					th.setCurrentTabByTag("TS_HOME");
					break;
				case R.id.radio_button1://��Ϣ
					th.setCurrentTabByTag("TS_MSG");
					break;
				case R.id.radio_button2://�û�����
					th.setCurrentTabByTag("TS_USER");
					break;
				case R.id.radio_button3://����
					
					th.setCurrentTabByTag("TS_GAME");
					
					break;
				case R.id.radio_button4://����
					th.setCurrentTabByTag("TS_MORE");
					break;
				}
			}
	    	
	    }
	    );
		
	}

	
}
