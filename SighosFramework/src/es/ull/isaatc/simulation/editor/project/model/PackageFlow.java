package es.ull.isaatc.simulation.editor.project.model;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

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
			getRootFlow().getPackageFlowList().remove(this);
		}
		this.rootFlow = rootFlow;
		if (rootFlow != null)
			this.rootFlow.getPackageFlowList().add(this);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		if (rootFlow != null)
			return rootFlow.getDescription();
		return super.getDescription();
	}
	
	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("packageflow");
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
	
	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		if (rootFlow == null)
			problems.add(new ProblemTableItem(ProblemType.ERROR, 
					ResourceLoader.getMessage("packageflow_rootflow_validation"),
					getComponentString(), getId()));
		return problems;
	}
	
	public String toString() {
		String str = "Not defined";
		if (rootFlow != null)
			str = rootFlow.toString();
		return str;
	}
}
