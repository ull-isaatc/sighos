/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class InitAgeListener extends Listener implements StructuredOutputListener {
	protected final int nPatients;
	protected double aggregated;
	protected final double[]values;
	/**
	 * @param description
	 */
	public InitAgeListener(int nPatients) {
		super("Initial age listener");
		this.nPatients = nPatients;
		this.values = new double[nPatients];
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof DiabetesPatientInfo) {
			final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
			final DiabetesPatient pat = pInfo.getPatient();
			if (DiabetesPatientInfo.Type.START.equals(pInfo.getType())) {
				values[pat.getIdentifier()] = pat.getInitAge();
				aggregated += pat.getInitAge();
			}
		}
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
		str.append("AVG_INITAGE_" + intervention + "\t");
		str.append("L95CI_INITAGE_" + intervention + "\t");
		str.append("U95CI_INITAGE_" + intervention + "\t");
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
