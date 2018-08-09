/**
 * 
 */
package es.ull.iis.simulation.hta.retal;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.SimulationCycle;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A class to create patients, either from scratch or mimicking a previous set of patients created in a different simulation.
 * @author Iván Castilla
 *
 */
public class PatientCreator extends TimeDrivenGenerator<es.ull.iis.simulation.model.Generator.GenerationInfo> {
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
	public PatientCreator(RETALSimulation simul, int nPatients, TimeFunction initialAges, Intervention intervention, SimulationCycle cycle) {
		super(simul, nPatients, cycle);
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
	public PatientCreator(RETALSimulation simul, Patient[] copyOf, Intervention intervention, SimulationCycle cycle) {
		super(simul, copyOf.length, cycle);
		this.simul = simul;
		this.initialAges = null;
		this.copyOf = copyOf;
		this.intervention = intervention;
	}

	@Override
	public EventSource createEventSource(int ind, es.ull.iis.simulation.model.Generator.GenerationInfo info) {
		RetalPatient p = null;
		if (copyOf == null) {
			final double age = initialAges.getValue(null);
			p = new RetalPatient(simul, age, intervention);
		}
		else {
			p = new RetalPatient(simul, (RetalPatient)copyOf[ind], intervention);
		}
		simul.addGeneratedPatient(p, ind);
		return p;
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// Nothing to do		
	}	
}
