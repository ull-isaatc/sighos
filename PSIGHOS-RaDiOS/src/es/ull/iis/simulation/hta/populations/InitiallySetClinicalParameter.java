/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;
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
	 */
	public InitiallySetClinicalParameter(String name, RandomVariate firstOrderValue) {
		super(name);
		this.firstOrderValue = firstOrderValue;
	}
	
	public double getInitialValue(PatientProfile profile, DiseaseProgressionSimulation simul) {
		return firstOrderValue.generate();
	}
}
