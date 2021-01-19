package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.List;

public class CostMatrixElement {
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
	
	public CostMatrixElement clone() {
		CostMatrixElement obj = new CostMatrixElement();
		obj.setCondition(this.getCondition() != null ? new String(this.getCondition()) : null);
		obj.setCostExpression(this.getCostExpression() != null ? new String(this.getCostExpression()) : null);
		obj.cost = this.getCost() != null ? new Double(this.getCost()) : null;
		obj.timesEvent = new ArrayList<>(this.getTimesEvent());
		return obj;
	}
	
	@Override
	public String toString() {
		return String.format("Condition=%s, Cost=%s, Times event(years)=%s, CostExpression=%s", this.condition, this.cost, this.timesEvent, this.costExpression);
	}
}
