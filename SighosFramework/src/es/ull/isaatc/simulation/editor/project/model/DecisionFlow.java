package es.ull.isaatc.simulation.editor.project.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

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
	public String getComponentString() {
		return ResourceLoader.getMessage("decisionflow");
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
	
	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		float sum = 0;
		
		Iterator<DecisionBranchFlow> dbfIt = getOptions().keySet().iterator();
		while (dbfIt.hasNext()) {
			DecisionBranchFlow dbFlow = dbfIt.next();
			dbFlow.validate();
			sum += dbFlow.getProb();
		}
		// check the sumatory of the probability of all branches
		if (sum != 1)
			problems.add(new ProblemTableItem(ProblemType.WARNING, 
					ResourceLoader.getMessage("decisionflow_prob_validation"),
					getComponentString(), getId()));
		
		return problems;
	}
}
