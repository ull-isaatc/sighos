package es.ull.iis.simulation.hta.radios;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;
import es.ull.iis.simulation.hta.params.SecondOrderParam;

public class RadiosRangeAgeMatrixRRCalculator implements RRCalculator {
	private Object [][] relativeRisk;

	public RadiosRangeAgeMatrixRRCalculator(Object [][] relativeRisk) {
		this.relativeRisk = relativeRisk;
	}

	@Override
	public double getRR(Patient pat) {
		double patientAge = pat.getAge();
		for (int i = 0; i < relativeRisk.length; i++) {
			if ((Double) relativeRisk[i][0] >= patientAge && patientAge <= (Double) relativeRisk[i][1]) {
				return ((SecondOrderParam) relativeRisk[i][3]).getValue(pat.getSimulation().getIdentifier());
			}
		}
		return 0.0;
	}

}
