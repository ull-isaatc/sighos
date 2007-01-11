package es.ull.isaatc.simulation.state.sql;

import es.ull.isaatc.random.*;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.util.*;

/** 
 * Se corresponde con la unidad que se va a simular.
 * Se está simulando el servicio de Ginecología, separado en cuatro patologías:
 * Funcional, Orgánica, Oncológica y Precoz. Para cada patología se define una
 * clase de médico y una clase de consulta.
 * Unicamente se ha definido un recurso activo por cada clase de recurso.
 * El resultado de todo esto sería que se dispone de 4 gestores de actividades, 
 * uno por cada patología.
 */
class Example1 extends Simulation {
    static final int NELEM = 10;
    
	Example1(double startTs, double endTs) {
		super("Sistema de análisis", startTs, endTs);
    }
    
    protected void createModel() {
        // STEP 1: Initializa activities
        Activity act = new Activity(0, this, "Sample activity");
 
        // STEP 2: Initialize resource types
        ResourceType rt1 = new ResourceType(0, this, "Sample resource type");

        // STEP 3: Initialize the workgroups
        WorkGroup wg1 = act.getNewWorkGroup(0, new Fixed(1500));
        wg1.add(rt1, 1);
        
        // STEP 4: Create the element types
        new ElementType(0, this, "Element type 1");
		
        // STEP 5: Initialize the resources
        Cycle c = new Cycle(0, new Fixed(14400.0), 0);
        new Resource(1, this, "Enfermero 1").addTimeTableEntry(c, 1500, getResourceType(0));
        createMetaFlow();
    }
	
    protected void createMetaFlow() {
	int i = 0;
	MetaFlow flow; 
	SequenceMetaFlow seq1 = new SequenceMetaFlow(i++, new Fixed(1));
	new SingleMetaFlow(i++, seq1, new Fixed(1), getActivity(0));
	new SingleMetaFlow(i++, seq1, new Fixed(1), getActivity(0));
	
	flow = seq1;
        Cycle c = new Cycle(0.0, new Fixed(1440.0), 0);
        new ElementGenerator(this, new Fixed(NELEM), c.iterator(startTs, endTs), getElementType(0), flow);
    }
}

class ExpExample1 extends Experiment {
	static final int NEXP = 5;
	static final double ENDTS = 60 * 24;
	static final double PERIOD = 1440.0;
	double prevStart = 0.0, prevEnd = 0.0;
	
	public ExpExample1() {
//		super("Hospital", NEXP, new StdResultProcessor(1440.0), new Output(Output.DEBUGLEVEL));
		super("Hospital", NEXP);
	}

	public ExpExample1(double prevStart, double prevEnd) {
		super("Hospital", NEXP);
		this.prevStart = prevStart;
		this.prevEnd = prevEnd;		
	}

	public Simulation getSimulation(int ind) {
	    Example1 sim = null;
	    sim = new Example1(ENDTS * ind, ENDTS * (ind + 1));
//	    sim.addListener(new StdInfoListener());
//	    sim.addListener(new SimulationStateListener());
	    return sim;
	}
	
	public void start() {
            Simulation prevSim = getSimulation(0);
            prevSim.start();
            for (int i = 1; i < nExperiments; i++) {
            	DBStateProcessor dbStateProcessor = new DBStateProcessor();
            	dbStateProcessor.process(prevSim.getState());
            	System.out.println("STATE STORED\tID : " + dbStateProcessor.getSimulationId());
            	System.out.println(prevSim.getState().toString());
		Simulation sim = getSimulation(i);
		SimulationState state = dbStateProcessor.getState(dbStateProcessor.getSimulationId());
		System.out.println("STATE LOADED\tID : " + dbStateProcessor.getSimulationId());
		System.out.println(state.toString());
//		sim.start(prevSim.getState());
		sim.start(state);
		prevSim = sim;
            	System.out.println("---------------------------------------------------------------");
            }
	}
}

public class TestDBSimpleExample1 {
    
    public static void main(String arg[]) {
	ExpExample1 exp = new ExpExample1();
	exp.start();

    } // fin del main
} // fin de Hospital
