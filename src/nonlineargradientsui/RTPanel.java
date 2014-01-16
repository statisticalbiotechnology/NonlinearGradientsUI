/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import java.util.List;

/**
 *
 * @author luminitamoruz
 */
public interface RTPanel {
    public List<Float> getRT() throws ValidationException;
    public void setInputFieldsChanged(boolean b);
}
