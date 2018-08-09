/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DeathParams extends ModelParams {
	private final AllCausesDeathParam allCausesDeath;
	/** Incremented mortality risk due to complications */
	private final double[] complicationsIMR;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathESRD;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathNPH;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathLEA;
	private final CanadaOtherCausesDeathParam canadaTimeToDeathOther;
	private final CVDCanadaDeathParam canadaTimeToDeathCHD;
	
	/**
	 */
	public DeathParams(SecondOrderParams secParams) {
		super();
		allCausesDeath = new AllCausesDeathParam(CommonParams.NPATIENTS);
		if (CommonParams.CANADA) {
			canadaTimeToDeathESRD = new AnnualBasedTimeToEventParam(CommonParams.NPATIENTS, 0.164, SecondOrderParams.NO_RR);
			canadaTimeToDeathNPH = new AnnualBasedTimeToEventParam(CommonParams.NPATIENTS, 0.0036, SecondOrderParams.NO_RR);
			canadaTimeToDeathLEA = new AnnualBasedTimeToEventParam(CommonParams.NPATIENTS, 0.093, SecondOrderParams.NO_RR);
			canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(CommonParams.NPATIENTS);
			canadaTimeToDeathCHD = new CVDCanadaDeathParam(CommonParams.NPATIENTS);
			complicationsIMR = null;
		}
		else {
			canadaTimeToDeathESRD = null;
			canadaTimeToDeathNPH = null;
			canadaTimeToDeathLEA = null;
			canadaTimeToDeathOther = null;
			canadaTimeToDeathCHD = null;
			complicationsIMR = new double[CommonParams.N_COMPLICATIONS];
			for (Complication comp : Complication.values())
				complicationsIMR[comp.ordinal()] = secParams.getIMR(comp);
		}
	}

	public long getTimeToDeath(T1DMPatient pat) {
		if (CommonParams.CANADA) {
			long timeToDeath = canadaTimeToDeathOther.getValue(pat);
			final EnumSet<Complication> state = pat.getState();
			if (state.contains(Complication.ESRD)) {
				final long deathESRD = canadaTimeToDeathESRD.getValue(pat);
				if (deathESRD < timeToDeath)
					timeToDeath = deathESRD;
			}
			if (state.contains(Complication.NPH)) {
				final long deathNPH = canadaTimeToDeathNPH.getValue(pat);
				if (deathNPH < timeToDeath)
					timeToDeath = deathNPH;
			}
			if (state.contains(Complication.LEA)) {
				final long deathLEA = canadaTimeToDeathLEA.getValue(pat);
				if (deathLEA < timeToDeath)
					timeToDeath = deathLEA;
			}
			if (state.contains(Complication.CHD)) {
				final long deathCHD = canadaTimeToDeathCHD.getValue(pat);
				if (deathCHD < timeToDeath)
					timeToDeath = deathCHD;				
			}
			return timeToDeath;
		}
		else {
			double maxIMR = 1.0;
			for (Complication comp : pat.getState()) {
				if (complicationsIMR[comp.ordinal()] > maxIMR) {
					maxIMR = complicationsIMR[comp.ordinal()];
				}
			}
			return allCausesDeath.getValue(pat, maxIMR);
		}
	}
}
