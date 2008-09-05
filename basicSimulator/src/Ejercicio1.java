import java.util.ArrayList;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.SequenceMetaFlow;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SimultaneousMetaFlow;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.listener.ActivityListener;
import es.ull.isaatc.simulation.listener.ActivityTimeListener;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StdInfoListener;

class Simulation1 extends StandAloneLPSimulation {
	int ndays;
	
	public Simulation1(int id, int ndays) {
		super(id, "Ejercicio1", SimulationTimeUnit.MINUTE, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTime(SimulationTimeUnit.DAY, ndays));
		this.ndays = ndays;
    }
    
    protected void createModel() {
    	new Activity(0, this, "Extracción de sangre");
        new Activity(1, this, "Análisis de sangre");
        new Activity(2, this, "Análisis de orina");    	
        new Activity(3, this, "Consulta primera");
        new Activity(4, this, "Consulta sucesiva");
        new Activity(5, this, "Radiografía");
        
        new ResourceType(0, this, "Doctor");
        new ResourceType(1, this, "Auxiliar");
        new ResourceType(2, this, "Máquina sangre");
        new ResourceType(3, this, "Máquina orina");
        new ResourceType(4, this, "Máquina RX");
        
        WorkGroup wg = new WorkGroup(0, this, "Doctores");
        wg.add(getResourceType(0), 1);
        getActivity(3).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 15.0, 2.0), wg);
        getActivity(4).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 12.0, 2.0), wg);
        
        wg = new WorkGroup(1, this, "Auxiliar");
        wg.add(getResourceType(1), 1);
        getActivity(0).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 5.0), wg);
        
        wg = new WorkGroup(2, this, "Sangre");
        wg.add(getResourceType(2), 1);
        getActivity(1).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 60.0), wg);

        wg = new WorkGroup(3, this, "Orina");
        wg.add(getResourceType(3), 1);
        getActivity(2).addWorkGroup(new SimulationTimeFunction(this, "UniformVariate", 40.0, 50.0), wg);
    
        wg = new WorkGroup(4, this, "RX");
        wg.add(getResourceType(4), 1);
        wg.add(getResourceType(1), 1);
        getActivity(5).addWorkGroup(new SimulationTimeFunction(this, "NormalVariate", 25.0, 5.0), wg);
        
        SimulationCycle subc1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 600), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 5);
        SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0 * 7), 0, subc1);
        
        new Resource(0, this, "Auxiliar 1").addTimeTableEntry(c1, new SimulationTime(SimulationTimeUnit.MINUTE, 240), getResourceType(1));
        
        SimulationCycle subc2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 480), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 5);
        SimulationCycle c2 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0 * 7), 0, subc2);

        new Resource(1, this, "Doctor 1").addTimeTableEntry(c2, new SimulationTime(SimulationTimeUnit.MINUTE, 420), getResourceType(0));
        new Resource(2, this, "Doctor 2").addTimeTableEntry(c2, new SimulationTime(SimulationTimeUnit.MINUTE, 420), getResourceType(0));
        
        SimulationCycle c = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0), new SimulationTimeFunction(this, "ConstantVariate", internalEndTs), 0);
        new Resource(3, this, "RX 1").addTimeTableEntry(c, endTs, getResourceType(4));
        ArrayList<ResourceType> roleList = new ArrayList<ResourceType>();
        roleList.add(getResourceType(2));
        roleList.add(getResourceType(3));
        new Resource(4, this, "Maq 1").addTimeTableEntry(c, endTs, roleList);
        
        SequenceMetaFlow sec = new SequenceMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", 1));
        new SingleMetaFlow(1, sec, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(3));
        SequenceMetaFlow sec2 = new SequenceMetaFlow(2, sec, RandomVariateFactory.getInstance("DiscreteUniformVariate", 1, 3));
        SimultaneousMetaFlow sim = new SimultaneousMetaFlow(3, sec2, RandomVariateFactory.getInstance("ConstantVariate", 1));
        SequenceMetaFlow sec3 = new  SequenceMetaFlow(4, sim, RandomVariateFactory.getInstance("ConstantVariate", 1));
        new SingleMetaFlow(5, sec3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
        new SingleMetaFlow(6, sec3, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
        new SingleMetaFlow(7, sim, RandomVariateFactory.getInstance("UniformVariate", 0.75, 2), getActivity(2));
        new SingleMetaFlow(8, sim, RandomVariateFactory.getInstance("UniformVariate", 0, 1.25), getActivity(5));
        new SingleMetaFlow(9, sec2, RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(4));
        
        new ElementType(0, this, "Paciente");
        SimulationCycle cGen = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), ndays);
        new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("NormalVariate", 21, 2), getElementType(0), sec), cGen);        
    }
	
}
/**
 * 
 */
class Experiment1 extends PooledExperiment {
	static final int NEXP = 1;
    static final int NDIAS = 4;
	static final double PERIOD = 1440.0;
	
	public Experiment1() {
		super("Hospital", NEXP);
	}

	public Simulation getSimulation(int ind) {
		Simulation1 sim = null;
		ListenerController cont = new ListenerController();
		sim = new Simulation1(ind, NDIAS);
		sim.setListenerController(cont);
		cont.addListener(new StdInfoListener());
		cont.addListener(new ActivityListener(PERIOD));
		cont.addListener(new ActivityTimeListener(PERIOD));
		return sim;
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class Ejercicio1 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment1().start();
	}

}
