/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.io.PrintStream;
import java.util.ArrayList;

import es.ull.iis.simulation.hta.diabetes.htaReportCGM.OriginalMonitoDM1Population;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AcuteComplicationCounterListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.EpidemiologicView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.ExperimentListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.HbA1cListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.interventions.SecondOrderDiabetesIntervention.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.outcomes.CostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelCostCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.SubmodelUtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator;
import es.ull.iis.simulation.hta.diabetes.outcomes.UtilityCalculator.DisutilityCombinationMethod;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.Discount;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderCostParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParam;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.diabetes.params.StdDiscount;
import es.ull.iis.simulation.hta.diabetes.populations.DiabetesPopulation;
import es.ull.iis.simulation.hta.diabetes.submodels.AcuteComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.BattelinoSevereHypoglycemiaEvent;
import es.ull.iis.simulation.hta.diabetes.submodels.ChronicComplicationSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.EmpiricalSpainDeathSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldNPHSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SheffieldRETSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleCHDSubmodel;
import es.ull.iis.simulation.hta.diabetes.submodels.SimpleNEUSubmodel;
import es.ull.iis.util.Statistics;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMainTest extends SecondOrderParamsRepository {
	public final static boolean BASIC_TEST_ONE_PATIENT = false;
	private final static int N_PATIENTS = BASIC_TEST_ONE_PATIENT ? 1 : BasicConfigParams.N_PATIENTS;

	private static final PrintStream out = System.out;

	public T1DMMainTest(DiabetesPopulation pop) {
		super(N_PATIENTS, pop);
		
		registerComplication(new SheffieldRETSubmodel());
		registerComplication(new SheffieldNPHSubmodel());
		registerComplication(new SimpleCHDSubmodel());
		registerComplication(new SimpleNEUSubmodel());

		registerComplication(new BattelinoSevereHypoglycemiaEvent());

		registerIntervention(new AMGS_Intervention());
		registerIntervention(new SMGC_Intervention());
		
		registerDeathSubmodel(new EmpiricalSpainDeathSubmodel(this));
		
		addCostParam(new SecondOrderCostParam(STR_COST_PREFIX + STR_NO_COMPLICATIONS, "Cost of Diabetes with no complications", 
				BasicConfigParams.DEF_C_DNC.SOURCE, BasicConfigParams.DEF_C_DNC.YEAR, 
				BasicConfigParams.DEF_C_DNC.VALUE, getRandomVariateForCost(BasicConfigParams.DEF_C_DNC.VALUE)));

		final double[] paramsDuDNC = Statistics.betaParametersFromNormal(BasicConfigParams.DEF_DU_DNC[0], BasicConfigParams.DEF_DU_DNC[1]);
		addUtilParam(new SecondOrderParam(STR_DISUTILITY_PREFIX + STR_NO_COMPLICATIONS, "Disutility of DNC", "", 
				BasicConfigParams.DEF_DU_DNC[0], "BetaVariate", paramsDuDNC[0], paramsDuDNC[1]));		
	}
	
	@Override
	public CostCalculator getCostCalculator(double cDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelCostCalculator(cDNC, submodels, acuteSubmodels);
	}
	
	@Override
	public UtilityCalculator getUtilityCalculator(double duDNC, ChronicComplicationSubmodel[] submodels, AcuteComplicationSubmodel[] acuteSubmodels) {
		return new SubmodelUtilityCalculator(DisutilityCombinationMethod.ADD, duDNC, BasicConfigParams.DEF_U_GENERAL_POP, submodels, acuteSubmodels);
	}


	public static void main(String[] args) {		
		System.out.println("Reproducing original MONITO T1DM model");
		final Discount discount = new StdDiscount(BasicConfigParams.DEF_DISCOUNT_RATE);
		final SecondOrderParamsRepository secParams = new T1DMMainTest(new OriginalMonitoDM1Population());
		final ArrayList<ExperimentListener> baseCaseExpListeners = new ArrayList<>();
		baseCaseExpListeners.add(new AnnualCostView(1, secParams, discount));
		baseCaseExpListeners.add(new EpidemiologicView(1, secParams, 1, EpidemiologicView.Type.CUMUL_INCIDENCE, true, false));
		
		// Print header
		final StringBuilder str = new StringBuilder();
		str.append("SIM\t");
		for (SecondOrderDiabetesIntervention intervention : secParams.getRegisteredInterventions()) {
			final String shortName = intervention.getShortName();
			str.append(HbA1cListener.getStrHeader(shortName));
			str.append(CostListener.getStrHeader(shortName));
			str.append(LYListener.getStrHeader(shortName));
			str.append(QALYListener.getStrHeader(shortName));
			str.append(AcuteComplicationCounterListener.getStrHeader(shortName));
		}
		str.append(System.lineSeparator());

		final RepositoryInstance common = secParams.getInstance();
		final int timeHorizon = BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1;
		
		final int nInterventions = secParams.getRegisteredInterventions().size();
		final HbA1cListener[] hba1cListeners = new HbA1cListener[nInterventions];
		final CostListener[] costListeners = new CostListener[nInterventions];
		final LYListener[] lyListeners = new LYListener[nInterventions];
		final QALYListener[] qalyListeners = new QALYListener[nInterventions];
		final AcuteComplicationCounterListener[] acuteListeners = new AcuteComplicationCounterListener[nInterventions];

		for (int i = 0; i < nInterventions; i++) {
			hba1cListeners[i] = new HbA1cListener(N_PATIENTS);
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discount, N_PATIENTS);
			lyListeners[i] = new LYListener(discount, N_PATIENTS);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), discount, N_PATIENTS);
			acuteListeners[i] = new AcuteComplicationCounterListener(N_PATIENTS);
		}
		final DiabetesIntervention[] intInstances = common.getInterventions();
		DiabetesSimulation simul = new DiabetesSimulation(0, intInstances[0], N_PATIENTS, common, secParams.getPopulation(), timeHorizon);
		simul.addInfoReceiver(hba1cListeners[0]);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(acuteListeners[0]);
		for (ExperimentListener listener : baseCaseExpListeners) {
			listener.addListener(simul);
		}			
		
		simul.run();
		for (int i = 1; i < nInterventions; i++) {
			simul = new DiabetesSimulation(simul, intInstances[i]);
			simul.addInfoReceiver(hba1cListeners[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(acuteListeners[i]);
			for (ExperimentListener listener : baseCaseExpListeners) {
				listener.addListener(simul);
			}			
			
			simul.run();
		}

		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < nInterventions; i++) {
			str.append(hba1cListeners[i]);
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
			str.append(acuteListeners[i]);
		}
		out.println(str.toString());	
		for (ExperimentListener listener : baseCaseExpListeners) {
			out.println(listener);
		}		
	}

	private static class AMGS_Intervention extends SecondOrderDiabetesIntervention {

		public AMGS_Intervention() {
			super("AMGS", "AMGS");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id);
		}

		public class Instance extends DiabetesIntervention {

			public Instance(int id) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
			}

			@Override
			public double getHBA1cLevel(DiabetesPatient pat) {
				return pat.getBaselineHBA1c();
			}

			@Override
			public double getAnnualCost(DiabetesPatient pat) {
				return 1717.40;
			}
			
		}
	}
	
	private static class SMGC_Intervention extends SecondOrderDiabetesIntervention {

		public SMGC_Intervention() {
			super("SMGC", "SMCG-TR+AMGS");
		}

		@Override
		public void addSecondOrderParams(SecondOrderParamsRepository secParams) {
		}

		@Override
		public DiabetesIntervention getInstance(int id, SecondOrderParamsRepository secParams) {
			return new Instance(id);
		}

		public class Instance extends DiabetesIntervention {

			public Instance(int id) {
				super(id, BasicConfigParams.DEF_MAX_AGE);
			}

			@Override
			public double getHBA1cLevel(DiabetesPatient pat) {
				return pat.getBaselineHBA1c() - 0.23;
			}

			@Override
			public double getAnnualCost(DiabetesPatient pat) {
				return 4374.55;
			}
			
		}
	}
}
