/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;

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
	public CanadaDeathSubmodel(int nPatients) {
		canadaTimeToDeathESRD = new AnnualBasedTimeToEventParam(nPatients, 0.164, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathNPH = new AnnualBasedTimeToEventParam(nPatients, 0.0036, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathLEA = new AnnualBasedTimeToEventParam(nPatients, 0.093, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(nPatients);
		canadaTimeToDeathCHD = new CVDCanadaDeathParam(nPatients, SecondOrderParamsRepository.NO_RR);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public long getTimeToDeath(T1DMPatient pat) {
		long timeToDeath = canadaTimeToDeathOther.getValue(pat);
		final TreeSet<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(CanadaNPHSubmodel.ESRD)) {
			final long deathESRD = canadaTimeToDeathESRD.getValue(pat);
			if (deathESRD < timeToDeath)
				timeToDeath = deathESRD;
		}
		if (state.contains(CanadaNPHSubmodel.NPH)) {
			final long deathNPH = canadaTimeToDeathNPH.getValue(pat);
			if (deathNPH < timeToDeath)
				timeToDeath = deathNPH;
		}
		if (state.contains(CanadaNEUSubmodel.LEA)) {
			final long deathLEA = canadaTimeToDeathLEA.getValue(pat);
			if (deathLEA < timeToDeath)
				timeToDeath = deathLEA;
		}
		if (state.contains(CanadaCHDSubmodel.CHD)) {
			final long deathCHD = canadaTimeToDeathCHD.getValue(pat);
			if (deathCHD < timeToDeath)
				timeToDeath = deathCHD;				
		}
		return timeToDeath;
	}

}
