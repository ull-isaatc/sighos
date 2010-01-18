/**
 * 
 */
package es.ull.isaatc.salford;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.*;

class SalfordWLSimulation extends StandAloneLPSimulation {
	static final String [] specNames = new String[] {"ESP1"};
	static final int [] slotSize = new int[] {50};
	public SalfordWLSimulation(int id, double startTs, double endTs) {
		super(id, "Salford Waiting List Model", startTs, endTs);
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
			new Activity(i, this, "Get Slot " + specNames[i]);
		
		// Defines Workgroups and associate to activities
		for (int i = 0; i < specNames.length; i++) {
			WorkGroup wg = new WorkGroup(i, this, "WG " + specNames[i]);
			wg.add(getResourceType(i), 1);
			getActivity(i).addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 0), wg);
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
		return null;
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
