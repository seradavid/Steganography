public class Main
{
    public static void main(String[] args)
    {
        Image test = new Image("download.jpg");
        test.setMessage("a");
        test.embedMessage(1);
        test.printImage("dl.jpg");
        test.setImage("dl.jpg");
        System.out.println(test.recoverMessage(1));
    }
}
