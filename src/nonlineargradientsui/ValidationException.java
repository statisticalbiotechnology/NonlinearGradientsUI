package nonlineargradientsui;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author luminitamoruz
 */
public class ValidationException extends Exception {
     
    private String errorCode="Unknown_Exception";
    
    public ValidationException(String message, String errorCode) {
        super(message);
        this.errorCode=errorCode;
    }
     
    public String getErrorCode() {
        return this.errorCode;
    }
}
