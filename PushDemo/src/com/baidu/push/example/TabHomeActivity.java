package com.baidu.push.example;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.shortmessage.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class TabHomeActivity extends Activity {
	 private ListView talkView;
	 private ImageView search;
	 private SimpleAdapter mProdList;
	 boolean isLongClick=false;
	 private int items;
	 private SQLiteDatabase mSQLiteDatabase = null;
	 private final static String DATABASE_NAME = "GDmessage.db";
	 private final static String MSG = "Msg1";//信息
	 private final static String MSG_ID = "_id";//消息ID号
	 private final static String MSG_FROM = "msgfrom";//哪个联系人的会话记录
	 private String username;
	 private Context context=TabHomeActivity.this;
	 ArrayList<HashMap<String, Object>> mlist;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		
		talkView = (ListView) this.findViewById(R.id.list);
		search= (ImageView) this.findViewById(R.id.search);

	    mlist = new ArrayList<HashMap<String, Object>>();
		search.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
    			intent.setClass(TabHomeActivity.this, SearchActivity.class);
    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    			if(mSQLiteDatabase.isOpen())
    			{
    			mSQLiteDatabase.close();
    			}
    			startActivity(intent);
    			TabHomeActivity.this.finish();
			}
			
		});

		
		 final Cursor cur1=mSQLiteDatabase.query(MSG, new String[]{MSG_ID,MSG_FROM}, null, null, MSG_FROM, null, "_id desc");
			//将数据库中的信息显示到custom界面中
	        if(cur1!=null&&cur1.getCount()>=0){
	        	if(cur1.moveToFirst()){
	        		do{
	        			    String username=cur1.getString(1);
	        			    String id=cur1.getString(0);
	        			    System.out.println("username:"+username);
	        			    System.out.println("id:"+id);
	        			    HashMap<String, Object> map = new HashMap<String, Object>();
	        				map.put("name",username);
	        				mlist.add(map);
	        				mProdList = new SimpleAdapter(this, mlist, R.layout.list_say_back_item,new String[] { "name"}, new int[] {R.id.messagedetail_row_name});
	        				talkView.setAdapter(mProdList);
	        		}while(cur1.moveToNext());
	        	}
	        	
	        }
	        cur1.close();
	        if(cur1.isClosed()){
	        	System.out.println("cur1 is closed");
	        }

		talkView.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				isLongClick=true;
				final String name=mlist.get(arg2).toString();
			    System.out.println("长按。。。。。。。。。。。");
				if(isLongClick){
					isLongClick=false;
					Builder builder = new AlertDialog.Builder(TabHomeActivity.this);
					builder.setMessage("确定删除对话？");
					builder.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									isLongClick=false;
									mSQLiteDatabase.execSQL("DELETE FROM "+MSG+" WHERE msgfrom='"+name.substring(6,name.length()-1)+"'");
									 final Cursor cur1=mSQLiteDatabase.query(MSG, new String[]{MSG_ID,MSG_FROM}, null, null, MSG_FROM, null, "_id desc");
										
										//将数据库中的信息显示到custom界面中
								        if(cur1!=null&&cur1.getCount()>=0){
								        	mlist.clear();
								        	if(cur1.moveToFirst()){
								        		do{
								        			    String username=cur1.getString(1);
								        			    String id=cur1.getString(0);
								        			    System.out.println("username:"+username);
								        			    System.out.println("id:"+id);
								        			    HashMap<String, Object> map = new HashMap<String, Object>();
								        				map.put("name",username);
								        				mlist.add(map);
								        		}while(cur1.moveToNext());
								        	}
								        	mProdList = new SimpleAdapter(context, mlist, R.layout.list_say_back_item,new String[] { "name"}, new int[] {R.id.messagedetail_row_name});
					        				talkView.setAdapter(mProdList);
								        }
								        cur1.close();
								        if(cur1.isClosed()){
								        	System.out.println("cur1 is closed");
								        }
								}
							});
					builder.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									isLongClick=false;
								}
							});
					builder.show();
				
				}
				return false;
			}
			
		});
		

		talkView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent();
				intent.putExtra("item", Integer.toString(arg2));
				items=arg2;
				System.out.println(".................");
			if(!isLongClick){
				String name=mlist.get(arg2).toString();
				username=name.substring(6,name.length()-1);
				
				intent.setClass(TabHomeActivity.this, CustomActivity.class);
				if(mSQLiteDatabase.isOpen()){
	    			mSQLiteDatabase.close();}
				intent.putExtra("username", username);
				TabHomeActivity.this.finish();
    			startActivity(intent);
			 }
			}
		});
		
	}
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {//拦截返回按钮事件
			Builder builder = new AlertDialog.Builder(TabHomeActivity.this);
			builder.setMessage("您确定退出？");
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Intent startMain = new Intent(Intent.ACTION_MAIN);
							startMain.addCategory(Intent.CATEGORY_HOME);
							startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(startMain);
							System.exit(0);
						}
					});
			builder.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							Toast.makeText(TabHomeActivity.this, "取消",
									Toast.LENGTH_SHORT).show();
						}
					});
			builder.show();
		}
		return true;
	}
	
}
