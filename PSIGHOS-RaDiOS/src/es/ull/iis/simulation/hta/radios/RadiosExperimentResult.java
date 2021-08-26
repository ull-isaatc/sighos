package es.ull.iis.simulation.hta.radios;

import java.io.ByteArrayOutputStream;

public class RadiosExperimentResult {
	private RadiosSimulationResult simResultSplitted;
	private ByteArrayOutputStream simResult;
	private String prettySavedParams;

	public RadiosExperimentResult(ByteArrayOutputStream simResult, String prettySavedParams, int nSimulations) {
		this.simResultSplitted = RadiosSimulationResult.etl(simResult, nSimulations);
		this.simResult = simResult;
		this.prettySavedParams = prettySavedParams;
	}

	public RadiosSimulationResult getSimResultSplitted() {
		return simResultSplitted;
	}
	
	public ByteArrayOutputStream getSimResult() {
		return simResult;
	}
	
	public String getPrettySavedParams() {
		return prettySavedParams;
	}
}
