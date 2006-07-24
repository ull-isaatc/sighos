package es.ull.isaatc.simulation.editor.project.model;

import es.ull.isaatc.simulation.editor.project.ProjectModel;

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
