/**
 * 
 */
package es.ull.iis.simulation.examples;

import java.util.ArrayList;
import java.util.Arrays;

import es.ull.iis.simulation.core.ElementCreator;
import es.ull.iis.simulation.core.ElementType;
import es.ull.iis.simulation.core.Resource;
import es.ull.iis.simulation.core.ResourceType;
import es.ull.iis.simulation.core.Simulation;
import es.ull.iis.simulation.core.SimulationPeriodicCycle;
import es.ull.iis.simulation.core.SimulationTimeFunction;
import es.ull.iis.simulation.core.TimeDrivenActivity;
import es.ull.iis.simulation.core.TimeStamp;
import es.ull.iis.simulation.core.TimeUnit;
import es.ull.iis.simulation.core.WorkGroup;
import es.ull.iis.simulation.core.flow.ForLoopFlow;
import es.ull.iis.simulation.core.flow.InterleavedRoutingFlow;
import es.ull.iis.simulation.core.flow.SingleFlow;
import es.ull.iis.simulation.factory.SimulationFactory;
import es.ull.iis.simulation.factory.SimulationObjectFactory;
import es.ull.iis.simulation.factory.SimulationUserCode;
import es.ull.iis.simulation.factory.UserMethod;
import es.ull.iis.simulation.factory.SimulationFactory.SimulationType;
import es.ull.isaatc.function.TimeFunctionFactory;

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
	enum ModelType {NORESOURCES, RESOURCES, CONFLICT, MIXCONFLICT, TOTALCONFLICT, PARALLEL}
	/**
	 * Defines the temporal behaviour of the elements when requesting the activities:
	 * - SAMETIME: All the elements request the activities at the same time
	 * - CONSECUTIVE: Only one element requests an activity at a time
	 * - MIXED: Something in the middle of the former ones.
	 * @author Iván Castilla Rodríguez
	 */
	enum OverlappingType {SAMETIME, CONSECUTIVE, MIXED};
	final private String head;
	final static private TimeUnit unit = TimeUnit.MINUTE;
	final static private SimulationTimeFunction oneFunction = new SimulationTimeFunction(unit, "ConstantVariate", 1);
	final int id;
	final SimulationFactory.SimulationType simType;
	final ModelType modType;
	final OverlappingType ovType;
	final int nThread;
	final int nIter;
	/** Amount of elements. By default it's taken as the activity duration. */
	final int nElem;
	final int nAct;
	final int mixFactor;
	final long workLoad;
	final TimeStamp endTs;
	final SimulationPeriodicCycle allCycle;
	int rtXact = 4;
	int rtXres = 1;
	double resAvailabilityFactor = 1;

	
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
		switch(modType) {
			case PARALLEL: this.endTs = new TimeStamp(TimeUnit.MINUTE, nElem * nAct * (nIter + 1) + 1); break;
			case NORESOURCES:
			case RESOURCES: 
			case MIXCONFLICT: 
			case TOTALCONFLICT:
			case CONFLICT:
			default: this.endTs = new TimeStamp(TimeUnit.MINUTE, nElem * (nIter + 1) + 1); break;
		}
		String auxHead = "Simulation Type\tModel Type\tOverlapping Type\tThreads\tIterations";
		if (modType == ModelType.CONFLICT)
			auxHead += "\tRTxACT\tRTxRES";
		if (workLoad > 0)
			auxHead += "\tWork load";
		if (ovType == OverlappingType.MIXED)
			auxHead += "\tMix";
		auxHead += "\tActivities\tElements";
		head = auxHead;
		this.allCycle = new SimulationPeriodicCycle(unit, TimeStamp.getZero(), new SimulationTimeFunction(unit, "ConstantVariate", endTs.getValue()), 0);
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
	 * @param workLoad
	 */
	public BenchmarkModel(int id, SimulationType simType, ModelType modType,
			OverlappingType ovType, int nThread, int nIter, int nElem,
			int nAct, long workLoad) {
		this(id, simType, modType, ovType, nThread, nIter, nElem, nAct, 2, workLoad);
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
		this(id, simType, modType, ovType, nThread, nIter, nElem, nAct, 2, 0);
	}
	
	@Override
	public String toString() {
		String text = "";
		if (modType == ModelType.CONFLICT)
			text += simType + "\t" + modType + "\t" + ovType + "\t" + nThread + "\t" + nIter + "\t" + rtXact + "\t" + rtXres;
		else
			text += simType + "\t" + modType + "\t" + ovType + "\t" + nThread + "\t" + nIter;
		if (workLoad > 0)
			text += "\t" + workLoad;
		if (ovType == OverlappingType.MIXED)
			text += "\t" + mixFactor;
		return text + "\t" + nAct + "\t" + nElem;
	}

	/**
	 * Returns the header of a table containing results from these experiments.
	 * @return The list of fields as shown when using toString
	 */
	public String getHeader() {
		return head;
	}
	
	public Simulation getTestModel() {
		Simulation sim = null;
		switch(modType) {
			case NORESOURCES: sim = getTestSimpleNoResources(); break;
			case RESOURCES: sim = getTestSimpleResources(); break;
			case CONFLICT: sim = getTestConflict(); break;
			case MIXCONFLICT: sim = getTestMixConflict(); break;
			case TOTALCONFLICT: sim = getTestTotalConflict(); break;
			case PARALLEL: sim = getTestParallelSimResources(); break;
		}
		sim.setNThreads(nThread);
		return sim;
	}
	
	private void stdBuildElementGenerators(SimulationObjectFactory factory, ForLoopFlow[] smfs, TimeDrivenActivity[] acts, WorkGroup[] wgs) {
		ElementType et = factory.getElementTypeInstance("E_TEST");
		switch(ovType) {
			case SAMETIME:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", nElem), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", nElem / smfs.length), et, smf);
					factory.getTimeDrivenGeneratorInstance(creator, allCycle);
				}
				break;
			case CONSECUTIVE:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", nElem), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
			case MIXED:
				for (int i = 0; i < acts.length; i++)
					acts[i].addWorkGroup(new SimulationTimeFunction(unit, "ConstantVariate", nElem / mixFactor), wgs[i]);
				for (ForLoopFlow smf : smfs) {
					ElementCreator creator = factory.getElementCreatorInstance(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, smf);
					factory.getTimeDrivenGeneratorInstance(creator, new SimulationPeriodicCycle(unit, TimeStamp.getZero(), oneFunction, nElem));
				}
				break;
		}
	}

	private SimulationUserCode addWorkLoad(SimulationObjectFactory factory) {
		SimulationUserCode code = null;
		if (workLoad > 0) {
			code = new SimulationUserCode();
			factory.getSimulation().putVar("AA", 0);
			code.add(UserMethod.BEFORE_REQUEST, "for (int i = 1; i < " + workLoad + "; i++)" +
					"<%SET(S.AA, <%GET(S.AA)%> + Math.log(i))%>;" + 
					"return super.beforeRequest(e);");
		}
		return code;
	}
	
	private Simulation getTestParallelSimResources() {
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem * nAct];
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		SingleFlow[] smfs = new SingleFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
		
		for (int i = 0; i < acts.length; i++) {
			rts[i] = factory.getResourceTypeInstance("RT" + i);
			wgs[i] = factory.getWorkGroupInstance(new ResourceType[] {rts[i]}, new int[] {1});
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			if (code != null)
				smfs[i] = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				smfs[i] = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
		}
		InterleavedRoutingFlow iFlow = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		for (SingleFlow sf : smfs)
			iFlow.addBranch(sf);
		ForLoopFlow rootFlow = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", iFlow, TimeFunctionFactory.getInstance("ConstantVariate", nIter));

		for (int i = 0; i < nElem * nAct; i++) {
			res[i] = factory.getResourceInstance("RES_TEST" + i);
			res[i].addTimeTableEntry(allCycle, endTs, rts[i % nAct]);
		}

		stdBuildElementGenerators(factory, new ForLoopFlow[] {rootFlow}, acts, wgs);
		return factory.getSimulation();

	}

	private Simulation getTestSimpleResources() {
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];
		
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
		
		for (int i = 0; i < acts.length; i++) {
			rts[i] = factory.getResourceTypeInstance("RT" + i);
			wgs[i] = factory.getWorkGroupInstance(new ResourceType[] {rts[i]}, new int[] {1});
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			SingleFlow sf = null;
			if (code != null)
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}

		for (int i = 0; i < nElem; i++) {
			res[i] = factory.getResourceInstance("RES_TEST" + i);
			res[i].addTimeTableEntry(allCycle, endTs, rts[i % nAct]);
		}
		
		stdBuildElementGenerators(factory, smfs, acts, wgs);
		return factory.getSimulation();

	}

	private Simulation getTestSimpleNoResources() {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
		
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			SingleFlow sf = null;
			if (code != null)
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}
		
		WorkGroup wg = factory.getWorkGroupInstance(new ResourceType[0], new int[0]);
		WorkGroup []wgs = new WorkGroup[nAct];
		// Assigns the same WG to each activity
		Arrays.fill(wgs, wg);
		
		stdBuildElementGenerators(factory, smfs, acts, wgs);
		return factory.getSimulation();

	}
	
	private Simulation getTestTotalConflict() {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
		
		for (int i = 0; i < nElem; i++)
			res[i] = factory.getResourceInstance("RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			SingleFlow sf = null;
			if (code != null)
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = factory.getResourceTypeInstance("RT_TEST" + i);
			wgs[i] = factory.getWorkGroupInstance(new ResourceType[] {rts[i]}, new int[] {1});
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
//		for (int j = 0; j < nElem; j++)
//			res[j].addTimeTableEntry(allCycle, endTs, rts[j % acts.length]);
		
		stdBuildElementGenerators(factory, smfs, acts, wgs);
		return factory.getSimulation();
	}
	
	private Simulation getTestMixConflict() {
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		ResourceType[] rts = new ResourceType[nAct];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[nElem];

		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
		
		for (int i = 0; i < nElem / 2; i++)
			res[i] = factory.getResourceInstance("RES_TEST" + i);
		for (int i = 0; i < acts.length; i++) {
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			SingleFlow sf = null;
			if (code != null)
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
			rts[i] = factory.getResourceTypeInstance("RT_TEST" + i);
			wgs[i] = factory.getWorkGroupInstance(new ResourceType[] {rts[i]}, new int[] {1});
		}
		ArrayList<ResourceType> list = new ArrayList<ResourceType>(Arrays.asList(rts));
		for (int j = 0; j < nElem / 2; j++)
			res[j].addTimeTableEntry(allCycle, endTs, list);
		
		stdBuildElementGenerators(factory, smfs, acts, wgs);

		return factory.getSimulation();		
	}

	/**
	 * @param rtXact the rtXact to set
	 */
	public void setRtXact(int rtXact) {
		this.rtXact = rtXact;
	}

	/**
	 * @param rtXres the rtXres to set
	 */
	public void setRtXres(int rtXres) {
		this.rtXres = rtXres;
	}

	/**
	 * @param resAvailabilityFactor the resAvailabilityFactor to set
	 */
	public void setResAvailabilityFactor(double resAvailabilityFactor) {
		this.resAvailabilityFactor = resAvailabilityFactor;
	}

	private Simulation getTestConflict() {
		
		ResourceType[] rts = new ResourceType[nAct * rtXact];
		WorkGroup[] wgs = new WorkGroup[nAct];
		Resource[] res = new Resource[(int) (nElem * rtXact * resAvailabilityFactor)];
		
		TimeDrivenActivity[] acts = new TimeDrivenActivity[nAct];
		ForLoopFlow[] smfs = new ForLoopFlow[nAct];
		
		SimulationObjectFactory factory = SimulationFactory.getInstance(simType, id, "TEST", unit, TimeStamp.getZero(), endTs);
		
		SimulationUserCode code = addWorkLoad(factory);
	
		for (int i = 0; i < rts.length; i++)
			rts[i] = factory.getResourceTypeInstance("RT" + i);
		
		for (int i = 0; i < acts.length; i++) {
			ResourceType[] rtGroup = new ResourceType[rtXact];
			int[] needGroup = new int[rtXact];
			for (int j = 0; j < rtXact; j++) {
				rtGroup[j] = rts[i * rtXact + j];
				needGroup[j] = 1;
			}
			wgs[i] = factory.getWorkGroupInstance(rtGroup, needGroup);
			acts[i] = factory.getTimeDrivenActivityInstance("A_TEST" + i);
			SingleFlow sf = null;
			if (code != null)
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", code, acts[i]);
			else
				sf = (SingleFlow)factory.getFlowInstance("SingleFlow", acts[i]);
			smfs[i] = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", sf, TimeFunctionFactory.getInstance("ConstantVariate", nIter));
		}

		for (int i = 0; i < res.length; i++) {
			res[i] = factory.getResourceInstance("RES_TEST" + i);
			ArrayList<ResourceType> roles = new ArrayList<ResourceType>();
			for (int j = 0; j < rtXres; j++)
				roles.add(rts[(i + (int) (j * (rts.length / rtXres) * resAvailabilityFactor)) % rts.length]);
			res[i].addTimeTableEntry(allCycle, endTs, roles);
		}
		
		stdBuildElementGenerators(factory, smfs, acts, wgs);
		return factory.getSimulation();

	}
}
