/**
 * 
 */
package es.ull.iis.simulation.retal.test;

/**
 * @author Ivan Castilla Rodriguez
 *
 */
public class DiscountTest {

	/**
	 * 
	 */
	public DiscountTest() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Outcome outcome = new Outcome(2, 10, 0.03);
		outcome.update(0, 0, 1, 9.5, 10.5);
		outcome.update(0, 1, 10, 0);
		outcome.update(0, 2, 10, 1);
		outcome.update(0, 3, 10, 10);
		outcome.print(true, false);
	}

}
