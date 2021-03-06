package es.ull.iis.simulation.hta.radios.wrappers;

import es.ull.iis.ontology.radios.Constants;
import es.ull.iis.simulation.hta.params.SecondOrderParamsRepository;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

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

	public RandomVariate getProbabilisticValueInitializedForProbability() {
		if (probabilisticValue == null && deterministicValue != null) {
			if (deterministicValue == 0.0 || deterministicValue == 1.0) {
				return RandomVariateFactory.getInstance("ConstantVariate", getDeterministicValue());
			} else {
				return SecondOrderParamsRepository.getRandomVariateForProbability(getDeterministicValue());
			}
		}
		return probabilisticValue;
	}

	public RandomVariate getProbabilisticValueInitializedForCost() {
		if (probabilisticValue == null && deterministicValue != null) {
			return SecondOrderParamsRepository.getRandomVariateForCost(getDeterministicValue());
		}
		return probabilisticValue;
	}

	public void setProbabilisticValue(RandomVariate probabilisticValue) {
		this.probabilisticValue = probabilisticValue;
	}

	private String getDeterministicValueOrEmpty () {
		return getDeterministicValue() != null ? getDeterministicValue().toString() : Constants.CONSTANT_EMPTY_STRING;
	}
	
	private String getProbabilisticValueOrEmpty () {
		return getProbabilisticValue() != null ? getProbabilisticValue().toString() : Constants.CONSTANT_EMPTY_STRING;
	}
	
	@Override
	public String toString() {
		if (getDeterministicValue() != null && getProbabilisticValue() != null) {
			return String.format("%s#%s", getDeterministicValueOrEmpty(), getProbabilisticValueOrEmpty()); 
		} else if (getDeterministicValue() != null) {
			return String.format("%s", getDeterministicValueOrEmpty()); 
		} else if (getProbabilisticValue() != null) {
			return String.format("%s", getProbabilisticValueOrEmpty()); 
		} else {
			return Constants.CONSTANT_EMPTY_STRING;
		}
	}
}
