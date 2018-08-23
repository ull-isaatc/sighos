/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM.params;

import es.ull.iis.simulation.hta.T1DM.ComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.DeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.MainComplications;
import es.ull.iis.simulation.hta.T1DM.SimpleCHDComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNEUComplicationSubmodel.NEUTransitions;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleNPHComplicationSubmodel.NPHTransitions;
import es.ull.iis.simulation.hta.T1DM.SimpleRETComplicationSubmodel;
import es.ull.iis.simulation.hta.T1DM.SimpleRETComplicationSubmodel.RETTransitions;
import es.ull.iis.simulation.hta.T1DM.StandardSpainDeathSubmodel;
import es.ull.iis.simulation.hta.T1DM.T1DMHealthState;
import es.ull.iis.simulation.hta.T1DM.T1DMMonitoringIntervention;
import es.ull.iis.simulation.hta.T1DM.T1DMPatient;
import simkit.random.DiscreteSelectorVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class DCCTSecondOrderParams extends SecondOrderParams {
	/** Duration of effect of the intervention (assumption) */
	private static final double YEARS_OF_EFFECT = 20.0;

	private static final double REF_HBA1C = 9.1; 
//	private static final double BETA_BGRET = 10.10;
//	private static final double BETA_PRET = 6.30;
//	private static final double BETA_ME = 1.20;
	private static final double P_DNC_CHD = 0.031;
	private static final double P_NEU_CHD = 0.029;
	private static final double P_NEU_LEA = 0.0154; // Klein et al. 2004. También usado en Sheffield (DCCT, Moss et al)
	private static final double P_NEU_NPH = 0.097;
	private static final double P_NPH_CHD = 0.022;
	private static final double P_RET_BLI = 0.0038;
	private static final double P_RET_CHD = 0.028;
	private static final double P_DNC_RET = 0.0013;
	
//	private static final double[] CI_DNC_BGRET = {0.00104, 0.00156};
	private static final double[] CI_DNC_RET = {0.00104, 0.00156};
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
	public DCCTSecondOrderParams(boolean baseCase, int nPatients) {
		super(baseCase, nPatients);
		registerHealthStates(SimpleRETComplicationSubmodel.RETSubstates);
		registerHealthStates(SimpleCHDComplicationSubmodel.CHDSubstates);
		registerHealthStates(SimpleNPHComplicationSubmodel.NPHSubstates);
		registerHealthStates(SimpleNEUComplicationSubmodel.NEUSubstates);
		
		final double coefHbA1c = REF_HBA1C/10;
		// From Sheffield
		final double pDNC_NEU = 0.0354 * Math.pow(coefHbA1c, 5.3);
		final double pDNC_NPH = 0.0436 * Math.pow(coefHbA1c, 3.25);
//		final double pDNC_BGRET = 0.0454 * Math.pow(coefHbA1c, BETA_BGRET);
//		final double pDNC_PRET = 0.0013 * Math.pow(coefHbA1c, BETA_PRET);
//		final double pDNC_ME = 0.0012 * Math.pow(coefHbA1c, BETA_ME);
//		final double pBGRET_PRET = 0.0595 * Math.pow(coefHbA1c, BETA_PRET);
//		final double pBGRET_ME = 0.0512 * Math.pow(coefHbA1c, BETA_ME);
		
		final double[] paramsDNC_CHD = betaParametersFromNormal(P_DNC_CHD, sdFrom95CI(CI_DNC_CHD));
		final double[] paramsNEU_CHD = betaParametersFromNormal(P_NEU_CHD, sdFrom95CI(CI_NEU_CHD));
		final double[] paramsNEU_LEA = betaParametersFromNormal(P_NEU_LEA, sdFrom95CI(CI_NEU_LEA));
		final double[] paramsNEU_NPH = betaParametersFromNormal(P_NEU_NPH, sdFrom95CI(CI_NEU_NPH));
		final double[] paramsNPH_CHD = betaParametersFromNormal(P_NPH_CHD, sdFrom95CI(CI_NPH_CHD));
		final double[] paramsRET_BLI = betaParametersFromNormal(P_RET_BLI, sdFrom95CI(CI_RET_BLI));
		final double[] paramsRET_CHD = betaParametersFromNormal(P_RET_CHD, sdFrom95CI(CI_RET_CHD));
		final double[] paramsDNC_RET = betaParametersFromNormal(P_DNC_RET, sdFrom95CI(CI_DNC_RET));

		addProbParam(new SecondOrderParam(getProbString(null, SimpleNEUComplicationSubmodel.NEU), "Probability of healthy to clinically confirmed neuropathy, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", pDNC_NEU));
		addProbParam(new SecondOrderParam(getProbString(null, SimpleNEUComplicationSubmodel.LEA), "Probability of healthy to PAD with amputation, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 0.0003));
		addProbParam(new SecondOrderParam(getProbString(SimpleNEUComplicationSubmodel.NEU, SimpleNEUComplicationSubmodel.LEA), "Probability of clinically confirmed neuropathy to PAD with amputation", 
				"Klein et al. 2004 (also Sheffield)", P_NEU_LEA, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_LEA[0], paramsNEU_LEA[1])));
		addProbParam(new SecondOrderParam(getProbString(null, SimpleNPHComplicationSubmodel.NPH), "Probability of healthy to microalbuminutia, as processed in Sheffield Type 1 model", 
				"", pDNC_NPH));
		addProbParam(new SecondOrderParam(getProbString(SimpleNPHComplicationSubmodel.NPH, SimpleNPHComplicationSubmodel.ESRD), "", 
				"", 0.1579));
		addProbParam(new SecondOrderParam(getProbString(null, SimpleNPHComplicationSubmodel.ESRD), "Probability of healthy to ESRD, as processed in Sheffield Type 1 model", 
				"DCCT 1995 https://doi.org/10.7326/0003-4819-122-8-199504150-00001", 0.0002));
		addProbParam(new SecondOrderParam(getProbString(null, SimpleRETComplicationSubmodel.RET), "", 
				"", P_DNC_RET, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_RET[0], paramsDNC_RET[1])));
		addProbParam(new SecondOrderParam(getProbString(SimpleRETComplicationSubmodel.RET, SimpleRETComplicationSubmodel.BLI), "", 
				"", P_RET_BLI, RandomVariateFactory.getInstance("BetaVariate", paramsRET_BLI[0], paramsRET_BLI[1])));
		addProbParam(new SecondOrderParam(getProbString(null, SimpleRETComplicationSubmodel.BLI), "Probability of healthy to blindness", 
				"Sheffield (WESDR XXII)", 1.9e-6));

		addProbParam(new SecondOrderParam(getProbString(null, MainComplications.CHD), "Probability of healthy to CHD", 
				"", P_DNC_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsDNC_CHD[0], paramsDNC_CHD[1])));

		addProbParam(new SecondOrderParam(getProbString(MainComplications.NEU, MainComplications.CHD), "", 
				"", P_NEU_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_CHD[0], paramsNEU_CHD[1])));
		addProbParam(new SecondOrderParam(getProbString(MainComplications.NEU, MainComplications.NPH), "", 
				"", P_NEU_NPH, RandomVariateFactory.getInstance("BetaVariate", paramsNEU_NPH[0], paramsNEU_NPH[1])));
		addProbParam(new SecondOrderParam(getProbString(MainComplications.NPH, MainComplications.CHD), "", 
				"", P_NPH_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsNPH_CHD[0], paramsNPH_CHD[1])));
		addProbParam(new SecondOrderParam(getProbString(MainComplications.RET, MainComplications.CHD), "", 
				"", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));
		
