package com.example.tcpservertest;


public class ClientClass {
	public int id = 0;
	public String ip = "";
	public String lastSeen;
	public Boolean isOn=false; 
	public int reserveNumber = 0;
	public Boolean isUpdate = false;
	public ClientClass(int inId,String inIp,String dt,Boolean inIsOn,int inReservenumber) {
		id = inId;
		ip = inIp;
		lastSeen = dt;
		isOn = inIsOn;
		reserveNumber = inReservenumber;
	}
	public ClientClass(int inId,String inIp,String dt,Boolean inIsOn,int inReservenumber,Boolean inIsUpdate) {
		id = inId;
		ip = inIp;
		lastSeen = dt;
		isOn = inIsOn;
		reserveNumber = inReservenumber;
		isUpdate = inIsUpdate;
	}
	public static int ipExists(String inIp,ClientClass[] clientsArray)
	{
		int out = -1;
		for(int i = 0;i < clientsArray.length;i++)
			try {
				if(clientsArray[i].ip.equals(inIp))
					out = i;
			} catch (Exception e) {
			}
		return out;
	}
	public static int getId(String inIp,ClientClass[] clientsArray)
	{
		int out = -1;
		int clIndx = ClientClass.ipExists(inIp, clientsArray);
		if(clIndx > -1)
			out = clientsArray[clIndx].id;				
		else
		{
			for(int i = 0;i < clientsArray.length;i++)
				try {
					if(out < clientsArray[i].id)
						out = clientsArray[i].id;
				} catch (Exception e) {
				}
			out++;
		}
		return out;
	}
	public void setReserve(int inReserveNumber)
	{
		reserveNumber = inReserveNumber;
	}
	public void setOn(Boolean inIsOn)
	{
		isOn  = true;
	}
	public void setOn()
	{
		Boolean inIsOn = true;
		setOn(inIsOn);
	}
	public static int getIpByReserve(int reserveNumber,ClientClass[] clientsArray)
	{
		int out = -1;
		for(int i = 0;i < clientsArray.length;i++)
			try {
				if(clientsArray[i].reserveNumber==reserveNumber)
					out = i;
			} catch (Exception e) {
			}
		return out;
	}
}
