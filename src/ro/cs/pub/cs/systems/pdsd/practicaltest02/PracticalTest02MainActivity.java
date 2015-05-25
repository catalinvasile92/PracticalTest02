package ro.cs.pub.cs.systems.pdsd.practicaltest02;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;

import android.app.Activity;
import android.os.Bundle;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

public class PracticalTest02MainActivity extends Activity {
	EditText serverPortEditText = null;
	private ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
	
	public class CommunicationThread extends Thread {
		private ServerThread serverThread;
		private Socket       socket;
		
		public CommunicationThread(ServerThread serverThread, Socket socket) {
			this.serverThread = serverThread;
			this.socket       = socket;
		}
		
		@Override
		public void run() {
			if (socket != null) {
				try {
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
					
					if (bufferedReader != null && printWriter != null) {
						String cmd            = bufferedReader.readLine();
						if (cmd.startsWith("set")) {
							String[] tokens = cmd.split(",");
							serverThread.setData("" + socket.getInetAddress(), tokens[1] + "," + tokens[2]);
						}
						if (cmd.startsWith("reset")) {
							serverThread.setData("" + socket.getInetAddress(), "");
						}
						if (cmd.startsWith("poll")) {
							if (serverThread.data.containsKey("" + socket.getInetAddress())) {
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public class ServerThread extends Thread {
		private ServerSocket serverSocket = null;
		public HashMap<String, String> data = null;
	
		public ServerThread(int port) {
			try {
				this.serverSocket = new ServerSocket(port);
			} catch (IOException ioException) {
				Toast.makeText(getApplicationContext(), "An exception has occurred: " + ioException.getMessage(), Toast.LENGTH_SHORT).show();
				ioException.printStackTrace();
			}
			this.data = new HashMap<String, String>();
		}
		
		@Override
		public void run() {
			try {		
				while (!Thread.currentThread().isInterrupted()) {
					Socket socket = serverSocket.accept();
					
					CommunicationThread communicationThread = new CommunicationThread(this, socket);
					communicationThread.start();
				}			
			} catch (ClientProtocolException clientProtocolException) {
				clientProtocolException.printStackTrace();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
		
		public synchronized void setData(String socketInetAdd, String alarmTime) {
			this.data.put(socketInetAdd, alarmTime);
		}
		
		public void stopThread() {
			if (serverSocket != null) {
				interrupt();
				try {
					serverSocket.close();
				} catch (IOException ioException) {
					ioException.printStackTrace();		
				}
			}
		}
	}
	
	private class ConnectButtonClickListener implements OnClickListener {
		
		@Override
		public void onClick(View view) {
			String serverPort = serverPortEditText.getText().toString();
			if (serverPort == null || serverPort.isEmpty()) {
				Toast.makeText(
					getApplicationContext(),
					"Server port should be filled!",
					Toast.LENGTH_SHORT
				).show();
				return;
			}
			
			
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_practical_test02_main);
		serverPortEditText = (EditText)findViewById(R.id.editText2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.practical_test02_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
