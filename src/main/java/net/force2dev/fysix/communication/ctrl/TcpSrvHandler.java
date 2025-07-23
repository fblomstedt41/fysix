package net.force2dev.fysix.communication.ctrl;

import java.net.*;
import java.io.*;
import java.util.Vector;

import net.force2dev.fysix.communication.CommunicationHandling;

public class TcpSrvHandler extends CommunicationHandling implements Runnable {
	private Vector connections = new Vector();
	private ServerSocket srvSocket = null;
	private Thread m_thread;	
	
	public TcpSrvHandler(int port){
		try{
		  srvSocket = new ServerSocket(port);
		}
		catch (IOException ioe){
			System.out.println("Error : Port " + port + " was not available.");
		}
		System.out.println("Server on port " + port + " is up and running.");
	}
	
	public void sendData(byte data[], int size){
		try {
		  for(int i=0;i<connections.size();i++){
		  	  TcpSrvThread st = (TcpSrvThread)connections.get(i);
			  st.sendData(data, size);
		  }
		}
		catch (IOException ioe){
			System.out.println("Error while sending to all clients.");
		}
	}
	
	public void start()
	{
		m_thread = new Thread(this);
		m_thread.start();
	}
	
	public void stop()
	{
		m_thread.stop();
	}
		
	public void run() {
		while(true){ // Loop...
			try{
			  Socket client = srvSocket.accept();
			  TcpSrvThread st = new TcpSrvThread(client, connections, m_msgHandler);
			  st.start();			  
			  connections.add(st);
			  st = null;
			  System.out.println("Nr of clients active = " + connections.size());
		  }
		  catch(IOException ioe){
		  	System.out.println("Could not accept client.");
		  } 
		}
	}
}
