/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c1PPComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaCHDSubmodel extends SecondOrderChronicComplicationSubmodel {
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
	
	public CanadaCHDSubmodel() {
		super(DiabetesChronicComplications.CHD, EnumSet.of(DiabetesType.T1));
	}

	@Override
	public int getNStages() {
		return CHDSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return CHDSubstates;
	}
	
	@Override
	public int getNTransitions() {
		return CHDTransitions.values().length;
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
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

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}
	
	public class Instance extends ChronicComplicationSubmodel {

		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(CanadaCHDSubmodel.this);
			
			final RRCalculator rrToCHD = new HbA1c1PPComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.CHD.name()), REF_HBA1C);
		
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			addTime2Event(CHDTransitions.HEALTHY_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.NEU_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(DiabetesChronicComplications.NEU, DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.NPH_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(DiabetesChronicComplications.NPH, DiabetesChronicComplications.CHD), rrToCHD));
			addTime2Event(CHDTransitions.RET_CHD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(DiabetesChronicComplications.RET, DiabetesChronicComplications.CHD), rrToCHD));
			setStageInstance(CHD, secParams);
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
			if (isEnabled()) {
				// If already has CHD, then nothing else to progress to
				if (!pat.hasComplication(DiabetesChronicComplications.CHD)) {
					long timeToCHD = pat.getTimeToDeath();
					if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.NEU_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					if (pat.hasComplication(DiabetesChronicComplications.NPH)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.NPH_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					if (pat.hasComplication(DiabetesChronicComplications.RET)) {
						final long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.RET_CHD.ordinal(), timeToCHD);
						if (newTimeToCHD < timeToCHD)
							timeToCHD = newTimeToCHD;
					}
					long newTimeToCHD = getTimeToEvent(pat, CHDTransitions.HEALTHY_CHD.ordinal(), timeToCHD);
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
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			return pat.getDetailedState().contains(CHD) ? getCosts(CHD)[0] : 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			return pat.getDetailedState().contains(CHD) ? getDisutility(CHD) : 0.0;
		}
	}
}
