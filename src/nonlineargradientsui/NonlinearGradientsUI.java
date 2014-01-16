/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 *
 * @author luminitamoruz
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
