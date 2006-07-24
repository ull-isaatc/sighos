package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroup;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable;
import es.ull.isaatc.simulation.editor.util.ModelComponent;

public class ResourceType extends ModelComponent {
	
	ArrayList<TimeTable> ttList;
	
	ArrayList<WorkGroup> wgList;

	public ResourceType() {
		super(ComponentType.RESOURCE_TYPE);
		ttList = getTTList();
		wgList = getWGList();
	}
		
	/**
	 * @return the packageFlowList
	 */
	public ArrayList<TimeTable> getTTList() {
		if (ttList == null)
			ttList = new ArrayList<TimeTable>();
		return ttList;
	}

	/**
	 * @param packageFlowList the packageFlowList to set
	 */
	public void setTTList(ArrayList<TimeTable> ttList) {
		this.ttList = ttList;
	}

	/**
	 * @return the wgList
	 */
	public ArrayList<WorkGroup> getWGList() {
		if (wgList == null)
			wgList = new ArrayList<WorkGroup>();
		return wgList;
	}

	/**
	 * @param wgList the wgList to set
	 */
	public void setWGList(ArrayList<WorkGroup> wgList) {
		this.wgList = wgList;
	}

	
	public void addTimeTable(TimeTable tt) {
		getTTList().add(tt);
	}
	
	public void removeTimeTable(TimeTable tt) {
		getTTList().remove(tt);
	}
	
	public void addWorkGroup(WorkGroup wg) {
		getWGList().add(wg);
	}
	
	public void removeWorkGroup(WorkGroup wg) {
		getWGList().remove(wg);
	}
	
	public boolean hasReferences() {
		return ((getTTList().size() > 0) || (getWGList().size() > 0));	
	}

	public void removeReferences() {
		Iterator<TimeTable> ttIt = getTTList().iterator();
		while (ttIt.hasNext())
			ttIt.next().getRTList().remove(this);

		Iterator<WorkGroup> wgIt = getWGList().iterator();
		while (wgIt.hasNext())
			wgIt.next().getResourceType().remove(this);
	}
	
	
	public String toString() {
		return description;
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.ResourceType rtXML = ProjectModel.getXmlModelFactory().createResourceType();
		rtXML.setId(getId());
		rtXML.setDescription(getDescription());
		return rtXML;
	}
}
