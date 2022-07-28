/**
 * 
 */
package es.ull.iis.simulation.hta.inforeceiver;

import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DetectionTestResult;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.inforeceiver.Listener;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class ScreeningTestPerformanceView extends Listener implements StructuredOutputListener {
	private final int [] nResults; 

	/**
	 * @param description
	 */
	public ScreeningTestPerformanceView(SecondOrderParamsRepository secParams) {
		super("Screening test performance");
		nResults = new int[DetectionTestResult.values().length];
		addGenerated(PatientInfo.class);
		addEntrance(PatientInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof PatientInfo) {
			final PatientInfo pInfo = (PatientInfo) info;
			if (PatientInfo.Type.SCREEN.equals(pInfo.getType())) {
				nResults[((DetectionTestResult)pInfo.getCause()).ordinal()]++;
			}
		}

	}

	public static String getStrHeader(String intervention) {
		final StringBuilder str = new StringBuilder();
		for (DetectionTestResult res : DetectionTestResult.values())
			str.append(res + "_" + intervention + "\t");
		return str.toString();
	}
	
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		for (int res : nResults)
			str.append(res + "\t");
		return str.toString();
	}
}
