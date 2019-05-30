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
import es.ull.iis.simulation.hta.diabetes.params.RRCalculator;
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
public class CanadaNPHSubmodel extends SecondOrderChronicComplicationSubmodel {
	public static DiabetesComplicationStage NPH = new DiabetesComplicationStage("NPH", "Neuropathy", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage ESRD = new DiabetesComplicationStage("ESRD", "End-Stage Renal Disease", DiabetesChronicComplications.NPH);
	public static DiabetesComplicationStage[] NPHSubstates = new DiabetesComplicationStage[] {NPH, ESRD};

	private static final double P_DNC_NPH = 0.0094;
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_ESRD = 0.072;
	private static final double RR_NPH = 0.742;
	private static final double C_NPH = 13;
	private static final double C_ESRD = 12808;
	private static final double TC_NPH = 80 - C_NPH;
	private static final double TC_ESRD = 28221 - C_ESRD;
	private static final double DU_NPH = CanadaSecondOrderParams.U_DNC - 0.575;
	private static final double DU_ESRD = CanadaSecondOrderParams.U_DNC - 0.49;
	
//	addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 8.87480916));
	private static final double REF_HBA1C = 9.1; 

	public enum NPHTransitions {
		HEALTHY_NPH,
		NPH_ESRD,
		NEU_NPH		
	}
	
	public CanadaNPHSubmodel() {
		super(DiabetesChronicComplications.NPH, EnumSet.of(DiabetesType.T1));
	}
	
	@Override
	public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(null, NPH), "", "", P_DNC_NPH));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(NPH, ESRD), "", "", P_NPH_ESRD));
		secParams.addProbParam(new SecondOrderParam(SecondOrderParamsRepository.getProbString(DiabetesChronicComplications.NEU, DiabetesChronicComplications.NPH),	"",	"",	P_NEU_NPH));		
		
		secParams.addOtherParam(new SecondOrderParam(SecondOrderParamsRepository.STR_RR_PREFIX + DiabetesChronicComplications.NPH.name(), 
				"%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", 
				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, SecondOrderParamsRepository.sdFrom95CI(new double[] {0.19, 0.32}))));

		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + NPH, "Cost of NPH", "", 2018, C_NPH, SecondOrderParamsRepository.getRandomVariateForCost(C_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_COST_PREFIX + ESRD, "Cost of ESRD", "", 2018, C_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(C_ESRD)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + NPH, "Transition cost to NPH", "", 2018, TC_NPH, SecondOrderParamsRepository.getRandomVariateForCost(TC_NPH)));
		secParams.addCostParam(new SecondOrderCostParam(SecondOrderParamsRepository.STR_TRANS_PREFIX + ESRD, "Transition cost to ESRD", "", 2018, TC_ESRD, SecondOrderParamsRepository.getRandomVariateForCost(TC_ESRD)));
		
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + NPH, "Disutility of NPH", "", DU_NPH));
		secParams.addUtilParam(new SecondOrderParam(SecondOrderParamsRepository.STR_DISUTILITY_PREFIX + ESRD, "Disutility of ESRD", "", DU_ESRD));

		addSecondOrderInitProportion(secParams);
	}
	
	@Override
	public ComplicationSubmodel getInstance(SecondOrderParamsRepository secParams) {
		return new Instance(secParams);
	}

	@Override
	public int getNStages() {
		return NPHSubstates.length;
	}

	@Override
	public DiabetesComplicationStage[] getStages() {
		return NPHSubstates;
	}

	@Override
	public int getNTransitions() {
		return NPHTransitions.values().length;
	}
	
	public class Instance extends ChronicComplicationSubmodel {
		
		/**
		 * 
		 */
		public Instance(SecondOrderParamsRepository secParams) {
			super(CanadaNPHSubmodel.this);
			
			final RRCalculator rrToNPH = new HbA1c10ReductionComplicationRR(secParams.getOtherParam(SecondOrderParamsRepository.STR_RR_PREFIX + NPH.name()), REF_HBA1C); 

			final int nPatients = secParams.getnPatients();
			final RandomNumber rng = secParams.getRngFirstOrder();

			addTime2Event(NPHTransitions.HEALTHY_NPH.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NPH), rrToNPH));
			addTime2Event(NPHTransitions.NPH_ESRD.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(NPH, ESRD), SecondOrderParamsRepository.NO_RR));
			// Assume the same RR from healthy to NPH than from NEU to NPH
			addTime2Event(NPHTransitions.NEU_NPH.ordinal(), 
					new AnnualRiskBasedTimeToEventParam(rng, nPatients, 
					secParams.getProbability(DiabetesChronicComplications.NEU, NPH), rrToNPH));

			addData(secParams, NPH);
			addData(secParams, ESRD);
		}

		@Override
		public DiabetesProgression getProgression(DiabetesPatient pat) {
			final DiabetesProgression prog = new DiabetesProgression();
			if (isEnabled()) {
				final TreeSet<DiabetesComplicationStage> state = pat.getDetailedState();
				// Checks whether there is somewhere to transit to
				if (!state.contains(ESRD)) {
					long timeToESRD = Long.MAX_VALUE;
					long timeToNPH = Long.MAX_VALUE;
					final long previousTimeToNPH = pat.getTimeToChronicComorbidity(NPH);
					final long previousTimeToESRD = pat.getTimeToChronicComorbidity(ESRD);
					long limit = pat.getTimeToDeath();
					if (limit > previousTimeToESRD)
						limit = previousTimeToESRD;
					if (state.contains(NPH)) {
						// RR from NPH to ESRD
						timeToESRD = getTimeToEvent(pat, NPHTransitions.NPH_ESRD.ordinal(), limit);
					}
					else {
						if (limit > previousTimeToNPH)
							limit = previousTimeToNPH;
						// RR from healthy to NPH (must be previous to ESRD and a (potential) formerly scheduled NPH event)
						timeToNPH = getTimeToEvent(pat, NPHTransitions.HEALTHY_NPH.ordinal(), limit);
						if (pat.hasComplication(DiabetesChronicComplications.NEU)) {
							// RR from NEU to NPH (must be previous to the former transition)
							if (limit > timeToNPH)
								limit = timeToNPH;
							final long altTimeToNPH = getTimeToEvent(pat, NPHTransitions.NEU_NPH.ordinal(), limit);
							if (altTimeToNPH < timeToNPH)
								timeToNPH = altTimeToNPH;						
						}
					}
					// Check previously scheduled events
					if (timeToNPH != Long.MAX_VALUE) {
						if (previousTimeToNPH < Long.MAX_VALUE) {
							prog.addCancelEvent(NPH);
						}
						prog.addNewEvent(NPH, timeToNPH);
					}
					if (timeToESRD != Long.MAX_VALUE) {
						if (previousTimeToESRD < Long.MAX_VALUE) {
							prog.addCancelEvent(ESRD);
						}
						prog.addNewEvent(ESRD, timeToESRD);
						// If the new ESRD event happens before a previously scheduled NPH event, the latter must be cancelled 
						if (previousTimeToNPH < Long.MAX_VALUE && timeToESRD < previousTimeToNPH)
							prog.addCancelEvent(NPH);
					}
				}
			}
			return prog;
		}

		@Override
		public double getAnnualCostWithinPeriod(DiabetesPatient pat, double initAge, double endAge) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getData(ESRD).getCosts()[0];
			else if (state.contains(NPH))
				return getData(NPH).getCosts()[0];
			return 0.0;
		}

		@Override
		public double getDisutility(DiabetesPatient pat, DisutilityCombinationMethod method) {
			final Collection<DiabetesComplicationStage> state = pat.getDetailedState();
			if (state.contains(ESRD))
				return getData(ESRD).getDisutility();
			else if (state.contains(NPH))
				return getData(NPH).getDisutility();
			return 0.0;
		}
	}

}
