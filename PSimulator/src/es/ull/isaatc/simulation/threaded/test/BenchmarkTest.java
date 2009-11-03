/**
 * 
 */
package es.ull.isaatc.simulation.threaded.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.threaded.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.inforeceiver.CpuTimeView;
import es.ull.isaatc.simulation.common.inforeceiver.View;
import es.ull.isaatc.simulation.common.Element;
import es.ull.isaatc.simulation.threaded.ElementCreator;
import es.ull.isaatc.simulation.threaded.ElementType;
import es.ull.isaatc.simulation.threaded.Resource;
import es.ull.isaatc.simulation.threaded.ResourceType;
import es.ull.isaatc.simulation.threaded.Simulation;
import es.ull.isaatc.simulation.threaded.StandAloneLPSimulation;
import es.ull.isaatc.simulation.threaded.TimeDrivenActivity;
import es.ull.isaatc.simulation.threaded.TimeDrivenGenerator;
import es.ull.isaatc.simulation.threaded.WorkGroup;
import es.ull.isaatc.simulation.threaded.flow.ForLoopFlow;
import es.ull.isaatc.simulation.threaded.flow.SingleFlow;

enum Type {SAMETIME, CONSECUTIVE, MIXED};

class TestOcurrenceSimN extends StandAloneLPSimulation {
	Type type;
	int nIter;
	double actTime;
	int nElem;
	int mixFactor;
	TimeDrivenActivity[] acts;
	ForLoopFlow[] smfs;

