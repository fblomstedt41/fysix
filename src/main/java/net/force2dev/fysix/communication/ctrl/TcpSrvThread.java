package net.force2dev.fysix.communication.ctrl;

import java.net.*;
import java.io.*;
import java.util.Vector;

import net.force2dev.fysix.communication.MessageHandler;

public class TcpSrvThread extends Thread {
		
	private Socket clientSocket;	
	private Vector clientVector;
	private byte inBuf[]         = new byte[1024];
	private InputStream inStr    = null;
	private OutputStream outStr  = null;
	private MessageHandler m_msgHandler = null;
  
	public TcpSrvThread(Socket cs, Vector cv, MessageHandler mh){
		clientSocket = cs;
		clientVector = cv;
		m_msgHandler = mh;
	}
	
	synchronized public void sendData(byte buf[], int size) throws IOException {		
		outStr.write(buf, 0, size);
	}
	
	public void run() {
		boolean connected = true;
		
		try{
		  inStr  = clientSocket.getInputStream();
		  outStr = clientSocket.getOutputStream();
	  
		  System.out.println("Client added.");
		
		  // outStr.write(connected.getBytes());
		  
		  while(connected){
			  // Read data...
			  int res  = inStr.read(inBuf);

			  System.out.println("Receive: " + res);
			  // Send data...
			  m_msgHandler.receiveData(inBuf, res, null);

			  // Send data to all other clients
			  /*
			  for(int i=0;i<clientVector.size();i++){
			  	if(clientVector.get(i) != this){
			  		TcpSrvThread st = (TcpSrvThread)clientVector.get(i);
			  		st.sendData(inBuf, res);
			  	}
			  }
			  */
		  }
		}
		catch (IOException ioe) {
			System.out.println("Client error, disconnecting client.");
			connected = false;
		}
		finally {
		  System.out.println("Server client handler thread inactive!");
		
		  // Clean up!
		  connected = false;
		  inStr = null;
		  outStr = null;
		  clientVector.remove(this);
		  // GC will handle it!
		}
	}
}
