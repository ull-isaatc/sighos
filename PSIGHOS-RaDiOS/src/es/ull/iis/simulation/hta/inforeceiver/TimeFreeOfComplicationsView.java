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
	private final static String STR_AVG = "AVG_TIME_TO_";
	private final static String STR_LCI = "L95CI_TIME_TO_";
	private final static String STR_UCI = "U95CI_TIME_TO_";
	private final static String STR_INC = "INC_";
	private final static String STR_PREV = "PREV_";
	
	/** Inner structure to store time to event. For each mapped manifestation contains an array with t-uples <intervention, patient> */
	private final TreeMap<Manifestation, long[][]> timeToEvents;
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
	private final Manifestation[] availableChronicManifestations;

	/**
	 * 
	 * @param secParams Main repository for the simulations
	 * @param printFirstOrderVariance Enables printing the confidence intervals for first order simulations
	 */
	public TimeFreeOfComplicationsView(SecondOrderParamsRepository secParams, boolean printFirstOrderVariance) {
		super("Standard patient viewer");
		this.nInterventions = secParams.getNInterventions();
		this.nPatients = secParams.getnPatients();
		this.availableChronicManifestations = secParams.getRegisteredManifestations(Manifestation.Type.CHRONIC);
		prevalence = new int[nInterventions][availableChronicManifestations.length];
		incidence = new int[nInterventions][availableChronicManifestations.length];
		timeToEvents = new TreeMap<>();
		for (Manifestation manif : availableChronicManifestations) {
			timeToEvents.put(manif, new long[nInterventions][nPatients]);
		}
		this.printFirstOrderVariance = printFirstOrderVariance;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	/**
	 * Generates a string with the header required to print the information collected by this listener in a line 
	 * @param printFirstOrderVariance Enables printing the confidence intervals for first order simulations
	 * @param interventions The interventions analyzed
	 * @param availableChronicManifestations Chronic manifestations included
	 * @return
	 */
	public static String getStrHeader(boolean printFirstOrderVariance, Intervention[] interventions, Manifestation[] availableChronicManifestations) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (Intervention inter : interventions) {
				for (Manifestation comp : availableChronicManifestations) {
					final String suf = comp.name() + "_" + inter.name() + "\t";
					str.append(STR_PREV).append(suf).append(STR_INC).append(suf).append(STR_AVG).append(suf).append(STR_LCI).append(suf).append(STR_UCI).append(suf);
				}			
			}
		}
		else {
			for (Intervention inter : interventions) {
				for (Manifestation comp : availableChronicManifestations) {
					final String suf = comp.name() + "_" + inter.name() + "\t";
					str.append(STR_PREV).append(suf).append(STR_INC).append(suf).append(STR_AVG).append(suf);
				}
			}
		}
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableChronicManifestations.length; j++) {				
				final ArrayList<Long> validValues = getValidValues(timeToEvents.get(availableChronicManifestations[j])[i]);
				str.append(prevalence[i][j]).append("\t").append(incidence[i][j]).append("\t");
				if (validValues.size() == 0) {
					str.append(Double.NaN).append("\t");						
					if (printFirstOrderVariance)
						str.append(Double.NaN).append("\t").append(Double.NaN).append("\t");						
				}
				else {
					final double avg = Statistics.average(validValues);
					str.append(avg / BasicConfigParams.YEAR_CONVERSION).append("\t");
					if (printFirstOrderVariance) {
						final double[] ci = Statistics.normal95CI(avg, Statistics.stdDev(validValues, avg), validValues.size());
						str.append(ci[0] /BasicConfigParams.YEAR_CONVERSION).append("\t").append(ci[1]/BasicConfigParams.YEAR_CONVERSION).append("\t");
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
			final long deathTs = pInfo.getTs();
			// Check all the complications
			for (Manifestation comp : availableChronicManifestations) {
				final long time = pat.getTimeToManifestation(comp);
				timeToEvents.get(comp)[nIntervention][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
	
	public double[][] getIncidence() {
		final double[][] percIncidence = new double[nInterventions][availableChronicManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableChronicManifestations.length; j++) {
				percIncidence[i][j] = (double) incidence[i][j] / (double)nPatients;
			}
		}
		return percIncidence;
	}
	
	public double[][] getPrevalence() {
		final double[][] percPrevalence = new double[nInterventions][availableChronicManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableChronicManifestations.length; j++) {
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
		final double[][] results = new double[nInterventions][availableChronicManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableChronicManifestations.length; j++) {				
				final ArrayList<Long> validValues = getValidValues(timeToEvents.get(availableChronicManifestations[j])[i]);
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
		final double[][] results = new double[nInterventions][availableChronicManifestations.length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < results[i].length; j++) {
				final long val = timeToEvents.get(availableChronicManifestations[j])[i][patient];
				if (Long.MAX_VALUE == val || val == 0)
					results[i][j] = Double.NaN;
				else 
					results[i][j] = ((double)val) /BasicConfigParams.YEAR_CONVERSION;
			}
		}
		return results;
	}
	}
