/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.Patient;
import simkit.random.RandomVariate;

/**
 * A clinical parameter that is initially set for a patient and will not change during the simulation
 * @author Iván Castilla Rodríguez
 *
 */
public class InitiallySetClinicalParameter extends ClinicalParameter {
	private final RandomVariate firstOrderValue;

	/**
	 * @param name
	 * @param firstOrderValue
	 * @throws MalformedSimulationModelException 
	 */
	public InitiallySetClinicalParameter(String name, RandomVariate firstOrderValue) throws MalformedSimulationModelException {
		super(name);
		this.firstOrderValue = firstOrderValue;
	}
	
	@Override
	public double getInitialValue(Patient pat, DiseaseProgressionSimulation simul) {
		return firstOrderValue.generate();
	}
}
