/**
 * 
 */
package es.ull.isaatc.simulation.test;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationFactory;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkModel {
	/**
	 * Defines different test models:
	 * - NORESOURCES: A model with E elements. Each one requests N times an activity. There are A
	 *   activities (A <= E), which are equally distributed among the elements. The activities have 
	 *   a fixed duration and use no resources.
	 * - RESOURCES: Exactly the same than NORESOURCES, but there are as many resources as elements. 
	 *   Resources are distributed in a way that all the activities can be performed.
	 * - CONFLICT: Again the same model, but this time, the resources have multiple timetable entries
	 *   and can be used by any activity. REVIEW!!
	 * - TOTALCONFLICT: Again the same model, but this time, the resources have multiple timetable 
	 *   entries and can be used by any activity.
	 * - PARALLEL: Each resource simultaneously requests ALL the activities. 
	 * @author Iván Castilla Rodríguez
	 */
	enum ModelType {NORESOURCES, RESOURCES, CONFLICT, TOTALCONFLICT, PARALLEL}
	/**
	 * Defines the temporal behaviour of the elements when requesting the activities:
	 * - SAMETIME: All the elements request the activities at the same time
	 * - CONSECUTIVE: Only one element requests an activity at a time
	 * - MIXED: Something in the middle of the former ones.
	 * @author Iván Castilla Rodríguez
	 */
	enum OverlappingType {SAMETIME, CONSECUTIVE, MIXED};
	final static private String head = "EXP\t[ID]\tSimulation Type\tModel Type\tOverlapping Type\tThreads\tIterations\tWork load\tMix\tActivities\tElements";
	final static private TimeUnit unit = TimeUnit.MINUTE;
	final int id;
	final SimulationFactory.SimulationType simType;
	final ModelType modType;
	final OverlappingType ovType;
	final int nThread;
	final int nIter;
	final int nElem;
	final int nAct;
	final int mixFactor;
	final long workLoad;

	
	/**
	 * @param id
	 * @param simType
	 * @param modType
	 * @param ovType
	 * @param nThread
	 * @param nIter
	 * @param nElem
	 * @param nAct
	 * @param mixFactor
	 * @param workLoad
	 */
	public BenchmarkModel(int id, SimulationType simType, ModelType modType,
			OverlappingType ovType, int nThread, int nIter, int nElem,
			int nAct, int mixFactor, long workLoad) {
		this.id = id;
		this.simType = simType;
		this.modType = modType;
		this.ovType = ovType;
		this.nThread = nThread;
		this.nIter = nIter;
		this.nElem = nElem;
		this.nAct = nAct;
		this.mixFactor = mixFactor;
		this.workLoad = workLoad;
	}

	/**
	 * @param id
	 * @param simType
	 * @param modType
	 * @param ovType
	 * @param nThread
	 * @param nIter
	 * @param nElem
	 * @param nAct
	 * @param mixFactor
	 * @param workLoad
	 */
	public BenchmarkModel(int id, SimulationType simType, ModelType modType,
			OverlappingType ovType, int nThread, int nIter, int nElem,
			int nAct, long workLoad) {
		this.id = id;
		this.simType = simType;
		this.modType = modType;
		this.ovType = ovType;
		this.nThread = nThread;
		this.nIter = nIter;
		this.nElem = nElem;
		this.nAct = nAct;
		this.mixFactor = 2;
		this.workLoad = workLoad;
	}

 	/**
	 * @param id
	 * @param simType
	 * @param modType
	 * @param ovType
	 * @param nThread
	 * @param nIter
	 * @param nElem
	 * @param nAct
	 */
	public BenchmarkModel(int id, SimulationType simType, ModelType modType,
			OverlappingType ovType, int nThread, int nIter, int nElem,
			int nAct) {
		this.id = id;
		this.simType = simType;
		this.modType = modType;
		this.ovType = ovType;
		this.nThread = nThread;
		this.nIter = nIter;
		this.nElem = nElem;
		this.nAct = nAct;
		this.mixFactor = 2;
		this.workLoad = 0;
	}
	
	@Override
	public String toString() {
		return "EXP\t[" + id + "]\t" + simType + "\t" + modType + "\t" + ovType + "\t" + nThread + "\t" + nIter + "\t" + workLoad + "\t" + mixFactor + "\t" + nAct + "\t" + nElem;
	}

	/**
	 * Returns the header of a table containing results from these experiments.
	 * @return The list of fields as shown when using toString
	 */
	public static String getHeader() {
		return head;
	}
	
	public Simulation getTestModel() {
		Simulation sim = null;
		switch(modType) {
			case NORESOURCES: sim = getTestOcurrenceSimN(); break;
			case RESOURCES: sim = getTestOcurrenceSimResources(); break;
			case CONFLICT: sim = getTestOcurrenceSimNResMix(); break;
			case TOTALCONFLICT: sim = getTestOcurrenceSimNRes(); break;
			case PARALLEL: sim = getTestParallelSimResources(); break;
		}
		sim.setNThreads(nThread);
		return sim;
	}
	
	private Simulation getTestParallelSimResources() {
		long actTime = nElem;
		TimeStamp endTs = new TimeStamp(TimeUnit.MINUTE, actTime * nAct * (nIter + 1) + 1);	
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem * nAct];
		
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		SingleFlow[] smfs = new SingleFlow[nAct];
		
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = new SimulationUserCode();
		if (workLoad > 0) {
			factory.getSimulation().putVar("AA", 0);
			
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}
		
		for (int i = 0; i < acts.length; i++) {
			rts[i] = factory.getResourceTypeInstance(i, "RT" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			if (workLoad > 0)
				smfs[i] = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", code, acts[i]);
			else
				smfs[i] = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
		}
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		InterleavedRoutingFlow iFlow = (InterleavedRoutingFlow)factory.getFlowInstance(acts.length + 1, "InterleavedRoutingFlow");
		for (SingleFlow sf : smfs)
			iFlow.addBranch(sf);
		ForLoopFlow rootFlow = (ForLoopFlow)factory.getFlowInstance(acts.length + 2, "ForLoopFlow", iFlow, TimeFunctionFactory.getInstance("ConstantVariate", nIter));

		SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);

		for (int i = 0; i < nElem * nAct; i++) {
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
			res[i].addTimeTableEntry(allCycle, endTs, rts[i % nAct]);
		}
		
		int elemCounter = 0;
		ElementCreator creator = null;
		switch(ovType) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem), et, rootFlow);
				factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0));
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, rootFlow);
				factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, rootFlow);
				factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				break;
		}
		return factory.getSimulation();

	}

	private Simulation getTestOcurrenceSimResources() {
		long actTime = nElem;
		TimeStamp endTs = new TimeStamp(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);	
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];
		
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = new SimulationUserCode();
		if (workLoad > 0) {
			factory.getSimulation().putVar("AA", 0);
			
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}
		
		for (int i = 0; i < acts.length; i++) {
			rts[i] = factory.getResourceTypeInstance(i, "RT" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = null;
			if (workLoad > 0)
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);

		for (int i = 0; i < nElem; i++) {
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
			res[i].addTimeTableEntry(allCycle, endTs, rts[i % nAct]);
		}
		
		int elemCounter = 0;
		switch(ovType) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0));
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
		}
		return factory.getSimulation();

	}

	private Simulation getTestOcurrenceSimN() {
		long actTime = nElem;
		TimeStamp endTs = new TimeStamp(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = new SimulationUserCode();
		if (workLoad > 0) {
			factory.getSimulation().putVar("AA", 0);
			
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}
		
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = null;
			if (workLoad > 0)
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);
		
		WorkGroup wg = factory.getWorkGroupInstance(0, new ResourceType[0], new int[0]);
		int elemCounter = 0;
		switch(ovType) {
			case SAMETIME:
				for (TimeDrivenActivity act : acts) 
					act.addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0));
				}
				break;
			case CONSECUTIVE:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
			case MIXED:
				for (TimeDrivenActivity act : acts)
					act.addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wg);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
		}
		return factory.getSimulation();

	}
	
	private Simulation getTestOcurrenceSimNRes() {
		long actTime = nElem;
		TimeStamp endTs = new TimeStamp(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = new SimulationUserCode();
		if (workLoad > 0) {
			factory.getSimulation().putVar("AA", 0);
			
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}

		SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem; i++)
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = null;
			if (workLoad > 0)
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
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
		switch(ovType) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, allCycle);					
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
					
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
					
				}
				break;
		}
		return factory.getSimulation();
	}
	
	private Simulation getTestOcurrenceSimNResMix() {
		long actTime = nElem;
		TimeStamp endTs = new TimeStamp(TimeUnit.MINUTE, actTime * (nIter + 1) + 1);
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = new SimulationUserCode();
		if (workLoad > 0) {
			factory.getSimulation().putVar("AA", 0);
			
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}
		
		SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);
		SimulationPeriodicCycle allCycle = new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
		for (int i = 0; i < nElem / 2; i++)
			res[i] = factory.getResourceInstance(i, "RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance(i, "A_TEST" + i);
			SingleFlow sf = null;
			if (workLoad > 0)
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance(i, "SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance(i + acts.length, "ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = factory.getResourceTypeInstance(i, "RT_TEST" + i);
			wgs[i] = factory.getWorkGroupInstance(i, new ResourceType[] {rts[i]}, new int[] {1});
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem / 2; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
		ElementType et = factory.getElementTypeInstance(0, "E_TEST");
		int elemCounter = 0;
		switch(ovType) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, allCycle);					
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
					
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", actTime / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(elemCounter, TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(elemCounter++, creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
					
				}
				break;
		}

		return factory.getSimulation();		
	}

}
