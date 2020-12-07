/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import es.ull.iis.simulation.hta.AcuteComplication;
import es.ull.iis.simulation.hta.ChronicComplication;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention;
import es.ull.iis.simulation.hta.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository.RepositoryInstance;
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
	private final ArrayList<SecondOrderIntervention> interventions;
	private final int nPatients;
	private final Discount discount;
	private final int minAge;
	private final int maxAge;
	private final double[][][] chronicCost;
	private final double[][][] acuteCost;
	private final double[][] interventionCost;
	private final double[][] managementCost;
	
	/**
	 * 
	 */
	public AnnualCostView(int nExperiments, SecondOrderParamsRepository secParams, Discount discount) {
		this.nExperiments = nExperiments;
		this.secParams = secParams;
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.size();
		this.discount = discount;
		this.nPatients = secParams.getnPatients();
		this.minAge = secParams.getMinAge();
		this.maxAge = BasicConfigParams.DEF_MAX_AGE;
		chronicCost = new double[nInterventions][ChronicComplication.values().length][maxAge-minAge+1];
		acuteCost = new double[nInterventions][AcuteComplication.values().length][maxAge-minAge+1];
		interventionCost = new double[nInterventions][maxAge-minAge+1];
		managementCost = new double[nInterventions][maxAge-minAge+1];
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		final RepositoryInstance common = simul.getCommonParams();
		final CostCalculator calc = secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getDiseases(), common.getAcuteCompSubmodels());
		simul.addInfoReceiver(new InnerListenerInstance(calc));
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("Breakdown of costs");
		str.append(System.lineSeparator()).append("Year");
		for (int i = 0; i < interventions.size(); i++) {
			final String name = interventions.get(i).getShortName();
			str.append("\t").append(name).append("-I\t" + name + "-M");
			for (ChronicComplication comp : ChronicComplication.values()) {
				str.append("\t" + name + "-").append(comp);
			}
			for (AcuteComplication comp : AcuteComplication.values()) {
				str.append("\t" + name + "-").append(comp);			
			}
		}
		for (int year = 0; year < maxAge-minAge+1; year++) {
			str.append(System.lineSeparator()).append(year);
			for (int i = 0; i < interventions.size(); i++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", interventionCost[i][year] /nExperiments));
				str.append("\t").append(String.format(Locale.US, "%.2f", managementCost[i][year] /nExperiments));
				for (int k = 0; k < ChronicComplication.values().length; k++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", chronicCost[i][k][year] / nExperiments));
				}
				for (int k = 0; k < AcuteComplication.values().length; k++) {
					str.append("\t").append(String.format(Locale.US, "%.2f", acuteCost[i][k][year] / nExperiments));
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
		private final double[][] chronicCost;
		private final double[][] acuteCost;
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
			chronicCost = new double[ChronicComplication.values().length][maxAge-minAge+1];
			acuteCost = new double[AcuteComplication.values().length][maxAge-minAge+1];
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
				case COMPLICATION:
					chronicCost[pInfo.getComplication().getDisease().getInternalId()][(int) endAge] += discount.applyPunctualDiscount(calc.getCostOfComplication(pat, pInfo.getComplication()), endAge);
				case DEATH:
					// Update outcomes
					updateAll(pat, initAge, endAge);
					break;
				case ACUTE_EVENT:
					acuteCost[pInfo.getAcuteEvent().getInternalId()][(int) endAge] += discount.applyPunctualDiscount(calc.getCostForAcuteEvent(pat, pInfo.getAcuteEvent()), endAge);
					// Update outcomes
					updateAll(pat, initAge, endAge);
					break;
				case START:
					break;
				default:
					break;
				
				}
				// Update lastTs
				lastAge[pat.getIdentifier()] = endAge;
			}
		}

		public synchronized void updateExperiment(DiseaseProgressionSimulation simul) {
			final int interventionId = simul.getIntervention().getIdentifier();
			for (int i = 0; i < maxAge-minAge+1; i++) {
				AnnualCostView.this.managementCost[interventionId][i] += managementCost[i] / nPatients;
				AnnualCostView.this.interventionCost[interventionId][i] += interventionCost[i] / nPatients;
				for (int j = 0; j < ChronicComplication.values().length; j++) {
					AnnualCostView.this.chronicCost[interventionId][j][i] += chronicCost[j][i] / nPatients;
				}
				for (int j = 0; j < AcuteComplication.values().length; j++) {
					AnnualCostView.this.acuteCost[interventionId][j][i] += acuteCost[j][i] / nPatients;
				}
			}			
		}
		private void updateAll(Patient pat, double initAge, double endAge) {
			if (endAge > initAge) {
				update(interventionCost, calc.getAnnualInterventionCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
				update(managementCost, calc.getStdManagementCostWithinPeriod(pat, initAge, endAge), initAge, endAge);
				final double[] complicationPeriodCost = calc.getAnnualChronicComplicationCostWithinPeriod(pat, initAge, endAge);
				for (int i = 0; i < ChronicComplication.values().length; i++) {
					update(chronicCost[i], complicationPeriodCost[i], initAge, endAge);
				}
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
