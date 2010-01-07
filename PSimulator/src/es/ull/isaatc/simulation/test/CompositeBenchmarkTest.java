/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Experiment;
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
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.info.SimulationStartInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

class FileCPUTimeView extends View {
	PrintWriter buf;
	protected long iniT;
	protected long endT;
	
	public FileCPUTimeView(Simulation simul, PrintWriter buf) {
		super(simul, "CPU Time viewer");
		this.buf = buf;
		addEntrance(SimulationStartInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof SimulationStartInfo) {
			iniT = ((SimulationStartInfo)info).getCpuTime();
		}
		else if (info instanceof SimulationEndInfo) {
			endT = ((SimulationEndInfo)info).getCpuTime();
			buf.println("" + (endT - iniT));
			System.out.println("" + (endT - iniT));
			buf.flush();
		}
	}
	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CompositeBenchmarkTest {
	enum Type {SAMETIME, CONSECUTIVE, MIXED};
	
	static int []nThreads = {1,2,3};
	static int []nObjects = {256, 1024, 2048};
	static int []nIters = {10000};
	static int nExp = 15;
	static Type type = Type.SAMETIME;
	static boolean sequential = true;
	static SimulationFactory.SimulationType []simTypes = {SimulationType.GROUPED, SimulationType.GROUPEDX, SimulationType.BUFFERED};
//	static Type type = Type.CONSECUTIVE;
//	static Type type = Type.MIXED;
	static int mixFactor = 2;
	static boolean debug = false;
	static PrintStream out = System.out;
	static TimeUnit unit = TimeUnit.MINUTE;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Same Time", nExp) {
			@Override
			// We are not going to use this method
			public Simulation getSimulation(int ind) {
				return null;
			}

			@Override
			public void start() {
				PrintWriter buf = null;
				try {
					buf = new PrintWriter(new BufferedWriter(new FileWriter("c:\\res.txt")));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Simulation sim = null;
				for (int i = 0; i < nExperiments; i++) {
					for (int nIter : nIters) {
						for (int j = 1; j < nObjects.length; j++) {
							for (int k = 0; k <= j; k++) {
								if (sequential) {
									buf.print("SEQUENTIAL\t0\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
									System.out.print("SEQUENTIAL\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
									sim = getTestOcurrenceSimN2(SimulationType.SEQUENTIAL, i, nIter, nObjects[k], nObjects[j]);
									if (debug)
										sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
									sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
//									sim.addInfoReceiver(new ProgressListener(sim));
									sim.run();
									buf.print("SEQUENTIAL2\t0\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
									System.out.print("SEQUENTIAL2\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
									sim = getTestOcurrenceSimN2(SimulationType.SEQUENTIAL2, i, nIter, nObjects[k], nObjects[j]);
									if (debug)
										sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
									sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
//									sim.addInfoReceiver(new ProgressListener(sim));
									sim.run();
								}
								for (SimulationType simType : simTypes) {
									for (int nTh : nThreads) {
										buf.print("" + simType + "\t" + nTh + "\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
										System.out.print("" + simType + "\t" + nTh + "\t" + nIter + "\t" + nObjects[k] + "\t" + nObjects[j] + "\t");
										sim = getTestOcurrenceSimN2(simType, i, nIter, nObjects[k], nObjects[j]);
										sim.setNThreads(nTh);
										if (debug)
											sim.addInfoReceiver(new BenchmarkListener(sim, System.out));
										sim.addInfoReceiver(new FileCPUTimeView(sim, buf));
//										sim.addInfoReceiver(new ProgressListener(sim));
										sim.run();
									}
									
								}
							}
						}
					}
				}
				buf.close();
			}
		}.start();
	}
	
	public static Simulation getTestOcurrenceSimN2(SimulationFactory.SimulationType simType, int id, int nIter, int nAct, int nElem) {
		long actTime = nElem;
		Time endTs = new Time(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);	
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

	public static Simulation getTestOcurrenceSimN(SimulationFactory.SimulationType simType, int id, int nIter, int nAct, int nElem) {
		long actTime = nElem;
		Time endTs = new Time(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
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
	
	public static Simulation getTestOcurrenceSimNRes(SimulationFactory.SimulationType simType, int id, int nIter, int nAct, int nElem) {
		long actTime = nElem;
		Time endTs = new Time(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
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
	
	public static Simulation getTestOcurrenceSimNResMix(SimulationFactory.SimulationType simType, int id, int nIter, int nAct, int nElem) {
		long actTime = nElem;
		Time endTs = new Time(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
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
