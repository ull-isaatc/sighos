/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.engine.SimulationEngine;

/**
 * A class to create patients with Type 1 diabetes mellitus, either from scratch or mimicking a previous set of patients created in a different simulation.
 * All the patients are created at the beginning of the simulation
 * @author Iván Castilla
 *
 */
public class T1DMPatientGenerator extends TimeDrivenGenerator<T1DMPatientGenerator.T1DMPatientGenerationInfo> {
	/** Associated simulation */
	private final T1DMSimulation simul;
	/** Original patients that this generator reproduces */
	private final Patient[] copyOf;
	/** Intervention assigned to all the patients created */
	private final T1DMMonitoringIntervention intervention;

	/**
	 * Creates a patient generator that generates patients from scratch
	 * @param simul Associated simulation
	 * @param nPatients Amount of patients to create
	 * @param intervention Intervention assigned to the patients
	 */
	public T1DMPatientGenerator(T1DMSimulation simul, int nPatients, T1DMMonitoringIntervention intervention) {
		super(simul, nPatients, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = null;
		this.intervention = intervention;
		add(new T1DMPatientGenerationInfo());
	}
	
	/**
	 * Initializes the creator to mimic an original set of patients created in a different simulation and, supposedly, 
	 * affected by a different intervention
	 * @param simul The simulation this creator is attached to
	 * @param copyOf An original set of patients created for a previous simulation (and already simulated)
	 * @param intervention Numerical identifier of the intervention
	 */
	public T1DMPatientGenerator(T1DMSimulation simul, Patient[] copyOf, T1DMMonitoringIntervention intervention) {
		super(simul, copyOf.length, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = copyOf;
		this.intervention = intervention;
		add(new T1DMPatientGenerationInfo());
	}

	@Override
	public EventSource createEventSource(int ind, T1DMPatientGenerationInfo info) {
		T1DMPatient p = null;
		if (copyOf == null) {
			p = new T1DMPatient(simul, intervention);
		}
		else {
			p = new T1DMPatient(simul, (T1DMPatient)copyOf[ind], intervention);
		}
		simul.addGeneratedPatient(p, ind);
		return p;
	}

	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// Nothing to do		
	}	
	
	static class T1DMPatientGenerationInfo extends es.ull.iis.simulation.model.Generator.GenerationInfo {

		protected T1DMPatientGenerationInfo() {
			super(1.0);
		}
		
	}
}
