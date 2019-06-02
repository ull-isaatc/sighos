/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;
import es.ull.iis.simulation.model.EventSource;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeDrivenGenerator;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * A class to create patients with diabetes mellitus, either from scratch or mimicking a previous set of patients created in a different simulation.
 * All the patients are created at the beginning of the simulation
 * @author Iván Castilla
 *
 */
public class DiabetesPatientGenerator extends TimeDrivenGenerator<DiabetesPatientGenerator.DiabetesPatientGenerationInfo> {
	/** Associated simulation */
	private final DiabetesSimulation simul;
	/** Original patients that this generator reproduces */
	private final DiabetesPatient[] copyOf;
	/** Intervention assigned to all the patients created */
	private final DiabetesIntervention intervention;
	private final DiabetesPopulation population;

	/**
	 * Creates a patient generator that generates patients from scratch
	 * @param simul Associated simulation
	 * @param nPatients Amount of patients to create
	 * @param intervention Intervention assigned to the patients
	 */
	public DiabetesPatientGenerator(DiabetesSimulation simul, int nPatients, DiabetesIntervention intervention, DiabetesPopulation population) {
		super(simul, nPatients, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = null;
		this.intervention = intervention;
		this.population = population;
		add(new DiabetesPatientGenerationInfo());
	}
	
	/**
	 * Initializes the creator to mimic an original set of patients created in a different simulation and, supposedly, 
	 * affected by a different intervention
	 * @param simul The simulation this creator is attached to
	 * @param copyOf An original set of patients created for a previous simulation (and already simulated)
	 * @param intervention Numerical identifier of the intervention
	 */
	public DiabetesPatientGenerator(DiabetesSimulation simul, DiabetesPatient[] copyOf, DiabetesIntervention intervention) {
		super(simul, copyOf.length, new SimulationPeriodicCycle(TimeUnit.YEAR, (long)0, new SimulationTimeFunction(TimeUnit.DAY, "ConstantVariate", 365), 1));
		this.simul = simul;
		this.copyOf = copyOf;
		this.intervention = intervention;
		this.population = null;
		add(new DiabetesPatientGenerationInfo());
	}

	@Override
	public EventSource createEventSource(int ind, DiabetesPatientGenerationInfo info) {
		DiabetesPatient p = null;
		if (copyOf == null) {
			p = new DiabetesPatient(simul, intervention, population);
		}
		else {
			p = new DiabetesPatient(simul, copyOf[ind], intervention);
		}
		simul.addGeneratedPatient(p, ind);
		return p;
	}

	protected static class DiabetesPatientGenerationInfo extends es.ull.iis.simulation.model.Generator.GenerationInfo {

		public DiabetesPatientGenerationInfo() {
			super(1.0);
		}
	}
}
