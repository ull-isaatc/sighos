package es.ull.iis.simulation.hta.radios;

import java.io.ByteArrayOutputStream;

import es.ull.iis.simulation.hta.progression.Transition;

public class RadiosExperimentResult {
	private Transition[] transitions;
	private RadiosSimulationResult simResultSplitted;
	private ByteArrayOutputStream simResult;
	private String prettySavedParams;

	public RadiosExperimentResult(Transition[] transitions, ByteArrayOutputStream simResult, String prettySavedParams, int nSimulations) {
		this.simResultSplitted = RadiosSimulationResult.etl(simResult, nSimulations);
		this.simResult = simResult;
		this.transitions = transitions;
		this.prettySavedParams = prettySavedParams;
	}

	public RadiosSimulationResult getSimResultSplitted() {
		return simResultSplitted;
	}
	
	public ByteArrayOutputStream getSimResult() {
		return simResult;
	}

	public Transition[] getTransitions() {
		return transitions;
	}
	
	public String getPrettySavedParams() {
		return prettySavedParams;
	}
}
