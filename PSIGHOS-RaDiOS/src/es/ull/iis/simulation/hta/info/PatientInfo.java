/**
 * 
 */
package es.ull.iis.simulation.hta.info;

import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.progression.Manifestation;
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
			MANIFESTATION ("MANIFESTATION"),
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
	final private Manifestation manifestation;

	/**
	 * Standard constructor that uses all the parameters
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param type Type of piece of information
	 * @param manifestation Chronic manifestation stage (in case the simulation is reporting a manifestation-related piece of information)
	 * @param acuteEvent Acute event (in case the simulation is reporting an acute-event-related piece of information)
	 * @param ts Simulation time when this piece of information occurs
	 */
	public PatientInfo(Simulation simul, Patient patient, Type type, Manifestation manifestation, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.manifestation = manifestation;
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
	 * Piece of information related to a chronic manifestation
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param manifestation Chronic manifestation stage (in case the simulation is reporting a manifestation-related piece of information)
	 * @param ts Simulation time when this piece of information occurs
	 */
	public PatientInfo(Simulation simul, Patient patient, Manifestation complication, long ts) {
		this(simul, patient, Type.MANIFESTATION, complication, ts);
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
	public Manifestation getManifestation() {
		return manifestation;
	}

	public Named getCauseOfDeath() {
		return manifestation;
	}
	public String toString() {
		String description = type.getDescription();
		switch (type) {
		case MANIFESTATION:
			description = description + "\t" + manifestation.name() + "\t" + manifestation.getType();
			break;
		case DEATH:
			if (manifestation != null)
				description = description + "\t" + manifestation.name() + "\t" + manifestation.getType();
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
