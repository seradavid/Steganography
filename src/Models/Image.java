package Models;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class Image
{
    private byte[] bytes;
    private int width;
    private int height;
    private ColorModel colorModel;
    
    public Image()
    {
        width = 0;
        height = 0;
        colorModel = null;
        bytes = null;
    }
    
    public Image(String path) throws IOException
    {
        setImage(path);
    }
    
    public void setBytes(byte[] bytes)
    {
        if(bytes != null)
        {
            this.bytes = bytes;
        }
        else
        {
            System.out.println("Bytes cannot be null");
        }
    }
    
    public byte[] getBytes()
    {
        return bytes;
    }
    
    public void setImage(String path) throws IOException
    {
        try
        {
            BufferedImage bufferedImage = ImageIO.read(new File(path)); // Open the new image file
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
            colorModel = bufferedImage.getColorModel();
            bytes = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData(); // Get the RGB values of the pixels
            // The initial order is BGR, so we swap the values (BGR -> RGB)
            for (int i = 0; i < bytes.length - 2; i += 3)
            {
                byte tmp = bytes[i];
                bytes[i] = bytes[i + 2];
                bytes[i + 2] = tmp;
            }
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    
    public void printImage() throws NullPointerException, IOException
    {
        printImage("default.bmp");
    }
    
    public void printImage(String path) throws NullPointerException, IOException
    {
        if (bytes == null)
        {
            System.out.println("Please specify a file first");
            throw new NullPointerException();
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
            DataBufferByte buffer = new DataBufferByte(bytes, bytes.length); // Create a new DataBuffer
            
            WritableRaster raster = WritableRaster.createInterleavedRaster(buffer, width, height, 3 * width, 3, new int[]{0, 1, 2}, null);
            BufferedImage image = new BufferedImage(colorModel, raster, true, null); // Create the image
            
            ImageIO.write(image, extension[extension.length - 1], new File(path)); // "Write"(draw) the image
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
