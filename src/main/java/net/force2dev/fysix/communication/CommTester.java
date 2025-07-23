package net.force2dev.fysix.communication;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;

import net.force2dev.fysix.communication.CommunicationHandling;
import net.force2dev.fysix.communication.MessageHandler;
import net.force2dev.fysix.communication.ctrl.TcpCntHandler;
import net.force2dev.fysix.communication.ctrl.TcpSrvHandler;
import net.force2dev.fysix.communication.data.MulticastUDPHandler;
import net.force2dev.fysix.communication.msg.help.MessageData;
import net.force2dev.fysix.communication.msg.ChatMessage;

public class CommTester extends MessageHandler implements KeyListener
{
	private CommunicationHandling tcpHandler = null;
	
	public static void main(String argv[])
	{  		
		CommTester owner = new CommTester();
		
		// AddKeyListener..
		
		System.out.println("Argv.length = " + argv.length + " , "  + argv[0]);
		
		if(argv.length == 1){
			if(argv[0].equalsIgnoreCase("server")){
				System.out.println("SERVER");
				owner.tcpHandler = new TcpSrvHandler(1235);
				((TcpSrvHandler)owner.tcpHandler).start();
			}
			else{
				System.out.println("CLIENT");
				owner.tcpHandler = new TcpCntHandler();
				((TcpCntHandler)owner.tcpHandler).start("127.0.0.1", 1235);
			}
		}
		
		owner.tcpHandler.setMessageHandler(owner);
		
		MulticastUDPHandler muh = null;
		
		try {
			boolean quit = false;
			//			byte key[] = new byte[1];
			while(!quit)
			{
				ChatMessage msgT = new ChatMessage();
				byte msgBuf[] = msgT.build();
				System.out.println("Press to close...");
				int i = System.in.read();
				switch(i)
				{
				case 's':
				case 'S':
					for(int d=0;d<1;d++){
						for(int n=0;n<50;n++){							
							owner.tcpHandler.sendData(msgBuf, msgBuf.length);
						}
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e1) { 
							e1.printStackTrace();
						}
					}
					break;
				case 'm':
				case 'M':
					muh = new MulticastUDPHandler();
					muh.setMessageHandler(owner);
					muh.start("228.5.6.7", 4322);
					break;
				case 'n':
				case 'N':
					muh.sendData(msgBuf, msgBuf.length);
					break;
				case 'q':
				case 'Q':
					quit = true;
					break;
				default:break;
				}
				i = System.in.read();
				i = System.in.read();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Quit: Close all!");
		
		if(owner.tcpHandler instanceof TcpSrvHandler){
			((TcpSrvHandler)owner.tcpHandler).stop();
		}
		if(owner.tcpHandler instanceof TcpCntHandler){
			((TcpCntHandler)owner.tcpHandler).stop();
		}
		
		System.exit(0);
	}
	
	public synchronized void receiveData(byte[] data, int size, CommunicationHandling receiver) {
		int recSize = 0;
		
		System.out.println("receiveData : " + receiver);
		
		if(receiver instanceof MulticastUDPHandler){
			System.out.println("Multicast group");
		}
		
		MessageData md = new MessageData(data, size, MessageData.BIG_ENDIAN);
		while(size>0 && recSize<size){			
			String str = md.getString(0, 11); // TODO: Fix const!
			
			System.out.println("Received [" + recSize + "]: " + str + " , size = " + size);
			
			recSize += 11; // TODO: Fix const!
		}
		
/*		if(this.tcpHandler instanceof TcpSrvHandler)
		{
			this.tcpHandler.sendData(data, size);
		}
		*/
	}
	
	public void keyPressed(KeyEvent ke) {
	}
	
	public void keyReleased(KeyEvent ke) {
	}
	
	public void keyTyped(KeyEvent ke) {
		System.out.println("a");
		if(ke.getKeyChar() == 'a')
		{
			System.out.println("a");
		}
	}
}
