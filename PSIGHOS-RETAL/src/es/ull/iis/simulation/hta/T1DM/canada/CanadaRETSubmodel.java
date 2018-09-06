/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.canada;

import java.util.Collection;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.T1DMProgression;
import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaRETSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity RET = new T1DMComorbidity("RET", "Retinopathy", MainComplications.RET);
	public static T1DMComorbidity BLI = new T1DMComorbidity("BLI", "Blindness", MainComplications.RET);
	public static T1DMComorbidity[] RETSubstates = new T1DMComorbidity[] {RET, BLI};
	
//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_RET = 0.0764;
	private static final double P_RET_BLI = 0.0064;
	private static final double RR_RET = 0.661;

	private static final double C_RET = 52;
	private static final double C_BLI = 2482;
	private static final double TC_RET = 492 - C_RET;
	private static final double TC_BLI = 3483 - C_BLI;
	private static final double DU_RET = CanadaSecondOrderParams.U_DNC - 0.612;
	private static final double DU_BLI = CanadaSecondOrderParams.U_DNC - 0.569;
	
	public enum RETTransitions {
		HEALTHY_RET,
		RET_BLI,
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	private final double[] costRET;
	private final double[] costBLI;
	
	private final double duRET;
	private final double duBLI;

	/**
	 * 
	 */
	public CanadaRETSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[RETTransitions.values().length];
		invProb[CanadaRETSubmodel.RETTransitions.HEALTHY_RET.ordinal()] = -1 / secParams.getProbability(RET);
		invProb[CanadaRETSubmodel.RETTransitions.RET_BLI.ordinal()] = -1 / secParams.getProbability(RET, BLI);
		
		this.rr = new ComplicationRR[RETTransitions.values().length];;
		rr[RETTransitions.HEALTHY_RET.ordinal()] = new HbA1c10ReductionComplicationRR(
				secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + RET.name()), REF_HBA1C);
		rr[RETTransitions.RET_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][RETSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < RETSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
		
		costRET = secParams.getCostsForHealthState(RET);
		costBLI = secParams.getCostsForHealthState(BLI);

		duRET = secParams.getDisutilityForHealthState(RET);
		duBLI = secParams.getDisutilityForHealthState(BLI);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, CanadaRETSubmodel.RET), "", 
				"", P_DNC_RET));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(CanadaRETSubmodel.RET, CanadaRETSubmodel.BLI), "", 
				"", P_RET_BLI));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.29, 0.41}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + RET, "Cost of RET", "", 2018, C_RET, SecondOrderParamsRepository.getRandomVariateForCost(C_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "", 2018, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + RET, "Transition cost to RET", "", 2018, TC_RET, SecondOrderParamsRepository.getRandomVariateForCost(TC_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + BLI, "Transition cost to BLI", "", 2018, TC_BLI, SecondOrderParamsRepository.getRandomVariateForCost(TC_BLI)));
		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + RET, "Disutility of RET", "", DU_RET));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BLI, "Disutility of BLI", "", DU_BLI));
		
		secParams.registerComplication(MainComplications.RET);
		secParams.registerHealthStates(RETSubstates);		
	}

	@Override
	public T1DMProgression getNextComplication(T1DMPatient pat) {
		final T1DMProgression prog = new T1DMProgression();
		if (enable) {
			final TreeSet<T1DMComorbidity> state = pat.getDetailedState();
			// Checks whether there is somewhere to transit to
			if (!state.contains(BLI)) {
				long timeToBLI = Long.MAX_VALUE;
				long timeToRET = Long.MAX_VALUE;
				final long previousTimeToRET = pat.getTimeToChronicComorbidity(RET);
				final long previousTimeToBLI = pat.getTimeToChronicComorbidity(BLI);
				long limit = pat.getTimeToDeath();
				if (limit > previousTimeToBLI)
					limit = previousTimeToBLI;
				if (state.contains(RET)) {
					// RR from RET to BLI
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.RET_BLI, limit);
				}
				else {
					if (limit > previousTimeToRET)
						limit = previousTimeToRET;
					// RR from healthy to RET (must be previous to BLI and a (potential) formerly scheduled RET event)
					timeToRET = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_RET, limit);
				}
				// Check previously scheduled events
				if (timeToRET != Long.MAX_VALUE) {
					if (previousTimeToRET < Long.MAX_VALUE) {
						prog.addCancelEvent(RET);
					}
					prog.addNewEvent(RET, timeToRET);
				}
				if (timeToBLI != Long.MAX_VALUE) {
					if (previousTimeToBLI < Long.MAX_VALUE) {
						prog.addCancelEvent(BLI);
					}
					prog.addNewEvent(BLI, timeToBLI);
					// If the new BLI event happens before a previously scheduled RET event, the latter must be cancelled 
					if (previousTimeToRET < Long.MAX_VALUE && timeToBLI < previousTimeToRET)
						prog.addCancelEvent(RET);
				}
			}
		}
		return prog;
	}

	private long getAnnualBasedTimeToEvent(T1DMPatient pat, RETTransitions transition, long limit) {
		final int ord = RETTransitions.HEALTHY_RET.equals(transition) ? 0 : 1;
		return getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()][ord], rr[transition.ordinal()].getRR(pat), limit);
		
	}

	@Override
	public int getNSubstates() {
		return RETSubstates.length;
	}

	@Override
	public T1DMComorbidity[] getSubstates() {
		return RETSubstates;
	}
	
	@Override
	public TreeSet<T1DMComorbidity> getInitialState(T1DMPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(T1DMPatient pat, double initAge, double endAge) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(BLI))
			return costBLI[0];
		return costRET[0];
	}

	@Override
	public double getCostOfComplication(T1DMPatient pat, T1DMComorbidity newEvent) {
		if (BLI.equals(newEvent))
			return costBLI[1];
		return costRET[1];
	}

	@Override
	public double getDisutility(T1DMPatient pat, DisutilityCombinationMethod method) {
		final Collection<T1DMComorbidity> state = pat.getDetailedState();
		if (state.contains(BLI))
			return duBLI;
		return duRET;
	}
}
