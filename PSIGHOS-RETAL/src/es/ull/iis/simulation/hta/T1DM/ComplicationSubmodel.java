/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ComplicationSubmodel {
	protected boolean enable;
	/**
	 * 
	 */
	public ComplicationSubmodel() {
		enable = true;
	}

	public abstract T1DMProgression[] getNextComplication(T1DMPatient pat);
	public abstract void progress(T1DMPatient pat, T1DMProgression prog);
	public abstract int getNSubstates();
	public abstract T1DMHealthState[] getSubstates();
	public abstract void reset();
	
	/**
	 * Returns the health states that this patient has within this complication; null if the patient does not have the complication
	 * @param pat A patient
	 * @return the health states that this patient has within this complication; null if the patient does not have the complication
	 */
	public abstract T1DMHealthState[] hasComplication(T1DMPatient pat);
	
	public void disable() {
		enable = false;
	}
	public interface PatientComplicationState {
		public void progress(T1DMProgression prog);
		public void reset();
	}
	
}
