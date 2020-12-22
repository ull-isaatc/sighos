package es.ull.iis.simulation.hta.radios;

import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.RRCalculator;

public class RadiosRangeAgeMatrixRRCalculator implements RRCalculator {
	private double [][] relativeRisk;

	public RadiosRangeAgeMatrixRRCalculator(double [][] relativeRisk) {
		this.relativeRisk = relativeRisk;
	}

	@Override
	public double getRR(Patient pat) {
		double patientAge = pat.getAge();
		for (int i = 0; i < relativeRisk.length; i++) {
			if (relativeRisk[i][0] >= patientAge && patientAge <= relativeRisk[i][1]) {
				return relativeRisk[i][3];
			}
		}
		return 0.0;
	}

}
