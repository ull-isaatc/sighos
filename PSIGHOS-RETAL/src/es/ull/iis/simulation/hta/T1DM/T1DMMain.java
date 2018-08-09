/**
 * 
 */
package es.ull.iis.simulation.hta.T1DM;

import java.io.PrintStream;

import es.ull.iis.simulation.hta.Intervention;
import es.ull.iis.simulation.hta.T1DM.inforeceiver.T1DMTimeFreeOfComplicationsView;
import es.ull.iis.simulation.hta.T1DM.params.BaseSecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.CommonParams;
import es.ull.iis.simulation.hta.T1DM.params.DeathParams;
import es.ull.iis.simulation.hta.T1DM.params.SecondOrderParams;
import es.ull.iis.simulation.hta.T1DM.params.UtilityParams;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class T1DMMain {
	private static final PrintStream out = System.out;
	private static final int N_RUNS = 10;
	private static final SecondOrderParams secParams = new BaseSecondOrderParams(true);

	public T1DMMain() {
		super();
	}

	private static void addListeners(T1DMSimulation simul) {
//		simul.addInfoReceiver(new StdInfoView());
//		simul.addInfoReceiver(new T1DMPatientInfoView());
//		simul.addInfoReceiver(new PatientCounterHistogramView(CommonParams.MIN_AGE, CommonParams.MAX_AGE, 5));
//		simul.addInfoReceiver(new T1DMPatientPrevalenceView(simul.getTimeUnit(), T1DMPatientPrevalenceView.buildAgesInterval(25, 90, 5, true)));
//		simul.addInfoReceiver(new ICERView(true, true, true, true));
//		simul.addInfoReceiver(new T1DMTimeFreeOfComplicationsView(CommonParams.NPATIENTS));
	}
	
	private static void printHeader() {
		final Intervention[] interventions = secParams.getInterventions();
		out.print("SIM\t");
		for (int i = 0; i < interventions.length; i++) {
			out.print("AVG_C_" + interventions[i].getShortName() + "\t");
			out.print("L95CI_C_" + interventions[i].getShortName() + "\t");
			out.print("U95CI_C_" + interventions[i].getShortName() + "\t");
			out.print("AVG_LY_" + interventions[i].getShortName() + "\t");
			out.print("L95CI_LY_" + interventions[i].getShortName() + "\t");
			out.print("U95CI_LY_" + interventions[i].getShortName() + "\t");
			out.print("AVG_QALY_" + interventions[i].getShortName() + "\t");
			out.print("LC95I_QALY_" + interventions[i].getShortName() + "\t");
			out.print("UC95I_QALY_" + interventions[i].getShortName() + "\t");
		}
		out.println(secParams.getStrHeader());
	}
	
	private static void print(T1DMSimulation simul) {
		final Intervention[] interventions = secParams.getInterventions();
		out.print("" +  simul.getIdentifier() + "\t");
		for (int i = 0; i < interventions.length; i++) {
			out.print(simul.getCost().getAverage(i) +  "\t");
			double[] ci = simul.getCost().get95CI(i, true); 
			out.print(ci[0] + "\t");
			out.print(ci[1] + "\t");
			out.print(simul.getLY().getAverage(i) +  "\t");
			ci = simul.getLY().get95CI(i, true); 
			out.print(ci[0] + "\t");
			out.print(ci[1] + "\t");
			out.print(simul.getQALY().getAverage(i) +  "\t");
			ci = simul.getQALY().get95CI(i, true); 
			out.print(ci[0] + "\t");
			out.print(ci[1] + "\t");
		}
		out.println(secParams);
	}
	
	public static void main(String[] args) {
		printHeader();
		final Intervention[] interventions = secParams.getInterventions();
		// First the deterministic simulation
		T1DMSimulation simul = new T1DMSimulation(0, true, interventions[0], new CommonParams(secParams), new DeathParams(secParams), new UtilityParams());
		addListeners(simul);
		simul.run();
		simul = new T1DMSimulation(simul, interventions[1]);
		addListeners(simul);
		simul.run();
		print(simul);
		// Now probabilistic
		secParams.setBaseCase(false);
		for (int i = 1; i <= N_RUNS; i++) {
			simul = new T1DMSimulation(i, false, interventions[0], new CommonParams(secParams), new DeathParams(secParams), new UtilityParams());
			addListeners(simul);
			simul.run();
			simul = new T1DMSimulation(simul, interventions[1]);
			addListeners(simul);
			simul.run();
			print(simul);
		}
	}
}
