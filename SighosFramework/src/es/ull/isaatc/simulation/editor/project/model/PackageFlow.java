package es.ull.isaatc.simulation.editor.project.model;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

public class PackageFlow extends Flow {

	/** package reference */
	protected RootFlow rootFlow;

	/**
	 * Creates a new Flow
	 * 
	 * @param componentType
	 */
	public PackageFlow() {
		super(ComponentType.FLOW);
	}

	/**
	 * @return the rootFlow
	 */
	public RootFlow getRootFlow() {
		return rootFlow;
	}

	/**
	 * @param rootFlow
	 *            the rootFlow to set
	 */
	public void setRootFlow(RootFlow rootFlow, boolean secureSet) {
		// remove this flow from the flow list of the root flow
		if ((getRootFlow() != null) && !secureSet) {
			getRootFlow().getFlowList().remove(this);
		}
		this.rootFlow = rootFlow;
		if (rootFlow != null)
			this.rootFlow.getFlowList().add(this);
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.PackageFlow flowXML = ProjectModel
				.getXmlModelFactory().createPackageFlow();
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
		flowXML.setRootFlowId(getRootFlow().getId());
		return flowXML;
	}

	public String toString() {
		String str = "Not defined";
		if (rootFlow != null)
			str = rootFlow.toString();
		return str;
	}
}
