package net.force2dev.fysix.communication.msg;

import net.force2dev.fysix.communication.msg.help.MessageData;

public class ChatMessage extends AbstractMessage
{
	//public int a      = 14;
	//public int b      = 41;
	public String str = "CODEMACHINE";
	
	public byte[] build() {
		MessageData msg = new MessageData(str.length(), MessageData.BIG_ENDIAN);
		
		//msg.add(a);
		//msg.add(b);		
		msg.add(str.getBytes());

		return msg.getMessage();
	}
	
	public void parse(byte[] data) {
		MessageData msg = new MessageData(data, MessageData.BIG_ENDIAN);
		
		//a   = (int)msg.getInt();
		//b   = (int)msg.getInt();
		str = msg.getString(0,0); // Rest
	}
}
