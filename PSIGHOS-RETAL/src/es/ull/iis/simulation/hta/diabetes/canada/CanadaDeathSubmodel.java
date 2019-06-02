/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.DeathSubmodel;

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
		canadaTimeToDeathESRD = new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, 0.164, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathNPH = new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, 0.0036, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathLEA = new AnnualRiskBasedTimeToEventParam(SecondOrderParamsRepository.getRNG_FIRST_ORDER(), nPatients, 0.093, SecondOrderParamsRepository.NO_RR);
		canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(secParams);
		canadaTimeToDeathCHD = new CVDCanadaDeathParam(secParams, SecondOrderParamsRepository.NO_RR);
	}

	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.hta.T1DM.DeathSubmodel#getTimeToDeath(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
	 */
	@Override
	public long getTimeToDeath(DiabetesPatient pat) {
		long timeToDeath = canadaTimeToDeathOther.getValue(pat);
		final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
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
