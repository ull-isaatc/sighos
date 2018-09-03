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
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.params.UtilityCalculator.DisutilityCombinationMethod;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParamsRepository {
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;
	private static final double BASELINE_HBA1C_MIN = 6.6; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final double BASELINE_HBA1C_AVG = 8.9; // DCCT: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2866072/
	private static final double BASELINE_HBA1C_SD = 1.6; // DCCT: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2866072/
	private static final int BASELINE_AGE_MIN = 13; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_MAX = 40; // Design of DCCT: http://diabetes.diabetesjournals.org/content/35/5/530
	private static final int BASELINE_AGE_AVG = 27; // DCCT: https://www.ncbi.nlm.nih.gov/pmc/articles/PMC2866072/
	
	
	/**
	 * @param baseCase
	 */
	public DCCTSecondOrderParams() {
		super();
		BasicConfigParams.MIN_AGE = BASELINE_AGE_MIN;
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
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
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", 
				"DCCT: https://www.ncbi.nlm.nih.gov/pubmed/9000705", 
				0.187, RandomVariateFactory.getInstance("BetaVariate", 18.513, 80.487)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", 
				"DCCT: https://www.ncbi.nlm.nih.gov/pubmed/9000705", 
				3.27272727, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", 1.1856237, 0.22319455))));

		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1056/NEJMoa052187", 0.525));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "No discount", 0.0));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		if (BasicConfigParams.USE_FIXED_BASELINE_HBA1C)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_HBA1C_AVG);
			
		final double alfa = ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD) * ((BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN) / BASELINE_HBA1C_SD);
		final double beta = (BASELINE_HBA1C_SD * BASELINE_HBA1C_SD) / (BASELINE_HBA1C_AVG - BASELINE_HBA1C_MIN);
		final RandomVariate rnd = RandomVariateFactory.getInstance("GammaVariate", alfa, beta);
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, 1.0, BASELINE_HBA1C_MIN);
	}
	
	@Override
	public RandomVariate getBaselineAge() {
		if (BasicConfigParams.USE_FIXED_BASELINE_AGE)
			return RandomVariateFactory.getInstance("ConstantVariate", BASELINE_AGE_AVG);
		// 28.4 has been established empirically to get a sd of 7.
		final double[] betaParams = betaParametersFromEmpiricData(BASELINE_AGE_AVG, 28.4, BASELINE_AGE_MIN, BASELINE_AGE_MAX);
		final RandomVariate rnd = RandomVariateFactory.getInstance("BetaVariate", betaParams[0], betaParams[1]); 
		return RandomVariateFactory.getInstance("ScaledVariate", rnd, BASELINE_AGE_MAX - BASELINE_AGE_MIN, BASELINE_AGE_MIN);
	}

	@Override
	public T1DMMonitoringIntervention[] getInterventions() {
		return new T1DMMonitoringIntervention[] {new ConventionalTherapy(0), new IntensiveTherapy(1)};
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
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
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
	public CostCalculator getCostCalculator(ComplicationSubmodel[] submodels) {
		return new SubmodelCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode(), submodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(ComplicationSubmodel[] submodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, getNoComplicationDisutility(), getGeneralPopulationUtility(), getHypoEventDisutility(), submodels);
	}
	
	public class ConventionalTherapy extends T1DMMonitoringIntervention {
		public final static String NAME = "Normal";
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public ConventionalTherapy(int id) {
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

	public class IntensiveTherapy extends T1DMMonitoringIntervention {
		public final static String NAME = "Intensive";
		private final RandomVariate rnd; 
		/**
		 * @param id
		 * @param shortName
		 * @param description
		 */
		public IntensiveTherapy(int id) {
			super(id, NAME, NAME, YEARS_OF_EFFECT);
			rnd = RandomVariateFactory.getInstance("NormalVariate", 1.5, 1.1);
		}

		/* (non-Javadoc)
		 * @see es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention#getHBA1cLevel(es.ull.iis.simulation.hta.T1DM.T1DMPatient)
		 */
		@Override
		public double getHBA1cLevel(T1DMPatient pat) {
			if (pat.isEffectActive()) {
				return pat.getBaselineHBA1c() - Math.max(0.0, rnd.generate());
				
			}
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
}
