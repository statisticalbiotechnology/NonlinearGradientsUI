package nonlineargradientsui;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import uk.ac.ebi.jmzml.xml.io.MzMLUnmarshaller;
import uk.ac.ebi.jmzml.xml.io.MzMLObjectIterator;
import uk.ac.ebi.jmzml.model.mzml.Spectrum;
import uk.ac.ebi.jmzml.model.mzml.ScanList;
import uk.ac.ebi.jmzml.model.mzml.CVParam;
import uk.ac.ebi.jmzml.model.mzml.Scan;
import uk.ac.ebi.jmzml.model.mzml.BinaryDataArray;

        
/**
 * Class to manipulate spectra from .MZML files
 * 
 * @author Luminita Moruz
 */
public class MzmlIO {     
    /**
    static void writeMs1SpectraToFile(String mzmlFilename, String outFile) throws Exception {
        List<MySpectrum> ms1Spectra = loadMS1FromMzml(mzmlFilename);
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        for (MySpectrum ms:ms1Spectra) {
            bw.write(ms.toString());
        }
        bw.close();
    }*/
   
    /**
     * Unmarshall .mzml using the .jmzml library. This method may be quite 
     * slow
     * @param mzmlFilename full path to the .mzml file 
     * @return an MzMLUnmarshaller object 
     * @throws Exception 
     */
    static MzMLUnmarshaller unmarshallMzml(String mzmlFilename) throws Exception {
        // use the jmzml library to load the mzml file 
        MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(new File(mzmlFilename));  
        return unmarshaller;
    }
    
    /**
     * Return the mz of the intensity values that are above a given intensity 
     * in a spectrum 
     * 
     * @param spectrum the spectrum object 
     * @param intThreshold the intensity threshold
     * @param minMz minimum m/z of the features taken into account
     * @param maxMz maximum m/z of the features to be taken into account
     * @return a list of mz corresponding to the  intensity values above the threshold, or 
     * an empty list if no intensity or mz data is found in this spectrum object. 
     * If more than one intensity array if found,
     * then only the first one is taken into account;
     */
    static List<Double> getFeaturesAboveThreshold(Spectrum spectrum, double intThreshold,
            double minMz, double maxMz) {
        Number[] intValues = null;
        Number[] mzValues = null;
        List<BinaryDataArray> binDataArrayList; 
        List<CVParam> cvParamList;
        List <Double> mzs = new ArrayList<>();
        
        // get the arrays containing the data 
        try {
        binDataArrayList = spectrum.getBinaryDataArrayList().getBinaryDataArray();                        
        for (BinaryDataArray bda:binDataArrayList) {   
            cvParamList = bda.getCvParam();            
            // check if the arrays is a list of intensity or m/z values
            for (CVParam cv:cvParamList) {
                if (cv.getAccession().equals("MS:1000515")) {
                    intValues = bda.getBinaryDataAsNumberArray(); 
                    /*System.out.println("FOUND NTENSITY: " + intValues.length);
                    for (Number n: intValues) {
                        double b = (double) n;
                        System.out.println("Int = " + b);
                    }*/
                } 
                if (cv.getAccession().equals("MS:1000514")) {
                    mzValues = bda.getBinaryDataAsNumberArray();  
                    /*for (Number n: mzValues) {
                        double b = (double) n;
                        System.out.println("mz = " + b);
                    }*/
                }                
            }
        }      
        if (intValues == null || mzValues == null) {
            return mzs;
        }
        //System.out.println(intValues.length + " AAA " + mzValues.length);
        double mz;
        for (int i = 0; i < intValues.length; ++i) {
            mz = mzValues[i].doubleValue(); 
            if (mz >= minMz && mz <= maxMz && intValues[i].doubleValue() >= intThreshold) {
                mzs.add(mz);
            }
        }
        return mzs;       
        } catch (Exception e) {
            e.printStackTrace();
            return mzs;
        }
    } 
    
     
     /**
     * Extract the retention time from the ScanList of a spectrum
     * @param spectrum the spectrum object 
     * @return the value of the "scan start time" (MS:1000016), or "elution time"
     *          (MS:1000826); -1.0 if no scan or none of these times are found 
     */
    static Float getRetentionTime(Spectrum spectrum) {
        Float retentionTime = (float) -1.0;
        Scan scan;
        ScanList scanList = spectrum.getScanList();
        List<CVParam> cvParamList;
        
        // check that at least one scan is found
        if (scanList.getCount() == 0) {
            return retentionTime;
        }
        // extract the retention time from the scan by going through the CV list 
        // and searching scan start time or elution time
        scan = scanList.getScan().get(0);
        cvParamList = scan.getCvParam();
        for (CVParam cv:cvParamList) {
            if (cv.getAccession().equals("MS:1000016") || 
                cv.getAccession().equals("MS:1000826")) {
                if (cv.getUnitAccession().equals("UO:0000010")) {
                    return Float.parseFloat(cv.getValue())/(float)60.0;        
                } else {
                   return Float.parseFloat(cv.getValue());        
                }
            }
        }        
        return retentionTime;
    }
    
