/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.info;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.CHDComplication;
import es.ull.iis.simulation.hta.T1DM.params.Complication;
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
			HYPO_EVENT ("HYPOVGLYCEMIC EVENT"),
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
	final private Complication complication; 
	final private CHDComplication chdComplication; 

	/**
	 * @param simul
	 * @param patient
	 * @param ts
	 */
	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, Complication complication, CHDComplication chdComplication, long ts) {
		super(simul, ts);
		this.patient = patient;
		this.type = type;
		this.complication = complication;
		this.chdComplication = chdComplication;
	}

	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Type type, long ts) {
		this(simul, patient, type, null, null, ts);
	}

	public T1DMPatientInfo(Simulation simul, T1DMPatient patient, Complication complication, long ts) {
		this(simul, patient, Type.COMPLICATION, complication, Complication.CHD.equals(complication) ? patient.getCHDComplication() : null, ts);
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
	public Complication getComplication() {
		return complication;
	}

	public String toString() {
		String description = type.getDescription();
		if (Type.COMPLICATION.equals(type)) {
			description = description + "\t" + complication;
			if (chdComplication != null)
				description = description + "\t" + chdComplication;
		}
		else if (Type.START.equals(type)) {
			description += "\t" + patient.getHba1c();
		}
		return "" + simul.long2SimulationTime(getTs()) + "\t" + patient.toString() + " \t" + description;
	}
}
