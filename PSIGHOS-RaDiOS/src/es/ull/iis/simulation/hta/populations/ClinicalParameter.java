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
	
	public abstract double getInitialValue(PatientProfile profile, DiseaseProgressionSimulation simul);	
}