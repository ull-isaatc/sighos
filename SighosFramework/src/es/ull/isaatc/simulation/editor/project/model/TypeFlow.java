package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

public class TypeFlow extends Flow {

	/** List of branches and element types of this type flow */
	private HashMap<TypeBranchFlow, ArrayList<ElementType>> options = new HashMap<TypeBranchFlow, ArrayList<ElementType>>();
	
	/**
	 * Creates a new Flow
	 * @param componentType
	 */
	public TypeFlow() {
		super(ComponentType.FLOW);
	}
	/**
	 * 
	 * @param flow
	 *            add a flow to the options
	 */
	public void addOption(TypeBranchFlow flow) {
		options.put(flow, new ArrayList<ElementType>());
	}

	/**
	 * @return the element types of a branch
	 */
	public ArrayList<ElementType> getElementTypes(TypeBranchFlow flow) {
		return options.get(flow);
	}

	/**
	 * 
	 * @param flow
	 * @param elementTypes
	 */
	public void setElementTypes(TypeBranchFlow flow,
			ArrayList<ElementType> elementTypes) {
		options.put(flow, elementTypes);
	}

	/**
	 * 
	 * @param flow
	 * @param elementType
	 */
	public void addElementType(TypeBranchFlow flow, ElementType elementType) {
		options.get(flow).add(elementType);
	}

	/**
	 * @return the options
	 */
	public HashMap<TypeBranchFlow, ArrayList<ElementType>> getOptions() {
		return options;
	}
	
	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.TypeFlow flowXML = ProjectModel
		.getXmlModelFactory().createTypeFlow();
		flowXML.setId(getId());
		Iterator<TypeBranchFlow> opIt = this.getOptions().keySet().iterator();
		while (opIt.hasNext()) {
			flowXML.getBranch().add((es.ull.isaatc.simulation.xml.TypeBranch) opIt.next().getXML());
		}
		return flowXML;
	}
}
