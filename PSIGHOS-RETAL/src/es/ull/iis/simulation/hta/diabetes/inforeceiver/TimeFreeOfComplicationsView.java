/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class TimeFreeOfComplicationsView extends Listener implements StructuredOutputListener {
	/** Inner structure to store the time to each complication. Contains a value for each <intervention, complication, patient> */
	private final long [][][] timeToComplications;
	private final int [][] prevalence;
	private final int [][] incidence;
	/** Enables printing the confidence intervals for first order simulations */
	private final boolean printFirstOrderVariance;
	/** Number of interventions assessed */
	private final int nInterventions;
	private final int nPatients;
	private final ArrayList<DiabetesComplicationStage> availableHealthStates;

	/**
	 * 
	 * @param nPatients
	 * @param nInterventions
	 * @param printFirstOrderVariance
	 * @param availableHealthStates
	 */
	public TimeFreeOfComplicationsView(int nPatients, int nInterventions, boolean printFirstOrderVariance, ArrayList<DiabetesComplicationStage> availableHealthStates) {
		super("Standard patient viewer");
		this.availableHealthStates = availableHealthStates;
		timeToComplications = new long[nInterventions][availableHealthStates.size()][nPatients];
		prevalence = new int[nInterventions][availableHealthStates.size()];
		incidence = new int[nInterventions][availableHealthStates.size()];
		this.printFirstOrderVariance = printFirstOrderVariance;
		this.nInterventions = nInterventions;
		this.nPatients = nPatients;
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
	}
	
	/**
	 * @return the timeToComplications
	 */
	public double[][] getAvgTimeToComplications() {
		final double[][] results = new double[nInterventions][timeToComplications[0].length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableHealthStates.size(); j++) {				
				final ArrayList<Long> validValues = new ArrayList<>();
				for (long val : timeToComplications[i][j]) {
					if (Long.MAX_VALUE != val && val != 0) {
						validValues.add(val);
					}
				}
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
		final double[][] results = new double[nInterventions][timeToComplications[0].length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < results[i].length; j++) {
				if (Long.MAX_VALUE == timeToComplications[i][j][patient] || timeToComplications[i][j][patient] == 0)
					results[i][j] = Double.NaN;
				else 
					results[i][j] = ((double)timeToComplications[i][j][patient]) /BasicConfigParams.YEAR_CONVERSION;
			}
		}
		return results;
	}
	
	public static String getStrHeader(boolean printFirstOrderVariance, ArrayList<SecondOrderDiabetesIntervention> interventions, ArrayList<DiabetesComplicationStage> availableHealthStates) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (SecondOrderDiabetesIntervention inter : interventions) {
				for (DiabetesComplicationStage comp : availableHealthStates) {
					final String suf = comp.name() + "_" + inter.getShortName() + "\t";
					str.append("PREV_").append(suf).append("INC_").append(suf).append("AVG_TIME_TO_").append(suf).append("\tL95CI_TIME_TO_").append(suf).append("\tU95CI_TIME_TO_").append(suf);
				}			
			}
		}
		else {
			for (SecondOrderDiabetesIntervention inter : interventions) {
				for (DiabetesComplicationStage comp : availableHealthStates) {
					final String suf = comp.name() + "_" + inter.getShortName() + "\t";
					str.append("PREV_").append(suf).append("INC_").append(suf).append("AVG_TIME_TO_").append(suf);
				}
			}
		}
		return str.toString();
	}

	public boolean checkPaired() {
		boolean checked = false;
		for (int i = 0; i < availableHealthStates.size(); i++) {
			for (int j = 0; j < timeToComplications[0][i].length; j++) {
				if (timeToComplications[0][i][j] > timeToComplications[1][i][j]) {
					checked = true;
					System.out.println("Paciente " + j + " Comp " + DiabetesChronicComplications.values()[i] + "\t" + timeToComplications[0][i][j] + ":" + timeToComplications[1][i][j]);
				}
			}
		}
		return checked;
	}
	
	public double[][] getIncidence() {
		final double[][] percIncidence = new double[nInterventions][availableHealthStates.size()];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableHealthStates.size(); j++) {
				percIncidence[i][j] = (double) incidence[i][j] / (double)nPatients;
			}
		}
		return percIncidence;
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < availableHealthStates.size(); j++) {				
				final ArrayList<Long> validValues = new ArrayList<>();
				for (long val : timeToComplications[i][j]) {
					if (Long.MAX_VALUE != val && val != 0) {
						validValues.add(val);
					}
				}
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
		final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
		final DiabetesPatient pat = pInfo.getPatient();
		final int nIntervention = pat.getnIntervention(); 
		if (pInfo.getType() == DiabetesPatientInfo.Type.DEATH) {
			// Check all the complications
			for (DiabetesComplicationStage comp : availableHealthStates) {
				final long time = pat.getTimeToChronicComorbidity(comp);
				timeToComplications[nIntervention][comp.ordinal()][pat.getIdentifier()] = time;
				if (pat.hasComplicationFromStart(comp)) {
					prevalence[nIntervention][comp.ordinal()]++;
				}
				else if (Long.MAX_VALUE != time){
					prevalence[nIntervention][comp.ordinal()]++;
					incidence[nIntervention][comp.ordinal()]++;
				}
			}
		}
	}
}
