/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import java.util.EnumSet;

import es.ull.isaatc.simulation.common.*;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * Model of the central services of a hospital
 * 
 * @author Iván Castilla Rodríguez
 */
public class CentralServicesModel {
	private static TimeDrivenActivity actOPUSSTest = null;
	private static TimeDrivenActivity actOPRadTest = null;
	private static TimeDrivenActivity actOPUSSReport = null;
	private static TimeDrivenActivity actOPRadReport = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECHUSS(Integer.class, "Resources available for USS"),
		LENGTH_USSTEST(SimulationTimeFunction.class, "Duration of the test"),
		LENGTH_USSREPORT(SimulationTimeFunction.class, "Duration of the preparation of the report of results"),
		NTECHRAD(Integer.class, "Resources available for Radiology"),
		LENGTH_RADTEST(SimulationTimeFunction.class, "Duration of the test"),
		LENGTH_RADREPORT(SimulationTimeFunction.class, "Duration of the preparation of the report of results");
		
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
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");

		// Resource types
		ResourceType rtUSS = HospitalModelConfig.createNStdHumanResources(factory, "USS Technician", (Integer)params.get(Parameters.NTECHUSS));
		ResourceType rtRad = HospitalModelConfig.createNStdHumanResources(factory, "Radiology Technician", (Integer)params.get(Parameters.NTECHRAD));

		// Workgroups
		WorkGroup wgUSS = factory.getWorkGroupInstance(new ResourceType[] {rtUSS}, new int[] {1});
		WorkGroup wgRad = factory.getWorkGroupInstance(new ResourceType[] {rtRad}, new int[] {1});
		
		// Activities
		actOPUSSTest = factory.getTimeDrivenActivityInstance("USS Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPUSSTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_USSTEST), wgUSS);
		actOPRadTest = factory.getTimeDrivenActivityInstance("Radiology Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPRadTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADTEST), wgRad); 
		actOPUSSReport = factory.getTimeDrivenActivityInstance("USS Report OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL)); 
		actOPUSSReport.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_USSREPORT), wgUSS); 
		actOPRadReport = factory.getTimeDrivenActivityInstance("Radiology Report OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOPRadReport.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADREPORT), wgRad);
	}

	/**
	 * Creates the flow for a nuclear test for an outpatient. 
	 * @return The flow for a nuclear test for an outpatient
	 */
	public static Flow[] getOPUSSFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPUSSTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPUSSReport);
		flow[0].link(flow[1]);
		return flow;
	}

	/**
	 * Creates the flow for a X-ray test for an outpatient. 
	 * @return The flow for a X-ray test for an outpatient
	 */
	public static Flow[] getOPRadiologyFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPRadTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPRadReport);
		flow[0].link(flow[1]);
		return flow;
	}
}
