package com.baidu.push.example;

import org.json.JSONException;
import org.json.JSONObject;

import Thread.LoginThread;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.android.common.logging.Log;
//import com.baidu.android.pushservice.richmedia.MediaListActivity;
import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class PushDemoActivity extends Activity {

	RelativeLayout mainLayout = null;
	TextView infoText = null;
	Button initButton = null;
	Button initWithApiKey = null;
	Button displayRichMedia = null;
	Button setTags = null;
	Button delTags = null;
	Button login = null;
	public static int initialCnt = 0;
	private boolean isLogin = false;
	EditText txt_username = null;
	EditText txt_password = null;
	TextView txt_toregister = null;
	public String channelId = null;
	private String result = null;
	private String userName = null;
	private String passWord = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Resources resource = this.getResources();
		String pkgName = this.getPackageName();

		setContentView(resource.getIdentifier("login", "layout", pkgName));

		// Context ct = PushDemoActivity.this;
		// SharedPreferences sp = ct.getSharedPreferences(
		// "gdMessage", MODE_PRIVATE);//存储用户最后发送的信息作为Home界面对该会话的表示
		// Editor editor = sp.edit();
		// editor.putString("login","0");
		// editor.commit();

		// txt_toregister = (TextView)
		// findViewById(com.example.shortmessage.R.id.txt_toregister);
		txt_username = (EditText) findViewById(com.example.shortmessage.R.id.txt_username);
		txt_password = (EditText) findViewById(com.example.shortmessage.R.id.txt_password);
		initWithApiKey = (Button) findViewById(resource.getIdentifier("bind",
				"id", pkgName));
		login = (Button) findViewById(resource.getIdentifier("login", "id",
				pkgName));
		// initButton = (Button) findViewById(resource.getIdentifier("btn_init",
		// "id", pkgName));
		// displayRichMedia = (Button)
		// findViewById(resource.getIdentifier("btn_rich", "id", pkgName));
		// setTags = (Button) findViewById(resource.getIdentifier("btn_setTags",
		// "id", pkgName));
		// delTags = (Button) findViewById(resource.getIdentifier("btn_delTags",
		// "id", pkgName));
		initWithApiKey.setOnClickListener(initApiKeyButtonListener());
		login.setOnClickListener(loginButtonListener());
		// initButton.setOnClickListener(initButtonListner());
		// setTags.setOnClickListener(setTagsButtonListner());
		// delTags.setOnClickListener(deleteTagsButtonListner());
		// displayRichMedia.setOnClickListener(richMediaButtonListner());
	}

	// private OnClickListener richMediaButtonListner() {
	// return new View.OnClickListener() {
	//
	// public void onClick(View v) {
	// Intent sendIntent = new Intent();
	// sendIntent.setClass(PushDemoActivity.this,
	// MediaListActivity.class);
	// sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// PushDemoActivity.this.startActivity(sendIntent);
	// }
	// };
	// }

	// private OnClickListener deleteTagsButtonListner() {
	// return new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// LinearLayout layout = new LinearLayout(PushDemoActivity.this);
	// layout.setOrientation(LinearLayout.VERTICAL);
	//
	// final EditText textviewGid = new EditText(PushDemoActivity.this);
	// textviewGid.setHint("请输入多个标签，以英文逗号隔开");
	// layout.addView(textviewGid);
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// PushDemoActivity.this);
	// builder.setView(layout);
	// builder.setPositiveButton("删除标签",
	// new DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog,
	// int which) {
	//
	// List<String> tags = getTagsList(textviewGid
	// .getText().toString());
	//
	// PushManager.delTags(getApplicationContext(),
	// tags);
	//
	// }
	// });
	// builder.show();
	//
	// }
	// };
	// }
	//
	// private OnClickListener setTagsButtonListner() {
	// return new View.OnClickListener() {
	//
	// @Override
	// public void onClick(View arg0) {
	// // 设置标签,以英文逗号隔开
	// LinearLayout layout = new LinearLayout(PushDemoActivity.this);
	// layout.setOrientation(LinearLayout.VERTICAL);
	//
	// final EditText textviewGid = new EditText(PushDemoActivity.this);
	// textviewGid.setHint("请输入多个标签，以英文逗号隔开");
	// layout.addView(textviewGid);
	//
	// AlertDialog.Builder builder = new AlertDialog.Builder(
	// PushDemoActivity.this);
	// builder.setView(layout);
	// builder.setPositiveButton("设置标签",
	// new DialogInterface.OnClickListener() {
	//
	// public void onClick(DialogInterface dialog,
	// int which) {
	//
	// List<String> tags = getTagsList(textviewGid
	// .getText().toString());
	// PushManager.setTags(getApplicationContext(),
	// tags);
	// }
	//
	// });
	// builder.show();
	//
	// }
	// };
	// }
	private OnClickListener loginButtonListener() {
		return new View.OnClickListener() {

			public void onClick(View v) {
				userName = txt_username.getText().toString();
				passWord = txt_password.getText().toString();
				try {

					if (userName.equals("") || passWord.equals("")) {
						new AlertDialog.Builder(PushDemoActivity.this)
								.setTitle("警告！").setMessage("密码或用户名不能为空")
								.setPositiveButton("确定", null).show();
					}

					else {
						System.out.println("///////////////////" + channelId);
						new LoginThread(userName, passWord, channelId);
						Consts.loginHandler = new Handler() {
							public void handleMessage(Message msg) {
								// TODO Auto-generated method stub
								result = msg.getData().getString("result");
								System.out.println("result" + result);

								if (result.equals("1")) {
									new AlertDialog.Builder(
											PushDemoActivity.this)
											.setTitle("警告!")
											.setMessage("密码或用户名错误")
											.setPositiveButton("确定", null)
											.show();
								} else if (result.equals("0")) {
									Context ct = PushDemoActivity.this;
									SharedPreferences sp = ct.getSharedPreferences("gdMessage",
											MODE_PRIVATE);// 存储用户最后发送的信息作为Home界面对该会话的表示
									Editor editor = sp.edit();
									editor.putString("login", "2");
									editor.commit();
									Intent intent = new Intent();
									intent.setClass(PushDemoActivity.this,
											MainActivity.class);
									startActivity(intent);
									PushDemoActivity.this.finish();
								}
							}
						};
					}
				} catch (Exception e) {
					new AlertDialog.Builder(PushDemoActivity.this)
							.setTitle("失败！").setMessage("服务器未响应")
							.setPositiveButton("确定", null).show();
					System.out.println("login de e" + e);
				}

			}
		};
	}

	private OnClickListener initApiKeyButtonListener() {
		return new View.OnClickListener() {

			public void onClick(View v) {
				// 以apikey的方式登录
				System.out.println("进入click");
				PushManager.startWork(getApplicationContext(),
						PushConstants.LOGIN_TYPE_API_KEY,
						Utils.getMetaValue(PushDemoActivity.this, "api_key"));
			}

		};
	}

	// private OnClickListener initButtonListner() {
	// return new View.OnClickListener() {
	//
	// public void onClick(View v) {
	// if (isLogin) {
	// // 已登录则清除Cookie, access token, 设置登录按钮
	// CookieSyncManager.createInstance(getApplicationContext());
	// CookieManager.getInstance().removeAllCookie();
	// CookieSyncManager.getInstance().sync();
	//
	// isLogin = false;
	// initButton.setText("登陆百度账号初始化Channel");
	// }
	// // 跳转到百度账号登录的activity
	// Intent intent = new Intent(PushDemoActivity.this,
	// LoginActivity.class);
	// startActivity(intent);
	// }
	// };
	// }

	@Override
	public void onStart() {
		System.out.println("进入onStart");
		super.onStart();

	}

	@Override
	public void onResume() {
		super.onResume();
		System.out.println("进入onResume");
		showChannelIds();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		// 如果要统计Push引起的用户使用应用情况，请实现本方法，且加上这一个语句
		setIntent(intent);
		System.out.println("进入onNewIntent");
		handleIntent(intent);
	}

	@Override
	public void onStop() {
		System.out.println("进入onStop");
		// PushDemoActivity.this.finish();
		super.onStop();
		// PushManager.activityStoped(this);
	}

	/**
	 * 处理Intent
	 * 
	 * @param intent
	 *            intent
	 */
	private void handleIntent(Intent intent) {
		System.out.println("进入handleIntent");
		String action = intent.getAction();

		if (Utils.ACTION_RESPONSE.equals(action)) {
			System.out.println("进入ACTION_RESPONSE" + action);
			String method = intent.getStringExtra(Utils.RESPONSE_METHOD);

			if (PushConstants.METHOD_BIND.equals(method)) {
				System.out.println("进入METHOD_BIND" + method);
				String toastStr = "";
				int errorCode = intent.getIntExtra(Utils.RESPONSE_ERRCODE, 0);
				if (errorCode == 0) {
					System.out.println("进入errorCode" + errorCode);
					String content = intent
							.getStringExtra(Utils.RESPONSE_CONTENT);
					String appid = "";
					String channelid = "";
					String userid = "";

					try {
						JSONObject jsonContent = new JSONObject(content);
						JSONObject params = jsonContent
								.getJSONObject("response_params");
						appid = params.getString("appid");
						channelid = params.getString("channel_id");
						channelId = params.getString("channel_id");
						userid = params.getString("user_id");
					} catch (JSONException e) {
						Log.e(Utils.TAG, "Parse bind json infos error: " + e);
					}

					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(this);
					Editor editor = sp.edit();
					editor.putString("appid", appid);
					editor.putString("channel_id", channelid);
					editor.putString("user_id", userid);
					editor.commit();

					showChannelIds();
				} else {
					System.out.println("进入Bind" + errorCode);
					toastStr = "Bind Fail, Error Code: " + errorCode;
					if (errorCode == 30607) {
						System.out.println("update channel token-----"
								+ errorCode);
						Log.d("Bind Fail", "update channel token-----!");
					}
				}

				Toast.makeText(this, "..." + toastStr, Toast.LENGTH_LONG)
						.show();
			}
		} else if (Utils.ACTION_LOGIN.equals(action)) {
			System.out.println("进入ACTION_LOGIN" + action);
			String accessToken = intent
					.getStringExtra(Utils.EXTRA_ACCESS_TOKEN);
			PushManager.startWork(getApplicationContext(),
					PushConstants.LOGIN_TYPE_ACCESS_TOKEN, accessToken);
			isLogin = true;
			initButton.setText("更换百度账号初始化Channel");
		} else if (Utils.ACTION_MESSAGE.equals(action)) {
			System.out.println("进入ACTION_MESSAGE" + action);
			String message = intent.getStringExtra(Utils.EXTRA_MESSAGE);
			String summary = "Receive message from server:\n\t";
			Log.e(Utils.TAG, summary + message);
			JSONObject contentJson = null;
			String contentStr = message;
			try {
				contentJson = new JSONObject(message);
				contentStr = contentJson.toString(4);
			} catch (JSONException e) {
				Log.d(Utils.TAG, "Parse message json exception.");
			}
			summary += contentStr;
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(summary);
			builder.setCancelable(true);
			Dialog dialog = builder.create();
			dialog.setCanceledOnTouchOutside(true);
			dialog.show();
		} else {
			System.out.println("Activity normally start!");
			Log.i(Utils.TAG, "Activity normally start!");
		}
	}

	private void showChannelIds() {
		System.out.println("进入showChannelIds");
		String appId = null;
		String channelId = null;
		String clientId = null;

		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		appId = sp.getString("appid", "");
		channelId = sp.getString("channel_id", "");
		clientId = sp.getString("user_id", "");

		Resources resource = this.getResources();
		String pkgName = this.getPackageName();

		infoText = (TextView) findViewById(resource.getIdentifier("news", "id",
				pkgName));

		String content = "\tApp ID: " + appId + "\n\tChannel ID: " + channelId
				+ "\n\tUser ID: " + clientId + "\n\t";

		System.out.println("-------------------------------" + appId);
		System.out.println("-------------------------------" + channelId);
		System.out.println("-------------------------------" + clientId);
		if (infoText != null) {
			infoText.setText(content);
			infoText.invalidate();
		}
	}

	// private List<String> getTagsList(String originalText) {
	// System.out.println("进入getTagsList");
	//
	// List<String> tags = new ArrayList<String>();
	// int indexOfComma = originalText.indexOf(',');
	// String tag;
	// while (indexOfComma != -1) {
	// tag = originalText.substring(0, indexOfComma);
	// tags.add(tag);
	//
	// originalText = originalText.substring(indexOfComma + 1);
	// indexOfComma = originalText.indexOf(',');
	// }
	//
	// tags.add(originalText);
	// return tags;
	// }
}
