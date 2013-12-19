package Thread;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MsgThread extends Thread {
	private String msg = null;

	public MsgThread(String msg) {
		this.msg = msg;
		start();
	}

	public void run() {
		try {
			System.out.println("lalalalalaal msg is" + msg);
			URL url = new URL(
					"http://192.168.168.26:8080/pushdemo/message/Message.action?type="
							+ msg);//new URL("http://localhost:port/工程名/包名/action名")
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setDoInput(true);
			con.setDoOutput(true);
			con.setRequestMethod("GET");
			con.setConnectTimeout(5000);
			con.connect();
			InputStream in = con.getInputStream();
			OutputStream out = con.getOutputStream();
			out.close();
			in.close();
		} catch (Exception e)
		{
			System.out.println("发送失败");
		}
	}

}
