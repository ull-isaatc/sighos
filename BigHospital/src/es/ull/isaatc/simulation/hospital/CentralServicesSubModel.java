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
 * @author Iván Castilla Rodríguez
 *
 */
public class CentralServicesSubModel {
	private static TimeDrivenActivity actOPNucTest = null;
	private static TimeDrivenActivity actOPRadTest = null;
	private static TimeDrivenActivity actOPNucAnalysis = null;
	private static TimeDrivenActivity actOPRadAnalysis = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NTECHNUC(Integer.class, "Resources available for Nuclear Medicine"),
		LENGTH_NUCTEST(SimulationTimeFunction.class, "Duration of the test"),
		LENGTH_NUCANALYSIS(SimulationTimeFunction.class, "Duration of the analysis of results"),
		NTECHRAD(Integer.class, "Resources available for Radiology"),
		LENGTH_RADTEST(SimulationTimeFunction.class, "Duration of the test"),
		LENGTH_RADANALYSIS(SimulationTimeFunction.class, "Duration of the analysis of results");
		
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
		ResourceType rtNuc = HospitalModelTools.createNStdHumanResources(factory, "Nuclear Medicine Technician", (Integer)params.get(Parameters.NTECHNUC));
		ResourceType rtRad = HospitalModelTools.createNStdHumanResources(factory, "Radiology Technician", (Integer)params.get(Parameters.NTECHRAD));

		// Workgroups
		WorkGroup wgNuc = factory.getWorkGroupInstance(new ResourceType[] {rtNuc}, new int[] {1});
		WorkGroup wgRad = factory.getWorkGroupInstance(new ResourceType[] {rtRad}, new int[] {1});
		
		// Activities
		actOPNucTest = factory.getTimeDrivenActivityInstance("Nuclear Medicine Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPNucTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_NUCTEST), wgNuc);
		actOPRadTest = factory.getTimeDrivenActivityInstance("Radiology Test OP", 2, EnumSet.noneOf(TimeDrivenActivity.Modifier.class)); 
		actOPRadTest.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADTEST), wgRad); 
		actOPNucAnalysis = factory.getTimeDrivenActivityInstance("Nuclear Medicine Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL)); 
		actOPNucAnalysis.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_NUCANALYSIS), wgNuc); 
		actOPRadAnalysis = factory.getTimeDrivenActivityInstance("Radiology Analysis OP", 2, EnumSet.of(TimeDrivenActivity.Modifier.NONPRESENTIAL));
		actOPRadAnalysis.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_RADANALYSIS), wgRad);
	}

	/**
	 * @return 
	 */
	public static Flow[] getOPNuclearFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPNucTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPNucAnalysis);
		flow[0].link(flow[1]);
		return flow;
	}

	/**
	 * @return 
	 */
	public static Flow[] getOPRadiologyFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPRadTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actOPRadAnalysis);
		flow[0].link(flow[1]);
		return flow;
	}
}
