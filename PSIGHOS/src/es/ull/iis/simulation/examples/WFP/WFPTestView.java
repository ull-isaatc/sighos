/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import es.ull.iis.simulation.inforeceiver.View;
import es.ull.iis.simulation.model.Simulation;

/**
 * A series of classes to automatically check the correct behavior of the different WF patterns.
 * @author Iván Castilla Rodríguez
 */
public abstract class WFPTestView extends View {
	boolean detailed = true;
	
	public WFPTestView(Simulation model, String description) {
		this(model, description, true);
	}

	public WFPTestView(Simulation model, String description, boolean detailed) {
		super(model, description);
		this.detailed = detailed;
	}

	public void notifyResult(boolean ok) {
		if (!ok)
			System.out.println("---------- ERRORS!! Please review your model ----------");
		else
			System.out.println("----------              CORRECT!!            ----------");
	}
}
