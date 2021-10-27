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
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
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
	
	/**
	 * 
	 */
	public AnnualCostView(int nExperiments, SecondOrderParamsRepository secParams, Discount discount) {
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.length;
		this.discount = discount;
		this.nPatients = secParams.getnPatients();
		this.minAge = secParams.getMinAge();
		this.maxAge = BasicConfigParams.DEF_MAX_AGE;
		diseaseCost = new double[nInterventions][secParams.getRegisteredDiseases().length][maxAge-minAge+1];
		interventionCost = new double[nInterventions][maxAge-minAge+1];
		managementCost = new double[nInterventions][maxAge-minAge+1];
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		final CostCalculator calc = secParams.getCostCalculator();
		simul.addInfoReceiver(new InnerListenerInstance(calc));
	}
	
	@Override
	public String toString() {
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
		private final double[]lastAge;
		private final CostCalculator calc;

		/**
		 * 
		 * @param calc
		 * @param nPatients
		 * @param minAge
		 * @param maxAge
		 */
		public InnerListenerInstance(CostCalculator calc) {
			super("Standard patient viewer");
			this.calc = calc;
			this.lastAge = new double[nPatients];
			Arrays.fill(lastAge, 0.0);
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
					for (int i = 0; i < lastAge.length; i++) {
						final Patient pat = (Patient)simul.getGeneratedPatient(i);
						if (!pat.isDead()) {
							final double initAge = lastAge[pat.getIdentifier()]; 
							final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
							updateAll(pat, initAge, endAge);
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
				final double initAge = lastAge[pat.getIdentifier()]; 
				final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
				switch(pInfo.getType()) {
				case DIAGNOSIS:
					diseaseCost[pInfo.getManifestation().getDisease().ordinal()][(int) endAge] += discount.applyPunctualDiscount(pat.getDisease().getDiagnosisCost(pat), endAge);
					break;
				case SCREEN:
					interventionCost[(int) endAge] += discount.applyPunctualDiscount(calc.getCostForIntervention(pat), endAge);
					break;
				case START_MANIF:
					diseaseCost[pInfo.getManifestation().getDisease().ordinal()][(int) endAge] += discount.applyPunctualDiscount(calc.getCostOfManifestation(pat, pInfo.getManifestation()), endAge);
				case DEATH:
				case START:
					break;
				default:
					break;
				
				}
				if (!PatientInfo.Type.START.equals(pInfo.getType())) {
					// Update outcomes
					updateAll(pat, initAge, endAge);
				}
				// Update lastTs
				lastAge[pat.getIdentifier()] = endAge;
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
		private void updateAll(Patient pat, double initAge, double endAge) {
			if (endAge > initAge) {
				update(interventionCost, calc.getAnnualInterventionCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
				update(managementCost, calc.getStdManagementCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
				// Assuming each patient may have at most one disease
				if (!pat.isHealthy())
					update(diseaseCost[pat.getDisease().ordinal()], calc.getAnnualDiseaseCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
			}
		}
		
		/**
		 * Updates the value of this outcome for a specified period
		 * @param value A constant value during the period
		 * @param initAge Initial age when the value is applied
		 * @param endAge End age when the value is applied
		 */
		private void update(double []result, double value, double initAge, double endAge) {
			final int firstInterval = (int)initAge;
			final int lastInterval = (int)endAge;
			if ((int)initAge < (int)endAge)
				result[firstInterval] += discount.applyDiscount(value, initAge, (int)(initAge+1));
//			result[firstInterval] += value * ((int)(initAge+1) - initAge);
			for (int i = firstInterval + 1; i < lastInterval; i++) {
				result[i] += discount.applyDiscount(value, i, i + 1);
			}
			result[lastInterval] += discount.applyDiscount(value, Math.max((int)endAge, initAge), endAge); 
//			result[lastInterval] += value * (endAge - Math.max((int)endAge, initAge)); 
		}	
		
	}
}
