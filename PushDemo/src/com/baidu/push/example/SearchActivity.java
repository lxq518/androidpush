package com.baidu.push.example;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.shortmessage.R;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

public class SearchActivity extends Activity{
	 private Button back;
	 private Button send_btn;
	 private ArrayList<HashMap<String, Object>> mlist;
	 SimpleAdapter mProdList;
	 private SQLiteDatabase mSQLiteDatabase = null;
	 private final static String DATABASE_NAME = "GDmessage.db";
	 private ListView userView;
	
	/* 表名 */
	 private final static String USER = "user";//信息
	/* 表中的字段 */
	 private final static String ID = "_id";//消息ID号
	 private final static String USER_ID = "userid";//用户名
	 private final static String USER_NAME = "username";//用户名
		//建表sql
	private final static String CREATE_TABLE = "CREATE TABLE "+USER+" ("+ID+" INTEGER PRIMARY KEY,"+USER_ID+" TEXT,"+USER_NAME+" TEXT)";
	
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
		try{
			mSQLiteDatabase.execSQL(CREATE_TABLE);
		}
		catch(Exception e){
			System.out.println(e);
		}
		mlist = new ArrayList<HashMap<String, Object>>();
		back= (Button) this.findViewById(R.id.back);
		send_btn= (Button) this.findViewById(R.id.send_btn);
		userView=(ListView)findViewById(R.id.list);
		back.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
	    			intent.setClass(SearchActivity.this, MainActivity.class);
	    			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    			mSQLiteDatabase.close();
	    			startActivity(intent);
	    			SearchActivity.this.finish();
				}
			});
		
		
	    send_btn.setOnClickListener(new OnClickListener(){
		public void onClick(View arg0) {
		Cursor cur=mSQLiteDatabase.query(USER, new String[]{ID}, null, null, null, null, "_id desc");
		if(cur.getCount()>0){
			cur.moveToFirst();
			String idstring=cur.getString(cur.getColumnIndexOrThrow("_id"));
			int id=Integer.parseInt(idstring);
			ContentValues cv=new ContentValues();
			cv.put(ID, (id+1));
			cv.put(USER_ID, "jkjk");
			cv.put(USER_NAME,"张三1");
			mSQLiteDatabase.insert(USER, null, cv);
			System.out.println("db write complete 1");
			//mSQLiteDatabase.close();
			
		}
		else{
			ContentValues cv=new ContentValues();
			cv.put(ID, 1);
			cv.put(USER_ID, "jkjk");
			cv.put(USER_NAME,"张三");
			mSQLiteDatabase.insert(USER, null, cv);
			System.out.println("db write complete");
			//mSQLiteDatabase.close();
		}
		cur.close();
		if(cur.isClosed()){
        	System.out.println("cur is closed");
        }
			}
		});
	
		
		
		
	    
	    final Cursor cur1=mSQLiteDatabase.query(USER, new String[]{ID,USER_NAME}, null, null, null, null, "_id asc");
		//将数据库中的信息显示到custom界面中
        if(cur1!=null&&cur1.getCount()>=0){
        	if(cur1.moveToFirst()){
        		do{
        			    String username=cur1.getString(1);
        			    String id=cur1.getString(0);
        			    System.out.println("id:"+id);
        			    System.out.println("username:"+username);
        			    HashMap<String, Object> map = new HashMap<String, Object>();
        				map.put("name",username);
        				mlist.add(map);
        				mProdList = new SimpleAdapter(this, mlist, R.layout.list_say_back_item,new String[] { "name", "date","msgText"}, new int[] {R.id.messagedetail_row_name, R.id.messagedetail_row_date ,R.id.messagedetail_row_text});
        				userView.setAdapter(mProdList);
        		}while(cur1.moveToNext());
        	}
        	
        }
        cur1.close();
        if(cur1.isClosed()){
        	System.out.println("cur1 is closed");
        }

	    userView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					Intent intent = new Intent();
					intent.setClass(SearchActivity.this, CustomActivity.class);
					final Cursor cur2=mSQLiteDatabase.query(USER, new String[]{ID,USER_NAME}, "_id="+(arg2+1), null, null, null, "_id asc");//数据库查询item arg2等于_id的人名
					if(cur2.getCount()>0 && cur2!=null)
					{
					cur2.moveToFirst();
					String username=cur2.getString(1);
    			    System.out.println("username:"+username);
					if(mSQLiteDatabase.isOpen())
					{
		    			mSQLiteDatabase.close();
		    			}
					intent.putExtra("username", username);
				    cur2.close();
	    			startActivity(intent);
	    			SearchActivity.this.finish();
	    			}
				 }
			});
		  
	}

}
