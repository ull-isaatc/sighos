/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.costs.ScreeningStrategy;
import es.ull.iis.simulation.hta.costs.Strategy;
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
public abstract class ScreeningIntervention extends Intervention {
	public final static String STR_SCREENING = "tScreening";
	public enum ScreeningResult implements Named {
		TP,
		FP,
		TN,
		FN
	}
	private final RandomSeedForPatients[] randomSeeds;
	private final ScreeningStrategy strategy;
	
	/**
	 * 
	 */
	public ScreeningIntervention(SecondOrderParamsRepository secParams, String name, String description, ScreeningStrategy strategy) {
		super(secParams, name, description);
		this.randomSeeds = new RandomSeedForPatients[secParams.getnRuns() + 1];
		Arrays.fill(randomSeeds, null);
		this.strategy = strategy;
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
	
	@Override
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		final ArrayList<DiscreteEvent> eventList = new ArrayList<>();
		eventList.add(new ScreeningEvent(pat.getTs(), pat, strategy));
		return eventList;
	}

	
	public class ScreeningEvent extends DiscreteEvent {
		private final Patient pat;
		private final Strategy strategyStage;
		
		public ScreeningEvent(long ts, Patient pat, Strategy strategyStage) {
			super(ts);
			this.pat = pat;
			this.strategyStage = strategyStage;
		}

		@Override
		public void event() {
			final DiseaseProgressionSimulation simul = pat.getSimulation();
			pat.getProfile().addElementToListProperty(STR_SCREENING, ts);
			// If the patient is already diagnosed, no sense in performing screening
			if (!pat.isDiagnosed() && strategyStage.getCondition().check(pat)) {
				final int id = simul.getIdentifier();
				ScreeningResult result;
				// Healthy patients can be wrongly identified as false positives 
				if (pat.isHealthy()) {
					result = (getRandomSeedForPatients(id).draw(pat) >= strategy.getSpecificity(pat)) ? ScreeningResult.FP : ScreeningResult.TN;
				}
				else {
					result = (getRandomSeedForPatients(id).draw(pat) >= strategy.getSensitivity(pat)) ? ScreeningResult.FN : ScreeningResult.TP;					
				}
				simul.notifyInfo(new PatientInfo(simul, pat, PatientInfo.Type.SCREEN, result, this.getTs()));
				switch(result) {
				case TP:
					pat.setDiagnosed(true);
				case FP:
					simul.notifyInfo(new PatientInfo(simul, pat, PatientInfo.Type.DIAGNOSIS, ScreeningIntervention.this, this.getTs()));
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
