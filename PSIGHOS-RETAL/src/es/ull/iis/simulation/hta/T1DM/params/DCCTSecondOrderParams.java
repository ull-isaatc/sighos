/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.StandardSpainDeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.T1DMComorbidity;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.CombinationMethod;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;
	
	private final boolean useSimpleModels;
	/**
	 * @param baseCase
	 */
	public DCCTSecondOrderParams(boolean baseCase, int nPatients, boolean useSimpleModels) {
		super(baseCase, nPatients);
		this.useSimpleModels = useSimpleModels; 
		if (useSimpleModels) {
			SimpleRETSubmodel.registerSecondOrder(this);;
		}
		else {
			SheffieldRETSubmodel.registerSecondOrder(this);;
		}
		SimpleCHDSubmodel.registerSecondOrder(this);
		SimpleNPHSubmodel.registerSecondOrder(this);
		SimpleNEUSubmodel.registerSecondOrder(this);

		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", "Ly et al.", 0.234286582, 
				RandomVariateFactory.getInstance("BetaVariate", 23.19437163, 75.80562837)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", "Ly et al.", 
				0.020895447, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", -3.868224010, 1.421931924))));

		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1056/NEJMoa052187", 0.525));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "No discount", 0.0));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_AGE, "Average baseline age", "https://doi.org/10.1056/NEJMoa052187", 27));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_HBA1C, "Average baseline level of HBA1c", "https://doi.org/10.1056/NEJMoa052187", 9.1));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_HBA1C).getValue(baseCase));
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_AGE).getValue(baseCase));
	}

	@Override
	public RandomVariate getWeeklySensorUsage() {
		return RandomVariateFactory.getInstance("ConstantVariate", 7);
	}

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new DCCTIntervention1(0), new DCCTIntervention2(1)};
	}

	@Override
	public int getNInterventions() {
		return 2;
	}

	@Override
	public ComplicationRR getHypoRR() {
		final double[] rrValues = new double[getNInterventions()];
		rrValues[0] = 1.0;
		final SecondOrderParam param = otherParams.get(STR_RR_HYPO);
		final double rr = (param == null) ? 1.0 : param.getValue(baseCase);
		for (int i = 1; i < getNInterventions(); i++) {
			rrValues[i] = rr;
		}
		return new InterventionSpecificComplicationRR(rrValues);
	}

	@Override
	public DeathSubmodel getDeathSubmodel() {
		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(getRngFirstOrder(), nPatients);

		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleNPHSubmodel.NPH, getIMR(SimpleNPHSubmodel.NPH));
		dModel.addIMR(SimpleNPHSubmodel.ESRD, getIMR(SimpleNPHSubmodel.ESRD));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(MainComplications.CHD));
		return dModel;
	}
	
	@Override
	public ComplicationSubmodel[] getComplicationSubmodels() {
		final ComplicationSubmodel[] comps = new ComplicationSubmodel[MainComplications.values().length];
		
		// Adds nephropathy submodel
		comps[MainComplications.NPH.ordinal()] = new SimpleNPHSubmodel(this);
		
		// Adds neuropathy submodel
		comps[MainComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds retinopathy submodel
		if (useSimpleModels) {
			comps[MainComplications.RET.ordinal()] = new SimpleRETSubmodel(this);
		}
		else {
			comps[MainComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		}
		
		// Adds major Cardiovascular disease submodel
		comps[MainComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}
	
	@Override
	public CostCalculator getCostCalculator() {
		final StdCostCalculator calc = new StdCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode());
		for (final T1DMComorbidity subst : availableHealthStates) {
			final double[] costs = getCostsForHealthState(subst);
			calc.addCostForHealthState(subst, costs);
		}
		return calc;
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator() {
		final StdUtilityCalculator calc = new StdUtilityCalculator(CombinationMethod.ADD, getNoComplicationDisutility(), getGeneralPopulationUtility(), getHypoEventDisutility());
		for (final T1DMComorbidity subst : availableHealthStates) {
			calc.addDisutilityForHealthState(subst, getDisutilityForHealthState(subst));
		}
		return calc;
	}
	
	public class DCCTIntervention1 extends T1DMMonitoringIntervention {
		public final static String NAME = "Normal";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public DCCTIntervention1(int id) {
			super(id, NAME, NAME, BasicConfigParams.MAX_AGE);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c();
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return 0.0;
		}
	}

	public class DCCTIntervention2 extends T1DMMonitoringIntervention {
		public final static String NAME = "Intensive";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public DCCTIntervention2(int id) {
			super(id, NAME, NAME, YEARS_OF_EFFECT);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			return pat.getBaselineHBA1c() - (pat.isEffectActive() ? 1.9 : 0.0);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getAnnualCost(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getAnnualCost(T1DMPatient pat) {
			return 0.0;
		}
	}
}
