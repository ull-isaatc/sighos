/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Experiment;
import es.ull.isaatc.simulation.common.PooledExperiment;
import es.ull.isaatc.simulation.common.ModelPeriodicCycle;
import es.ull.isaatc.simulation.common.ModelTimeFunction;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.info.TimeChangeInfo;
import es.ull.isaatc.simulation.common.inforeceiver.CpuTimeView;
import es.ull.isaatc.simulation.common.inforeceiver.View;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.ElementInfo;
import es.ull.isaatc.simulation.common.info.ResourceInfo;
import es.ull.isaatc.simulation.common.inforeceiver.StdInfoView;
import es.ull.isaatc.util.Output;

class ProgressListener extends View {
	long nextMsg = 0;
	final long gap;
	int percentage = 0;
	public ProgressListener(Simulation simul) {
		super(simul, "Progress");
		addEntrance(TimeChangeInfo.class);
		addEntrance(SimulationStartInfo.class);
		gap = simul.getInternalEndTs() / 100;
		nextMsg = gap;
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			System.out.println("Starting!!");
		}
		else if (info instanceof TimeChangeInfo) {
			if (((TimeChangeInfo) info).getTs() >= nextMsg) {
				System.out.println("" + (++percentage) + "%");
				nextMsg += gap;
			}
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
	enum Type {SAMETIME, CONSECUTIVE, MIXED};
	
	static int nThreads = 3;
	static int nElem = 1024;
	static int nAct = 1024;
	static long actTime = nElem;
	static int nIter = 1000;
	static int nExp = 1;
	static int mixFactor = 2;
	static Type type = Type.SAMETIME;
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static boolean debug = false;
	static Time endTs;
	static PrintStream out = System.out;
	static TimeUnit unit = TimeUnit.MINUTE;
	static SimulationFactory.SimulationType simType = SimulationType.GROUPED;

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
				Simulation sim = null; 
				sim = getTestOcurrenceSimN2(simType, ind);
				sim.setNThreads(nThreads);
				
				if (debug)
					sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
				sim.addInfoReceiver(new CpuTimeView(sim));
				sim.addInfoReceiver(new ProgressListener(sim));
//				sim.addInfoReceiver(new StdInfoView(sim));
//				sim.setOutput(new Output(true));
				return sim;
			}
			
		}.start();
		
//		new Experiment("Same Time", nExp) {
//			long t1;
//			@Override
//			public Simulation getSimulation(int ind) {
//				return getTestOcurrenceSimN2(simType, ind);
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
//						sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
//					sim.addInfoReceiver(new CpuTimeView(sim));
//					sim.addInfoReceiver(new ProgressListener(sim));
//					Thread th = new Thread(sim);
//					th.start();
//					try {
//						th.join();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//				end();		
//			}
//			protected void end() {
//				System.out.println("" + (System.currentTimeMillis() - t1));
//			}			
//		}.start();
	}
	
	public static Simulation getTestOcurrenceSimN2(SimulationFactory.SimulationType simType, int id) {
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];
		
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		ModelPeriodicCycle allCycle = new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, Time.getZero(), endTs);
		factory.getSimulation().putVar("AA", 0);
		
//		SimulationUserCode code = new SimulationUserCode();
//		code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < 100000; i++)" +
//				"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
//				"return super.beforeRequest(e);");
		for (int i = 0; i < acts.length; i++) {
			rts[i] = factory.getResourceTypeInstance(i, "RT" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", /*code,*/ acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		ModelTimeFunction oneFunction = new ModelTimeFunction(unit, "ConstantVariate", 1);

		for (int i = 0; i < nElem; i++) {
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
			res[i].addTimeTableEntry(allCycle, endTs, rts[i % nAct]);
		}
		
		int elemCounter = 0;
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0));
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
				}
				break;
		}
		return factory.getSimulation();

	}

	public static Simulation getTestOcurrenceSimN(SimulationFactory.SimulationType simType, int id) {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, Time.getZero(), endTs);
		factory.getSimulation().putVar("AA", 0);
		
//		SimulationUserCode code = new SimulationUserCode();
//		code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < 100000; i++)" +
//				"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
//				"return super.beforeRequest(e);");
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", /*code,*/ acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		ModelTimeFunction oneFunction = new ModelTimeFunction(unit, "ConstantVariate", 1);
		
		WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[0], new int[0]);
		int elemCounter = 0;
		switch(type) {
			case SAMETIME:
				for (TimeDrivenActivity act : acts) 
					act.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0));
				}
				break;
			case CONSECUTIVE:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
				}
				break;
			case MIXED:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
				}
				break;
		}
		return factory.getSimulation();

	}
	
	public static Simulation getTestOcurrenceSimNRes(SimulationFactory.SimulationType simType, int id) {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, Time.getZero(), endTs);
		
		ModelTimeFunction oneFunction = new ModelTimeFunction(unit, "ConstantVariate", 1);
		ModelPeriodicCycle allCycle = new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem; i++)
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = factory.getResourceTypeInstance(i, "RT_TEST" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
//		for (int j = 0; j < nElem; j++)
//			res[j].addTimeTableEntry(allCycle, endTs, rts[j % acts.length]);
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		int elemCounter = 0;
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, allCycle);					
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
					
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
					
				}
				break;
		}
		return factory.getSimulation();
	}
	
	public static Simulation getTestOcurrenceSimNResMix(SimulationFactory.SimulationType simType, int id) {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, Time.getZero(), endTs);
		
		ModelTimeFunction oneFunction = new ModelTimeFunction(unit, "ConstantVariate", 1);
		ModelPeriodicCycle allCycle = new ModelPeriodicCycle(unit, Time.getZero(), new ModelTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem / 2; i++)
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = factory.getResourceTypeInstance(i, "RT_TEST" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem / 2; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		int elemCounter = 0;
		switch(type) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, allCycle);					
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
					
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new ModelTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGenerator(elemCounter++, creator, new ModelPeriodicCycle(unit, Time.getZero(), oneFunction, nElem));
					
				}
				break;
		}

		return factory.getSimulation();		
	}
	
}
