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
 * TODO: Make specificity and sensitivity second-order parameters
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class ScreeningStrategy extends Intervention {
	public final static String STR_SCREENING = "tScreening";
	public enum ScreeningResult implements Named {
		TP,
		FP,
		TN,
		FN
	}
	private final double sensitivity;
	private final double specificity;
	private final RandomSeedForPatients[] randomSeeds;
	
	/**
	 * 
	 */
	public ScreeningStrategy(SecondOrderParamsRepository secParams, String name, String description, double sensitivity, double specificity) {
		super(secParams, name, description);
		this.sensitivity = sensitivity;
		this.specificity = specificity;
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
	 * @return the sensitivity
	 */
	public double getSensitivity() {
		return sensitivity;
	}

	/**
	 * @return the specificity
	 */
	public double getSpecificity() {
		return specificity;
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
					result = (getRandomSeedForPatients(id).draw(pat) >= getSpecificity()) ? ScreeningResult.FP : ScreeningResult.TN;
				}
				else {
					result = (getRandomSeedForPatients(id).draw(pat) >= getSensitivity()) ? ScreeningResult.FN : ScreeningResult.TP;					
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
	
	@Override
	public String prettyPrint(String linePrefix) {
		final StringBuilder str = new StringBuilder(linePrefix).append(super.prettyPrint(linePrefix));
		str.append(linePrefix + "\t").append("Sensitivity: ").append(sensitivity).append(System.lineSeparator());
		str.append(linePrefix + "\t").append("Specificity: ").append(specificity).append(System.lineSeparator());

		return str.toString();
	}
}
