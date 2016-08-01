/**
 * 
 */
package es.ull.iis.simulation.retal.info;

import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.retal.Patient;

/**
 * @author Iván Castilla
 *
 */
public class PatientInfo extends AsynchronousInfo {
	/** Possible types of element information */
	public enum Type {
			START ("PATIENT STARTS"),
			EARM1 ("PATIENT WITH EARLY ARM IN FIRST EYE"),
			CNV1 ("PATIENT WITH CNV IN FIRST EYE"),
			GA1 ("PATIENT WITH GA IN FIRST EYE"),
			EARM2 ("PATIENT WITH EARLY ARM IN FELLOW EYE"),
			CNV2 ("PATIENT WITH CNV IN FELLOW EYE"),
			GA2 ("PATIENT WITH GA IN FELLOW EYE"),
			SCREENED ("PATIENT IS SCREENED"),
			DIAGNOSED ("PATIENT IS DIAGNOSED"),
			DEATH ("PATIENT DIES"),
			FINISH ("PATIENT FINISHES");
			
			private final String description;
			
			Type(String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}

		};
	
	final private Patient patient;
	final private Type type;

	/**
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
	}

	/**
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + type.getDescription();
	}

}
