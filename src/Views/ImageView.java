package Views;

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
    private JTextField textFieldMessage;
    private JButton chooseFileButtonMessage;
    private JRadioButton textRadioButton;
    private JRadioButton fileRadioButton;
    private JPanel panelImage;
    private JPanel panelMessage;
    private JPanel panelFileOrText;
    private JTextArea textAreaMessage;
    private ImageIcon image;
    
    public ImageView()
    {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setContentPane(panelMain);
        frame.setVisible(true);
        image = new ImageIcon();
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(textRadioButton);
        buttonGroup.add(fileRadioButton);
        
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
                try
                {
                    image.setImage(ImageIO.read(new File(textFieldFile.getText())));
                    labelImage.setIcon(image);
                } catch (IOException ex)
                {
                    return;
                }
            }
        });
    }
    
}
