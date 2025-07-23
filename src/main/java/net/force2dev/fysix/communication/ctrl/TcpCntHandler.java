package net.force2dev.fysix.communication.ctrl;

import java.net.*;
import java.io.*;

import net.force2dev.fysix.communication.CommunicationHandling;

public class TcpCntHandler extends CommunicationHandling implements Runnable {
	private Socket clientSocket  = null;
	private InputStream inStr    = null;
	private OutputStream outStr  = null;
	private byte inBuf[]         = new byte[1024]; // Size???
	private Thread m_thread      = null;
	private boolean m_connected  = false;
	private String m_server      = "127.0.0.1";
	private int m_port           = 1234;
	
	public TcpCntHandler(){
	}
	
	public void sendData(byte data[], int size){
		try {
			if(outStr != null)
			{
				outStr.write(data, 0, size);
			}
		}
		catch (IOException ioe){
			System.out.println("Error while sending.");
		}
	}
	
	public void start(String server, int port) {
		m_server = server;
		m_port   = port;
		
		m_thread = new Thread(this);
		m_thread.start();
	}
	
	public void stop()
	{
		if(m_thread != null){
			m_thread.stop();
		}
		if(clientSocket != null){
			try {
				clientSocket.close();
			} catch (IOException e) {
				//	
			}
			clientSocket = null;
		}		
		if(inStr != null){
			try {
				inStr.close();
			} catch (IOException e) {
				//
			}
			inStr = null;
		}
		if(outStr != null){
			try {
				outStr.close();
			} catch (IOException e) {
				//
			}
			outStr = null;
		}
		m_connected = false;
	}
	
	public void run() {
		while(true){ // Loop...
			if(!m_connected){
				connect();
			}
			else{
				try {
					
					// Read data...
					int res = inStr.read(inBuf);
					
					// Notify handler with received data...
					m_msgHandler.receiveData(inBuf, res, this);
					
				} catch (IOException e) {
					//
					disconnect();
				}
			}
		}
	}
	
	private void connect()
	{
		System.out.println("Try to connect to server " + m_server + " on port " + m_port);
		try{
			
			clientSocket = new Socket(m_server, m_port);
			inStr  = clientSocket.getInputStream();
			outStr = clientSocket.getOutputStream();
			
			System.out.println(m_server + " on port " + m_port + " is up and running.");
			
			m_connected = true;
		}
		catch(IOException ioe){
			System.out.println("Error, " + m_server + " on port " + m_port + " is not available.");		 	
		}
	}
	
	private void disconnect()
	{
		m_connected = false;
		if(inStr != null){
			try {
				inStr.close();
			} catch (IOException ex) {
				//
			}
			inStr = null;
		}
		if(outStr != null){
			try {
				outStr.close();
			} catch (IOException ex) {
				//
			}
			outStr = null;
		}
		if(clientSocket != null){
			try {
				clientSocket.close();
			} catch (IOException ex) {
				//	
			}
			clientSocket = null;
		}
	}
}
