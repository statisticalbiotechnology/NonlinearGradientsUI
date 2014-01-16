package nonlineargradientsui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Whenever the mzml text field changes, a flag is set such that we know 
 * that the mzml file needs to be reloaded
 * 
 * @author Luminita Moruz
 */

class MzmlTextFieldListener implements DocumentListener {

    public MzmlTextFieldListener(MS1DistributionPanel v) {
        panel = v;
    }

    @Override 
    public void changedUpdate(DocumentEvent e) {
        //System.out.println("ChangeUpdate");
        panel.setMzmlFieldChanged(true);        
    }
     
    @Override
    public void removeUpdate(DocumentEvent e) {
        //System.out.println("RemoveUpdate");
        panel.setMzmlFieldChanged(true);
    }
      
    @Override
    public void insertUpdate(DocumentEvent e) {
        //System.out.println("InsertUpdate");
        panel.setMzmlFieldChanged(true);
    }

    private MS1DistributionPanel panel; 
}
