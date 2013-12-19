package Thread;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.baidu.push.example.Consts;

import android.os.Bundle;
import android.os.Message;


public class LoginThread extends Thread{
	
	private String userName = null;
	private String passWord = null;
	private String ChannelID=null;

	public LoginThread(java.lang.String userName,java.lang.String passWord,String ChannelID){
		this.userName=userName;
		this.passWord=passWord;
		this.ChannelID=ChannelID;
		start();
	}

	public void run() {
		try{
		URL url = new URL(
				"http://202.43.146.120:8080/baby_server/CheckUserServlet?username="+ userName + "&password=" + passWord+"&ChannelID"+ChannelID);
		HttpURLConnection con = (HttpURLConnection) url
				.openConnection();
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setRequestMethod("GET");
		con.setConnectTimeout(5000);
		BufferedInputStream in = new BufferedInputStream(con
				.getInputStream());
		final byte[] back = new byte[2048];
		in.read(back);
		final String result = new String(back, "GBK").trim();// GBK解码只能解byte型的
		Message message = new Message();
		Bundle bundle = new Bundle();
		bundle.putString("result",result);
		message.setData(bundle);
		Consts.loginHandler.sendMessage(message);
		System.out.println("resultthread:----------------------------------" + result);
		}catch(Exception e){
			System.out.println("==========================");
		}
	}
	
	
}

