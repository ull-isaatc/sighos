package es.ull.isaatc.random;

import java.lang.Math;

/************************************************************
 * Triangular is used to generate random variables from
 * the triangular distribution.
 * <br><br>
 * This is a continuous distribution.
 * <br><br>
 * <b>Tests Performed</b><br>
 * 1000 samples were generated and the means and variances
 * were examined.  Subjectively, they seemed correct.
 * A goodness of fit test was performed with 100 samples
 * and 10 intervals.  It succeeded about 19/20 times.
 * <br>
 * @version 1.96
 * @author Juraj Pivovarov
 ************************************************************/

public class Triangular extends RandomNumber {
  double a, b, c;  // left, center, right

    /************************************************************
     * Triangular constructor.  The left, right and center points
     * along the x-axis characterize the triangular distribution.
     *
     * @param left The leftmost endpoint
     *
     * @param center The center point of the triangle (x-axis pos
     * not height.
     *
     * @param right The rightmost endpoint
     ************************************************************/
    
    public Triangular(double left, double center, double right) {
	a = left;
	b = center;
	c = right;

	if( !(left<=center && center <=right) )
	    throw new RuntimeException(); 
    }
    
    // Random number sampling functions
    
    /************************************************************
     * Generate a random double from the triangular distribution.
     *
     * @return A double from the triangular distribution.  The
     * output is gauranteed to be between the left and right
     * endpoints.
     *
     * @see Triangular#Triangular 
     ************************************************************/

    public double sampleDouble() {
        double x = sample01();
        if ( x < (b-a)/(c-a))
          x = Math.sqrt(x*(b-a)*(c-a)) + a;
        else
          x = c- Math.sqrt((1-x)*(c-a)*(c-b));
        return x;
    }

    /************************************************************
     * The sampleInt function should not be called for
     * this continuous distribution.
     ************************************************************/

    public int sampleInt() {
	throw new RuntimeException();
    } 
}