	public TestOcurrenceSimN(Type type, int id, int nAct, int nElem, double actTime, int nIter, Time endTs) {
		super(id, "TEST",  TimeUnit.MINUTE, Time.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new TimeDrivenActivity[nAct];
		this.smfs = new ForLoopFlow[nAct];
		this.actTime = actTime;
		this.type = type;
	}
	
	public TestOcurrenceSimN(int id, int nAct, int nElem, double actTime, int nIter, Time endTs, int mixFactor) {
		super(id, "TEST",  TimeUnit.MINUTE, Time.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new TimeDrivenActivity[nAct];
		this.smfs = new ForLoopFlow[nAct];
		this.actTime = actTime;
		this.type = Type.MIXED;
		this.mixFactor = mixFactor;
	}
	
	@Override
	protected void createModel() {
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new TimeDrivenActivity(i, this, "A_TEST" + i);
			SingleFlow sf = new SingleFlow(this, acts[i]) {
				double stupidCalculus = 0.0;
				@Override
				public boolean beforeRequest(Element e) {
					for (int i = 1; i < 200; i++)
						stupidCalculus += Math.log(i);					
					return super.beforeRequest(e);
				}
			};
			smfs[i] = new ForLoopFlow(this, sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = new ElementType(0, this, "E_TEST");
		ModelTimeFunction oneFunction = new ModelTimeFunction(this, "ConstantVariate", 1);
		switch(type) {
			case SAMETIME:
				for (TimeDrivenActivity act : acts) 
					act.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), new ModelTimeFunction(this, "ConstantVariate", endTs.getValue()), 0));
				break;
			case CONSECUTIVE:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime / mixFactor));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class TestOcurrenceSimNRes extends StandAloneLPSimulation {
	final static double MIX_FACTOR = 8.0;
	Type type;
	int nIter;
	double actTime;
	int nElem;
	TimeDrivenActivity[] acts;
	ForLoopFlow[] smfs;
	ResourceType[] rts;
	WorkGroup[] wgs;
	Resource[] res;

	public TestOcurrenceSimNRes(Type type, int id, int nAct, int nElem, double actTime, int nIter, Time endTs) {
		super(id, "TEST",  TimeUnit.MINUTE, Time.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new TimeDrivenActivity[nAct];
		this.smfs = new ForLoopFlow[nAct];
		this.rts = new ResourceType[nAct];
		this.wgs = new WorkGroup[nAct];
		this.res = new Resource[nElem];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		ModelTimeFunction oneFunction = new ModelTimeFunction(this, "ConstantVariate", 1);
		ModelPeriodicCycle allCycle = new ModelPeriodicCycle(this, Time.getZero(), new ModelTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem; i++)
			res[i] = new Resource(i, this, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new TimeDrivenActivity(i, this, "A_TEST" + i);
			smfs[i] = new ForLoopFlow(this, new SingleFlow(this, acts[i]), TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = new ResourceType(i, this, "RT_TEST" + i);
			wgs[i] = new WorkGroup(rts[i], 1);
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
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
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
	TimeDrivenActivity[] acts;
	ForLoopFlow[] smfs;
	ResourceType[] rts;
	WorkGroup[] wgs;
	Resource[] res;

	public TestOcurrenceSimNResMix(Type type, int id, int nAct, int nElem, double actTime, int nIter, Time endTs) {
		super(id, "TEST",  TimeUnit.MINUTE, Time.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new TimeDrivenActivity[nAct];
		this.smfs = new ForLoopFlow[nAct];
		this.rts = new ResourceType[nAct];
		this.wgs = new WorkGroup[nAct];
		this.res = new Resource[nElem / 2];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		ModelTimeFunction oneFunction = new ModelTimeFunction(this, "ConstantVariate", 1);
		ModelPeriodicCycle allCycle = new ModelPeriodicCycle(this, Time.getZero(), new ModelTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem / 2; i++)
			res[i] = new Resource(i, this, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new TimeDrivenActivity(i, this, "A_TEST" + i);
			smfs[i] = new ForLoopFlow(this, new SingleFlow(this, acts[i]), TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = new ResourceType(i, this, "RT_TEST" + i);
			wgs[i] = new WorkGroup(rts[i], 1);
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem / 2; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
		ElementType et = new ElementType(0, this, "E_TEST");
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new ModelPeriodicCycle(this, Time.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class BenchmarkListener extends View {
	long elemEvents = 0;
	long startEv = 0;
	long endEv = 0;
	long startActEv = 0;
	long reqActEv = 0;
	long endActEv = 0;
	
	long resEvents = 0;
	long concurrentEvents = 0;
	double lastEventTs = -1.0;
	long maxConcurrentEvents = 0;
	long cpuTime;
	PrintStream out;

	public BenchmarkListener(Simulation simul, PrintStream out) {
		super(simul, "Bench");
		this.out = out;
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(ElementInfo.class);
		addEntrance(ElementActionInfo.class);
		addEntrance(ResourceInfo.class);
	}

	public synchronized void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			elemEvents++;
			if (((ElementInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ElementInfo) info).getTs();
			}
			if (((ElementInfo) info).getType() == ElementInfo.Type.START)
				startEv++;
			else if (((ElementInfo) info).getType() == ElementInfo.Type.FINISH)
				endEv++;
		}
		else if (info instanceof ElementActionInfo) {
			elemEvents++;
			if (((ElementActionInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ElementActionInfo) info).getTs();
			}
			if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.REQACT)
				reqActEv++;
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.STAACT)
				startActEv++;
			else if (((ElementActionInfo) info).getType() == ElementActionInfo.Type.ENDACT)
				endActEv++;
			
		}
		else if (info instanceof ResourceInfo) {
			resEvents++;
			if (((ResourceInfo) info).getTs() == lastEventTs) {
				concurrentEvents++;
				if (concurrentEvents > maxConcurrentEvents)
					maxConcurrentEvents = concurrentEvents;
			}
			else {
				concurrentEvents = 0;
				lastEventTs = ((ResourceInfo) info).getTs();
			}
		}
		else if (info instanceof SimulationStartInfo) {
			cpuTime = ((SimulationStartInfo)info).getCpuTime();
		}
		else if (info instanceof SimulationEndInfo) {
			cpuTime = ((SimulationEndInfo)info).getCpuTime() - cpuTime;
			out.println("T:\t" + cpuTime + "\tElem Events:\t" + elemEvents + "\tRes Events:\t" + resEvents + "\nMax. concurrent Events:\t" + maxConcurrentEvents);
			out.println("STA:\t" + startEv + "\tEND:\t" + endEv + "\tREQ:\t" + reqActEv + "\tSAC\t" + startActEv + "\tEAC\t" + endActEv);
		}
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkTest {
	static int nThreads = 1;
	static int nElem = 4;
	static int nAct = 4;
	static double actTime = nElem;
	static int nIter = 50000;
	static int nExp = 1;
	static int mixFactor = 2;
	static Type type = Type.SAMETIME;
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static boolean debug = false;
	static Time endTs;
	static PrintStream out = System.out;

	public static long computeSomething() {
		long t1 = System.currentTimeMillis();
		long result = 0;
		for (int i = 1; i < 100000000; i++)
			result += Math.log(i);
		System.out.println("Something done ("+ result + "): " + (System.currentTimeMillis() - t1));
		return result;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length >= 7) {
			type = Type.valueOf(args[0]);
			nAct = Integer.parseInt(args[1]);
			nElem = Integer.parseInt(args[2]);
			actTime = Integer.parseInt(args[3]);
			nIter = Integer.parseInt(args[4]);
			nThreads = Integer.parseInt(args[5]);
			nExp = Integer.parseInt(args[6]);
			if (args.length > 7) {
				if (type == Type.MIXED) {
					mixFactor = Integer.parseInt(args[7]);
				}
				else
					debug = "D".equals(args[7]);
			}
		} else if (args.length > 0) { 
			System.err.println("Wrong number of arguments.\n Arguments expected: 6");
			System.exit(0);
		} 
		endTs = new Time(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);	
		
//		for (int e = 0; e < 3; e++) {
		new PooledExperiment("Same Time", nExp/*, Executors.newFixedThreadPool(3)*/) {
			long t1;

			@Override
			public void start() {
				t1 = System.currentTimeMillis();
				super.start();
			}
			
			@Override
			protected void end() {
				super.end();
				System.out.println("" + (System.currentTimeMillis() - t1));
			}
			
			@Override
			public Simulation getSimulation(int ind) {
				Simulation sim = null; 
				if (Type.MIXED == type)
					sim = new TestOcurrenceSimN(ind, nAct, nElem, actTime, nIter, endTs, mixFactor);
				else
					sim = new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
//				sim.setOutput(new Output(true));
				sim.setNThreads(nThreads);
				
				if (debug)
					sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
				sim.addInfoReceiver(new CpuTimeView(sim));
				return sim;
			}
			
		}.start();

//		}

		
//		new Experiment("Same Time", nExp) {
//			long t1;
//			@Override
//			public Simulation getSimulation(int ind) {
//				Simulation sim = null;
//				if (Type.MIXED == type)
//					sim = new TestOcurrenceSimN(ind, nAct, nElem, actTime, nIter, endTs, mixFactor);
//				else
//					sim = new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
//				sim.setNThreads(nThreads);
////				sim.addInfoReciever(new StdInfoView(sim));
//				if (debug)
//					sim.addInfoReciever(new BenchmarkListener(sim, System.out));
//				sim.addInfoReciever(new CpuTimeView(sim));
//				return sim;
//			}
//
//			@Override
//			public void start() {
//				t1 = System.currentTimeMillis();
//				for (int i = 0; i < nExperiments; i++) {
//					getSimulation(i).run();
//				}
//				end();		
//			}
//			protected void end() {
//				System.out.println("" + (System.currentTimeMillis() - t1));
//			}			
//		}.start();
	}
}
