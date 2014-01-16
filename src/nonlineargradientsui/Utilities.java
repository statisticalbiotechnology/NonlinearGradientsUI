
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
}
