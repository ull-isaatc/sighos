package es.ull.isaatc.simulation.editor.project.model;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

public class ExitFlow extends Flow {

	/**
	 * Creates a new Flow
	 * 
	 * @param componentType
	 */
	public ExitFlow() {
		super(ComponentType.FLOW);
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.ExitFlow flowXML = ProjectModel
				.getXmlModelFactory().createExitFlow();
		flowXML.setId(getId());
		return flowXML;
	}
}
