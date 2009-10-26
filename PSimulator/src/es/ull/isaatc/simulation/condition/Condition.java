package es.ull.isaatc.simulation.condition;


/**
 * A logical condition which is used for create restrictions or 
 * uncertainty situations.
 * 
 * @author ycallero
 */

public class Condition {
	/** The condition expressed in the condition's format */
	protected String conditionText;
	
	/** 
	 * Creates a new Condition.
	 * @param id Condition's identifier
	 */
	public Condition(){
	}
	
	/**
	 * Creates a new Condition.
	 * @param id Identifier.
	 * @param simul Actual simulation.
	 */
	public Condition(String conditionText){
		this.conditionText = conditionText;
	}

	/**
	 * @return the conditionText
	 */
	public String getConditionText() {
		return conditionText;
	}

	/**
	 * @param conditionText the conditionText to set
	 */
	public void setConditionText(String conditionText) {
		this.conditionText = conditionText;
	}
}
