import es.ull.iis.function.TimeFunctionFactory;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.inforeceiver.StdInfoView;

/**
 * 
 */

class SimulTest {
	protected SimulationObjectFactory factory;
	public final static TimeStamp SIMSTART = TimeStamp.getZero();
	public final static TimeStamp SIMEND = TimeStamp.getDay();
	public final static TimeUnit SIMUNIT = TimeUnit.MINUTE; 
	public final static TimeStamp RESAVAILABLE = new TimeStamp(TimeUnit.HOUR, 7);
	public final static TimeStamp RESSTART = new TimeStamp(TimeUnit.HOUR, 8);
	public final static TimeStamp RESPERIOD = TimeStamp.getDay();
	public final static TimeStamp GENSTART = TimeStamp.getZero();
	public final static TimeStamp GENPERIOD = TimeStamp.getDay();
	
	SimulTest() {
		factory = SimulationFactory.getInstance(SimulationType.SEQUENTIAL, 1, "test", SIMUNIT, SIMSTART, SIMEND);
		ResourceType rt = factory.getResourceTypeInstance("Médico");
		Resource res = factory.getResourceInstance("Médico 1");
		res.addTimeTableEntry(new SimulationPeriodicCycle(SIMUNIT, RESSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", RESPERIOD), 0), RESAVAILABLE, rt);

        WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[] {rt}, new int[] {1});
		TimeDrivenActivity act = null;
		act = factory.getTimeDrivenActivityInstance("Consulta");
    	act.addWorkGroup(new SimulationTimeFunction(SIMUNIT, "ConstantVariate", new TimeStamp(TimeUnit.MINUTE, 10)), wg);
		
        SingleFlow root = (SingleFlow)factory.getFlowInstance("SingleFlow", act);
        
        ElementType et = factory.getElementTypeInstance("Paciente");
        et.putVar("coste", 0);
        
        factory.getTimeDrivenGeneratorInstance(
        		factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 10), et, root), 
        		new SimulationPeriodicCycle(SIMUNIT, GENSTART, new SimulationTimeFunction(SIMUNIT, "ConstantVariate", GENPERIOD), 0));
        
        factory.getSimulation().addInfoReceiver(new StdInfoView(factory.getSimulation()));
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
		CalibrationTest.testThis();
	}

}
