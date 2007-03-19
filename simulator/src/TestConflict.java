import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.CycleIterator;
import es.ull.isaatc.util.Output;
import es.ull.isaatc.util.PeriodicCycle;

/**
 * Define un modelo:
 * - A0 {RT0:1, RT1:1}; A1 {RT3:1, RT2:1}
 * - R0 {RT0, RT2}; R1 {RT3, RT1}
 * - E0 {A0}; E1 {A1} 
 */
class SimConflict1 extends Simulation {
	final static int NRT = 4;
	final static int NACTS = 2;
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
		
		Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		r0.addTimeTableEntry(c, 480.0, getResourceType(0));
		r0.addTimeTableEntry(c, 480.0, getResourceType(2));
		r1.addTimeTableEntry(c, 480.0, getResourceType(3));
		r1.addTimeTableEntry(c, 480.0, getResourceType(1));

		Cycle c1 = new PeriodicCycle(1.0, new Fixed(1440.0), 480.0);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(0, this, "ET0"), new SingleMetaFlow(0, new Fixed(1), getActivity(0))), c1.iterator(startTs, endTs));
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(1, this, "ET1"), new SingleMetaFlow(1, new Fixed(1), getActivity(1))), c1.iterator(startTs, endTs));
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

		Cycle c = new PeriodicCycle(0.0, new Fixed(1440.0), endTs);
		Resource r0 = new Resource(0, this, "Res0");
		Resource r1 = new Resource(1, this, "Res1");
		Resource r2 = new Resource(2, this, "Res2");
		r0.addTimeTableEntry(c, 480.0, getResourceType(0));
		r0.addTimeTableEntry(c, 480.0, getResourceType(2));
		r1.addTimeTableEntry(c, 480.0, getResourceType(3));
		r1.addTimeTableEntry(c, 480.0, getResourceType(1));
		r2.addTimeTableEntry(c, 480.0, getResourceType(4));
		r2.addTimeTableEntry(c, 480.0, getResourceType(5));

		Cycle c1 = new PeriodicCycle(1.0, new Fixed(1440.0), 480.0);
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(0, this, "ET0"), new SingleMetaFlow(0, new Fixed(1), getActivity(0))), new CycleIterator(c1, startTs, endTs));
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(1, this, "ET1"), new SingleMetaFlow(1, new Fixed(1), getActivity(1))), new CycleIterator(c1, startTs, endTs));
		new TimeDrivenGenerator(this, new ElementCreator(new Fixed(NELEM), new ElementType(2, this, "ET2"), new SingleMetaFlow(2, new Fixed(1), getActivity(2))), new CycleIterator(c1, startTs, endTs));
	}
}

class ExpConflict extends Experiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;
    
    ExpConflict() {
    	super("CHECKING CONFLICTS", NTESTS);
    }
    
	@Override
	public Simulation getSimulation(int ind) {
		return new SimConflict2(0.0, 24 * 60.0 * NDAYS, new Output(true));
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
