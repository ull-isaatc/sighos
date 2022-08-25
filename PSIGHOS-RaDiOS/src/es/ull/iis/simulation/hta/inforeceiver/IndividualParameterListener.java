/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.List;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.populations.ClinicalParameter;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class IndividualParameterListener extends Listener implements StructuredOutputListener {
	private final int nPatients;
	private final String [] paramNames;
	private final TreeMap<String, Double> aggregated;
	private final TreeMap<String, double[]> values;
	private double aggregatedInitAge;
	private final double[] initAges;
	
	/**
	 * @param description
	 */
	public IndividualParameterListener(int nPatients, List<ClinicalParameter> paramList) {
		super("Listener for individual parameters");
		this.nPatients = nPatients;
		this.values = new TreeMap<>();
		paramNames = new String[paramList.size()];
		this.aggregated = new TreeMap<>();
		for (int i = 0; i < paramList.size(); i++) {
			final ClinicalParameter param = paramList.get(i);
			paramNames[i] = param.name();
			values.put(paramNames[i], new double[nPatients]);
			aggregated.put(paramNames[i], 0.0);
		}
		initAges = new double[nPatients];
		aggregatedInitAge = 0.0;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof PatientInfo) {
			final PatientInfo pInfo = (PatientInfo) info;
			final Patient pat = pInfo.getPatient();
			if (PatientInfo.Type.START.equals(pInfo.getType())) {
				initAges[pat.getIdentifier()] = pat.getInitAge();
				aggregatedInitAge += initAges[pat.getIdentifier()]; 
				for (String name : paramNames) {
					values.get(name)[pat.getIdentifier()] = (double)pat.getPropertyValue(name);
					aggregated.put(name, aggregated.get(name) + values.get(name)[pat.getIdentifier()]);
				}
			}
		}
	}
	
	/**
	 * Returns average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5% for each intervention
	 * @return An array with n t-uples {average, standard deviation, lower 95%CI, upper 95%CI, percentile 2.5%, percentile 97.5%}, 
	 * with n the number of interventions.  
	 */
	public TreeMap<String, double[]> getResults() {
		final TreeMap<String, double[]> results = new TreeMap<>();
		final double avgAge = aggregatedInitAge / nPatients;
		final double sdAge = Statistics.stdDev(initAges, avgAge);
		final double[] ciAge = Statistics.normal95CI(avgAge, sdAge, nPatients);
		final double[] cipAge = Statistics.getPercentile95CI(initAges);
		results.put("INIT_AGE", new double[] {avgAge, sdAge, ciAge[0], ciAge[1], cipAge[0], cipAge[1]});

		for (String name : paramNames) {
			final double avg = aggregated.get(name) / nPatients;
			final double sd = Statistics.stdDev(values.get(name), avg);
			final double[] ci = Statistics.normal95CI(avg, sd, nPatients);
			final double[] cip = Statistics.getPercentile95CI(values.get(name));
			results.put(name, new double[] {avg, sd, ci[0], ci[1], cip[0], cip[1]});
		}
		return results;
	}
	
	public static String getStrHeader(String intervention, List<ClinicalParameter> paramList) {
		final StringBuilder str = new StringBuilder();
		str.append(STR_AVG_PREFIX + "INITAGE_" + intervention + SEP);
		str.append(STR_L95CI_PREFIX + "INITAGE_" + intervention + SEP);
		str.append(STR_U95CI_PREFIX + "INITAGE_" + intervention + SEP);
		for (ClinicalParameter param : paramList) {
			str.append(STR_AVG_PREFIX + param.name() + "_" + intervention + SEP);
			str.append(STR_L95CI_PREFIX + param.name() + "_" + intervention + SEP);
			str.append(STR_U95CI_PREFIX + param.name() + "_" + intervention + SEP);
		}
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		final double[] cipAge = Statistics.getPercentile95CI(initAges);
		str.append((aggregatedInitAge / nPatients) + SEP + cipAge[0] + SEP + cipAge[1] + SEP);
		for (String name : paramNames) {
			final double[] cip = Statistics.getPercentile95CI(values.get(name));
			str.append((aggregated.get(name) / nPatients) + SEP + cip[0] + SEP + cip[1] + SEP);
		}
		return str.toString();
	}
	
	/**
	 * @return the values
	 */
	public TreeMap<String, double[]> getValues() {
		return values;
	}
	
}
