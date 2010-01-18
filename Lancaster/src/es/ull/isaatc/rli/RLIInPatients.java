/**
 * 
 */
package es.ull.isaatc.rli;

import java.io.FileWriter;
import java.io.IOException;

import simkit.random.ExponentialVariate;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.listener.*;

/**
 * Time unit is DAY.
 * @author Iván Castilla Rodríguez
 */
class RLIIPExperiment extends PooledExperiment {
	static final int NEXP = 2;
	
	public RLIIPExperiment() {
		super("Inpatients", NEXP);
	}

	@Override
	public Simulation getSimulation(int ind) {
//		Simulation sim = new RLIIP8GSimulation(ind);
//		Simulation sim = new RLIIP7GSimulation(ind);
		Simulation sim = new RLIIP14GSimulation(ind);
		ListenerController cont = new RLIListenerController(ind);
//		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
		cont.addListener(new RLIPathwayListener());
		cont.addListener(new RLIOccListener(1.0));
		cont.addListener(new TimeChangeListener() {
			int day = 0;
			public void infoEmited(TimeChangeInfo info) {
				if (info.getTs() >= day) { 
					System.out.print("#");
					day++;
					if (day % 30 == 0)
						System.out.println();
				}
			}
			/* (non-Javadoc)
			 * @see java.lang.Object#toString()
			 */
			@Override
			public String toString() {
				return "";
			}
			
		});
		cont.addListener(new SimulationTimeListener());
		return sim;
	}
}

class RLIListenerController extends ListenerController {
	private FileWriter file;
	private final static String PATH = "C:\\Users\\Iván\\Documents\\Lancaster\\ResSIGHOS\\"; 

	/**
	 * @param ind
	 */
	public RLIListenerController(int ind) {
		super();
		try {
			file = new FileWriter(PATH + "Res14Sim" + (ind + 5) + ".txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void end() {
		super.end();
		try {
			for (String res : getListenerResults())
				file.write(res + "\n=====================================================\n");
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class RLIInPatients {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new RLIIPExperiment().start();
		// UNCOMMENT FOR TESTING PeriodicProportionFunction
//		int [] n = {10, 20, 30, 40, 50};
//		double []prop = {0.1, 0.2, 0.3};
//		PeriodicProportionFunction pf = new PeriodicProportionFunction(n, prop, 1);
//		for (double t = 0.0; t < 100.0; t += 1.0)
//			System.out.println(pf.getPositiveValue(t));
		
		// UNCOMMENT FOR TESTING UniformlyDistributedSplitFunction
//		int n = 3;
//		TimeFunction []part = new TimeFunction[n];
//		for (int i = 0; i < n; i++)
//			part[i] = TimeFunctionFactory.getInstance("ConstantVariate", i + 1);
//		PeriodicCycle c1 = new PeriodicCycle(0.0, new UniformlyDistributedSplitFunction(part, 10.0), 0);
//		PeriodicCycle c = new PeriodicCycle(0.0, new ConstantFunction(10.0), 90.1, c1);
//		CycleIterator cit = c.iterator(0.0, 90.1);
//		double val = 0.0;
//		do {
//			val = cit.next();
//			System.out.println(val);
//		} while (!Double.isNaN(val));
	}

}
