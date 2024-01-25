package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.info.PatientInfo;
import es.ull.iis.simulation.hta.interventions.DoNothingIntervention;
import es.ull.iis.simulation.hta.osdi.expressionEvaluators.ExpressionLanguageParameter;
import es.ull.iis.simulation.hta.osdi.expressionEvaluators.JavaluatorParameter;
import es.ull.iis.simulation.hta.params.StandardParameter;
import es.ull.iis.simulation.hta.params.Parameter.ParameterType;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;
import es.ull.iis.simulation.hta.progression.DiseaseProgressionPathway;
import es.ull.iis.simulation.hta.progression.calculator.ParameterBasedTimeToEventCalculator;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartStopInfo;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.Listener;
import es.ull.iis.simulation.model.TimeUnit;

public class TestCalculatedParameters {  
    public enum TESTS {
        JAVALUATOR,
        JEXL
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
    public void testJavaluator() {
        arguments.example = TESTS.JAVALUATOR;
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
    public void testJEXL() {
        arguments.example = TESTS.JEXL;
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
            listeners.add(new ExpressionChecker(this));
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
        public static final int YEARS_TO_MANIFESTATION1 = 50;
        public static final double DISUTILITY = 0.1;
        /** An ad hoc expression used to reduce the time to suffer certain problem according to some attributes of the patient. 
         * The expression divides the years to manifestation by a formula: (LDL * 2 + HDL - ¿female? * 10) */
        private final String expression = "" + YEARS_TO_MANIFESTATION1 + " / (" + TestPopulation.ATTRIBUTE_LDL + " * 2 + " + TestPopulation.ATTRIBUTE_HDL + " - SEX * 10)";

        /**
         * @param model Repository with common information about the disease 
         */
        public BasicDisease(BasicDiseaseModel model) {
            super(model, "D0", "Test disease 0");
            final DiseaseProgression manif1 = new TestChronicManifestation(model, this, "Cholesterol", 100, 0.2);
            new DiseaseProgressionPathway(model, "PATHWAY1",  "Pathway to cholesterol", manif1,
                new ParameterBasedTimeToEventCalculator("tteChol", TimeUnit.YEAR));
        }

        @Override
        public void createParameters() {
            addUsedParameter(StandardParameter.ANNUAL_DISUTILITY, "", "Test", DISUTILITY);
            if (TESTS.JAVALUATOR.equals(((BasicDiseaseModel) model).getExample()))
                model.addParameter(new JavaluatorParameter(model, "tteChol", "Time to cholesterol", 
                    "Assumption", 2024, ParameterType.RISK, expression));
            else
                model.addParameter(new ExpressionLanguageParameter(model, "tteChol", "Time to cholesterol", 
                "Assumption", 2024, ParameterType.RISK, expression));
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

    public static class ExpressionChecker extends Listener {
        final private BasicDiseaseExperiment exp;
        final private long [] expectedTimesToEvent;
        public ExpressionChecker(BasicDiseaseExperiment exp) {
            super("Disease 0 listener");
            this.exp = exp;
            this.expectedTimesToEvent = new long[exp.getNPatients()];
	    	addGenerated(PatientInfo.class);
    		addEntrance(PatientInfo.class);
            addEntrance(SimulationStartStopInfo.class);
        }

        @Override
        public void infoEmited(SimulationInfo info) {
            if (info instanceof PatientInfo) {
                final PatientInfo pInfo = (PatientInfo) info;
                final Patient pat = pInfo.getPatient();
                if (pInfo.getType().equals(PatientInfo.Type.START)) {
                    final int sex = pat.getSex();
                    final double ldl = exp.getModel().getParameterValue(TestPopulation.ATTRIBUTE_LDL, pat);
                    final double hdl = exp.getModel().getParameterValue(TestPopulation.ATTRIBUTE_HDL, pat);
                    double timeTo = BasicDisease.YEARS_TO_MANIFESTATION1 / (ldl * 2 + hdl - sex * 10);
                    expectedTimesToEvent[pat.getIdentifier()] = pat.getTs() + pat.getSimulation().getTimeUnit().convert(timeTo, TimeUnit.YEAR);
                }
                else if (pInfo.getType().equals(PatientInfo.Type.START_MANIF)) {
                    assertEquals(expectedTimesToEvent[pat.getIdentifier()], pInfo.getTs(), "Event time does not match");
                }
                else if (pInfo.getType().equals(PatientInfo.Type.DEATH)) {
                    assertTrue(pat.getState().contains(exp.getModel().getDiseaseProgression("Cholesterol")), "The event never happened. Probably the time to event was not computed properly.");
                }
            }
        }
    }

    public static class TestArguments extends CommonArguments {
		@Parameter(names = { "--example", "-ex" }, description = "Example to test with", order = 3)
		public TESTS example = TESTS.JEXL;
	}

    
}