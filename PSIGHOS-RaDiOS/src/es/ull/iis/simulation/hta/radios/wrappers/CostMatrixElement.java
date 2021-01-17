package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.List;

public class CostMatrixElement {
	private String condition;
	private String costExpression;
	private Double cost;
	private List<Double> timesEvent;

	public CostMatrixElement() {
		this.timesEvent = new ArrayList<>();
	}

	public CostMatrixElement(String condition, String costExpression, Double cost, List<Double> timesEvent) {
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
	
	public static CostMatrixElement clone(CostMatrixElement source) {
		CostMatrixElement obj = new CostMatrixElement();
		obj.setCondition(new String(source.getCondition()));
		obj.setCostExpression(new String(source.getCostExpression()));
		obj.cost = new Double(source.getCost());
		obj.timesEvent = new ArrayList<>(source.getTimesEvent());
		return obj;
	}
	
	@Override
	public String toString() {
		return String.format("Condition=%s, Cost=%s, Times event(years)=%s, CostExpression=%s", this.condition, this.cost, this.timesEvent, this.costExpression);
	}
}
