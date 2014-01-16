package nonlineargradientsui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main Class of the application; it create and displays the main frame 
 * 
 * @author Luminita Moruz
 */
public class NonlinearGradientsUI {

    private static void createAndShowGUI() {
        JFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);        
        frame.setVisible(true);
    }
        
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }
                catch (Exception e) {
                    UIManager.put("swing.boldMetal", Boolean.FALSE);
                }
                createAndShowGUI();
            }
        }); 
    }
    
}
