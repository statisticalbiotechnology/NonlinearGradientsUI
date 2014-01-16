/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import javax.swing.SwingWorker;
import java.util.List;
import static nonlineargradientsui.MzmlIO.unmarshallMzml;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import java.text.DecimalFormat;

/**
 * Class to process .mzml files 
 * 
 * @author Luminita Moruz
 */

public class MzmlWorker extends SwingWorker<List<NFeaturesRT>, Void> {
    /**
     * Constructor to be used when the .mzml needs to be unmarshalled 
     * @param mzml name of the mzml file 
     * @param minIntensity the minimum intensity threshold 
     * @param isAbsIntensity true if the threshold represents and absolute intensity value 
     * @param minMz the minimum m/z to be taken into account 
     * @param maxMz the maximum m/z to be taken into account 
     */
    public MzmlWorker(String mzml, double minIntensity, boolean isAbsIntensity, 
            double minMz, double maxMz) {
        this.mzmlFilename = mzml;
        this.minIntensity = minIntensity;
        this.isAbsIntensity = isAbsIntensity;        
        this.minMz = minMz;
        this.maxMz = maxMz;
        this.unmarshaller = null;
    }
    
    /**
     * Constructor to be used when the .mzml is already loaded 
     * @param unmarshaller unmarshaller for the he mzml file 
     * @param minIntensity the minimum intensity threshold 
     * @param isAbsIntensity true if the threshold represents and absolute intensity value 
     * @param minMz the minimum m/z to be taken into account 
     * @param maxMz the maximum m/z to be taken into account 
     */
    public MzmlWorker(MzMLUnmarshaller unmarshaller, double minIntensity, boolean isAbsIntensity, 
            double minMz, double maxMz) {
        this.unmarshaller = unmarshaller;
        this.minIntensity = minIntensity;
        this.isAbsIntensity = isAbsIntensity;        
        this.minMz = minMz;
        this.maxMz = maxMz;
        this.mzmlFilename = null;        
    }
    
    /**
     * Function to process an .mzml file (loading, extracting features above threshold)
     * @return
     * @throws Exception 
     */
    @Override
    protected List<NFeaturesRT> doInBackground() throws Exception {
        // unmarshall the mzml if necessary         
        long startTime, stopTime, elapsedTime;
        DecimalFormat df = new DecimalFormat("#.##");
        if (this.unmarshaller == null) {
            System.out.println(GeneralUtilities.NEWLINE + 
                    "Unmarshalling the mzml file " + this.mzmlFilename + " ...");
            startTime = System.currentTimeMillis();            
            this.unmarshaller = unmarshallMzml(this.mzmlFilename);
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;      
            System.out.println("Unmarshalling took " + df.format(elapsedTime/60000.0)
                    + " minutes");
            
        }            
        double minIntesityThreshold = this.minIntensity;
        // if the intensity is not absolute, get the max intensity and calculate 
        // the absolute intensity threshold 
        if (!this.isAbsIntensity) {
            System.out.println(GeneralUtilities.NEWLINE + 
                    "Find the maximum intensity of a feature");
            startTime = System.currentTimeMillis();            
            double maxInt = MzmlIO.getMaxIntensity(this.unmarshaller, this.minMz, this.maxMz);
            minIntesityThreshold = this.minIntensity/100.0 * maxInt;
            stopTime = System.currentTimeMillis();
            elapsedTime = stopTime - startTime;      
            System.out.println("Finding max intensity took " + df.format(elapsedTime/60000.0)
                    + " minutes, max intensity = " + maxInt);
        }
        // Get the retention times above threshold 
        System.out.println(GeneralUtilities.NEWLINE + 
                    "Extract features above intensity threshold ...");            
        startTime = System.currentTimeMillis();                        
        List<NFeaturesRT> nrtList = MzmlIO.loadMS1FromMzml(this.unmarshaller, minIntesityThreshold, 
                this.minMz, this.maxMz);
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;      
        System.out.println("Extracting features took " + df.format(elapsedTime/60000.0)
                + " minutes");
        
        return nrtList;        
    }

    /**
     * Get the unmarshaller for the mzml file 
     * @return unmarshaller 
     */
    public MzMLUnmarshaller getUnmarshaller() {
        return this.unmarshaller;
    }
    
    // unmarshaller for the xml file 
    MzMLUnmarshaller unmarshaller;
    // Path to the .mzml file 
    final private String mzmlFilename;
    // The minimum intensity threshold of an MS1 feature 
    final private double minIntensity;
    // true if the minIntensity is an absolute value, false if it is percentage of 
    // the maximum intensity 
    final private boolean isAbsIntensity;    
    // minimum and maximum intensity of features to be taken into account 
    double minMz; 
    double maxMz;
}
