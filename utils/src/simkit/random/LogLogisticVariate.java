/**
 * 
 */
package simkit.random;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class LogLogisticVariate extends LogisticVariate {
	public LogLogisticVariate() {
		
	}
	
	public double generate() {
		return Math.exp(super.generate());
	}

}
