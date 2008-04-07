/**
 * 
 */
package simkit.random;

/**
 * @author Iván
 *
 */
public class LogLogisticVariate extends LogisticVariate {
	public LogLogisticVariate() {
		
	}
	
	public double generate() {
		return Math.exp(super.generate());
	}

}
