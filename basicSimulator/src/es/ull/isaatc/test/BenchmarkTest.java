/**
 * 
 */
package es.ull.isaatc.test;

import java.io.PrintStream;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationObjectListener;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;

enum Type {SAMETIME, CONSECUTIVE, MIXED};

class TestOcurrenceSimN extends StandAloneLPSimulation {
	final static double MIX_FACTOR = 10.0;
	Type type;
	int nIter;
	double actTime;
	int nElem;
	Activity[] acts;
	SingleMetaFlow[] smfs;

	public TestOcurrenceSimN(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new Activity[nAct];
		this.smfs = new SingleMetaFlow[nAct];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new Activity(i, this, "A_TEST" + i);
			smfs[i] = new SingleMetaFlow(i, RandomVariateFactory.getInstance("ConstantVariate", nIter), acts[i]);
		}
		ElementType et = new ElementType(0, this, "E_TEST");
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		switch(type) {
			case SAMETIME:
				for (Activity act : acts) 
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0));
				break;
			case CONSECUTIVE:
				for (Activity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (Activity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR));
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class BenchmarkListener extends SimulationTimeListener implements SimulationObjectListener {
	int elemEvents = 0;
	int resEvents = 0;
	int concurrentEvents = 0;
	double lastEventTs = -1.0;
	int maxConcurrentEvents = 0;
	PrintStream out;

	public BenchmarkListener(PrintStream out) {
		super();
		this.out = out;
	}

	public void infoEmited(SimulationObjectInfo info) {
		if (info instanceof ElementInfo) {
			elemEvents++;
			if (info.getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = info.getTs();
			}
		}
		else if (info instanceof ResourceInfo) {
			resEvents++;
			if (info.getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = info.getTs();
			}
		}
	}
	
	@Override
	public void infoEmited(SimulationEndInfo info) {
		super.infoEmited(info);
		out.println(this + "Elem Events:\t" + elemEvents + "\tRes Events:\t" + resEvents + "\nMax. concurrent Events:\t" + maxConcurrentEvents);
	}
}

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class BenchmarkTest {
	static int nThreads = 2;
	static int nElem = 2000;
	static int nAct = 2;
	static double actTime = nElem;
	static int nIter = 10;
	static int nExp = 46;
	static Type type = Type.SAMETIME;
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static SimulationTime endTs = new SimulationTime(SimulationTimeUnit.MINUTE, actTime * (nIter + 1) + 1); 
	static PrintStream out = System.out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 6) {
			type = Type.valueOf(args[0]);
			nAct = Integer.parseInt(args[1]);
			nElem = Integer.parseInt(args[2]);
			actTime = Integer.parseInt(args[3]);
			nIter = Integer.parseInt(args[4]);
			nThreads = Integer.parseInt(args[5]);
		} else if (args.length > 0) { 
			System.err.println("Wrong number of arguments.\n Arguments expected: 6");
			System.exit(0);
		} 
		new Experiment("Same Time", nExp) {
			@Override
			public Simulation getSimulation(int ind) {
//				return new TestOcurrenceSim(Type.CONSECUTIVE, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim(Type.SAMETIME, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim(Type.MIXED, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim2(Type.CONSECUTIVE, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim2(Type.SAMETIME, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim2(Type.MIXED, ind, NELEM, ACTTIME, NITER, ENDTS);
				return new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
			}

			@Override
			public void start() {
				for (int i = 0; i < nExperiments; i++) {
					Simulation sim = getSimulation(i);
					sim.setNThreads(nThreads);
					ListenerController cont = new ListenerController();
					sim.setListenerController(cont);
//					cont.addListener(new StdInfoListener());
//					cont.addListener(new BenchmarkListener(out));
					cont.addListener(new SimulationTimeListener() {
						@Override
						public void infoEmited(SimulationEndInfo info) {
							super.infoEmited(info);
							out.println("" + (endT - iniT));
						}
						
					});
					sim.run();
				}
				end();		
			}			
		}.start();
	}
}
