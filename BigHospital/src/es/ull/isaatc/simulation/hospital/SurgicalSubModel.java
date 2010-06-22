/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.function.TimeFunctionFactory;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.simulation.common.flow.SynchronizationFlow;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class SurgicalSubModel extends HospitalSubModel {
	private static final int BEDSPACU = 10;
	private static final int BEDSICU = 15;
	private static TimeDrivenActivity actPACU = null;
	private static TimeDrivenActivity actICU = null;
	private static WorkGroup wgPACU = null;
	private static WorkGroup wgICU = null;
	private static boolean created = false;
	protected int nBeds;
	protected int nSurgeries;
	protected int nSurgeons;
	protected int nDoctors;
	protected int nNurses;
	protected SimulationTimeFunction PACUduration;
	protected SimulationTimeFunction ICUduration;
	protected SimulationTimeFunction postICUduration;
	protected SimulationTimeFunction postOpduration;
	protected TimeFunction nPatients;
	protected SimulationCycle interArrival;

	/**
	 * @param factory
	 * @param nBeds
	 * @param nSurgeries
	 * @param nSurgeons
	 * @param nDoctors
	 * @param nNurses
	 */
	public SurgicalSubModel(SimulationObjectFactory factory, String name, String code, int firstId, 
			int nBeds, int nSurgeries, int nSurgeons, int nDoctors, int nNurses, 
			SimulationTimeFunction PACUduration, SimulationTimeFunction ICUduration,
			SimulationTimeFunction postICUduration, SimulationTimeFunction postOpduration,
			TimeFunction nPatients, SimulationCycle interArrival) {
		super(factory, name, code, firstId);
		this.nBeds = nBeds;
		this.nSurgeries = nSurgeries;
		this.nSurgeons = nSurgeons;
		this.nDoctors = nDoctors;
		this.nNurses = nNurses;
		this.PACUduration = PACUduration;
		this.ICUduration = ICUduration;
		this.postOpduration = postOpduration;
		this.postICUduration = postICUduration;
		this.nPatients = nPatients;
		this.interArrival = interArrival;
	}

	/**
	 * Creates the structures that are common to all the surgical services, that is, 
	 * the P.A.C.U. and the I.C.U.
	 * @param factory
	 */
	public static void createModel(SimulationObjectFactory factory) {
		if (!created) {
			final Simulation simul = factory.getSimulation();
			 
			int rtId = BigHospital.SURGICALID;
			// Resource types
			ResourceType rtPACUBed = factory.getResourceTypeInstance(rtId++, "PACU Bed"); 
			ResourceType rtICUBed = factory.getResourceTypeInstance(rtId++, "ICU Bed"); 
	
			// Resources
			int resId = BigHospital.SURGICALID;
			for (int i = 0; i < BEDSPACU; i++) {
				Resource res = factory.getResourceInstance(resId++, "PACU Bed " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtPACUBed);
			}
			for (int i = 0; i < BEDSICU; i++) {
				Resource res = factory.getResourceInstance(resId++, "ICU Bed " + i);
				res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtICUBed);
			}
	
			// Workgroups
			int wgId = BigHospital.SURGICALID;
			wgPACU = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtPACUBed}, new int[] {1});
			wgICU = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtICUBed}, new int[] {1});
	
			// Activities: Duration depends on the service, so workgroups are not added yet
			int actId = BigHospital.SURGICALID;
			actPACU = factory.getTimeDrivenActivityInstance(actId++, "PACU stay");
//			actPACU.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 2)), wgPACU);
			actICU = factory.getTimeDrivenActivityInstance(actId++, "ICU stay");
