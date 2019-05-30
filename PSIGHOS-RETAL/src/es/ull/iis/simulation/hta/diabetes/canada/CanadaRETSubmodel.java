/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes.canada;

import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeSet;

import es.ull.iis.simulation.hta.diabetes.DiabetesChronicComplications;
import es.ull.iis.simulation.hta.diabetes.DiabetesComplicationStage;
import es.ull.iis.simulation.hta.diabetes.DiabetesPatient;
import es.ull.iis.simulation.hta.diabetes.DiabetesProgression;
import es.ull.iis.simulation.hta.diabetes.DiabetesType;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.AnnualRiskBasedTimeToEventParam;
import es.ull.iis.simulation.hta.diabetes.params.HbA1c10ReductionComplicationRR;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.ComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SecondOrderChronicComplicationSubmodel;
import simkit.random.RandomNumber;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CanadaRETSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage RET = new DiabetesComplicationStage("RET", "Retinopathy", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage BLI = new DiabetesComplicationStage("BLI", "Blindness", DiabetesChronicComplications.RET);
	public static DiabetesComplicationStage[] RETSubstates = new DiabetesComplicationStage[] {RET, BLI};
	
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

	public CanadaRETSubmodel() {
		super(DiabetesChronicComplications.RET, EnumSet.of(DiabetesType.T1));
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, CanadaRETSubmodel.RET), "", 
				"", P_DNC_RET));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(CanadaRETSubmodel.RET, CanadaRETSubmodel.BLI), "", 
				"", P_RET_BLI));

		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.29, 0.41}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + RET, "Cost of RET", "", 2018, C_RET, SecondOrderParamsRepository.getRandomVariateForCost(C_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + BLI, "Cost of BLI", "", 2018, C_BLI, SecondOrderParamsRepository.getRandomVariateForCost(C_BLI)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + RET, "Transition cost to RET", "", 2018, TC_RET, SecondOrderParamsRepository.getRandomVariateForCost(TC_RET)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + BLI, "Transition cost to BLI", "", 2018, TC_BLI, SecondOrderParamsRepository.getRandomVariateForCost(TC_BLI)));
		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + RET, "Disutility of RET", "", DU_RET));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + BLI, "Disutility of BLI", "", DU_BLI));

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}

	@Override
	public int getNStages() {
		return RETSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return RETSubstates;
	}

	@Override
	public int getNTransitions() {
		return RETTransitions.values().length;
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(CanadaRETSubmodel.this);
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = secParams.getRngFirstOrder();

			addTime2Event(RETTransitions.HEALTHY_RET.ordinal(), 
				new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(RET), 
					new HbA1c10ReductionComplicationRR(
					secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + RET.name()), REF_HBA1C)));
			addTime2Event(RETTransitions.RET_BLI.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, secParams.getProbability(RET, BLI), SecondOrderParamsRepository.NO_RR));
			
			addData(secParams, RET);
			addData(secParams, BLI);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
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
						timeToBLI = getTimeToEvent(pat, RETTransitions.RET_BLI.ordinal(), limit);
					}
					else {
						if (limit > previousTimeToRET)
							limit = previousTimeToRET;
						// RR from healthy to RET (must be previous to BLI and a (potential) formerly scheduled RET event)
						timeToRET = getTimeToEvent(pat, RETTransitions.HEALTHY_RET.ordinal(), limit);
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

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(BLI))
				return getData(BLI).getCosts()[0];
			else if (state.contains(RET))
				return getData(RET).getCosts()[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(BLI))
				return getData(BLI).getDisutility();
			else if (state.contains(RET))
				return getData(RET).getDisutility();
			return 0.0;
		}
	}

}
