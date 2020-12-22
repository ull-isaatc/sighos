package es.ull.iis.simulation.hta.radios.wrappers;

public class IntervalDistribution {
	private Double lowerLimit;
	private Double upperLimit;
	private ProbabilityDistribution probabilityDistribution;

	public IntervalDistribution(Double lowerLimit, Double upperLimit, ProbabilityDistribution probabilityDistribution) {
		this.lowerLimit = lowerLimit;
		this.upperLimit = upperLimit;
		this.probabilityDistribution = probabilityDistribution;
	}

	public Double getLowerLimit() {
		return lowerLimit;
	}

	public void setLowerLimit(Double lowerLimit) {
		this.lowerLimit = lowerLimit;
	}

	public Double getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(Double upperLimit) {
		this.upperLimit = upperLimit;
	}

	public ProbabilityDistribution getProbabilityDistribution() {
		return probabilityDistribution;
	}

	public void setProbabilityDistribution(ProbabilityDistribution probabilityDistribution) {
		this.probabilityDistribution = probabilityDistribution;
	}

}
