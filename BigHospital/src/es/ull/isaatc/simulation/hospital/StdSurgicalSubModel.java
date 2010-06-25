/**
 * 
 */
package es.ull.isaatc.simulation.hospital;

import es.ull.isaatc.function.TimeFunction;
import es.ull.isaatc.simulation.common.ElementCreator;
import es.ull.isaatc.simulation.common.ElementType;
import es.ull.isaatc.simulation.common.FlowDrivenActivity;
import es.ull.isaatc.simulation.common.Resource;
import es.ull.isaatc.simulation.common.ResourceType;
import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.SimulationCycle;
import es.ull.isaatc.simulation.common.SimulationTableCycle;
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.simulation.common.flow.SynchronizationFlow;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class StdSurgicalSubModel {
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NBEDS(Integer.class, "Beds available for the service"),
		NSURGERIES(Integer.class, "Surgeries available for the service"),
		NSURGEONS(Integer.class, "Surgeons available for the service"),
		NDOCTORS(Integer.class, "Doctors available for the service"),
		NSCRUBNURSES(Integer.class, "Scrub Nurses available for the service"),
		NSURGERY_ASSIST(Integer.class, "Surgery Assistants: since 1 can be used in two surgeries, it is considered 2 resources"),
		LENGTH_OP1(SimulationTimeFunction.class, "Duration of 1st appointment"),
		LENGTH_OP2(SimulationTimeFunction.class, "Duration of successive appointment"),
		LENGTH_POP(SimulationTimeFunction.class, "Duration of post-surgery appointment"),
		LENGTH_OP2SUR(SimulationTimeFunction.class, "Minimum time between "),
		LENGTH_OP2OP(SimulationTimeFunction.class, "Time between successive appointments"),
		LENGTH_SUR2POP(SimulationTimeFunction.class, "Time between surgery and post-surgery appointment"),
		LENGTH_SUR(SimulationTimeFunction.class, "Duration of surgery"),
		LENGTH_PACU(SimulationTimeFunction.class, "Duration of P.A.C.U. stay"),
		LENGTH_ICU(SimulationTimeFunction.class, "Duration of I.C.U. stay"),
		LENGTH_SUR2EXIT(SimulationTimeFunction.class, "Minimum stay after surgery"),
		NPATIENTS(TimeFunction.class, "Number of patients that arrives at the service"),
		INTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients"),
		ITERSUCC(TimeFunction.class, "Number of successive appointments"),
		PROB_1ST_APP(Double.class, "Probability of being first appointment");
		
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
	
	public static void createModel(SimulationObjectFactory factory, String code, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
		// Element types
		ElementType et = factory.getElementTypeInstance(code + " Patient");
		
		// Resource types
		ResourceType rtBed = factory.getResourceTypeInstance(code + " Bed");
		ResourceType rtDoctorFirst = factory.getResourceTypeInstance(code + " Doctor 1st");
		ResourceType rtDoctorSucc = factory.getResourceTypeInstance(code + " Doctor Succ");
		ResourceType rtSurgeon = factory.getResourceTypeInstance(code + " Surgeon");
		ResourceType rtSurgery = factory.getResourceTypeInstance(code + " Surgery");
		ResourceType rtSurgeryAssist = factory.getResourceTypeInstance(code + " Surgery Assistant");
		ResourceType rtScrubNurse = factory.getResourceTypeInstance(code + " Scrub Nurse");
		
		TimeStamp[] daysFirst = new TimeStamp[5];
		for (int i = 0; i < 5; i++)
			daysFirst[i] = new TimeStamp(TimeUnit.HOUR, HospitalModelTools.DAYSTART + 24 * i);
		SimulationCycle doctorFirstCycle = new SimulationTableCycle(simul.getTimeUnit(), daysFirst);
		final double probFirstApp = ((Double)params.get(Parameters.PROB_1ST_APP)).doubleValue();
		long hoursFirst = Math.round(HospitalModelTools.WORKHOURS * probFirstApp);
		TimeStamp[] daysSucc = new TimeStamp[5];
		for (int i = 0; i < 5; i++)
			daysSucc[i] = new TimeStamp(TimeUnit.HOUR, HospitalModelTools.DAYSTART + hoursFirst + 24 * i);
		SimulationCycle doctorSuccCycle = new SimulationTableCycle(simul.getTimeUnit(), daysSucc);

		// Resources
		final int nBeds = ((Integer)params.get(Parameters.NBEDS)).intValue();
		for (int i = 0; i < nBeds; i++) {
			Resource res = factory.getResourceInstance(code + " Bed " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtBed);
		}
		final int nDoctors = ((Integer)params.get(Parameters.NDOCTORS)).intValue();
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(code + " Doctor " + i);
			res.addTimeTableEntry(doctorFirstCycle, new TimeStamp(TimeUnit.HOUR, hoursFirst), rtDoctorFirst);
			res.addTimeTableEntry(doctorSuccCycle, new TimeStamp(TimeUnit.HOUR, HospitalModelTools.WORKHOURS - hoursFirst), rtDoctorSucc);
		}
		final int nSurgeons = ((Integer)params.get(Parameters.NSURGEONS)).intValue();
		for (int i = 0; i < nSurgeons; i++) {
			Resource res = factory.getResourceInstance(code + " Surgeon " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtSurgeon);
		}
		final int nScrubNurses = ((Integer)params.get(Parameters.NSCRUBNURSES)).intValue();
		for (int i = 0; i < nScrubNurses; i++) {
			Resource res = factory.getResourceInstance(code + " Scrub Nurse " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtScrubNurse);
		}
		
		final int nSurgeries = ((Integer)params.get(Parameters.NSURGERIES)).intValue();
		for (int i = 0; i < nSurgeries; i++) {
			Resource res = factory.getResourceInstance(code + " Surgery " + i);
			res.addTimeTableEntry(HospitalModelTools.getStdMaterialResourceCycle(simul), HospitalModelTools.getStdMaterialResourceAvailability(simul), rtSurgery);
		}
		final int nSurgAssist = ((Integer)params.get(Parameters.NSURGERY_ASSIST)).intValue();
		// Surgery assistants are doubled because can be used in two surgeries at the same time
		for (int i = 0; i < nSurgAssist; i++) {
			Resource res = factory.getResourceInstance(code + " Surgery Assistant" + i + "a");
			res.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtSurgeryAssist);
			Resource res2 = factory.getResourceInstance(code + " Surgery Assistant" + i + "b");
			res2.addTimeTableEntry(HospitalModelTools.getStdHumanResourceCycle(simul), HospitalModelTools.getStdHumanResourceAvailability(simul), rtSurgeryAssist);
		}

		// Workgroups
		WorkGroup wgBed = factory.getWorkGroupInstance(new ResourceType[] {rtBed}, new int[] {1});
		WorkGroup wgDocFirst = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorFirst}, new int[] {1});
		WorkGroup wgDocSucc = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorSucc}, new int[] {1});
		WorkGroup wgSur = factory.getWorkGroupInstance(new ResourceType[] {rtSurgery, rtSurgeon, rtScrubNurse, rtSurgeryAssist, SurgicalSubModel.getAnaesthetistRT()}, new int[] {1, 1, 1, 1, 1});
		WorkGroup wgDummy = factory.getWorkGroupInstance(new ResourceType[] {}, new int[] {});

		// Time Driven Activities
		TimeDrivenActivity actFirstApp = factory.getTimeDrivenActivityInstance("1st Out App");
		actFirstApp.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_OP1), wgDocFirst);
		TimeDrivenActivity actDelaySucc = factory.getTimeDrivenActivityInstance("Waiting for next appointment");
		actDelaySucc.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_OP2OP), wgDummy);
		TimeDrivenActivity actSuccApp = factory.getTimeDrivenActivityInstance("Successive Out App");
		actSuccApp.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_OP2), wgDocSucc);
		TimeDrivenActivity actDelayPostSur = factory.getTimeDrivenActivityInstance("Waiting for post-surgery appointment");
		actDelayPostSur.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2POP), wgDummy);
		TimeDrivenActivity actPostSurApp = factory.getTimeDrivenActivityInstance("Post-Surgery App");
		actPostSurApp.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_POP), wgDocSucc);
		
		TimeDrivenActivity actSurgery = factory.getTimeDrivenActivityInstance("Surgery");
		actSurgery.addWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SUR), wgSur);

		// Flows
		// Let's do that patients can directly go to the main loop (just to avoid warmup)
		ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		SingleFlow first = (SingleFlow)factory.getFlowInstance("SingleFlow", actFirstApp);
		ExclusiveChoiceFlow firstDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		first.link(firstDecision);
		root.link(first, 0.15);
		
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labTest = CentralLabSubModel.getFlow(factory); 
		testDecision.addBranch((InitializerFlow)labTest[0], (FinalizerFlow)labTest[1], new PercentageCondition(90));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", CentralServicesSubModel.getActHae()), 
				new PercentageCondition(70));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", CentralServicesSubModel.getActCar()), 
				new PercentageCondition(20));
		testDecision.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", CentralServicesSubModel.getActRad()), 
				new PercentageCondition(40));
		
		SingleFlow delaySucc = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelaySucc);
		testDecision.link(delaySucc);
		SingleFlow succ = (SingleFlow)factory.getFlowInstance("SingleFlow", actSuccApp);
		delaySucc.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", testDecision, succ, (TimeFunction)params.get(Parameters.ITERSUCC));
		firstDecision.link(mainLoop, new PercentageCondition(80));
		root.link(mainLoop, 0.85);
		ExclusiveChoiceFlow secondDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		mainLoop.link(secondDecision);
		
		// FIXME: 3-Phase simulation doesn't work with this
		// Flow Driven Activities
		FlowDrivenActivity actStay = factory.getFlowDrivenActivityInstance("Stay in bed");
		SingleFlow surgicalPhase = (SingleFlow)factory.getFlowInstance("SingleFlow", actStay);
		secondDecision.link(surgicalPhase, new PercentageCondition(50));
		SingleFlow surgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PACU), et);
		SurgicalSubModel.addICUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ICU), et);
		SingleFlow PACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		surgery.link(PACU);
		ProbabilitySelectionFlow afterSurgery = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		PACU.link(afterSurgery);
		SingleFlow ICU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActICU());
		afterSurgery.link(ICU, 0.1);
		TimeDrivenActivity actPostSur = factory.getTimeDrivenActivityInstance("Recovering after Surgery");
		SimulationTimeFunction postICUduration = (SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2EXIT);
		actPostSur.addWorkGroup(postICUduration, wgDummy);
		SingleFlow postICU = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSur);
		ICU.link(postICU);
		SingleFlow resurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);
		afterSurgery.link(resurgery, 0.1);
		resurgery.link(PACU);
		SingleFlow postOp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSur);
		afterSurgery.link(postOp, 0.8);
		SynchronizationFlow afterOpSync = (SynchronizationFlow)factory.getFlowInstance("SynchronizationFlow");
		postICU.link(afterOpSync);
		postOp.link(afterOpSync);
				
		actStay.addWorkGroup(surgery, afterOpSync, wgBed);
		SingleFlow delayPostSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPostSur);
		surgicalPhase.link(delayPostSur);
		SingleFlow postSurApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostSur.link(postSurApp);
				
		TimeFunction nPatients = (TimeFunction)params.get(Parameters.NPATIENTS);
		ElementCreator ec = factory.getElementCreatorInstance(nPatients, et, root);
		SimulationCycle interArrival = (SimulationCycle)params.get(Parameters.INTERARRIVAL);
		factory.getTimeDrivenGeneratorInstance(ec, interArrival);

	}
}
