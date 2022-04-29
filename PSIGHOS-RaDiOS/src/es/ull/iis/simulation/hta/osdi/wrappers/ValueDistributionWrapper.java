package es.ull.iis.simulation.hta.osdi.wrappers;

public class ValueDistributionWrapper {
	private Double value;
	private String distribution;
	private Boolean multiplesValues;

	public Double getValue() {
		return value;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public String getDistribution() {
		return distribution;
	}

	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}

	public Boolean getMultiplesValues() {
		return multiplesValues;
	}

	public void setMultiplesValues(Boolean multiplesValues) {
		this.multiplesValues = multiplesValues;
	}
}
