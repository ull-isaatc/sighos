package es.ull.iis.simulation.hta.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import com.beust.jcommander.Parameter;

import es.ull.iis.simulation.hta.CommonArguments;
import es.ull.iis.simulation.hta.HTAExperiment.MalformedSimulationModelException;
import es.ull.iis.simulation.hta.HTAExperiment;
import es.ull.iis.simulation.hta.HTAModel;
import es.ull.iis.simulation.hta.osdi.OSDiGenericModel;
import es.ull.iis.simulation.hta.osdi.generators.DiseaseProgressionTemplate;
import es.ull.iis.simulation.hta.progression.Disease;
import es.ull.iis.simulation.hta.progression.DiseaseProgression;

@Tag("loadOntology")
@DisplayName("Test the loading of different model items from the ontology")
@TestInstance(Lifecycle.PER_CLASS)
public class TestLoadOntology {
    private HTAExperiment exp;
    private HTAModel model;
	private final ByteArrayOutputStream baos = new ByteArrayOutputStream ();
    private final TestArguments arguments = new TestArguments();

    @BeforeAll
    public void setUp() throws MalformedSimulationModelException {
        
        exp = new BasicTestExperiment(arguments, baos);
        model = exp.getModel();
    }

    @Test
    @DisplayName("Test if the model can load basic properties of the diseases")
    public void testDisease() {        
        final Disease[] diseases = model.getRegisteredDiseases();
        assertAll("Disease: ",
            () -> assertTrue(diseases.length == 2, "There should be exactly two diseases"),
            () -> assertTrue(diseases[0].name().equals("HEALTHY"), "First disease should be HEALTHY. Found " + diseases[0].name() + " instead"),
            () -> assertTrue(diseases[1].name().equals("T1DM_Disease"), "Second disease should be T1DM_Disease. Found " + diseases[1].name() + " instead")
        );
    }

    @Test
    @DisplayName("Test if the model can load basic properties of the disease progressions")
    public void testDiseaseProgressions() {
        for (DiseaseProgressionTemplate prog : DiseaseProgressionTemplate.values()) {
            final DiseaseProgression progression = model.getDiseaseProgression(prog.getInstanceIRI());
            assertTrue(progression != null);
            switch (prog.getType()) {
                case ACUTE_MANIFESTATION:
                    assertTrue(progression.getType().equals(DiseaseProgression.Type.ACUTE_MANIFESTATION), progression.name() + " should be of type ACUTE_MANIFESTATION");
                    break;
                case CHRONIC_MANIFESTATION:
                    assertTrue(progression.getType().equals(DiseaseProgression.Type.CHRONIC_MANIFESTATION), progression.name() + " should be of type CHRONIC_MANIFESTATION");
                    break;
                case STAGE:
                    assertTrue(progression.getType().equals(DiseaseProgression.Type.STAGE), progression.name() + " should be of type STAGE");
                    break;
                default:
                    break;
            }
        }
    }

    @Test
    @DisplayName("Test if the model can load basic properties of some parameters")
    public void testParameters() {
        Map<String, es.ull.iis.simulation.hta.params.Parameter> parameters = model.getParameters();
        assertAll("Parameters: ",
            () -> assertTrue(parameters.containsKey(arguments.owlPrefix + "BETA_Manif_NEU"), "This parameter should be created even since it is used by a Calculated Parameter"),
            () -> assertEquals(parameters.get(arguments.owlPrefix + "BETA_Manif_NEU").getType(), es.ull.iis.simulation.hta.params.Parameter.ParameterType.RISK),
            () -> assertTrue(parameters.containsKey(arguments.owlPrefix + "DCCT1_ComplicationsFree_U")),
            () -> assertEquals(parameters.get(arguments.owlPrefix + "DCCT1_ComplicationsFree_U").getType(), es.ull.iis.simulation.hta.params.Parameter.ParameterType.UTILITY),
            () -> assertTrue(!parameters.containsKey(arguments.owlPrefix + "Stage_CHD_IncidenceBase_RR_U95CI"), "This parameter exists in the ontology but should not be created as an independent parameter"),
            () -> assertTrue(parameters.containsKey(arguments.owlPrefix + "Manif_HF_AC")),
            () -> assertEquals(parameters.get(arguments.owlPrefix + "Manif_HF_AC").getType(), es.ull.iis.simulation.hta.params.Parameter.ParameterType.COST)
        );
    }
    
    private class BasicTestExperiment extends HTAExperiment {
        public BasicTestExperiment(TestArguments arguments, ByteArrayOutputStream byteArrayOutputStream) throws MalformedSimulationModelException {
            super(arguments, byteArrayOutputStream);
        }

        @Override
        public HTAModel createModel(CommonArguments arguments) throws MalformedSimulationModelException {
            HTAModel model = null;
            try {
                model = new OSDiGenericModel(this, ((TestArguments)arguments).owlPath, ((TestArguments)arguments).owlModelIRI, ((TestArguments)arguments).owlPrefix);
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
            return model;
        }
    }

    public static class TestArguments extends CommonArguments {
		@Parameter(names = { "--owlpath", "-Op" }, description = "Path to the owl file", order = 3)
		public String owlPath = System.getProperty("user.dir") + "\\resources\\OSDi_test.owl";
		@Parameter(names = { "--owlmodel", "-Om" }, description = "Model IRI in the owl file", order = 3)
		public String owlModelIRI = "StdModelDES";
		@Parameter(names = { "--owlprefix", "-Ox" }, description = "Prefix to model item instances in the owl file", order = 3)
		public String owlPrefix = "T1DM_";
	}
    
}
