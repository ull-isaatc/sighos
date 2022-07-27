/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.costs.CostCalculator;
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
	private final CostCalculator calc;
	protected final Discount discountRate;
	protected final int nPatients;
	protected double aggregated;
	protected final double[]values;
	protected final long[]lastTs;
	/**
	 * @param description
	 */
	public CostListener(CostCalculator calc, Discount discountRate, int nPatients) {
		super("Cost listener");
		this.calc = calc;
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
						final double initAge = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
						final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
						if (endAge > initAge) {
							final double periodCost = calc.getCostWithinPeriod(pat, initAge, endAge, discountRate);
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
			final double initAge = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
			final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
			// Update lastTs
			lastTs[pat.getIdentifier()] = ts;
			switch(pInfo.getType()) {
			case DIAGNOSIS:
				update(pat, pat.getDisease().getDiagnosisCost(pat, endAge, discountRate));
				break;
			case SCREEN:
				update(pat, calc.getCostForIntervention(pat, endAge, discountRate));
				break;
			case START_MANIF:
				update(pat, calc.getCostUponIncidence(pat, pInfo.getManifestation(), endAge, discountRate));
			case DEATH:
			case START:
				break;
			default:
				break;			
			}
			
			if (!PatientInfo.Type.START.equals(pInfo.getType())) {
				// Update outcomes
				if (endAge > initAge) {
					final double periodCost = calc.getCostWithinPeriod(pat, initAge, endAge, discountRate);
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
		final double[] cip = getPercentile95CI();
		return new double[] {avg, sd, ci[0], ci[1], cip[0], cip[1]};
	}
	
	public static String getStrHeader(String intervention) {
		final StringBuilder str = new StringBuilder();
		str.append("AVG_C_" + intervention + "\t");
		str.append("L95CI_C_" + intervention + "\t");
		str.append("U95CI_C_" + intervention + "\t");
		return str.toString();
	}
	@Override
	public String toString() {
		final double[] cip = getPercentile95CI();
		return (aggregated / nPatients) + "\t" + cip[0] + "\t" + cip[1] + "\t";
	}
	
	private double[] getPercentile95CI() {
		final double[] ordered = Arrays.copyOf(values, nPatients);
		Arrays.sort(ordered);
		final int index = (int)Math.ceil(nPatients * 0.025);
		return new double[] {ordered[index - 1], ordered[nPatients - index]}; 
	}

	/**
	 * @return the values
	 */
	public double[] getValues() {
		return values;
	}
}
