package es.ull.isaatc.simulation.editor.project.model;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class SingleFlow extends Flow {

	/** activity performed */
	protected Activity activity;

	/**
	 * Creates a new Flow
	 * 
	 * @param componentType
	 */
	public SingleFlow() {
		super(ComponentType.FLOW);
	}

	/**
	 * @return the activity
	 */
	public Activity getActivity() {
		return activity;
	}

	/**
	 * @param activity
	 *            the activity to set
	 */
	public void setActivity(Activity activity) {
		// remove this flow from the flow list of the activity
		if (getActivity() != null) {
			getActivity().getFlowList().remove(this);
		}
		this.activity = activity;
		if (activity != null)
			this.activity.getFlowList().add(this);
	}
	
	/**
	 * @return the description
	 */
	public String getDescription() {
		if (activity != null)
			return activity.getDescription();
		return super.getDescription();
	}

	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("singleflow");
	}
	
	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.SingleFlow flowXML = ProjectModel
				.getXmlModelFactory().createSingleFlow();
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
		flowXML.setActId(getActivity().getId());
		return flowXML;
	}

	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		if (activity == null)
			problems.add(new ProblemTableItem(ProblemType.ERROR, 
					ResourceLoader.getMessage("singleflow_activity_validation"),
					getComponentString(), getId()));
		return problems;
	}

	public String toString() {
		String str = "Not defined";
		if (activity != null) {
			// int endIndex = activity.toString().length();
			// if (endIndex > 15)
			// endIndex = 15;
			// str = activity.toString().substring(0, endIndex);
			str = activity.toString();
		}
		return str;
	}
}
