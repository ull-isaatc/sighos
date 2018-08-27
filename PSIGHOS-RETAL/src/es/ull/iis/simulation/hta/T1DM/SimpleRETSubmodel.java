/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.T1DM.params.ComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SimpleRETSubmodel extends ComplicationSubmodel {
	public static T1DMComorbidity RET = new T1DMComorbidity("RET", "Retinopathy", MainComplications.RET);
	public static T1DMComorbidity BLI = new T1DMComorbidity("BLI", "Blindness", MainComplications.RET);
	public static T1DMComorbidity[] RETSubstates = new T1DMComorbidity[] {RET, BLI};
	
	private static final double REF_HBA1C = 9.1; 
	private static final double P_RET_BLI = 0.0038;
	private static final double P_DNC_RET = 0.0013;
	private static final double[] CI_DNC_RET = {0.00104, 0.00156};
	private static final double[] CI_RET_BLI = {0.00304, 0.00456};
	private static final double C_RET = 3651.31;
	private static final double C_BLI = 469.22;
	private static final double TC_RET = 0.0;
	private static final double TC_BLI = 0.0;
	private static final double DU_RET = 0.0156;
	private static final double DU_BLI = 0.0498;
	
	public enum RETTransitions {
		HEALTHY_RET,
		RET_BLI,
		HEALTHY_BLI;
	}
	private final double[] invProb;
	private final ComplicationRR[] rr;
	private final double [][] rnd;

	/**
	 * 
	 */
	public SimpleRETSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[RETTransitions.values().length];
		invProb[SimpleRETSubmodel.RETTransitions.HEALTHY_RET.ordinal()] = -1 / secParams.getProbability(RET);
		invProb[SimpleRETSubmodel.RETTransitions.HEALTHY_BLI.ordinal()] = -1 / secParams.getProbability(BLI);
		invProb[SimpleRETSubmodel.RETTransitions.RET_BLI.ordinal()] = -1 / secParams.getProbability(RET, BLI);
		
		this.rr = new ComplicationRR[RETTransitions.values().length];;
		rr[RETTransitions.HEALTHY_RET.ordinal()] = new HbA1c10ReductionComplicationRR(
				secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + RET.name()), REF_HBA1C);
		rr[RETTransitions.HEALTHY_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		rr[RETTransitions.RET_BLI.ordinal()] = SecondOrderParamsRepository.NO_RR;
		
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients][RETSubstates.length];
		for (int i = 0; i < nPatients; i++) {
			for (int j = 0; j < RETSubstates.length; j++) {
				rnd[i][j] = rng.draw();
			}
		}
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {
		final double[] paramsDNC_RET = SecondOrderParamsRepository.betaParametersFromNormal(P_DNC_RET, SecondOrderParamsRepository.sdFrom95CI(CI_DNC_RET));
		final double[] paramsRET_BLI = SecondOrderParamsRepository.betaParametersFromNormal(P_RET_BLI, SecondOrderParamsRepository.sdFrom95CI(CI_RET_BLI));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, SimpleRETSubmodel.RET), "", 
				"", P_DNC_RET, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(SimpleRETSubmodel.RET, SimpleRETSubmodel.BLI), "", 
				"", P_RET_BLI, RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		secParams.addProbParam(new SecondOrderParam(secParams.getProbString(null, SimpleRETSubmodel.BLI), "Probability of healthy to blindness", 
				"Sheffield (WESDR XXII)", 1.9e-6));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + MainComplications.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.29, 0.41}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + RET, "Cost of RET", "", 2015, C_RET, SecondOrderParamsRepository.getRandomVariateForCost(C_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "", 2015, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + RET, "Transition cost to RET", "", 2015, TC_RET, SecondOrderParamsRepository.getRandomVariateForCost(TC_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + BLI, "Transition cost to BLI", "", 2015, TC_BLI, SecondOrderParamsRepository.getRandomVariateForCost(TC_BLI)));
		
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
					// RR from healthy to BLI
					timeToBLI = getAnnualBasedTimeToEvent(pat, RETTransitions.HEALTHY_BLI, limit);
					if (limit > timeToBLI)
						limit = timeToBLI;
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
}
