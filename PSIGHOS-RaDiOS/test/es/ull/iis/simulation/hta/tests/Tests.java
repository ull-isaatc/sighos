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

import es.tenerife.ull.ontology.radios.json.schema4simulation.Schema4Simulation;
import es.ull.iis.simulation.hta.radios.RadiosDisease;
import es.ull.iis.simulation.hta.radios.RadiosRepository;

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
		File radiosJson = new File("resources/radios.json");
		if (fromUrl) {
			return getMapperInstance().readValue(new URL("http://some-domains/api/Schema4Simulation.json"), Schema4Simulation.class);
		} else {
			return getMapperInstance().readValue(radiosJson, Schema4Simulation.class);
		}
	}
	
	@Test	
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
	
	@Test	
	public void simulateDisease () {
		System.out.println("Starting test ...");

		boolean expectedResult = true;
		boolean result = true;
		int nRuns = 10;
		int nPatients = 1;
		
		try {
			Schema4Simulation radiosDiseaseInstance = loadDiseaseFromJson(false);
			RadiosRepository repository = new RadiosRepository(nRuns, nPatients);
			repository.registerDisease(new RadiosDisease(repository, radiosDiseaseInstance.getDisease()));		
			// registerIntervention(...)
			// registerPopulation(...)
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		}

		assertEquals(expectedResult, result);
		System.out.println("Test finished ...");
	}
}
