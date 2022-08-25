/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.ArrayList;
import java.util.TreeMap;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.Intervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * A listener to capture the time until a chronic manifestation onsets. Time can be 0 (if the patient starts with the manifestation),
 * Long.MAX_VALUE if the simulation finishes and the patient never developed the manifestation, or any positive value among both extremes
 * (it is expected to be always inferior to the maximum age of the patient)
 * @author Iván Castilla
 *
 */
public class TimeFreeOfComplicationsView extends Listener implements StructuredOutputListener {
	private final static String STR_AVG = "AVG_";
	private final static String STR_LCI = "L95CI_";
	private final static String STR_UCI = "U95CI_";
	private final static String STR_AVG_TIME = STR_AVG + "TIME_TO_";
	private final static String STR_LCI_TIME = STR_LCI + "TIME_TO_";
	private final static String STR_UCI_TIME = STR_UCI + "TIME_TO_";
	private final static String STR_INC = "INC_";
	private final static String STR_PREV = "PREV_";
	
	/** Inner structure to store time to chronic manifestations. For each mapped manifestation contains an array with t-uples <intervention, patient> */
	private final TreeMap<Manifestation, long[][]> timeToEvents;
	/** Inner structure to store number of acute events per patient. For each mapped manifestation contains an array with t-uples <intervention, patient> */
	private final TreeMap<Manifestation, int[][]> nEvents;
	/** For each intervention, number of patients that develop each chronic manifestation, including
	 * those who started the simulated with such manifestation */  
	private final int [][] prevalence;
	/** For each intervention, number of patients that develop each chronic manifestation during their 
	 * simulated lifetime */  
	private final int [][] incidence;
	/** Enables printing the confidence intervals for first order simulations */
	private final boolean printFirstOrderVariance;
	/** Number of interventions assessed */
	private final int nInterventions;
	/** Number of patients simulated */
	private final int nPatients;
	/** Available chronic manifestations in the simulation */
	private final Manifestation[] availableManifestations;

