/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

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
	public String getComponentString() {
		return ResourceLoader.getMessage("typebranchflow");
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

	@Override
	public List<ProblemTableItem> validate() {
		super.validate();

		// check if each elemen type selected has been previously selected
		Flow prevFlow;
		for (ElementType et : getElemTypes()) {
			prevFlow = getParent();
			while (prevFlow != null) {
				if (prevFlow instanceof TypeBranchFlow) {
					if (!((TypeBranchFlow)prevFlow).getElemTypes().contains(et))
						problems.add(new ProblemTableItem(ProblemType.ERROR, 
								ResourceLoader.getMessage("typebranchflow_elementtype_validation"),
								getComponentString(), getId()));
					prevFlow = null;
				}
				else
					prevFlow = ((Flow)prevFlow).getParent();
			}
		}
		
		problems.addAll(getOption().validate());

		return problems;
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
