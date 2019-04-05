/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.info;

import es.ull.iis.simulation.hta.T1DM.Named;
import es.ull.iis.simulation.hta.T1DM.T1DMAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.model.DiscreteEvent;
import es.ull.iis.simulation.model.Simulation;

/**
 * Simulation piece of information related to the evolution of a T1DM patient. Patient {@link DiscreteEvent events} that represent the
 * progression of the disease should emit these pieces of information to be collected by the corresponding {@link InfoReceiver}
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMPatientInfo extends AsynchronousInfo {
	/** Possible types of pieces of information */
	public enum Type {
			START ("PATIENT STARTS"),
			COMPLICATION ("COMPLICATION"),
			ACUTE_EVENT ("ACUTE EVENT"),
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
	final private T1DMPatient patient;
	/** Type of information */
	final private Type type;
	/** Description of the acute or chronic complication */
	final private Named complication;

	/**
	 * Standard constructor that uses all the parameters
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param type Type of piece of information
	 * @param complication Chronic complication stage (in case the simulation is reporting a complication-related piece of information)
	 * @param acuteEvent Acute event (in case the simulation is reporting an acute-event-related piece of information)
	 * @param ts Simulation time when this piece of information occurs
	 */
	private T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, Named complication, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.complication = complication;
	}

	/**
	 * Piece of information non related to acute or chronic complications
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param type Type of piece of information
	 * @param ts Simulation time when this piece of information occurs
	 */
	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, long ts) {
		this(simul, patient, type, null, ts);
	}

	/**
	 * Piece of information related to a chronic complication
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param complication Chronic complication stage (in case the simulation is reporting a complication-related piece of information)
	 * @param ts Simulation time when this piece of information occurs
	 */
	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, T1DMComplicationStage complication, long ts) {
		this(simul, patient, Type.COMPLICATION, complication, ts);
	}

	/**
	 * Piece of information related to an acute event 
	 * @param simul Simulation that emits this piece of information 
	 * @param patient A patients
	 * @param acuteEvent Acute event (in case the simulation is reporting an acute-event-related piece of information)
	 * @param ts Simulation time when this piece of information occurs
	 */
	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, T1DMAcuteComplications acuteEvent, long ts) {
		this(simul, patient, Type.ACUTE_EVENT, acuteEvent, ts);		
	}

	/**
	 * Returns the patient affected by this piece of information
	 * @return the patient
	 */
	public T1DMPatient getPatient() {
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
	 * Returns the chronic complication stage referenced by this piece of information (null if the piece of information
	 * is not related to a chronic complication) 
	 * @return the chronic complication
	 */
	public T1DMComplicationStage getComplication() {
		if (complication instanceof T1DMComplicationStage)
			return (T1DMComplicationStage)complication;
		return null;
	}

	/**
	 * Returns the acute complication referenced by this piece of information (null if the piece of information
	 * is not related to an acute complication 
	 * @return the acute event
	 */
	public T1DMAcuteComplications getAcuteEvent() {
		if (complication instanceof T1DMAcuteComplications)
			return (T1DMAcuteComplications)complication;
		return null;
	}

	public String toString() {
		String description = type.getDescription();
		switch (type) {
		case ACUTE_EVENT:
		case COMPLICATION:
			description = description + "\t" + complication.name();
			break;
		case DEATH:
			if (complication != null)
				description = description + "\t" + complication.name();
			break;
		case START:
			description += "\t" + patient.getHba1c();
			break;
		default:
			break;
		}
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + description;
	}
}
