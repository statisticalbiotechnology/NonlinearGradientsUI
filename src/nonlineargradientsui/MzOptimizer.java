package nonlineargradientsui;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


/**
 * Class that calculates optimized m/z windows  
 * 
 * @author Luminita Moruz
 */
public class MzOptimizer {

    /**
     * Constructor 
     * @param lagTime difference between LC time and raw file time 
     * @param linGradient linear gradient 
     * @param optGradient optimized gradient 
     * @param nTimePoints number of time points when the m/z windows are changed 
     * @param nMZWindows number of m/z windows 
     * @param minMz minimum m/z of the features considered 
     * @param maxMz maximum m/z of the features considered 
     * @param features list of features above intensity loaded from the mzml file 
     */
    public MzOptimizer(float lagTime, GradientFunction linGradient, 
            GradientFunction optGradient, int nTimePoints, int nMZWindows, 
            double minMz, double maxMz, List<NFeaturesRT> features) {
        this.lagTime = lagTime;
        this.linearGradient = linGradient;
        this.optimizedGradient = optGradient;
        this.nTimePoints = nTimePoints;
        this.nMZWindows = nMZWindows;
        this.minMz = minMz;
        this.maxMz = maxMz;
        this.features = features;
    }    
    
    /**
     * Calculate the optimal mz windows for the mzs
     * @param mzs - the mzs of the features to be spreaded equally 
     * @return a list of mzs splits (a window will be between splits i and i+1) 
     */
    public static List<Double> getMzSplits(List<Double> mzs, int nMzWin, double minM, double maxM) {
        List<Double> splits = new ArrayList<>();
        // the number of mzs should be at larger than the number of windows 
        if (mzs.size() <= nMzWin) {
            return splits;
        }    
        // number of mzs per window 
        double nmzWin = (double) mzs.size() / nMzWin;
        //System.out.println("Number of mz per window: " + nmzWin);
        // sort the mzs 
        Collections.sort(mzs);
        //new Utilities<Double>().printList(mzs);
        // calculate the splits
        splits.add(minM);
        double index = nmzWin-1;
        int idx, prevIndex, nextIndex;
        double prevMz, nextMz, currMz;
        while (index < mzs.size()-1) {
            prevIndex = (int) Math.floor(index);
            prevMz = mzs.get(prevIndex);
            if (index == prevIndex) {     
                splits.add((prevMz + mzs.get(prevIndex+1)) / 2);
                //splits.add(mzs.get(idx)); 
            }
            else {		
                nextIndex = (int) Math.ceil(index);
		nextMz = mzs.get(nextIndex); 
		currMz = (index-prevIndex)*nextMz + (nextIndex-index)*prevMz;
                if (currMz < prevMz) {
                    currMz = prevMz;
                }
                splits.add(currMz);
            }
            index += nmzWin;
        }    
        if (splits.size() == nMzWin+1) {
            splits.remove(splits.size()-1);
        }
        splits.add(maxM);
        return splits;
    }
    
    /** 
     * Fill the retention times according to the nonlinear gradient 
     */
    public void fillOptimizedRTs() {
        float rt, percB, optRT;
        for (NFeaturesRT nf: this.features) {
            // get the corresponding lc time
            rt = nf.getRT() - lagTime;
            // check whether the peptide elutes during the gradient time 
            if (rt >= this.linearGradient.getStartTime() && rt <= this.linearGradient.getEndTime()) {
                // get the perc B according to the linear gradient 
                percB = GradientFunction.interpolateValue(rt, 
                        linearGradient.getLcTimes(), linearGradient.getPercB());
                // get the corresponding time in the optimized gradient 
                optRT = GradientFunction.interpolateValue(percB, 
                        optimizedGradient.getPercB(), optimizedGradient.getLcTimes());
                nf.setRTOpt(optRT);
            }
        }     
    }
      
