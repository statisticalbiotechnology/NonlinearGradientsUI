package nonlineargradientsui;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.*;


/**
 * Class that calculates an optimized gradient 
 * 
 * @author Luminita Moruz
 */
public class GradientCalculator {		
    
    public GradientCalculator() {
        this.linearGradientFunction = null;
        this.lagTime = (float) -1.0;
        this.retentionTimes = null;
        this.stepSize = (float) -1.0;
        this.epsilon = 0.5;
    }
	
    /**
     * Function to set the values of the member variables 
     * @param gradientFunction linear gradient used to generate the RTs to be optimized
     * @param lagTime difference between LC and raw file times 
     * @param retentionTimes the retention times to be optimized
     * @param stepSize how often to sample the optimized gradient 
     */
    public void setVariables(GradientFunction gradientFunction, float lagTime, 
            List<Float> retentionTimes, float stepSize) {
        this.linearGradientFunction = gradientFunction;
        this.lagTime = lagTime;
        this.retentionTimes = retentionTimes;
        this.stepSize = stepSize;
    }
        
    @Override 
    public String toString() {
        StringBuilder result = new StringBuilder();
	String newline = System.getProperty("line.separator");
        result.append("Linear gradient (LC times):" + newline + 
                this.linearGradientFunction.toString() + newline);
	result.append("Lag Time: " + this.lagTime + newline);
	result.append("Step size: " + this.stepSize + newline);
	result.append("Retention times (raw file times): ");
	for (float rt : this.retentionTimes) {
	    result.append(rt + " ");
	}   
	return result.toString();
    }
	
    /**
     * Get the %B corresponding to a certain time by using linear interpolation 
     * @param rawTime time point of interest 
     * @param rawStartGradTime the start of the linear gradient (raw time)
     * @param rawStartGradB the start of the linear gradient (%B)
     * @param rawEndGradTime the end of the linear gradient (raw time)
     * @param rawEndGradB the end of the linear gradient (%B)
     * @return the %B corresponding to rawTime 
     */
    public float getPercentageB(float rawTime, float rawStartGradTime, 
		float rawStartGradB, float rawEndGradTime, float rawEndGradB) {		
        float rtInterval = rawEndGradTime - rawStartGradTime;		
	float pB;		
		
	pB = (((rawTime-rawStartGradTime) * (rawEndGradB-rawStartGradB)) 
                / rtInterval) + rawStartGradB; 
	return pB;
    }
	
    /**
     * Compute the optimized gradient 
     * @return the optimized gradient 
     */
    public GradientFunction computeOptimizedGradient(){
        System.out.println(GeneralUtilities.NEWLINE + 
                    "Calculating optimized gradient ...");
	List<Float> rtsOptimized = new ArrayList<>();
	List<Float> bOptimized = new ArrayList<>();
        
        // get the linear gradient information 
	final float rawStartGradTime = linearGradientFunction.getStartTime() +
                this.lagTime;
	final float rawStartGradB = linearGradientFunction.getStartB();				
	final float rawEndGradTime = linearGradientFunction.getEndTime() + 
		this.lagTime;
	final float rawEndGradB = linearGradientFunction.getEndB();		
				
	// filter out peptides outside the gradient time 
        int nBeforeFiltering = this.retentionTimes.size();
        List<Float> toOptimize = new ArrayList<>();
	for (float rt : this.retentionTimes) {
            if (rt >= rawStartGradTime && rt <= rawEndGradTime)  {
		toOptimize.add(rt);
            }
	}	
        System.out.println("The gradient is based on " + toOptimize.size() + "/" + 
                nBeforeFiltering + " retention times (the ones located in the "
                + "gradient interval)");
                
	// sort the retention times ascending 
	Collections.sort(toOptimize);
	
        // the start point is the same as for the linear gradient  
	rtsOptimized.add(rawStartGradTime);
	bOptimized.add(rawStartGradB);
        
	// compute the number of peptides per time unit 
	Float nTimeUnit = toOptimize.size() / ((rawEndGradTime-rawStartGradTime) / 
			this.stepSize);				
        if (nTimeUnit < 1) {
            return null;
        }
                
	Float i = nTimeUnit-1;
	Float rt = rawStartGradTime + this.stepSize;	
	Float b, prevB, nextB;
	int prevIndex, nextIndex;                
	while (i < toOptimize.size()-1)  {
            if (i == i.intValue()) {
                b = getPercentageB(toOptimize.get(i.intValue()), rawStartGradTime, rawStartGradB, 
                        rawEndGradTime, rawEndGradB);
            } else {
		prevIndex = (int) Math.floor(i);
		prevB = getPercentageB(toOptimize.get(prevIndex), rawStartGradTime, 
			rawStartGradB, rawEndGradTime, rawEndGradB);
		nextIndex = (int) Math.ceil(i);
		nextB = getPercentageB(toOptimize.get(nextIndex), rawStartGradTime, 
                        rawStartGradB, rawEndGradTime, rawEndGradB);
		b = (i-prevIndex)*nextB + (nextIndex-i)*prevB;
                if (b < prevB) {
                    b = prevB;
		}
            }
	    rtsOptimized.add(rt);
	    bOptimized.add(b);
	    rt += this.stepSize;
	    i += nTimeUnit;
	}	
	
        // append the end of the gradient 
        int nRTs = rtsOptimized.size();
        float diff = rawEndGradTime-rtsOptimized.get(nRTs-1);        
        //System.out.println("##### " + rawEndGradTime + "  " + rtsOptimized.get(nRTs-1));
        if (diff < this.stepSize && diff < epsilon) {                
            rtsOptimized.remove(nRTs-1);
            bOptimized.remove(nRTs-1);
        } 
        rtsOptimized.add(rawEndGradTime);
        bOptimized.add(rawEndGradB);		
	
        // convert all the times to LC times
	//System.out.println(rtsOptimized.get(0));
        int j = 0;
	for (float r : rtsOptimized){
            rtsOptimized.set(j, r-this.lagTime);
            j += 1;
	}	
	//System.out.println(rtsOptimized.get(0));

        return new GradientFunction(rtsOptimized, bOptimized);		
    }

