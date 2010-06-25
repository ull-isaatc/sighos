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
	private static TimeDrivenActivity actLab = null;
	private static TimeDrivenActivity actHae = null;
	private static TimeDrivenActivity actCar = null;
	private static TimeDrivenActivity actRad = null;
	
	public enum Parameters implements ModelParameterMap.ModelParameter {
		RESLAB(Integer.class, "Resources available for laboratory"),
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
	
	public static int createModel(SimulationObjectFactory factory, int firstId, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
		// Resource types
		int rtId = firstId;
		ResourceType rtLab = factory.getResourceTypeInstance(rtId++, "Laboratory Technician");
		ResourceType rtHae = factory.getResourceTypeInstance(rtId++, "Haematology Technician");
		ResourceType rtCar = factory.getResourceTypeInstance(rtId++, "Cardiology Technician");
		ResourceType rtRad = factory.getResourceTypeInstance(rtId++, "Radiology Technician");

		// Resources
		int resId = firstId;
		final int resLab = ((Integer)params.get(Parameters.RESLAB)).intValue();
		for (int i = 0; i < resLab; i++) {
			Resource res = factory.getResourceInstance(resId++, "Lab. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtLab);
		}
		final int resHae = ((Integer)params.get(Parameters.RESHAE)).intValue();
		for (int i = 0; i < resHae; i++) {
			Resource res = factory.getResourceInstance(resId++, "Hae. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtHae);
		}			
		final int resCar = ((Integer)params.get(Parameters.RESCAR)).intValue();
		for (int i = 0; i < resCar; i++) {
			Resource res = factory.getResourceInstance(resId++, "Car. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtCar);
		}			
		final int resRad = ((Integer)params.get(Parameters.RESRAD)).intValue();
		for (int i = 0; i < resRad; i++) {
			Resource res = factory.getResourceInstance(resId++, "Rad. Tech. " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtRad);
		}
		
		// Workgroups
		int wgId = firstId;
		WorkGroup wgLab = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtLab}, new int[] {1});
		WorkGroup wgHae = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtHae}, new int[] {1});
		WorkGroup wgCar = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtCar}, new int[] {1});
		WorkGroup wgRad = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtRad}, new int[] {1});
		
		// Activities
		int actId = firstId;
		actLab = factory.getTimeDrivenActivityInstance(actId++, "Laboratory Test");
		actLab.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgLab);
		actHae = factory.getTimeDrivenActivityInstance(actId++, "Haematology Test");
		actHae.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgHae);
		actCar = factory.getTimeDrivenActivityInstance(actId++, "Cardiology Test");
		actCar.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgCar);
		actRad = factory.getTimeDrivenActivityInstance(actId++, "Radiology Test");
		actRad.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgRad);
		
		return Math.max(Math.max(rtId, resId), Math.max(wgId, actId));
	}

	/**
	 * @return the actLab
	 */
	public static TimeDrivenActivity getActLab() {
		return actLab;
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
