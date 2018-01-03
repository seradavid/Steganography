package Controllers;

import Models.Encoder;
import Models.Image;

public class ImageController
{
    Image image;
    Encoder encoder;
    
    public ImageController()
    {
        image = new Image();
        encoder = new Encoder();
    }
    
    public void setImage(String path)
    {
        image.setImage(path);
    }
    
    public void encode(String message, int nrOfBits)
    {
        encoder.setBytes(image.getBytes());
        encoder.setMessage(message);
        encoder.embedMessage(nrOfBits);
        image.setBytes(encoder.getBytes());
        image.printImage();
    }
    
    public void decode(int nrOfBits)
    {
        encoder.setBytes(image.getBytes());
        encoder.recoverMessage(nrOfBits);
        System.out.println(encoder.getMessage());
    }
}