//			actICU.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", new TimeStamp(TimeUnit.HOUR, 12)), wgICU);
			
			assert rtId < BigHospital.SURGICALID + BigHospital.MAXENTITIESXSERVICE;
			assert resId < BigHospital.SURGICALID + BigHospital.MAXENTITIESXSERVICE;
			assert wgId < BigHospital.SURGICALID + BigHospital.MAXENTITIESXSERVICE;
			assert actId < BigHospital.SURGICALID + BigHospital.MAXENTITIESXSERVICE;
			created = true;
		}
	}
	
	/**
	 * @return the actPACU
	 */
	public static TimeDrivenActivity getActPACU(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actPACU;
	}

	/**
	 * @return the actICU
	 */
	public static TimeDrivenActivity getActICU(SimulationObjectFactory factory) {
		if (!created)
			createModel(factory);
		return actICU;
	}

	protected static void addPACUWorkGroup(SimulationTimeFunction duration, ElementType et) {
		actPACU.addWorkGroup(duration, wgPACU, new ElementTypeCondition(et));
	}
	
	protected static void addICUWorkGroup(SimulationTimeFunction duration, ElementType et) {
		actICU.addWorkGroup(duration, wgICU, new ElementTypeCondition(et));
	}
	
	@Override
	public void createModel() {
		// Element types
		ElementType et = factory.getElementTypeInstance(firstId, code + " Patient");
		
		// Resource types
		int rtId = firstId;
		ResourceType rtBed = factory.getResourceTypeInstance(rtId++, code + " Bed");
		ResourceType rtDoctor = factory.getResourceTypeInstance(rtId++, code + " Doctor");
		ResourceType rtSurgeon = factory.getResourceTypeInstance(rtId++, code + " Surgeon");
		ResourceType rtSurgery = factory.getResourceTypeInstance(rtId++, code + " Surgery");
		ResourceType rtNurse = factory.getResourceTypeInstance(rtId++, code + " Nurse");
		
		// Resources
		int resId = firstId;
		for (int i = 0; i < nBeds; i++) {
			Resource res = factory.getResourceInstance(resId++, code + " Bed " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtBed);
		}
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(resId++, code + " Doctor " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtDoctor);
		}
		for (int i = 0; i < nSurgeons; i++) {
			Resource res = factory.getResourceInstance(resId++, code + " Surgeon " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtSurgeon);
		}
		for (int i = 0; i < nNurses; i++) {
			Resource res = factory.getResourceInstance(resId++, code + " Nurse " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtNurse);
		}
		for (int i = 0; i < nSurgeries; i++) {
			Resource res = factory.getResourceInstance(resId++, code + " Surgery " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtSurgery);
		}

		// Workgroups
		int wgId = firstId;
		WorkGroup wgBed = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtBed}, new int[] {1});
		WorkGroup wgDoc = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtDoctor}, new int[] {1});
		WorkGroup wgSur = factory.getWorkGroupInstance(wgId++, new ResourceType[] {rtSurgery, rtSurgeon, rtNurse}, new int[] {1, 1, 1});

		// Time Driven Activities
		int actId = firstId;
		TimeDrivenActivity actFirstApp = factory.getTimeDrivenActivityInstance(actId++, "1st Out App");
		actFirstApp.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 10), wgDoc);
		TimeDrivenActivity actSuccApp = factory.getTimeDrivenActivityInstance(actId++, "Successive Out App");
		actSuccApp.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 12), wgDoc);
		TimeDrivenActivity actSurgery = factory.getTimeDrivenActivityInstance(actId++, "Surgery");
		actSurgery.addWorkGroup(new SimulationTimeFunction(simul.getTimeUnit(), "ConstantVariate", 30), wgSur);

		// Flows
		int flowId = firstId;
		SingleFlow root = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actFirstApp);
		ExclusiveChoiceFlow firstDecision = (ExclusiveChoiceFlow)factory.getFlowInstance(flowId++, "ExclusiveChoiceFlow");
		root.link(firstDecision);
		
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance(flowId++, "StructuredSynchroMergeFlow");
		testDecision.addBranch((SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", CentralServicesSubModel.getActLab(factory)), 
				new PercentageCondition(90));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", CentralServicesSubModel.getActHae(factory)), 
				new PercentageCondition(70));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", CentralServicesSubModel.getActCar(factory)), 
				new PercentageCondition(20));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", CentralServicesSubModel.getActRad(factory)), 
				new PercentageCondition(40));
		
		SingleFlow succ = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actSuccApp);
		testDecision.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance(flowId++, "ForLoopFlow", testDecision, succ, TimeFunctionFactory.getInstance("UniformVariate", 1, 5));
		firstDecision.link(mainLoop, new PercentageCondition(80));
		ExclusiveChoiceFlow secondDecision = (ExclusiveChoiceFlow)factory.getFlowInstance(flowId++, "ExclusiveChoiceFlow");
		mainLoop.link(secondDecision);
		
		// FIXME: 3-Phase simulation doesn't work with this
		// Flow Driven Activities
		FlowDrivenActivity actStay = factory.getFlowDrivenActivityInstance(actId++, "Stay in bed");
		SingleFlow surgicalPhase = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actStay);
		secondDecision.link(surgicalPhase, new PercentageCondition(50));
		SingleFlow surgery = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actSurgery);

		addPACUWorkGroup(PACUduration, et);
		addICUWorkGroup(ICUduration, et);
		SingleFlow PACU = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", getActPACU(factory));
		surgery.link(PACU);
		ProbabilitySelectionFlow afterSurgery = (ProbabilitySelectionFlow)factory.getFlowInstance(flowId++, "ProbabilitySelectionFlow");
		PACU.link(afterSurgery);
		SingleFlow ICU = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", getActICU(factory));
		afterSurgery.link(ICU, 0.1);
		TimeDrivenActivity actPostICU = factory.getTimeDrivenActivityInstance(actId++, "Recovering after ICU");
		actPostICU.addWorkGroup(postICUduration, factory.getWorkGroupInstance(wgId++, new ResourceType[] {}, new int[] {}));
		SingleFlow postICU = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actPostICU);
		ICU.link(postICU);
		afterSurgery.link(surgery, 0.1);
		TimeDrivenActivity actPostOp = factory.getTimeDrivenActivityInstance(actId++, "Recovering after Surgery");
		actPostOp.addWorkGroup(postOpduration, factory.getWorkGroupInstance(wgId++, new ResourceType[] {}, new int[] {}));
		SingleFlow postOp = (SingleFlow)factory.getFlowInstance(flowId++, "SingleFlow", actPostOp);
		afterSurgery.link(postOp, 0.8);
		SynchronizationFlow afterOpSync = (SynchronizationFlow)factory.getFlowInstance(flowId++, "SynchronizationFlow");
		postICU.link(afterOpSync);
		postOp.link(afterOpSync);
				
		actStay.addWorkGroup(surgery, afterOpSync, wgBed);
		
		ElementCreator ec = factory.getElementCreatorInstance(firstId, nPatients, et, root);
		factory.getTimeDrivenGeneratorInstance(firstId, ec, interArrival);
		assert rtId < firstId + BigHospital.MAXENTITIESXSERVICE;
		assert resId < firstId + BigHospital.MAXENTITIESXSERVICE;
		assert wgId < firstId + BigHospital.MAXENTITIESXSERVICE;
		assert actId < firstId + BigHospital.MAXENTITIESXSERVICE;
		assert flowId < firstId + BigHospital.MAXENTITIESXSERVICE;
	}

}
