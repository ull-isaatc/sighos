/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.params.ModelParams;
import es.ull.iis.simulation.model.TimeUnit;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomNumber;
import simkit.random.RandomNumberFactory;
import simkit.random.RandomVariate;

/**
 * TODO El cálculo de tiempo hasta complicación usa siempre el mismo número aleatorio para la misma complicación. Si aumenta el riesgo de esa
 * complicación en un momento de la simulación, se recalcula el tiempo, pero empezando en el instante actual. Esto produce que no necesariamente se acorte
 * el tiempo hasta evento en caso de un nuevo factor de riesgo. ¿debería reescalar de alguna manera el tiempo hasta evento en estos casos (¿proporcional al RR?)?
 * @author Iván Castilla Rodríguez
 *
 */
public class CommonParams extends ModelParams {
	private static double LN09 = Math.log(0.9);
	private final SevereHypoglycemicEventParam hypoParam;
	private final RandomNumber rng;
	private final double[][] rndComplications;

	private final double invDNC_RET;
	private final double invDNC_NEU;
	private final double invDNC_NPH;
	private final double invDNC_CHD;
	private final double invNEU_CHD;
	private final double invNEU_LEA;
	private final double invNEU_NPH;
	private final double invNPH_CHD;
	private final double invNPH_ESRD;
	private final double invRET_BLI;
	private final double invRET_CHD;
	
	/** Reference HbA1c level for the risk reductions */
	private final double referenceHbA1c;
	private final double lnReferenceHbA1c;
	/** Risk reduction of complications associated to a 10% reduction in the HbA1c level */
	private final double[] rr10Complications;
	private final double[] noRR;
	
	private final double pMan;
	private final RandomVariate baselineAge;
	private final RandomVariate baselineHBA1c;
	private final RandomVariate weeklySensorUsage;
	private final long[] durationOfEffect;
	private final double discountRate;
	
	private final AllCausesDeathParam allCausesDeath;
	private final double noComplicationsIMR;
	/** Incremented mortality risk due to complications */
	private final double[] complicationsIMR;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathESRD;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathNPH;
	private final AnnualBasedTimeToEventParam canadaTimeToDeathLEA;
	private final CanadaOtherCausesDeathParam canadaTimeToDeathOther;
	private final CVDCanadaDeathParam canadaTimeToDeathCHD;
	private final DiscreteSelectorVariate pCHDComplication;

