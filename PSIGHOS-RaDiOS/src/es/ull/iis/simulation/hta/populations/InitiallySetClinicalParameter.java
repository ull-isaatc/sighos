/**
 * 
 */
package es.ull.iis.simulation.hta.populations;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.PatientProfile;
import es.ull.iis.simulation.hta.progression.Modification;
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
	
	public double getValue(PatientProfile profile, DiseaseProgressionSimulation simul) {
		final int id = simul.getIdentifier();
		final Modification modif = simul.getIntervention().getClinicalParameterModification(getName());
		double value = firstOrderValue.generate(); 
		switch(modif.getType()) {
		case DIFF:
			value -= modif.getValue(id);
			break;
		case RR:
			value *= modif.getValue(id);
			break;
		case SET:
			value = modif.getValue(id);
			break;
		default:
			break;
		}
		return value;
	}
}
