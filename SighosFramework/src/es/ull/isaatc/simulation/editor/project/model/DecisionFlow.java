package es.ull.isaatc.simulation.editor.project.model;

import java.util.HashMap;
import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

public class DecisionFlow extends Flow {

	HashMap<DecisionBranchFlow, Float> options = new HashMap<DecisionBranchFlow, Float>();

	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public DecisionFlow() {
		super(ComponentType.FLOW);
	}
	
	/**
	 * 
	 * @param flow add a flow to the options
	 */
	public void addOption(DecisionBranchFlow flow) {
		options.put(flow, Float.valueOf(0));
	}
	
	/**
	 * @return the prob
	 */
	public double getProb(DecisionBranchFlow flow) {
		return options.get(flow);
	}

	/**
	 * 
	 * @param flow
	 * @param prob
	 */
	public void setProb(DecisionBranchFlow flow, float prob) {
		options.put(flow, prob);
	}

	/**
	 * @return the options
	 */
	public HashMap<DecisionBranchFlow, Float> getOptions() {
		return options;
	}
	
	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.DecisionFlow flowXML = ProjectModel
		.getXmlModelFactory().createDecisionFlow();
		flowXML.setId(getId());
		Iterator<DecisionBranchFlow> opIt = this.getOptions().keySet().iterator();
		while (opIt.hasNext()) {
			flowXML.getOption().add((es.ull.isaatc.simulation.xml.DecisionOption) opIt.next().getXML());
		}
		return flowXML;
	}
}
