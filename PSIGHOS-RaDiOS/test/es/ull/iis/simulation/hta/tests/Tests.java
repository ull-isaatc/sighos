package es.ull.iis.simulation.hta.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import es.ull.iis.ontology.radios.json.schema4simulation.Manifestation;
import es.ull.iis.ontology.radios.json.schema4simulation.Schema4Simulation;
import es.ull.iis.simulation.hta.radios.RadiosRepository;
import es.ull.iis.simulation.hta.radios.transforms.ValueTransform;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;

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
	
	private Schema4Simulation loadDiseaseFromJson (String pathToRaDiOSJson) throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		return getMapperInstance().readValue(new File(pathToRaDiOSJson), Schema4Simulation.class);
	}
	
	// @Test	
	public void loadJsonDisease () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		
		try {
			Schema4Simulation schema4Simulation = loadDiseaseFromJson("/home/davidpg/workspace/java/RaDiOS-MTT/radios.json");
			getMapperInstance().writeValueAsString(schema4Simulation);
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished ...");
	}
	
	// @Test 
	public void parseDatatable () throws JsonParseException, JsonMappingException, MalformedURLException, IOException {
		boolean expectedResult = true;
		boolean result = true;
		
		try {			
			Schema4Simulation schema4Simulation = loadDiseaseFromJson("/home/davidpg/workspace/java/RaDiOS-MTT/radios.json");
			RadiosRepository repository = new RadiosRepository(1, 1, schema4Simulation, 10, true);
			for (Manifestation m : schema4Simulation.getDisease().getDevelopments().get(0).getManifestations()) {
				if (m.getProbability() != null && m.getProbability().length() > 50) {
					System.out.println(String.format("Parseando la probabilidad de %s: %s...", m.getName(), m.getProbability().substring(0, 50)));
					Object[][] datatable = ValueTransform.rangeDatatableToMatrix(XmlTransform.getDataTable(m.getProbability()), repository);
					for (int i = 0; i < datatable.length; i++) {
						for (int j = 0; j < datatable[i].length; j++) {
							System.out.print(String.format("\t %s", datatable[i][j]));							
						}						
					}
					System.out.println("");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}
		
		assertEquals(expectedResult, result);
		System.out.println("Test finished ...");
	}

//	@Test	
	public void simulateDiseaseSCD () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		int nRuns = 10;
		int nPatients = 1;
		int timeHorizont = 10;
		
		try {
			new RadiosRepository(nRuns, nPatients, "/home/davidpg/workspace/java/sighos-radios/PSIGHOS-RaDiOS/resources/radios.json", timeHorizont, true);			
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("\nTest finished ...");
	}
	
//	@Test
	public void simulateTest1Disease () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		int nRuns = 10;
		int nPatients = 1;
		int timeHorizont = 10;
		
		try {
			new RadiosRepository(nRuns, nPatients, "/home/davidpg/workspace/java/sighos-radios/PSIGHOS-RaDiOS/resources/radios-test_disease1.json", timeHorizont, true);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("\nTest finished ...");
	}
	
	public static void main(String[] args) {
		System.out.println(Math.floor(12.435));
		System.out.println(Math.ceil(12.435));
	}
	
}
