package Models;

public class Encoder
{
    private byte[] bytes;
    private byte[] messageBytes;
    private String message;
    
    private final byte[] bit = {
            (byte) 0b0000_0001,
            (byte) 0b0000_0010,
            (byte) 0b0000_0100,
            (byte) 0b0000_1000,
            (byte) 0b0001_0000,
            (byte) 0b0010_0000,
            (byte) 0b0100_0000,
            (byte) 0b1000_0000};
    private final byte[] nbit = {
            (byte) 0b1111_1110,
            (byte) 0b1111_1101,
            (byte) 0b1111_1011,
            (byte) 0b1111_0111,
            (byte) 0b1110_1111,
            (byte) 0b1101_1111,
            (byte) 0b1011_1111,
            (byte) 0b0111_1111};
    
    public Encoder()
    {
        bytes = null;
        messageBytes = null;
        message = null;
    }
    
    public Encoder(byte[] bytes, String message)
    {
        messageBytes = null;
        setBytes(bytes);
        setMessage(message);
    }
    
    public void setMessage(String message) throws NullPointerException
    {
        if (message != null)
        {
            this.message = message;
            messageBytes = message.getBytes();
        }
        else
        {
            System.out.println("Message can't be null");
            throw new NullPointerException();
        }
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public void setBytes(byte[] bytes) throws NullPointerException
    {
        if (bytes != null)
        {
            this.bytes = bytes;
        }
        else
        {
            System.out.println("Not a valid array of bytes");
            throw new NullPointerException();
        }
    }
    
    public byte[] getBytes()
    {
        return bytes;
    }
    
    public void setMessageBytes(byte[] messageBytes) throws NullPointerException
    {
        if(messageBytes != null)
        {
            this.messageBytes = messageBytes;
            message = new String(messageBytes);
        }
        else
        {
            System.out.println("Bytes cant be null");
            throw new NullPointerException();
        }
    }
    
    public byte[] getMessageBytes()
    {
        return messageBytes;
    }
    
    public boolean embedMessage(int nrOfBitsToUse)
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            return false;
        }
        
        if (messageBytes == null)
        {
            System.out.println("Please set the message to be embedded");
            return false;
        }
        
        if (nrOfBitsToUse < 1 || nrOfBitsToUse > 8)
        {
            System.out.println("Number of bits to use must be between 1 and 8");
            return false;
        }
        
        String tmpmessage = "(encoded)" + String.format("%5s", message.length()).replace(' ', '0') + message;
        
        byte[] messageBytestmp = tmpmessage.getBytes();
        
        if (bytes.length * nrOfBitsToUse < messageBytestmp.length * 8)
        {
            System.out.println("Message too long. Increase the number of bits or shorten the message.");
            return false;
        }
        
        int currentBit = nrOfBitsToUse - 1;
        int remainingBits;
        int index = 0;
        
        next:
        for (byte i : messageBytestmp)
        {
            remainingBits = 7;
            while (remainingBits >= 0)
            {
                while (currentBit >= 0)
                {
                    if (remainingBits == -1)
                    {
                        continue next;
                    }
                    
                    if ((i & bit[remainingBits]) == 0)
                    {
                        bytes[index] = (byte) (bytes[index] & nbit[currentBit]);
                    }
                    else
                    {
                        bytes[index] = (byte) (bytes[index] | bit[currentBit]);
                    }
                    
                    currentBit--;
                    remainingBits--;
                }
                
                index++;
                currentBit = nrOfBitsToUse - 1;
            }
        }
        
        return true;
    }
    
    public boolean recoverMessage(int nrOfBitsUsed)
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            return false;
        }
        
        if (nrOfBitsUsed < 1 || nrOfBitsUsed > 8)
        {
            System.out.println("Number of bits used must be between 1 and 8");
            return false;
        }
        
        int len = checkForMessage(nrOfBitsUsed);
        if (len < 0)
        {
            message = null;
            return true;
        }
        
        int currentBit;
        int remainingBits = 7;
        int index = 0;
        byte[] messageBytesRead = new byte[len + "(encoded)".length() + 5];
        
        next:
        for (byte i : bytes)
        {
            currentBit = nrOfBitsUsed - 1;
            while (currentBit >= 0)
            {
                if (remainingBits == -1)
                {
                    if (index == len + "(encoded)".length() + 4)
                    {
                        break next;
                    }
                    
                    index++;
                    remainingBits = 7;
                }
                
                if ((i & bit[currentBit]) == 0)
                {
                    messageBytesRead[index] = (byte) (messageBytesRead[index] & nbit[remainingBits]);
                }
                else
                {
                    messageBytesRead[index] = (byte) (messageBytesRead[index] | bit[remainingBits]);
                }
                
                currentBit--;
                remainingBits--;
            }
        }
        
        messageBytes = new byte[len];
        System.arraycopy(messageBytesRead, "(encoded)".length() + 5, messageBytes, 0, len);
        
        message = new String(messageBytes);
        
        return true;
    }
    
    public int checkForMessage(int nrOfBitsUsed)
    {
        int currentBit;
        int remainingBits = 7;
        int index = 0;
        byte[] messageBytes = new byte["(encoded)".length() + 5];
        
        for (byte i : bytes)
        {
            currentBit = nrOfBitsUsed - 1;
            while (currentBit >= 0)
            {
                if (remainingBits == -1)
                {
                    if (index == "(encoded)".length() + 4)
                    {
                        String msg = new String(messageBytes);
                        if (msg.contains("(encoded)"))
                        {
                            return Integer.parseInt(msg.substring("(encoded)".length()));
                        }
                        else
                        {
                            return -1;
                        }
                    }
                    
                    index++;
                    remainingBits = 7;
                }
                
                if ((i & bit[currentBit]) == 0)
                {
                    messageBytes[index] = (byte) (messageBytes[index] & nbit[remainingBits]);
                }
                else
                {
                    messageBytes[index] = (byte) (messageBytes[index] | bit[remainingBits]);
                }
                
                currentBit--;
                remainingBits--;
            }
        }
        return -1;
    }
}
