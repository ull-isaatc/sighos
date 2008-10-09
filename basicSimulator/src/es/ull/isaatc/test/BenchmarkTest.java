/**
 * 
 */
package es.ull.isaatc.test;

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Experiment;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
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

class TestOcurrenceSim extends StandAloneLPSimulation {
	final static double MIX_FACTOR = 10.0;
	public enum Type {SAMETIME, CONSECUTIVE, MIXED};
	Type type;
	int nIter;
	double actTime;
	int nElem;

	public TestOcurrenceSim(Type type, int id, int nElem, double actTime, int nIter, SimulationTime endTs) {
		super(id, "TEST",  SimulationTimeUnit.MINUTE, SimulationTime.getZero(), endTs);
		this.nIter = nIter;
		this.nElem = nElem;
		this.actTime = actTime;
		this.type = type;
	}
	
	@Override
	protected void createModel() {
		Activity a = new Activity(0, this, "A_TEST");
		SingleMetaFlow sf = new SingleMetaFlow(0, RandomVariateFactory.getInstance("ConstantVariate", nIter), a);
		ElementType et = new ElementType(0, this, "E_TEST");
		switch(type) {
			case SAMETIME: 
				a.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				SimulationCycle c1 = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0);
				new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", nElem), et, sf), c1);
				break;
			case CONSECUTIVE:
				a.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime));
				SimulationCycle subc2 = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", 1), nElem);
				SimulationCycle c2 = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0, subc2);
				new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, sf), c2);
				break;
			case MIXED:
				a.addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", actTime / MIX_FACTOR));
				SimulationCycle subc3 = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", 1), nElem);
				SimulationCycle c3 = new SimulationPeriodicCycle(this, SimulationTime.getZero(), new SimulationTimeFunction(this, "ConstantVariate", endTs.getValue()), 0, subc3);
				new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", 1), et, sf), c3);
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

	public BenchmarkListener() {
		super();
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
		System.out.println(this + "Elem Events:\t" + elemEvents + "\tRes Events:\t" + resEvents + "\nMax. concurrent Events:\t" + maxConcurrentEvents);
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class BenchmarkTest {
	final static int NELEM = 2000;
	final static double ACTTIME = NELEM;
	final static int NITER = 10;
	final static SimulationTime ENDTS = new SimulationTime(SimulationTimeUnit.MINUTE, ACTTIME * (NITER + 1) + 1); 
	final static int NEXP = 10;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Experiment("Same Time", NEXP) {
			@Override
			public Simulation getSimulation(int ind) {
				return new TestOcurrenceSim(TestOcurrenceSim.Type.CONSECUTIVE, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim(TestOcurrenceSim.Type.SAMETIME, ind, NELEM, ACTTIME, NITER, ENDTS);
//				return new TestOcurrenceSim(TestOcurrenceSim.Type.MIXED, ind, NELEM, ACTTIME, NITER, ENDTS);
			}

			@Override
			public void start() {
				for (int i = 0; i < nExperiments; i++) {
					Simulation sim = getSimulation(i);
					ListenerController cont = new ListenerController();
					sim.setListenerController(cont);
//					cont.addListener(new StdInfoListener());
//					cont.addListener(new BenchmarkListener());
					cont.addListener(new SimulationTimeListener() {
						@Override
						public void infoEmited(SimulationEndInfo info) {
							super.infoEmited(info);
							System.out.println("" + (endT - iniT));
						}
						
					});
					sim.run();
				}
				end();		
			}			
		}.start();
	}
}
