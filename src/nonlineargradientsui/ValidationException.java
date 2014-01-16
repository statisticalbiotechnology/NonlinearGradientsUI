package nonlineargradientsui;
/**
 * Customized exception class 
 * 
 * @author Luminita Moruz
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
