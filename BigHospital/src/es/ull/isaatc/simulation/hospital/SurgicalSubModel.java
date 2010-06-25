/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
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
	public static int createModel(SimulationObjectFactory factory, int firstId, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
		 
		int rtId = firstId;
		// Resource types
		ResourceType rtPACUBed = factory.getResourceTypeInstance(rtId++, "PACU Bed"); 
		ResourceType rtICUBed = factory.getResourceTypeInstance(rtId++, "ICU Bed");
		rtAnaes = factory.getResourceTypeInstance(rtId++, "Anaesthetist");			

		// Resources
		int resId = firstId;
		final int nBedsPACU = ((Integer)params.get(Parameters.NBEDS_PACU)).intValue();
		for (int i = 0; i < nBedsPACU; i++) {
			Resource res = factory.getResourceInstance(resId++, "PACU Bed " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtPACUBed);
		}
		final int nBedsICU = ((Integer)params.get(Parameters.NBEDS_ICU)).intValue();
		for (int i = 0; i < nBedsICU; i++) {
			Resource res = factory.getResourceInstance(resId++, "ICU Bed " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtICUBed);
		}
		final int nAnaesthetists = ((Integer)params.get(Parameters.NANAESTHETISTS)).intValue();
		for (int i = 0; i < nAnaesthetists; i++) {
			Resource res = factory.getResourceInstance(resId++, "Anaesthetist " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtAnaes);
		}

		// Workgroups
		int wgId = firstId;
		wgPACU = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtPACUBed}, new int[] {1});
		wgICU = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtICUBed}, new int[] {1});

		// Activities: Duration depends on the service, so workgroups are not added yet
		int actId = firstId;
		actPACU = factory.getTimeDrivenActivityInstance(actId++, "PACU stay");
		actICU = factory.getTimeDrivenActivityInstance(actId++, "ICU stay");
		
		return Math.max(Math.max(rtId, resId), Math.max(wgId, actId));
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
