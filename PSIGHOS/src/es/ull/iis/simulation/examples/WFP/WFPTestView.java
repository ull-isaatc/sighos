/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.inforeceiver.View;

/**
 * A series of classes to automatically check the correct behavior of the different WF patterns.
 * @author Iván Castilla Rodríguez
 */
public abstract class WFPTestView extends View {
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
