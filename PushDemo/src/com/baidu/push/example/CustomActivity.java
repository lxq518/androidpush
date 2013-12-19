package com.baidu.push.example;

import java.util.ArrayList;
import java.util.Calendar;

import Thread.MsgThread;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.baidu.android.pushservice.PushConstants;
import com.example.shortmessage.R;

/**
 * 
 * 自定义的显示通知的Activity
 * 
 */
public class CustomActivity extends Activity implements OnScrollListener {

	protected static final String TAG = null;
	View view;
	Button footerButton;
	LinearLayout footerProgressBarLayout;
	int num;
	int num2 = 0;
	int num3;
	int lastItem;
	int scrollState;
	int totalItemCount;
	private ListView talkView;
	private Button back;
	private String titmsg = null;
	private SlideMenu slideMenu;
	private String conmsg = null;
	private Button messageButton;
	private EditText messageText;
	private ArrayList<ChatMsgEntity> list = new ArrayList<ChatMsgEntity>();
	private String username;
	private String sg;
	/* 数据库对象 */
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Resources resource = this.getResources();
		String pkgName = this.getPackageName();
		mSQLiteDatabase = this.openOrCreateDatabase(DATABASE_NAME,
				MODE_PRIVATE, null);
		try {
			mSQLiteDatabase.execSQL(CREATE_TABLE);
		} catch (Exception e) {
			System.out.println(e);
		}
		Intent intent1 = getIntent();
		username = intent1.getStringExtra("username");// 接受从search传过来的收信人姓名
		System.out.println(".........................username" + username);
		if (username == null) {
			username = "sys";
		}
		setContentView(resource.getIdentifier("custom", "layout", pkgName));

		LayoutInflater inflater = LayoutInflater.from(this);
		view = inflater.inflate(R.layout.other_listview_footer_more, null);

		messageText = (EditText) this.findViewById(resource.getIdentifier(
				"msg_et", "id", pkgName));
		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
		back = (Button) findViewById(R.id.back);
		messageButton = (Button) this.findViewById(resource.getIdentifier(
				"send_btn", "id", pkgName));
		talkView = (ListView) this.findViewById(resource.getIdentifier("list",
				"id", pkgName));
		footerButton = (Button) view.findViewById(R.id.button);
		footerProgressBarLayout = (LinearLayout) view
				.findViewById(R.id.linearlayout);
		footerProgressBarLayout.setVisibility(View.GONE);

		messageButton.setOnClickListener(messageButtonListener());
		final Cursor cur1 = mSQLiteDatabase.query(MSG, new String[] { MSG_ID,
				MSG_TITLE, MSG_NAME, MSG_DATE, MSG_CONTENT, MSG_FROM },
				MSG_FROM + " ='" + username + "'", null, null, null, "_id asc");
		// 将数据库中username的信息显示到custom界面中
		num = cur1.getCount();// 获得信息总数
		System.out.println("-------信息总条数:" + num);
		if (num > 10) {
			num2 = num - 10;
			System.out.println("num2:" + num2);
		}
		if (cur1 != null && cur1.getCount() >= 0) {
			if (cur1.moveToPosition(num2)) {
				do {
					String Title = cur1.getString(1);
					String Name = cur1.getString(2);
					String Date = cur1.getString(3);
					String Msg = cur1.getString(4);
					if (Title.equals("0")) {// 用户发的
						int RId = R.layout.list_say_he_item;
						ChatMsgEntity newMessage = new ChatMsgEntity(Name,
								Date, Msg, RId);
						list.add(newMessage);
					} else {// 系统发的
						int RId = R.layout.list_say_me_item;
						ChatMsgEntity newMessage = new ChatMsgEntity(Title,
								Date, Msg, RId);
						list.add(newMessage);
					}
				} while (cur1.moveToNext());
				if (num2 != 0) {
					talkView.addHeaderView(view);
				}
				talkView.setAdapter(new ChatMsgViewAdapter(CustomActivity.this,
						list));
				talkView.setSelection(talkView.getAdapter().getCount() - 1);
			}

			cur1.close();// 关闭数据库库游标
		}

		if (cur1.isClosed()) {
			System.out.println("cur1 is closed");
		}

		footerButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (lastItem == 0
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {

					footerButton.setVisibility(View.GONE);
					footerProgressBarLayout.setVisibility(View.VISIBLE);
					System.out.println("totalItemCount" + totalItemCount);
					System.out.println("num:" + num);
					if (totalItemCount < num) {
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								list.clear();
								final Cursor cur1 = mSQLiteDatabase.query(MSG,
										new String[] { MSG_ID, MSG_TITLE,
												MSG_NAME, MSG_DATE,
												MSG_CONTENT, MSG_FROM },
										MSG_FROM + " ='" + username + "'",
										null, null, null, "_id asc");
								// 将数据库中username的信息显示到custom界面中

								if (cur1 != null && cur1.getCount() >= 0) {
									num2 = num2 - 10;
									if (num2 <= 0) {
										num2 = 0;
										talkView.removeHeaderView(view);
									}
									if (cur1.moveToPosition(num2)) {
										do {
											String Title = cur1.getString(1);
											String Name = cur1.getString(2);
											String Date = cur1.getString(3);
											String Msg = cur1.getString(4);
											if (Title.equals("0")) {// 用户发的
												int RId = R.layout.list_say_he_item;
												ChatMsgEntity newMessage = new ChatMsgEntity(
														Name, Date, Msg, RId);
												list.add(newMessage);
											} else {// 系统发的
												int RId = R.layout.list_say_me_item;
												ChatMsgEntity newMessage = new ChatMsgEntity(
														Title, Date, Msg, RId);
												list.add(newMessage);
											}
										} while (cur1.moveToNext());
									}
									talkView.setAdapter(new ChatMsgViewAdapter(
											CustomActivity.this, list));
									num3 = num2 + 9;
									if (num2 == 0) {
										num3 = 0;
									}
									System.out.println("num2:" + num2);
									System.out.println("num3:" + num3);
									talkView.setSelection(num3);// 将新添加的信息显示到listview底部
									cur1.close();// 关闭数据库库游标
								}

								if (cur1.isClosed()) {
									System.out.println("cur1 is closed");
								}

								footerButton.setVisibility(View.VISIBLE);
								footerProgressBarLayout
										.setVisibility(View.GONE);
							}
						}, 2000);
					}

				}
			}
		});

		back.setOnClickListener(new ImageButton.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("flag", "1");
				intent.setClass(CustomActivity.this, MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (mSQLiteDatabase.isOpen()) {
					mSQLiteDatabase.close();
				}
				startActivity(intent);
				CustomActivity.this.finish();
			}
		});

	}

	private OnClickListener messageButtonListener() {// 显示、存储用户发送的消息
		return new View.OnClickListener() {
			public void onClick(View v) {

				InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(
						messageText.getWindowToken(), 0); // 隐藏键盘

				Log.v(TAG, "onclick >>>>>>>>");
				String name = getName();
				String date = getDate();

				String msgText = getText();// 获取输入的内容
				sg = getText();
				Context ct = CustomActivity.this;
				SharedPreferences sp = ct.getSharedPreferences("gdMessage",
						MODE_PRIVATE);// 存储用户最后发送的信息作为Home界面对该会话的表示
				Editor editor = sp.edit();
				editor.putString("name", name);
				editor.putString("date", date);
				editor.putString("msgText", msgText);
				editor.commit();
				int RId = R.layout.list_say_he_item;

				new MsgThread(msgText);// 向服务器发送消息内容

				ChatMsgEntity newMessage = new ChatMsgEntity(name, date,
						msgText, RId);
				list.add(newMessage);
				// list.add(d0);
				// talkView.addHeaderView(view);
				talkView.setAdapter(new ChatMsgViewAdapter(CustomActivity.this,
						list));
				talkView.setSelection(talkView.getAdapter().getCount() - 1);
				messageText.setText("");

				Cursor cur2 = mSQLiteDatabase.query(MSG,
						new String[] { MSG_ID }, null, null, null, null,
						"_id desc");
				if (cur2.getCount() > 0) {
					cur2.moveToFirst();
					String idstring = cur2.getString(cur2
							.getColumnIndexOrThrow("_id"));
					int id = Integer.parseInt(idstring);
					ContentValues cv = new ContentValues();
					cv.put(MSG_ID, (id + 1));
					cv.put(MSG_TITLE, 0);
					cv.put(MSG_NAME, getName());
					cv.put(MSG_DATE, getDate());
					cv.put(MSG_CONTENT, sg);
					cv.put(MSG_FROM, username);
					mSQLiteDatabase.insert(MSG, null, cv);
					System.out.println("db write complete 1");
					// mSQLiteDatabase.close();
				} else {
					ContentValues cv = new ContentValues();
					cv.put(MSG_ID, 1);
					cv.put(MSG_TITLE, 0);
					cv.put(MSG_NAME, getName());
					cv.put(MSG_DATE, getDate());
					cv.put(MSG_CONTENT, sg);
					cv.put(MSG_FROM, username);
					mSQLiteDatabase.insert(MSG, null, cv);
					System.out.println("db write complete");
					// mSQLiteDatabase.close();
				}
				cur2.close();
				if (cur2.isClosed()) {
					System.out.println("cur2 is closed");
				}
			}
		};
	}

	private String gettitle() {
		return titmsg;
	}

	private String getconmsg() {
		return conmsg;
	}

	private String getName() {
		return getResources().getString(R.string.myDisplayName);
	}

	// shuold be redefine in the future
	private String getDate() {
		Calendar c = Calendar.getInstance();
		String date = String.valueOf(c.get(Calendar.YEAR)) + "-"
				+ String.valueOf(c.get(Calendar.MONTH) + 1) + "-"
				+ String.valueOf(c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	// shuold be redefine in the future
	private String getText() {
		return messageText.getText().toString();

	}

	public void onDestroy() {
		Log.v(TAG, "onDestroy>>>>>>");
		// list = null;
		super.onDestroy();
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(CustomActivity.this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			if (mSQLiteDatabase.isOpen()) {
				mSQLiteDatabase.close();
			}
			CustomActivity.this.finish();
		}
		return true;
	}// 返回按键定义结束

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		String title = intent
				.getStringExtra(PushConstants.EXTRA_NOTIFICATION_TITLE);// 接受百度推送的标题
		String content = intent
				.getStringExtra(PushConstants.EXTRA_NOTIFICATION_CONTENT);// 接受百度推送的内容
		titmsg = title;
		conmsg = content;
		if (titmsg != null || conmsg != null)// 显示、存储 系统发过来的消息
		{
			System.out.println(titmsg);
			String name = gettitle();
			String date = getDate();
			String msgText = getconmsg();
			int RId = R.layout.list_say_me_item;
			ChatMsgEntity newMessage = new ChatMsgEntity(name, date, msgText,
					RId);
			list.add(newMessage);
			talkView.setAdapter(new ChatMsgViewAdapter(CustomActivity.this,
					list));
			talkView.setSelection(talkView.getAdapter().getCount() - 1);

		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		System.out.println("进入onScroll");
		lastItem = firstVisibleItem;
		this.totalItemCount = totalItemCount;
		if (totalItemCount >= num) {
			System.out.println("进入totalremove");
			talkView.removeHeaderView(view);
		}

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		this.scrollState = scrollState;
	}

}
