package es.ull.isaatc.simulation.editor.project.model;

import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

public class SequenceFlow extends GroupFlow {

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.SequenceFlow flowXML = ProjectModel
				.getXmlModelFactory().createSequenceFlow();
		flowXML.setId(getId());
		if (iterations != null)
			try {
				flowXML
						.setIterations((es.ull.isaatc.simulation.xml.RandomNumber) XMLModelUtilities
								.getJaxbFromXML(
										getIterations(),
										Class
												.forName("es.ull.isaatc.simulation.xml.RandomNumber"),
										"iterations"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		Iterator<Flow> opIt = getFlowList().iterator();
		while (opIt.hasNext()) {
			flowXML.getSingleOrPackageOrSequence().add(
					(es.ull.isaatc.simulation.xml.Flow) opIt.next().getXML());
		}
		return flowXML;
	}
}