	final private SecondOrderParams secParams;
	/**
	 * @param secondOrder
	 */
	public CommonParams(SecondOrderParams secParams, int nPatients) {
		super();
		this.secParams = secParams;
		rng = RandomNumberFactory.getInstance();
		rndComplications = new double[SecondOrderParams.N_COMPLICATIONS][nPatients];
		pCHDComplication = secParams.getRandomVariateForCHDComplications();

		for (int i = 0; i < SecondOrderParams.N_COMPLICATIONS; i++)
			for (int j = 0; j < nPatients; j++)
				rndComplications[i][j] = rng.draw();
		hypoParam = new SevereHypoglycemicEventParam(nPatients, secParams.getProbability(SecondOrderParams.STR_P_HYPO), secParams.getHypoRR(), secParams.getProbability(SecondOrderParams.STR_P_DEATH_HYPO));
		invDNC_RET = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_RET);
		invDNC_NEU = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_NEU);
		invDNC_NPH = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_NPH);
		invDNC_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_DNC_CHD);
		invNEU_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_CHD);
		invNEU_LEA = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_LEA);
		invNEU_NPH = -1 / secParams.getProbability(SecondOrderParams.STR_P_NEU_NPH);
		invNPH_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NPH_CHD);
		invNPH_ESRD = -1 / secParams.getProbability(SecondOrderParams.STR_P_NPH_ESRD);
		invRET_BLI = -1 / secParams.getProbability(SecondOrderParams.STR_P_RET_BLI);
		invRET_CHD = -1 / secParams.getProbability(SecondOrderParams.STR_P_RET_CHD);
		
		referenceHbA1c = secParams.getReferenceHbA1c();
		lnReferenceHbA1c = Math.log(referenceHbA1c);
		rr10Complications = secParams.getRR10Complications();
		noRR = secParams.getNoRR();

		pMan = secParams.getPMan();
		baselineAge = secParams.getBaselineAge();
		baselineHBA1c = secParams.getBaselineHBA1c();
		weeklySensorUsage = secParams.getWeeklySensorUsage();
		durationOfEffect = secParams.getDurationOfEffect(BasicConfigParams.SIMUNIT);
		discountRate = secParams.getDiscountRate();

		allCausesDeath = new AllCausesDeathParam(nPatients);
		if (secParams.isCanadaValidation()) {
			canadaTimeToDeathESRD = new AnnualBasedTimeToEventParam(nPatients, 0.164, noRR);
			canadaTimeToDeathNPH = new AnnualBasedTimeToEventParam(nPatients, 0.0036, noRR);
			canadaTimeToDeathLEA = new AnnualBasedTimeToEventParam(nPatients, 0.093, noRR);
			canadaTimeToDeathOther = new CanadaOtherCausesDeathParam(nPatients);
			canadaTimeToDeathCHD = new CVDCanadaDeathParam(nPatients, noRR);
			complicationsIMR = null;
			noComplicationsIMR = 1.0;
		}
		else {
			canadaTimeToDeathESRD = null;
			canadaTimeToDeathNPH = null;
			canadaTimeToDeathLEA = null;
			canadaTimeToDeathOther = null;
			canadaTimeToDeathCHD = null;
			noComplicationsIMR = secParams.getNoComplicationIMR();
			complicationsIMR = secParams.getIMRs();
		}
	}

	/**
	 * True if the modifications for the canadian model should be activated
	 * @return True if the modifications for the canadian model should be activated
	 */
	public boolean isCanadaValidation() {
		return secParams.isCanadaValidation();
	}
	
	public int getSex(T1DMPatient pat) {
		return (rng.draw() < pMan) ? 0 : 1;
	}
	
	public double getBaselineAge() {
		return baselineAge.generate();		
	}

	public double getBaselineHBA1c() {
		return baselineHBA1c.generate();		
	}

	public double getWeeklySensorUsage() {
		return weeklySensorUsage.generate();		
	}

	public long getDurationOfEffect(Intervention intervention) {
		return durationOfEffect[intervention.getId()];
	}
	
	public double getDiscountRate() {
		return discountRate;
	}
	
	public SevereHypoglycemicEventParam.ReturnValue getTimeToSevereHypoglycemicEvent(T1DMPatient pat) {
		return hypoParam.getValue(pat);
	}

	public long getTimeToComplication(T1DMPatient pat, Complication complication) {
		final EnumSet<Complication> state = pat.getState();
		long time = Long.MAX_VALUE;
		final long time2Death = pat.getTimeToDeath();
		final double rnd = rndComplications[complication.ordinal()][pat.getIdentifier()];
		
		// FIXME: Esto no será válido una vez que calcule el RR a partir del HbA1c actual del patiente. En ese momento, debería gestionar esto desde el mismo paciente.
//		final boolean applyRiskReduction = (pat.getTs() < durationOfEffect[pat.getnIntervention()]);
		
		final double beta = Math.log(-rr10Complications[complication.ordinal()]+1)/LN09;
		final double rr = Math.exp(beta * (Math.log(pat.getHba1c())-lnReferenceHbA1c));
		switch(complication) {
		case BLI:
			// Already at retinopathy
			if (state.contains(Complication.RET)) {
				final long time2BLI = getAnnualBasedTimeToEvent(pat, invRET_BLI, rnd, rr);
				if (time2BLI < time2Death)
					time = time2BLI;
			}
			break;
		case CHD:
			double minInv = invDNC_CHD;
			if (state.contains(Complication.NEU)) {
				if (minInv < invNEU_CHD) {
					minInv = invNEU_CHD;
				}
			}
			if (state.contains(Complication.NPH)) {
				if (minInv < invNPH_CHD) {
					minInv = invNPH_CHD;
				}
			}
			if (state.contains(Complication.RET)) {
				if (minInv < invRET_CHD) {
					minInv = invRET_CHD;
				}
			}

			final long time2CHD = getAnnualBasedTimeToEvent(pat, minInv, rnd, rr);
			if (time2CHD < time2Death)
				time = time2CHD;				
			break;
		case ESRD:
			// Already at nephropathy
			if (state.contains(Complication.NPH)) {
				final long time2ESRD = getAnnualBasedTimeToEvent(pat, invNPH_ESRD, rnd, rr);
				if (time2ESRD < time2Death)
					time = time2ESRD;
			}
			break;
		case LEA:
			// Already at neuropathy
			if (state.contains(Complication.NEU)) {
				final long time2LEA = getAnnualBasedTimeToEvent(pat, invNEU_LEA, rnd, rr);
				if (time2LEA < time2Death)
					time = time2LEA;
			}
			break;
		case NEU:
			final long time2NEU = getAnnualBasedTimeToEvent(pat, invDNC_NEU, rnd, rr);
			if (time2NEU < time2Death)
				time = time2NEU;
			break;
		case NPH:
			// Already at neuropathy: we're assuming that the risk from neuropathy is higher than from no complications
			if (state.contains(Complication.NEU)) {
				final long time2NPH = getAnnualBasedTimeToEvent(pat, invNEU_NPH, rnd, rr);
				if (time2NPH < time2Death)
					time = time2NPH;
			}
			else {
				final long time2NPH = getAnnualBasedTimeToEvent(pat, invDNC_NPH, rnd, rr);
				if (time2NPH < time2Death)
					time = time2NPH;				
			}
			break;
		case RET:
			final long time2RET = getAnnualBasedTimeToEvent(pat, invDNC_RET, rnd, rr);
			if (time2RET < time2Death)
				time = time2RET;
			break;
		default:
			break;		
		}
		return time;
	}

	public long getTimeToDeath(T1DMPatient pat) {
		if (secParams.isCanadaValidation()) {
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
			double maxIMR = noComplicationsIMR;
			for (Complication comp : pat.getState()) {
				if (complicationsIMR[comp.ordinal()] > maxIMR) {
					maxIMR = complicationsIMR[comp.ordinal()];
				}
			}
			return allCausesDeath.getValue(pat, maxIMR);
		}
	}

	/**
	 * Returns true if the CHD complications are detailed; false otherwise
	 * @return True if the CHD complications are detailed; false otherwise
	 */
	public boolean isDetailedCHD() {
		return pCHDComplication != null;
	}
	
	public CHDComplication getCHDComplication(T1DMPatient pat) {
		if (!isDetailedCHD())
			return null;
		return CHDComplication.values()[pCHDComplication.generateInt()];
	}
	
	/**
	 * Generates a time to event based on annual risk. The time to event is absolute, i.e., can be used directly to schedule a new event. 
	 * @param pat A patient
	 * @param minusAvgTimeToEvent -1/(annual risk of the event)
	 * @param rnd A random number
	 * @param rr Relative risk for the patient
	 * @return a time to event based on annual risk
	 */
	public static long getAnnualBasedTimeToEvent(T1DMPatient pat, double minusAvgTimeToEvent, double rnd, double rr) {
		final double lifetime = pat.getAgeAtDeath() - pat.getAge();
		final double time = (minusAvgTimeToEvent / rr) * Math.log(rnd);
		return (time >= lifetime) ? Long.MAX_VALUE : pat.getTs() + Math.max(BasicConfigParams.MIN_TIME_TO_EVENT, pat.getSimulation().getTimeUnit().convert(time, TimeUnit.YEAR));
	}
	
	public void reset() {
		hypoParam.reset();
	}
	
	public double getRandomNumber() {
		return rng.draw();
	}
}
