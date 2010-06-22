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
	private final static int RESLAB = 2;
	private final static int RESHAE = 2;
	private final static int RESCAR = 2;
	private final static int RESRAD = 2;
	private static TimeDrivenActivity actLab = null;
	private static TimeDrivenActivity actHae = null;
	private static TimeDrivenActivity actCar = null;
	private static TimeDrivenActivity actRad = null;
	private static boolean created = false;
	
	public static void createModel(SimulationObjectFactory factory) {
		if (!created) {
			final Simulation simul = factory.getSimulation();
			// Resource types
			int rtId = BigHospital.CENTRALSERVICESID;
			ResourceType rtLab = factory.getResourceTypeInstance(rtId++, "Laboratory Technician");
			ResourceType rtHae = factory.getResourceTypeInstance(rtId++, "Haematology Technician");
			ResourceType rtCar = factory.getResourceTypeInstance(rtId++, "Cardiology Technician");
			ResourceType rtRad = factory.getResourceTypeInstance(rtId++, "Radiology Technician");
	
			// Resources
			int resId = BigHospital.CENTRALSERVICESID;
			for (int i = 0; i < RESLAB; i++) {
				Resource res = factory.getResourceInstance(resId++, "Lab. Tech. " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtLab);
			}
			for (int i = 0; i < RESHAE; i++) {
				Resource res = factory.getResourceInstance(resId++, "Hae. Tech. " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtHae);
			}			
			for (int i = 0; i < RESCAR; i++) {
				Resource res = factory.getResourceInstance(resId++, "Car. Tech. " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtCar);
			}			
			for (int i = 0; i < RESRAD; i++) {
				Resource res = factory.getResourceInstance(resId++, "Rad. Tech. " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtRad);
			}
			
			// Workgroups
			int wgId = BigHospital.CENTRALSERVICESID;
			WorkGroup wgLab = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtLab}, new int[] {1});
			WorkGroup wgHae = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtHae}, new int[] {1});
			WorkGroup wgCar = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtCar}, new int[] {1});
			WorkGroup wgRad = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtRad}, new int[] {1});
			
			// Activities
			int actId = BigHospital.CENTRALSERVICESID;
			actLab = factory.getTimeDrivenActivityInstance(actId++, "Laboratory Test");
			actLab.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgLab);
			actHae = factory.getTimeDrivenActivityInstance(actId++, "Haematology Test");
			actHae.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgHae);
			actCar = factory.getTimeDrivenActivityInstance(actId++, "Cardiology Test");
			actCar.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgCar);
			actRad = factory.getTimeDrivenActivityInstance(actId++, "Radiology Test");
			actRad.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgRad);
			
			assert rtId < BigHospital.CENTRALSERVICESID + BigHospital.MAXENTITIESXSERVICE;
			assert resId < BigHospital.CENTRALSERVICESID + BigHospital.MAXENTITIESXSERVICE;
			assert wgId < BigHospital.CENTRALSERVICESID + BigHospital.MAXENTITIESXSERVICE;
			assert actId < BigHospital.CENTRALSERVICESID + BigHospital.MAXENTITIESXSERVICE;
			created = true;
		}
	}

	/**
	 * @return the actLab
	 */
	public static TimeDrivenActivity getActLab(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actLab;
	}

	/**
	 * @return the actHae
	 */
	public static TimeDrivenActivity getActHae(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actHae;
	}

	/**
	 * @return the actCar
	 */
	public static TimeDrivenActivity getActCar(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actCar;
	}

	/**
	 * @return the actRad
	 */
	public static TimeDrivenActivity getActRad(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actRad;
	}
	
	
}
