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

/**
 * Class to process .mzml files 
 * 
 * @author Luminita Moruz
 */
public class MzmlWorker extends SwingWorker<List<NFeaturesRT>, Void> {
    public MzmlWorker(String mzml, double minIntensity, boolean isAbsIntensity, 
            double minMz, double maxMz) {
        this.mzmlFilename = mzml;
        this.minIntensity = minIntensity;
        this.isAbsIntensity = isAbsIntensity;        
        this.minMz = minMz;
        this.maxMz = maxMz;
    }
    
    @Override
    protected List<NFeaturesRT> doInBackground() throws Exception {
        /* unmarshall the mzml */
        System.out.println("Processing " + this.mzmlFilename);
        MzMLUnmarshaller um = unmarshallMzml(this.mzmlFilename);
        double minIntesityThreshold = this.minIntensity;
        /* if the intensity is not absolute, get the max intensity and calculate 
        the absolute intensity threshold */
        if (!this.isAbsIntensity) {
            double maxInt = MzmlIO.getMaxIntensity(um, this.minMz, this.maxMz);
            minIntesityThreshold = this.minIntensity/100.0 * maxInt;
        }
        System.out.println(minIntesityThreshold);
        /* Get the retention times above threshold */
        List<NFeaturesRT> nrtList = MzmlIO.loadMS1FromMzml(um, minIntesityThreshold, 
                this.minMz, this.maxMz);
        return nrtList;        
    }

    /* Path to the .mzml file */
    final private String mzmlFilename;
    /* The minimum intensity threshold of an MS1 feature */
    final private double minIntensity;
    /* true if the minIntensity is an absolute value, false if it is percentage of 
    the maximum intensity */
    final private boolean isAbsIntensity;    
    /* minimum and maximum intensity of features to be taken into account */
    double minMz; 
    double maxMz;
}
