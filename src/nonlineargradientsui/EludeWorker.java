package nonlineargradientsui;

import java.io.File;
import javax.swing.SwingWorker;

/**
 * Class that will run Elude in the background
 * 
 * @author Luminita Moruz
 */

public class EludeWorker extends SwingWorker<Void, Void> {
    
    /**
     * Constructor
     * @param commands list of commands to be run 
     * @param tmpDir directory where to write temporary files 
     * @param errFile the file where the stderr from Elude is redirected
     * @param logFile the file where the stout from Elude is redirected
     */
    public EludeWorker(String[] commands, String tmpDir, String errFile, 
            String logFile) {
        this.commands = commands;
        this.tmpDir = tmpDir;
        this.errFile = errFile;
        this.logFile = logFile;
    }
    
    /**
     * Function to run elude in background
     * @return
     * @throws Exception 
     */
    @Override
    protected Void doInBackground() throws Exception {
        // run Elude as external process
        ProcessBuilder pb =  new ProcessBuilder(commands);
        pb.directory(new File(tmpDir));            
        pb.redirectError(new File(errFile));
        pb.redirectOutput(new File(logFile));                      
        Process eludeProcess = pb.start(); 
            
        // wait until Elude has finished and check the exit status 
        int exitStatus = eludeProcess.waitFor();
        if (exitStatus != 0) {
            throw new ValidationException("", "");
        }      
        return null;     
    }     
    
    final private String tmpDir;
    final private String errFile;
    final private String logFile;
    final private String[] commands;
}
