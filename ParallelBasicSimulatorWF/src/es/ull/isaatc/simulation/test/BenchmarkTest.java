/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.ActivityManager;
import es.ull.isaatc.simulation.BasicElement;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationExecutorSetter;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.flow.ForLoopFlow;
import es.ull.isaatc.simulation.flow.SingleFlow;
import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.ResourceInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.inforeceiver.CpuTimeView;
import es.ull.isaatc.simulation.inforeceiver.View;
import es.ull.isaatc.util.SingleThreadPool;
import es.ull.isaatc.util.StandardThreadPool;
import es.ull.isaatc.util.ThreadPool;

enum Type {SAMETIME, CONSECUTIVE, MIXED};

class TestOcurrenceSimN extends Simulation {
	final static double MIX_FACTOR = 10.0;
	Type type;
	int nIter;
	double actTime;
	int nElem;
	TimeDrivenActivity[] acts;
	ForLoopFlow[] smfs;

	public TestOcurrenceSimN(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.acts = new TimeDrivenActivity[nAct];
		this.smfs = new ForLoopFlow[nAct];
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		for (int i = 0; i < acts.length; i++) {
			acts[i] = new TimeDrivenActivity(i, this, "A_TEST" + i);
			smfs[i] = new ForLoopFlow(this, new SingleFlow(this, acts[i]), TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = new ElementType(0, this, "E_TEST");
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		switch(type) {
			case SAMETIME:
				for (TimeDrivenActivity act : acts) 
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0));
				break;
			case CONSECUTIVE:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR));
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class TestOcurrenceSimNRes extends Simulation {
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

	public TestOcurrenceSimNRes(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
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
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
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
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
		}
	}	
}

class TestOcurrenceSimNResMix extends Simulation {
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

	public TestOcurrenceSimNResMix(Type type, int id, int nAct, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
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
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(this, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
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
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf), 
							allCycle);
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR), wgs[i]);
				for (ForLoopFlow smf : smfs)
					new TimeDrivenGenerator(this, 
							new ElementCreator(this, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf), 
							new SimulationPeriodicCycle(this, SimulationTime.getZero(), oneFunction, nElem));
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

	public void infoEmited(SimulationInfo info) {
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

class SimulationFixedExecutionSetter extends SimulationExecutorSetter {
	int nThreads;
	/**
	 * @param simul
	 */
	public SimulationFixedExecutionSetter(Simulation simul, int nThreads) {
		super(simul);
		this.nThreads = nThreads;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.SimulationExecutorSetter#setExecutors()
	 */
	@Override
	protected void setExecutors() {
		int nThreadsLP = nThreads - simul.getActivityManagerList().size();
		int nThreadsAM;
		ThreadPool<BasicElement.DiscreteEvent> lpTp = null;
		if (nThreadsLP <= 0) {
			lpTp = new SingleThreadPool<BasicElement.DiscreteEvent>();
			nThreadsAM = nThreads - 1;
		}
		else {
			lpTp = new StandardThreadPool<BasicElement.DiscreteEvent>(nThreadsLP);
			nThreadsAM = nThreads - nThreadsLP;
		}
		executorList.add(lpTp);
		simul.getDefaultLogicalProcess().setExecutor(lpTp);

		if (nThreadsAM == 0) {
			executorList.add(lpTp);
			for (ActivityManager am : simul.getActivityManagerList())
				am.setExecutor(lpTp);
		}
		else {
			ArrayList<SingleThreadPool<BasicElement.DiscreteEvent>> amTps = new ArrayList<SingleThreadPool<BasicElement.DiscreteEvent>>(nThreadsAM);
			for (int i = 0; i < nThreadsAM; i++) {
				SingleThreadPool<BasicElement.DiscreteEvent> auxTp = new SingleThreadPool<BasicElement.DiscreteEvent>(); 
				amTps.add(auxTp);
				executorList.add(auxTp);
			}
			int cont = 0;
			for (ActivityManager am : simul.getActivityManagerList()) {
				am.setExecutor(amTps.get(cont));
				cont = (cont + 1) %  amTps.size();
			}
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
	static Type type = Type.SAMETIME;
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static boolean debug = false;
	static SimulationTime endTs;
	static PrintStream out = System.out;

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
			if (args.length > 7)
				debug = "D".equals(args[7]);
		} else if (args.length > 0) { 
			System.err.println("Wrong number of arguments.\n Arguments expected: 6");
			System.exit(0);
		} 
		endTs = new SimulationTime(SimulationTimeUnit.MINUTE, actTime * (nIter + 1) + 1);	
		
		new PooledExperiment("Same Time", nExp/*, Executors.newFixedThreadPool(nExp)*/) {
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
				Simulation sim = new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
				sim.setExecSetter(new SimulationFixedExecutionSetter(sim, nThreads));
				
				if (debug)
					sim.addInfoReciever(new BenchmarkListener(sim, System.out));
				sim.addInfoReciever(new CpuTimeView(sim));
				return sim;
			}
			
		}.start();
		
//		new Experiment("Same Time", nExp) {
//			long t1;
//			@Override
//			public Simulation getSimulation(int ind) {
//				return new TestOcurrenceSimN(type, ind, nAct, nElem, actTime, nIter, endTs);
//			}
//
//			@Override
//			public void start() {
//				t1 = System.currentTimeMillis();
//				for (int i = 0; i < nExperiments; i++) {
//					Simulation sim = getSimulation(i);
//					sim.setNThreads(nThreads);
//					
////					sim.addInfoReciever(new StdInfoView(sim));
//					if (debug)
//						sim.addInfoReciever(new BenchmarkListener(sim, System.out));
//					sim.addInfoReciever(new CpuTimeView(sim));
//					
//					sim.run();
//				}
//				end();		
//			}
//			protected void end() {
//				System.out.println("" + (System.currentTimeMillis() - t1));
//			}			
//		}.start();
	}
}
