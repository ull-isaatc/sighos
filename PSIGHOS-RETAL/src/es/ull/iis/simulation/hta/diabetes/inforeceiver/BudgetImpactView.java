/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;
import java.util.Locale;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository.RepositoryInstance;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BudgetImpactView implements ExperimentListener<BudgetImpactView.InnerInstanceView> {
	private final int nPatients;
	private final int nYears;
	private final SecondOrderParamsRepository secParams;
	private final double[][] cost;
	private final ArrayList<SecondOrderDiabetesIntervention> interventions;

	/**
	 * 
	 */
	public BudgetImpactView(SecondOrderParamsRepository secParams, int nYears) {
		this.interventions = secParams.getRegisteredInterventions();
		final int nInterventions = interventions.size();
		this.nPatients = secParams.getnPatients();
		this.secParams = secParams;
		this.nYears = nYears;
		this.cost = new double[nInterventions][nYears+1];
	}

	@Override
	public void addListener(DiabetesSimulation simul) {
		final RepositoryInstance common = simul.getCommonParams();
		final CostCalculator calc = secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels());
		simul.addInfoReceiver(new InnerInstanceView(calc));
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("Annual costs (for computing budget impact)");
		str.append(System.lineSeparator()).append("Year");
		for (int i = 0; i < interventions.size(); i++) {
			str.append("\t").append(interventions.get(i).getShortName());
		}
		str.append(System.lineSeparator());
		for (int year = 0; year < nYears; year++) {
			str.append(year);
			for (int i = 0; i < interventions.size(); i++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", cost[i][year]));			
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
	public class InnerInstanceView extends Listener implements ExperimentListener.InnerListener {
		private final double[] cost;
		private final CostCalculator calc;
		private final double[]lastAge;
		private boolean finish;
	
		/**
		 * @param simUnit The time unit used within the simulation
		 */
		public InnerInstanceView(CostCalculator calc) {
			super("Budget impact");
			this.calc = calc;
			this.lastAge = new double[nPatients];
			cost = new double[nYears+1];
			finish = false;
			addGenerated(DiabetesPatientInfo.class);
			addEntrance(DiabetesPatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}
	
		@Override
		public void infoEmited(SimulationInfo info) {
			if (!finish) {
				if (info instanceof SimulationStartStopInfo) {
					final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
					if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
						final long ts = tInfo.getTs();
						final DiabetesSimulation simul = (DiabetesSimulation)tInfo.getSimul();
						final TimeUnit simUnit = simul.getTimeUnit();
						final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
						for (int i = 0; i < lastAge.length; i++) {
							final DiabetesPatient pat = (DiabetesPatient)simul.getGeneratedPatient(i);
							if (!pat.isDead()) {
								final double initAge = lastAge[pat.getIdentifier()]; 
								if (endAge > initAge) {
									final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
									update(pat, periodCost, initAge, endAge);							
								}						
							}
						}
						updateExperiment(simul);
					}
				}
				else if (info instanceof DiabetesPatientInfo) {
					final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
					final DiabetesSimulation simul = (DiabetesSimulation)pInfo.getSimul();
					final double endAge = TimeUnit.DAY.convert(pInfo.getTs(), simul.getTimeUnit()) / BasicConfigParams.YEAR_CONVERSION;
					if (endAge > nYears) {
						for (int i = 0; i < nPatients; i++) {
							final DiabetesPatient pat = simul.getGeneratedPatient(i);
							if (!pat.isDead()) {
								final double initAge = lastAge[pat.getIdentifier()]; 
								if (nYears > initAge) {
									final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, nYears);
									update(pat, periodCost, initAge, nYears);
								}
							}
						}
						finish = true;
						updateExperiment(simul);
					}
					else {
						final DiabetesPatient pat = pInfo.getPatient();
						final double initAge = lastAge[pat.getIdentifier()]; 
						switch(pInfo.getType()) {
						case COMPLICATION:
							update(calc.getCostOfComplication(pat, pInfo.getComplication()), endAge);
						case DEATH:
							// Update outcomes
							if (endAge > initAge) {
								final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
								update(pat, periodCost, initAge, endAge);
							}
							break;
						case ACUTE_EVENT:
							update(calc.getCostForAcuteEvent(pat, pInfo.getAcuteEvent()), endAge);
							// Update outcomes
							if (endAge > initAge) {
								final double periodCost = calc.getAnnualCostWithinPeriod(pat, initAge, endAge);
								update(pat, periodCost, initAge, endAge);
							}
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
			}
		}
	
		/**
		 * Updates the value of this outcome for a specified period
		 * @param value A constant value during the period
		 * @param initAge Initial age when the value is applied
		 * @param endAge End age when the value is applied
		 */
		private void update(DiabetesPatient pat, double value, double initAge, double endAge) {
			final int firstInterval = (int)initAge;
			final int lastInterval = (int)endAge;
			if ((int)initAge < (int)endAge)
				cost[firstInterval] += value * ((int)(initAge+1) - initAge);
			for (int i = firstInterval + 1; i < lastInterval; i++) {
				cost[i] += value;
			}
			cost[lastInterval] += value * (endAge - Math.max((int)endAge, initAge)); 
		}
		/**
		 * Updates the value of this outcome at a specified age
		 * @param value The value to update
		 * @param age The age at which the value is applied
		 */
		private void update(double value, double age) {
			cost[(int)age] += value;
		}

		@Override
		public void updateExperiment(DiabetesSimulation simul) {			
			final int interventionId = simul.getIntervention().getIdentifier();
			for (int year = 0; year < cost.length; year++) {
				BudgetImpactView.this.cost[interventionId][year] = cost[year] / nPatients;
			}
		}
		
		
	}
}
