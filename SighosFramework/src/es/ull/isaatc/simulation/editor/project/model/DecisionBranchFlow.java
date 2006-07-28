/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

/**
 * @author Roberto Muñoz
 *
 */
public class DecisionBranchFlow extends Flow implements ContainerFlow {
	/** flow this option encapsulates */
	private Flow option;
	/** branch restrictions */
	float prob;

	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public DecisionBranchFlow() {
		super(ComponentType.FLOW);
	}
	
	/**
	 * @return the option
	 */
	public Flow getOption() {
		return option;
	}

	/**
	 * @param option the option to set
	 */
	public void setOption(Flow option) {
		this.option = option;
	}

	public void addFlow(Flow flow) {
		if (option == null)
			option = new SequenceFlow();
		((GroupFlow)option).addFlow(flow);
	}


	/**
	 * @return the restrictions
	 */
	public float getProb() {
		return prob;
	}

	/**
	 * @param prob the restrictions to set
	 */
	public void setProb(float prob) {
		this.prob = prob;
	}
	
	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("decisionbranchflow");
	}
	
	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.DecisionOption flowXML = ProjectModel
		.getXmlModelFactory().createDecisionOption();
		flowXML.setId(getId());
		flowXML.setProb(getProb());
		if (option instanceof SingleFlow)
			flowXML.setSingle((es.ull.isaatc.simulation.xml.SingleFlow)option.getXML());
		else if (option instanceof PackageFlow)
			flowXML.setPackage((es.ull.isaatc.simulation.xml.PackageFlow)option.getXML());
		else if (option instanceof ExitFlow)
			flowXML.setExit((es.ull.isaatc.simulation.xml.ExitFlow)option.getXML());
		else if (option instanceof SequenceFlow)
			flowXML.setSequence((es.ull.isaatc.simulation.xml.SequenceFlow)option.getXML());
		else if (option instanceof SimultaneousFlow)
			flowXML.setSimultaneous((es.ull.isaatc.simulation.xml.SimultaneousFlow)option.getXML());
		else if (option instanceof DecisionFlow)
			flowXML.setDecision((es.ull.isaatc.simulation.xml.DecisionFlow)option.getXML());
		else
			flowXML.setType((es.ull.isaatc.simulation.xml.TypeFlow)option.getXML());
		return flowXML;
	}
	

	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		prob = getProb();
		if ((prob < 0) || (prob > 1))
			problems.add(new ProblemTableItem(ProblemType.ERROR, 
					ResourceLoader.getMessage("decisionbrachflow_prob_validation"),
					getComponentString(), getId()));
		
		problems.addAll(getOption().validate());
		
		return problems;
	}

	public String toString() {
		return String.valueOf(prob);
	}
}
