/**
 * 
 */
package es.ull.iis.simulation.hta;

import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A class to create patients, either from scratch or mimicking a previous set of patients created in a different simulation.
 * All the patients are created at the beginning of the simulation
 * @author Iv�n Castilla
 *
 */
public class PatientGenerator extends TimeDrivenGenerator<PatientGenerator.PatientGenerationInfo> {
	/** Associated simulation */
	private final DiseaseProgressionSimulation simul;
	/** Original patients that this generator reproduces */
	private final Patient[] copyOf;
	/** Intervention assigned to all the patients created */
	private final Intervention intervention;
	private final Population population;

	/**
	 * Creates a patient generator that generates patients from scratch
	 * @param simul Associated simulation
	 * @param nPatients Amount of patients to create
	 * @param intervention Intervention assigned to the patients
	 */
	public PatientGenerator(DiseaseProgressionSimulation simul, int nPatients, Intervention intervention, Population population) {
		super(simul, nPatients, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = null;
		this.intervention = intervention;
		this.population = population;
		add(new PatientGenerationInfo());
	}
	
	/**
	 * Initializes the creator to mimic an original set of patients created in a different simulation and, supposedly, 
	 * affected by a different intervention
	 * @param simul The simulation this creator is attached to
	 * @param copyOf An original set of patients created for a previous simulation (and already simulated)
	 * @param intervention Numerical identifier of the intervention
	 */
	public PatientGenerator(DiseaseProgressionSimulation simul, Patient[] copyOf, Intervention intervention) {
		super(simul, copyOf.length, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = copyOf;
		this.intervention = intervention;
		this.population = null;
		add(new PatientGenerationInfo());
	}

	@Override
	public EventSource createEventSource(int ind, PatientGenerationInfo info) {
		Patient p = null;
		if (copyOf == null) {
			p = new Patient(simul, intervention, population);
		}
		else {
			p = new Patient(simul, copyOf[ind], intervention);
		}
		simul.addGeneratedPatient(p, ind);
		return p;
	}

	protected static class PatientGenerationInfo extends es.ull.iis.simulation.model.Generator.GenerationInfo {

		public PatientGenerationInfo() {
			super(1.0);
		}
	}
}
