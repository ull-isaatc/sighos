/**
 * 
 */
package es.ull.iis.simulation.hta.outcomes;

import java.util.ArrayList;

import es.ull.iis.simulation.condition.Condition;
import es.ull.iis.simulation.condition.TrueCondition;
import es.ull.iis.simulation.hta.Patient;
import es.ull.iis.simulation.model.Describable;

/**
 * A stepped strategy intended to involve different costs at different stages. For example, a treatment strategy may consist on starting with a drug, 
 * then switching to another, and finally staying with a different one. Strategies may require a previous condition to be applied. The simplest strategy just incurs
 * in a specific cost; more complex strategies involve a succession of stages or steps, each one consisting on one or several drugs, treatments, follow-up tests...  
 * @author Iván Castilla Rodríguez
 *
 */
public class Strategy implements Describable {
	private final Condition<Patient> condition;
	private final String description;
	private final ArrayList<Strategy> stages;
	
	/**
	 * 
	 */
	public Strategy(String description) {
		this(description, new TrueCondition<Patient>());
	}

	/**
	 * 
	 */
	public Strategy(String description, Condition<Patient> cond) {
		this.condition = cond;
		this.description = description;
		this.stages = new  ArrayList<>();
	}

	@Override
	public String getDescription() {
		return description;
	}

	public Condition<Patient> getCondition() {
		return condition;
	}

	public double getCost(Patient pat) {
		return 0.0;
	}
	
	public void addStage(String description, Condition<Patient> cond) {
		stages.add(new Strategy(description, cond));
	}
	
/*	private class Stage {
		private final Condition<Patient> condition;
		private final String description;
		
		public Stage(String description, Condition<Patient> cond) {
			this.condition = cond;
			this.description = description;
		}
		
		public String getDescription() {
			return description;
		}

		public Condition<Patient> getCondition() {
			return condition;
		}
	}*/
}