//		addProbParam(new SecondOrderParam(getProbString(null, MainComplications.BGRET), "Probability of healthy to background retinopathy", 
//				"Sheffield (WESDR XXII)", pDNC_BGRET));
//		addProbParam(new SecondOrderParam(getProbString(null, MainComplications.PRET), "Probability of healthy to proliferative retinopathy", 
//				"Sheffield (WESDR XXII)", pDNC_PRET));
//		addProbParam(new SecondOrderParam(getProbString(null, MainComplications.ME),	"Probability of healthy to macular edema", 
//				"Sheffield (WESDR XXII)", pDNC_ME));
//		addProbParam(new SecondOrderParam(getProbString(MainComplications.BGRET, MainComplications.PRET),	"Probability of BG ret to proliferative retinopathy", 
//				"Sheffield (WESDR XXII)", pBGRET_PRET));
//		addProbParam(new SecondOrderParam(getProbString(MainComplications.BGRET, MainComplications.ME),	"Probability of BG ret to ME", 
//				"Sheffield (WESDR XXII)", pBGRET_ME));
//		addProbParam(new SecondOrderParam(getProbString(MainComplications.BGRET, MainComplications.BLI),	"Probability of BG ret to blindness", 
//				"Sheffield (WESDR XXII)", 0.0001));
//		addProbParam(new SecondOrderParam(getProbString(MainComplications.PRET, MainComplications.BLI),	"Probability of Proliferative ret to blindness", 
//				"Sheffield (WESDR XXII)", 0.0038));
//		addProbParam(new SecondOrderParam(getProbString(MainComplications.ME, MainComplications.BLI),	"Probability of macular edema to blindness", 
//				"Sheffield (WESDR XXII)", 0.0016));

