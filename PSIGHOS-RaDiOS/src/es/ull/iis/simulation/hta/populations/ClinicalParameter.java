package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;

/**
 * Any extra clinical parameter of the patient
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ClinicalParameter {
	private final String name;
	
	public ClinicalParameter(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the initial value of a clinical parameter for a specific patient 
	 * @param profile The patient's profile, with baseline characteristics
	 * @param simul The current simulation
	 * @return The initial value of a clinical parameter for a specific patient
	 */
	public abstract double getInitialValue(PatientProfile profile, DiseaseProgressionSimulation simul);	
}