/**
 * 
 */

import simkit.random.RandomVariateFactory;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.ElementCreator;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.SequenceMetaFlow;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationCycle;
import es.ull.isaatc.simulation.SimulationPeriodicCycle;
import es.ull.isaatc.simulation.SimulationTime;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.SingleMetaFlow;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.TimeDrivenGenerator;
import es.ull.isaatc.simulation.TypeBranchMetaFlow;
import es.ull.isaatc.simulation.TypeMetaFlow;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StatisticListener;
import es.ull.isaatc.simulation.listener.StdInfoListener;

class SimSchedula extends StandAloneLPSimulation {
	final static int NRES_ACT[] = {10, 12, 10, 5, 1, 5, 2, 5, 12, 5, 10};
	final static String RESNAME[] = {"Acceptance", "Credit check", "Recomputation",
		"Update database", "Update dossier", "Compute balance", "Produce report",
		"Operators", "Accountants", "Computers", "Calculators" 
	};
//	final static int NELEM[] = {200, 600, 120};
	final static int NELEM[] = {10, 10, 10};
//	final static int NELEM[] = {1, 0, 0};

	public SimSchedula(int id, SimulationTime startTs, SimulationTime endTs) {
		super(id, "Schedula example", SimulationTimeUnit.MINUTE, startTs, endTs);
	}
	
	@Override
	protected void createModel() {
		// Workstations and shared resources
		for (int i = 0; i < RESNAME.length; i++)
			new ResourceType(i, this, RESNAME[i]);
		
		WorkGroup wgs[] = new WorkGroup[7];
		wgs[0] = new WorkGroup(0, this, "");
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(8), 1);
		wgs[0].add(getResourceType(10), 1);
		new Activity(0, this, "AcceptanceN").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 8.33), wgs[0]);
		new Activity(7, this, "AcceptanceRE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[0]);

		wgs[1] = new WorkGroup(1, this, "");
		wgs[1].add(getResourceType(1), 1);
		wgs[1].add(getResourceType(8), 1);
		new Activity(1, this, "Credit checkN").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 8.13), wgs[1]);
		new Activity(8, this, "Credit checkRE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[1]);

		wgs[2] = new WorkGroup(2, this, "");
		wgs[2].add(getResourceType(2), 1);
		wgs[2].add(getResourceType(8), 1);
		wgs[2].add(getResourceType(10), 1);
		new Activity(2, this, "Recomputation").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 2), wgs[2]);

		wgs[3] = new WorkGroup(3, this, "");
		wgs[3].add(getResourceType(3), 1);
		wgs[3].add(getResourceType(7), 1);
		wgs[3].add(getResourceType(9), 1);
		new Activity(3, this, "Update databaseN").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 5.56), wgs[3]);
		new Activity(9, this, "Update databaseR").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 4), wgs[3]);
		new Activity(10, this, "Update databaseE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 10.53), wgs[3]);

		wgs[4] = new WorkGroup(4, this, "");
		wgs[4].add(getResourceType(4), 1);
		wgs[4].add(getResourceType(8), 2);
		new Activity(4, this, "Update dossierN").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 6.67), wgs[4]);
		new Activity(11, this, "Update dossierRE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[4]);
		
		wgs[5] = new WorkGroup(5, this, "");
		wgs[5].add(getResourceType(5), 1);
		wgs[5].add(getResourceType(8), 1);
		wgs[5].add(getResourceType(9), 1);
		new Activity(5, this, "Compute balanceN").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 2.63), wgs[5]);
		new Activity(12, this, "Compute balanceRE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[5]);

		wgs[6] = new WorkGroup(6, this, "");
		wgs[6].add(getResourceType(6), 1);
		wgs[6].add(getResourceType(7), 1);
		wgs[6].add(getResourceType(8), 1);
		wgs[6].add(getResourceType(9), 1);
		new Activity(6, this, "Produce reportNR").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 1), wgs[6]);
		new Activity(13, this, "Produce reportE").addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 2.63), wgs[6]);
		
		// Element types creation
		for (int i = 0; i < NELEM.length; i++) {
			new ElementType(i, this, "NELEM : " + NELEM[i]);
		}

		// Resources
		SimulationCycle c1 = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 510.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		int count = 0;
		for (int i = 0; i < NRES_ACT.length; i++)
			for (int j = 0; j < NRES_ACT[i]; j++) 
				new Resource(count++, this, RESNAME[i] + j).addTimeTableEntry(c1, new SimulationTime(SimulationTimeUnit.MINUTE, 630.0), getResourceType(i));

		// Meta flows
		int countMeta = 0;
		TypeMetaFlow type = new TypeMetaFlow(countMeta++, RandomVariateFactory.getInstance("ConstantVariate", 1));
		SequenceMetaFlow sec[] = new SequenceMetaFlow[NELEM.length];
		for (int i = 0; i < NELEM.length; i++) {
			// hay dos constructores para el typebranch
			TypeBranchMetaFlow tb = new TypeBranchMetaFlow(countMeta++, type, getElementType(i));
			sec[i] = new SequenceMetaFlow(countMeta++, tb,  RandomVariateFactory.getInstance("ConstantVariate", 1));
		}
		// Type A
		new SingleMetaFlow(countMeta++, sec[0], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(0));
		new SingleMetaFlow(countMeta++, sec[0], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(1));
		new SingleMetaFlow(countMeta++, sec[0], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[0], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(3));
		new SingleMetaFlow(countMeta++, sec[0], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(4));
		// Type B
		new SingleMetaFlow(countMeta++, sec[1], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[1], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(3));
		// Type C
		new SingleMetaFlow(countMeta++, sec[2], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(5));
		new SingleMetaFlow(countMeta++, sec[2], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[2], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(3));
		new SingleMetaFlow(countMeta++, sec[2], RandomVariateFactory.getInstance("ConstantVariate", 1), getActivity(6));
		
		SimulationCycle c[] = new SimulationCycle[3];
		c[0] = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 750.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		c[1] = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 510.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		c[2] = new SimulationPeriodicCycle(this, new SimulationTime(SimulationTimeUnit.MINUTE, 510.0), new SimulationTimeFunction(this, "ConstantVariate", 1440.0), 0);
		for (int i = 0; i < NELEM.length; i++)
			new TimeDrivenGenerator(this, new ElementCreator(TimeFunctionFactory.getInstance("ConstantVariate", NELEM[i]), getElementType(i), sec[i]), c[i]);
	}
	
}

class ExpSchedula extends PooledExperiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;

	ExpSchedula() {
		super("Schedula Example", NTESTS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimSchedula sim = new SimSchedula(ind, new SimulationTime(SimulationTimeUnit.MINUTE, 0.0), new SimulationTime(SimulationTimeUnit.DAY, NDAYS));
		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
		cont.addListener(new StdInfoListener());
		cont.addListener(new StatisticListener(1440.0));
		return sim;
	}
	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SchedulaExample {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpSchedula().start();
	}

}
