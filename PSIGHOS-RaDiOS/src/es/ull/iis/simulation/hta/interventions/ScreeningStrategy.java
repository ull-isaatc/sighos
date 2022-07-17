/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * A screening intervention to detect a disease 
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ScreeningStrategy extends Intervention {
	protected final static String [] STR_SPECIFICITY = {"Specificity_", "Specificity for "};
	protected final static String [] STR_SENSITIVITY = {"Sensitivity_", "Sensitivity for "};
	public final static String STR_SCREENING = "tScreening";
	public enum ScreeningResult implements Named {
		TP,
		FP,
		TN,
		FN
	}
	private final RandomSeedForPatients[] randomSeeds;
	
	/**
	 * 
	 */
	public ScreeningStrategy(SecondOrderParamsRepository secParams, String name, String description) {
		super(secParams, name, description);
		this.randomSeeds = new RandomSeedForPatients[secParams.getnRuns() + 1];
		Arrays.fill(randomSeeds, null);
	}
	
	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getnPatients(), true);
		}
		return randomSeeds[id];
	}
	
	/**
	 * Returns a string to identify/describe the specificity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the specificity parameter associated to this disease
	 */
	public String getSpecificityParameterString(boolean longText) {
		return longText ? (STR_SPECIFICITY[1] + getDescription()) : (STR_SPECIFICITY[0] + name());
	}
	
	/**
	 * Returns a string to identify/describe the sensitivity parameter associated to this intervention
	 * @param longText If true, returns the description of the parameter; otherwise, returns the identifier
	 * @return a string to identify/describe the sensitivity parameter associated to this disease
	 */
	public String getSensitivityParameterString(boolean longText) {
		return longText ? (STR_SENSITIVITY[1] + getDescription()) : (STR_SENSITIVITY[0] + name());
	}
	
	/**
	 * @return the sensitivity
	 */
	public double getSensitivity(Patient pat) {
		return secParams.getProbParam(getSpecificityParameterString(false), pat.getSimulation());
	}

	/**
	 * @return the specificity
	 */
	public double getSpecificity(Patient pat) {
		return secParams.getProbParam(getSensitivityParameterString(false), pat.getSimulation());
	}
	
	@Override
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		final ArrayList<DiscreteEvent> eventList = new ArrayList<>();
		eventList.add(new ScreeningEvent(pat.getTs(), pat));
		return eventList;
	}

	
	public class ScreeningEvent extends DiscreteEvent {
		final Patient pat;
		
		public ScreeningEvent(long ts, Patient pat) {
			super(ts);
			this.pat = pat;
		}

		@Override
		public void event() {
			final DiseaseProgressionSimulation simul = pat.getSimulation();
			pat.getProfile().addElementToListProperty(STR_SCREENING, ts);
			// If the patient is already diagnosed, no sense in performing screening
			if (!pat.isDiagnosed()) {
				final int id = simul.getIdentifier();
				ScreeningResult result;
				// Healthy patients can be wrongly identified as false positives 
				if (pat.isHealthy()) {
					result = (getRandomSeedForPatients(id).draw(pat) >= getSpecificity(pat)) ? ScreeningResult.FP : ScreeningResult.TN;
				}
				else {
					result = (getRandomSeedForPatients(id).draw(pat) >= getSensitivity(pat)) ? ScreeningResult.FN : ScreeningResult.TP;					
				}
				simul.notifyInfo(new PatientInfo(simul, pat, PatientInfo.Type.SCREEN, result, this.getTs()));
				switch(result) {
				case TP:
					pat.setDiagnosed(true);
				case FP:
					simul.notifyInfo(new PatientInfo(simul, pat, PatientInfo.Type.DIAGNOSIS, ScreeningStrategy.this, this.getTs()));
					break;
				case FN:
				case TN:
				default:
					break;
				
				}
			}
		}
		
	}
}