//		addProbParam(new SecondOrderParam(getProbString(MainComplications.BGRET, MainComplications.CHD), "", 
//				"", P_RET_CHD, RandomVariateFactory.getInstance("BetaVariate", paramsRET_CHD[0], paramsRET_CHD[1])));



		// Severe hypoglycemic episodes
		final double[] paramsDeathHypo = betaParametersFromNormal(0.0063, sdFrom95CI(new double[]{0.0058, 0.0068}));
		addProbParam(new SecondOrderParam(STR_P_HYPO, "Annual probability of severe hypoglycemic episode (adjusted from rate/100 patient-month)", "Ly et al.", 0.234286582, 
				RandomVariateFactory.getInstance("BetaVariate", 23.19437163, 75.80562837)));
		addProbParam(new SecondOrderParam(STR_P_DEATH_HYPO, "Probability of death after severe hypoglycemic episode", "Canada", 0.0063, 
				RandomVariateFactory.getInstance("BetaVariate", paramsDeathHypo[0], paramsDeathHypo[1])));
		addOtherParam(new SecondOrderParam(STR_RR_HYPO, "Relative risk of severe hypoglycemic event in intervention branch (adjusted from rate/100 patient-month)", "Ly et al.", 
				0.020895447, RandomVariateFactory.getInstance("ExpTransformVariate", RandomVariateFactory.getInstance("NormalVariate", -3.868224010, 1.421931924))));

		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.CHD.name(), STR_RR_PREFIX + MainComplications.CHD.name(), "Selvin et al. https://doi.org/2004 10.7326/0003-4819-141-6-200409210-00007", 
				1.15, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.15, 0.92, 1.43, 1)));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NPH.name(), "%risk reducion for combined groups for microalbuminuria (>= 40 mg/24 h)", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.25, RandomVariateFactory.getInstance("NormalVariate", 0.25, sdFrom95CI(new double[] {0.19, 0.32}))));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NEU.name(), "Beta for confirmed clinical neuropathy", 
//				"DCCT 1996 https://doi.org/10.2337/diab.45.10.1289, as adapted by Sheffield", 5.3));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.NEU.name(), "%risk reducion for combined groups for confirmed clinical neuropathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.3, RandomVariateFactory.getInstance("NormalVariate", 0.3, sdFrom95CI(new double[] {0.18, 0.40}))));
		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.RET.name(), "%risk reducion for combined groups for sustained onset of retinopathy", "DCCT 1996 https://doi.org/10.2337/diab.45.10.1289", 
				0.35, RandomVariateFactory.getInstance("NormalVariate", 0.35, sdFrom95CI(new double[] {0.29, 0.41}))));

