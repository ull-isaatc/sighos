/**
 * 
 */
package es.ull.iis.simulation.retal.info;

import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.model.Simulation;
import es.ull.iis.simulation.retal.EyeState;
import es.ull.iis.simulation.retal.Patient;
import es.ull.iis.simulation.retal.params.CNVStage;

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
			DIABETES ("PATIENT STARTS WITH DIABETES"),
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
	
	public enum ScreeningResult {
		TP,
		FP,
		TN, 
		FN,
		NA		// Not attending
	}
	
	final private Patient patient;
	final private Type type;
	final private int eyeIndex;
	final private EyeState toState;
	final private CNVStage toCNVStage;
	final private ScreeningResult scrResult;

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
		this.toState = null;
		this.toCNVStage = null;
		this.scrResult = null;
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
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, ScreeningResult result, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = Type.SCREENED;
		this.eyeIndex = -1;
		this.toState = null;
		this.toCNVStage = null;
		this.scrResult = result;
	}

	/**
	 * 
	 * @param simul
	 * @param patient
	 * @param toState
	 * @param eyeIndex
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, EyeState toState, int eyeIndex, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = Type.CHANGE_EYE_STATE;
		this.eyeIndex = eyeIndex;
		this.toState = toState;
		this.toCNVStage = null;
		this.scrResult = null;
	}

	/**
	 * 
	 * @param simul
	 * @param patient
	 * @param toState
	 * @param eyeIndex
	 * @param ts
	 */
	public PatientInfo(Simulation simul, Patient patient, CNVStage toCNVStage, int eyeIndex, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = Type.CHANGE_CNV_STAGE;
		this.eyeIndex = eyeIndex;
		this.toState = null;
		this.toCNVStage = toCNVStage;
		this.scrResult = null;
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

	/**
	 * @return the toState
	 */
	public EyeState getToState() {
		return toState;
	}

	/**
	 * @return the toCNVStage
	 */
	public CNVStage getToCNVStage() {
		return toCNVStage;
	}

	/**
	 * @return the screening result
	 */
	public ScreeningResult getScrResult() {
		return scrResult;
	}

	public String toString() {
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + type.getDescription();
	}

}
