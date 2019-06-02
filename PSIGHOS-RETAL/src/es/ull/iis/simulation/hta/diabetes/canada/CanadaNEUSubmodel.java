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
public class CanadaNEUSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage NEU = new DiabetesComplicationStage("NEU", "Neuropathy", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage LEA = new DiabetesComplicationStage("LEA", "Low extremity amputation", DiabetesChronicComplications.NEU);
	public static DiabetesComplicationStage[] NEUSubstates = new DiabetesComplicationStage[] {NEU, LEA};

//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 
	private static final double P_DNC_NEU = 0.0235;
	private static final double P_NEU_LEA = 0.12;
	private static final double RR_NEU = 0.624;
	private static final double C_NEU = 192;
	private static final double C_LEA = 6024;
	private static final double TC_NEU = 192 - C_NEU;
	private static final double TC_LEA = 43984 - C_LEA;

	private static final double DU_NEU = CanadaSecondOrderParams.U_DNC - 0.624;
	private static final double DU_LEA = CanadaSecondOrderParams.U_DNC - 0.534;

	public enum NEUTransitions {
		HEALTHY_NEU,
		NEU_LEA,
	}

	public CanadaNEUSubmodel() {
		super(DiabetesChronicComplications.NEU, EnumSet.of(DiabetesType.T1));
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NEU), 
				"",	"", P_DNC_NEU));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NEU, LEA), 
				"",	"",	P_NEU_LEA));

//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainChronicComplications.NEU.name(), "Beta for confirmed clinical neuropathy", 
//		"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 5.3));
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU.name(), 
				"%risk reducion for combined groups for confirmed clinical neuropathy", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.18, 0.40}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NEU, "Cost of NEU", "", 2018, C_NEU, SecondOrderParamsRepository.getRandomVariateForCost(C_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + LEA, "Cost of LEA", "", 2018, C_LEA, SecondOrderParamsRepository.getRandomVariateForCost(C_LEA)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NEU, "Transition cost to NEU", "", 2018, TC_NEU, SecondOrderParamsRepository.getRandomVariateForCost(TC_NEU)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + LEA, "Transition cost to LEA", "", 2018, TC_LEA, SecondOrderParamsRepository.getRandomVariateForCost(TC_LEA)));

		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NEU, "Disutility of NEU", "", DU_NEU));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + LEA, "Disutility of LEA", "", DU_LEA));

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}
	
	@Override
	public int getNStages() {
		return NEUSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return NEUSubstates;
	}

	@Override
	public int getNTransitions() {
		return NEUTransitions.values().length;
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(CanadaNEUSubmodel.this);
			
			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = SecondOrderParamsRepository.getRNG_FIRST_ORDER();

			addTime2Event(NEUTransitions.HEALTHY_NEU.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU), new HbA1c10ReductionComplicationRR(
							secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NEU.name()), REF_HBA1C)));
			addTime2Event(NEUTransitions.NEU_LEA.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NEU, LEA), SecondOrderParamsRepository.NO_RR));
			
			addData(secParams, NEU);
			addData(secParams, LEA);
		}
		
		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(LEA)) {
					long timeToLEA = Long.MAX_VALUE;
					long timeToNEU = Long.MAX_VALUE;
					final long previousTimeToNEU = pat.getTimeToChronicComorbidity(NEU);
					final long previousTimeToLEA = pat.getTimeToChronicComorbidity(LEA);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToLEA)
						limit = previousTimeToLEA;
					if (state.contains(NEU)) {
						// RR from NEU to LEA
						timeToLEA = getTimeToEvent(pat, NEUTransitions.NEU_LEA.ordinal(), limit);
					}
					else {
						if (limit > previousTimeToNEU)
							limit = previousTimeToNEU;
						// RR from healthy to NEU (must be previous to LEA and a (potential) formerly scheduled NEU event)
						timeToNEU = getTimeToEvent(pat, NEUTransitions.HEALTHY_NEU.ordinal(), limit);
					}
					// Check previously scheduled events
					if (timeToNEU != Long.MAX_VALUE) {
						if (previousTimeToNEU < Long.MAX_VALUE) {
							prog.addCancelEvent(NEU);
						}
						prog.addNewEvent(NEU, timeToNEU);
					}
					if (timeToLEA != Long.MAX_VALUE) {
						if (previousTimeToLEA < Long.MAX_VALUE) {
							prog.addCancelEvent(LEA);
						}
						prog.addNewEvent(LEA, timeToLEA);
						// If the new LEA event happens before a previously scheduled NEU event, the latter must be cancelled 
						if (previousTimeToNEU < Long.MAX_VALUE && timeToLEA < previousTimeToNEU)
							prog.addCancelEvent(NEU);
					}
				}
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getData(LEA).getCosts()[0];
			else if (state.contains(NEU))
				return getData(NEU).getCosts()[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(LEA))
				return getData(LEA).getDisutility();
			else if (state.contains(NEU))
				return getData(NEU).getDisutility();
			return 0.0;
		}
		
	}
}
