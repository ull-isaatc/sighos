/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.*;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.*;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class LabCentralSubModel {
	private static Flow labTest = null;
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECH(Integer.class, "Number of technicians"),
		NCENT(Integer.class, "Number of centrifuges"),
		NNURSES(Integer.class, "Number of nurses"),
		LENGTH_SAMPLE(SimulationTimeFunction.class, "Duration of taking a sample"),
		LENGTH_CENT(SimulationTimeFunction.class, "Duration of centrifugation");
		
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
	
	public static int createModel(SimulationObjectFactory factory, int firstId, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
		final TimeUnit unit = simul.getTimeUnit();
	
		// Resource types
		int rtId = firstId;
		ResourceType rtTech = factory.getResourceTypeInstance(rtId++, "Laboratory Technician");
		ResourceType rtCent = factory.getResourceTypeInstance(rtId++, "Centrifuge");
		ResourceType rtNurse = factory.getResourceTypeInstance(rtId++, "Nurse");
		
		// Resources
		int resId = firstId;
		
		// Workgroups
		int wgId = firstId;
		WorkGroup wgSample = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtNurse}, new int[] {1});
		WorkGroup wgCent = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtCent}, new int[] {1});
		
		// Activities
		int actId = firstId;
		TimeDrivenActivity actSample = factory.getTimeDrivenActivityInstance(actId++, "Take a sample");
		actSample.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SAMPLE), wgSample);
		TimeDrivenActivity actCent = factory.getTimeDrivenActivityInstance(actId++, "Centrifugation");
		actCent.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_CENT), wgCent);
		
		// Flow of a lab test
		int flowId = firstId;
		// Pre-analytical PHASE
		labTest = factory.getFlowInstance(flowId++, "SingleFlow", actSample);
		SingleFlow cent = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actCent);
		labTest.link(cent);
		
		return Math.max(Math.max(Math.max(rtId, resId), Math.max(wgId, actId)), flowId);
	}

}
