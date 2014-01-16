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


public class GradientFigure extends JPanel {


    public GradientFigure(GradientFunction lg, List<Float> optRTs, List<Float> optBs) {
        super();
        this.linearGradient = lg;
        this.optRTs = optRTs;
        this.optBs = optBs;
    }
    
   @Override
   protected void paintComponent(Graphics g) {
         System.out.println("End Bs: " + this.linearGradient.getEndB() +
                " " + this.optBs.get(this.optBs.size()-1));
       
       int YP1,YP2; 
       super.paintComponent(g);
       Graphics2D g2 = (Graphics2D)g;
       int h = getHeight();  
       int w = getWidth();
       Line2D xaxis = new Line2D.Double(DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL,
               w-DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL);
       Line2D yaxis = new Line2D.Double(DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL,
               DISTANCE_FROM_PANEL, DISTANCE_FROM_PANEL);
       
       Line2D arrowY1 = new Line2D.Double(DISTANCE_FROM_PANEL-8, DISTANCE_FROM_PANEL+8,
               DISTANCE_FROM_PANEL, DISTANCE_FROM_PANEL);
       Line2D arrowY2 = new Line2D.Double(DISTANCE_FROM_PANEL+8, DISTANCE_FROM_PANEL+8,
               DISTANCE_FROM_PANEL, DISTANCE_FROM_PANEL);
       
       Line2D arrowX1 = new Line2D.Double(w-DISTANCE_FROM_PANEL-8, h-DISTANCE_FROM_PANEL-8,
               w-DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL);
       Line2D arrowX2 = new Line2D.Double(w-DISTANCE_FROM_PANEL-8, h-DISTANCE_FROM_PANEL+8,
               w-DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL);    
       double startLinearTime = this.linearGradient.getStartTime();
       double startLinearB = this.linearGradient.getStartB();
       double endLinearTime = this.linearGradient.getEndTime();
       double endLinearB = this.linearGradient.getEndB();
       
       Line2D linearGradientLine = new Line2D.Double(DISTANCE_FROM_PANEL, h-DISTANCE_FROM_PANEL,
               w-DISTANCE_FROM_PANEL-25, DISTANCE_FROM_PANEL+25);
       
       // Draw axeX.
       g2.setStroke(new BasicStroke(2)); 
       g2.draw(xaxis); //to make axisX in the middle
       g2.draw(arrowY1); //to make axisX in the middle
       g2.draw(arrowY2); //to make axisX in the middle
       // Draw axeY.
       g2.draw(yaxis);//to make axisY in the middle of the panel
       g2.draw(arrowX1); //to make axisX in the middle
       g2.draw(arrowX2); //to make axisX in the middle
       
       g2.setStroke(new BasicStroke(1,  BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {5}, 0));
       g2.draw(linearGradientLine);
       
       g2.setColor(Color.blue);
       Ellipse2D.Double circle;
       double tt, bb;
       for (int i = 0; i < this.optRTs.size(); ++i) {
           tt = (this.optRTs.get(i) - startLinearTime) / (endLinearTime - startLinearTime) *
                   (w - 2*DISTANCE_FROM_PANEL - 25) + DISTANCE_FROM_PANEL;
           bb = (this.optBs.get(i) - startLinearB) / (endLinearB - startLinearB) *
                   (2*DISTANCE_FROM_PANEL - h + 25) + (h - DISTANCE_FROM_PANEL);
           
           circle = new Ellipse2D.Double(tt, bb, 3, 3);
           g2.fill(circle);
           g2.draw(circle);
           //Point2D P1 = new Point2D.Double(w/2,(h/2)+ UNIT);
           //Point2D P2 = new Point2D.Double((w/2)+ UNIT,(h/2)+ 2*UNIT);  //considering 20 = 1 unit in your syste,
           //g2.draw(new Line2D.Double(P1,P2));
       }
       g2.setColor(Color.black);
       g2.drawString("Retention time (LC Time)", w/4, h - DISTANCE_FROM_PANEL/2);
       AffineTransform fontAT = new AffineTransform();
       Font theFont = g2.getFont();
       fontAT.rotate(-java.lang.Math.PI/2);
       Font theDerivedFont = theFont.deriveFont(fontAT);
       g2.setFont(theDerivedFont);
       g2.drawString("B%", DISTANCE_FROM_PANEL/2+10, h/2);
       g2.setFont(theFont);
    }
   private static final int DISTANCE_FROM_PANEL = 30;
   private static final int UNIT = 20;
   private GradientFunction linearGradient;
   private List<Float> optRTs;
   private List<Float> optBs;
}   
