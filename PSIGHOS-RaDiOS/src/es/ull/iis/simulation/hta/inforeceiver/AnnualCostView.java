/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.Arrays;
import java.util.Locale;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * Experiment-level listener for dissagregated annual costs.  
 * @author Iván Castilla Rodríguez
 *
 */
public class AnnualCostView implements ExperimentListener {
	private final int nExperiments;
	private final SecondOrderParamsRepository secParams;
	private final Intervention[] interventions;
	private final int nPatients;
	private final Discount discount;
	private final int minAge;
	private final int maxAge;
	private final double[][][] diseaseCost;
	private final double[][] interventionCost;
	private final double[][] managementCost;
	/** A flag to indicate whether the results have been processed after finishing the experiment */
	private boolean resultsReady;
	
	/**
	 * 
	 */
	public AnnualCostView(int nExperiments, SecondOrderParamsRepository secParams, Discount discount) {
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.length;
		this.discount = discount;
		this.nPatients = secParams.getNPatients();
		this.minAge = secParams.getMinAge();
		this.maxAge = BasicConfigParams.DEF_MAX_AGE;
		diseaseCost = new double[nInterventions][secParams.getRegisteredDiseases().length][maxAge-minAge+1];
		interventionCost = new double[nInterventions][maxAge-minAge+1];
		managementCost = new double[nInterventions][maxAge-minAge+1];
		resultsReady = false;
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(new InnerListenerInstance());
	}

	@Override
	public void notifyEndExperiments() {
		for (int year = 0; year < maxAge-minAge+1; year++) {
			for (int i = 0; i < interventions.length; i++) {
				interventionCost[i][year] /= nExperiments;
				managementCost[i][year] /= nExperiments;
				for (int k = 0; k < secParams.getRegisteredDiseases().length; k++) {
					diseaseCost[i][k][year] /= nExperiments;
				}
			}
		}
		resultsReady = true;
	}
	
	@Override
	public String toString() {
		if (resultsReady) {
			final StringBuilder str = new StringBuilder("Breakdown of costs");
			str.append(System.lineSeparator()).append("Year");
			for (int i = 0; i < interventions.length; i++) {
				final String name = interventions[i].name();
				str.append("\t").append(name).append("-I\t" + name + "-M");
				for (Disease disease : secParams.getRegisteredDiseases()) {
					str.append("\t" + name + "-").append(disease);
				}
			}
			for (int year = 0; year < maxAge-minAge+1; year++) {
				str.append(System.lineSeparator()).append(year);
				for (int i = 0; i < interventions.length; i++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", interventionCost[i][year] /nExperiments));
					str.append("\t").append(String.format(Locale.US, "%.2f", managementCost[i][year] /nExperiments));
					for (int k = 0; k < secParams.getRegisteredDiseases().length; k++) {
						str.append("\t").append(String.format(Locale.US, "%.2f", diseaseCost[i][k][year] / nExperiments));
					}
				}
			}
			return str.toString();
		}
		else {
			return "Annual cost listener: RESULTS NOT READY";
		}
	}
	
	/**
	 * A viewer to show the breakdown of annual costs 
	 * 
	 * @author Iván Castilla
	 *
	 */
	public class InnerListenerInstance extends Listener implements InnerListener {
		private final double[][] diseaseCost;
		private final double[] interventionCost;
		private final double[] managementCost;
		private final double[]lastYear;

		/**
		 * 
		 * @param calc
		 * @param nPatients
		 * @param minAge
		 * @param maxAge
		 */
		public InnerListenerInstance() {
			super("Standard patient viewer");
			this.lastYear = new double[nPatients];
			Arrays.fill(lastYear, 0.0);
			diseaseCost = new double[secParams.getRegisteredDiseases().length][maxAge-minAge+1];
			interventionCost = new double[maxAge-minAge+1];
			managementCost = new double[maxAge-minAge+1];
			addGenerated(PatientInfo.class);
			addEntrance(PatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}
		
		@Override
		public void infoEmited(SimulationInfo info) {
			if (info instanceof SimulationStartStopInfo) {
				final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
				if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
					final long ts = tInfo.getTs();
					final DiseaseProgressionSimulation simul = (DiseaseProgressionSimulation)tInfo.getSimul();
					final TimeUnit simUnit = simul.getTimeUnit();
					for (int i = 0; i < lastYear.length; i++) {
						final Patient pat = (Patient)simul.getGeneratedPatient(i);
						if (!pat.isDead()) {
							final double initYear = lastYear[pat.getIdentifier()]; 
							final double endYear = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
							updateAll(pat, initYear, endYear);
						}
					}
					updateExperiment(simul);
				}
			}
			else if (info instanceof PatientInfo) {
				final PatientInfo pInfo = (PatientInfo) info;
				final Patient pat = pInfo.getPatient();
				final long ts = pInfo.getTs();
				final TimeUnit simUnit = pat.getSimulation().getTimeUnit();
				final double initYear = lastYear[pat.getIdentifier()]; 
				final double endYear = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
				switch(pInfo.getType()) {
				case DIAGNOSIS:
					diseaseCost[pInfo.getManifestation().getDisease().ordinal()][(int) endYear] += pat.getDisease().getStartingCost(pat, endYear, discount);
					break;
				case SCREEN:
					interventionCost[(int) endYear] += pat.getIntervention().getStartingCost(pat, endYear, discount);
					break;
				case START_MANIF:
					diseaseCost[pInfo.getManifestation().getDisease().ordinal()][(int) endYear] += pInfo.getManifestation().getStartingCost(pat, endYear, discount);
				case DEATH:
				case START:
					break;
				default:
					break;
				
				}
				if (!PatientInfo.Type.START.equals(pInfo.getType())) {
					// Update outcomes
					updateAll(pat, initYear, endYear);
				}
				// Update lastTs
				lastYear[pat.getIdentifier()] = endYear;
			}
		}

		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			final int interventionId = simul.getIntervention().ordinal();
			for (int i = 0; i < maxAge-minAge+1; i++) {
				AnnualCostView.this.managementCost[interventionId][i] += managementCost[i] / nPatients;
				AnnualCostView.this.interventionCost[interventionId][i] += interventionCost[i] / nPatients;
				for (int j = 0; j < secParams.getRegisteredDiseases().length; j++) {
					AnnualCostView.this.diseaseCost[interventionId][j][i] += diseaseCost[j][i] / nPatients;
				}
			}			
		}
		private void updateAll(Patient pat, double initYear, double endYear) {
			if (endYear > initYear) {
				update(interventionCost, pat.getIntervention().getAnnualizedCostWithinPeriod(pat, initYear, endYear, discount), initYear);
				update(managementCost, pat.getDisease().getAnnualizedTreatmentAndFollowUpCosts(pat, initYear, endYear, discount), initYear);
				// Assuming each patient may have at most one disease
				if (!pat.isHealthy())
					update(diseaseCost[pat.getDisease().ordinal()], pat.getDisease().getAnnualizedCostWithinPeriod(pat, initYear, endYear, discount), initYear);
			}
		}

		
		/**
		 * Updates the value of this outcome for a specified period
		 * @param values An array with the values to be applied each time interval during the period
		 * @param index The first interval when it will be applied
		 */
		private void update(double[]outcome, double[] values, double initYear) {
			final int index = (int) initYear;
			for (int i = 0; i < values.length; i++) {
				outcome[i + index] += values[i];
			}
		}
		
	}
}
