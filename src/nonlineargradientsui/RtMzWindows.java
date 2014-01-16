package nonlineargradientsui;

import java.util.List;

/**
 * Class to store the optimized m/z windows. Important note: the start and end
 * times should be MS times!
 *
 * @author Luminita Moruz
 */
class RtMzWindows {
    /**
     * Constructor 
     * @param startRt the start retention time
     * @param endRt the end retention time 
     * @param mzs the list of m/z 
     */
    public RtMzWindows(float startRt, float endRt, List<Double> mzs) {

        this.startRt = startRt;
        this.endRt = endRt;
        this.mzSplits = mzs;
    }

    public float getStartRT() {
        return this.startRt;
    }

    public float getEndRT() {
        return this.endRt;
    }

    public List<Double> getSplits() {
        return this.mzSplits;
    }

    @Override
    public String toString() {
        String newline = System.getProperty("line.separator");
        String res = "Start RT = " + this.startRt + newline;
        res += "End RT = " + this.endRt + newline;
        res += "M/z windows = ";
        for (int i = 0; i < this.mzSplits.size() - 1; ++i) {
            res += "[" + this.mzSplits.get(i) + ", " + this.mzSplits.get(i + 1) + "]";
        }
        return res + "\n";
    }
    
    // start and end of RT
    final private float startRt;
    final private float endRt;
    // includes start and end of the the interval
    final private List<Double> mzSplits;
}
