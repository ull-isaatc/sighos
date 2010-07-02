/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SurgicalSubModel {
	private static TimeDrivenActivity actPACU = null;
	private static TimeDrivenActivity actICU = null;
	private static ResourceType rtAnaes = null;
	private static WorkGroup wgPACU = null;
	private static WorkGroup wgICU = null;

	public enum Parameters implements ModelParameterMap.ModelParameter {
		NBEDS_PACU(Integer.class, "Number of beds available in P.A.C.U."),
		NBEDS_ICU(Integer.class, "Number of beds available in I.C.U."),
		NANAESTHETISTS(Integer.class, "Number of anaesthetists");

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
	
	/**
	 * Creates the structures that are common to all the surgical services, that is, 
	 * the P.A.C.U. and the I.C.U.
	 * @param factory
	 */
	public static void createModel(SimulationObjectFactory factory, ModelParameterMap params) {
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");
		
		// Resource types and resources
		ResourceType rtPACUBed = HospitalModelTools.createNStdMaterialResources(factory, "PACU Bed", ((Integer)params.get(Parameters.NBEDS_PACU)).intValue());
		ResourceType rtICUBed = HospitalModelTools.createNStdMaterialResources(factory, "ICU Bed", ((Integer)params.get(Parameters.NBEDS_ICU)).intValue());
		rtAnaes = HospitalModelTools.createNStdHumanResources(factory, "Anaesthetist", ((Integer)params.get(Parameters.NANAESTHETISTS)).intValue());

		// Workgroups
		wgPACU = factory.getWorkGroupInstance(new ResourceType[] {rtPACUBed}, new int[] {1});
		wgICU = factory.getWorkGroupInstance(new ResourceType[] {rtICUBed}, new int[] {1});

		// Activities: Duration depends on the service, so workgroups are not added yet
		actPACU = factory.getTimeDrivenActivityInstance("PACU stay");
		actICU = factory.getTimeDrivenActivityInstance("ICU stay");
	}
	
	/**
	 * @return the actPACU
	 */
	protected static TimeDrivenActivity getActPACU() {
		return actPACU;
	}

	/**
	 * @return the actICU
	 */
	protected static TimeDrivenActivity getActICU() {
		return actICU;
	}

	protected static void addPACUWorkGroup(SimulationTimeFunction duration, ElementType et) {
		actPACU.addWorkGroup(duration, wgPACU, new ElementTypeCondition(et));
	}
	
	protected static void addICUWorkGroup(SimulationTimeFunction duration, ElementType et) {
		actICU.addWorkGroup(duration, wgICU, new ElementTypeCondition(et));
	}
	
	protected static ResourceType getAnaesthetistRT() {
		return rtAnaes;
	}
}
