/**
 * Copyright (c) 2014, by the Authors: Antonio Sanchez (UBC)
 *
 * This software is freely available under a 2-clause BSD license. Please see
 * the LICENSE file in the ArtiSynth distribution directory for details.
 */
package maspack.function;

public interface MIMOFunction {
   void eval(double[] in, double[] out);
   int getInputSize();
   int getOutputSize();
}
