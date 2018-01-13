package Controllers;

import Models.Encoder;
import Models.Image;
import org.jasypt.util.text.BasicTextEncryptor;

import java.io.IOException;

public class ImageController
{
    private Image image;
    private Encoder encoder;
    private BasicTextEncryptor textEncryptor;
    
    public ImageController()
    {
        image = new Image();
        encoder = new Encoder();
    }
    
    public void setImage(String path) throws IOException
    {
        try
        {
            image.setImage(path);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
            throw e;
        }
    }
    
    public boolean encode(String message, int nrOfBits, boolean encrypt, String password) throws IOException
    {
        if (encrypt)
        {
            textEncryptor = new BasicTextEncryptor();
            textEncryptor.setPassword(password);
            message = textEncryptor.encrypt(message);
        }
        
        encoder.setBytes(image.getBytes());
        encoder.setMessage(message);
        if(encoder.embedMessage(nrOfBits))
        {
            image.setBytes(encoder.getBytes());
            image.printImage();
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public boolean decode(int nrOfBits, boolean encrypted, String password)
    {
        encoder.setBytes(image.getBytes());
        if(encoder.recoverMessage(nrOfBits))
        {
            if (encrypted)
            {
                textEncryptor = new BasicTextEncryptor();
                textEncryptor.setPassword(password);
                encoder.setMessage(textEncryptor.decrypt(encoder.getMessage()));
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public String getMessage()
    {
        String message = encoder.getMessage();
        if (message != null)
        {
            return message;
        }
        else
        {
            return "There is no message embedded in this file";
        }
    }
}
