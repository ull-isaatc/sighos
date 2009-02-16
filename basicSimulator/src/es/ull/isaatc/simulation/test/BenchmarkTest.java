/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationObjectInfo;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.SimulationObjectListener;
import es.ull.isaatc.simulation.listener.SimulationTimeListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;
import es.ull.isaatc.util.Output;

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

class TestOcurrenceSimNRes extends StandAloneLPSimulation {
	final static double MIX_FACTOR = 10.0;
	Type type;
	int nIter;
	double actTime;
	int nElem;
	Activity[] acts;
	SingleMetaFlow[] smfs;
	ResourceType[] rts;
	WorkGroup[] wgs;
	Resource[] res;

	public TestOcurrenceSimNRes(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new Activity[nAct];
		this.smfs = new SingleMetaFlow[nAct];
		this.rts = new ResourceType[nAct];
		this.wgs = new WorkGroup[nAct];
		this.res = new Resource[nElem];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem; i++)
			res[i] = new Resource(i, this, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new Activity(i, this, "A_TEST" + i);
			smfs[i] = new SingleMetaFlow(i, RandomVariateFactory.getInstance("ConstantVariate", nIter), acts[i]);
			rts[i] = new ResourceType(i, this, "RT_TEST" + i);
			wgs[i] = new WorkGroup(i, this, "WG_TEST" + i);
			wgs[i].add(rts[i], 1);
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
//		for (int j = 0; j < nElem; j++)
//			res[j].addTimeTableEntry(allCycle, endTs, rts[j % acts.length]);
		ElementType et = new ElementType(0, this, "E_TEST");
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class TestOcurrenceSimNResMix extends StandAloneLPSimulation {
	final static double MIX_FACTOR = 10.0;
	Type type;
	int nIter;
	double actTime;
	int nElem;
	Activity[] acts;
	SingleMetaFlow[] smfs;
	ResourceType[] rts;
	WorkGroup[] wgs;
	Resource[] res;

	public TestOcurrenceSimNResMix(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new Activity[nAct];
		this.smfs = new SingleMetaFlow[nAct];
		this.rts = new ResourceType[nAct];
		this.wgs = new WorkGroup[nAct];
		this.res = new Resource[nElem / 2];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem / 2; i++)
			res[i] = new Resource(i, this, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new Activity(i, this, "A_TEST" + i);
			smfs[i] = new SingleMetaFlow(i, RandomVariateFactory.getInstance("ConstantVariate", nIter), acts[i]);
			rts[i] = new ResourceType(i, this, "RT_TEST" + i);
			wgs[i] = new WorkGroup(i, this, "WG_TEST" + i);
			wgs[i].add(rts[i], 1);
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem / 2; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
		ElementType et = new ElementType(0, this, "E_TEST");
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (SingleMetaFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
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
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkTest {
	static int nThreads = 1;
	static int nElem = 256;
	static int nAct = 1;
	static double actTime = nElem;
	static int nIter = 10;
	static int nExp = 1;
	static Type type = Type.SAMETIME;
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static SimulationTime endTs; 
	static PrintStream out = System.out;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length == 7) {
			type = Type.valueOf(args[0]);
			nAct = Integer.parseInt(args[1]);
			nElem = Integer.parseInt(args[2]);
			actTime = Integer.parseInt(args[3]);
			nIter = Integer.parseInt(args[4]);
			nThreads = Integer.parseInt(args[5]);
			nExp = Integer.parseInt(args[6]);
		} else if (args.length > 0) { 
			System.err.println("Wrong number of arguments.\n Arguments expected: 6");
			System.exit(0);
		} 
		endTs = new SimulationTime(SimulationTimeUnit.MINUTE, actTime * (nIter + 1) + 1);
		new Experiment("Same Time", nExp) {
			@Override
			public Simulation getSimulation(int ind) {
				return new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
			}

			@Override
			public void start() {
				for (int i = 0; i < nExperiments; i++) {
					Simulation sim = getSimulation(i);
					sim.setNThreads(nThreads);
//					sim.setOutput(new Output(true));
					ListenerController cont = new ListenerController();
					sim.setListenerController(cont);
//					cont.addListener(new StdInfoListener());
					cont.addListener(new BenchmarkListener(out));
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
