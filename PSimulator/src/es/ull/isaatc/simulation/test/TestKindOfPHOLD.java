/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.concurrent.atomic.AtomicLongArray;

import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.inforeceiver.CpuTimeView;
import es.ull.isaatc.simulation.threaded.BasicElement;
import es.ull.isaatc.simulation.threaded.BasicElementCreator;
import es.ull.isaatc.simulation.threaded.Generator;
import es.ull.isaatc.simulation.threaded.LogicalProcess;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;

class RequestingElement extends BasicElement {
	int eventIter;
	int eventProcess;
	static TimeStamp minute = new TimeStamp(TimeUnit.MINUTE, 1);
	
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
		addEvent(new ReqEvent(ts, defLP));		
	}
	
	class ReqEvent extends BasicElement.DiscreteEvent {

		public ReqEvent(long ts, LogicalProcess lp) {
			super(ts, lp);
		}
		
		@Override
		public void event() {
			long res = 0;
			for (int i = 1; i < eventProcess; i++)
				res += Math.log(i);
			((KindOfPHOLDSimulation)simul).setValue(id, res);
			debug("Weird event " + eventIter);
			if (--eventIter > 0)
				addEvent(new ReqEvent(ts + simul.simulationTime2Long(minute), lp));
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
			e.start(simul.getDefaultLogicalProcess());
		}
		
	}

}

class KindOfPHOLDSimulation extends Simulation {
	AtomicLongArray values;	
	int nElem;
	int eventIter;
	int eventProcess;
	
	public KindOfPHOLDSimulation(int id, TimeStamp endTs, int n, int nElem, int eventIter, int eventProcess) {
		super(id, "PHOLD", false, TimeUnit.MINUTE, TimeStamp.getZero(), endTs);
		values = new AtomicLongArray(n);
		this.nElem = nElem;
		this.eventIter = eventIter;
		this.eventProcess = eventProcess;
	}

	public void setValue(int id, long value) {
		values.set(id % values.length(), value);
	}
	
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestKindOfPHOLD {
	static int nThreads = 1;
	static int nElem = 4;
	static int nAct = 4;
	static int nIter = 50000;
	static int nExp = 3;
	static int eventIter = 40000;
	static int eventProcess = 200;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Kind of PHOLD", nExp) {
			long t1;

			@Override
			public void start() {
				t1 = System.nanoTime();
//				super.start();
				for (int i = 0; i < nExperiments; i++)
					getSimulation(i).run();
				end();		
			}
			
			@Override
			protected void end() {
				super.end();
				System.out.println("" + (System.nanoTime() - t1));
			}

			@Override
			public Simulation getSimulation(int ind) {
				KindOfPHOLDSimulation sim = new KindOfPHOLDSimulation(ind, new TimeStamp(TimeUnit.MINUTE, nIter + 1), nAct, nElem, eventIter, eventProcess);
				BasicElementCreator elemCreator = new RequestingElementCreator(sim, nElem, eventIter, eventProcess);
				new TimeDrivenGenerator(sim, elemCreator, new SimulationPeriodicCycle(sim.getTimeUnit(), TimeStamp.getZero(), new SimulationTimeFunction(sim.getTimeUnit(), "ConstantVariate", sim.getEndTs().getValue()), 0));
				sim.addInfoReceiver(new CpuTimeView(sim));
//				sim.setOutput(new Output(true));
				return sim;
			}
			
			
		}.start();

	}

}
