package nonlineargradientsui;

import java.util.List;

/**
 * Interface for the panels giving a retention time distribution 
 * 
 * @author Luminita Moruz
 */
public interface RTPanel {
    public List<Float> getRT() throws ValidationException;
    public void setInputFieldsChanged(boolean b);
    public void resetRTs();
}
