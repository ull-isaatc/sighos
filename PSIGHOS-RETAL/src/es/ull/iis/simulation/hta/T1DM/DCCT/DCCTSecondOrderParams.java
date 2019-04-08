/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.DCCT;

import es.ull.iis.simulation.hta.T1DM.T1DMChronicComplications;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import es.ull.iis.simulation.hta.T1DM.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.T1DM.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParam;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleNEUSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleNPHSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.SimpleRETSubmodel;
import es.ull.iis.simulation.hta.T1DM.submodels.StandardSpainDeathSubmodel;
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
			SimpleNPHSubmodel.registerSecondOrder(this);
		}
		else {
			SheffieldRETSubmodel.registerSecondOrder(this);;
			SheffieldNPHSubmodel.registerSecondOrder(this);
		}
		SimpleCHDSubmodel.registerSecondOrder(this);
		SimpleNEUSubmodel.registerSecondOrder(this);
		
		DCCTSevereHypoglycemiaEvent.registerSecondOrder(this);

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
	public RandomVariate getBaselineDurationOfDiabetes() {
		// FIXME: Currently not using this, but probably should
		return RandomVariateFactory.getInstance("ConstantVariate", 0.0);
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
	public DeathSubmodel getDeathSubmodel() {
		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(getRngFirstOrder(), nPatients);

		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			dModel.addIMR(SimpleNPHSubmodel.NPH, getIMR(SimpleNPHSubmodel.NPH));
			dModel.addIMR(SimpleNPHSubmodel.ESRD, getIMR(SimpleNPHSubmodel.ESRD));
		}
		else {
			dModel.addIMR(SheffieldNPHSubmodel.ALB2, getIMR(SheffieldNPHSubmodel.ALB2));
			dModel.addIMR(SheffieldNPHSubmodel.ESRD, getIMR(SheffieldNPHSubmodel.ESRD));			
		}
		dModel.addIMR(SimpleNEUSubmodel.NEU, getIMR(SimpleNEUSubmodel.NEU));
		dModel.addIMR(SimpleNEUSubmodel.LEA, getIMR(SimpleNEUSubmodel.LEA));
		dModel.addIMR(SimpleCHDSubmodel.ANGINA, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.STROKE, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.HF, getIMR(T1DMChronicComplications.CHD));
		dModel.addIMR(SimpleCHDSubmodel.MI, getIMR(T1DMChronicComplications.CHD));
		return dModel;
	}
	
	@Override
	public ChronicComplicationSubmodel[] getComplicationSubmodels() {
		final ChronicComplicationSubmodel[] comps = new ChronicComplicationSubmodel[T1DMChronicComplications.values().length];
		
		// Adds neuropathy submodel
		comps[T1DMChronicComplications.NEU.ordinal()] = new SimpleNEUSubmodel(this);
		
		// Adds nephropathy and retinopathy submodels
		if (BasicConfigParams.USE_SIMPLE_MODELS) {
			comps[T1DMChronicComplications.NPH.ordinal()] = new SimpleNPHSubmodel(this);
			comps[T1DMChronicComplications.RET.ordinal()] = new SimpleRETSubmodel(this);
		}
		else {
			comps[T1DMChronicComplications.NPH.ordinal()] = new SheffieldNPHSubmodel(this);
			comps[T1DMChronicComplications.RET.ordinal()] = new SheffieldRETSubmodel(this);
		}
		
		// Adds major Cardiovascular disease submodel
		comps[T1DMChronicComplications.CHD.ordinal()] = new SimpleCHDSubmodel(this);
		
		return comps;
	}

	@Override
	public AcuteComplicationSubmodel[] getAcuteComplicationSubmodels() {
		return new AcuteComplicationSubmodel[] {new DCCTSevereHypoglycemiaEvent(this)};
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
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
				return pat.getBaselineHBA1c() - rnd.generate();
				
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
