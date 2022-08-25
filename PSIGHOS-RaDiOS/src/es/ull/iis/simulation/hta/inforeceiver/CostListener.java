/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.Discount;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CostListener extends Listener implements StructuredOutputListener {
	private final static String PREFIX = "C_";
	protected final Discount discountRate;
	protected final int nPatients;
	protected double aggregated;
	protected final double[]values;
	protected final long[]lastTs;
	/**
	 * @param description
	 */
	public CostListener(Discount discountRate, int nPatients) {
		super("Cost listener");
		this.discountRate = discountRate;
		this.nPatients = nPatients;
		this.values = new double[nPatients];
		this.lastTs = new long[nPatients];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			final SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) {
				final long ts = tInfo.getTs();
				final DiseaseProgressionSimulation simul = (DiseaseProgressionSimulation)tInfo.getSimul();
				final TimeUnit simUnit = simul.getTimeUnit();
				for (int i = 0; i < lastTs.length; i++) {
					final Patient pat = (Patient)simul.getGeneratedPatient(i);
					if (!pat.isDead()) {
						final double initT = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
						final double endT = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
						if (endT > initT) {
							final double periodCost = pat.getIntervention().getCostWithinPeriod(pat, initT, endT, discountRate) +
									pat.getDisease().getCostWithinPeriod(pat, initT, endT, discountRate);
							update(pat, periodCost);							
						}						
					}
				}
			}
		}
		else if (info instanceof PatientInfo) {
			final PatientInfo pInfo = (PatientInfo) info;
			final Patient pat = pInfo.getPatient();
			final long ts = pInfo.getTs();
			final TimeUnit simUnit = pat.getSimulation().getTimeUnit();
			final double initT = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
			final double endT = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
			// Update lastTs
			lastTs[pat.getIdentifier()] = ts;
			switch(pInfo.getType()) {
			case DIAGNOSIS:
				update(pat, pat.getDisease().getStartingCost(pat, endT, discountRate));
				break;
			case SCREEN:
				update(pat, pat.getIntervention().getStartingCost(pat, endT, discountRate));
				break;
			case START_MANIF:
				update(pat, pInfo.getManifestation().getStartingCost(pat, endT, discountRate));
			case DEATH:
			case START:
				break;
			default:
				break;			
			}
			
			if (!PatientInfo.Type.START.equals(pInfo.getType())) {
				// Update outcomes
				if (endT > initT) {
					final double periodCost = pat.getIntervention().getCostWithinPeriod(pat, initT, endT, discountRate) +
							pat.getDisease().getCostWithinPeriod(pat, initT, endT, discountRate);
					update(pat, periodCost);
				}
			}
		}
	}

	/**
	 * Updates the value of this outcome
	 * @param pat A patient
	 * @param value The value to update
	 * @param age The age at which the value is applied
	 */
	private void update(Patient pat, double value) {
		values[pat.getIdentifier()] += value;
		aggregated += value;
	}
	
	/**
	 * Returns average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5% for each intervention
	 * @return An array with n t-uples {average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5%}, 
	 * with n the number of interventions.  
	 */
	public double[] getResults() {
		final double avg = aggregated / nPatients;
		final double sd = Statistics.stdDev(values, avg);
		final double[] ci = Statistics.normal95CI(avg, sd, nPatients);
		final double[] cip = Statistics.getPercentile95CI(values);
		return new double[] {avg, sd, ci[0], ci[1], cip[0], cip[1]};
	}
	
	public static String getStrHeader(String intervention) {
		final StringBuilder str = new StringBuilder();
		str.append(STR_AVG_PREFIX + PREFIX + intervention + SEP);
		str.append(STR_L95CI_PREFIX + PREFIX + intervention + SEP);
		str.append(STR_U95CI_PREFIX + PREFIX + intervention + SEP);
		return str.toString();
	}
	@Override
	public String toString() {
		final double[] cip = Statistics.getPercentile95CI(values);
		return (aggregated / nPatients) + SEP + cip[0] + SEP + cip[1] + SEP;
	}

	/**
	 * @return the values
	 */
	public double[] getValues() {
		return values;
	}
}
