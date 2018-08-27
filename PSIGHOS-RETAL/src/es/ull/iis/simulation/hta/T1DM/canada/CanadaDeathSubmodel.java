/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.AnnualBasedTimeToEventParam;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaDeathSubmodel extends DeathSubmodel {
	private final AnnualBasedTimeToEventParam canadaTimeToDeathESRD;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathNPH;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathLEA;
	private final CanadaOtherCausesDeathParam canadaTimeToDeathOther;
	private final CVDCanadaDeathParam canadaTimeToDeathCHD;


	/**
	 * 
	 */
	public CanadaDeathSubmodel() {
		canadaTimeToDeathESRD = new AnnualBasedTimeToEventParam(nPatients, 0.164, noRR);
		canadaTimeToDeathNPH = new AnnualBasedTimeToEventParam(nPatients, 0.0036, noRR);
		canadaTimeToDeathLEA = new AnnualBasedTimeToEventParam(nPatients, 0.093, noRR);
		canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(nPatients);
		canadaTimeToDeathCHD = new CVDCanadaDeathParam(nPatients, noRR);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public long getTimeToDeath(T1DMPatient pat) {
		long timeToDeath = canadaTimeToDeathOther.getValue(pat);
		final EnumSet<MainComplications> state = pat.getState();
		if (state.contains(MainComplications.ESRD)) {
			final long deathESRD = canadaTimeToDeathESRD.getValue(pat);
			if (deathESRD < timeToDeath)
				timeToDeath = deathESRD;
		}
		if (state.contains(MainComplications.NPH)) {
			final long deathNPH = canadaTimeToDeathNPH.getValue(pat);
			if (deathNPH < timeToDeath)
				timeToDeath = deathNPH;
		}
		if (state.contains(MainComplications.LEA)) {
			final long deathLEA = canadaTimeToDeathLEA.getValue(pat);
			if (deathLEA < timeToDeath)
				timeToDeath = deathLEA;
		}
		if (state.contains(MainComplications.CHD)) {
			final long deathCHD = canadaTimeToDeathCHD.getValue(pat);
			if (deathCHD < timeToDeath)
				timeToDeath = deathCHD;				
		}
		return timeToDeath;
	}

}
