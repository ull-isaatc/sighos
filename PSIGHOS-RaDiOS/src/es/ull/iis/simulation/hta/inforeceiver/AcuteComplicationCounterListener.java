/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla
 *
 */
public class AcuteComplicationCounterListener extends Listener implements StructuredOutputListener {
	private final int[][] nComplications;
	private final int nPatients;

	/**
	 * 
	 * @param simul
	 * @param minAge
	 * @param maxAge
	 * @param length
	 * @param detailDeaths
	 */
	public AcuteComplicationCounterListener(int nPatients) {
		super("Counter of acute complications");
		nComplications = new int[DiabetesAcuteComplications.values().length][nPatients+1];
		this.nPatients = nPatients;
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		final PatientInfo pInfo = (PatientInfo) info;
		if (PatientInfo.Type.ACUTE_EVENT.equals(pInfo.getType())) {
			nComplications[pInfo.getAcuteEvent().ordinal()][pInfo.getPatient().getIdentifier()]++;
			nComplications[pInfo.getAcuteEvent().ordinal()][nPatients]++;
		}
	}

	public static String getStrHeader(String intervention) {
		final StringBuilder str = new StringBuilder();
		for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
			str.append("AVG_" + comp.name() + "_" + intervention + "\t");
			str.append("L95CI_" + comp.name() + "_" + intervention + "\t");
			str.append("U95CI_" + comp.name() + "_" + intervention + "\t");
		}
		return str.toString();
	}

	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (DiabetesAcuteComplications comp : DiabetesAcuteComplications.values()) {
			final int[] cip = getPercentile95CI(comp);
			str.append(((double)nComplications[comp.ordinal()][nPatients] / nPatients) + "\t" + cip[0] + "\t" + cip[1] + "\t");
		}
		return str.toString();
	}
	
	private int[] getPercentile95CI(DiabetesAcuteComplications comp) {
		final int[] ordered = Arrays.copyOf(nComplications[comp.ordinal()], nPatients);
		Arrays.sort(ordered);
		final int index = (int)Math.ceil(nPatients * 0.025);
		return new int[] {ordered[index - 1], ordered[nPatients - index]}; 
	}
	
}
