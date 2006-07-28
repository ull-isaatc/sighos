package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

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
	public String getComponentString() {
		return ResourceLoader.getMessage("typeflow");
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

	/**
	 * Checks the element types of the branches
	 * @param checkedList
	 * @param newList
	 */
	private void checkTypeBranch(List<ElementType> checkedList, TypeBranchFlow tbFlow) {
		// check the hierarchy of the element types 
		problems.addAll(tbFlow.validate());
		
		for (ElementType et : tbFlow.getElemTypes()) {
			if (checkedList.contains(et))
				problems.add(new ProblemTableItem(ProblemType.ERROR, 
						ResourceLoader.getMessage("typeflow_elementtype_validation"),
						getComponentString(), getId()));
			checkedList.add(et);
		}
	}
	
	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		// check the elemtypes selected for each branch
		List<ElementType> elemTypeList = new ArrayList<ElementType>();  // list of element types checked
		Iterator<TypeBranchFlow> dbfIt = getOptions().keySet().iterator();
		while (dbfIt.hasNext()) {
			checkTypeBranch(elemTypeList, dbfIt.next());
		}		
		return problems;
	}
}
