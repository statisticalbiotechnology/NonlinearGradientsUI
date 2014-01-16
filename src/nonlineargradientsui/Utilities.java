
package nonlineargradientsui;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Class that provides a few simple utilities 
 * 
 * @author Luminita Moruz
 */
public class Utilities<T> {
   static List<Float> filterListInPlace(List<Float> myList, Float minVal, Float maxVal) {
       Iterator<Float> it = myList.iterator();
       Float tmp;
       while (it.hasNext()) {
           tmp = it.next();
           if (tmp < minVal || tmp > maxVal) {
               it.remove();
           }
       }
       return myList;
   } 
     
   void printList(List<T> myList) {
       System.out.println();
       for (Object xx:myList) {
           System.out.print(xx.toString() + " , ");
        }
        System.out.println();
   }
    
   public List<T> getList(T[] x) {
         List<T> l = new ArrayList<>();
         for (int i = 0; i < x.length; ++i) {
             l.add(x[i]);
         }
         return l;
    }
   
   public static void main(String [] args) {
       Utilities<Float> ut = new Utilities<>();
       List<Float> x = new ArrayList<>();

       x.add((float)14.7);
       x.add((float)10.3);
       x.add((float)10.7);
       x.add((float)11.3);
       x.add((float)11.7);
       x.add((float)10.9);
       x.add((float)10.3);
       x.add((float)12.7);

       ut.printList(x);
       x = filterListInPlace(x, (float)10.5, (float)11.5);
       ut.printList(x);
   }
   
}
