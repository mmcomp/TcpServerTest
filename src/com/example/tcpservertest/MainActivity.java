package com.example.tcpservertest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;


@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity {
	private ServerSocket serverSocket;
	public static int clientCount = 100;
	public static ClientClass[] clientIps = new ClientClass[clientCount]; 
	public static String[] clientCode = new String[clientCount];
	public static int currentReserve = 1;
	public static int callReserve = 1;
	public static String str = "";
	public static String serverId = "100";
	public static int selectedClientId = -1;
	public static String updateSequence = "0#http://192.168.1.2/a.mp4#3|1#http://192.168.1.2/b.mp4#3|2#http://192.168.1.2/c.mp4#3";
	Socket socket;
	Handler updateConversationHandler;
	Thread serverThread = null;
	public static final int SERVERPORT = 6000;
	public static LinearLayout lin1;
	public static 
	Button settBtn;
	Button callBtn;
	WebView mywebview;

    /*public static String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
        "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
        "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
        "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
        "Android", "iPhone", "WindowsMobile" };*/
    public static ListView listview;
    public static ArrayList<String> list;
	
	class StableArrayAdapter extends ArrayAdapter<String> {

	    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

	    public StableArrayAdapter(Context context, int textViewResourceId,
	        List<String> objects) {
	      super(context, textViewResourceId, objects);
	      for (int i = 0; i < objects.size(); ++i) {
	        mIdMap.put(objects.get(i), i);
	      }
	    }

	    @Override
	    public long getItemId(int position) {
	      String item = getItem(position);
	      return mIdMap.get(item);
	    }

	    @Override
	    public boolean hasStableIds() {
	      return true;
	    }

	  }
    
    
	void createListView()
	{

	    /*
	    final StableArrayAdapter adapter = new StableArrayAdapter(this,
	        android.R.layout.simple_list_item_1, list);
	    listview.setAdapter(adapter);
	    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

	      @Override
	      public void onItemClick(AdapterView<?> parent, final View view,
	          int position, long id) {
	        final String item = (String) parent.getItemAtPosition(position);
	        Toast.makeText(getApplicationContext(), item, Toast.LENGTH_LONG).show();
            adapter.notifyDataSetChanged();
	      }
	    });
	    */
	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateConversationHandler = new Handler();
		setContentView(R.layout.activity_main);
		mywebview = (WebView) findViewById(R.id.webview1);
		WebSettings webSettings = mywebview.getSettings();
		//Intent inten = getIntent();
		//int kala_miniGroup_id = inten.getIntExtra("kala_miniGroup_id", -1);
		mywebview.addJavascriptInterface(new ServerClass(this), "server");
		webSettings.setJavaScriptEnabled(true);
		mywebview.loadUrl("file:///android_asset/html/server.html");
		if(android.os.Build.VERSION.SDK_INT==Build.VERSION_CODES.JELLY_BEAN)
			fixPro();

		/*
	    listview = (ListView) findViewById(R.id.listview);
	    list = new ArrayList<String>();
	    */
	    /*
	    for (int i = 0; i < values.length; ++i)
		      list.add(values[i]);
		*/
		//createListView();
		/*
		settBtn = (Button)findViewById(R.id.reserve);
		settBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText ed1 = (EditText) findViewById(R.id.resNum);
				String string = ed1.getText().toString();
				Toast.makeText(getApplicationContext(), "reserve ready "+string, Toast.LENGTH_SHORT).show();
				currentReserve = Integer.parseInt(string);
			}
		});
		callBtn = (Button)findViewById(R.id.call);
		callBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				EditText ed1 = (EditText) findViewById(R.id.resNum);
				String string = ed1.getText().toString();
				int ClientIndex = ClientClass.getIpByReserve(Integer.parseInt(string), clientIps);
				Toast.makeText(getApplicationContext(), "call "+string+" client index "+ClientIndex+" ip:"+clientIps[ClientIndex].ip, Toast.LENGTH_SHORT).show();
				callReserve = Integer.valueOf(string);
				callCommand(clientIps[ClientIndex].ip);
			}
		});
		Button updateBtn = (Button)findViewById(R.id.update);
		updateBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				updateCommand("192.168.1.7");
				
			}
		});
		Button logButton = (Button) findViewById(R.id.log);
		logButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
			}
		});
		*/
		this.serverThread = new Thread(new ServerThread());
		this.serverThread.start();
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void fixPro()
	{
		mywebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
		mywebview.getSettings().setAllowFileAccessFromFileURLs(true);
	}
	
	void sendCommand(String comm,String sIp)
	{
		ClientThread ct = new ClientThread();
		ct.commandToSend = comm;
		ct.SERVER_IP = sIp;
		new Thread(ct).start();
	}

	
	protected void onStop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	class ClientThread implements Runnable {
		public String commandToSend = "";
		public String SERVER_IP = "";
		public int SERVER_PORT = 6000;
		@Override
		public void run() {
			str += "runing \n";
			byte[] buffer = new byte[1024];
			InetAddress serverAddr;
			str += "Ip = "+SERVER_IP+":"+SERVER_PORT+"\n";
			try {
				serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVER_PORT);
				DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
				DataInputStream infromServer = new DataInputStream(socket.getInputStream());
				str += "....\n";
				outToServer.writeChars(commandToSend+"\n");
				str += "written to client\n";
				int bytes = infromServer.read(buffer);
				String infromServerStr = new String(buffer, 0, bytes);
				str += commandToSend+" -> "+infromServerStr+"\n";
				str += "read from client\n";
				socket.close();
			} catch (UnknownHostException e) {
				int clientIndex = ClientClass.ipExists(SERVER_IP, clientIps);
				clientIps[clientIndex].isOn = false;
				str += "err : "+SERVER_IP+"\n";
			} catch (IOException e) {
				int clientIndex = ClientClass.ipExists(SERVER_IP, clientIps);
				clientIps[clientIndex].isOn = false;
				str += "err : "+SERVER_IP+"\n";
			}catch (Exception e) {
				int clientIndex = ClientClass.ipExists(SERVER_IP, clientIps);
				clientIps[clientIndex].isOn = false;
				str += "err : "+SERVER_IP+"\n";
			}
		}
	}

	class ServerThread implements Runnable {

		public void run() {
			Socket socket = null;
			try {
				serverSocket = new ServerSocket(SERVERPORT);
			} catch (IOException e) {
				e.printStackTrace();
			}
			while (!Thread.currentThread().isInterrupted()) {

				try {

					socket = serverSocket.accept();

					CommunicationThread commThread = new CommunicationThread(socket);
					new Thread(commThread).start();

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	class CommunicationThread implements Runnable {

		private Socket clientSocket;

		private BufferedReader input;

		public CommunicationThread(Socket clientSocket) {

			this.clientSocket = clientSocket;

			try {

				this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {

			while (!Thread.currentThread().isInterrupted()) {

				try {
//					String output = "NaN";
					String read = input.readLine();
					String cIp = clientSocket.getInetAddress().toString().replace("/", "");
					DataOutputStream outToClient = new DataOutputStream(clientSocket.getOutputStream());
					int clientIndex = ClientClass.ipExists(cIp, clientIps);					
					if(read!=null)
					{
						CommandClass cc = new CommandClass(read);
//						output = "isValid = "+String.valueOf(cc.isValid)+"\n";
						if(cc.isValid)
						{
							//SERVER_IP = cIp;
							Calendar cl = Calendar.getInstance();
//							output+="command = "+String.valueOf(cc.command)+"["+read+"]\n";
//							output += "err="+cc.data+"\n";
							str += "command = "+String.valueOf(cc.command)+"["+read+"]\n";
							switch (cc.command) {
								case 7:
									if(clientIndex < 0)
									{
										int tmpId = ClientClass.getId(cIp, clientIps);
										String dt = String.valueOf(cl.get(Calendar.YEAR))+"-"+String.valueOf(cl.get(Calendar.MONTH)+1)+"-"+String.valueOf(cl.get(Calendar.DAY_OF_MONTH))+" "+String.valueOf(cl.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(cl.get(Calendar.MINUTE))+":"+String.valueOf(cl.get(Calendar.SECOND));
										clientIps[tmpId]  = new ClientClass(tmpId, cIp, dt, true, 0);
										read = "added";
										str += read+" "+cIp+"\n";
										outToClient.writeChars(read);
									}
									else
									{
										clientIps[clientIndex].setOn();
										read = "setOn";
										outToClient.writeChars(read);
									}
									break;
								case 8:
									String dt = String.valueOf(cl.get(Calendar.YEAR))+"-"+String.valueOf(cl.get(Calendar.MONTH)+1)+"-"+String.valueOf(cl.get(Calendar.DAY_OF_MONTH))+" "+String.valueOf(cl.get(Calendar.HOUR_OF_DAY))+":"+String.valueOf(cl.get(Calendar.MINUTE))+":"+String.valueOf(cl.get(Calendar.SECOND));
									read = "dateTime";
									outToClient.writeChars(dt);
									break;
								case 9:
									if(clientIndex >= 0)
									{
										if(currentReserve>0)
										{
											read = "Reserve";
											clientIps[clientIndex].setReserve(currentReserve);
										}
										outToClient.writeChars(String.valueOf(currentReserve));
										currentReserve = 0;
									}
									else
									{
										read = "reserve not in list";
										outToClient.writeChars("-1");
									}
									break;
								case 10:
									if(clientIndex >= 0)
									{
										read = "low";
										outToClient.writeChars("0");
									}
									else
									{
										outToClient.writeChars("-1");
										read = "low not in list";
									}
									break;
							}
						}
						else						
							outToClient.writeChars("0,0");
					}
					updateConversationHandler.post(new updateUIThread(read,cIp));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	class updateUIThread implements Runnable {
		private String msg;
		private String cIp;
		public updateUIThread(String str,String cIpp) {
			this.msg = str;
			this.cIp = cIpp;
		}

		@Override
		public void run() {
			
			if(msg!=null)
			{
				//Toast.makeText(getApplicationContext(), cIp+":"+msg, Toast.LENGTH_SHORT).show();
				str+=cIp+":'"+msg+"'\n";
				if(msg=="added")
				{
					int clientIndex = ClientClass.ipExists(cIp, clientIps);
					//Toast.makeText(getApplicationContext(), "added "+cIp, Toast.LENGTH_SHORT).show();
		            mywebview.loadUrl("javascript:addClient("+clientIndex+",'"+cIp+"');");
				}
				else if(msg == "Reserve")
				{
					int clientIndex = ClientClass.ipExists(cIp, clientIps);
					//Toast.makeText(getApplicationContext(), "reserve "+currentReserve, Toast.LENGTH_SHORT).show();
		            //list.add(String.valueOf(clientIps[clientIndex].reserveNumber));
		            //createListView();
		            mywebview.loadUrl("javascript:addReserve("+clientIndex+","+clientIps[clientIndex].reserveNumber+");");
				}
			}
		}
	}
	void setCommand(String cIp)
	{
		try {
			int clientIndex = ClientClass.ipExists(cIp, clientIps);
			if(clientIndex>=0)
			{
				clientIps[clientIndex].reserveNumber = currentReserve;
				String comm= serverId+",1,"+String.valueOf(currentReserve);
				sendCommand(comm, cIp);
				currentReserve = 0;
			}
			else if(clientIndex<0)
				Toast.makeText(getApplicationContext(), "Not a client", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	void callCommand(String cIp)
	{
		try {
			int clientIndex = ClientClass.ipExists(cIp, clientIps);
			if(clientIndex>=0 && clientIps[clientIndex].reserveNumber==callReserve && callReserve>0)
			{
				String comm= serverId+",2,"+String.valueOf(callReserve);
				sendCommand(comm, cIp);
				callReserve = 0;
			}
			else if(clientIndex<0)
				Toast.makeText(getApplicationContext(), "Not a client", Toast.LENGTH_SHORT).show();
			else if(clientIps[clientIndex].reserveNumber!=callReserve)
				Toast.makeText(getApplicationContext(), "Not a in this reserve", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	void resetCommand(String cIp)
	{
		try {
			int clientIndex = ClientClass.ipExists(cIp, clientIps);
			if(clientIndex>=0)
			{
				clientIps[clientIndex].reserveNumber = 0;
				String comm= serverId+",3";
				sendCommand(comm, cIp);
			}
			else if(clientIndex<0)
				Toast.makeText(getApplicationContext(), "Not a client", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
	}
	void updateCommand(String cIp)
	{
		/*
		cIp = cIp.trim();
		try {
			int clientIndex = ClientClass.ipExists(cIp, clientIps);
			if(clientIndex>=0)
			{
				clientIps[clientIndex].reserveNumber = 0;
				String comm= serverId+",4,"+updateSequence;
				Toast.makeText(getApplicationContext(), "update command to "+cIp+"\n"+comm, Toast.LENGTH_SHORT).show();
				sendCommand(comm, cIp);
			}
			else if(clientIndex<0)
				Toast.makeText(getApplicationContext(), "Not a client", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		*/
		try {
			String comm= serverId+",4,"+updateSequence;
			sendCommand(comm, cIp);
			callReserve = 0;
		} catch (Exception e) {
			Toast.makeText(getApplicationContext(), "error:"+e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}
	
}


