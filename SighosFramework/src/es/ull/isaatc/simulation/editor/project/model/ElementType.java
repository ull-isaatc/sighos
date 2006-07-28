package es.ull.isaatc.simulation.editor.project.model;

import java.util.HashMap;
import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class ElementType extends ModelComponent {

	/** List of type flows where this element type appears in any of its branches */
	private HashMap<TypeFlow, TypeBranchFlow> typeFlowList;

	public ElementType() {
		super(ComponentType.ELEMENT_TYPE);
		typeFlowList = new HashMap<TypeFlow, TypeBranchFlow>();
	}

	public void addBranch(TypeFlow tf, TypeBranchFlow bf) {
		typeFlowList.put(tf, bf);
	}

	/**
	 * Removes a type flow from the reference list
	 * 
	 * @param tf
	 *            the type flow to remove
	 */
	public void removeBranch(TypeFlow tf) {
		typeFlowList.remove(tf);
	}

	public boolean hasReferences() {
		return (typeFlowList.size() > 0);
	}

	/**
	 * Remove the references of this element type in the model
	 */
	public void removeReferences() {
		Iterator<TypeFlow> flowIt = typeFlowList.keySet().iterator();
		while (flowIt.hasNext())
			flowIt.next().getElementTypes(typeFlowList.get(flowIt))
					.remove(this);
	}

	public String toString() {
		return description;
	}

	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("elementtype");
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.ElementType etXML = ProjectModel
				.getXmlModelFactory().createElementType();
		etXML.setId(getId());
		etXML.setDescription(getDescription());
		return etXML;
	}
}
