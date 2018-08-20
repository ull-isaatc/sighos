/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.Complication;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParams;
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

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public T1DMTimeFreeOfComplicationsView(int nPatients, int nInterventions, boolean printFirstOrderVariance) {
		super("Standard patient viewer");
		timeToComplications = new double[nInterventions][SecondOrderParams.N_COMPLICATIONS][nPatients];
		this.printFirstOrderVariance = printFirstOrderVariance;
		this.nInterventions = nInterventions;
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
	}
	
	public static String getStrHeader(boolean printFirstOrderVariance, Intervention[] interventions) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (Intervention inter : interventions) {
				for (Complication comp : Complication.values()) {
					final String suf = comp.name() + "_" + inter.getShortName() + "\t";
					str.append("AVG_TIME_TO_").append(suf).append("\tL95CI_TIME_TO_").append(suf).append("\tU95CI_TIME_TO_").append(suf);
				}			
			}
		}
		else {
			for (Intervention inter : interventions) {
				for (Complication comp : Complication.values()) {
					str.append("AVG_TIME_TO_").append(comp.name()).append("_").append(inter.getShortName()).append("\t");
				}
			}
		}
		return str.toString();
	}

	public boolean checkPaired() {
		boolean checked = false;
		for (int i = 0; i < SecondOrderParams.N_COMPLICATIONS; i++) {
			for (int j = 0; j < timeToComplications[0][i].length; j++) {
				if (timeToComplications[0][i][j] > timeToComplications[1][i][j]) {
					checked = true;
					System.out.println("Paciente " + j + " Comp " + Complication.values()[i] + "\t" + timeToComplications[0][i][j] + ":" + timeToComplications[1][i][j]);
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
		final T1DMPatient pat = pInfo.getPatient();
		final int nIntervention = pat.getnIntervention(); 
		if (pInfo.getType() == T1DMPatientInfo.Type.DEATH) {
			final long deathTs = pInfo.getTs();
			// Check all the complications
			for (Complication comp : Complication.values()) {
				final long time = pat.getTimeToComplication(comp);
				timeToComplications[nIntervention][comp.ordinal()][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
}
