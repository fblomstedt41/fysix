package net.force2dev.fysix.communication.data;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import net.force2dev.fysix.communication.CommunicationHandling;

public class MulticastUDPHandler extends CommunicationHandling implements Runnable 
{
	private MulticastSocket m_ms = null;
	private byte inBuf[]         = new byte[1024]; // Size???
	private Thread m_thread      = null;
	private boolean m_connected  = false;	
	private int m_port           = 1234;
	private InetAddress m_group  = null;
	
	public MulticastUDPHandler(){		
	}
	
	public void sendData(byte data[], int size){
		System.out.println("MCAST: sendData port : " + m_port + " , size : " + size);
		try {
			if(m_connected && m_ms != null)
			{
				//					outStr.write(data, 0, size);
				DatagramPacket sndMsg = new DatagramPacket(data, size, m_group, m_port);
				m_ms.send(sndMsg);
			}
		}
		catch (IOException ioe){
			System.out.println("Error while sending.");
		}
	}
	
	public void start(String group, int port) {
		try {
			m_group = InetAddress.getByName(group);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			m_group = null;
			return;			
		}
		m_port  = port;
		
		m_thread = new Thread(this);
		m_thread.start();
	}
	
	public void stop()
	{
		if(m_thread != null){
			m_thread.stop();
		}
		disconnect();
		m_connected = false;
	}
	
	public void run() {
		System.out.println("Running...");
		while(true){ // Loop...
			if(!m_connected){
				connect();
			}
			else{
				try {
					
					// Read data...
					//						int res = inStr.read(inBuf);
					DatagramPacket recMsg = new DatagramPacket(inBuf, inBuf.length);
					m_ms.receive(recMsg);
					System.out.println("Receive GROUP msg : " + recMsg.getLength());
					
					// Notify handler with received data...
					m_msgHandler.receiveData(recMsg.getData(), recMsg.getLength(), this);
					
				} catch (IOException e) {
					//
					disconnect();
				}
			}
		}
	}
	
	private void connect()
	{
		System.out.println("MultiCAST : Try to connect to server " + m_group + " on port " + m_port);
		try{
			m_ms = new MulticastSocket(m_port);
			m_ms.joinGroup(m_group);
			
			System.out.println(m_group + " on port " + m_port + " is up and running.");
			
			m_connected = true;
		}
		catch(IOException ioe){
			System.out.println("Error, " + m_group + " on port " + m_port + " is not available.");		 	
		}
	}
	
	private void disconnect()
	{
		m_connected = false;
		try {
			m_ms.leaveGroup(m_group);
		} catch (IOException e) {
			// 
		}
		if(m_ms != null){
			m_ms.close();
			m_ms = null;
		}
	}
}
