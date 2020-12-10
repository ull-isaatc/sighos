/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.ArrayList;

import es.ull.iis.simulation.hta.ChronicComplication;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.SecondOrderIntervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.progression.Manifestation;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class TimeFreeOfComplicationsView extends Listener implements StructuredOutputListener {
	private final double [][][] timeToComplications;
	private final boolean printFirstOrderVariance;
	private final int nInterventions;
	private final ArrayList<Manifestation> availableHealthStates;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public TimeFreeOfComplicationsView(int nPatients, int nInterventions, boolean printFirstOrderVariance, ArrayList<Manifestation> availableHealthStates) {
		super("Standard patient viewer");
		this.availableHealthStates = availableHealthStates;
		timeToComplications = new double[nInterventions][availableHealthStates.size()][nPatients];
		this.printFirstOrderVariance = printFirstOrderVariance;
		this.nInterventions = nInterventions;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	public static String getStrHeader(boolean printFirstOrderVariance, ArrayList<SecondOrderIntervention> interventions, ArrayList<Manifestation> availableHealthStates) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (SecondOrderIntervention inter : interventions) {
				for (Manifestation comp : availableHealthStates) {
					final String suf = comp.name() + "_" + inter.getShortName() + "\t";
					str.append("AVG_TIME_TO_").append(suf).append("\tL95CI_TIME_TO_").append(suf).append("\tU95CI_TIME_TO_").append(suf);
				}			
			}
		}
		else {
			for (SecondOrderIntervention inter : interventions) {
				for (Manifestation comp : availableHealthStates) {
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
					System.out.println("Paciente " + j + " Comp " + ChronicComplication.values()[i] + "\t" + timeToComplications[0][i][j] + ":" + timeToComplications[1][i][j]);
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
		final PatientInfo pInfo = (PatientInfo) info;
		final Patient pat = pInfo.getPatient();
		final int nIntervention = pat.getnIntervention(); 
		if (pInfo.getType() == PatientInfo.Type.DEATH) {
			final long deathTs = pInfo.getTs();
			// Check all the complications
			for (Manifestation comp : availableHealthStates) {
				final long time = pat.getTimeToManifestation(comp);
				timeToComplications[nIntervention][comp.ordinal()][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
}
