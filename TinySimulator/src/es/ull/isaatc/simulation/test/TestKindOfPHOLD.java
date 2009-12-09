package es.ull.isaatc.simulation.test;


import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import es.ull.isaatc.simulation.BasicElement;
import es.ull.isaatc.simulation.BasicElementCreator;
import es.ull.isaatc.simulation.Generator;
import es.ull.isaatc.simulation.LogicalProcess;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.Time;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.TimeUnit;
import es.ull.isaatc.simulation.inforeceiver.CpuTimeView;

class RequestingElement extends BasicElement {
	static AtomicInteger count = new AtomicInteger(0);
	int eventIter;
	int eventProcess;
	static Time minute = new Time(TimeUnit.MINUTE, 1);
	
	public RequestingElement(int id, KindOfPHOLDSimulation simul, int eventIter, int eventProcess) {
		super(id, simul);
		this.eventIter = eventIter;
		this.eventProcess = eventProcess;
	}
	
	@Override
	protected void end() {
	}

	@Override
	protected void init() {
		defLP.addEvent(new ReqEvent(ts, defLP));		
	}
	
	class ReqEvent extends BasicElement.DiscreteEvent {

		public ReqEvent(long ts, LogicalProcess lp) {
			super(ts, lp);
		}
		
		@Override
		public void event() {
			((KindOfPHOLDSimulation)simul).acquire(id);
			long res = 0;
			for (int i = 1; i < eventProcess; i++)
				res += Math.log(i);
			((KindOfPHOLDSimulation)simul).setValue(id, res);
			debug("Weird event " + eventIter);
			count.incrementAndGet();
			((KindOfPHOLDSimulation)simul).release(id);
			if (--eventIter > 0)
				defLP.addEvent(new ReqEvent(ts + simul.simulationTime2Long(minute), lp));
			else
				notifyEnd();
		}
		
	}
}

class RequestingElementCreator implements BasicElementCreator {
	static int id = 0;
	int nElem;
	int eventIter;
	int eventProcess;
	KindOfPHOLDSimulation simul;
	
	/**
	 * @param elem
	 * @param eventIter
	 * @param eventProcess
	 */
	public RequestingElementCreator(KindOfPHOLDSimulation simul, int elem, int eventIter, int eventProcess) {
		nElem = elem;
		this.eventIter = eventIter;
		this.eventProcess = eventProcess;
		this.simul = simul;
	}

	@Override
	public void create(Generator gen) {
		for (int i = 0; i < nElem; i++) {
			RequestingElement e = new RequestingElement(id++, simul, eventIter, eventProcess);
			simul.getDefaultLogicalProcess().addEvent(e.getStartEvent(simul.getDefaultLogicalProcess(), simul.getDefaultLogicalProcess().getTs()));
		}
		
	}

}

/**
 * A very simple simulation. It creates a set of elements which perform a certain amount of
 * computation N times. The results of the computation are stored in a shared variable. The 
 * amount of shared variables is an input parameter and sets the potential parallelism of the
 * test.
 * @author Iván Castilla Rodríguez
 */
class KindOfPHOLDSimulation extends Simulation {
	long []values;
	Semaphore []sems;
	int nElem;
	int eventIter;
	int eventProcess;

	/**
	 * Creates a new simulation
	 * @param id Simulation's identifier
	 * @param endTs Timestamp of simulation's end
	 * @param n Degree of parallelism
	 * @param nElem Total amount of elements to be created
	 * @param eventIter Iterations if the problem
	 * @param eventProcess A measure of the execution time of the event
	 * @param type Type of logical process to be used
	 */
	public KindOfPHOLDSimulation(int id, Time endTs, int n, int nElem, int eventIter, int eventProcess, Simulation.LPType type) {
		super(id, "PHOLD", TimeUnit.MINUTE, Time.getZero(), endTs, type);
		values = new long[n];
		sems = new Semaphore[n];
		for (int i = 0; i < sems.length; i++)
			sems[i] = new Semaphore(1);
		this.nElem = nElem;
		this.eventIter = eventIter;
		this.eventProcess = eventProcess;
	}

	public void setValue(int id, long value) {
		values[id % values.length] = value;
	}
	
	public void acquire(int id) {
		try {
			sems[id % values.length].acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void release(int id) {
		sems[id % values.length].release();
	}
	
	@Override
	protected void createModel() {
		BasicElementCreator elemCreator = new RequestingElementCreator(this, nElem, eventIter, eventProcess);
		new TimeDrivenGenerator(this, elemCreator, new SimulationPeriodicCycle(this, Time.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0));
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestKindOfPHOLD {
	static int nElem = 1024;
	static int nAct = 1024;
	static int nExp = 1;
	static int eventIter = 1000;
	static int eventProcess = 100;
	static Simulation.LPType type = Simulation.LPType.SEQUENTIAL;
	static int nThreads = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < nExp; i++) {
			Simulation sim = new KindOfPHOLDSimulation(i, new Time(TimeUnit.MINUTE, eventIter + 1), nAct, nElem, eventIter, eventProcess, type);
			sim.setNThreads(nThreads);
			sim.addInfoReciever(new CpuTimeView(sim));
//			sim.setOutput(new Output(true));
			sim.run();
		}
		System.out.println("" + RequestingElement.count + " events");
	}


}
