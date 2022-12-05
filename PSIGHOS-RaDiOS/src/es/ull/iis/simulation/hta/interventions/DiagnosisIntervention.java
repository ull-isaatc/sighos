/**
 * 
 */
package es.ull.iis.simulation.hta.interventions;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.Reseteable;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.outcomes.DiagnosisStrategy;
import es.ull.iis.simulation.hta.outcomes.Strategy;
import es.ull.iis.simulation.hta.params.DefinesSensitivityAndSpecificity;
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
public abstract class DiagnosisIntervention extends Intervention implements DefinesSensitivityAndSpecificity, Reseteable {
	public final static String STR_DIAGNOSIS = "tDiagnosis";
	private final RandomSeedForPatients[] randomSeeds;
	private final int nPatients;
	
	/**
	 * 
	 */
	public DiagnosisIntervention(SecondOrderParamsRepository secParams, String name, String description) {
		this(secParams, name, description, null);
	}
	
	/**
	 * 
	 */
	public DiagnosisIntervention(SecondOrderParamsRepository secParams, String name, String description, DiagnosisStrategy strategy) {
		super(secParams, name, description, strategy);
		this.randomSeeds = new RandomSeedForPatients[secParams.getNRuns() + 1];
		Arrays.fill(randomSeeds, null);
		nPatients = secParams.getNPatients();
	}
	
	@Override
	public void reset(int id) {
		randomSeeds[id].reset();
	}
	
	public RandomSeedForPatients getRandomSeedForPatients(int id) {
		if (randomSeeds[id] == null) {
			randomSeeds[id] = new MultipleRandomSeedPerPatient(nPatients, true);
		}
		return randomSeeds[id];
	}
	
	@Override
	public ArrayList<DiscreteEvent> getEvents(Patient pat) {
		final ArrayList<DiscreteEvent> eventList = new ArrayList<>();
		if (getStrategy() == null)
			eventList.add(new DiagnosisEvent(pat.getTs(), pat));
		else
			eventList.add(new StrategyDiagnosisEvent(pat.getTs(), pat, getStrategy()));			
		return eventList;
	}

	
	public class DiagnosisEvent extends DiscreteEvent {
		private final Patient pat;
		
		public DiagnosisEvent(long ts, Patient pat) {
			super(ts);
			this.pat = pat;
		}

		@Override
		public void event() {
			final DiseaseProgressionSimulation simul = pat.getSimulation();
			// If the patient is already diagnosed, no sense in performing screening
			if (!pat.isDiagnosed()) {
				final int id = simul.getIdentifier();
				DetectionTestResult result;
				// Healthy patients can be wrongly identified as false positives 
				if (pat.isHealthy()) {
					result = (getRandomSeedForPatients(id).draw(pat) >= getSpecificity(pat)) ? DetectionTestResult.FP : DetectionTestResult.TN;
				}
				else {
					result = (getRandomSeedForPatients(id).draw(pat) >= getSensitivity(pat)) ? DetectionTestResult.FN : DetectionTestResult.TP;					
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
	
	public class StrategyDiagnosisEvent extends DiscreteEvent {
		private final Patient pat;
		private final Strategy strategyStage;
		
		public StrategyDiagnosisEvent(long ts, Patient pat, Strategy strategyStage) {
			super(ts);
			this.pat = pat;
			this.strategyStage = strategyStage;
		}

		@Override
		public void event() {
			final DiseaseProgressionSimulation simul = pat.getSimulation();
			// If the patient is already diagnosed, no sense in performing screening
			if (!pat.isDiagnosed() && strategyStage.getCondition().check(pat)) {
				final int id = simul.getIdentifier();
				DetectionTestResult result;
				// Healthy patients can be wrongly identified as false positives 
				if (pat.isHealthy()) {
					result = (getRandomSeedForPatients(id).draw(pat) >= ((DiagnosisStrategy)getStrategy()).getSpecificity(pat)) ? DetectionTestResult.FP : DetectionTestResult.TN;
				}
				else {
					result = (getRandomSeedForPatients(id).draw(pat) >= ((DiagnosisStrategy)getStrategy()).getSensitivity(pat)) ? DetectionTestResult.FN : DetectionTestResult.TP;					
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
