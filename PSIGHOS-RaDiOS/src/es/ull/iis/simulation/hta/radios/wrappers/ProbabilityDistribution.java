package es.ull.iis.simulation.hta.radios.wrappers;

import simkit.random.RandomVariate;

public class ProbabilityDistribution {
	private Double deterministicValue;
	private RandomVariate probabilisticValue;

	public ProbabilityDistribution(Double deterministicValue, RandomVariate probabilisticValue) {
		this.deterministicValue = deterministicValue;
		this.probabilisticValue = probabilisticValue;
	}

	public Double getDeterministicValue() {
		return deterministicValue;
	}

	public void setDeterministicValue(Double deterministicValue) {
		this.deterministicValue = deterministicValue;
	}

	public RandomVariate getProbabilisticValue() {
		return probabilisticValue;
	}

	public void setProbabilisticValue(RandomVariate probabilisticValue) {
		this.probabilisticValue = probabilisticValue;
	}

}
