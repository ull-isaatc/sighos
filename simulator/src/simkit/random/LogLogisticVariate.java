/**
 * 
 */
package simkit.random;

/**
 * @author Iv�n
 *
 */
public class LogLogisticVariate extends LogisticVariate {
	public LogLogisticVariate() {
		
	}
	
	public double generate() {
		return Math.exp(super.generate());
	}

}