	/**
	 * 
	 * @param secParams Main repository for the simulations
	 * @param printFirstOrderVariance Enables printing the confidence intervals for first order simulations
	 */
	public TimeFreeOfComplicationsView(SecondOrderParamsRepository secParams, boolean printFirstOrderVariance) {
		super("Standard patient viewer");
		this.nInterventions = secParams.getNInterventions();
		this.nPatients = secParams.getNPatients();
		this.availableManifestations = secParams.getRegisteredManifestations();
		prevalence = new int[nInterventions][availableManifestations.length];
		incidence = new int[nInterventions][availableManifestations.length];
		timeToEvents = new TreeMap<>();
		nEvents = new TreeMap<>();
		for (Manifestation manif : availableManifestations) {
			if (Manifestation.Type.CHRONIC.equals(manif.getType()))
				timeToEvents.put(manif, new long[nInterventions][nPatients]);
			else
				nEvents.put(manif, new int[nInterventions][nPatients]);
		}
		this.printFirstOrderVariance = printFirstOrderVariance;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	/**
	 * Generates a string with the header required to print the information collected by this listener in a line 
	 * @param printFirstOrderVariance Enables printing the confidence intervals for first order simulations
	 * @param interventions The interventions analyzed
	 * @param availableManifestations Chronic manifestations included
	 * @return
	 */
	public static String getStrHeader(boolean printFirstOrderVariance, SecondOrderParamsRepository secParams) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (Intervention inter : secParams.getRegisteredInterventions()) {
				for (Manifestation manif : secParams.getRegisteredManifestations()) {
					final String suf = manif.name() + "_" + inter.name() + SEP;
					if (Manifestation.Type.CHRONIC.equals(manif.getType())) {
						str.append(STR_INC).append(suf).append(STR_PREV).append(suf).append(STR_AVG_TIME).append(suf).append(STR_LCI_TIME).append(suf).append(STR_UCI_TIME).append(suf);
					}
					else {
						str.append(STR_AVG).append(suf).append(STR_LCI).append(suf).append(STR_UCI).append(suf);
					}
				}			
			}
		}
		else {
			for (Intervention inter : secParams.getRegisteredInterventions()) {
				for (Manifestation manif : secParams.getRegisteredManifestations()) {
					final String suf = manif.name() + "_" + inter.name() + SEP;
					if (Manifestation.Type.CHRONIC.equals(manif.getType())) {
						str.append(STR_INC).append(suf).append(STR_PREV).append(suf).append(STR_AVG_TIME).append(suf);
					}
					else {
						str.append(STR_AVG).append(suf);
					}
				}
			}
		}
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableManifestations.length; j++) {
				if (Manifestation.Type.CHRONIC.equals(availableManifestations[j].getType())) {
					str.append(incidence[i][j]).append(SEP).append(prevalence[i][j]).append(SEP);
					final ArrayList<Long> validValues = getValidValues(timeToEvents.get(availableManifestations[j])[i]);
					if (validValues.size() == 0) {
						str.append(Double.NaN).append(SEP);						
						if (printFirstOrderVariance)
							str.append(Double.NaN).append(SEP).append(Double.NaN).append(SEP);						
					}
					else {
						final double avg = Statistics.average(validValues);
						str.append(avg / BasicConfigParams.YEAR_CONVERSION).append(SEP);
						if (printFirstOrderVariance) {
							final double[] ci = Statistics.normal95CI(avg, Statistics.stdDev(validValues, avg), validValues.size());
							str.append(ci[0] /BasicConfigParams.YEAR_CONVERSION).append(SEP).append(ci[1]/BasicConfigParams.YEAR_CONVERSION).append(SEP);
						}
					}
				}
				else {
					str.append((double)incidence[i][j] / nPatients).append(SEP);
					if (printFirstOrderVariance) {
						final int[] cip = Statistics.getPercentile95CI(nEvents.get(availableManifestations[j])[i]);
						str.append(cip[0]).append(SEP).append(cip[1]).append(SEP);
					}
				}
			}
		}
		return str.toString();
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		final PatientInfo pInfo = (PatientInfo) info;
		final Patient pat = pInfo.getPatient();
		final int nIntervention = pat.getnIntervention(); 
		if (pInfo.getType() == PatientInfo.Type.DEATH) {
			// Check all the complications
			for (int i = 0; i < availableManifestations.length; i++) {
				final Manifestation manif = availableManifestations[i];
				final long time = pat.getTimeToManifestation(manif);
				if (Manifestation.Type.CHRONIC.equals(manif.getType())) {
					timeToEvents.get(manif)[nIntervention][pat.getIdentifier()] = time;
					if (time == 0) {
						prevalence[nIntervention][i]++;
					}
					else if (Long.MAX_VALUE != time){
						prevalence[nIntervention][i]++;
						incidence[nIntervention][i]++;
					}
				}
				else {
					nEvents.get(manif)[nIntervention][pat.getIdentifier()] = pat.getNManifestations(manif);
					incidence[nIntervention][i] += nEvents.get(manif)[nIntervention][pat.getIdentifier()];
					
				}
			}
		}
	}
	
	public double[][] getIncidence() {
		final double[][] percIncidence = new double[nInterventions][availableManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableManifestations.length; j++) {
				percIncidence[i][j] = (double) incidence[i][j] / (double)nPatients;
			}
		}
		return percIncidence;
	}
	
	public double[][] getPrevalence() {
		final double[][] percPrevalence = new double[nInterventions][availableManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableManifestations.length; j++) {
				percPrevalence[i][j] = (double) prevalence[i][j] / (double)nPatients;
			}
		}
		return percPrevalence;
	}
	
	/**
	 * Returns a list with those values corresponding to valid times to an event, i.e., different to 0 and Long.MAX_VALUE 
	 * @param values Original times to event 
	 * @return  a list containing only those values corresponding to valid times to an event
	 */
	private ArrayList<Long> getValidValues(long[] values) {
		final ArrayList<Long> validValues = new ArrayList<>();
		for (long val : values) {
			if (Long.MAX_VALUE != val && val != 0) {
				validValues.add(val);
			}
		}
		return validValues;
	}
	
	/**
	 * Returns the average time to develop each chronic manifestation for each intervention
	 * @return the average time to develop each chronic manifestation for each intervention
	 */
	public double[][] getAvgTimeToComplications() {
		final double[][] results = new double[nInterventions][availableManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableManifestations.length; j++) {				
				final ArrayList<Long> validValues = getValidValues(timeToEvents.get(availableManifestations[j])[i]);
				if (validValues.size() == 0) {
					results[i][j] = Double.NaN;
				}
				else {
					final double avg = Statistics.average(validValues);
					results[i][j] = avg /BasicConfigParams.YEAR_CONVERSION;
				}
			}
		}
		return results;
	}

	/**
	 * Returns the time until a specified patient has developed each complication 
	 * @param patient A patient
	 * @return the time until a specified patient has developed each complication
	 */
	public double[][] getTimeToComplications(int patient) {
		final double[][] results = new double[nInterventions][availableManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < results[i].length; j++) {
				final long val = timeToEvents.get(availableManifestations[j])[i][patient];
				if (Long.MAX_VALUE == val || val == 0)
					results[i][j] = Double.NaN;
				else 
					results[i][j] = ((double)val) /BasicConfigParams.YEAR_CONVERSION;
			}
		}
		return results;
	}
	}
