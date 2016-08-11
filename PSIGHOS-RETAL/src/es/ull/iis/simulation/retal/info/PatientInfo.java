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
			CHANGE_EYE_STATE ("PATIENT EYE STATE'S CHANGE"),
			CHANGE_CNV_STAGE ("PATIENT WITH CNV CHANGES STAGE"),
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
	final private int eyeIndex;

	/**
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, int eyeIndex, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.eyeIndex = eyeIndex;
	}

	/**
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, long ts) {
		this(simul, patient, type, -1, ts);
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

	/**
	 * @return the eyeIndex
	 */
	public int getEyeIndex() {
		return eyeIndex;
	}

	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + type.getDescription();
	}

}
