package nonlineargradientsui;


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;


/**
 * Class where several 
 * 
 * @author Luminita Moruz
 */
public class GeneralUtilities {
    
    /**
     * Function to paint the axes of a coordinate system
     * @param g2 - graphics 
     * @param distanceFromPanel - the distance from the margins of the origin 
     * @param height - height of the panel 
     * @param width - width of the panel 
     */
    public static void PaintAxes(Graphics2D g2, int distanceFromPanel, int height, 
            int width) {
       Line2D xaxis = new Line2D.Double(distanceFromPanel, height-distanceFromPanel,
               width-distanceFromPanel, height-distanceFromPanel);       
       Line2D yaxis = new Line2D.Double(distanceFromPanel, height-distanceFromPanel,
               distanceFromPanel, distanceFromPanel);
       
       Line2D arrowY1 = new Line2D.Double(distanceFromPanel-8, distanceFromPanel+8,
               distanceFromPanel, distanceFromPanel);
       Line2D arrowY2 = new Line2D.Double(distanceFromPanel+8, distanceFromPanel+8,
               distanceFromPanel, distanceFromPanel);
       
       Line2D arrowX1 = new Line2D.Double(width-distanceFromPanel-8, height-distanceFromPanel-8,
               width-distanceFromPanel, height-distanceFromPanel);
       Line2D arrowX2 = new Line2D.Double(width-distanceFromPanel-8, height-distanceFromPanel+8,
               width-distanceFromPanel, height-distanceFromPanel);    
       
       // Draw horizontal axis
       g2.setStroke(new BasicStroke(2)); 
       g2.draw(xaxis); //to make axisX in the middle
       g2.draw(arrowY1); //to make axisX in the middle
       g2.draw(arrowY2); //to make axisX in the middle
       // Draw vertical axis
       g2.draw(yaxis);//to make axisY in the middle of the panel
       g2.draw(arrowX1); //to make axisX in the middle
       g2.draw(arrowX2); //to make axisX in the middle
    
    }
    
}
