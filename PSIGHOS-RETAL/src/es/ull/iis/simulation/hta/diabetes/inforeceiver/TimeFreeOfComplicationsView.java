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
	private final double [][][] timeToComplications;
	/** Enables printing the confidence intervals for first order simulations */
	private final boolean printFirstOrderVariance;
	/** Number of interventions assessed */
	private final int nInterventions;
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
		timeToComplications = new double[nInterventions][availableHealthStates.size()][nPatients];
		this.printFirstOrderVariance = printFirstOrderVariance;
		this.nInterventions = nInterventions;
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
	}
	
	/**
	 * @return the timeToComplications
	 */
	public double[][] getAvgTimeToComplications() {
		final double[][] results = new double[nInterventions][timeToComplications[0].length];
		for (int i = 0; i < nInterventions; i++) {
			int j = 0;
			for (double[] values : timeToComplications[i]) {
				final double avg = Statistics.average(values);
				results[i][j++] = avg /BasicConfigParams.YEAR_CONVERSION;
			}
		}
		return results;
	}

	public double[][] getTimeToComplications(int patient) {
		final double[][] results = new double[nInterventions][timeToComplications[0].length];
		for (int i = 0; i < nInterventions; i++) {
			for (int j = 0; j < results[i].length; j++) {
				results[i][j] = timeToComplications[i][j][patient] /BasicConfigParams.YEAR_CONVERSION;
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
					str.append("AVG_TIME_TO_").append(suf).append("\tL95CI_TIME_TO_").append(suf).append("\tU95CI_TIME_TO_").append(suf);
				}			
			}
		}
		else {
			for (SecondOrderDiabetesIntervention inter : interventions) {
				for (DiabetesComplicationStage comp : availableHealthStates) {
					str.append("AVG_TIME_TO_").append(comp.name()).append("_").append(inter.getShortName()).append("\t");
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
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nInterventions; i++) {
			for (double[] values : timeToComplications[i]) {
				final double avg = Statistics.average(values);
				str.append(avg /BasicConfigParams.YEAR_CONVERSION).append("\t");
				if (printFirstOrderVariance) {
					final double[] ci = Statistics.normal95CI(avg, Statistics.stdDev(values, avg), values.length);
					str.append(ci[0] /BasicConfigParams.YEAR_CONVERSION).append("\t").append(ci[1]/BasicConfigParams.YEAR_CONVERSION).append("\t");
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
			final long deathTs = pInfo.getTs();
			// Check all the complications
			for (DiabetesComplicationStage comp : availableHealthStates) {
				final long time = pat.getTimeToChronicComorbidity(comp);
				timeToComplications[nIntervention][comp.ordinal()][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
}
