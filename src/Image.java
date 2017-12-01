import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.*;

class Image
{
    private byte[] bytes;
    private int width;
    private int height;
    
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
    
    private String message;
    
    Image()
    {
        width = 0;
        height = 0;
        bytes = null;
        message = null;
    }
    
    Image(String path)
    {
        setImage(path);
        message = null;
    }
    
    void setImage(String path)
    {
        try
        {
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
            bytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
            
            for (int i = 0; i < bytes.length - 2; i += 3)
            {
                byte tmp = bytes[i];
                bytes[i] = bytes[i + 2];
                bytes[i + 2] = tmp;
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    void printImage()
    {
        printImage("default.jpg");
    }
    
    void printImage(String path)
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            return;
        }
        
        String[] extension;
        if (path.contains("."))
        {
            extension = path.split("\\.");
        }
        else
        {
            System.out.println("Please specify a file extension");
            return;
        }
        
        try
        {
            DataBufferByte buffer = new DataBufferByte(bytes, bytes.length);
            
            WritableRaster raster = WritableRaster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[]{0, 1, 2}, null);
            ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            BufferedImage image = new BufferedImage(cm, raster, true, null);
            
            ImageIO.write(image, extension[extension.length - 1], new File(path));
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    
    void setMessage(String message)
    {
        if (message != null)
        {
            this.message = message;
        }
        else
        {
            System.out.println("Message can't be null");
        }
    }
    
    void embedMessage(int nrOfBitsToUse)
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            return;
        }
        
        if (message == null)
        {
            System.out.println("Please set the message to be embedded");
            return;
        }
        
        if (nrOfBitsToUse < 1 || nrOfBitsToUse > 8)
        {
            System.out.println("Number of bits to use must be between 1 and 8");
            return;
        }
        
        message += "5x0";
        
        byte[] messageBytes = message.getBytes();
        
        if (bytes.length * nrOfBitsToUse < messageBytes.length * 8)
        {
            System.out.println("Message too long. Increase the number of bits or shorten the message.");
            return;
        }
        
        int currentBit = nrOfBitsToUse - 1;
        int remainingBits;
        int index = 0;
        
        next:
        for (byte i : messageBytes)
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
    }
    
    String recoverMessage(int nrOfBitsUsed)
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            return null;
        }
        
        if (nrOfBitsUsed < 1 || nrOfBitsUsed > 8)
        {
            System.out.println("Number of bits used must be between 1 and 8");
            return null;
        }
        
        int currentBit;
        int remainingBits = 7;
        int index = 0;
        byte[] messageBytes = new byte[nrOfBitsUsed * bytes.length / 8];
        
        next:
        for (byte i : bytes)
        {
            currentBit = nrOfBitsUsed - 1;
            while (currentBit >= 0)
            {
                if (remainingBits == -1)
                {
                    if (index >= 3)
                    {
                        if (messageBytes[index - 2] == (byte) '5' && messageBytes[index - 1] == (byte) 'x' && messageBytes[index] == (byte) '0')
                        {
                            break next;
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
        
        byte[] messageBytesCopy = new byte[index - 2];
        System.arraycopy(messageBytes, 0, messageBytesCopy, 0, index - 2);
        
        message = new String(messageBytesCopy);
        
        return message;
    }
}
