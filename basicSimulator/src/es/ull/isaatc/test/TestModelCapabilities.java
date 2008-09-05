/**
 * 
 */
package es.ull.isaatc.test;

import java.util.EnumSet;

import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.Activity;
import es.ull.isaatc.simulation.PooledExperiment;
import es.ull.isaatc.simulation.Resource;
import es.ull.isaatc.simulation.ResourceType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.StandAloneLPSimulation;
import es.ull.isaatc.simulation.WorkGroup;
import es.ull.isaatc.simulation.listener.ListenerController;
import es.ull.isaatc.simulation.listener.StdInfoListener;

class TestModelCapabilitiesSimulation extends StandAloneLPSimulation {
	public TestModelCapabilitiesSimulation(int id, int ndays) {
		super(id, "Simulation for testing model capabilities", 0.0, ndays * 60 * 24.0);
	}
	
	@Override
	protected void createModel() {
		// Resource types
		new ResourceType(0, this, "Doctor");
		new ResourceType(1, this, "Specialized Doctor");
		new ResourceType(2, this, "Surgeon");
		new ResourceType(3, this, "Lab Technician");		
		new ResourceType(4, this, "USS Technician");
		new ResourceType(5, this, "Surgery");
		new ResourceType(6, this, "Exam Room");
		new ResourceType(7, this, "Ultrasonograph");
		new ResourceType(8, this, "Automated Analizer");
		
		// WorkGroups and Activities
		WorkGroup wg = new WorkGroup(0, this, "Appointment");
		wg.add(getResourceType(0), 1);
		wg.add(getResourceType(6), 1);
		Activity act = new Activity(0, this, "First Outpatient Appointment", 0);
		act.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 10), wg);
		act = new Activity(1, this, "Subsequent Outpatient Appointment", 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 12.0, 3.0), wg);
		wg = new WorkGroup(1, this, "Specialized Appointment");
		wg.add(getResourceType(1), 1);
		wg.add(getResourceType(6), 1);
		act = new Activity(2, this, "Specialized Outpatient Appointment");
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 15.0, 5.0), wg);
		act = new Activity(3, this, "Blood Sample", 0);
		wg = new WorkGroup(4, this, "Lab Sample");
		wg.add(getResourceType(3), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("ConstantVariate", 5.0), wg);
		act = new Activity(4, this, "Blood Test", 1, EnumSet.of(Activity.Modifier.INTERRUPTIBLE, Activity.Modifier.NONPRESENTIAL));
		wg = new WorkGroup(5, this, "Lab 1");
		wg.add(getResourceType(3), 2);
		wg.add(getResourceType(8), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 50.0, 10.0), 0, wg);
		wg = new WorkGroup(6, this, "Lab 2");
		wg.add(getResourceType(3), 1);
		wg.add(getResourceType(8), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 120.0, 10.0), 1, wg);
		act = new Activity(5, this, "USS");
		wg = new WorkGroup(7, this, "USS");
		wg.add(getResourceType(4), 1);
		wg.add(getResourceType(7), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 30.0, 5.0), wg);
		act = new Activity(6, this, "Surgery");
		wg = new WorkGroup(2, this, "Surgery 1");
		wg.add(getResourceType(2), 2);
		wg.add(getResourceType(5), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 45.0, 5.0), wg);
		wg = new WorkGroup(3, this, "Surgery 2");
		wg.add(getResourceType(2), 1);
		wg.add(getResourceType(5), 1);
		act.addWorkGroup(TimeFunctionFactory.getInstance("NormalVariate", 115.0, 5.0), wg);
		
		// Resources
		Resource res = new Resource(0, this, "Doctor 1");
		
	}
	
}

class TestModelCapabilitiesExperiment extends PooledExperiment {
	final static int NEXP = 1;
	final static int NDAYS = 1;

	public TestModelCapabilitiesExperiment() {
		super("Testing model capabilities", NEXP);
	}
	
	@Override
	public Simulation getSimulation(int ind) {
		Simulation sim = new TestModelCapabilitiesSimulation(ind, NDAYS);
		ListenerController cont = new ListenerController();
		sim.setListenerController(cont);
		cont.addListener(new StdInfoListener());
		return sim;
	}
	
}

/**
 * Puts to the test all the model capabilities of SIGHOS.
 * @author Iván Castilla Rodríguez
 *
 */
public class TestModelCapabilities {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new TestModelCapabilitiesExperiment().start();
	}

}
