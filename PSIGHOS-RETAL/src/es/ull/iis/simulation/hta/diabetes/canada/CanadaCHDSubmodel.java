/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.CommonParams;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaCHDSubmodel extends ChronicComplicationSubmodel {
	public static DiabetesComplicationStage CHD = new DiabetesComplicationStage("CHD", "Cardiac heart disease", DiabetesChronicComplications.CHD);
	public static DiabetesComplicationStage[] CHDSubstates = new DiabetesComplicationStage[] {CHD}; 
	
//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_CHD = 0.0045;
	private static final double P_NEU_CHD = 0.02;
	private static final double P_NPH_CHD = 0.0224;
	private static final double P_RET_CHD = 0.0155;
	private static final double RR_CHD = 0.761;
	private static final double C_CHD = 4072;
	private static final double TC_CHD = 18682 - C_CHD;
	private static final double DU_CHD = CanadaSecondOrderParams.U_DNC - 0.685;
	
	public enum CHDTransitions {
		HEALTHY_CHD,
		NPH_CHD,
		RET_CHD,
		NEU_CHD		
	}
	private final double[] invProb;
	private final RRCalculator[] rr;
	private final double [] rnd;

	private final double[] costCHD;
	private final double duCHD;
	
	/**
	 * 
	 */
	public CanadaCHDSubmodel(SecondOrderParamsRepository secParams) {
		super();
		
		invProb = new double[CHDTransitions.values().length];
		invProb[CHDTransitions.HEALTHY_CHD.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.CHD);
		invProb[CHDTransitions.NEU_CHD.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.NEU, DiabetesChronicComplications.CHD);
		invProb[CHDTransitions.NPH_CHD.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.NPH, DiabetesChronicComplications.CHD);
		invProb[CHDTransitions.RET_CHD.ordinal()] = -1 / secParams.getProbability(DiabetesChronicComplications.RET, DiabetesChronicComplications.CHD);
		
		this.rr = new RRCalculator[CHDTransitions.values().length];
		final RRCalculator rrToCHD = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name()), REF_HBA1C);
		rr[CHDTransitions.HEALTHY_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NEU_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.NPH_CHD.ordinal()] = rrToCHD;
		rr[CHDTransitions.RET_CHD.ordinal()] = rrToCHD;
	
		final int nPatients = secParams.getnPatients();
		final RandomNumber rng = secParams.getRngFirstOrder();
		rnd = new double[nPatients];
		for (int i = 0; i < nPatients; i++) {
			rnd[i] = rng.draw();
		}
		
		costCHD = secParams.getCostsForChronicComplication(CHD);
		duCHD = secParams.getDisutilityForChronicComplication(CHD);
	}

	public static void registerSecondOrder(SecondOrderParamsRepository secParams) {

		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, DiabetesChronicComplications.CHD), "Probability of healthy to CHD", 
				"", P_DNC_CHD));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NEU, DiabetesChronicComplications.CHD), "", 
				"", P_NEU_CHD));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NPH, DiabetesChronicComplications.CHD), "", 
				"", P_NPH_CHD));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.RET, DiabetesChronicComplications.CHD), "", 
				"", P_RET_CHD));
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name(), 
				SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name(), 
				"Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 
				1.15, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.15, 0.92, 1.43, 1)));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + CHD, "Cost of year 2+ CHD", "", 2018, C_CHD, SecondOrderParamsRepository.getRandomVariateForCost(C_CHD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + CHD, "Cost of episode of CHD", "", 2018, TC_CHD, SecondOrderParamsRepository.getRandomVariateForCost(TC_CHD)));

		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + CHD, "Disutility of CHD", "", DU_CHD));

		secParams.registerComplication(DiabetesChronicComplications.CHD);
		secParams.registerComplicationStages(CHDSubstates);
	}

	public DiscreteSelectorVariate getRandomVariateForCHDComplications(SecondOrderParamsRepository secParams) {
		final double [] coef = new double[CHDSubstates.length];
		for (int i = 0; i < CHDSubstates.length; i++) {
			final DiabetesComplicationStage comp = CHDSubstates[i];
			coef[i] = secParams.getOtherParam(SecondOrderParamsRepository.STR_PROBABILITY_PREFIX + comp.name());
		}
		return (DiscreteSelectorVariate)RandomVariateFactory.getInstance("DiscreteSelectorVariate", coef);
	}

	
	@Override
	public DiabetesProgression getProgression(DiabetesPatient pat) {
		final DiabetesProgression prog = new DiabetesProgression();
		if (enable) {
			// If already has CHD, then nothing else to progress to
			if (!pat.hasComplication(DiabetesChronicComplications.CHD)) {
				long timeToCHD = pat.getTimeToDeath();
				if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NEU_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(DiabetesChronicComplications.NPH)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.NPH_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				if (pat.hasComplication(DiabetesChronicComplications.RET)) {
					final long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.RET_CHD);
					if (newTimeToCHD < timeToCHD)
						timeToCHD = newTimeToCHD;
				}
				long newTimeToCHD = getAnnualBasedTimeToEvent(pat, CHDTransitions.HEALTHY_CHD);
				if (newTimeToCHD < timeToCHD)
					timeToCHD = newTimeToCHD;
				if (timeToCHD < pat.getTimeToDeath()) {
					final long previousTime = pat.getTimeToChronicComorbidity(CHD);
					if (previousTime > timeToCHD) {
						if (previousTime < Long.MAX_VALUE) {
							prog.addCancelEvent(CHD);
						}
						prog.addNewEvent(CHD, timeToCHD);
					}
				}
			}
			
		}
		return prog;
	}

	@Override
	public int getNStages() {
		return CHDSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return CHDSubstates;
	}

	private long getAnnualBasedTimeToEvent(DiabetesPatient pat, CHDTransitions transition) {
		return CommonParams.getAnnualBasedTimeToEvent(pat, invProb[transition.ordinal()], rnd[pat.getIdentifier()], rr[transition.ordinal()].getRR(pat));
	}

	@Override
	public TreeSet<DiabetesComplicationStage> getInitialStage(DiabetesPatient pat) {
		return new TreeSet<>();
	}

	@Override
	public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
		return pat.getDetailedState().contains(CHD) ? costCHD[0] : 0.0;
	}

	@Override
	public double getCostOfComplication(DiabetesPatient pat, DiabetesComplicationStage newEvent) {
		return costCHD[1];
	}

	@Override
	public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
		return pat.getDetailedState().contains(CHD) ? duCHD : 0.0;
	}
}
