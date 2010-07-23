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
import es.ull.isaatc.simulation.common.SimulationTimeFunction;
import es.ull.isaatc.simulation.common.SimulationWeeklyPeriodicCycle;
import es.ull.isaatc.simulation.common.TimeDrivenActivity;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.TimeUnit;
import es.ull.isaatc.simulation.common.WorkGroup;
import es.ull.isaatc.simulation.common.condition.Condition;
import es.ull.isaatc.simulation.common.condition.ElementTypeCondition;
import es.ull.isaatc.simulation.common.condition.PercentageCondition;
import es.ull.isaatc.simulation.common.factory.SimulationObjectFactory;
import es.ull.isaatc.simulation.common.factory.SimulationUserCode;
import es.ull.isaatc.simulation.common.factory.UserMethod;
import es.ull.isaatc.simulation.common.flow.DoWhileFlow;
import es.ull.isaatc.simulation.common.flow.ExclusiveChoiceFlow;
import es.ull.isaatc.simulation.common.flow.FinalizerFlow;
import es.ull.isaatc.simulation.common.flow.Flow;
import es.ull.isaatc.simulation.common.flow.ForLoopFlow;
import es.ull.isaatc.simulation.common.flow.InitializerFlow;
import es.ull.isaatc.simulation.common.flow.InterleavedRoutingFlow;
import es.ull.isaatc.simulation.common.flow.ProbabilitySelectionFlow;
import es.ull.isaatc.simulation.common.flow.SimpleMergeFlow;
import es.ull.isaatc.simulation.common.flow.SingleFlow;
import es.ull.isaatc.simulation.common.flow.StructuredSynchroMergeFlow;
import es.ull.isaatc.simulation.common.flow.TaskFlow;
import es.ull.isaatc.util.WeeklyPeriodicCycle;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class StdSurgicalSubModel {
	public enum Parameters implements ModelParameterMap.ModelParameter {
		NBEDS(Integer.class, "Beds available"),
		NSBEDS(Integer.class, "Beds available for short surgeries"),
		NSURGERIES(Integer.class, "Surgeries available for the service"),
		NSURGEONS(Integer.class, "Surgeons available for the service"),
		NDOCTORS(Integer.class, "Doctors available for the service"),
		NSCRUBNURSES(Integer.class, "Scrub Nurses available for the service"),
		NSURGERY_ASSIST(Integer.class, "Surgery Assistants: since 1 can be used in two surgeries, it is considered 2 resources"),
		PROB_RAD_OP(Double.class, "Probability of performing an X-Ray test during appointments"),
		PROB_NUC_OP(Double.class, "Probability of performing a scanner test during appointments"),
		PROB_LAB_OP(Double.class, "Probability of performing a lab test during appointments"),
		PROB_LABCENT_OP(Double.class, "Probability of centrifugation of sample during appointments"),
		PROB_LABLAB_OP(Double.class, "Probability of performing a central lab test during appointments"),
		PROB_LABHAE_OP(Double.class, "Probability of performing a Haematology lab test during appointments"),
		PROB_LABMIC_OP(Double.class, "Probability of performing a Microbiology lab test during appointments"),
		PROB_LABPAT_OP(Double.class, "Probability of performing an Anatomopathology lab test during appointments"),
		PROB_RAD_IP(Double.class, "Probability of performing an X-Ray test during stay"),
		PROB_NUC_IP(Double.class, "Probability of performing a scanner test during stay"),
		PROB_LAB_IP(Double.class, "Probability of performing a lab test during stay"),
		PROB_LABCENT_IP(Double.class, "Probability of centrifugation of sample during stay"),
		PROB_LABLAB_IP(Double.class, "Probability of performing a central lab test during stay"),
		PROB_LABHAE_IP(Double.class, "Probability of performing a Haematology lab test during stay"),
		PROB_LABMIC_IP(Double.class, "Probability of performing a Microbiology lab test during stay"),
		PROB_LABPAT_IP(Double.class, "Probability of performing an Anatomopathology lab test during stay"),
		LENGTH_OP1(SimulationTimeFunction.class, "Length of 1st appointment"),
		LENGTH_OP2(SimulationTimeFunction.class, "Length of successive appointment"),
		LENGTH_POP(SimulationTimeFunction.class, "Length of post-surgery appointment"),
		LENGTH_OP2ADM(SimulationTimeFunction.class, "Length between last appointment and admission"),
		LENGTH_OP2OP(SimulationTimeFunction.class, "Time between successive appointments"),
		LENGTH_SUR2POP(SimulationTimeFunction.class, "Time between surgery and post-surgery appointment"),
		LENGTH_SUR(SimulationTimeFunction.class, "Length of surgery"),
		LENGTH_SSUR(SimulationTimeFunction.class, "Length of short surgery"),
		LENGTH_ASUR(SimulationTimeFunction.class, "Length of ambulatory surgery"),
		LENGTH_SPACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay after short surgery"),
		LENGTH_APACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay after ambulatory surgery"),
		LENGTH_PACU(SimulationTimeFunction.class, "Length of P.A.C.U. stay"),
		LENGTH_ICU(SimulationTimeFunction.class, "Length of I.C.U. stay"),
		LENGTH_SUR2EXIT(SimulationTimeFunction.class, "Minimum stay after surgery"),
		LENGTH_SSUR2EXIT(SimulationTimeFunction.class, "Minimum stay after short surgery"),
		PROB_ADM(Double.class, "Probability of being admitted"),
		NPATIENTS(TimeFunction.class, "Number of patients that arrives at the service"),
		INTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients"),
		NAPATIENTS(TimeFunction.class, "Number of patients that arrives at the service for ambulatory surgery"),
		AINTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients for ambulatory surgery"),
		NSPATIENTS(TimeFunction.class, "Number of patients that arrives at the service for short surgery"),
		SINTERARRIVAL(SimulationCycle.class, "Time between successive arrivals of patients for short surgery"),
		ITERSUCC(TimeFunction.class, "Number of successive appointments"),
		PROB_1ST_APP(Double.class, "Probability of a doctor being devoted to first appointment"),
		HOURS_INTERIPTEST(Integer.class, "Time (in hours) between successive IP tests");
		
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

	private static TaskFlow createCyclicIPTests(SimulationObjectFactory factory, String code, ModelParameterMap params) {
		final Condition iPTestCond = new Condition() {
			public boolean check(es.ull.isaatc.simulation.common.Element e) {
				if (e.getVar("maketest").getValue().intValue() != 0)
					return true;
				return false;
			}
		};

		StructuredSynchroMergeFlow iPTests = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labIPTest = CentralLabSubModel.getIPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_IP), (Double)params.get(Parameters.PROB_LABLAB_IP), 
				(Double)params.get(Parameters.PROB_LABHAE_IP), (Double)params.get(Parameters.PROB_LABMIC_IP), (Double)params.get(Parameters.PROB_LABPAT_IP)); 
		iPTests.addBranch((InitializerFlow)labIPTest[0], (FinalizerFlow)labIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_IP) * 100));
		Flow[] nucIPTest = CentralServicesSubModel.getOPNuclearFlow(factory);
		iPTests.addBranch((InitializerFlow)nucIPTest[0], (FinalizerFlow)nucIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_IP) * 100));
		Flow[] radIPTest = CentralServicesSubModel.getOPRadiologyFlow(factory);
		iPTests.addBranch((InitializerFlow)radIPTest[0], (FinalizerFlow)radIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_IP) * 100));
		int hours = (Integer)params.get(Parameters.HOURS_INTERIPTEST);
		SingleFlow waitTilNext = (SingleFlow)factory.getFlowInstance("SingleFlow", HospitalModelConfig.getWaitTilNext(factory, code + " Wait until next test", 
				new TimeStamp(TimeUnit.HOUR, hours), TimeStamp.getZero()));
		waitTilNext.link(iPTests);
		DoWhileFlow iterIPTests = (DoWhileFlow)factory.getFlowInstance("DoWhileFlow", waitTilNext, iPTests, iPTestCond);
		return iterIPTests;
	}
	
	public static void createModel(SimulationObjectFactory factory, String code, ModelParameterMap params) {
		for (Parameters p : Parameters.values())
			if (params.get(p) == null)
				throw new IllegalArgumentException("Param <<" + p + ">> missing");
		
		final Simulation simul = factory.getSimulation();
		// Element types
		ElementType et = factory.getElementTypeInstance(code + " Patient");
		et.addElementVar("maketest", 0);
		ElementType sEt = factory.getElementTypeInstance(code + " Patient (short surgery)");
		sEt.addElementVar("maketest", 0);
		ElementType aEt = factory.getElementTypeInstance(code + " AMB Patient");
		
		// Resource types and standard resources
		ResourceType rtBed = HospitalModelConfig.createNStdMaterialResources(factory, code + " Bed", (Integer)params.get(Parameters.NBEDS)); 
		ResourceType rtSBed = HospitalModelConfig.createNStdMaterialResources(factory, code + " S Bed", (Integer)params.get(Parameters.NSBEDS)); 
		ResourceType rtSurgeon = HospitalModelConfig.createNStdHumanResources(factory, code + " Surgeon", (Integer)params.get(Parameters.NSURGEONS));
		ResourceType rtSurgery = HospitalModelConfig.createNStdMaterialResources(factory, code + " Surgery", (Integer)params.get(Parameters.NSURGERIES));
		ResourceType rtScrubNurse = HospitalModelConfig.createNStdHumanResources(factory, code + " Scrub Nurse", (Integer)params.get(Parameters.NSCRUBNURSES)); 
		ResourceType rtDoctorFirst = factory.getResourceTypeInstance(code + " Doctor 1st");
		ResourceType rtDoctorSucc = factory.getResourceTypeInstance(code + " Doctor Succ");
		ResourceType rtSurgeryAssist = factory.getResourceTypeInstance(code + " Surgery Assistant");
		
		SimulationCycle doctorFirstCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.DAYSTART, 0);
		final double probFirstApp = ((Double)params.get(Parameters.PROB_1ST_APP)).doubleValue();
		long hoursFirst = Math.round(HospitalModelConfig.WORKHOURS.getValue() * probFirstApp);
		SimulationCycle doctorSuccCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.DAYSTART.add(new TimeStamp(TimeUnit.HOUR, hoursFirst)), 0);

		// Resources with non-standard behaviour
		final int nDoctors = ((Integer)params.get(Parameters.NDOCTORS)).intValue();
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(code + " Doctor " + i);
			res.addTimeTableEntry(doctorFirstCycle, new TimeStamp(TimeUnit.HOUR, hoursFirst), rtDoctorFirst);
			res.addTimeTableEntry(doctorSuccCycle, new TimeStamp(TimeUnit.HOUR, HospitalModelConfig.WORKHOURS.getValue() - hoursFirst), rtDoctorSucc);
		}
		final int nSurgAssist = ((Integer)params.get(Parameters.NSURGERY_ASSIST)).intValue();
		// Surgery assistants are doubled because can be used in two surgeries at the same time
		for (int i = 0; i < nSurgAssist; i++) {
			HospitalModelConfig.getStdHumanResource(factory, code + " Surgery Assistant" + i + "a", rtSurgeryAssist);
			HospitalModelConfig.getStdHumanResource(factory, code + " Surgery Assistant" + i + "b", rtSurgeryAssist);
		}

		// Workgroups
		WorkGroup wgBed = factory.getWorkGroupInstance(new ResourceType[] {rtBed}, new int[] {1});
		WorkGroup wgSBed = factory.getWorkGroupInstance(new ResourceType[] {rtSBed}, new int[] {1});
		WorkGroup wgDocFirst = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorFirst}, new int[] {1});
		WorkGroup wgDocSucc = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorSucc}, new int[] {1});
		WorkGroup wgSur = factory.getWorkGroupInstance(new ResourceType[] {rtSurgery, rtSurgeon, rtScrubNurse, rtSurgeryAssist, SurgicalSubModel.getAnaesthetistRT()}, new int[] {1, 1, 1, 1, 1});

		// Time Driven Activities
		TimeDrivenActivity actFirstApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " 1st Out App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP1), wgDocFirst, true);
		TimeDrivenActivity actDelaySucc = HospitalModelConfig.getDelay(factory, code + " Waiting for next appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2OP), false);
		TimeDrivenActivity actSuccApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Successive Out App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2), wgDocSucc, true);
		TimeDrivenActivity actDelayPOP = HospitalModelConfig.getDelay(factory, code + " Waiting for post-surgery appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2POP), false);
		TimeDrivenActivity actDelayAppAdm = HospitalModelConfig.getDelay(factory, code + " Waiting for admission", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2ADM), false);
		TimeDrivenActivity actPostSurApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Post-Surgery App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_POP), wgDocSucc, true);
		TimeDrivenActivity actSurgery = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR), wgSur, true);
		TimeDrivenActivity actShortSurgery = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Short Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SSUR), wgSur, true);
		TimeDrivenActivity actAmbSurgery = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " AMB Surgery", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_ASUR), wgSur, true);
		TimeDrivenActivity actDelayPostSur = HospitalModelConfig.getDelay(factory, code + " Recovering after Surgery",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SUR2EXIT), true);
		TimeDrivenActivity actDelayPostShortSur = HospitalModelConfig.getDelay(factory, code + " Recovering after Short Surgery",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_SSUR2EXIT), true);

		// Flows
		// Let's do that patients can directly go to the main loop (just to avoid warmup)
		ProbabilitySelectionFlow root = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		SingleFlow first = (SingleFlow)factory.getFlowInstance("SingleFlow", actFirstApp);
		ExclusiveChoiceFlow firstDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		first.link(firstDecision);
		root.link(first, 0.15);
		
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labTest = CentralLabSubModel.getOPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_OP), (Double)params.get(Parameters.PROB_LABLAB_OP), (Double)params.get(Parameters.PROB_LABHAE_OP),
				(Double)params.get(Parameters.PROB_LABMIC_OP), (Double)params.get(Parameters.PROB_LABPAT_OP)); 
		testDecision.addBranch((InitializerFlow)labTest[0], (FinalizerFlow)labTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_OP) * 100));
		Flow[] nucTest = CentralServicesSubModel.getOPNuclearFlow(factory);
		testDecision.addBranch((InitializerFlow)nucTest[0], (FinalizerFlow)nucTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_OP) * 100));
		Flow[] radTest = CentralServicesSubModel.getOPRadiologyFlow(factory);
		testDecision.addBranch((InitializerFlow)radTest[0], (FinalizerFlow)radTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_OP) * 100));
		
		InterleavedRoutingFlow beforeSucc = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		beforeSucc.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actDelaySucc));
		beforeSucc.addBranch(testDecision);
		
		SingleFlow succ = (SingleFlow)factory.getFlowInstance("SingleFlow", actSuccApp);
		beforeSucc.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", beforeSucc, succ, (TimeFunction)params.get(Parameters.ITERSUCC));
		firstDecision.link(mainLoop, new PercentageCondition(80));
		root.link(mainLoop, 0.85);
		ExclusiveChoiceFlow admittanceDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow");
		mainLoop.link(admittanceDecision);
		double percAdmission = ((Double)params.get(Parameters.PROB_ADM)).doubleValue() * 100;
		ExclusiveChoiceFlow surgeryTypeDecision = (ExclusiveChoiceFlow)factory.getFlowInstance("ExclusiveChoiceFlow"); 
		admittanceDecision.link(surgeryTypeDecision, new PercentageCondition(percAdmission));
		
		SimulationUserCode userCodeEnableIPTests = new SimulationUserCode();
		userCodeEnableIPTests.add(UserMethod.AFTER_START, "<%SET(@E.maketest, 1)%>;");
		SimulationUserCode userCodeDisableIPTests = new SimulationUserCode();
		userCodeDisableIPTests.add(UserMethod.AFTER_FINALIZE, "<%SET(@E.maketest, 0)%>;");

		// Flow for ordinary surgical patients
		FlowDrivenActivity actStay = factory.getFlowDrivenActivityInstance(code + " Stay in bed");
		SingleFlow surgicalPhase = (SingleFlow)factory.getFlowInstance("SingleFlow", userCodeEnableIPTests, actStay);
		SingleFlow ordinaryAdmission = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayAppAdm);
		SingleFlow surgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_PACU), et);
		SurgicalSubModel.addICUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_ICU), et);
		SingleFlow PACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		surgery.link(PACU);
		ProbabilitySelectionFlow afterSurgery = (ProbabilitySelectionFlow)factory.getFlowInstance("ProbabilitySelectionFlow");
		PACU.link(afterSurgery);
		SingleFlow ICU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActICU());
		afterSurgery.link(ICU, 0.2);
		SingleFlow postICU = (SingleFlow)factory.getFlowInstance("SingleFlow", userCodeDisableIPTests, actDelayPostSur);
		ICU.link(postICU);
		SingleFlow resurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actSurgery);
		afterSurgery.link(resurgery, 0.1);
		resurgery.link(PACU);
		SingleFlow postOp = (SingleFlow)factory.getFlowInstance("SingleFlow", userCodeDisableIPTests, actDelayPostSur);
		afterSurgery.link(postOp, 0.7);
		SimpleMergeFlow afterOpSync = (SimpleMergeFlow)factory.getFlowInstance("SimpleMergeFlow");
		postICU.link(afterOpSync);
		postOp.link(afterOpSync);

		InterleavedRoutingFlow parallelStay = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		ordinaryAdmission.link(parallelStay);
		parallelStay.addBranch(surgery, afterOpSync);
		parallelStay.addBranch(createCyclicIPTests(factory, code, params));
		
		actStay.addWorkGroup(ordinaryAdmission, parallelStay, wgBed);
		SingleFlow delayPostSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		surgicalPhase.link(delayPostSur);
		SingleFlow postSurApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostSur.link(postSurApp);
		surgeryTypeDecision.link(surgicalPhase, new ElementTypeCondition(et));
		
		// Flow for short surgical patients
		FlowDrivenActivity actShortStay = factory.getFlowDrivenActivityInstance(code + " Short stay in bed");
		SingleFlow shortSurgicalPhase = (SingleFlow)factory.getFlowInstance("SingleFlow", userCodeEnableIPTests, actShortStay);
		SingleFlow shortAdmission = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayAppAdm);
		SingleFlow shortSurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actShortSurgery);
		SingleFlow postShortSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPostShortSur);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_SPACU), sEt);
		SingleFlow shortPACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());

		StructuredSynchroMergeFlow iPTests = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labIPTest = CentralLabSubModel.getIPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_IP), (Double)params.get(Parameters.PROB_LABLAB_IP), (Double)params.get(Parameters.PROB_LABHAE_IP),
				(Double)params.get(Parameters.PROB_LABMIC_IP), (Double)params.get(Parameters.PROB_LABPAT_IP)); 
		iPTests.addBranch((InitializerFlow)labIPTest[0], (FinalizerFlow)labIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_IP) * 100));
		Flow[] nucIPTest = CentralServicesSubModel.getOPNuclearFlow(factory);
		iPTests.addBranch((InitializerFlow)nucIPTest[0], (FinalizerFlow)nucIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_IP) * 100));
		Flow[] radIPTest = CentralServicesSubModel.getOPRadiologyFlow(factory);
		iPTests.addBranch((InitializerFlow)radIPTest[0], (FinalizerFlow)radIPTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_IP) * 100));

		InterleavedRoutingFlow parallelShortStay = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		parallelShortStay.addBranch(postShortSur);
		parallelShortStay.addBranch(iPTests);
		
		shortAdmission.link(shortSurgery);
		shortSurgery.link(shortPACU);
		shortPACU.link(parallelShortStay);
				
		actShortStay.addWorkGroup(shortAdmission, parallelShortStay, wgSBed);
		
		SingleFlow delayPostShortSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		SingleFlow postShortSurApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);

		shortSurgicalPhase.link(delayPostShortSur);
		delayPostShortSur.link(postShortSurApp);
		
		surgeryTypeDecision.link(shortSurgicalPhase, new ElementTypeCondition(sEt));

		// Flow for ambulatory patients
		SingleFlow ambSurgery = (SingleFlow)factory.getFlowInstance("SingleFlow", actAmbSurgery);

		SurgicalSubModel.addPACUWorkGroup((SimulationTimeFunction)params.get(Parameters.LENGTH_APACU), aEt);
		SingleFlow ambPACU = (SingleFlow)factory.getFlowInstance("SingleFlow", SurgicalSubModel.getActPACU());
		ambSurgery.link(ambPACU);
				
		SingleFlow delayPostAmbSur = (SingleFlow)factory.getFlowInstance("SingleFlow", actDelayPOP);
		ambPACU.link(delayPostAmbSur);
		SingleFlow postAmbApp = (SingleFlow)factory.getFlowInstance("SingleFlow", actPostSurApp);
		delayPostAmbSur.link(postAmbApp);
		surgeryTypeDecision.link(ambSurgery, new ElementTypeCondition(aEt));
		
		ElementCreator ec = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NPATIENTS), et, root);
		factory.getTimeDrivenGeneratorInstance(ec, (SimulationCycle)params.get(Parameters.INTERARRIVAL));

		ElementCreator sEc = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NSPATIENTS), sEt, root);
		factory.getTimeDrivenGeneratorInstance(sEc, (SimulationCycle)params.get(Parameters.SINTERARRIVAL));

		ElementCreator aEc = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NAPATIENTS), aEt, root);
		factory.getTimeDrivenGeneratorInstance(aEc, (SimulationCycle)params.get(Parameters.AINTERARRIVAL));
	}

	public static void createDeterministicModel(SimulationObjectFactory factory, String code, ModelParameterMap params) {
		final Simulation simul = factory.getSimulation();
		// Element types
		ElementType et = factory.getElementTypeInstance(code + " Patient");
		et.addElementVar("maketest", 0);
		
		// Resource types and standard resources
		ResourceType rtDoctorSucc = factory.getResourceTypeInstance(code + " Doctor Succ");
		
		final double probFirstApp = ((Double)params.get(Parameters.PROB_1ST_APP)).doubleValue();
		long hoursFirst = Math.round(HospitalModelConfig.WORKHOURS.getValue() * probFirstApp);
		SimulationCycle doctorSuccCycle = new SimulationWeeklyPeriodicCycle(simul.getTimeUnit(), 
				WeeklyPeriodicCycle.WEEKDAYS, HospitalModelConfig.DAYSTART.add(new TimeStamp(TimeUnit.HOUR, hoursFirst)), 0);

		// Resources with non-standard behaviour
		final int nDoctors = ((Integer)params.get(Parameters.NDOCTORS)).intValue();
		for (int i = 0; i < nDoctors; i++) {
			Resource res = factory.getResourceInstance(code + " Doctor " + i);
			res.addTimeTableEntry(doctorSuccCycle, new TimeStamp(TimeUnit.HOUR, HospitalModelConfig.WORKHOURS.getValue() - hoursFirst), rtDoctorSucc);
		}

		// Workgroups
		WorkGroup wgDocSucc = factory.getWorkGroupInstance(new ResourceType[] {rtDoctorSucc}, new int[] {1});

		// Time Driven Activities
		TimeDrivenActivity actDelaySucc = HospitalModelConfig.getDelay(factory, code + " Waiting for next appointment", 
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2OP), false);
		TimeDrivenActivity actSuccApp = HospitalModelConfig.createStdTimeDrivenActivity(factory, code + " Successive Out App",
				(SimulationTimeFunction)params.get(Parameters.LENGTH_OP2), wgDocSucc, true);

		// Flows
		// Let's do that patients can directly go to the main loop (just to avoid warmup)
		
		StructuredSynchroMergeFlow testDecision = (StructuredSynchroMergeFlow)factory.getFlowInstance("StructuredSynchroMergeFlow");
		Flow[] labTest = CentralLabSubModel.getOPFlow(factory, (Double)params.get(Parameters.PROB_LABCENT_OP), (Double)params.get(Parameters.PROB_LABLAB_OP), (Double)params.get(Parameters.PROB_LABHAE_OP),
				(Double)params.get(Parameters.PROB_LABMIC_OP), (Double)params.get(Parameters.PROB_LABPAT_OP)); 
		testDecision.addBranch((InitializerFlow)labTest[0], (FinalizerFlow)labTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_LAB_OP) * 100));
		Flow[] nucTest = CentralServicesSubModel.getOPNuclearFlow(factory);
		testDecision.addBranch((InitializerFlow)nucTest[0], (FinalizerFlow)nucTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_NUC_OP) * 100));
		Flow[] radTest = CentralServicesSubModel.getOPRadiologyFlow(factory);
		testDecision.addBranch((InitializerFlow)radTest[0], (FinalizerFlow)radTest[1], new PercentageCondition((Double)params.get(Parameters.PROB_RAD_OP) * 100));
		
		InterleavedRoutingFlow beforeSucc = (InterleavedRoutingFlow)factory.getFlowInstance("InterleavedRoutingFlow");
		beforeSucc.addBranch((SingleFlow)factory.getFlowInstance("SingleFlow", actDelaySucc));
		beforeSucc.addBranch(testDecision);
		
		SingleFlow succ = (SingleFlow)factory.getFlowInstance("SingleFlow", actSuccApp);
		beforeSucc.link(succ);

		ForLoopFlow mainLoop = (ForLoopFlow)factory.getFlowInstance("ForLoopFlow", beforeSucc, succ, (TimeFunction)params.get(Parameters.ITERSUCC));
		
		ElementCreator ec = factory.getElementCreatorInstance((TimeFunction)params.get(Parameters.NPATIENTS), et, mainLoop);
		factory.getTimeDrivenGeneratorInstance(ec, (SimulationCycle)params.get(Parameters.INTERARRIVAL));
	}
	
}
