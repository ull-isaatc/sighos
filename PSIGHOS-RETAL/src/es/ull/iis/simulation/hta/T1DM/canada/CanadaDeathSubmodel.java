/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.T1DMComplicationStage;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaDeathSubmodel extends DeathSubmodel {
	private final AnnualRiskBasedTimeToEventParam canadaTimeToDeathESRD;
	private final AnnualRiskBasedTimeToEventParam canadaTimeToDeathNPH;
	private final AnnualRiskBasedTimeToEventParam canadaTimeToDeathLEA;
	private final CanadaOtherCausesDeathParam canadaTimeToDeathOther;
	private final CVDCanadaDeathParam canadaTimeToDeathCHD;


	/**
	 * 
	 */
	public CanadaDeathSubmodel(CanadaSecondOrderParams secParams) {
		int nPatients = secParams.getnPatients();
		canadaTimeToDeathESRD = new AnnualRiskBasedTimeToEventParam(secParams.getRngFirstOrder(), nPatients, 0.164, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathNPH = new AnnualRiskBasedTimeToEventParam(secParams.getRngFirstOrder(), nPatients, 0.0036, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathLEA = new AnnualRiskBasedTimeToEventParam(secParams.getRngFirstOrder(), nPatients, 0.093, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(secParams);
		canadaTimeToDeathCHD = new CVDCanadaDeathParam(secParams, SecondOrderParamsRepository.NO_RR);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public long getTimeToDeath(T1DMPatient pat) {
		long timeToDeath = canadaTimeToDeathOther.getValue(pat);
		final TreeSet<T1DMComplicationStage> state = pat.getDetailedState();
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
