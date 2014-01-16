/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 *
 * @author Luminita Moruz
 */

class TextFieldListener implements DocumentListener {

    public TextFieldListener(RTPanel v) {
        panel = v;
    }

    @Override 
    public void changedUpdate(DocumentEvent e) {
        //System.out.println("ChangeUpdate");
        panel.setInputFieldsChanged(true);
        /* add listeners */
        
    }
     
    @Override
    public void removeUpdate(DocumentEvent e) {
        //System.out.println("RemoveUpdate");
        panel.setInputFieldsChanged(true);
    }
      
    @Override
    public void insertUpdate(DocumentEvent e) {
        //System.out.println("InsertUpdate");
        panel.setInputFieldsChanged(true);
    }

    private RTPanel panel; 
}
