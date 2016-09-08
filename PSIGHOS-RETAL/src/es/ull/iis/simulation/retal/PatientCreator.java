/**
 * 
 */
package es.ull.iis.simulation.retal;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.sequential.BasicElement;
import es.ull.iis.simulation.sequential.BasicElementCreator;
import es.ull.iis.simulation.sequential.Generator;

/**
 * A class to create patients, either from scratch or mimicking a previous set of patients created in a different simulation.
 * @author Iván Castilla
 *
 */
public class PatientCreator implements BasicElementCreator {
	/** Number of objects created each time this creator is invoked. */
	private final int nPatients;
	/** Associated simulation */
	private final RETALSimulation simul;
	/** A distribution to characterize the initial age of each generated patient */
	private final TimeFunction initialAges;
	private final Patient[] copyOf;
	private final Intervention intervention;

	/**
	 * @param simul
	 * @param nPatients
	 * @param pMen
	 */
	public PatientCreator(RETALSimulation simul, int nPatients, TimeFunction initialAges, Intervention intervention) {
		this.nPatients = nPatients;
		this.simul = simul;
		this.initialAges = initialAges;
		this.copyOf = null;
		this.intervention = intervention;
	}
	
	/**
	 * Initializes the creator to mimic an original set of patients created in a different simulation and, supposedly, 
	 * affected by a different intervention
	 * @param simul The simulation this creator is attached to
	 * @param copyOf An original set of patients created for a previous simulation (and already simulated)
	 * @param intervention Numerical identifier of the intervention
	 */
	public PatientCreator(RETALSimulation simul, Patient[] copyOf, Intervention intervention) {
		this.nPatients = copyOf.length;
		this.simul = simul;
		this.initialAges = null;
		this.copyOf = copyOf;
		this.intervention = intervention;
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.sequential.BasicElementCreator#create(es.ull.iis.simulation.sequential.Generator)
	 */
	@Override
	public void create(Generator gen) {
		if (copyOf == null) {
			for (int i = 0; i < nPatients; i++) {
				final double age = initialAges.getValue(0);
				Patient p = new Patient(simul, age, intervention);
				simul.addGeneratedPatient(p, i);
				final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
				p.addEvent(ev);
			}
		}
		else {
			for (int i = 0; i < nPatients; i++) {
				Patient p = new Patient(simul, copyOf[i], intervention);
				simul.addGeneratedPatient(p, i);
				final BasicElement.DiscreteEvent ev = p.getStartEvent(simul.getTs());
				p.addEvent(ev);
			}			
		}
	}	
}
