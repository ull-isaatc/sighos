package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.List;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

public class CostMatrixElement {
	// Stores the lower limit of the range (in years)
	private Double floorLimitRange;
	// Stores the upper limit of the range (in years)
	private Double ceilLimitRange;
	// Stores the frequency of occurrence of the event within the range (in years)
	private Double frequency;
	// Stores the conditions under which a cost is applied
	private String condition;
	// Stores the expression for the cost calculation
	private String costExpression;
	// Stores the cost value
	private Double cost;
	// Stores the distribution function for cost
	private RandomVariate distribution;
	// If the cost is applied in discrete instants of time, those discrete instants are stored in this array
	private List<TimeCostEvent> timesCostsEvents;
	// Stores the year in which the cost evidence is collected
	private Integer year;
	
	public CostMatrixElement() {
		this.timesCostsEvents = new ArrayList<>();
	}

	public CostMatrixElement(Double floorLimitRange, Double ceilLimitRange, Double frequency, String condition, String costExpression, Double cost, List<TimeCostEvent> timesCostsEvents, Integer year, RandomVariate distribution) {
		this.floorLimitRange = floorLimitRange;
		this.ceilLimitRange = ceilLimitRange;
		this.frequency = frequency;
		this.condition = condition;
		this.costExpression = costExpression;
		this.cost = cost;
		this.timesCostsEvents = timesCostsEvents;
		this.year = year;
		this.distribution = distribution;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}

	public String getCostExpression() {
		return costExpression;
	}

	public void setCostExpression(String costExpression) {
		this.costExpression = costExpression;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public List<TimeCostEvent> getTimesCostsEvents() {
		return timesCostsEvents;
	}

	public void setTimesCostsEvents(List<TimeCostEvent> timesCostsEvents) {
		this.timesCostsEvents = timesCostsEvents;
	}

	public Double getFloorLimitRange() {
		return floorLimitRange;
	}

	public void setFloorLimitRange(Double floorLimitRange) {
		this.floorLimitRange = floorLimitRange;
	}

	public Double getCeilLimitRange() {
		return ceilLimitRange;
	}

	public void setCeilLimitRange(Double ceilLimitRange) {
		this.ceilLimitRange = ceilLimitRange;
	}
	
	public Double getFrequency() {
		return frequency;
	}

	public void setFrequency(Double frequency) {
		this.frequency = frequency;
	}
	
	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public RandomVariate getDistribution() {
		return distribution;
	}
	
	public void setDistribution(RandomVariate distribution) {
		this.distribution = distribution;
	}

	public Double calculateNTimesInRange(Double initTimeRange, Double finalTimeRange) {
		double from = this.floorLimitRange;		
		double to = (this.ceilLimitRange != null ? this.ceilLimitRange : Double.MAX_VALUE);		
		if (initTimeRange != null && finalTimeRange != null) {
			if (initTimeRange >= from && initTimeRange <= to) {
				from = initTimeRange;
			}
			if (finalTimeRange <= to) {
				to = finalTimeRange;
			}
		}		
		return Math.ceil(to - from);			
	}
	
	public CostMatrixElement clone() {
		CostMatrixElement obj = new CostMatrixElement();
		obj.setFloorLimitRange(this.getFloorLimitRange() != null ? new Double(this.getFloorLimitRange()) : null);
		obj.setCeilLimitRange(this.getCeilLimitRange() != null ? new Double(this.getCeilLimitRange()) : null);
		obj.setFrequency(this.getFrequency() != null ? new Double(this.getFrequency()) : null);
		obj.setCondition(this.getCondition() != null ? new String(this.getCondition()) : null);
		obj.setCostExpression(this.getCostExpression() != null ? new String(this.getCostExpression()) : null);
		obj.setCost(this.getCost() != null ? new Double(this.getCost()) : null);
		obj.setTimesCostsEvents(new ArrayList<>(this.getTimesCostsEvents()));
		obj.setYear(new Integer(this.getYear()));
		obj.setDistribution(this.getDistribution() != null ? RandomVariateFactory.getInstance(this.getDistribution()) : null);
		return obj;
	}

	@Override
	public String toString() {
		return String.format("RangeFloorLimit=%s, RangeCeilLimit=%s, Frequency=%s, Condition=%s, Cost=%s, CostExpression=%s, NTimes=%s, Year=%s, Distro=%s", 
				getFloorLimitRange(), getCeilLimitRange(), getFrequency(), getCondition(), getCost(), getCostExpression(), calculateNTimesInRange(null, null), getYear(), getDistribution());
	}
	
	public String toString(String prefix) {
		return String.format("%sRangeFloorLimit=%s, RangeCeilLimit=%s, Frequency=%s, Condition=%s, Cost=%s, CostExpression=%s, NTimes=%s, Year=%s, Distro=%s", 
				prefix, getFloorLimitRange(), getCeilLimitRange(), getFrequency(), getCondition(), getCost(), getCostExpression(), calculateNTimesInRange(null, null), getYear(), getDistribution());
	}
}
