package es.ull.iis.simulation.hta.radios.wrappers;

public class RangeWrapper {
	private Integer index;
	private Double floorLimit;
	private Double ceilLimit;

	public RangeWrapper(Integer index, Double floorLimit, Double ceilLimit) {
		super();
		this.index = index;
		this.floorLimit = floorLimit;
		this.ceilLimit = ceilLimit;
	}

	public Integer getIndex() {
		return index;
	}

	public RangeWrapper setIndex(Integer index) {
		this.index = index;
		return this;
	}

	public Double getFloorLimit() {
		return floorLimit;
	}

	public RangeWrapper setFloorLimit(Double floorLimit) {
		this.floorLimit = floorLimit;
		return this;
	}

	public Double getCeilLimit() {
		return ceilLimit;
	}

	public RangeWrapper setCeilLimit(Double ceilLimit) {
		this.ceilLimit = ceilLimit;
		return this;
	}

	@Override
	public String toString() {
		return String.format("index = %s, floorLimit = %s, ceilLimit = %s", this.index, this.floorLimit, this.ceilLimit);
	}
}