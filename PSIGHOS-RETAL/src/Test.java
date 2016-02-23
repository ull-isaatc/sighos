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
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.iis.simulation.inforeceiver.StdInfoView;
import es.ull.iis.function.TimeFunctionFactory;

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

/**
 * @author icasrod
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SimulTest sim = new SimulTest();
		sim.run();

	}

}