    /**
     * For each spectrum, get the retention time and the number of features 
     * with intensity above the threshold. 
     * @param unmarshaller unmarshaller for an mzml file
     * @param intensityThreshold the intensity threshold 
     * @param minMz the minimum mz for features to be taken into account 
     * @param maxMz the maximum mz for features to be taken into account 
     * @return list of spectra retention times, number of features above intensity
     * threshold
     */
    static List<NFeaturesRT> loadMS1FromMzml(MzMLUnmarshaller unmarshaller, 
            double intensityThreshold, double minMz, double maxMz) throws Exception {
        List<NFeaturesRT> ms1SpectraList = new ArrayList<NFeaturesRT>();
        List<Double> mzs;
        Spectrum spectrum;
        Float retentionTime;
        int nMs1Spectra = 0;    
        NFeaturesRT tmp;
        
        // iterate through spectra, one spectrum at a time
        MzMLObjectIterator<Spectrum> spectrumIterator = 
                unmarshaller.unmarshalCollectionFromXpath("/run/spectrumList/spectrum", 
                Spectrum.class);
        
        while (spectrumIterator.hasNext()) {                   
            spectrum = spectrumIterator.next(); 
            // jump to the next iteration if this spectrum is not MS1
            if (spectrum.getPrecursorList() != null) {
                continue;
            }
            nMs1Spectra += 1;
            
            // get the retention time from the ScanList
            retentionTime = getRetentionTime(spectrum);                      
            // if no retention time was found, no point to do anything else
             if (retentionTime < 0) {
                continue;
            }            
            
            // get the list of mz's for the features with intensity above threshold 
            mzs = getFeaturesAboveThreshold(spectrum, intensityThreshold, minMz, maxMz);
            if (mzs.isEmpty()) {
                continue;
            }
            
            // add this to the list 
            tmp = new NFeaturesRT(retentionTime, mzs.size(), mzs);
            ms1SpectraList.add(tmp);  
        }   
        System.out.println("" + nMs1Spectra + " MS1 spectra found");
        
        return ms1SpectraList;
    } 
    
    /**
     * return the maximum intensity between each intensity in this 
     * spectrum, and the intensity in intermediaryMax
     * @param spectrum the spectrum object
     * @param intermediaryMax the maximum intensity found so far 
     * @param minMz minimum mz of the features considered
     * @param maxMz maximum mz of the features considered
     * @return maximum intensity up to now 
     */
    static double getMaxIntensity(Spectrum spectrum, double intermediaryMax, 
            double minMz, double maxMz) {
        Number[] intValues = null;
        Number[] mzValues = null;
        List<BinaryDataArray> binDataArrayList; 
        List<CVParam> cvParamList;
        
        /*
        // get the arrays containing the data 
        binDataArrayList = spectrum.getBinaryDataArrayList().getBinaryDataArray();                        
        for (BinaryDataArray bda:binDataArrayList) {   
            cvParamList = bda.getCvParam();            
            // check if the arrays is a list of intensity values 
            for (CVParam cv:cvParamList) {
                if (cv.getAccession().equals("MS:1000515")) {
                    intValues = bda.getBinaryDataAsNumberArray();
                    for (Number intensity:intValues) {
                        if ((double)intensity > intermediaryMax) {
                            intermediaryMax = (double) intensity;
                        }
                    }
                    return intermediaryMax;
                }
                
                
            }
        }*/
        // get the intensities and mz values 
        binDataArrayList = spectrum.getBinaryDataArrayList().getBinaryDataArray();                        
        for (BinaryDataArray bda:binDataArrayList) {   
            cvParamList = bda.getCvParam();            
            // check if the arrays is a list of intensity or m/z values
            for (CVParam cv:cvParamList) {
                if (cv.getAccession().equals("MS:1000515")) {
                    intValues = bda.getBinaryDataAsNumberArray(); 
                } 
                if (cv.getAccession().equals("MS:1000514")) {
                    mzValues = bda.getBinaryDataAsNumberArray();  
                }                
            }
        }      
        // get the maximum intensity looking only at features within the mz interval 
        if (intValues != null) {
            int i;
            if (mzValues != null) {
                for (i = 0; i < intValues.length; ++i) {
                    if ((double) mzValues[i] >= minMz && (double) mzValues[i] <= maxMz &&
                            (double)intValues[i] > intermediaryMax) {
                        intermediaryMax = (double) intValues[i];
                    }          
                } 
            } else {
                for (i = 0; i < intValues.length; ++i) {
                    if ((double)intValues[i] > intermediaryMax) {
                        intermediaryMax = (double) intValues[i];
                    }
                }
            }
        }
        return intermediaryMax;    
    }
  
