package com.baidu.push.example;

import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.baidu.android.pushservice.PushConstants;

/**
 * Push消息处理receiver
 */
public class PushMessageReceiver extends BroadcastReceiver {
	
	private SQLiteDatabase mSQLiteDatabase = null;
	/* 数据库名 */
	 private final static String	DATABASE_NAME = "GDmessage.db";
	/* 表名 */
	 private final static String	MSG = "Msg1";//信息
	/* 表中的字段 */
	 private final static String	MSG_ID = "_id";//消息ID号
	 private final static String MSG_TITLE="msgtitle";//消息标题
	 private final static String	MSG_NAME = "name";//用户名
	 private final static String MSG_DATE = "date";//消息日期
	 private final static String	MSG_CONTENT = "msgText";//消息内容
	 private final static String	MSG_FROM = "msgfrom";//哪个联系人的会话记录
		//建表sql
	private final static String CREATE_TABLE = "CREATE TABLE "+MSG+" ("+MSG_ID+" INTEGER PRIMARY KEY,"+MSG_TITLE+" TEXT,"+MSG_NAME+" TEXT,"+MSG_DATE+" TEXT,"+MSG_CONTENT+" TEXT,"+MSG_FROM+" TEXT)";
	
	
	
	public static final String TAG = PushMessageReceiver.class.getSimpleName();
	@Override
	public void onReceive(final Context context, Intent intent) {
		System.out.println("进入onReceive");

		Log.d(TAG, ">>> Receive intent: \r\n" + intent);

		if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
			System.out.println("进入ACTION_MESSAGE");
			System.out.println("进入PushConstants.ACTION_MESSAGE");
			//获取消息内容
			String message = intent.getExtras().getString(
					PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			//消息的用户自定义内容读取方式
			Log.i(TAG, "onMessage: " + message);

			//用户在此自定义处理消息,以下代码为demo界面展示用
			Intent responseIntent = null;
			responseIntent = new Intent(Utils.ACTION_MESSAGE);
			responseIntent.putExtra(Utils.EXTRA_MESSAGE, message);
//			responseIntent.setClass(context, PushDemoActivity.class);
			responseIntent.setClass(context,CustomActivity.class);
			
			responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(responseIntent);

		} else if (intent.getAction().equals(PushConstants.ACTION_RECEIVE)) {

			System.out.println("进入PushConstants.ACTION_RECEIVE");
			//处理绑定等方法的返回数据
			//PushManager.startWork()的返回值通过PushConstants.METHOD_BIND得到
			
			//获取方法
			final String method = intent.getStringExtra(PushConstants.EXTRA_METHOD);
			//方法返回错误码。若绑定返回错误（非0），则应用将不能正常接收消息。
			//绑定失败的原因有多种，如网络原因，或access token过期。
			//请不要在出错时进行简单的startWork调用，这有可能导致死循环。
			//可以通过限制重试次数，或者在其他时机重新调用来解决。
			final int errorCode = intent.getIntExtra(PushConstants.EXTRA_ERROR_CODE,
							PushConstants.ERROR_SUCCESS);
			//返回内容
			final String content = new String(intent.getByteArrayExtra(PushConstants.EXTRA_CONTENT));
			
			//用户在此自定义处理消息,以下代码为demo界面展示用	
			Log.d(TAG, "onMessage: method : " + method);
			Log.d(TAG, "onMessage: result : " + errorCode);
			Log.d(TAG, "onMessage: content : " + content);
//			Toast.makeText(
//					context,
//					"method : " + method + "\n result: " + errorCode
//							+ "\n content = " + content, Toast.LENGTH_SHORT)
//					.show();
			Toast.makeText(context,"绑定成功", Toast.LENGTH_SHORT)
					.show();

			Intent responseIntent = null;
			responseIntent = new Intent(Utils.ACTION_RESPONSE);
			responseIntent.putExtra(Utils.RESPONSE_METHOD, method);
			responseIntent.putExtra(Utils.RESPONSE_ERRCODE,
					errorCode);
			responseIntent.putExtra(Utils.RESPONSE_CONTENT, content);
			responseIntent.setClass(context, PushDemoActivity.class);//放回登录界面的值
			responseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(responseIntent);
			
		//可选。通知用户点击事件处理
		} 
		else if (intent.getAction().equals(
				PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK)) 
		{
			 
			System.out.println("进入ACTION_RECEIVER_NOTIFICATION_CLICK");
			Log.d(TAG, "intent=" + intent.toUri(0));
			
			SharedPreferences sp = context.getSharedPreferences(
					"gdMessage", Context.MODE_PRIVATE);
			 Editor editor = sp.edit();
			 editor.putString("login","1");
			 editor.commit();
			Intent aIntent = new Intent(PushConstants.ACTION_RECEIVER_NOTIFICATION_CLICK);//通过指定的动作创建Intent
			String title = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);
			System.out.println("title receiver is :"+title);
			String content = intent.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);
			System.out.println("content receiver is :"+content);
			
			//////////////////////////////////////////////////////////
//			if(title.equals("web"))
//			{
//				System.out.println("进入web");
//				Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com"));  
//				it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
//			    it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");  
//			    context.startActivity(it);  
//		    }
			//////////////////////////////////////////////////////////////
			
			aIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_TITLE, title);	
			aIntent.putExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT, content);
			mSQLiteDatabase=context.openOrCreateDatabase(DATABASE_NAME, Context.MODE_PRIVATE, null);
			try{
				mSQLiteDatabase.execSQL(CREATE_TABLE);
			}
			catch(Exception e){
				System.out.println(e);
			}
		
		       Cursor cur=mSQLiteDatabase.query(MSG, new String[]{MSG_ID}, null, null, null, null, "_id desc");
				if(cur.getCount()>0){
					cur.moveToFirst();
					String idstring=cur.getString(cur.getColumnIndexOrThrow("_id"));
					int id=Integer.parseInt(idstring);
					ContentValues cv=new ContentValues();
					cv.put(MSG_ID, (id+1));
					cv.put(MSG_TITLE, title);
					cv.put(MSG_NAME, "0");
					cv.put(MSG_DATE, getDate());
					cv.put(MSG_CONTENT, content);
					cv.put(MSG_FROM,"sys");
					mSQLiteDatabase.insert(MSG, null, cv);
					System.out.println("db write complete 1");
					//mSQLiteDatabase.close();
					
				}
				else{
					ContentValues cv=new ContentValues();
					cv.put(MSG_ID, 1);
					cv.put(MSG_TITLE,title);
					cv.put(MSG_NAME, 0);
					cv.put(MSG_DATE, getDate());
					cv.put(MSG_CONTENT, content);
					cv.put(MSG_FROM, "sys");
					mSQLiteDatabase.insert(MSG, null, cv);
					System.out.println("db write complete");
					//mSQLiteDatabase.close();
				}
				cur.close();
				if(cur.isClosed()){
		        	System.out.println("cur is closed");
		        }
				if(mSQLiteDatabase.isOpen())
				{
					mSQLiteDatabase.close();
				}
			aIntent.setClass(context, CustomActivity.class);
			context.startActivity(aIntent);
			
		}
	}
	private String getDate() {
        Calendar c = Calendar.getInstance();
        String date = String.valueOf(c.get(Calendar.YEAR)) + "-"
                + String.valueOf(c.get(Calendar.MONTH)+1) + "-" + String.valueOf(c.get(Calendar.DAY_OF_MONTH));
        System.out.println(date);
        return date;
    }
}

