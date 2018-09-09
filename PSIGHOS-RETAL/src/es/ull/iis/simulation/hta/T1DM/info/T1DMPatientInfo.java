/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.info;

import es.ull.iis.simulation.hta.T1DM.MainAcuteComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla
 *
 */
public class T1DMPatientInfo extends AsynchronousInfo {
	/** Possible types of element information */
	public enum Type {
			START ("PATIENT STARTS"),
			COMPLICATION ("COMPLICATION"),
			ACUTE_EVENT ("ACUTE EVENT"),
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
	
	final private T1DMPatient patient;
	final private Type type;
	final private T1DMComorbidity complication;
	final private MainAcuteComplications acuteEvent;

	/**
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, T1DMComorbidity complication, MainAcuteComplications acuteEvent, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.complication = complication;
		this.acuteEvent = acuteEvent;
	}

	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, long ts) {
		this(simul, patient, type, null, null, ts);
	}

	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, T1DMComorbidity complication, long ts) {
		this(simul, patient, Type.COMPLICATION, complication, null, ts);
	}

	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, MainAcuteComplications acuteEvent, long ts) {
		this(simul, patient, Type.ACUTE_EVENT, null, acuteEvent, ts);		
	}
	/**
	 * @return the patient
	 */
	public T1DMPatient getPatient() {
		return patient;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the complication
	 */
	public T1DMComorbidity getComplication() {
		return complication;
	}

	/**
	 * @return the acuteEvent
	 */
	public MainAcuteComplications getAcuteEvent() {
		return acuteEvent;
	}

	public String toString() {
		String description = type.getDescription();
		if (Type.COMPLICATION.equals(type)) {
			description = description + "\t" + complication.name();
		}
		else if (Type.ACUTE_EVENT.equals(type)) {
			description = description + "\t" + acuteEvent.name();
		}
		else if (Type.START.equals(type)) {
			description += "\t" + patient.getHba1c();
		}
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + description;
	}
}
