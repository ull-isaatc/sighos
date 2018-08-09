import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.simulation.model.ElementType;
import es.ull.iis.simulation.model.Resource;
import es.ull.iis.simulation.model.ResourceType;
import es.ull.iis.simulation.model.SimulationPeriodicCycle;
import es.ull.iis.simulation.model.SimulationTimeFunction;
import es.ull.iis.simulation.model.TimeStamp;
import es.ull.iis.simulation.model.TimeUnit;
import es.ull.iis.simulation.model.WorkGroup;
import es.ull.iis.simulation.model.flow.ActivityFlow;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * 
 */

class SimulTest {
	protected SimulationFactory factory;
	public final static TimeStamp SIMSTART = TimeStamp.getZero();
	public final static TimeStamp SIMEND = TimeStamp.getDay();
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	public final static TimeStamp RESAVAILABLE = new TimeStamp(TimeUnit.HOUR, 7);
	public final static TimeStamp RESSTART = new TimeStamp(TimeUnit.HOUR, 8);
	public final static TimeStamp RESPERIOD = TimeStamp.getDay();
	public final static TimeStamp GENSTART = TimeStamp.getZero();
	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	
	SimulTest() {
		factory = new SimulationFactory(1, "test", SIMUNIT, SIMSTART, SIMEND);
		ResourceType rt = factory.getResourceTypeInstance("Médico");
		Resource res = factory.getResourceInstance("Médico 1");
		res.addTimeTableEntry(new SimulationPeriodicCycle(SIMUNIT, RESSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0), RESAVAILABLE, rt);

        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
		ActivityFlow act = null;
		act = (ActivityFlow)factory.getFlowInstance("ActivityFlow", "Consulta");
    	act.addWorkGroup(0, wg, 10);
		
        ElementType et = factory.getElementTypeInstance("Paciente");
        et.putVar("coste", 0);
        
        factory.getTimeDrivenElementGeneratorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 10), et, act, 
        		new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0));
        
        factory.getSimulation().addInfoReceiver(new StdInfoView());
	}
	
	void run() {
		factory.getSimulation().run();
	}
}

class CalibrationTest {
	private final static int NPATIENTS = 8;
	private final static int arrayOfTimes[][] = {
		{20, Integer.MAX_VALUE, 21}, // Should take next EARM
		{20, 19, 31}, 
		{25, 26, Integer.MAX_VALUE},
		{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
		{25, Integer.MAX_VALUE, Integer.MAX_VALUE},
		{40, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
		{20, Integer.MAX_VALUE, 10}, // Should work fine
		{20, 10, Integer.MAX_VALUE} // Should work fine
	};
	
	private final static int arrayOfTimes2[][] = {
			{20, 21, Integer.MAX_VALUE}, // Should take next EARM
			{20, 31, 19}, 
			{25, Integer.MAX_VALUE, 26},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{25, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
			{20, 10, Integer.MAX_VALUE}, // Should work fine
			{20, Integer.MAX_VALUE, 10} // Should work fine
		};
		
	private final static int arrayOfTimes3[][] = {
			{10, Integer.MAX_VALUE, Integer.MAX_VALUE}, // Should work fine
			{20, 10, Integer.MAX_VALUE}, // Should work fine
			{20, Integer.MAX_VALUE, 10}, // Should work fine
			{20, 21, Integer.MAX_VALUE}, // Should take next EARM
			{20, 31, 19}, 
			{25, Integer.MAX_VALUE, 21},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE},
			{40, Integer.MAX_VALUE, Integer.MAX_VALUE}
		};
	
	protected static void testThis() {
		
	}
}

	class WeibullTest {
		final double alpha;
		final double beta;
		final int n;
		final RandomVariate rnd;
		
		public WeibullTest(int n, double alpha, double beta) {
			this.n = n;
			this.alpha = alpha;
			this.beta = beta;
			rnd = RandomVariateFactory.getInstance("WeibullVariate", alpha, beta);
		}
		
		public void test() {
			for (int i = 0; i < n; i++)
				System.out.println(rnd.generate());
		}
	}
	
/**
 * @author icasrod
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		SimulTest sim = new SimulTest();
//		sim.run();
		new WeibullTest(5000, 1.999174026, 478.7268718).test();
	}

}
