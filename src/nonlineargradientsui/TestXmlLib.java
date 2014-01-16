/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

import java.io.File;
import uk.ac.ebi.jmzml.model.mzml.*;
import uk.ac.ebi.jmzml.xml.io.*;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;


public class TestXmlLib {
    
   public static void main(String[] args) throws Exception {
       String filename = "/scratch/lumi_work/projects/gradient_design/java/NonlinearGradientsUI/data/ms1/20130604_OT-XL_U3000-BETA_500ng_Hela_01.mzML";
        
       File xmlFile = new File(filename);
       System.out.println("Before unmarchall");
       MzMLUnmarshaller unmarshaller = new MzMLUnmarshaller(xmlFile);  
       System.out.println("After unmarchall");
       /*
       Spectrum s;
       MzMLObjectIterator<Spectrum> spectrumIterator = unmarshaller.unmarshalCollectionFromXpath("/run/spectrumList/spectrum", Spectrum.class);
       while (spectrumIterator.hasNext()) {
       //read next spectrum from XML file
            Spectrum spectrum = spectrumIterator.next();            
            
            ScanList sc = spectrum.getScanList();
            List<CVParam> params = sc.getScan().get(0).getCvParam();
            for (CVParam cv:params) {
                if (cv.getName().equals("scan start time")) {
                    System.out.println("TIME: " + cv.getValue());
                }
            }
            
            //use it
            System.out.println("Spectrum ID: " + spectrum.getId());            
            
            //List<CVParam> cvp = spectrum.getCvParam();
            //for (CVParam cv:cvp) {
            //    System.out.println(cv.getName() + " " + cv.getValue());
            //}
            // MS1 spectra have spectrum.getPrecursorList() = null
            System.out.println("Spectrum precursor list: " + spectrum.getPrecursorList());            
            List<BinaryDataArray> b = spectrum.getBinaryDataArrayList().getBinaryDataArray();            
            
            for (BinaryDataArray bda:b) {   
                List<CVParam> myparams = bda.getCvParam();
                for (CVParam cp:myparams) {
                    if (cp.getName().equals("m/z array")) {
                        Number[] x = bda.getBinaryDataAsNumberArray();
                        for (Number xx:x) {
                            System.out.print(xx + ", ");                    
                        }
                    } 
                    if (cp.getName().equals("intensity array")) {
                        Number[] x = bda.getBinaryDataAsNumberArray();
                        for (Number xx:x) {
                            System.out.print(xx + ", ");                    
                        }
                    } 
                }
                
                
                
                System.out.println("--------------------");
            }
            System.out.println("############################");            
        }*/
       List <BinaryDataArray> bdaList;
       Number [] timeList = null;
       Number [] intensityList = null;
       List<CVParam> params;
       Chromatogram c;
       MzMLObjectIterator<Chromatogram> chromatogramIterator = 
               unmarshaller.unmarshalCollectionFromXpath(
               "/run/chromatogramList/chromatogram", Chromatogram.class);
       String type = "";
       
       while (chromatogramIterator.hasNext()) {
       //read next spectrum from XML file
            c = chromatogramIterator.next();            
            // get the binary array list 
            bdaList = c.getBinaryDataArrayList().getBinaryDataArray();
            for (BinaryDataArray bda:bdaList) {
                params = bda.getCvParam();                                
                for (CVParam cp:params) {
                    System.out.println(cp.getName());
                    if (cp.getName().equals("time array")) {                        
                        timeList = bda.getBinaryDataAsNumberArray();
                    }
                    if (cp.getName().equals("intensity array")) {
                        type = "intensity array";
                        intensityList = bda.getBinaryDataAsNumberArray();                        
                    }                   
                    
                }
            }
       }
       
       if (timeList == null || intensityList == null) {
           System.err.println("Error 1");
           return;
       }
       if (timeList.length != intensityList.length) {
           System.err.println("Error 2");
           return;
       }
       BufferedWriter bw = new BufferedWriter(new FileWriter("/tmp/xxx.txt"));
       for (int i = 0; i < timeList.length; ++i){
           bw.write("" + timeList[i] + "\t" + intensityList[i] + "\n");
       }
       bw.close();
       

    }
}

