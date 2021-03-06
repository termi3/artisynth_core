package artisynth.core.inverse;

import java.io.PrintWriter;
import java.io.IOException;
import java.util.Deque;

import artisynth.core.util.ScanToken;
import artisynth.core.modelbase.CompositeComponent;
import maspack.matrix.Vector3d;
import maspack.util.ReaderTokenizer;
import maspack.util.NumberFormat;
import maspack.matrix.VectorNd;
import maspack.matrix.MatrixNd;

/**
 * Constraint term to bound the excitation values generated by the controller,
 * using explicitly set upper and lower bounds.
 * 
 * @author Teun, Ian
 */
public class NonuniformBoundsTerm extends QPConstraintTermBase {

   protected VectorNd myLowerBound = new VectorNd ();
   protected VectorNd myUpperBound = new VectorNd (); 
   
   public NonuniformBoundsTerm() {
      super();
   }
   
   public void setLowerBound(VectorNd lowerBound) {
      myLowerBound.set (lowerBound);
   }
   
   public void setUpperBound(VectorNd upperBound) {
      myUpperBound.set(upperBound);
   }
   
   public void setBounds(VectorNd lowerBound, VectorNd upperBound) {
      myLowerBound.set (lowerBound);
      myUpperBound.set(upperBound);
   }
   
   public void clearBounds() {
      myLowerBound.setSize (0);
      myUpperBound.setSize (0);
   }

   public int getTerm (MatrixNd A, VectorNd b, int rowoff, double t0, double t1) {
      int numex = A.colSize();
      // get lower bounds first, then upper bounds, just to ensure numeric
      // repeatability with legacy code
      int numc = Math.min (myLowerBound.size(), numex);
      for (int i=0; i<numc; i++) {
         for (int j=0; j<numex; j++) {
            if (j == i) {
               A.set (rowoff, j, 1.0); // x >= lb
            }
            else {
               A.set (rowoff, j, 0.0);
            }
         }
         b.set (rowoff++, myLowerBound.get (i));
      }
      numc = Math.min (myUpperBound.size(), numex);
      for (int i=0; i<numc; i++) {
         for (int j=0; j<numex; j++) {
            if (j == i) {
               A.set (rowoff, j, -1.0); // -x >= -ub
            }
            else {
               A.set (rowoff, j, 0.0);
            }
            b.set (rowoff++, -myUpperBound.get (i));
         }
      }
      return rowoff;
   }

   @Override
   public int numConstraints (int qpsize) {
      int result = 0;
      result += Math.min (qpsize, myLowerBound.size());
      result += Math.min (qpsize, myUpperBound.size());
      return result;
   }

   protected void writeItems (
      PrintWriter pw, NumberFormat fmt, CompositeComponent ancestor)
      throws IOException {

      if (myLowerBound.size() > 0) {
         pw.print ("lowerBound=");
         myLowerBound.write (pw, fmt, /*withBrackets=*/true);
         pw.println ("");
      }
      if (myUpperBound.size() > 0) {
         pw.print ("upperBound=");
         myUpperBound.write (pw, fmt, /*withBrackets=*/true);
         pw.println ("");
      }
      super.writeItems (pw, fmt, ancestor);
   }

   protected boolean scanItem (ReaderTokenizer rtok, Deque<ScanToken> tokens)
      throws IOException {

      rtok.nextToken();
      if (scanAttributeName (rtok, "lowerBound")) {
         myLowerBound.scan (rtok);
         return true;
      }
      else if (scanAttributeName (rtok, "upperBound")) {
         myUpperBound.scan (rtok);
         return true;
      }
      rtok.pushBack();
      return super.scanItem (rtok, tokens);
   }
}
