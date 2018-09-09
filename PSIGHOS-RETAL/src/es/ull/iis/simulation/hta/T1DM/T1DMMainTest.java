/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.canada.CanadaSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.AnnualCostView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.CostListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.LYListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.QALYListener;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMPatientInfoView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMTimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.T1DM.params.UnconsciousSecondOrderParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMainTest {
	public final static boolean CHECK_CANADA = false;

	public final static boolean BASIC_TEST_ONE_PATIENT = true;

	private static final PrintStream out = System.out;
	private static final SecondOrderParamsRepository secParams = CHECK_CANADA ? new CanadaSecondOrderParams() : new UnconsciousSecondOrderParams();

	public T1DMMainTest() {
		super();
	}

	private static void addListeners(T1DMSimulation simul) {
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
		str.append(T1DMTimeFreeOfComplicationsView.getStrHeader(false, interventions, secParams.getAvailableHealthStates()));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private static String print(T1DMSimulation simul, CostListener[] costListeners, LYListener[] lyListeners, QALYListener[] qalyListeners, T1DMTimeFreeOfComplicationsView timeFreeListener) {
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

	private static void simulateInterventions(int id, boolean baseCase, T1DMMonitoringIntervention[] interventions) {
		final CommonParams common = new CommonParams(secParams);
		final T1DMTimeFreeOfComplicationsView timeFreeListener = new T1DMTimeFreeOfComplicationsView(BasicConfigParams.N_PATIENTS, interventions.length, false, secParams.getAvailableHealthStates());
		final CostListener[] costListeners = new CostListener[interventions.length];
		final LYListener[] lyListeners = new LYListener[interventions.length];
		final QALYListener[] qalyListeners = new QALYListener[interventions.length];
		for (int i = 0; i < interventions.length; i++) {
			costListeners[i] = new CostListener(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), common.getDiscountRate(), BasicConfigParams.N_PATIENTS);
			lyListeners[i] = new LYListener(common.getDiscountRate(), BasicConfigParams.N_PATIENTS);
			qalyListeners[i] = new QALYListener(secParams.getUtilityCalculator(common.getNoComplicationDisutility(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), common.getDiscountRate(), BasicConfigParams.N_PATIENTS);
		}
		T1DMSimulation simul = new T1DMSimulation(id, baseCase, interventions[0], BasicConfigParams.N_PATIENTS, common);
		simul.addInfoReceiver(costListeners[0]);
		simul.addInfoReceiver(lyListeners[0]);
		simul.addInfoReceiver(qalyListeners[0]);
		simul.addInfoReceiver(new AnnualCostView(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), BasicConfigParams.N_PATIENTS, BasicConfigParams.MIN_AGE, BasicConfigParams.MAX_AGE));
		simul.addInfoReceiver(timeFreeListener);
		addListeners(simul);
		simul.run();
		for (int i = 1; i < interventions.length; i++) {
			simul = new T1DMSimulation(simul, interventions[i]);
			simul.addInfoReceiver(costListeners[i]);
			simul.addInfoReceiver(lyListeners[i]);
			simul.addInfoReceiver(qalyListeners[i]);
			simul.addInfoReceiver(new AnnualCostView(secParams.getCostCalculator(common.getAnnualNoComplicationCost(), common.getCompSubmodels(), common.getAcuteCompSubmodels()), BasicConfigParams.N_PATIENTS, BasicConfigParams.MIN_AGE, BasicConfigParams.MAX_AGE));
			simul.addInfoReceiver(timeFreeListener);
			addListeners(simul);
			simul.run();				
		}
		out.println(print(simul, costListeners, lyListeners, qalyListeners, timeFreeListener));	
	}

	public static void main(String[] args) {
		BasicConfigParams.N_RUNS = BASIC_TEST_ONE_PATIENT ? 0 : 100;
		BasicConfigParams.N_PATIENTS = BASIC_TEST_ONE_PATIENT ? 1 : 5000;

		final T1DMMonitoringIntervention[] interventions = secParams.getInterventions();
		secParams.setDiscountZero(true);

		out.println(getStrHeader());
		simulateInterventions(0, true, interventions);
		secParams.setBaseCase(false);
		// Now probabilistic
//		secParams.setBaseCase(false);
//		for (int i = 1; i <= N_RUNS; i++) {
//			simulateInterventions(i, false, interventions);
//		}
	}
}
