/**
 * 
 */
package es.ull.isaatc.simulation.sequential.test.WFP;

import es.ull.isaatc.simulation.inforeceiver.View;

/**
 * A series of classes to automatically check the correct behavior of the different WF patterns.
 * @author Iván Castilla Rodríguez
 */
public abstract class WFPTestView extends View {
	boolean detailed = true;
	
	public WFPTestView(WFPTestSimulation simul, String description) {
		this(simul, description, true);
	}

	public WFPTestView(WFPTestSimulation simul, String description, boolean detailed) {
		super(simul, description);
		this.detailed = detailed;
	}

	public void notifyResult(boolean ok) {
		if (!ok)
			System.out.println("---------- ERRORS!! Please review your model ----------");
		else
			System.out.println("----------              CORRECT!!            ----------");
	}
}
