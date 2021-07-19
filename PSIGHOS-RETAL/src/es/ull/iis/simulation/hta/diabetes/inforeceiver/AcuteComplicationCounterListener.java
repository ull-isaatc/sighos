/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.util.Arrays;

import es.ull.iis.simulation.hta.diabetes.DiabetesAcuteComplications;
import es.ull.iis.simulation.hta.diabetes.info.DiabetesPatientInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * A listener to compute the number of acute complications suffered by a patient during his/her lifetime
 * @author Iván Castilla
 *
 */
public class AcuteComplicationCounterListener extends Listener implements StructuredOutputListener {
	/** Number of complications of each type suffered by each patient. The last element of the array stores the total acummulated number of events for all the patients (useful to compute average) */
	private final int[][] nComplications;
	/** Number of patients */
	private final int nPatients;

	/**
	 * 
	 * @param nPatients Number of patients
	 */
	public AcuteComplicationCounterListener(int nPatients) {
		super("Counter of acute complications");
		nComplications = new int[DiabetesAcuteComplications.values().length][nPatients+1];
		this.nPatients = nPatients;
		addGenerated(DiabetesPatientInfo.class);
		addEntrance(DiabetesPatientInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		final DiabetesPatientInfo pInfo = (DiabetesPatientInfo) info;
		if (DiabetesPatientInfo.Type.ACUTE_EVENT.equals(pInfo.getType())) {
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

	/**
	 * @return the average number of complications suffered by all the patients during their lifetime
	 */
	public double[] getAvgNComplications() {
		final double[] avg = new double[nComplications.length];
		for (int i = 0; i < nComplications.length; i++)
			avg[i] = (double)nComplications[i][nPatients] / nPatients;
		return avg;
	}

	/**
	 * @return the nComplications
	 */
	public int[][] getNComplications() {
		return nComplications;
	}
	
}