    /**
     * Calculate the optimized DIA windows 
     * 
     * @return List of optimized windows 
     */
    public List<RtMzWindows> getOptimizedMzWindows() {
        List<RtMzWindows> opt = new ArrayList<>();
        List<Double> mzs = new ArrayList<>();
        List<Double> splits = new ArrayList<>();
        
        // calculate the width of the RT interval 
        float rtIntervalWidth = (this.linearGradient.getEndTime()-this.linearGradient.getStartTime())
                /this.nTimePoints;
        
        // calculate the retention time of the features according to the optimized gradient 
        fillOptimizedRTs();
        //new Utilities<NFeaturesRT>().printList(this.features);
        
        // sort the features according to their rt in the optimized gradient 
        Collections.sort(this.features, new Comparator<NFeaturesRT>() {
            public int compare(NFeaturesRT f1, NFeaturesRT f2) {
                return f1.getRTOpt().compareTo(f2.getRTOpt());
            }
        });
        //new Utilities<NFeaturesRT>().printList(this.features);
        
        //skip the rts before the start of the gradient 
        int i = 0;
        while (i<this.features.size() && this.features.get(i).getRTOpt()<this.optimizedGradient.getStartTime()) {
            i += 1;
        }
        
        // start calculating the mz windows 
        float s = this.optimizedGradient.getStartTime();
        float e = s + rtIntervalWidth;
        for (int j = 0; j < this.nTimePoints; ++j) 
        {  
            // get all the mzs in this interval 
            mzs.clear();
            while (i<this.features.size() && this.features.get(i).getRTOpt()<=e) {
                // add all the mz in the interval 
                for (Double m: this.features.get(i).getMz()) {
                    if (m >= this.minMz && m <= this.maxMz) {
                        mzs.add(m);
                    }
                }
                i += 1;
            }
            splits = getMzSplits(mzs, this.nMZWindows, this.minMz, this.maxMz);
            System.out.println("Start time, end time, number of features used: "
                    + s + ", " + e + ", " + mzs.size());
            opt.add(new RtMzWindows(s+lagTime, e+lagTime, splits));
            s = e;
            if (j == this.nTimePoints - 1) {
                e = this.optimizedGradient.getEndTime();    
            } else {
                e = s + rtIntervalWidth;
            }
        }
        System.out.println("Done.");
        return opt;
    }
    
    public static List<Double> getList(double[] x) {
         List<Double> l = new ArrayList<>();
         for (int i = 0; i < x.length; ++i) {
             l.add(x[i]);
         }
         return l;
    }
    
    public static List<Float> getList(float[] x) {
         List<Float> l = new ArrayList<>();
         for (int i = 0; i < x.length; ++i) {
             l.add(x[i]);
         }
         return l;
    }
    
    /*
    public static List<NFeaturesRT> genData() {
        List<NFeaturesRT> res = new ArrayList<>();
        
        double [] mz1 = {20, 144};
        res.add(new NFeaturesRT((float)9.0, mz1.length, getList(mz1)));
        
        double [] mz2 = {10, 100, 110, 120, 140};
        res.add(new NFeaturesRT((float)10.0, mz2.length, getList(mz2)));
        
        double [] mz3 = {90, 110, 115, 145, 170};
        res.add(new NFeaturesRT((float)11.0, mz3.length, getList(mz3)));
        
        double [] mz4 = {100, 110, 140, 190, 250};
        res.add(new NFeaturesRT((float)20.0, mz4.length, getList(mz4)));
        
        double [] mz5 = {100, 150, 170};
        res.add(new NFeaturesRT((float)25.0, mz5.length, getList(mz5)));
        
        
        double [] mz6 = {100, 110, 120, 130, 190};
        res.add(new NFeaturesRT((float)30.0, mz6.length, getList(mz6)));
              
        double [] mz7 = {90, 150, 170, 199};
        res.add(new NFeaturesRT((float)35.0, mz7.length, getList(mz7)));
        
        double [] mz8 = {90, 175, 180, 185, 195, 210};
        res.add(new NFeaturesRT((float)40.0, mz8.length, getList(mz8)));
        
        double [] mz9 = {180, 190, 211, 220, 270};
        res.add(new NFeaturesRT((float)50.0, mz9.length, getList(mz9)));
        
        double [] mz10 = {190, 210, 220, 230};
        res.add(new NFeaturesRT((float)51.0, mz10.length, getList(mz10)));
        
        double [] mz11 = {200, 210, 225, 240, 270};
        res.add(new NFeaturesRT((float)60.0, mz11.length, getList(mz11)));      
        
        double [] mz12 = {111, 1111};
        res.add(new NFeaturesRT((float)65.0, mz12.length, getList(mz12)));
        
        return res;
    } 
    
    public static void main(String [] args) {
        // lin and optimized gradients 
        float [] times = {0, 50};
        float [] b = {0, 50};
        GradientFunction lg = new GradientFunction(getList(times), getList(b));
        float [] otimes = {0, 20, 25, 50};
        float [] ob = {0, 20, 30, 50};
        GradientFunction og = new GradientFunction(getList(otimes), getList(ob));
        // small parameters 
        float lagTime = (float) 10.0;
        double m = 100;
        double M = 250;
        int ntimes = 3;
        int nmzWin = 4;
        // the data 
        List<NFeaturesRT> data = genData();       
        MzOptimizer mop = new MzOptimizer(lagTime, lg, og, ntimes, nmzWin, m, M, data);
        List<RtMzWindows> result = mop.getOptimizedMzWindows();
        System.out.println("------------------");
        new Utilities<RtMzWindows>().printList(result);     
    } */
    
    // MS time = LC time + lagTime 
    final private float lagTime;
    // the linear and the optimized gradients (LC time) 
    final private GradientFunction linearGradient;
    final private GradientFunction optimizedGradient;
    // Number of time points and m/z windows 
    final private int nTimePoints;
    final private int nMZWindows;
    // Minimum and maximum m/z 
    final private double minMz;
    final private double maxMz;
    // the MS1-features used to optimize the distribution 
    final List<NFeaturesRT> features;
    
}

