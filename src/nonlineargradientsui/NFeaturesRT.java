package nonlineargradientsui;

import java.util.List;
import java.util.ArrayList;
        
/**
 * Class to store a rt time and the number of features above a given 
 * threshold observed at that retention time 
 * 
 * @author Luminita Moruz
 */
public class NFeaturesRT {
    
    /**
     * 
     * @param rt the retention time 
     * @param nfeatures the number of features 

    public NFeaturesRT(Float rt, int nfeatures) {
        this.rt = rt;
        this.nfeatures = nfeatures;
        this.mzFeatures = null;
    }*/
    
    public NFeaturesRT(Float rt, int nfeatures, List<Double> mzFeatures) {
        this.rt = rt;
        this.nfeatures = nfeatures;
        this.mzFeatures = mzFeatures;
        /* this will be given in LC times */
        this.rtOpt = (float) -1.0;
    }
    
    public Float getRT() {
        return this.rt;
    }
    
    public int getNFeatures() {
        return this.nfeatures;
    }
    
    public List<Double> getMz() {
        return this.mzFeatures;
    }
    
    public Float getRTOpt() {
        return this.rtOpt;
    }
    
    public void setRTOpt(float rtOpt) {
        this.rtOpt = rtOpt;
    }
    
    /**    
     * @return rt, nfeatures; 
     */
    @Override
    public String toString() {
        String result = "rt: " + this.rt + ", opt-rt: " + this.rtOpt + 
                ", nfeat: " + this.nfeatures + ", mzs: " +
                this.mzFeatures;        
        return result+"\n";
    }
    
    /**
     * Get the full retention time distribution by repeating each rt 
     * nfeatures times
     * @param rtList list of NFeaturesRT objects from which the RT distribution
     *         is built
     * @return list of retention times giving the full RT distribution 
     */
    static List<Float> getFullRTDistrib(List<NFeaturesRT> rtList) {
        ArrayList<Float> result = new ArrayList<Float>();
        for (NFeaturesRT nf:rtList) {
            for (int i=0; i<nf.getNFeatures(); ++i) {
                result.add(nf.getRT());
            }
        }
        return result;        
    }
    
    /** the retention time **/
    Float rt;
    /** retention time in the optimized gradient (LC times) **/
    Float rtOpt;
    /** number of features observed at this retention time **/
    int nfeatures;
    /** list of m/z for the MS1 features **/
    List<Double> mzFeatures;
}
