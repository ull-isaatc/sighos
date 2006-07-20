/**
 * 
 */

import es.ull.isaatc.random.Fixed;
import es.ull.isaatc.simulation.*;
import es.ull.isaatc.simulation.info.StatisticListener;
import es.ull.isaatc.simulation.info.StdInfoListener;
import es.ull.isaatc.util.Cycle;
import es.ull.isaatc.util.Output;

class SimSchedula extends Simulation {
	final static int NRES_ACT[] = {10, 12, 10, 5, 1, 5, 2, 5, 12, 5, 10};
	final static String RESNAME[] = {"Acceptance", "Credit check", "Recomputation",
		"Update database", "Update dossier", "Compute balance", "Produce report",
		"Operators", "Accountants", "Computers", "Calculators" 
	};
//	final static int NELEM[] = {200, 600, 120};
	final static int NELEM[] = {10, 10, 10};
//	final static int NELEM[] = {1, 0, 0};

	SimSchedula(double startTs, double endTs, Output out) {
		super("Schedula example", startTs, endTs, out);
	}
	
	SimSchedula(double startTs, double endTs) {
		super("Schedula example", startTs, endTs);
	}
	
	@Override
	protected void createModel() {
		// Workstations and shared resources
		for (int i = 0; i < RESNAME.length; i++)
			new ResourceType(i, this, RESNAME[i]);
		
		WorkGroup wgs[] = new WorkGroup[14];
		wgs[0] = new Activity(0, this, "AcceptanceN").getNewWorkGroup(0, new Fixed(8.33));
		wgs[7] = new Activity(7, this, "AcceptanceRE").getNewWorkGroup(0, new Fixed(1));
		wgs[1] = new Activity(1, this, "Credit checkN").getNewWorkGroup(0, new Fixed(8.13));
		wgs[8] = new Activity(8, this, "Credit checkRE").getNewWorkGroup(0, new Fixed(1));
		wgs[2] = new Activity(2, this, "Recomputation").getNewWorkGroup(0, new Fixed(2));
		wgs[3] = new Activity(3, this, "Update databaseN").getNewWorkGroup(0, new Fixed(5.56));
		wgs[9] = new Activity(9, this, "Update databaseR").getNewWorkGroup(0, new Fixed(4));
		wgs[10] = new Activity(10, this, "Update databaseE").getNewWorkGroup(0, new Fixed(10.53));
		wgs[4] = new Activity(4, this, "Update dossierN").getNewWorkGroup(0, new Fixed(6.67));
		wgs[11] = new Activity(11, this, "Update dossierRE").getNewWorkGroup(0, new Fixed(1));
		wgs[5] = new Activity(5, this, "Compute balanceN").getNewWorkGroup(0, new Fixed(2.63));
		wgs[12] = new Activity(12, this, "Compute balanceRE").getNewWorkGroup(0, new Fixed(1));
		wgs[6] = new Activity(6, this, "Produce reportNR").getNewWorkGroup(0, new Fixed(1));
		wgs[13] = new Activity(13, this, "Produce reportE").getNewWorkGroup(0, new Fixed(2.63));
		
		wgs[0].add(getResourceType(0), 1);
		wgs[0].add(getResourceType(8), 1);
		wgs[0].add(getResourceType(10), 1);
		wgs[7].add(getResourceType(0), 1);
		wgs[7].add(getResourceType(8), 1);
		wgs[7].add(getResourceType(10), 1);
		wgs[1].add(getResourceType(1), 1);
		wgs[1].add(getResourceType(8), 1);
		wgs[8].add(getResourceType(1), 1);
		wgs[8].add(getResourceType(8), 1);
		wgs[2].add(getResourceType(2), 1);
		wgs[2].add(getResourceType(8), 1);
		wgs[2].add(getResourceType(10), 1);
		wgs[3].add(getResourceType(3), 1);
		wgs[3].add(getResourceType(7), 1);
		wgs[3].add(getResourceType(9), 1);
		wgs[9].add(getResourceType(3), 1);
		wgs[9].add(getResourceType(7), 1);
		wgs[9].add(getResourceType(9), 1);
		wgs[10].add(getResourceType(3), 1);
		wgs[10].add(getResourceType(7), 1);
		wgs[10].add(getResourceType(9), 1);
		wgs[4].add(getResourceType(4), 1);
		wgs[4].add(getResourceType(8), 2);
		wgs[11].add(getResourceType(4), 1);
		wgs[11].add(getResourceType(8), 2);
		wgs[5].add(getResourceType(5), 1);
		wgs[5].add(getResourceType(8), 1);
		wgs[5].add(getResourceType(9), 1);
		wgs[12].add(getResourceType(5), 1);
		wgs[12].add(getResourceType(8), 1);
		wgs[12].add(getResourceType(9), 1);
		wgs[6].add(getResourceType(6), 1);
		wgs[6].add(getResourceType(7), 1);
		wgs[6].add(getResourceType(8), 1);
		wgs[6].add(getResourceType(9), 1);
		wgs[13].add(getResourceType(6), 1);
		wgs[13].add(getResourceType(7), 1);
		wgs[13].add(getResourceType(8), 1);
		wgs[13].add(getResourceType(9), 1);
		
		// Element types creation
		for (int i = 0; i < NELEM.length; i++) {
			new ElementType(i, this, "NELEM : " + NELEM[i]);
		}

		// Resources
		Cycle c1 = new Cycle(510.0, new Fixed(1440.0), 0);
		int count = 0;
		for (int i = 0; i < NRES_ACT.length; i++)
			for (int j = 0; j < NRES_ACT[i]; j++) 
				new Resource(count++, this, RESNAME[i] + j).addTimeTableEntry(c1, 630.0, getResourceType(i));

		// Meta flows
		int countMeta = 0;
		TypeMetaFlow type = new TypeMetaFlow(countMeta++, new Fixed(1));
		SequenceMetaFlow sec[] = new SequenceMetaFlow[NELEM.length];
		for (int i = 0; i < NELEM.length; i++) {
			// hay dos constructores para el typebranch
			TypeBranchMetaFlow tb = new TypeBranchMetaFlow(countMeta++, type, getElementType(i));
			sec[i] = new SequenceMetaFlow(countMeta++, tb,  new Fixed(1));
		}
		// Type A
		new SingleMetaFlow(countMeta++, sec[0], new Fixed(1), getActivity(0));
		new SingleMetaFlow(countMeta++, sec[0], new Fixed(1), getActivity(1));
		new SingleMetaFlow(countMeta++, sec[0], new Fixed(1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[0], new Fixed(1), getActivity(3));
		new SingleMetaFlow(countMeta++, sec[0], new Fixed(1), getActivity(4));
		// Type B
		new SingleMetaFlow(countMeta++, sec[1], new Fixed(1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[1], new Fixed(1), getActivity(3));
		// Type C
		new SingleMetaFlow(countMeta++, sec[2], new Fixed(1), getActivity(5));
		new SingleMetaFlow(countMeta++, sec[2], new Fixed(1), getActivity(2));
		new SingleMetaFlow(countMeta++, sec[2], new Fixed(1), getActivity(3));
		new SingleMetaFlow(countMeta++, sec[2], new Fixed(1), getActivity(6));
		
		Cycle c[] = new Cycle[3];
		c[0] = new Cycle(750.0, new Fixed(1440.0), 0);
		c[1] = new Cycle(510.0, new Fixed(1440.0), 0);
		c[2] = new Cycle(510.0, new Fixed(1440.0), 0);
		for (int i = 0; i < NELEM.length; i++)
			new ElementGenerator(this, new Fixed(NELEM[i]), c[i].iterator(startTs, endTs), getElementType(i), sec[i]);
	}
	
}

class ExpSchedula extends Experiment {
    static final int NDAYS = 1;
    static final int NTESTS = 1;

	ExpSchedula() {
		super("Schedula Example", NTESTS);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		SimSchedula sim = new SimSchedula(0.0, 24 * 60.0 * NDAYS);
		sim.addListener(new StdInfoListener());
		sim.addListener(new StatisticListener(1440.0));
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
