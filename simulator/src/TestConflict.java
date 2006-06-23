import java.util.ArrayList;

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.results.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;
import es.ull.isaatc.util.Output;

/**
 * Define un modelo:
 * - A0 {RT0:1, RT1:1}; A1 {RT3:1, RT2:1}
 * - R0 {RT0, RT2}; R1 {RT3, RT1}
 * - E0 {A0}; E1 {A1} 
 */
class SimConflict1 extends Simulation {
	final static int NRT = 4;
	final static int NACTS = 2;
	final static int NRES = 2;
	final static int NELEM = 1;
	
	SimConflict1(double startTs, double endTs, Output out) {
		super("Testing conflicts", startTs, endTs, out);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		for (int i = 0; i < NACTS; i++)
			wgs[i] = new Activity(i, this, "ACT" + i).getNewWorkGroup(0, new Fixed(40));
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		
	}

	@Override
	protected ArrayList<Generator> createGenerators() {
		ArrayList<Generator> list = new ArrayList<Generator>();
		Cycle c = new Cycle(1.0, new Fixed(1440.0), 480.0);
		list.add(new ElementGenerator(this, new Fixed(NELEM), c.iterator(startTs, endTs), new SingleMetaFlow(0, new Fixed(1), getActivity(0))));
		list.add(new ElementGenerator(this, new Fixed(NELEM), c.iterator(startTs, endTs), new SingleMetaFlow(1, new Fixed(1), getActivity(1))));
		return list;
	}

	@Override
	protected ArrayList<Resource> createResources() {
		ArrayList<Resource> list = new ArrayList<Resource>();
		Cycle c = new Cycle(0.0, new Fixed(1440.0), endTs);
		for (int i = 0; i < NRES; i++)
			list.add(new Resource(i, this, "Res" + i));
		list.get(0).addTimeTableEntry(c, 480.0, getResourceType(0));
		list.get(0).addTimeTableEntry(c, 480.0, getResourceType(2));
		list.get(1).addTimeTableEntry(c, 480.0, getResourceType(3));
		list.get(1).addTimeTableEntry(c, 480.0, getResourceType(1));
		return list;
	}
}

/**
 * Define un modelo:
 * - A0 {RT0:1, RT1:1, RT4:1}; A1 {RT3:1, RT2:1}; A2 {RT5:1}
 * - R0 {RT0, RT2}; R1 {RT3, RT1}; R2 {RT5, RT4}
 * - E0 {A0}; E1 {A1}; E2 {A2} 
 */
class SimConflict2 extends Simulation {
	final static int NRT = 6;
	final static int NACTS = 3;
	final static int NRES = 3;
	final static int NELEM = 1;
	
	SimConflict2(double startTs, double endTs, Output out) {
		super("Testing conflicts", startTs, endTs, out);
	}

	@Override
	protected void createModel() {
		for (int i = 0; i < NRT; i++)
			new ResourceType(i, this, "RT" + i);
		WorkGroup wgs[] = new WorkGroup[NACTS];
		for (int i = 0; i < NACTS; i++)
			wgs[i] = new Activity(i, this, "ACT" + i).getNewWorkGroup(0, new Fixed(40));
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(1), 1);
		wgs[0].add(getResourceType(4), 1);
		wgs[1].add(getResourceType(3), 1);
		wgs[1].add(getResourceType(2), 1);
		wgs[2].add(getResourceType(5), 1);
	}

	@Override
	protected ArrayList<Generator> createGenerators() {
		ArrayList<Generator> list = new ArrayList<Generator>();
		Cycle c = new Cycle(1.0, new Fixed(1440.0), 480.0);
		list.add(new ElementGenerator(this, new Fixed(NELEM), new CycleIterator(c, startTs, endTs), new SingleMetaFlow(0, new Fixed(1), getActivity(0))));
		list.add(new ElementGenerator(this, new Fixed(NELEM), new CycleIterator(c, startTs, endTs), new SingleMetaFlow(1, new Fixed(1), getActivity(1))));
		list.add(new ElementGenerator(this, new Fixed(NELEM), new CycleIterator(c, startTs, endTs), new SingleMetaFlow(2, new Fixed(1), getActivity(2))));
		return list;
	}

	@Override
	protected ArrayList<Resource> createResources() {
		ArrayList<Resource> list = new ArrayList<Resource>();
		Cycle c = new Cycle(0.0, new Fixed(1440.0), endTs);
		for (int i = 0; i < NRES; i++)
			list.add(new Resource(i, this, "Res" + i));
		list.get(0).addTimeTableEntry(c, 480.0, getResourceType(0));
		list.get(0).addTimeTableEntry(c, 480.0, getResourceType(2));
		list.get(1).addTimeTableEntry(c, 480.0, getResourceType(3));
		list.get(1).addTimeTableEntry(c, 480.0, getResourceType(1));
		list.get(2).addTimeTableEntry(c, 480.0, getResourceType(4));
		list.get(2).addTimeTableEntry(c, 480.0, getResourceType(5));
		return list;
	}
}

class ExpConflict extends Experiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;
    
    ExpConflict() {
    	super("CHECKING CONFLICTS", NTESTS, new NullResultProcessor(), new Output(Output.DebugLevel.DEBUG));
    }
    
	@Override
	public Simulation getSimulation(int ind) {
		return new SimConflict2(0.0, 24 * 60.0 * NDAYS, out);
	}	
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestConflict {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new ExpConflict().start();
	}

}
