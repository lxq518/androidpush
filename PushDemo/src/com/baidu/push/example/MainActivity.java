package com.baidu.push.example;


import com.example.shortmessage.R;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
 		tabHost.setup();
		Intent intent = new Intent().setClass(this, TabHomeActivity.class);
		setupTab(tabHost, "聊天", R.drawable.tab_item_home, intent);
		intent = new Intent().setClass(this, TabSettingActivity.class);
		setupTab(tabHost, "设置", R.drawable.tab_item_setting, intent);
		intent = new Intent().setClass(this, TabMoreActivity.class);
		setupTab(tabHost, "更多", R.drawable.tab_item_more, intent);
//		Intent intent1 =getIntent();
////		username = intent1.getStringExtra("username");
//		flag=intent1.getStringExtra("flag");
//		if(flag.equals("1")){
//		System.out.println("进入flag1");
////		intent1.putExtra("username", username);
////		tabHost.setCurrentTab(0);//设置初始选中状态为第一个tab 
//		tabHost.setCurrentTabByTag ("聊天");
//		}
	tabHost.setOnTabChangedListener(new OnTabChangeListener(){
	         @Override
	         public void onTabChanged(String tabId) {
	        	 if(tabId.equals("聊天")){ 
	 	        	System.out.println("进入聊天");
	 	        }else if(tabId.equals("设置")){ 
	 	        	System.out.println("进入设置");
	 	        }else if(tabId.equals("更多")){ 
	 	        	System.out.println("进入更多");
	 	        }  
	         }            
	     });
	}

	  
	
	
	
	
	private void setupTab(TabHost tabhost, String tag, int drawable,
			Intent intent) {
		View tabview = createTabView(tabhost.getContext(), tag, drawable);
		TabSpec setContent = tabhost.newTabSpec(tag)
				.setIndicator(tabview)//设置Tab标签和图标
				.setContent(intent);//设置Tab内容
		tabhost.addTab(setContent);
	}

	private View createTabView(Context context, String text, int drawable) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab, null);
		TextView tv = (TextView) view.findViewById(R.id.tabtext);
		tv.setText(text);
		ImageView iv = (ImageView) view.findViewById(R.id.tabicon);
		iv.setImageResource(drawable);
		return view;
	}
	
	
	public void onStart() {
		System.out.println("lala进入onStart");
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("lala进入onResume");
	}
	@Override
	public void onStop() {
		System.out.println("lala进入onStop");
		super.onStop();
	}
	public void onRestart(){
		System.out.println("lala进入onRestart");
		super.onRestart();
	}
	public void onPause(){
		System.out.println("lala进入onPause");
		super.onPause();
		MainActivity.this.finish();
	}
	
}
