
package net.force2dev.fysix.communication.msg.help;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author FBT
 */
public class MessageData
{
  public static final int BIG_ENDIAN    = 0;
  public static final int LITTLE_ENDIAN = 1;
  
  private int byteorder = BIG_ENDIAN;

  private byte message[];
  private int bytePos;
  private int messageSize;
      
  public MessageData(int size, int type)
  {
    message = new byte[size];
    messageSize = size;
    bytePos = 0;
    byteorder = type;
  }
  
  public MessageData(byte data[], int type)
  {
    message = data;
    messageSize = data.length;
    bytePos = 0;
    byteorder = type;
  }
  
  public MessageData(byte data[], int size, int type)
  {
    message = data;
    messageSize = size;
    bytePos = 0;
    byteorder = type;
  }

  // 8 bits
  public void add(byte data)
  {
    message[bytePos++] = data;
  }

  // 16 bits
  public void add(short data)
  {        
    byte lb = (byte) data;
    byte hb = (byte) (data >> 8);

    message[bytePos++] = (byteorder == BIG_ENDIAN)?hb:lb;
    message[bytePos++] = (byteorder == BIG_ENDIAN)?lb:hb;
  }

  // 32 bits
  public void add(int data)
  {
    short ls = (short) data;
    short hs = (short) (data >> 16);
    
    add((byteorder == BIG_ENDIAN)?hs:ls);
    add((byteorder == BIG_ENDIAN)?ls:hs);      
  }

  // 64 bits
  public void add(long data)
  {
    ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
    DataOutputStream dataStream = new DataOutputStream(byteArr);
    try{
      dataStream.writeLong(data);
    }
    catch(Exception e){
    }
    
    add(byteArr.toByteArray());
  }

  // 32 bits
  public void add(float data)
  {
    add(Float.floatToIntBits(data));
  }  
  
  // 64 bits
  public void add(double data)
  {
    add(Double.doubleToLongBits(data));
  }
  
  public void add(byte data[])
  {
    for(int i=0;i<data.length;i++){
      add(data[(byteorder == BIG_ENDIAN)?i:data.length-i-1]);
    }
  }
  
  public boolean isReady()
  {
    return (bytePos >= messageSize);
  }
  
  public int getMessageSize()
  {
  	return messageSize;
  }
  
  public byte[] getMessage()
  {
    return message;
  }
  
  public byte getByte()
  {
    return message[bytePos++];
  }
  
  public short getShort()
  {
    short lb = (short)(getByte() & 0xFF);
    short hb = (short)(getByte() & 0xFF); 
    
    if(byteorder == BIG_ENDIAN){
      return (short)(lb << 8 | hb);
    }
    else{
      return (short)(hb << 8 | lb);
    }
  }
  
  public int getInt()
  {
    int ls = (int)(getShort() & 0xFFFF);
    int hs = (int)(getShort() & 0xFFFF);
    
    if(byteorder == BIG_ENDIAN){
      return (int)(ls << 16 | hs);
    }
    else{
      return (int)(hs << 16 | ls);
     }
  }

  public byte[] getByteArray(int offset, int len)
  {
    byte resArr[] = new byte[len];
    
    for(int i=offset;i<offset+len && i<messageSize;i++){
      resArr[(byteorder == BIG_ENDIAN)?i-offset:resArr.length-(i-offset)-1] = getByte();
    }
    
    return resArr;
  }
  
  public long getLong()
  {
    DataInputStream data = new DataInputStream(new ByteArrayInputStream(getByteArray(bytePos,8)));
    long res = 0;
    try{
      res = data.readLong();
    }catch(Exception e){
    }
    return res;
  }
  
  public float getFloat()
  {    
    return Float.intBitsToFloat(getInt());
  }
  
  public double getDouble()
  {
    DataInputStream data = new DataInputStream(new ByteArrayInputStream(getByteArray(bytePos,8)));
    double res = 0.0;
    try{
      res = data.readDouble();
    }catch(Exception e){
    }
    return res;
  }
  
  public String getString(int offset, int len)
  {
  	int bufLen = (len == 0)?messageSize:len;
  	
  	return new String(getByteArray(offset, bufLen));
  }
  
  public void reset()
  {
    bytePos = 0;
  }
}
