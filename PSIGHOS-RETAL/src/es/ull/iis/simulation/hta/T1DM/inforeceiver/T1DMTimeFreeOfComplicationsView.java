/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.info.T1DMPatientInfo;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.Complication;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParams;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla
 *
 */
public class T1DMTimeFreeOfComplicationsView extends Listener {
	private final double [][] timeToComplications;
	private final PrintStream out = System.out;

	/**
	 * @param simUnit The time unit used within the simulation
	 */
	public T1DMTimeFreeOfComplicationsView(int nPatients) {
		super("Standard patient viewer");
		timeToComplications = new double[SecondOrderParams.N_COMPLICATIONS][nPatients];
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationEndInfo.class);
	}
	
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationEndInfo) {
			for (Complication comp : Complication.values()) {
				out.print("\t" + comp.name());
			}
			out.println();
			for (double[] values : timeToComplications) {
				final double avg = Statistics.average(values);
				final double[] ci = Statistics.normal95CI(avg, Statistics.stdDev(values, avg), values.length);
				out.print("\t" + (avg /BasicConfigParams.YEAR_CONVERSION) + " [" + (ci[0] /BasicConfigParams.YEAR_CONVERSION) + ", " + (ci[1]/BasicConfigParams.YEAR_CONVERSION) + "]");
			}
			out.println();
		}
		else {
			T1DMPatientInfo pInfo = (T1DMPatientInfo) info;
			T1DMPatient pat = pInfo.getPatient();
			if (pInfo.getType() == T1DMPatientInfo.Type.DEATH) {
				final long deathTs = pInfo.getTs();
				// Check all the complications
				for (Complication comp : Complication.values()) {
					final long time = pat.getTimeToComplication(comp);
					timeToComplications[comp.ordinal()][pat.getIdentifier()] = (time == Long.MAX_VALUE) ? deathTs : time;
				}
			}
		}
	}
}
