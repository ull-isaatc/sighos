/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class T1DMTimeFreeOfComplicationsView extends Listener implements StructuredOutputListener {
	private final double [][][] timeToComplications;
	private final boolean printFirstOrderVariance;
	private final int nInterventions;
	private final ArrayList<DiabetesComplicationStage> availableHealthStates;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public T1DMTimeFreeOfComplicationsView(int nPatients, int nInterventions, boolean printFirstOrderVariance, ArrayList<DiabetesComplicationStage> availableHealthStates) {
		super("Standard patient viewer");
		this.availableHealthStates = availableHealthStates;
		timeToComplications = new double[nInterventions][availableHealthStates.size()][nPatients];
		this.printFirstOrderVariance = printFirstOrderVariance;
		this.nInterventions = nInterventions;
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
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
		final T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
		final DiabetesPatient pat = pInfo.getPatient();
		final int nIntervention = pat.getnIntervention(); 
		if (pInfo.getType() == T1DMPatientInfo.Type.DEATH) {
			final long deathTs = pInfo.getTs();
			// Check all the complications
			for (DiabetesComplicationStage comp : availableHealthStates) {
				final long time = pat.getTimeToChronicComorbidity(comp);
				timeToComplications[nIntervention][comp.ordinal()][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
}
