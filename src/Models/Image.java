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
    
    public Image(String path)
    {
        setImage(path);
    }
    
    public void setBytes(byte[] bytes)
    {
        this.bytes = bytes;
    }
    
    public byte[] getBytes()
    {
        return bytes;
    }
    
    public void setImage(String path)
    {
        try
        {
            BufferedImage bufferedImage = ImageIO.read(new File(path));
            width = bufferedImage.getWidth();
            height = bufferedImage.getHeight();
            colorModel = bufferedImage.getColorModel();
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
    
    public void printImage()
    {
        printImage("default.bmp");
    }
    
    public void printImage(String path)
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
            //ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
            BufferedImage image = new BufferedImage(colorModel, raster, true, null);
            
            ImageIO.write(image, extension[extension.length - 1], new File(path));
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
}
