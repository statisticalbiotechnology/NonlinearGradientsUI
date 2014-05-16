package nonlineargradientsui;

import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.util.List;
import java.util.Collections;
import java.util.HashMap;
import java.awt.geom.AffineTransform;
import java.awt.Font;

/**
 * Panel storing the graphical representation of the DIA windows 
 * 
 * @author Luminita Moruz and KJW
 */

public class MzFigure extends JPanel {

    /**
     * Constructor 
     * @param optimizedMzWindows list of optimized windows to be displayed 
     */
    public MzFigure(List<RtMzWindows> optimizedMzWindows) {
        super();
        this.optimizedMzWindows = optimizedMzWindows;
    }
    
    /**
     * Function to paint the m/z windows 
     * @param g 
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        int h = getHeight();
        int w = getWidth();

        // draw the axes
        GeneralUtilities.PaintAxes(g2, DISTANCE_FROM_PANEL, h, w);
        // check if any RT windows 
        if (this.optimizedMzWindows.isEmpty()) {
            return;
        }
        // variables needed for scaling the retention times and m/z on the axes 
        double minRT = this.optimizedMzWindows.get(0).getStartRT();
        double maxRT;
        double starty = h - DISTANCE_FROM_PANEL - CST_DIST;
        double endy = DISTANCE_FROM_PANEL + CST_DIST;
        double endx = w - DISTANCE_FROM_PANEL - CST_DIST;
        double startx = DISTANCE_FROM_PANEL + CST_DIST;
        double yinterval = starty - endy;
        double xinterval = endx - startx;
        double minMz = Double.MAX_VALUE;
	double maxMz = Double.MIN_VALUE;
        List<Double> splits;
        Line2D line;
        double stime, etime, y=0, prev_y=0, y_step;
        double x, x0, x1;

	maxRT = this.optimizedMzWindows.get(this.optimizedMzWindows.size() - 1).getEndRT();
        
	
	for (RtMzWindows win : this.optimizedMzWindows) {
	    minMz = Math.min(minMz, Collections.min(win.getSplits()));
	    maxMz = Math.max(maxMz, Collections.max(win.getSplits()));
	}
	
	double delta_y = yinterval/(maxMz - minMz);
	double delta_x = xinterval/(maxRT - minRT);
	double mz_s, mz_e;
	HashMap<Double, Double> start_markers = new HashMap();
	HashMap<Double, Double> end_markers = new HashMap();

	// drawing
	g2.setColor(Color.blue);
        for (RtMzWindows win : this.optimizedMzWindows) {
            stime = win.getStartRT();
            etime = win.getEndRT();
            for (Double d : win.getSplits()) {
		prev_y = y;
                y = starty - (d - minMz) * delta_y;
		x0 = startx + (stime-minRT) * delta_x;
		x1 = startx + (etime-minRT) * delta_x;
		line = new Line2D.Double(x0, y, x1, y);
                g2.draw(line);
		try{
		    x = start_markers.put(d,x0);
		}catch(NullPointerException npe) {
		    x = Double.MAX_VALUE;
		}
		start_markers.put(d, Math.min(x, x0));
		try{
		    x = end_markers.put(d,x1);
		}catch(NullPointerException npe) {
		    x = Double.MIN_VALUE;
		}
		end_markers.put(d, Math.max(x, x1));
            }
        }
	y_step = (y-prev_y)/2;
	g2.setColor(Color.gray);
        for (RtMzWindows win : this.optimizedMzWindows) {
	    for (Double d : win.getSplits()) {
                y = starty - (d - minMz) * delta_y;
		x0 = start_markers.get(d);
		x1 = end_markers.get(d);
                line = new Line2D.Double(x0, y+y_step, x0, y-y_step);
		g2.draw(line);
		line = new Line2D.Double(x1, y+y_step, x1, y-y_step);
		g2.draw(line);
	    }
	}

        // labels for the axes
        g2.setColor(Color.black);
        g2.drawString("Retention time (LC time)", w / 4, h - DISTANCE_FROM_PANEL / 2);
	AffineTransform fontAT = new AffineTransform();
	Font theFont = g2.getFont();
	fontAT.rotate(-java.lang.Math.PI/2);
	Font theDerivedFont = theFont.deriveFont(fontAT);
	g2.setFont(theDerivedFont);
	g2.drawString("m/z", DISTANCE_FROM_PANEL/2+10, h/2);
	g2.setFont(theFont);
    }

    // distance of the axes from the panel 
    private static final int DISTANCE_FROM_PANEL = 30;
    // some constant distance from the axes of the lines 
    private static final int CST_DIST = 20;
    // size of the markers 
    private static final int MARKER_SIZE = 5;
    // list of m/z windows to be drawn
    final private List<RtMzWindows> optimizedMzWindows;
}
