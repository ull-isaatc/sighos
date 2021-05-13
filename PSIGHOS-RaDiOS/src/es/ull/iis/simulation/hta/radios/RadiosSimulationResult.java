package es.ull.iis.simulation.hta.radios;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

public class RadiosSimulationResult {
	private StringBuilder deterministicDataSheet = new StringBuilder();
	private StringBuilder probabilisticDataSheet = new StringBuilder();
	private List<RadiosIncidenceSimulationResult> incidenceDataSheet = new ArrayList<>();

	public RadiosSimulationResult() {
	}

	public StringBuilder getDeterministicDataSheet() {
		return deterministicDataSheet;
	}

	public StringBuilder getProbabilisticDataSheet() {
		return probabilisticDataSheet;
	}

	public List<RadiosIncidenceSimulationResult> getIncidenceDataSheet() {
		return incidenceDataSheet;
	}

	public static RadiosSimulationResult etl (ByteArrayOutputStream baos, int probabilisticSimulations) {
		RadiosSimulationResult result = new RadiosSimulationResult();
//	   try (InputStreamReader reader = new InputStreamReader(new ByteArrayInputStream(baos.toByteArray()));
//			  BufferedReader bufferedReader = new BufferedReader(reader);) {
//			
//			String strCurrentLine = Constants.CONSTANT_EMPTY_STRING;
//
//			// Las dos primeras lineas son de la simulacion determinista
//			result.getDeterministicDataSheet().append(bufferedReader.readLine()).append("\n");
//			result.getDeterministicDataSheet().append(bufferedReader.readLine()).append("\n");
//
//			// Las <probabilisticSimulations + 1> siguientes líneas pertenecen la simulación probabilística
//			if (probabilisticSimulations > 0) {
//				int lineCounter = 0;
//				while (lineCounter <= probabilisticSimulations && (strCurrentLine = bufferedReader.readLine()) != null) {
//					lineCounter++;
//					result.getProbabilisticDataSheet().append(strCurrentLine).append("\n");
//				}
//			}
//			
//			if (strCurrentLine != null) {
//				// Eliminar líneas en blanco
//				while (strCurrentLine.trim().equals(Constants.CONSTANT_EMPTY_STRING) && (strCurrentLine = bufferedReader.readLine()) != null);
//				
//				if (strCurrentLine != null) {
//					strCurrentLine = bufferedReader.readLine(); 			// Línea de separación
//					String incidenceName = bufferedReader.readLine(); 	// Línea de separación
//					strCurrentLine = bufferedReader.readLine(); 			// Línea de separación
//					strCurrentLine = bufferedReader.readLine(); 			// Línea a descartar
//					StringBuilder tmp = new StringBuilder();
//					while (!strCurrentLine.trim().equals(Constants.CONSTANT_EMPTY_STRING) && (strCurrentLine = bufferedReader.readLine()) != null) {
//						tmp.append(strCurrentLine).append("\n");			// Líneas con valores de incidencia
//					}				
//					result.getIncidenceDataSheet().add(new RadiosIncidenceSimulationResult(incidenceName, tmp.toString())); 
//				}
//	
//				// Eliminar líneas en blanco
//				while (strCurrentLine.trim().equals(Constants.CONSTANT_EMPTY_STRING) && (strCurrentLine = bufferedReader.readLine()) != null);
//				
//				if (strCurrentLine != null) {
//					String incidenceName = bufferedReader.readLine(); 	// Línea de separación
//					strCurrentLine = bufferedReader.readLine(); 			// Línea de separación
//					strCurrentLine = bufferedReader.readLine(); 			// Línea a descartar
//					StringBuilder tmp = new StringBuilder();
//					while (!strCurrentLine.trim().equals(Constants.CONSTANT_EMPTY_STRING) && (strCurrentLine = bufferedReader.readLine()) != null) {
//						tmp.append(strCurrentLine).append("\n");			// Líneas con valores de incidencia
//					}				
//					result.getIncidenceDataSheet().add(new RadiosIncidenceSimulationResult(incidenceName, tmp.toString())); 
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		return result;
	}	
}
