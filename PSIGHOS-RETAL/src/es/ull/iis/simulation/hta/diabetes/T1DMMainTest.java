/**
 * 
 */
package es.ull.iis.simulation.hta.diabetes;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.diabetes.canada.CanadaSecondOrderParams;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.T1DMPatientInfoView;
import es.ull.iis.simulation.hta.diabetes.inforeceiver.T1DMTimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.diabetes.interventions.DiabetesIntervention;
import es.ull.iis.simulation.hta.diabetes.params.BasicConfigParams;
import es.ull.iis.simulation.hta.diabetes.params.CommonParams;
import es.ull.iis.simulation.hta.diabetes.params.SecondOrderParamsRepository;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMainTest {
	public final static boolean CHECK_CANADA = false;

	public final static boolean BASIC_TEST_ONE_PATIENT = true;
	private final static int N_PATIENTS = BASIC_TEST_ONE_PATIENT ? 1 : BasicConfigParams.DEF_N_PATIENTS;

	private static final PrintStream out = System.out;
	private static final SecondOrderParamsRepository secParams = CHECK_CANADA ? new CanadaSecondOrderParams(N_PATIENTS) : new UnconsciousSecondOrderParams(N_PATIENTS);

	public T1DMMainTest() {
		super();
	}

	private static void addListeners(DiabetesSimulation simul) {
//		simul.addInfoReceiver(new StdInfoView());
		if (BASIC_TEST_ONE_PATIENT) {
			simul.addInfoReceiver(new T1DMPatientInfoView());
		}
//		simul.addInfoReceiver(new PatientCounterHistogramView(CommonParams.MIN_AGE, CommonParams.MAX_AGE, 5));
//		simul.addInfoReceiver(new T1DMPatientPrevalenceView(simul.getTimeUnit(), T1DMPatientPrevalenceView.buildAgesInterval(25, 90, 5, true)));
	}
	
	private static String getStrHeader() {
		final StringBuilder str = new StringBuilder();
		final Intervention[] interventions = secParams.getInterventions();
		str.append("SIM\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(CostListener.getStrHeader(interventions[i].getShortName()));
			str.append(LYListener.getStrHeader(interventions[i].getShortName()));
			str.append(QALYListener.getStrHeader(interventions[i].getShortName()));
		}
		str.append(T1DMTimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getRegisteredComplicationStages()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private static String print(DiabetesSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, T1DMTimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		final Intervention[] interventions = secParams.getInterventions();
		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(costListeners[i]);
			str.append(lyListeners[i]);
			str.append(qalyListeners[i]);
		}
		str.append(timeFreeListener).append(secParams);
		return str.toString();
	}

	private static void simulateInterventions(int id, boolean baseCase, DiabetesIntervention[] interventions) {
		final CommonParams common = new CommonParams(secParams);
		final T1DMTimeFreeOfComplicationsView timeFreeListener = new T1DMTimeFreeOfComplicationsView(N_PATIENTS, interventions.length, false, secParams.getRegisteredComplicationStages());
		final CostListener[] costListeners = new CostListener[interventions.length];
		final LYListener[] lyListeners = new LYListener[interventions.length];
		final QALYListener[] qalyListeners = new QALYListener[interventions.length];
		for (int i = 0; i < interventions.length; i++) {
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), common.getDiscountRate(), N_PATIENTS);
			lyListeners[i] = new LYListener(common.getDiscountRate(), N_PATIENTS);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), common.getDiscountRate(), N_PATIENTS);
		}
		DiabetesSimulation simul = new DiabetesSimulation(id, interventions[0], N_PATIENTS, common, secParams.getPopulations(), BasicConfigParams.DEF_MAX_AGE - secParams.getMinAge() + 1);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(new AnnualCostView(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), N_PATIENTS, secParams.getMinAge(), BasicConfigParams.DEF_MAX_AGE));
		simul.addInfoReceiver(timeFreeListener);
		addListeners(simul);
		simul.run();
		for (int i = 1; i < interventions.length; i++) {
			simul = new DiabetesSimulation(simul, interventions[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(new AnnualCostView(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), N_PATIENTS, BasicConfigParams.DEF_MIN_AGE, BasicConfigParams.DEF_MAX_AGE));
			simul.addInfoReceiver(timeFreeListener);
			addListeners(simul);
			simul.run();				
		}
		out.println(print(simul, costListeners, lyListeners, qalyListeners, timeFreeListener));	
	}

	public static void main(String[] args) {
		 int nRuns = BASIC_TEST_ONE_PATIENT ? 0 : BasicConfigParams.N_RUNS;

		final DiabetesIntervention[] interventions = secParams.getInterventions();
		secParams.setDiscountZero(true);

		out.println(getStrHeader());
		simulateInterventions(0, true, interventions);
		secParams.setBaseCase(false);
		// Now probabilistic
//		secParams.setBaseCase(false);
//		for (int i = 1; i <= nRuns; i++) {
//			simulateInterventions(i, false, interventions);
//		}
	}
}
