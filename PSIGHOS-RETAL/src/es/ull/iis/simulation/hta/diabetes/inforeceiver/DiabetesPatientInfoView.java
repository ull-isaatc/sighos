/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.inforeceiver;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.diabetes.info.T1DMPatientInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iv�n Castilla
 *
 */
public class DiabetesPatientInfoView extends Listener {
	private final PrintStream out = System.out;
	long simulationInit = 0;
	private final int specificPatient;
	

	/**
	 * @param simul
	 */
	public DiabetesPatientInfoView(int specificPatient) {
		super("Standard patient viewer");
		addGenerated(T1DMPatientInfo.class);
		addEntrance(T1DMPatientInfo.class);
		addEntrance(SimulationStartStopInfo.class);
		this.specificPatient = specificPatient;
	}

	public DiabetesPatientInfoView() {
		this(-1);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartStopInfo) {
			SimulationStartStopInfo tInfo = (SimulationStartStopInfo) info;
			if (SimulationStartStopInfo.Type.END.equals(tInfo.getType())) { 
				if (specificPatient == -1)
					out.println(info.toString() + ": CPU Time = " 
						+ ((tInfo.getCpuTime() - simulationInit) / 1000000) + " miliseconds.");
			} else if (SimulationStartStopInfo.Type.START.equals(tInfo.getType())) {
				simulationInit = tInfo.getCpuTime();
				if (specificPatient == -1)
					out.println(info.toString());
			} 			
		} else if (info instanceof T1DMPatientInfo) {
			if (specificPatient == -1 || specificPatient == ((T1DMPatientInfo)info).getPatient().getIdentifier())
				out.println(info.toString());
		}
		
	}
}