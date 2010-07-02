/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.*;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CentralServicesSubModel {
	private static TimeDrivenActivity actNucTest = null;
	private static TimeDrivenActivity actRadTest = null;
	private static TimeDrivenActivity actNucAnalysis = null;
	private static TimeDrivenActivity actRadAnalysis = null;
	
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
		actNucTest = HospitalModelTools.createStdTimeDrivenActivity(factory, "Nuclear Medicine Test", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_NUCTEST), wgNuc, true);
		actRadTest = HospitalModelTools.createStdTimeDrivenActivity(factory, "Radiology Test", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_RADTEST), wgRad, true);
		actNucAnalysis = HospitalModelTools.createStdTimeDrivenActivity(factory, "Nuclear Medicine Analysis", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_NUCANALYSIS), wgNuc, false);
		actRadAnalysis = HospitalModelTools.createStdTimeDrivenActivity(factory, "Radiology Analysis", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_RADANALYSIS), wgRad, false);
	}

	/**
	 * @return 
	 */
	public static Flow[] getNuclearFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actNucTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actNucAnalysis);
		flow[0].link(flow[1]);
		return flow;
	}

	/**
	 * @return 
	 */
	public static Flow[] getRadiologyFlow(SimulationObjectFactory factory) {
		Flow[] flow = new Flow[2];
		flow[0] = (SingleFlow)factory.getFlowInstance("SingleFlow", actRadTest);
		flow[1] = (SingleFlow)factory.getFlowInstance("SingleFlow", actRadAnalysis);
		flow[0].link(flow[1]);
		return flow;
	}
}
