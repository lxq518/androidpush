package com.baidu.push.example;

import com.baidu.android.pushservice.PushConstants;
import com.example.shortmessage.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;

public class WelcomActivity extends Activity {
	private SQLiteDatabase mSQLiteDatabase = null;
	/* 数据库名 */
	private final static String DATABASE_NAME = "GDmessage.db";
	/* 表名 */
	private final static String MSG = "Msg1";// 信息
	/* 表中的字段 */
	private final static String MSG_ID = "_id";// 消息ID号
	private final static String MSG_TITLE = "msgtitle";// 消息标题
	private final static String MSG_NAME = "name";// 用户名
	private final static String MSG_DATE = "date";// 消息日期
	private final static String MSG_CONTENT = "msgText";// 消息内容
	private final static String MSG_FROM = "msgfrom";// 哪个联系人的会话记录
	// 建表sql
	private final static String CREATE_TABLE = "CREATE TABLE " + MSG + " ("
			+ MSG_ID + " INTEGER PRIMARY KEY," + MSG_TITLE + " TEXT,"
			+ MSG_NAME + " TEXT," + MSG_DATE + " TEXT," + MSG_CONTENT
			+ " TEXT," + MSG_FROM + " TEXT)";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		System.out.println("进入welcome");
		mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME,
				MODE_PRIVATE, null);
		try {
			mSQLiteDatabase.execSQL(CREATE_TABLE);
		} catch (Exception e) {
			System.out.println(e);
		}

		Intent intent = getIntent();
		String title = intent
				.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);// 接受百度推送的标题
		String content = intent
				.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);// 接受百度推送的内容
		System.out.println("tile is:" + title);
		System.out.println("content is :" + content);

		SharedPreferences ps = getSharedPreferences("gdMessage", MODE_PRIVATE);
		String login = ps.getString("login", "0");
		if (login.equals("1")) {// 点击通知栏调转到信息界面
			Context ct = WelcomActivity.this;
			SharedPreferences sp = ct.getSharedPreferences("gdMessage",
					MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString("login", "2");
			editor.commit();
			Intent intent1 = new Intent(WelcomActivity.this,
					CustomActivity.class);
			if (mSQLiteDatabase.isOpen()) {
				mSQLiteDatabase.close();
			}
			WelcomActivity.this.finish();
			startActivity(intent1);
		} else {
			if (login.equals("2")) {// 登陆成功跳到聊天记录界面
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent1 = new Intent(WelcomActivity.this,
								MainActivity.class);
						if (mSQLiteDatabase.isOpen()) {
							mSQLiteDatabase.close();
						}
						WelcomActivity.this.finish();
						startActivity(intent1);
					}
				}, 2000);
			} else {// 第一次登陆
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent(WelcomActivity.this,
								PushDemoActivity.class);
						if (mSQLiteDatabase.isOpen()) {
							mSQLiteDatabase.close();
						}
						WelcomActivity.this.finish();
						startActivity(intent);

					}
				}, 4000);
			}
		}
	}

	public void finish() {
		System.out.println("进入welcomefinish");
		super.finish();
	}
}
