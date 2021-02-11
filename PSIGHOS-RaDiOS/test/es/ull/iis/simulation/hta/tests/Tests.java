package es.ull.iis.simulation.hta.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
		int timeHorizont = 10;
		
		try {
			new RadiosRepository(nRuns, nPatients, "/home/davidpg/workspace/java/RaDiOS-MTT/radios.json", timeHorizont);
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("\nTest finished ...");
	}
}
