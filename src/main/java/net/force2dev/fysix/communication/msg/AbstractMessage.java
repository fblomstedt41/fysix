package net.force2dev.fysix.communication.msg;

public abstract class AbstractMessage
{
	public abstract byte[] build();
	public abstract void parse(byte[] data);
}
