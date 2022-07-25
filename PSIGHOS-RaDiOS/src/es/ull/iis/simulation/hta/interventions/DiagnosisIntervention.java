/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Named;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.costs.DiagnosisStrategy;
import es.ull.iis.simulation.hta.costs.Strategy;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.params.MultipleRandomSeedPerPatient;
import es.ull.iis.simulation.hta.params.RandomSeedForPatients;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.model.DiscreteEvent;

/**
 * A diagnosis intervention to detect a disease 
 * TODO: Process complex strategies that may produce several events
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class DiagnosisIntervention extends Intervention {
	public final static String STR_DIAGNOSIS = "tDiagnosis";
	public enum ScreeningResult implements Named {
		TP,
		FP,
		TN,
		FN
	}
	private final RandomSeedForPatients[] randomSeeds;
	private final DiagnosisStrategy strategy;
	
	/**
	 * 
	 */
	public DiagnosisIntervention(SecondOrderParamsRepository secParams, String name, String description, DiagnosisStrategy strategy) {
		super(secParams, name, description);
		this.randomSeeds = new RandomSeedForPatients[secParams.getNRuns() + 1];
		Arrays.fill(randomSeeds, null);
		this.strategy = strategy;
	}
	
	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			randomSeeds[id] = new MultipleRandomSeedPerPatient(secParams.getNPatients(), true);
		}
		return randomSeeds[id];
	}
	
	@Override
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		final ArrayList<DiscreteEvent> eventList = new ArrayList<>();
		eventList.add(new DiagnosisEvent(pat.getTs(), pat, strategy));
		return eventList;
	}

	
	public class DiagnosisEvent extends DiscreteEvent {
		private final Patient pat;
		private final Strategy strategyStage;
		
		public DiagnosisEvent(long ts, Patient pat, Strategy strategyStage) {
			super(ts);
			this.pat = pat;
			this.strategyStage = strategyStage;
		}

		@Override
		public void event() {
			final DiseaseProgressionSimulation simul = pat.getSimulation();
			pat.getProfile().addElementToListProperty(STR_DIAGNOSIS, ts);
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
					simul.notifyInfo(new PatientInfo(simul, pat, PatientInfo.Type.DIAGNOSIS, DiagnosisIntervention.this, this.getTs()));
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
