package net.force2dev.fysix.communication;

public abstract class MessageHandler
{
  abstract public void receiveData(byte data[], int size, CommunicationHandling receiver);
}
