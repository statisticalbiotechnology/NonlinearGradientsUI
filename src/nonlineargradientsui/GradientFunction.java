package nonlineargradientsui;
import java.util.ArrayList;
import java.util.List;
import java.io.*;
import java.util.Arrays;
import java.text.DecimalFormat;

public class GradientFunction {
	
    // the times and corresponding %B 
	private List<Float> lcTimes;
	private List<Float> percB;

	public GradientFunction() {		
            this.lcTimes = new ArrayList<Float>();
            this.percB = new ArrayList<Float>();
        }
        
        public GradientFunction(List<Float> lcTimes, List<Float> percB) {		
            setVariables(lcTimes, percB);
        }
        
        public void setVariables(List<Float> lcTimes, List<Float> percB) {		
            this.lcTimes = lcTimes;
            this.percB = percB;
	}
        
        public static float interpolateVal(float x, float x1, float x2, float y1, float y2) {
            return (y2-y1)*(x-x1)/(x2-x1) + y1;
        }
        
        /**
         * Assuming a function made of short lines given by the xcoord and y coord, 
         * calculate the interpolated value of x 
         * @param x - the point in which the function should be evaluated 
         * @return the value of the function in x
         */
        public static float interpolateValue(float x, List<Float> xcoord, List<Float> ycoord) {
            if (x < xcoord.get(0) || x > xcoord.get(xcoord.size()-1)) {
                return -1;
            }    
            /* find the closest element smaller than x  */
            int i = 0;
            while (i < xcoord.size() && x > xcoord.get(i) ) {
                i += 1;
            }
            if (i == 0) {
                return ycoord.get(0);
            }
            return interpolateVal(x, xcoord.get(i-1), xcoord.get(i), 
                    ycoord.get(i-1), ycoord.get(i));
        }
        
        
	@Override 
        public String toString() {
            StringBuilder result = new StringBuilder();
	    String newline = System.getProperty("line.separator");	    
	    for (int i=0; i<this.lcTimes.size(); i++) {
	    	result.append(this.lcTimes.get(i) + "\t" + this.percB.get(i) + newline);
	    }
	    return result.toString();
	}
	
	public List<Float> getLcTimes() {
		return this.lcTimes;
	}
	
	public List<Float> getPercB() {
		return this.percB;
	}
	
	public float getStartTime() {
		if (this.lcTimes.size()>0) {
			return this.lcTimes.get(0);
		}
		return 1000;
	}
	
	public float getStartB() {
		if (this.percB.size()>0) {
			return this.percB.get(0);
		}
		return 1000;
	}
	
	public float getEndTime() {
		if (this.lcTimes.size()>1) {
			return this.lcTimes.get(this.lcTimes.size()-1);
		}
		return 1000;
	}
	
	public float getEndB() {
		if (this.percB.size()>1) {
			return this.percB.get(this.percB.size()-1);
		}
		return 1000;
	}
	
	public void loadLinearGradient(String infile) throws IOException {
		// The file should include:
		// start_gradient = 2.0, 2.0
		// end_gradient = 60.0, 35.0
		BufferedReader br = new BufferedReader(new FileReader(infile));
		String line;
		String[] tmp;
		while ((line = br.readLine()) != null) {
		   if (!line.startsWith("#") && line.startsWith("start_gradient")) {
			   tmp = line.split("=")[1].split(",");
			   this.lcTimes.add(0, Float.parseFloat(tmp[0])); 
			   this.percB.add(0, Float.parseFloat(tmp[1]));
			   
		   }
		   if (!line.startsWith("#") && line.startsWith("end_gradient")) {
			   tmp = line.split("=")[1].split(",");
			   this.lcTimes.add(Float.parseFloat(tmp[0])); 
			   this.percB.add(Float.parseFloat(tmp[1]));			   
		   }   
		}
		br.close();
	}
		
	public void printGradientToFile(String outfile, int nDecimals) throws IOException {
		PrintWriter fileHandler = new PrintWriter(new FileWriter(outfile));
		char[] chars = new char[nDecimals];
		Arrays.fill(chars, '0');
		DecimalFormat df = new DecimalFormat("#." + new String(chars));
		
		fileHandler.println("Time(LC time)\tPercentage B");
		for(int i = 0; i < this.lcTimes.size(); i++)
		{
		    fileHandler.println(Double.toString(this.lcTimes.get(i)) + "\t" + 
		    		df.format(this.percB.get(i)));
		}
		fileHandler.close();
	}
        
    public static void main(String [] args) {
        List<Float> rts = new ArrayList<>();
        List<Float> b = new ArrayList<>();
        

        rts.add((float)1);
        rts.add((float)3);
        rts.add((float)4);
        rts.add((float)6);
        b.add((float)1);
        b.add((float)3);
        b.add((float)5);
        b.add((float)6);    
        
        GradientFunction g = new GradientFunction(rts, b);
        System.out.println(g.toString());
        /*
        System.out.println("rt = 1.0, " + getPercB((float)1.0, rts, b));
        System.out.println("rt = 1.5, " + getPercB((float)1.5, rts, b));
        System.out.println("rt = 3.0, " + getPercB((float)3.0, rts, b));
        System.out.println("rt = 3.5, " + getPercB((float)3.5, rts, b));
        System.out.println("rt = 4.5, " + getPercB((float)4.5, rts, b));
        System.out.println("rt = 5.0, " + getPercB((float)5.0, rts, b));
        System.out.println("rt = 5.5, " + getPercB((float)5.5, rts, b));
        System.out.println("rt = 6.0, " + getPercB((float)6.0, rts, b));
        System.out.println("rt = 7.0, " + getPercB((float)7.0, rts, b));
        
        System.out.println("-----------------------------");
        
        System.out.println("b = 1.0, " + getPercB((float)1.0, b, rts));
        System.out.println("b = 1.5, " + getPercB((float)1.5, b, rts));
        System.out.println("b = 3.0, " + getPercB((float)3.0, b, rts));
        System.out.println("b = 4.0, " + getPercB((float)4.0, b, rts));
        System.out.println("b = 5.25, " + getPercB((float)5.25, b, rts));
        System.out.println("b = 5.5, " + getPercB((float)5.5, b, rts));
        System.out.println("b = 5.75, " + getPercB((float)5.75, b, rts));
        System.out.println("b = 6.0, " + getPercB((float)6.0, b, rts));     
        System.out.println("b = 7.0, " + getPercB((float)7.0, b, rts));*/
        
   }
}
