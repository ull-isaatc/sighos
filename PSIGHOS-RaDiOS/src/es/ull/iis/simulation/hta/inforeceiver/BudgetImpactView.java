/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.Locale;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BudgetImpactView implements ExperimentListener {
	private final int nPatients;
	private final int nYears;
	private final double[][] cost;
	private final Intervention[] interventions;
	private final double coefExperiments;

	/**
	 * 
	 */
	public BudgetImpactView(int nExperiments, HTAModel model, int nYears) {
		this.coefExperiments = 1.0 / (double)nExperiments;
		this.interventions = model.getRegisteredInterventions();
		this.nPatients = model.getExperiment().getNPatients();
		this.nYears = nYears;
		this.cost = new double[interventions.length][nYears+1];
	}

	@Override
	public void addListener(DiseaseProgressionSimulation simul) {
		simul.addInfoReceiver(new InnerInstanceView());
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder("Annual costs (for computing budget impact)");
		str.append(System.lineSeparator()).append("Year");
		for (int i = 0; i < interventions.length; i++) {
			str.append("\t").append(interventions[i].name());
		}
		str.append(System.lineSeparator());
		for (int year = 0; year < nYears; year++) {
			str.append(year);
			for (int i = 0; i < interventions.length; i++) {
				str.append("\t").append(String.format(Locale.US, "%.2f", cost[i][year]));			
			}
			str.append(System.lineSeparator());
		}
		return str.toString();
	}
	
	public class InnerInstanceView extends Listener implements ExperimentListener.InnerListener {
		private final double[] cost;
		private final double[]lastYear;
		private boolean finish;
	
		/**
		 * @param simUnit The time unit used within the simulation
		 */
		public InnerInstanceView() {
			super("Budget impact");
			this.lastYear = new double[nPatients];
			cost = new double[nYears+1];
			finish = false;
			addGenerated(PatientInfo.class);
			addEntrance(PatientInfo.class);
			addEntrance(SimulationStartStopInfo.class);
		}
	
		@Override
		public void infoEmited(SimulationInfo info) {
			if (!finish) {
				if (info instanceof SimulationStartStopInfo) {
					final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
					if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
						final long ts = tInfo.getTs();
						final DiseaseProgressionSimulation simul = (DiseaseProgressionSimulation)tInfo.getSimul();
						final TimeUnit simUnit = simul.getTimeUnit();
						final double endYear = simul.getModel().simulationTimeToYears(TimeUnit.DAY.convert(ts, simUnit));
						for (int i = 0; i < lastYear.length; i++) {
							final Patient pat = (Patient)simul.getGeneratedPatient(i);
							if (!pat.isDead()) {
								final double initYear = lastYear[pat.getIdentifier()]; 
								if (endYear > initYear) {
									update(pat.getDisease().getAnnualizedCostWithinPeriod(pat, initYear, endYear, Discount.ZERO_DISCOUNT), initYear);
									update(pat.getIntervention().getAnnualizedCostWithinPeriod(pat, initYear, endYear, Discount.ZERO_DISCOUNT), initYear);
								}						
							}
						}
						updateExperiment(simul);
					}
				}
				else if (info instanceof PatientInfo) {
					final PatientInfo pInfo = (PatientInfo) info;
					final DiseaseProgressionSimulation simul = (DiseaseProgressionSimulation)pInfo.getSimul();
					final double endYear = simul.getModel().simulationTimeToYears(TimeUnit.DAY.convert(pInfo.getTs(), simul.getTimeUnit()));
					if (endYear > nYears) {
						for (int i = 0; i < nPatients; i++) {
							final Patient pat = simul.getGeneratedPatient(i);
							if (!pat.isDead()) {
								final double initYear = lastYear[pat.getIdentifier()]; 
								if (nYears > initYear) {
									update(pat.getDisease().getAnnualizedCostWithinPeriod(pat, initYear, nYears, Discount.ZERO_DISCOUNT), initYear);
									update(pat.getIntervention().getAnnualizedCostWithinPeriod(pat, initYear, nYears, Discount.ZERO_DISCOUNT), initYear);
								}
							}
						}
						finish = true;
						updateExperiment(simul);
					}
					else {
						final Patient pat = pInfo.getPatient();
						final double initYear = lastYear[pat.getIdentifier()]; 
						switch(pInfo.getType()) {
						case DIAGNOSIS:
							update(pat.getDisease().getStartingCost(pat, endYear, Discount.ZERO_DISCOUNT), endYear);
							break;
						case SCREEN:
							update(pat.getIntervention().getStartingCost(pat, endYear, Discount.ZERO_DISCOUNT), endYear);
							break;
						case START_MANIF:
							update(pInfo.getDiseaseProgression().getStartingCost(pat, endYear, Discount.ZERO_DISCOUNT), endYear);
						case DEATH:
						case START:
							break;
						default:
							break;
						
						}
						if (!PatientInfo.Type.START.equals(pInfo.getType())) {
							// Update outcomes
							if (endYear > initYear) {
								update(pat.getDisease().getAnnualizedCostWithinPeriod(pat, initYear, nYears, Discount.ZERO_DISCOUNT), initYear);
								update(pat.getIntervention().getAnnualizedCostWithinPeriod(pat, initYear, nYears, Discount.ZERO_DISCOUNT), initYear);
							}
						}
						// Update lastYears
						lastYear[pat.getIdentifier()] = endYear;
					}
				}
			}
		}
	
		/**
		 * Updates the value of this outcome for a specified period
		 * @param values An array with the values to be applied each time interval during the period
		 * @param index The first interval when it will be applied
		 */
		private void update(double[] values, double initYear) {
			final int index = (int) initYear;
			for (int i = 0; i < values.length; i++) {
				cost[i + index] += values[i];
			}
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
		public void updateExperiment(DiseaseProgressionSimulation simul) {			
			final int interventionId = simul.getIntervention().ordinal();
			for (int year = 0; year < cost.length; year++) {
				BudgetImpactView.this.cost[interventionId][year] += (cost[year] / nPatients);
			}
		}
		
		
	}

	@Override
	public void notifyEndExperiments() {
		for (int interventionId = 0; interventionId < interventions.length; interventionId++) {
			for (int year = 0; year < cost.length; year++) {
				cost[interventionId][year] *= coefExperiments;
			}
		}
	}
}
