/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMPatientInfoView;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMTimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.T1DM.params.BaseSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.BasicConfigParams;
import es.ull.iis.simulation.hta.T1DM.params.CanadaSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.ResourceUsageParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams;
import es.ull.iis.simulation.hta.inforeceiver.ICERView;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMainTest {
	public final static boolean CHECK_CANADA = false;

	public final static boolean BASIC_TEST_ONE_PATIENT = false;
	/** Number of patients to simulate */
	public final static int NPATIENTS = BASIC_TEST_ONE_PATIENT ? 1 : 5000;

	private static final PrintStream out = System.out;
	private static final int N_RUNS = BASIC_TEST_ONE_PATIENT ? 0 : 100;
	private static final SecondOrderParams secParams = CHECK_CANADA ? new CanadaSecondOrderParams(true) : new BaseSecondOrderParams(true);

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
			str.append("AVG_C_" + interventions[i].getShortName() + "\t");
			str.append("L95CI_C_" + interventions[i].getShortName() + "\t");
			str.append("U95CI_C_" + interventions[i].getShortName() + "\t");
			str.append("AVG_LY_" + interventions[i].getShortName() + "\t");
			str.append("L95CI_LY_" + interventions[i].getShortName() + "\t");
			str.append("U95CI_LY_" + interventions[i].getShortName() + "\t");
			str.append("AVG_QALY_" + interventions[i].getShortName() + "\t");
			str.append("LC95I_QALY_" + interventions[i].getShortName() + "\t");
			str.append("UC95I_QALY_" + interventions[i].getShortName() + "\t");
		}
		str.append(T1DMTimeFreeOfComplicationsView.getStrHeader(false, interventions));
		str.append(secParams.getStrHeader());
		return str.toString();
	}
	
	private static String print(T1DMSimulation simul, T1DMTimeFreeOfComplicationsView timeFreeListener) {
		final StringBuilder str = new StringBuilder();
		final Intervention[] interventions = secParams.getInterventions();
		str.append("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.length; i++) {
			str.append(simul.getCost().getAverage(i) +  "\t");
			double[] ci = simul.getCost().get95CI(i, true); 
			str.append(ci[0] + "\t");
			str.append(ci[1] + "\t");
			str.append(simul.getLY().getAverage(i) +  "\t");
			ci = simul.getLY().get95CI(i, true); 
			str.append(ci[0] + "\t");
			str.append(ci[1] + "\t");
			str.append(simul.getQALY().getAverage(i) +  "\t");
			ci = simul.getQALY().get95CI(i, true); 
			str.append(ci[0] + "\t");
			str.append(ci[1] + "\t");
		}
		str.append(timeFreeListener).append(secParams);
		return str.toString();
	}
	
	public static void main(String[] args) {
		out.println(getStrHeader());
		final Intervention[] interventions = secParams.getInterventions();
		T1DMTimeFreeOfComplicationsView timeFreeListener = new T1DMTimeFreeOfComplicationsView(NPATIENTS, interventions.length, false);
		// First the deterministic simulation
		T1DMSimulation simul = new T1DMSimulation(0, true, interventions[0], NPATIENTS, new CommonParams(secParams, NPATIENTS), new ResourceUsageParams(secParams), new UtilityParams(secParams));
		simul.addInfoReceiver(timeFreeListener);
		addListeners(simul);
		simul.run();
		simul = new T1DMSimulation(simul, interventions[1]);
		simul.addInfoReceiver(timeFreeListener);
		addListeners(simul);
		simul.run();
		out.println(print(simul, timeFreeListener));
		// Now probabilistic
		secParams.setBaseCase(false);
		for (int i = 1; i <= N_RUNS; i++) {
			timeFreeListener = new T1DMTimeFreeOfComplicationsView(NPATIENTS, interventions.length, false);
			simul = new T1DMSimulation(i, false, interventions[0], NPATIENTS, new CommonParams(secParams, NPATIENTS), new ResourceUsageParams(secParams), new UtilityParams(secParams));
			simul.addInfoReceiver(timeFreeListener);
			addListeners(simul);
			simul.run();
			simul = new T1DMSimulation(simul, interventions[1]);
			simul.addInfoReceiver(timeFreeListener);
			addListeners(simul);
			simul.run();
			out.println(print(simul, timeFreeListener));
		}
	}
}
