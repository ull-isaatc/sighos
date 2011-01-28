/**
 * 
 */
package es.ull.isaatc.salford;

import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.SimulationTimeFunction;
import es.ull.isaatc.simulation.SimulationTimeUnit;
import es.ull.isaatc.simulation.TimeDrivenActivity;
import es.ull.isaatc.simulation.WorkGroup;

class SalfordWLSimulation extends Simulation {
	static final String [] specNames = new String[] {"ESP1"};
	static final int [] slotSize = new int[] {50};
	public SalfordWLSimulation(int id, double startTs, double endTs) {
		super(id, "Salford Waiting List Model", SimulationTimeUnit.WEEK, startTs, endTs);
	}

	@Override
	protected void createModel() {
		// Defines the patient types
		for (int i = 0; i < specNames.length; i++) {
			new ElementType(i, this, "EL_" + specNames[i]);
			new ElementType(i + specNames.length, this, "EM_" + specNames[i]);
		}
		
		// Defines resource types
		for (int i = 0; i < specNames.length; i++)
			new ResourceType(i, this, "Slot " + specNames[i]);
		
		// Defines activities
		for (int i = 0; i < specNames.length; i++)
			new TimeDrivenActivity(i, this, "Get Slot " + specNames[i]);
		
		// Defines Workgroups and associate to activities
		for (int i = 0; i < specNames.length; i++) {
			WorkGroup wg = new WorkGroup(getResourceType(i), 1);
			((TimeDrivenActivity)getActivity(i)).addWorkGroup(new SimulationTimeFunction(this, "ConstantVariate", 0), wg);
		}
		
		// Defines available resources
		
	}
}

/**
 * Time unit is DAY, but the duration of the simulation is expressed in weeks.
 * @author Iván Castilla Rodríguez
 */
class SalfordWLExperiment extends PooledExperiment {
	static final int NWEEKS = 10;
	static final int NEXP = 1;
	
	public SalfordWLExperiment() {
		super("Salford Waiting List", NEXP);
	}

	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new SalfordWLSimulation(ind, 0.0, NWEEKS * 7);
		// TODO Auto-generated method stub
		return sim;
	}
}
/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SalfordWaitingList {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
