import Controllers.ImageController;
import Models.Encoder;
import Views.ImageView;

import javax.swing.*;

public class Main
{
    public static void main(String[] args)
    {
//        Image test = new Image("dwl.jpg");
//        test.setMessage("aaaa");
//        test.embedMessage(2);
//        test.printImage("asdf.jpg");
//        test.setImage("asdf.jpg");
//        System.out.println(test.recoverMessage(2));
        ImageController ic = new ImageController();
        ic.setImage("download.jpg");
        ic.encode("asdf", 3);
        ic.setImage("default.bmp");
        ic.decode(3);
    }
}
