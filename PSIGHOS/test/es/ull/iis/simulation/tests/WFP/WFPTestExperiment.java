/**
 * 
 */
package es.ull.iis.simulation.tests.WFP;

import com.beust.jcommander.JCommander;

import es.ull.iis.simulation.model.Experiment;
import es.ull.iis.simulation.model.Simulation;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class WFPTestExperiment extends Experiment {
	final TestWFP.CommonArguments args;
	final int wfp;
	
	public WFPTestExperiment(TestWFP.CommonArguments args) {
		super("Testing WFP " + args.wfp, 1);
		this.wfp = args.wfp;
		this.args = args;
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		Simulation simul = null;
		switch (wfp) {
			case 1:		simul = new WFP01Simulation(ind, args);	break;
			case 2:		simul = new WFP02Simulation(ind, args);	break;
			case 3:		simul = new WFP03Simulation(ind, args);	break;
			case 4:		simul = new WFP04Simulation(ind, args);	break;
			case 5:		simul = new WFP05Simulation(ind, args);	break;
			case 6:		simul = new WFP06Simulation(ind, args);	break;
			case 7:		simul = new WFP07Simulation(ind, args);	break;
			case 8:		simul = new WFP08Simulation(ind, args);	break;
			case 9:		simul = new WFP09Simulation(ind, args);	break;
			case 10:	simul = new WFP10Simulation(ind, args);	break;
			case 12:	simul = new WFP12Simulation(ind, args);	break;
			case 13:	simul = new WFP13Simulation(ind, args);	break;
			case 17:	simul = new WFP17Simulation(ind, args);	break;
			case 19:	simul = new WFP19Simulation(ind, args);	break;
			case 21:	simul = new WFP21Simulation_For(ind, args);	break;
			case 211:	simul = new WFP21Simulation_WhileDo(ind, args);	break;
			case 212:	simul = new WFP21Simulation_DoWhile(ind, args);	break;
			case 28:	simul = new WFP28Simulation(ind, args);	break;
			case 30:	simul = new WFP30Simulation(ind, args);	break;
			case 40:	simul = new WFP40Simulation(ind, args);	break;
			default: 
				System.err.println(description + " - Invalid WFP number: " + wfp);
			break;
		}
		return simul;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final TestWFP.CommonArguments arguments = new TestWFP.CommonArguments();
		try {
			final JCommander jc = JCommander.newBuilder()
					.addObject(arguments)
					.build();
			jc.parse(args);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		new WFPTestExperiment(arguments).start();
	}

}
