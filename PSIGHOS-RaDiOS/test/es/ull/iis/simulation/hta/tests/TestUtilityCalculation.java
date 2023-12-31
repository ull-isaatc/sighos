package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.params.BasicConfigParams;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.ConstantTimeToEventCalculator;
import es.ull.iis.simulation.hta.progression.condition.PreviousDiseaseProgressionCondition;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.Listener;

public class TestUtilityCalculation {  
    public enum TESTS {
        DISUTILITY,
        UTILITY
    }
    private final TestArguments arguments = new TestArguments();
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream ();

    @BeforeEach
    public void setUp() {
        arguments.nRuns = 0;
        arguments.nPatients = 1;
        arguments.singlePatientOutput = 0;
    }

    @Test
    @DisplayName("Test if utilities stored as disutilities are computed fine in a disease")
    public void testDiseaseDisutility() {
        arguments.example = TESTS.DISUTILITY;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("Test if utilities stored as utilities are computed fine in a disease")
    public void testDiseaseUtility() {
        arguments.example = TESTS.UTILITY;
        try {
            final HTAExperiment exp = new BasicDiseaseExperiment(arguments, baos);
            exp.run();
            System.out.println(baos.toString());
        } catch (MalformedSimulationModelException e) {
            e.printStackTrace();
        }
    }


    public static class BasicDiseaseExperiment extends HTAExperiment {
        private final TESTS example;
        public BasicDiseaseExperiment(TestArguments arguments, ByteArrayOutputStream simResult) throws MalformedSimulationModelException {
            super(arguments, simResult);
            this.example = arguments.example;
        }

        @Override
        public ArrayList<InfoReceiver> createAdditionalListeners(int id) {
            ArrayList<InfoReceiver> listeners = new ArrayList<InfoReceiver>();
            listeners.add(new UtilitiesChecker());
            return listeners;
        }
        
        @Override
        public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
            return new BasicDiseaseModel(this, ((TestArguments)arguments).example);
        }

        /**
         * @return the example
         */
        public TESTS getExample() {
            return example;
        }
    }
    public static class BasicDiseaseModel extends HTAModel {
        private final TESTS example;

        public BasicDiseaseModel(BasicDiseaseExperiment experiment, TESTS example) {
            super(experiment);
            this.example = example;
            final Disease disease = new BasicDisease(this);
            try {
                new TestPopulation(this, disease);
            } catch (MalformedSimulationModelException e) {
                e.printStackTrace();
            }
			new DoNothingIntervention(this);
        }

        /**
         * @return the example
         */
        public TESTS getExample() {
            return example;
        }
    }

    public static class BasicDisease extends Disease {
        public static final int YEARS_AMONG_ACUTE_MANIFESTATIONS = 10;
        public static final int YEARS_TO_MANIFESTATION1 = 1;
        public static final int YEARS_TO_MANIFESTATION2 = 1;
        public static final double DISUTILITY = 0.1;
        /**
         * @param model Repository with common information about the disease 
         */
        public BasicDisease(BasicDiseaseModel model) {
            super(model, "D0", "Test disease 0");
            final DiseaseProgression manif1 = new TestChronicManifestation(model, this, "MANIF1", 100, 0.2);
            new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to chronic manifestation 1", manif1,
                new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION1));
                final DiseaseProgression manif2 = new TestChronicManifestation(model, this, "MANIF2", 1000, 0.5);
                final Condition<DiseaseProgressionPathway.ConditionInformation> cond = new PreviousDiseaseProgressionCondition(manif1);
            new DiseaseProgressionPathway(model, "PATHWAY1_2", "Pathway from chronic manifestaion 1 to chronic manifestation 2", manif2, 
                new ConstantTimeToEventCalculator(YEARS_TO_MANIFESTATION2), cond); 
            addExclusion(manif2, manif1);
        }

        @Override
        public void createParameters() {
            if (((BasicDiseaseModel)model).getExample().equals(TESTS.UTILITY))
                addUsedParameter(StandardParameter.ANNUAL_UTILITY, "", "Test", BasicConfigParams.DEF_U_GENERAL_POP - DISUTILITY);
            else
                addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DISUTILITY);
        }
    }

    public static class TestChronicManifestation extends DiseaseProgression {
        private final double annualCost;
        private final double disutility;

        /**
         * @param model
         * @param disease
         */
        public TestChronicManifestation(BasicDiseaseModel model, Disease disease, String name, double annualCost, double disutility) {
            super(model, name, "Chronic manifestation of test disease", disease, Type.CHRONIC_MANIFESTATION);
            this.annualCost = annualCost;
            this.disutility = disutility;
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ANNUAL_COST, "", "Test", HTAModel.getStudyYear(), annualCost);
            addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", disutility);
        }

    }

    public static class UtilitiesChecker extends Listener {
        final private static double DELTA = 0.0001;
        public UtilitiesChecker() {
            super("Disease 0 listener");
	    	addGenerated(PatientInfo.class);
    		addEntrance(PatientInfo.class);
            addEntrance(SimulationStartStopInfo.class);
        }

        @Override
        public void infoEmited(SimulationInfo info) {
            if (info instanceof PatientInfo) {
                if (((PatientInfo) info).getType().equals(PatientInfo.Type.START)) {
                    final PatientInfo pInfo = (PatientInfo) info;
                    final Patient pat = pInfo.getPatient();
                    assertTrue(pat.getDisease().getUsedParameterValue(StandardParameter.ANNUAL_DISUTILITY, pat) - BasicDisease.DISUTILITY < DELTA, "Disutility not computed correctly");
                    assertTrue(pat.getDisease().getUsedParameterValue(StandardParameter.ANNUAL_UTILITY, pat) - pat.getSimulation().getModel().getPopulation().getBaseUtility(pat) - BasicDisease.DISUTILITY < DELTA, "Utility not computed correctly");
                }
            }
            // else if (info instanceof SimulationStartStopInfo) {
            //     if (((SimulationStartStopInfo) info).getType() == SimulationStartStopInfo.Type.END) {
            //         assertEquals(0, expectedEvents.size(), "Missing events: " + expectedEvents.size());
            //     }
            // }
        }
    }

    public static class TestArguments extends CommonArguments {
		@Parameter(names = { "--example", "-ex" }, description = "Example to test with", order = 3)
		public TESTS example = TESTS.UTILITY;
	}

    
}