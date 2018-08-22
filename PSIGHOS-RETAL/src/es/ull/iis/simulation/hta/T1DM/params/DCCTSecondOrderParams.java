/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import java.util.EnumSet;

import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParams {
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;

	private static final double P_DNC_RET = 0.0013;
	private static final double P_DNC_NEU = 0.021474479; // Sheffield (DCCT, Moss et al) Ajustado una probabilidad inicial de 0.0354 para HbA1c de 9.1 (usando fórmula de Sheffield)
	private static final double P_DNC_NPH = 0.032090096; // Sheffield (DCCT, Moss et al) Ajustado una probabilidad inicial de 0.0436 para HbA1c de 9.1 (usando fórmula de Sheffield)
	private static final double P_DNC_CHD = 0.031;
	private static final double P_NEU_CHD = 0.029;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_CHD = 0.022;
	private static final double P_RET_BLI = 0.0038;
	private static final double P_RET_CHD = 0.028;
	
	private static final double[] CI_DNC_RET = {0.00104, 0.00156};
	private static final double[] CI_DNC_NEU = {0.02, 0.055};
	private static final double[] CI_DNC_CHD = {0.018, 0.048};
	private static final double[] CI_NEU_CHD = {0.016, 0.044};
	private static final double[] CI_NEU_LEA = {0.01232, 0.01848};
	private static final double[] CI_NEU_NPH = {0.055, 0.149};
	private static final double[] CI_NPH_CHD = {0.013, 0.034};
	private static final double[] CI_RET_BLI = {0.00304, 0.00456};
	private static final double[] CI_RET_CHD = {0.016, 0.043};
	
	/**
	 * @param baseCase
	 */
	public DCCTSecondOrderParams(boolean baseCase) {
		super(baseCase);
		final double[] paramsDNC_RET = betaParametersFromNormal(P_DNC_RET, sdFrom95CI(CI_DNC_RET));
		final double[] paramsDNC_NEU = betaParametersFromNormal(P_DNC_NEU, sdFrom95CI(CI_DNC_NEU));
		final double[] paramsDNC_CHD = betaParametersFromNormal(P_DNC_CHD, sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = betaParametersFromNormal(P_NEU_CHD, sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNEU_LEA = betaParametersFromNormal(P_NEU_LEA, sdFrom95CI(CI_NEU_LEA));
		final double[] paramsNEU_NPH = betaParametersFromNormal(P_NEU_NPH, sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_CHD = betaParametersFromNormal(P_NPH_CHD, sdFrom95CI(CI_NPH_CHD));
		final double[] paramsRET_BLI = betaParametersFromNormal(P_RET_BLI, sdFrom95CI(CI_RET_BLI));
		final double[] paramsRET_CHD = betaParametersFromNormal(P_RET_CHD, sdFrom95CI(CI_RET_CHD));

		addProbParam(new SecondOrderParam(STR_P_DNC_RET, STR_P_DNC_RET, "", P_DNC_RET, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		addProbParam(new SecondOrderParam(STR_P_DNC_NEU, "Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", P_DNC_NEU, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_NEU[0], paramsDNC_NEU[1])));
		addProbParam(new SecondOrderParam(STR_P_DNC_NPH, STR_P_DNC_NPH, "", P_DNC_NPH));
		addProbParam(new SecondOrderParam(STR_P_DNC_CHD, STR_P_DNC_CHD, "", P_DNC_CHD, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_CHD, STR_P_NEU_CHD, "", P_NEU_CHD, 
				RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_LEA, STR_P_NEU_LEA, "Klein et al. 2004", P_NEU_LEA, 
				RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1])));
		addProbParam(new SecondOrderParam(STR_P_NEU_NPH, STR_P_NEU_NPH, "", P_NEU_NPH, 
				RandomVariateFactory.getInstance("BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1])));
		addProbParam(new SecondOrderParam(STR_P_NPH_CHD, STR_P_NPH_CHD, "", P_NPH_CHD, 
				RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		addProbParam(new SecondOrderParam(STR_P_NPH_ESRD, STR_P_NPH_ESRD, "", 0.1579));
		addProbParam(new SecondOrderParam(STR_P_RET_BLI, STR_P_RET_BLI, "", P_RET_BLI, 
				RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		addProbParam(new SecondOrderParam(STR_P_RET_CHD, STR_P_RET_CHD, "", P_RET_CHD, 
				RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));

		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", "Ly et al.", 0.234286582, 
				RandomVariateFactory.getInstance("BetaVariate", 23.19437163, 75.80562837)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", "Ly et al.", 
				0.020895447, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", -3.868224010, 1.421931924))));

		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.CHD.name(), STR_RR_PREFIX + Complication.CHD.name(), "Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 
				1.15, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.15, 0.92, 1.43, 1)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NPH.name(), "%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, sdFrom95CI(new double[] {0.19, 0.32}))));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NEU.name(), "Beta for confirmed clinical neuropathy", 
//				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 5.3));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.NEU.name(), "%risk reducion for combined groups for confirmed clinical neuropathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, sdFrom95CI(new double[] {0.18, 0.40}))));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + Complication.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, sdFrom95CI(new double[] {0.29, 0.41}))));
		addOtherParam(new SecondOrderParam(STR_REF_HBA1C, STR_REF_HBA1C, "", 9.1));
		
		addOtherParam(new SecondOrderParam(STR_IMR_DNC, "Increased mortaility risk due to T1 DM with no complications", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_RET, "Increased mortality risk due to retinopathy", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_NEU, "Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", "https://doi.org/10.2337/diacare.28.3.617", 1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_NPH, "Increased mortality risk due to severe proteinuria", "https://doi.org/10.2337/diacare.28.3.617", 2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_CHD, "Increased mortality risk due to macrovascular disease", "https://doi.org/10.2337/diacare.28.3.617", 1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_ESRD, "Increased mortality risk due to increased serum creatinine", "https://doi.org/10.2337/diacare.28.3.617", 4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_BLI, "Increased mortality risk due to blindness", "Assumption", 1.0));
		addOtherParam(new SecondOrderParam(STR_IMR_LEA, "Increased mortality risk due to peripheral neuropathy (amputation)", "https://doi.org/10.2337/diacare.28.3.617", 3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));

		addOtherParam(new SecondOrderParam(STR_P_MI, "Probability of a CHD complication being Myocardial Infarction", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.53, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.53)));
		addOtherParam(new SecondOrderParam(STR_P_STROKE, "Probability of a CHD complication being Stroke", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.07, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.07)));
		addOtherParam(new SecondOrderParam(STR_P_ANGINA, "Probability of a CHD complication being Angina", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.28, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.28)));
		addOtherParam(new SecondOrderParam(STR_P_HF, "Probability of a CHD complication being Heart Failure", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.12, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.12)));

		addOtherParam(new SecondOrderParam(STR_P_MAN, "Probability of sex = male", "https://doi.org/10.1056/NEJMoa052187", 0.525));
		addOtherParam(new SecondOrderParam(STR_DISCOUNT_RATE, "Discount rate", "No discount", 0.0));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_AGE, "Average baseline age", "https://doi.org/10.1056/NEJMoa052187", 27));
		addOtherParam(new SecondOrderParam(STR_AVG_BASELINE_HBA1C, "Average baseline level of HBA1c", "https://doi.org/10.1056/NEJMoa052187", 9.1));
	}

	@Override
	public RandomVariate getBaselineHBA1c() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_HBA1C).getValue());
	}

	@Override
	public RandomVariate getBaselineAge() {
		return RandomVariateFactory.getInstance("ConstantVariate", otherParams.get(STR_AVG_BASELINE_AGE).getValue());
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
	public ComplicationRR[] getComplicationRRs() {
		final double referenceHbA1c = otherParams.get(STR_REF_HBA1C).getValue();
		final ComplicationRR[] rr = new ComplicationRR[N_COMPLICATIONS];
		// First computes the relative risks for all the complications but CHD
		final EnumSet<Complication> allButCHD = EnumSet.complementOf(EnumSet.of(Complication.CHD/*, Complication.NEU*/));
		for (Complication comp : allButCHD) {
			final SecondOrderParam param = otherParams.get(STR_RR_PREFIX + comp.name());
			rr[comp.ordinal()] = (param == null) ? new StdComplicationRR(1.0) : new HbA1c10ReductionComplicationRR(param.getValue(), referenceHbA1c);
		}
		// Now CHD
		final SecondOrderParam param = otherParams.get(STR_RR_PREFIX + Complication.CHD.name());
		rr[Complication.CHD.ordinal()] = new HbA1c1PPComplicationRR((param == null) ? 1.0 : param.getValue(), referenceHbA1c);
		// ...and NEU
//		final SecondOrderParam paramNEU = otherParams.get(STR_RR_PREFIX + Complication.NEU.name());
//		rr[Complication.NEU.ordinal()] = new SheffieldComplicationRR((paramNEU == null) ? 0.0 : paramNEU.getValue());
		return rr;
	}

	@Override
	public ComplicationRR getHypoRR() {
		final double[] rrValues = new double[getNInterventions()];
		rrValues[0] = 1.0;
		final SecondOrderParam param = otherParams.get(STR_RR_HYPO);
		final double rr = (param == null) ? 1.0 : param.getValue();
		for (int i = 1; i < getNInterventions(); i++) {
			rrValues[i] = rr;
		}
		return new InterventionSpecificComplicationRR(rrValues);
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
