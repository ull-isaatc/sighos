package es.ull.iis.simulation.hta.interventions;

import es.ull.iis.simulation.hta.Named;

/**
 * The possible results of a diagnostic/screening test
 * @author Iv�n Castilla Rodr�guez
 *
 */
public enum DetectionTestResult implements Named {
	TP,
	FP,
	TN,
	FN
}