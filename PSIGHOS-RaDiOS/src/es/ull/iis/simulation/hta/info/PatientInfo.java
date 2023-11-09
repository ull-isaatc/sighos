/**
 * 
 */
package es.ull.iis.simulation.hta.info;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Simulation;

/**
 * Simulation piece of information related to the evolution of a patient. Patient {@link DiscreteEvent events} that represent the
 * progression of the disease should emit these pieces of information to be collected by the corresponding {@link InfoReceiver}
 * @author Iván Castilla Rodríguez
 *
 */
public class PatientInfo extends AsynchronousInfo {
	/** Possible types of pieces of information */
	public enum Type {
			START ("PATIENT STARTS"),
			START_MANIF ("MANIFESTATION STARTS"),
			END_MANIF ("MANIFESTATION ENDS"),
			DIAGNOSIS ("PATIENT DIAGNOSED"),
			SCREEN ("PATIENT SCREENED"),
			DEATH ("PATIENT DIES");
			
			private final String description;
			
			Type(String description) {
				this.description = description;
			}

			public String getDescription() {
				return description;
			}

		};
	
	/** The patient */
	final private Patient patient;
	/** Type of information */
	final private Type type;
	/** Description of the acute or chronic manifestation */
	final private Named cause;

	/**
	 * Standard constructor that uses all the parameters
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param type Type of piece of information
	 * @param cause A manifestation or intervention that causes the event
	 * @param ts Simulation time when this piece of information occurs
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, Named cause, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.cause = cause;
	}

	/**
	 * Piece of information non related to acute or chronic complications
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param type Type of piece of information
	 * @param ts Simulation time when this piece of information occurs
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, long ts) {
		this(simul, patient, type, null, ts);
	}

	/**
	 * Piece of information related to the onset of a manifestation
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param progression A disease progression
	 * @param ts Simulation time when this piece of information occurs
	 */
	public PatientInfo(Simulation simul, Patient patient, DiseaseProgression progression, long ts) {
		this(simul, patient, progression, ts, false);
	}

	/**
	 * Piece of information related to the onset or ending of a manifestation
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param progression A disease progression
	 * @param ts Simulation time when this piece of information occurs
	 * @param end True if the piece of information refers to a manifestation that the patient will no longer suffer; false in the case of the onset of the manifestation
	 */
	public PatientInfo(Simulation simul, Patient patient, DiseaseProgression progression, long ts, boolean end) {
		this(simul, patient, end ? Type.END_MANIF : Type.START_MANIF, progression, ts);
	}

	/**
	 * Returns the patient affected by this piece of information
	 * @return the patient
	 */
	public Patient getPatient() {
		return patient;
	}

	/**
	 * Returns the information type
	 * @return the information type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the chronic manifestation stage referenced by this piece of information (null if the piece of information
	 * is not related to a chronic manifestation) 
	 * @return the chronic manifestation
	 */
	public DiseaseProgression getDiseaseProgression() {
		return (DiseaseProgression) cause;
	}

	/**
	 * For DIAGNOSIS and DEATH pieces of information, returns the manifestation that led to that event.
	 * @return the manifestation that led to DEATH or DIAGNOSIS
	 */
	public Named getCause() {
		return cause;
	}
	
	public String toString() {
		String description = type.getDescription();
		switch (type) {
		case START_MANIF:
		case END_MANIF:
		case DIAGNOSIS:
		case SCREEN:
			description = description + "\t" + cause.name();
			break;
		case DEATH:
			if (cause != null)
				description = description + "\t" + cause.name();
			break;
		case START:
			description += "\t" + patient.getAge() + "\t" + patient.getDisease() + "\t" + patient.getIntervention();
			break;
		default:
			break;
		}
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + description;
	}
}
