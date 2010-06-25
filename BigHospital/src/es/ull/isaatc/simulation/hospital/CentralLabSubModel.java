/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CentralLabSubModel {
	private static TimeDrivenActivity actSample = null;
	private static TimeDrivenActivity actCent = null;
	private static TimeDrivenActivity actTest = null;
	private static TimeDrivenActivity actCheck = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECH(Integer.class, "Number of technicians"),
		NCENT(Integer.class, "Number of centrifuges"),
		NNURSES(Integer.class, "Number of nurses"),
		NSLOTS(Integer.class, "'Slots' for analytical tests"),
		LENGTH_SAMPLE(SimulationTimeFunction.class, "Duration of taking a sample"),
		LENGTH_CENT(SimulationTimeFunction.class, "Duration of centrifugation"),
		LENGTH_ANALYSIS(SimulationTimeFunction.class, "Duration of analysis"),
		LENGTH_CHECK(SimulationTimeFunction.class, "Duration of check of analysis");
		
		private final Class<?> type;
		private final String description;
		
		private Parameters(Class<?> type, String description) {
			this.type = type;
			this.description = description;
		}
		
		@Override
		public Class<?> getType() {
			return type;
		}
		
		@Override
		public String toString() {
			return super.toString() + ": " + description;
		}
	}
	
	public static void createModel(SimulationObjectFactory factory, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
	
		// Resource types
		ResourceType rtTech = factory.getResourceTypeInstance("Laboratory Technician");
		ResourceType rtCent = factory.getResourceTypeInstance("Centrifuge");
		ResourceType rtNurse = factory.getResourceTypeInstance("Nurse");
		ResourceType rtSlot = factory.getResourceTypeInstance("Analytical slot");
		
		// Resources
		final int nTech = ((Integer)params.get(Parameters.NTECH)).intValue();
		for (int i = 0; i < nTech; i++) {
			Resource res = factory.getResourceInstance("Lab Technician " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtTech);
		}
		final int nNurses = ((Integer)params.get(Parameters.NNURSES)).intValue();
		for (int i = 0; i < nNurses; i++) {
			Resource res = factory.getResourceInstance("Lab Nurse " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtNurse);
		}
		final int nCent = ((Integer)params.get(Parameters.NCENT)).intValue();
		for (int i = 0; i < nCent; i++) {
			Resource res = factory.getResourceInstance("Centrifuge " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtCent);
		}
		final int nSlot = ((Integer)params.get(Parameters.NSLOTS)).intValue();
		for (int i = 0; i < nSlot; i++) {
			Resource res = factory.getResourceInstance("Machine slot " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtSlot);
		}
			
		// Workgroups
		WorkGroup wgSample = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgCent = factory.getWorkGroupInstance(new ResourceType[] {rtCent}, new int[] {1});
		WorkGroup wgTest = factory.getWorkGroupInstance(new ResourceType[] {rtSlot, rtTech}, new int[] {1, 1});
		WorkGroup wgCheck = factory.getWorkGroupInstance(new ResourceType[] {rtNurse}, new int[] {1});
		
		// Activities
		actSample = factory.getTimeDrivenActivityInstance("Take a sample");
		actSample.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SAMPLE), wgSample);
		actCent = factory.getTimeDrivenActivityInstance("Centrifugation");
		actCent.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CENT), wgCent);
		actTest = factory.getTimeDrivenActivityInstance("Analysis");
		actTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ANALYSIS), wgTest);
		actCheck = factory.getTimeDrivenActivityInstance("Check");
		actCheck.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CHECK), wgCheck);
	}

	public static Flow[] getFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		// Flow of a lab test
		// Pre-analytical PHASE
		flow[0] = factory.getFlowInstance("SingleFlow", actSample);
		SingleFlow cent = (SingleFlow)factory.getFlowInstance("SingleFlow", actCent);
		flow[0].link(cent);
		// Analytical PHASE
		SingleFlow test = (SingleFlow)factory.getFlowInstance("SingleFlow", actTest);
		cent.link(test);
		//Post-analytical PHASE
		SingleFlow check = (SingleFlow)factory.getFlowInstance("SingleFlow", actCheck);
		test.link(check);
		
		flow[1] = check;
		return flow;
	}
}
