/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.simulation.common.*;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class CentralServicesSubModel {
	private static TimeDrivenActivity actHae = null;
	private static TimeDrivenActivity actCar = null;
	private static TimeDrivenActivity actRad = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		RESHAE(Integer.class, "Resources available for Haematology"),
		RESCAR(Integer.class, "Resources available for Cardiology"),
		RESRAD(Integer.class, "Resources available for Radiology");
		
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
		ResourceType rtHae = factory.getResourceTypeInstance("Haematology Technician");
		ResourceType rtCar = factory.getResourceTypeInstance("Cardiology Technician");
		ResourceType rtRad = factory.getResourceTypeInstance("Radiology Technician");

		// Resources
		final int resHae = ((Integer)params.get(Parameters.RESHAE)).intValue();
		for (int i = 0; i < resHae; i++) {
			Resource res = factory.getResourceInstance("Hae. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtHae);
		}			
		final int resCar = ((Integer)params.get(Parameters.RESCAR)).intValue();
		for (int i = 0; i < resCar; i++) {
			Resource res = factory.getResourceInstance("Car. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtCar);
		}			
		final int resRad = ((Integer)params.get(Parameters.RESRAD)).intValue();
		for (int i = 0; i < resRad; i++) {
			Resource res = factory.getResourceInstance("Rad. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtRad);
		}
		
		// Workgroups
		WorkGroup wgHae = factory.getWorkGroupInstance(new ResourceType[] {rtHae}, new int[] {1});
		WorkGroup wgCar = factory.getWorkGroupInstance(new ResourceType[] {rtCar}, new int[] {1});
		WorkGroup wgRad = factory.getWorkGroupInstance(new ResourceType[] {rtRad}, new int[] {1});
		
		// Activities
		actHae = factory.getTimeDrivenActivityInstance("Haematology Test");
		actHae.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgHae);
		actCar = factory.getTimeDrivenActivityInstance("Cardiology Test");
		actCar.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgCar);
		actRad = factory.getTimeDrivenActivityInstance("Radiology Test");
		actRad.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgRad);
	}

	/**
	 * @return the actHae
	 */
	public static TimeDrivenActivity getActHae() {
		return actHae;
	}

	/**
	 * @return the actCar
	 */
	public static TimeDrivenActivity getActCar() {
		return actCar;
	}

	/**
	 * @return the actRad
	 */
	public static TimeDrivenActivity getActRad() {
		return actRad;
	}
}
