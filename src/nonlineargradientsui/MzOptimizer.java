package nonlineargradientsui;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import org.apache.commons.lang.ArrayUtils;


/**
 * Class that calculates optimized m/z windows  
 * 
 * @author KJW after an original by Luminita Moruz
 */
public class MzOptimizer {

    /**
     * Constructor 
     * @param lagTime difference between LC time and raw file time 
     * @param linGradient linear gradient 
     * @param optGradient optimized gradient 
     * @param mzSize size of each m/z window 
     * @param nMZWindows number of m/z windows 
     * @param minMz minimum m/z of the features considered 
     * @param maxMz maximum m/z of the features considered 
     * @param features list of features above intensity loaded from the mzml file 
     */
    public MzOptimizer(float lagTime, GradientFunction linGradient, 
            GradientFunction optGradient, float mzSize, int nMZWindows, 
            double minMz, double maxMz, List<NFeaturesRT> features) {
        this.lagTime = lagTime;
        this.linearGradient = linGradient;
        this.optimizedGradient = optGradient;
        this.mzSize = mzSize;
        this.nMZWindows = nMZWindows;
        this.minMz = minMz;
        this.maxMz = maxMz;
        this.features = features;
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
     * Calculate the optimal scheduling of DIA windows 
     * 
     * @return List of optimal scheduling of windows 
     */
    public List<RtMzWindows> getScheduledMzWindows() {
	// consider the "number of time points" as the size of each m/z window instead.
	// simple but ugly solution of this problem.
	float mzSize = this.mzSize;
	int nrOfHypoteticalMzWindows = (int)((this.maxMz - this.minMz)/mzSize) + 1;
	float mzWindowIntervalWidth = mzSize * this.nMZWindows;
	int currentParentId;

        // calculate the retention time of the features according to the optimized gradient 
        fillOptimizedRTs();

        List<RtMzWindows> opt = new ArrayList<>();
	int[] mzs = new int[nrOfHypoteticalMzWindows];
        List<Double> splits = new ArrayList<>();
	List<Integer> tmp;
        // calculate the width of the RT interval 
        
        // sort the features according to their rt in the optimized gradient 
        Collections.sort(this.features, new Comparator<NFeaturesRT>() {
            public int compare(NFeaturesRT f1, NFeaturesRT f2) {
                return f1.getRTOpt().compareTo(f2.getRTOpt());
                //return f1.getRT().compareTo(f2.getRT());
            }
        });
        
        //skip the rts before the start of the gradient 
        int i = 0;
        //while (i<this.features.size() && this.features.get(i).getRT()<this.optimizedGradient.getStartTime()) {
        while (i<this.features.size() && this.features.get(i).getRTOpt()<this.optimizedGradient.getStartTime()) {
            i += 1;
        }

	int alpha=0, nrOfFeaturesNotUsed=i, alpha_val, alpha_prev;
	int size_x = this.features.size() - nrOfFeaturesNotUsed;
	int[][] mzIvals = new int[ size_x ][ nrOfHypoteticalMzWindows ];
	int[][] mzIParents = new int[ size_x ][ nrOfHypoteticalMzWindows ];
	float s, e;
	double currentSplit;

	System.out.println("Begin scheduling m/z-windows with size_x="+size_x+", size_y="+nrOfHypoteticalMzWindows+"...");

        // start calculating the mz windows 
	//while (i<this.features.size() && this.features.get(i).getRT()<this.optimizedGradient.getEndTime()) {
	while (i<this.features.size() && this.features.get(i).getRTOpt()<this.optimizedGradient.getEndTime()) {
            // count all the mzs at this rt point
	    Arrays.fill(mzs,0);
	    for (Double m: this.features.get(i).getMz()) {
		if (m >= this.minMz && m <= this.maxMz) {
                    alpha = (int)((m - this.minMz)/mzSize);
		    //System.out.println("alpha="+alpha);
		    mzs[ alpha ] += 1;
		}
	    }
	    for(int j=0; j<nrOfHypoteticalMzWindows-this.nMZWindows; ++j) {
		mzIvals[ i - nrOfFeaturesNotUsed ][ j ] = 0;
		for(int k=j; k<j+this.nMZWindows; ++k) {
		    mzIvals[ i - nrOfFeaturesNotUsed ][ j ] += mzs[ k ];
		}
		if(i!=nrOfFeaturesNotUsed) {
		    //calculate the maxvalue and it's corresponding parent-"node"
		    alpha=0;
                    currentParentId=0;
		    //for(int k=0; k<nrOfHypoteticalMzWindows-this.nMZWindows; ++k) {
		    for(int k=Math.max(j-1,0); k<Math.min(j+1,nrOfHypoteticalMzWindows-this.nMZWindows); ++k) {
			if(alpha < mzIvals[ i - nrOfFeaturesNotUsed-1 ][ k ]) { 
			    alpha = mzIvals[ i - nrOfFeaturesNotUsed-1 ][ k ];
			    currentParentId = k;
			}
		    }
		    mzIvals[ i - nrOfFeaturesNotUsed ][ j ] += mzIvals[ i - 1 - nrOfFeaturesNotUsed ][ currentParentId ];
		    mzIParents[ i - nrOfFeaturesNotUsed ][ j ] = currentParentId;
		}
	    }
	    i += 1;
	}
	--i;
	tmp = Arrays.asList(ArrayUtils.toObject(mzIvals[i-nrOfFeaturesNotUsed]));
	alpha_val = Collections.max(tmp);
	alpha = tmp.indexOf(alpha_val);
	e=this.features.get(i).getRTOpt(); //.getRT();

	System.out.println("i=" + i + ", nrOfFeaturesNotUsed="+nrOfFeaturesNotUsed+", alpha="+alpha);
	while(--i >= nrOfFeaturesNotUsed) {
	    alpha_prev = alpha;
	    alpha = mzIParents[ i-nrOfFeaturesNotUsed ][ alpha ];
	    if(alpha_prev != alpha || i == nrOfFeaturesNotUsed) {
		// new position was found
		splits.clear();
		for(double j=0; j<=mzWindowIntervalWidth; j+=mzSize) {
		    currentSplit = (double)(j+(alpha_prev*mzSize)+this.minMz);
		    splits.add(currentSplit);
		}
		s=this.features.get(i).getRTOpt(); //.getRT();
		System.out.println("Start time, end time, begin position, prevbeg: " + s + ", " + e + ", " + alpha + ", " + alpha_prev);
		opt.add(new RtMzWindows(s+lagTime, e+lagTime, new ArrayList<Double>(splits)));
		e=s;
	    }
	}
	Collections.reverse(opt);	
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
    } //*/
    
    // MS time = LC time + lagTime 
    final private float lagTime;
    // the linear and the optimized gradients (LC time) 
    final private GradientFunction linearGradient;
    final private GradientFunction optimizedGradient;
    // Number of windowSize and m/z windows 
    final private float mzSize;
    final private int nMZWindows;
    // Minimum and maximum m/z 
    final private double minMz;
    final private double maxMz;
    // the MS1-features used to optimize the distribution 
    final List<NFeaturesRT> features;
    
}