    /**
     * Load the retention times to be optimized from a file
     * @param infile Name of the input file 
     * @throws IOException 
     */
    public void loadRTDistribution(String infile) throws IOException{
        this.retentionTimes.clear();
	BufferedReader br = new BufferedReader(new FileReader(infile));
	String line;
	while ((line = br.readLine()) != null) {
	   if (!line.startsWith("#")) {
               this.retentionTimes.add(Float.parseFloat(line.split("\\s+")[1]));			   
	   }
	}
	br.close();
    }

    
    public static void main(String[] args) throws Exception {
        System.out.println("Main");
          
	/*
	double lagTime = 1.0;
	double step = 2.0;
	ArrayList<Double> values = new ArrayList<Double>();
	ArrayList<Double> times = new ArrayList<Double>();
	ArrayList<Double> b = new ArrayList<Double>();
	
	times.add(1.0);
	times.add(119.0);
	b.add(2.0);
	b.add(40.0);
	
	for (double i=0; i<130; i+=0.25) {
            values.add(i);
	}
	GradientFunction gf = new GradientFunction(times, b);		
	GradientDesign gd = new GradientDesign(gf, lagTime, values, step);		
	System.out.println(gd.toString());
		
	GradientFunction g;
	g = gd.computeOptimizedGradient();
	System.out.println(g.toString());
		
	String infile = "/scratch/lumi_work/projects/gradient_design/gradient-design/gradient-design/GradientDesign/examples/linear_gradient.txt";
	GradientFunction g = new GradientFunction();
	g.loadLinearGradient(infile);
		
        double lagTime = 1.5;
	ArrayList<Double> retentionTimes = new ArrayList<Double>();
	GradientCalculator gd = new GradientCalculator(g, lagTime, retentionTimes, 1.5);
	gd.loadRTDistribution("/scratch/lumi_work/projects/gradient_design/gradient-design/gradient-design/GradientDesign/examples/rt_file.txt");
	GradientFunction gg = gd.computeOptimizedGradient();
	gg.printGradientToFile("/scratch/lumi_work/projects/gradient_design/gradient-design/gradient-design/GradientDesign/examples/TMP", 3);
        */
		
    }
        
    // Linear gradient used to produce the distribution to be optimized 
    private GradientFunction linearGradientFunction;
    // Time difference between raw file retention time and LC times
    // rawTime = lcTime + lagTime   
    private float lagTime;
    // The retention time distribution to optimize (raw file times)
    private List<Float> retentionTimes;
    // The step size when calculating the nonlinear gradient 
    private float stepSize;
    // an epsilon to check that the difference between the last rts is not very small 
    // because of a rounding error 
    private final double epsilon;
    
}
