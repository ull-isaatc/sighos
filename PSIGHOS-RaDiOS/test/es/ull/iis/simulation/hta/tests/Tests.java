package es.ull.iis.simulation.hta.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.ull.iis.ontology.radios.json.schema4simulation.Intervention;
import es.ull.iis.ontology.radios.json.schema4simulation.Schema4Simulation;
import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.simulation.hta.DiseaseProgressionSimulation;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import es.ull.iis.simulation.hta.populations.Population;
import es.ull.iis.simulation.hta.radios.RadiosDisease;
import es.ull.iis.simulation.hta.radios.RadiosIntervention;
import es.ull.iis.simulation.hta.radios.RadiosRepository;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;
import es.ull.iis.simulation.hta.simpletest.TestAcuteManifestation1;
import es.ull.iis.simulation.hta.simpletest.TestPopulation;

public class Tests {
	private static ObjectMapper mapper = null; 
	
	private static ObjectMapper getMapperInstance() {
		if (mapper == null) {
			mapper = new ObjectMapper()
					.enable(SerializationFeature.INDENT_OUTPUT)
					.setSerializationInclusion(Include.NON_NULL)
					.setSerializationInclusion(Include.NON_EMPTY);
		}
		return mapper;
	}
	
	private Schema4Simulation loadDiseaseFromJson (Boolean fromUrl) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		File radiosJson = new File("/home/davidpg/workspace/java/RaDiOS-MTT/radios.json");
		if (fromUrl) {
			return getMapperInstance().readValue(new URL("http://some-domains/api/Schema4Simulation.json"), Schema4Simulation.class);
		} else {
			return getMapperInstance().readValue(radiosJson, Schema4Simulation.class);
		}
	}
	
	// @Test	
	public void loadJsonDisease () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		
		try {
			Schema4Simulation schema4Simulation = loadDiseaseFromJson(false);
			getMapperInstance().writeValueAsString(schema4Simulation);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished ...");
	}
	
	// @Test 
	public void parseDatatable () {
		boolean expectedResult = true;
		boolean result = true;
		String datatable = "<rdt:table xmlns:rdt=\"http://www.ull.es/RaDiOS/datatypes\"\n" + 
				"	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				"	xsi:schemaLocation=\"http://www.ull.es/RaDiOS/datatypes datatable.xsd \">\n" + 
				"\n" + 
				"	<rdt:description>Tasas de complicaciones por 100 pacientes-año</rdt:description>\n" + 
				"	<rdt:informationKind>RATE</rdt:informationKind>\n" + 
				"	<rdt:population>100</rdt:population>\n" + 
				"	<rdt:period value=\"1\" kind=\"YEAR\" />\n" + 
				"\n" + 
				"	<rdt:headers>\n" + 
				"		<rdt:headerDefinition text=\"Edad\" uid=\"001\" index=\"AGE\" />\n" + 
				"		<rdt:headerDefinition text=\"Anemia\" uid=\"005\" />\n" + 
				"	</rdt:headers>\n" + 
				"\n" + 
				"	<rdt:colHeaders>\n" + 
				"		<rdt:headerReference uidRef=\"001\" />\n" + 
				"		<rdt:headerReference uidRef=\"005\" />\n" + 
				"	</rdt:colHeaders>\n" + 
				"\n" + 
				"	<rdt:content>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">0-1</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">3,95</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">1-2</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">1,7</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">2-3</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">3,3</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">3-4</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">5,9</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">4-5</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">3,9</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">5-6</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">2</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">6-7</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">8,3</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">7-8</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">3</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">8-9</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">1,9</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"		<rdt:row>\n" + 
				"			<rdt:column type=\"RANGE\">9-10</rdt:column>\n" + 
				"			<rdt:column type=\"NUMBER\">1</rdt:column>\n" + 
				"		</rdt:row>\n" + 
				"	</rdt:content>\n" + 
				"</rdt:table>";
		
		try {			
			ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(datatable));
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished ...");
	}
	
	@Test	
	public void simulateDisease () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		int nRuns = 10;
		int nPatients = 1;
		double timeHorizont = 10.0;
		
		try {
			Schema4Simulation radiosDiseaseInstance = loadDiseaseFromJson(false);
			RadiosRepository repository = new RadiosRepository(nRuns, nPatients);
			RadiosDisease disease = new RadiosDisease(repository, radiosDiseaseInstance.getDisease(), timeHorizont);
			repository.registerDisease(disease);
			Population population = new TestPopulation(repository, disease);
			repository.registerPopulation(population);
			
			if (CollectionUtils.notIsEmpty(radiosDiseaseInstance.getDisease().getInterventions())) {
				for (Intervention intervention : radiosDiseaseInstance.getDisease().getInterventions()) {
					RadiosIntervention radiosIntervention = new RadiosIntervention(repository, intervention, disease.getNaturalDevelopmentName(), timeHorizont, 
							disease.getCostTreatments(), disease.getCostFollowUps(), disease.getCostScreenings(), disease.getCostClinicalDiagnosis()); 
					repository.registerIntervention(radiosIntervention);
					
					Patient patient = generatePatient(disease, radiosIntervention, population, repository);
					
					double a = radiosIntervention.getFullLifeCost(patient);
					System.err.println("Intervention: " + intervention.getName() + " ==> Full life cost: " + a + "\n\n");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("\nTest finished ...");
	}

	private Patient generatePatient (RadiosDisease disease, RadiosIntervention intervention, Population population, SecondOrderParamsRepository repository) {
		DiseaseProgressionSimulation dps = new DiseaseProgressionSimulation(0, intervention, repository, 10);
		Patient pat = new Patient(dps, intervention, population);
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_SplenicSequestration", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_SplenicSequestration_Recurrent", repository, disease));
		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Meningitis", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_PneumococcalSepsis", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Stroke", repository, disease));
//		pat.getDetailedState().add(new TestAcuteManifestation1("#SCD_Manif_Stroke_Recurrent", repository, disease));
		
		return pat;
	}
	
}
