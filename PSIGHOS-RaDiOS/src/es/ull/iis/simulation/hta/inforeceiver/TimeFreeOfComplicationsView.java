/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

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
 * @author Iván Castilla
 *
 */
public class TimeFreeOfComplicationsView extends Listener implements StructuredOutputListener {
	private final TreeMap<Manifestation, double [][]> timeToComplications;
	private final boolean printFirstOrderVariance;
	private final int nInterventions;
	private final Manifestation[] availableHealthStates;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public TimeFreeOfComplicationsView(SecondOrderParamsRepository secParams, boolean printFirstOrderVariance) {
		super("Standard patient viewer");
		this.availableHealthStates = secParams.getRegisteredManifestations(Manifestation.Type.CHRONIC);
		this.nInterventions = secParams.getNInterventions();
		timeToComplications = new TreeMap<>();
		for (Manifestation manif : availableHealthStates) {
			timeToComplications.put(manif, new double[nInterventions][secParams.getnPatients()]);
		}
		this.printFirstOrderVariance = printFirstOrderVariance;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}
	
	public static String getStrHeader(boolean printFirstOrderVariance, Intervention[] interventions, Manifestation[] availableHealthStates) {
		final StringBuilder str = new StringBuilder();
		if (printFirstOrderVariance) {
			for (Intervention inter : interventions) {
				for (Manifestation comp : availableHealthStates) {
					final String suf = comp.name() + "_" + inter.name() + "\t";
					str.append("AVG_TIME_TO_").append(suf).append("\tL95CI_TIME_TO_").append(suf).append("\tU95CI_TIME_TO_").append(suf);
				}			
			}
		}
		else {
			for (Intervention inter : interventions) {
				for (Manifestation comp : availableHealthStates) {
					str.append("AVG_TIME_TO_").append(comp.name()).append("_").append(inter.name()).append("\t");
				}
			}
		}
		return str.toString();
	}

	public boolean checkPaired() {
		boolean checked = false;
		for (Manifestation manif : availableHealthStates) {
			for (int j = 0; j < timeToComplications.get(manif)[0].length; j++) {
				if (timeToComplications.get(manif)[0][j] > timeToComplications.get(manif)[1][j]) {
					checked = true;
					System.out.println("Paciente " + j + " Comp " + manif + "\t" + timeToComplications.get(manif)[0][j] + ":" + timeToComplications.get(manif)[1][j]);
				}
			}
		}
		return checked;
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int i = 0; i < nInterventions; i++) {
			for (final Manifestation manif : timeToComplications.keySet()) {
				double[] values = timeToComplications.get(manif)[i];
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
				timeToComplications.get(comp)[nIntervention][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
			}
		}
	}
}
