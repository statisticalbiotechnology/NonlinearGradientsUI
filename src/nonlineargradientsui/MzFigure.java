/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nonlineargradientsui;

/**
 *
 * @author lumi
 */
import javax.swing.JPanel;
import java.awt.Graphics2D;
import java.awt.Graphics;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.awt.geom.AffineTransform;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Collections;


public class MzFigure extends JPanel {
    
    public MzFigure(List<RtMzWindows> optimizedMzWindows) {
        super();
        this.optimizedMzWindows = optimizedMzWindows;
    }
    
   @Override
   protected void paintComponent(Graphics g) {
       //System.out.println("======================");
       //new Utilities<RtMzWindows>().printList(this.optimizedMzWindows);        
 
       super.paintComponent(g);
       Graphics2D g2 = (Graphics2D)g;
       int h = getHeight();  
       int w = getWidth();
       
       /* draw the axes */
       GeneralUtilities.PaintAxes(g2, DISTANCE_FROM_PANEL, h, w);
       /* check if any RT windows */
       if (this.optimizedMzWindows.isEmpty()) {
           return;
       }      
       /* variables needed for scaling the retention times and m/z on the axes */
       float minRT = this.optimizedMzWindows.get(0).getStartRT();
       float maxRT;
       float starty = h - DISTANCE_FROM_PANEL - CST_DIST;
       float endy = DISTANCE_FROM_PANEL + CST_DIST;
       float endx = w - DISTANCE_FROM_PANEL - CST_DIST;
       float startx = DISTANCE_FROM_PANEL + CST_DIST;
       float yinterval = starty - endy;
       float xinterval = endx - startx;
       double minMz, maxMz;
       List<Double> splits;
       Line2D line;
       float stime, etime, y;
       double x;
       
       /* otherwise nothing gets drawn when only one time window */
       if (this.optimizedMzWindows.size() == 1) {
           maxRT = this.optimizedMzWindows.get(0).getEndRT();
       } else {
          maxRT = this.optimizedMzWindows.get(this.optimizedMzWindows.size()-1).getStartRT();
       }             
       /* drawing */
       for (RtMzWindows win: this.optimizedMzWindows) {
           stime = win.getStartRT();
           etime = win.getEndRT();
           
           /* make an horizontal line corresponding to the start time */
           y = starty - (stime - minRT) / (maxRT - minRT)*yinterval;
           g2.setColor(Color.gray); 
           line = new Line2D.Float(startx, y, endx, y);
           g2.draw(line);
           
           /* plot the m/z windows */
           g2.setColor(Color.blue); 
           splits = win.getSplits();
           minMz = Collections.min(splits);
           maxMz = Collections.max(splits);     
           for (Double d:win.getSplits()) {
               x = startx + (d - minMz) / (maxMz - minMz) * xinterval;
               line = new Line2D.Double(x, y+MARKER_SIZE, x, y-MARKER_SIZE);
               g2.draw(line);
           }
       
       }
       /* labels for the axes */
       g2.setColor(Color.black);
       g2.drawString("m/z windows", w/3+20, h - DISTANCE_FROM_PANEL/2);
    }
   
   private static final int DISTANCE_FROM_PANEL = 30;
   private static final int CST_DIST = 20;
   private static final int MARKER_SIZE = 5;
   final private List<RtMzWindows> optimizedMzWindows;
}   