//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.BGRET.name(), "Beta for background retinopathy", 
//				"WESDR XXII, as adapted by Sheffield", BETA_BGRET));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.PRET.name(), "Beta for proliferative retinopathy", 
//				"WESDR XXII, as adapted by Sheffield", BETA_PRET));
//		addOtherParam(new SecondOrderParam(STR_RR_PREFIX + MainComplications.ME.name(), "Beta for macular edema", 
//				"WESDR XXII, as adapted by Sheffield", BETA_ME));
		
		addOtherParam(new SecondOrderParam(STR_IMR_PREFIX + SimpleNEUComplicationSubmodel.NEU.name(), "Increased mortality risk due to peripheral neuropathy (vibratory sense diminished)", "https://doi.org/10.2337/diacare.28.3.617", 1.51, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.51, 1.00, 2.28, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_PREFIX + SimpleNPHComplicationSubmodel.NPH.name(), "Increased mortality risk due to severe proteinuria", "https://doi.org/10.2337/diacare.28.3.617", 2.23, RandomVariateFactory.getInstance("RRFromLnCIVariate", 2.23, 1.11, 4.49, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_PREFIX + MainComplications.CHD.name(), "Increased mortality risk due to macrovascular disease", "https://doi.org/10.2337/diacare.28.3.617", 1.96, RandomVariateFactory.getInstance("RRFromLnCIVariate", 1.96, 1.33, 2.89, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_PREFIX + SimpleNPHComplicationSubmodel.ESRD.name(), "Increased mortality risk due to increased serum creatinine", "https://doi.org/10.2337/diacare.28.3.617", 4.53, RandomVariateFactory.getInstance("RRFromLnCIVariate", 4.53, 2.64, 7.77, 1)));
		addOtherParam(new SecondOrderParam(STR_IMR_PREFIX + SimpleNEUComplicationSubmodel.LEA.name(), "Increased mortality risk due to peripheral neuropathy (amputation)", "https://doi.org/10.2337/diacare.28.3.617", 3.98, RandomVariateFactory.getInstance("RRFromLnCIVariate", 3.98, 1.84, 8.59, 1)));


		addOtherParam(new SecondOrderParam(STR_PROBABILITY_PREFIX + SimpleCHDComplicationSubmodel.MI.name(), "Probability of a CHD complication being Myocardial Infarction", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.53, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.53)));
		addOtherParam(new SecondOrderParam(STR_PROBABILITY_PREFIX + SimpleCHDComplicationSubmodel.STROKE.name(), "Probability of a CHD complication being Stroke", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.07, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.07)));
		addOtherParam(new SecondOrderParam(STR_PROBABILITY_PREFIX + SimpleCHDComplicationSubmodel.ANGINA.name(), "Probability of a CHD complication being Angina", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.28, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.28)));
		addOtherParam(new SecondOrderParam(STR_PROBABILITY_PREFIX + SimpleCHDComplicationSubmodel.HF.name(), "Probability of a CHD complication being Heart Failure", "https://www.sheffield.ac.uk/polopoly_fs/1.258754!/file/13.05.pdf", 0.12, RandomVariateFactory.getInstance("GammaVariate", 1.0, 0.12)));

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

	@Override
	public DeathSubmodel getDeathSubmodel() {
		final StandardSpainDeathSubmodel dModel = new StandardSpainDeathSubmodel(rngFirstOrder, nPatients);

		dModel.addIMR(SimpleNEUComplicationSubmodel.NEU, getIMR(SimpleNEUComplicationSubmodel.NEU));
		dModel.addIMR(SimpleNEUComplicationSubmodel.LEA, getIMR(SimpleNEUComplicationSubmodel.LEA));
		dModel.addIMR(SimpleNPHComplicationSubmodel.NPH, getIMR(SimpleNPHComplicationSubmodel.NPH));
		dModel.addIMR(SimpleNPHComplicationSubmodel.ESRD, getIMR(SimpleNPHComplicationSubmodel.ESRD));
		dModel.addIMR(SimpleCHDComplicationSubmodel.ANGINA, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDComplicationSubmodel.STROKE, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDComplicationSubmodel.HF, getIMR(MainComplications.CHD));
		dModel.addIMR(SimpleCHDComplicationSubmodel.MI, getIMR(MainComplications.CHD));
		return dModel;
	}
	
	@Override
	public ComplicationSubmodel[] getComplicationSubmodels() {
		final ComplicationSubmodel[] comps = new ComplicationSubmodel[MainComplications.values().length];
		final ComplicationRR rr1 = new StdComplicationRR(1.0);
		
		// Adds nephropathy submodel
		final double[] probNPH = new double[NPHTransitions.values().length];
		probNPH[NPHTransitions.HEALTHY_NPH.ordinal()] = getProbability(SimpleNPHComplicationSubmodel.NPH);
		probNPH[NPHTransitions.HEALTHY_ESRD.ordinal()] = getProbability(SimpleNPHComplicationSubmodel.ESRD);
		probNPH[NPHTransitions.NPH_ESRD.ordinal()] = getProbability(SimpleNPHComplicationSubmodel.NPH, SimpleNPHComplicationSubmodel.ESRD);
		probNPH[NPHTransitions.NEU_NPH.ordinal()] = getProbability(SimpleNEUComplicationSubmodel.NEU, SimpleNPHComplicationSubmodel.NPH);

		final ComplicationRR[] rrNPH = new ComplicationRR[NPHTransitions.values().length];
		final ComplicationRR rrToNPH = new HbA1c10ReductionComplicationRR(otherParams.get(STR_RR_PREFIX + SimpleNPHComplicationSubmodel.NPH.name()).getValue(), REF_HBA1C); 
		rrNPH[NPHTransitions.HEALTHY_NPH.ordinal()] = rrToNPH;
		rrNPH[NPHTransitions.HEALTHY_ESRD.ordinal()] = rr1;
		rrNPH[NPHTransitions.NPH_ESRD.ordinal()] = rr1;
		// Assume the same RR from healthy to NPH than from NEU to NPH
		rrNPH[NPHTransitions.NEU_NPH.ordinal()] = rrToNPH;
		comps[MainComplications.NPH.ordinal()] = new SimpleNPHComplicationSubmodel(rngFirstOrder, nPatients, probNPH, rrNPH);
		
		// Adds neuropathy submodel
		final double[] probNEU = new double[NEUTransitions.values().length];
		probNEU[NEUTransitions.HEALTHY_NEU.ordinal()] = getProbability(SimpleNEUComplicationSubmodel.NEU);
		probNEU[NEUTransitions.HEALTHY_LEA.ordinal()] = getProbability(SimpleNEUComplicationSubmodel.LEA);
		probNEU[NEUTransitions.NEU_LEA.ordinal()] = getProbability(SimpleNEUComplicationSubmodel.NEU, SimpleNEUComplicationSubmodel.LEA);
		final ComplicationRR[] rrNEU = new ComplicationRR[NEUTransitions.values().length];
		rrNEU[NEUTransitions.HEALTHY_NEU.ordinal()] = new HbA1c10ReductionComplicationRR(
				otherParams.get(STR_RR_PREFIX + SimpleNEUComplicationSubmodel.NEU.name()).getValue(), REF_HBA1C);
		rrNEU[NEUTransitions.HEALTHY_LEA.ordinal()] = rr1;
		rrNEU[NEUTransitions.NEU_LEA.ordinal()] = rr1;
		comps[MainComplications.NEU.ordinal()] = new SimpleNEUComplicationSubmodel(rngFirstOrder, nPatients, probNEU, rrNEU);
		
		// Adds retinopathy submodel
		final double[] probRET = new double[RETTransitions.values().length];
		probRET[RETTransitions.HEALTHY_RET.ordinal()] = getProbability(SimpleRETComplicationSubmodel.RET);
		probRET[RETTransitions.HEALTHY_BLI.ordinal()] = getProbability(SimpleRETComplicationSubmodel.BLI);
		probRET[RETTransitions.RET_BLI.ordinal()] = getProbability(SimpleRETComplicationSubmodel.RET, SimpleRETComplicationSubmodel.BLI);
		final ComplicationRR[] rrRET = new ComplicationRR[RETTransitions.values().length];
		rrRET[RETTransitions.HEALTHY_RET.ordinal()] = new HbA1c10ReductionComplicationRR(
				otherParams.get(STR_RR_PREFIX + SimpleRETComplicationSubmodel.RET.name()).getValue(), REF_HBA1C);
		rrRET[RETTransitions.HEALTHY_BLI.ordinal()] = rr1;
		rrRET[RETTransitions.RET_BLI.ordinal()] = rr1;
		comps[MainComplications.RET.ordinal()] = new SimpleRETComplicationSubmodel(rngFirstOrder, nPatients, probRET, rrRET);
		
		// Adds major Cardiovascular disease submodel
		final double[] probCHD = new double[SimpleCHDComplicationSubmodel.CHDTransitions.values().length];
		probCHD[SimpleCHDComplicationSubmodel.CHDTransitions.HEALTHY_CHD.ordinal()] = getProbability(MainComplications.CHD);
		probCHD[SimpleCHDComplicationSubmodel.CHDTransitions.NEU_CHD.ordinal()] = getProbability(MainComplications.NEU, MainComplications.CHD);
		probCHD[SimpleCHDComplicationSubmodel.CHDTransitions.NPH_CHD.ordinal()] = getProbability(MainComplications.NPH, MainComplications.CHD);
		probCHD[SimpleCHDComplicationSubmodel.CHDTransitions.RET_CHD.ordinal()] = getProbability(MainComplications.RET, MainComplications.CHD);
		final ComplicationRR[] rrCHD = new ComplicationRR[SimpleCHDComplicationSubmodel.CHDTransitions.values().length];		
		final ComplicationRR rrToCHD = new HbA1c1PPComplicationRR(otherParams.get(STR_RR_PREFIX + MainComplications.CHD.name()).getValue(), REF_HBA1C);
		rrCHD[SimpleCHDComplicationSubmodel.CHDTransitions.HEALTHY_CHD.ordinal()] = rrToCHD;
		rrCHD[SimpleCHDComplicationSubmodel.CHDTransitions.NEU_CHD.ordinal()] = rrToCHD;
		rrCHD[SimpleCHDComplicationSubmodel.CHDTransitions.NPH_CHD.ordinal()] = rrToCHD;
		rrCHD[SimpleCHDComplicationSubmodel.CHDTransitions.RET_CHD.ordinal()] = rrToCHD;
		comps[MainComplications.CHD.ordinal()] = new SimpleCHDComplicationSubmodel(rngFirstOrder, nPatients, probCHD, rrCHD, getRandomVariateForCHDComplications());
		
		return comps;
	}
	
	public DiscreteSelectorVariate getRandomVariateForCHDComplications() {
		final double [] coef = new double[SimpleCHDComplicationSubmodel.CHDSubstates.length];
		for (int i = 0; i < SimpleCHDComplicationSubmodel.CHDSubstates.length; i++) {
			final T1DMHealthState comp = SimpleCHDComplicationSubmodel.CHDSubstates[i];
			final SecondOrderParam param = otherParams.get(STR_PROBABILITY_PREFIX + comp.name());
			coef[i] = param.getValue();
		}
		return (DiscreteSelectorVariate)RandomVariateFactory.getInstance("DiscreteSelectorVariate", coef);
	}

	@Override
	public CostCalculator getCostCalculator() {
		final StdCostCalculator calc = new StdCostCalculator(getAnnualNoComplicationCost(), getCostForSevereHypoglycemicEpisode());
		for (final T1DMHealthState subst : SimpleNEUComplicationSubmodel.NEUSubstates) {
			final double[] costs = getCostsForHealthState(subst);
			calc.addCostForHealthState(subst, costs);
		}
		for (final T1DMHealthState subst : SimpleNPHComplicationSubmodel.NPHSubstates) {
			final double[] costs = getCostsForHealthState(subst);
			calc.addCostForHealthState(subst, costs);
		}
		for (final T1DMHealthState subst : SimpleCHDComplicationSubmodel.CHDSubstates) {
			final double[] costs = getCostsForHealthState(subst);
			calc.addCostForHealthState(subst, costs);
		}
		return calc;
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator() {
		final StdUtilityCalculator calc = new StdUtilityCalculator(getUtilityCombinationMethod(), getNoComplicationDisutility(), getGeneralPopulationUtility(), getHypoEventDisutility());
		for (final T1DMHealthState subst : SimpleNEUComplicationSubmodel.NEUSubstates) {
			calc.addDisutilityForHealthState(subst, getDisutilityForHealthState(subst));
		}
		for (final T1DMHealthState subst : SimpleNPHComplicationSubmodel.NPHSubstates) {
			calc.addDisutilityForHealthState(subst, getDisutilityForHealthState(subst));			
		}
		for (final T1DMHealthState subst : SimpleCHDComplicationSubmodel.CHDSubstates) {
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
