package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem.ProblemType;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class RootFlow extends ModelComponent {

	/** flow of the root flow */
	private Flow flow = null;

	/** Package flows where this root flow is performed */
	private ArrayList<PackageFlow> packageFlowList;

	public RootFlow() {
		super(ComponentType.ROOT_FLOW);
	}

	public RootFlow(String description) {
		super(ComponentType.ROOT_FLOW, description);
	}

	public Flow getFlow() {
		return flow;
	}

	public void setFlow(Flow flow) {
		this.flow = flow;
	}

	/**
	 * @return the packageFlowList
	 */
	public ArrayList<PackageFlow> getPackageFlowList() {
		if (packageFlowList == null)
			packageFlowList = new ArrayList<PackageFlow>();
		return packageFlowList;
	}

	/**
	 * @param packageFlowList
	 *            the packageFlowList to set
	 */
	public void setPackageFlowList(ArrayList<PackageFlow> packageFlowList) {
		this.packageFlowList = packageFlowList;
	}

	public boolean hasReferences() {
		return (getPackageFlowList().size() > 0);
	}

	public void removeReferences() {
		Iterator<PackageFlow> flowIt = getPackageFlowList().iterator();
		while (flowIt.hasNext())
			flowIt.next().setRootFlow(null, true);
	}

	public String toString() {
		return getDescription();
	}

	@Override
	public String getComponentString() {
		return ResourceLoader.getMessage("rootflow");
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.RootFlow rfXML = ProjectModel
				.getXmlModelFactory().createRootFlow();
		rfXML.setId(getId());
		rfXML.setDescription(getDescription());
		es.ull.isaatc.simulation.xml.FlowChoice flowXML = ProjectModel
				.getXmlModelFactory().createFlowChoice();
		if (flow instanceof SingleFlow)
			flowXML.setSingle((es.ull.isaatc.simulation.xml.SingleFlow) flow
					.getXML());
		else if (flow instanceof PackageFlow)
			flowXML.setPackage((es.ull.isaatc.simulation.xml.PackageFlow) flow
					.getXML());
		else if (flow instanceof ExitFlow)
			flowXML.setExit((es.ull.isaatc.simulation.xml.ExitFlow) flow
					.getXML());
		else if (flow instanceof SequenceFlow)
			flowXML
					.setSequence((es.ull.isaatc.simulation.xml.SequenceFlow) flow
							.getXML());
		else if (flow instanceof SimultaneousFlow)
			flowXML
					.setSimultaneous((es.ull.isaatc.simulation.xml.SimultaneousFlow) flow
							.getXML());
		else if (flow instanceof DecisionFlow)
			flowXML
					.setDecision((es.ull.isaatc.simulation.xml.DecisionFlow) flow
							.getXML());
		else
			flowXML.setType((es.ull.isaatc.simulation.xml.TypeFlow) flow
					.getXML());
		rfXML.setFlow(flowXML);
		return rfXML;
	}

	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.editor.project.model.ModelComponent#validate()
	 */
	@Override
	public List<ProblemTableItem> validate() {
		super.validate();
		
		if (flow == null)
			problems.add(new ProblemTableItem(ProblemType.ERROR, 
					ResourceLoader.getMessage("rootflow_flow_validation"),
					getComponentString(), getId()));
		else
			problems.addAll(getFlow().validate());
		
		return problems;
	}
	
	
}
