package net.force2dev.fysix.communication;

public abstract class CommunicationHandling {
	
	protected MessageHandler m_msgHandler;
	
	public void setMessageHandler(MessageHandler msgHandler)
	{
		m_msgHandler = msgHandler;
	}
	
	public abstract void sendData(byte data[], int size);
}
