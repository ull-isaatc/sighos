package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.List;

public class CostMatrixElement {
	// Almacena el limite inferior del rango (en años)
	private Double floorLimitRange;
	// Almacena el limite superior del rango (en años)
	private Double ceilLimitRange;
	// Almacena la frecuencia de ocurrencia del evento dentro del rango  (en años)
	private Double frequency;
	// Almacena las condiciones bajo las cuales se aplica un coste
	private String condition;
	// Almacena la expresión para el calculo del coste
	private String costExpression;
	// Almacena el coste en bruto
	private Double cost;
	// Si el coste es aplicado en insntantes discretos del tiempo, esos instantes discretos se almacenan en este array
	private List<Double> timesEvent;

	public CostMatrixElement() {
		this.timesEvent = new ArrayList<>();
	}

	public CostMatrixElement(Double floorLimitRange, Double ceilLimitRange, Double frequency, String condition, String costExpression, Double cost, List<Double> timesEvent) {
		this.floorLimitRange = floorLimitRange;
		this.ceilLimitRange = ceilLimitRange;
		this.frequency = frequency;
		this.condition = condition;
		this.costExpression = costExpression;
		this.cost = cost;
		this.timesEvent = timesEvent;
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

	public List<Double> getTimesEvent() {
		return timesEvent;
	}

	public void setTimesEvent(List<Double> timesEvent) {
		this.timesEvent = timesEvent;
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

	public Double calculateNTimesInRange(Double initTimeRange, Double finalTimeRange) {
		if (this.ceilLimitRange != null &&  this.floorLimitRange != null && this.frequency != null) {
			Double from = this.floorLimitRange;		
			Double to = this.ceilLimitRange;		
			if (initTimeRange != null && finalTimeRange != null) {
				if (initTimeRange >= from && initTimeRange <= to) {
					from = initTimeRange;
				} else {
					from = 0.0;
				}
				if (finalTimeRange <= to) {
					to = finalTimeRange;
				} else {
					to = 0.0;
				}
			}		
			return Math.floor((to - from) / this.frequency);			
		}
		return 1.0;
	}
	
	public CostMatrixElement clone() {
		CostMatrixElement obj = new CostMatrixElement();
		obj.setFloorLimitRange(this.getFloorLimitRange() != null ? new Double(this.getFloorLimitRange()) : null);
		obj.setCeilLimitRange(this.getCeilLimitRange() != null ? new Double(this.getCeilLimitRange()) : null);
		obj.setFrequency(this.getFrequency() != null ? new Double(this.getFrequency()) : null);
		obj.setCondition(this.getCondition() != null ? new String(this.getCondition()) : null);
		obj.setCostExpression(this.getCostExpression() != null ? new String(this.getCostExpression()) : null);
		obj.cost = this.getCost() != null ? new Double(this.getCost()) : null;
		obj.timesEvent = new ArrayList<>(this.getTimesEvent());
		return obj;
	}

	@Override
	public String toString() {
		return String.format("RangeFloorLimit=%s, RangeCeilLimit=%s, Frequency=%s, Condition=%s, Cost=%s, CostExpression=%s, NTimes=%s", 
				this.floorLimitRange, this.ceilLimitRange, this.frequency, this.condition, this.cost, this.costExpression, calculateNTimesInRange(null, null));
	}
	
	public String toString(String prefix) {
		return String.format("%sRangeFloorLimit=%s, RangeCeilLimit=%s, Frequency=%s, Condition=%s, Cost=%s, CostExpression=%s, NTimes=%s", 
				prefix, this.floorLimitRange, this.ceilLimitRange, this.frequency, this.condition, this.cost, this.costExpression, calculateNTimesInRange(null, null));
	}
}