    /**
     * Get the maximum intensity of any MS1 feature with mz between minMz and mazMz
     * @param unmarshaller the unmarshaller of the mzml file 
     * @param minMz minimum mz of the features considered
     * @param maxMz maximum mz of the features considered
     * @return the intensity of the highest abundant MS1 peak
     */
    static double getMaxIntensity(MzMLUnmarshaller unmarshaller, double minMz, 
            double maxMz) {
        double maxIntensity = -1; 
        Spectrum spectrum;      
        double tmp;
        
        // iterate through spectra, one spectrum at a time
        MzMLObjectIterator<Spectrum> spectrumIterator = 
                unmarshaller.unmarshalCollectionFromXpath("/run/spectrumList/spectrum", 
                Spectrum.class);        
        while (spectrumIterator.hasNext()) {                                
            spectrum = spectrumIterator.next();                 
            // jump to the next iteration if this spectrum is not MS1
            if (spectrum.getPrecursorList() != null) {
                continue;
            }
            //System.out.println(spectrum.getId());                
            maxIntensity = getMaxIntensity(spectrum, maxIntensity, minMz, maxMz);                        
        }           
        return maxIntensity;
    }
   
    
    static void writeRTsToFile(List<NFeaturesRT> rts, String outFile) throws Exception {
        BufferedWriter bw = new BufferedWriter(new FileWriter(outFile));
        for (NFeaturesRT nf:rts) {
            bw.write(nf.toString() + "\n");
        }
        bw.close();
    }
    
   /**
    * Main just for testing this function 
    * @param args
    * @throws Exception 
    */ 
   public static void main(String[] args) throws Exception {
       /*
       String mzmlFile = "/scratch/lumi_work/projects/gradient_design/java/NonlinearGradientsUI/data/ms1/20130604_OT-XL_U3000-BETA_500ng_Hela_01.mzML";
       mzmlFile = "/scratch/lumi_work/projects/gradient_design/java/NonlinearGradientsUI/data/ms1/103111-Yeast-2hr-01.mzML";
       
       long startTime = System.currentTimeMillis();       
       MzMLUnmarshaller um = unmarshallMzml(mzmlFile);
       long endTime = System.currentTimeMillis();       
       System.out.println("Unmarshall took " + (endTime - startTime)/60000 + " min");
       //System.out.println("------------------------");
       //System.out.println(getMaxIntensity(um));       
       double threshold = 0.5e8;
       startTime = System.currentTimeMillis();       
       List<NFeaturesRT> rts = loadMS1FromMzml(um,threshold);
       writeRTsToFile(rts, "/scratch/lumi_work/yyy");       
       endTime = System.currentTimeMillis();
       System.out.println("getting RTs took " + (endTime - startTime)/60000 + " min");*/
       Number[] a = new Number[0];
       Number[] b = new Number[2];
       b[0] = 1.0;
       b[1] = 2.0;
       System.out.println(a.length);
       a = b;
       for (Number xx:a) {
           System.out.println(xx);
       }
       
   }
}
