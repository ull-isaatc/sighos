/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.*;
import es.ull.isaatc.simulation.editor.util.Validatory;
import es.ull.isaatc.simulation.xml.CommonFreq;

/**
 * Encapsulates a model
 * 
 * @author Roberto Muñoz
 */
public class Model implements Validatory {

	/** Model file name */
	private String fileName;
	
	/** Model description */
	private String description = "SIGHOS Model";

	/** Base time unit emplyed in the model */
	private CommonFreq baseTimeUnit = CommonFreq.SECOND;

	/** Table with resource types */
	private ResourceTypeTableModel resourceTypeTableModel;

	/** Table with resources */
	private ResourceTableModel resourceTableModel;

	/** Table with activities */
	private ActivityTableModel activityTableModel;

	/** Table with element types */
	private ElementTypeTableModel elementTypeTableModel;

	/** Table with root flows */
	private RootFlowTableModel rootFlowTableModel;


	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * Resets a model.
	 */
	public void reset() {

		fileName = ProjectModel.getInstance().getDirectory() + "\\model.xml";
		resourceTypeTableModel = new ResourceTypeTableModel();
		resourceTableModel = new ResourceTableModel();
		activityTableModel = new ActivityTableModel();
		elementTypeTableModel = new ElementTypeTableModel();
		rootFlowTableModel = new RootFlowTableModel();
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the baseTimeUnit
	 */
	public CommonFreq getBaseTimeUnit() {
		return baseTimeUnit;
	}

	/**
	 * @param baseTimeUnit
	 *            the baseTimeUnit to set
	 */
	public void setBaseTimeUnit(CommonFreq baseTimeUnit) {
		this.baseTimeUnit = baseTimeUnit;
	}

	/**
	 * @return the resourceTypeTableModel
	 */
	public ResourceTypeTableModel getResourceTypeTableModel() {
		return resourceTypeTableModel;
	}

	/**
	 * @param resourceTypeTableModel
	 *            the resourceTypeTableModel to set
	 */
	public void setResourceTypeTableModel(
			ResourceTypeTableModel resourceTypeTableModel) {
		this.resourceTypeTableModel = resourceTypeTableModel;
	}

	/**
	 * @return the resourceTableModel
	 */
	public ResourceTableModel getResourceTableModel() {
		return resourceTableModel;
	}

	/**
	 * @param resourceTableModel
	 *            the resourceTableModel to set
	 */
	public void setResourceTableModel(ResourceTableModel resourceTableModel) {
		this.resourceTableModel = resourceTableModel;
	}

	/**
	 * @return the activityTableModel
	 */
	public ActivityTableModel getActivityTableModel() {
		return activityTableModel;
	}

	/**
	 * @param activityTableModel
	 *            the activityTableModel to set
	 */
	public void setActivityTableModel(ActivityTableModel activityTableModel) {
		this.activityTableModel = activityTableModel;
	}

	/**
	 * @return the elementTypeTableModel
	 */
	public ElementTypeTableModel getElementTypeTableModel() {
		return elementTypeTableModel;
	}

	/**
	 * @param elementTypeTableModel the elementTypeTableModel to set
	 */
	public void setElementTypeTableModel(ElementTypeTableModel elementTypeTableModel) {
		this.elementTypeTableModel = elementTypeTableModel;
	}
	
	/**
	 * @return the rootFlowTableModel
	 */
	public RootFlowTableModel getRootFlowTableModel() {
		return rootFlowTableModel;
	}

	/**
	 * @param rootFlowTableModel
	 *            the rootFlowTableModel to set
	 */
	public void setRootFlowTableModel(RootFlowTableModel rootFlowTableModel) {
		this.rootFlowTableModel = rootFlowTableModel;
	}

	public es.ull.isaatc.simulation.xml.Model getXML() {
		es.ull.isaatc.simulation.xml.Model modelXML = ProjectModel
				.getXmlModelFactory().createModel();
		modelXML.setBaseTimeUnit(getBaseTimeUnit());
		modelXML.setDescription(getDescription());
		modelXML.getResourceType().addAll(getResourceTypeTableModel().getXML());
		modelXML.getResource().addAll(getResourceTableModel().getXML());
		modelXML.getActivity().addAll(getActivityTableModel().getXML());
		modelXML.getElementType().addAll(getElementTypeTableModel().getXML());
		modelXML.getRootFlow().addAll(getRootFlowTableModel().getXML());
		return modelXML;
	}

	public List<ProblemTableItem> validate() {
		List<ProblemTableItem> problems = new ArrayList<ProblemTableItem>();
		problems.addAll(getResourceTypeTableModel().validate());
		problems.addAll(getResourceTableModel().validate());
		problems.addAll(getElementTypeTableModel().validate());
		problems.addAll(getActivityTableModel().validate());
		problems.addAll(getRootFlowTableModel().validate());
		return problems;
	}
}
