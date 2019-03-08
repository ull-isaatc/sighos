/**
 * 
 */
package es.ull.iis.simulation.test.WFP;

import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A series of classes to automatically check the correct behavior of the different WF patterns.
 * @author Iv�n Castilla Rodr�guez
 */
public abstract class WFPTestView extends Listener {
	boolean detailed = true;
	
	public WFPTestView(String description) {
		this(description, true);
	}

	public WFPTestView(String description, boolean detailed) {
		super(description);
		this.detailed = detailed;
	}

	public void notifyResult(boolean ok) {
		if (!ok)
			System.out.println("---------- ERRORS!! Please review your model ----------");
		else
			System.out.println("----------              CORRECT!!            ----------");
	}
}
