package com.example.tcpservertest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class ServerClass {
	Context mContext;
	String ajaxout="";
	String javaScriptFn = "";
    String javaScriptFailFn = "";
    Boolean ajaxReady = true;

	public ServerClass(Context cont) {
		mContext = cont;
	}
	
	@JavascriptInterface
	public void setReserve(String reserveNum)
	{
		MainActivity.currentReserve = Integer.parseInt(reserveNum);
	}
	
	@JavascriptInterface
	public void toast(String msg)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public void toast(String msg , String LongShow)
	{
		Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
	}
	
	@JavascriptInterface
	public void call(String clientIndex)
	{
		MainActivity m = (MainActivity)mContext;
		MainActivity.callReserve = MainActivity.clientIps[Integer.parseInt(clientIndex)].reserveNumber;
		m.callCommand(MainActivity.clientIps[Integer.parseInt(clientIndex)].ip);
	}
	
	
	@JavascriptInterface
	public void reset(String clientIndex)
	{
		MainActivity m = (MainActivity)mContext;
		MainActivity.callReserve = MainActivity.clientIps[Integer.parseInt(clientIndex)].reserveNumber;
		m.resetCommand(MainActivity.clientIps[Integer.parseInt(clientIndex)].ip);
	}

	@JavascriptInterface
	public void update(String cIp)
	{
		MainActivity m = (MainActivity)mContext;
		m.updateCommand(cIp);
		//m.callCommand(cIp);
	}
	
	@JavascriptInterface
	public String showLog()
	{
		return MainActivity.str;
	}
	//-------------------------AJAX--------------------------------------------------
    @JavascriptInterface
    public String getAjax(String url,String jscript)
    {
    	String out = "false";
    	final ajax aj = new ajax(mContext);
    	String[] tmp = jscript.split(",");
    	javaScriptFn = tmp[0];
    	if(tmp.length>1)
    		javaScriptFailFn = tmp[1];
    	aj.execute(url);
    	out = "true";
    	return out;
    }
    @JavascriptInterface
    public String ajaxResponse()
    {
    	if(ajaxReady)
    		return ajaxout;
    	else
    		return "NaN";
    }
	private class ajax extends AsyncTask<String, Integer, String> {

	    private Context context;
	    public ajax(Context context) {
	        this.context = context;
	    }

	    @SuppressLint({ "SdCardPath", "Wakelock" })
		@Override
	    protected String doInBackground(String... sUrl) {
	    	String out = "";
	        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
	        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
	             getClass().getName());
	        wl.acquire();
	        try {
	        	ajaxout = "";
	            InputStream input = null;
	            HttpURLConnection connection = null;
	            try {
	                URL url = new URL(sUrl[0]);
	                connection = (HttpURLConnection) url.openConnection();
	                connection.connect();
	                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
	                {
	                     out = "Server returned HTTP " + connection.getResponseCode() 
	                         + " " + connection.getResponseMessage();
	                     return out;
	                }
	                input = connection.getInputStream();
	                BufferedReader in = new BufferedReader(new InputStreamReader(input));
	                String line = null;
	                while ((line = in.readLine()) != null) {
	                	ajaxout += line;
	                }
	                in.close();
	            } catch (Exception e) {
	                return e.toString();
	            } finally {
	                try {
	                    if (input != null)
	                        input.close();
	                } 
	                catch (IOException ignored) { }

	                if (connection != null)
	                    connection.disconnect();
	            }
	        } finally {
	            wl.release();
	        }
	        return null;
	    }
	    @Override
	    protected void onPostExecute(String result) {
			MainActivity ma = (MainActivity) mContext;
	        if (result == null)
	        {
				try {
					ajaxReady = true;
					if(javaScriptFn != "")
						ma.mywebview.loadUrl("javascript:"+javaScriptFn+"('"+ajaxout+"');");
				} catch (Exception e) {
					if(javaScriptFailFn != "")
						ma.mywebview.loadUrl("javascript:"+javaScriptFailFn+"('"+result+"');");
				}
	        }
	        else
	        	if(javaScriptFailFn != "")
	        		ma.mywebview.loadUrl("javascript:"+javaScriptFailFn+"('"+result+"');");
	    }
	}

	
}
