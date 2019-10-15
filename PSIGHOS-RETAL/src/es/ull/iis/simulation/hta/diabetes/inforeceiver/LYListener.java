/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesSimulation;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LYListener extends Listener implements StructuredOutputListener {
	protected final Discount discountRate;
	protected final int nPatients;
	protected double aggregated;
	protected final double[]values;
	protected final long[]lastTs;
	/**
	 * @param description
	 */
	public LYListener(Discount discountRate, int nPatients) {
		super("Life expectancy listener");
		this.discountRate = discountRate;
		this.nPatients = nPatients;
		this.values = new double[nPatients];
		this.lastTs = new long[nPatients];
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
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
				final DiabetesSimulation simul = (DiabetesSimulation)tInfo.getSimul();
				final TimeUnit simUnit = simul.getTimeUnit();
				for (int i = 0; i < lastTs.length; i++) {
					final DiabetesPatient pat = (DiabetesPatient)simul.getGeneratedPatient(i);
					if (!pat.isDead()) {
						final double initAge = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
						final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
						if (endAge > initAge) {
							update(pat, initAge, endAge);							
						}						
					}
				}
			}
		}		
		else if (info instanceof DiabetesPatientInfo) {
			final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
			final DiabetesPatient pat = pInfo.getPatient();
			final long ts = pInfo.getTs();
			final TimeUnit simUnit = pat.getSimulation().getTimeUnit();
			final double initAge = TimeUnit.DAY.convert(lastTs[pat.getIdentifier()], simUnit) / BasicConfigParams.YEAR_CONVERSION; 
			final double endAge = TimeUnit.DAY.convert(ts, simUnit) / BasicConfigParams.YEAR_CONVERSION;
			if (DiabetesPatientInfo.Type.START.equals(pInfo.getType())) {
				lastTs[pat.getIdentifier()] = ts;
			}
			else if (DiabetesPatientInfo.Type.DEATH.equals(pInfo.getType())) {
				// Update outcomes
				if (endAge > initAge) {
					update(pat, initAge, endAge);
				}
			}
		}
	}

	/**
	 * Updates the value of this outcome for a specified period
	 * @param pat A patient
	 * @param initAge Initial age when the value is applied
	 * @param endAge End age when the value is applied
	 */
	private void update(DiabetesPatient pat, double initAge, double endAge) {
		double value = discountRate.applyDiscount(1.0, initAge, endAge);
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
		str.append("AVG_LY_" + intervention + "\t");
		str.append("L95CI_LY_" + intervention + "\t");
		str.append("U95CI_LY_" + intervention + "\t");
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
