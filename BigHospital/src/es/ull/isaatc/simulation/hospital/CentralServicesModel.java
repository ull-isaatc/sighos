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
	private static TimeDrivenActivity actOPNucTest = null;
	private static TimeDrivenActivity actOPRadTest = null;
	private static TimeDrivenActivity actOPNucReport = null;
	private static TimeDrivenActivity actOPRadReport = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECHNUC(Integer.class, "Resources available for Nuclear Medicine"),
		LENGTH_NUCTEST(SimulationTimeFunction.class, "Duration of the test"),
		LENGTH_NUCREPORT(SimulationTimeFunction.class, "Duration of the preparation of the report of results"),
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
		ResourceType rtNuc = HospitalModelConfig.createNStdHumanResources(factory, "Nuclear Medicine Technician", (Integer)params.get(Parameters.NTECHNUC));
		ResourceType rtRad = HospitalModelConfig.createNStdHumanResources(factory, "Radiology Technician", (Integer)params.get(Parameters.NTECHRAD));

		// Workgroups
		WorkGroup wgNuc = factory.getWorkGroupInstance(new ResourceType[] {rtNuc}, new int[] {1});
		WorkGroup wgRad = factory.getWorkGroupInstance(new ResourceType[] {rtRad}, new int[] {1});
		
		// Activities
		actOPNucTest = factory.getTimeDrivenActivityInstance("Nuclear Medicine Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPNucTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_NUCTEST), wgNuc);
		actOPRadTest = factory.getTimeDrivenActivityInstance("Radiology Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPRadTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADTEST), wgRad); 
		actOPNucReport = factory.getTimeDrivenActivityInstance("Nuclear Medicine Report OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL)); 
		actOPNucReport.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_NUCREPORT), wgNuc); 
		actOPRadReport = factory.getTimeDrivenActivityInstance("Radiology Report OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOPRadReport.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADREPORT), wgRad);
	}

	/**
	 * Creates the flow for a nuclear test for an outpatient. 
	 * @return The flow for a nuclear test for an outpatient
	 */
	public static Flow[] getOPNuclearFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPNucTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPNucReport);
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
