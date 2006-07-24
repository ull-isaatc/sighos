/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

/**
 * @author Roberto Muñoz
 *
 */
public class TypeBranchFlow extends Flow implements ContainerFlow {
	/** flow this option encapsulates */
	private Flow option;
	/** branch restrictions */
	ArrayList<ElementType> elemTypes = new ArrayList<ElementType>();
	
	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public TypeBranchFlow() {
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
		if (option == null) {
			option = new SequenceFlow();
			((GroupFlow)option).addFlow(flow);
		}
		else
			option = flow;
	}

	
	/**
	 * @return the restrictions
	 */
	public ArrayList<ElementType> getElemTypes() {
		return elemTypes;
	}

	/**
	 * @param elemTypes the restrictions to set
	 */
	public void setElemTypes(ArrayList<ElementType> elemTypes) {
		this.elemTypes = elemTypes;
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.TypeBranch flowXML = ProjectModel
		.getXmlModelFactory().createTypeBranch();
		flowXML.setId(getId());
		flowXML.setElemTypes(getElemTypesIdStr());
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
	
	public String getElemTypesIdStr() {
		String str = "";
		if (elemTypes.size() > 0)
			str = "" + elemTypes.get(0).getId();
		for (int i = 1; i < elemTypes.size(); i++)
			str += ", " + elemTypes.get(i).getId();
		return str;
	}
	
	public String toString() {
		String str = "";
		if (elemTypes.size() > 0)
			str = elemTypes.get(0).toString();
		for (int i = 1; i < elemTypes.size(); i++)
			str += ", " + elemTypes.get(i).toString();
		return str;
	}
}
