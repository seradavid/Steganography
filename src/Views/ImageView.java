package Views;

import Controllers.ImageController;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

public class ImageView extends JPanel
{
    private JFrame frame = new JFrame("Choose an image");
    private JPanel panelMain;
    private JButton chooseFileButton;
    private JTextField textFieldFile;
    private JLabel labelImage;
    private JPanel panelImage;
    private JPanel panelMessage;
    private JTextArea textAreaMessage;
    private JButton embedButton;
    private JButton recoverButton;
    private JRadioButton encryptRadioButton;
    private JTextField textFieldPassword;
    private ImageIcon image;
    private ImageController ic;
    
    public ImageView()
    {
        ic = new ImageController();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setContentPane(panelMain);
        frame.setVisible(true);
        image = new ImageIcon();
        
        chooseFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image files", "bmp", "jpg", "png", "jpeg");
            fileChooser.setFileFilter(filter);
            fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
            int result = fileChooser.showOpenDialog(panelMain);
            if (result == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                textFieldFile.setText(selectedFile.getAbsolutePath());
            }
        });
        
        textFieldFile.getDocument().addDocumentListener(new DocumentListener()
        {
            @Override
            public void insertUpdate(DocumentEvent e)
            {
                UpdateFile();
            }
            
            @Override
            public void removeUpdate(DocumentEvent e)
            {
                UpdateFile();
            }
            
            @Override
            public void changedUpdate(DocumentEvent e)
            {
                UpdateFile();
            }
            
            void UpdateFile()
            {
                if (textFieldFile.getText().equals(""))
                {
                    return;
                }
                try
                {
                    image.setImage(ImageIO.read(new File(textFieldFile.getText())));
                    labelImage.setIcon(image);
                    ic.setImage(textFieldFile.getText());
                } catch (IOException ex)
                {
                    infoBox(ex.getMessage(), "An exception was encountered");
                } finally
                {
                    textAreaMessage.setText("");
                    panelImage.revalidate();
                    panelMessage.revalidate();
                }
            }
        });
        
        embedButton.addActionListener(e -> {
            try
            {
                if (ic.encode(textAreaMessage.getText(), 2, encryptRadioButton.isSelected(), textFieldPassword.getText()))
                {
                    infoBox("Successfully encoded the message", "Success");
                }
                else
                {
                    infoBox("Could not encode the message", "Failure");
                }
            } catch (IOException ex)
            {
                infoBox(ex.getMessage(), "An exception was encountered");
            }
        });
        
        recoverButton.addActionListener(e -> {
            if(ic.decode(2, encryptRadioButton.isSelected(), textFieldPassword.getText()))
            {
                textAreaMessage.setText(ic.getMessage());
            }
            else
            {
                infoBox("Could not decode the message", "Failure");
            }
        });
    }
    
    private static void infoBox(String message, String titleBar)
    {
        JOptionPane.showMessageDialog(null, message, titleBar, JOptionPane.INFORMATION_MESSAGE);
    }
}
