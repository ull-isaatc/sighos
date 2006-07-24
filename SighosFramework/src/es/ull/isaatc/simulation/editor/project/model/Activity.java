package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ModelComponentTableModel;
import es.ull.isaatc.simulation.editor.util.ModelComponent;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

public class Activity extends ModelComponent {

	/** Activity priority */
	protected int priority;

	/** Activity presentiality */
	protected boolean presencial;

	/** Activity workgroups */
	protected WorkGroupTableModel workGroupTableModel;

	/** Single flows where this activity is performed */
	protected ArrayList<SingleFlow> flowList;

	/**
	 * Creates a new activity
	 */
	public Activity() {
		super(ComponentType.ACTIVITY);
		workGroupTableModel = new WorkGroupTableModel();
	}

	/**
	 * Creates a new activity and load the content of another activity
	 */
	public Activity(Activity act) {
		super(ComponentType.ACTIVITY);

		setDescription(act.getDescription());
		setPresencial(act.isPresencial());
		setPriority(act.getPriority());
		setId(act.getId());
		workGroupTableModel = new WorkGroupTableModel();
	}

	/**
	 * Gets the value of the priority property.
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * Sets the value of the priority property.
	 */
	public void setPriority(int value) {
		this.priority = value;
	}

	/**
	 * Gets the value of the presencial property.
	 */
	public boolean isPresencial() {
		return presencial;
	}

	/**
	 * Sets the value of the presencial property.
	 */
	public void setPresencial(boolean value) {
		this.presencial = value;
	}

	/**
	 * Gets the value of the workGroupTableModel property.
	 */
	public WorkGroupTableModel getWorkGroupTableModel() {
		return workGroupTableModel;
	}

	/**
	 * Sets the value of the workGroupTableModel property.
	 */
	public void setWorkGroupTableModel(WorkGroupTableModel workGroupTableModel) {
		this.workGroupTableModel = workGroupTableModel;
	}

	/**
	 * @return the packageFlowList
	 */
	public ArrayList<SingleFlow> getFlowList() {
		if (flowList == null)
			flowList = new ArrayList<SingleFlow>();
		return flowList;
	}

	/**
	 * @param packageFlowList
	 *            the packageFlowList to set
	 */
	public void setFlowList(ArrayList<SingleFlow> flowList) {
		this.flowList = flowList;
	}

	public boolean hasReferences() {
		return (getFlowList().size() > 0);
	}

	public void removeReferences() {
		Iterator<SingleFlow> flowIt = getFlowList().iterator();
		while (flowIt.hasNext())
			flowIt.next().setActivity(null);
	}

	public String toString() {
		return description;
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.Activity actXML = ProjectModel
				.getXmlModelFactory().createActivity();
		actXML.setId(getId());
		actXML.setDescription(getDescription());
		actXML.setPresencial(isPresencial());
		actXML.setPriority(getPriority());
		actXML.getWorkGroup().addAll(getWorkGroupTableModel().getXML());
		return actXML;
	}

	public static class WorkGroup extends ModelComponent implements Cloneable {

		protected HashMap<ResourceType, Integer> resourceType;

		protected int priority;

		protected String duration;

		public WorkGroup() {
			super(ComponentType.WORKGROUP);
		}

		public HashMap<ResourceType, Integer> getResourceType() {
			if (resourceType == null) {
				resourceType = new HashMap<ResourceType, Integer>();
			}
			return resourceType;
		}

		/**
		 * @param resourceType
		 *            the resourceType to set
		 */
		public void setResourceType(HashMap<ResourceType, Integer> resourceType) {
			/** Remove this WorkGroup from the ResourceType wglist */
			Iterator<ResourceType> rtIt = getResourceType().keySet().iterator();
			while (rtIt.hasNext())
				rtIt.next().removeWorkGroup(this);

			this.resourceType = resourceType;
		}

		/**
		 * Gets the value of the priority property.
		 */
		public int getPriority() {
			return priority;
		}

		/**
		 * Sets the value of the priority property.
		 */
		public void setPriority(int value) {
			this.priority = value;
		}

		/**
		 * Gets the value of the duration property.
		 */
		public String getDuration() {
			return duration;
		}

		/**
		 * Sets the value of the duration property.
		 */
		public void setDuration(String value) {
			this.duration = value;
		}

		@Override
		public Object getXML() {
			es.ull.isaatc.simulation.xml.Activity.WorkGroup wgXML = ProjectModel
					.getXmlModelFactory().createActivityWorkGroup();
			wgXML.setId(getId());
			wgXML.setDescription(getDescription());
			wgXML.setPriority(getPriority());
			Iterator<ResourceType> rtIt = resourceType.keySet().iterator();
			while (rtIt.hasNext()) {
				ResourceType rt = rtIt.next();
				es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role rtXML = ProjectModel
						.getXmlModelFactory()
						.createActivityWorkGroupRole();
				rtXML.setRtId(rt.getId());
				rtXML.setUnits(resourceType.get(rt));
				wgXML.getRole().add(rtXML);
			}
			try {
				wgXML.setDuration((es.ull.isaatc.simulation.xml.RandomNumber)XMLModelUtilities.getJaxbFromXML(getDuration(), Class.forName("es.ull.isaatc.simulation.xml.RandomNumber"), "duration"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			return wgXML;
		}

		public Object clone() {
			Object obj = null;
			try {
				obj = super.clone();
			} catch (CloneNotSupportedException ex) {
				System.out.println("WorkGroup : Clone not supported");
			}
			return obj;
		}
	}

	public class WorkGroupTableModel extends ModelComponentTableModel {
		private static final long serialVersionUID = 1L;

		public WorkGroupTableModel() {
			super();

			columnNames = new String[5];
			columnNames[0] = ResourceLoader.getMessage("component_id");
			columnNames[1] = ResourceLoader.getMessage("component_description");
			columnNames[2] = ResourceLoader
					.getMessage("activity_workgroup_roles");
			columnNames[3] = ResourceLoader
					.getMessage("activity_workgroup_priority");
			columnNames[4] = ResourceLoader
					.getMessage("activity_workgroup_duration");
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return elements.get(row).getId();
			case 1:
				return elements.get(row).getDescription();
			case 2:
				return ((WorkGroup) (elements.get(row))).getResourceType();
			case 3:
				return ((WorkGroup) (elements.get(row))).getPriority();
			case 4:
				return ((WorkGroup) (elements.get(row))).getDuration();
			}
			return null;
		}

		public void setValueAt(Object value, int row, int col) {
			switch (col) {
			case 0: // id
				elements.get(row).setId((Integer) value);
				break;
			case 1: // description
				elements.get(row).setDescription(value.toString());
				break;
			case 2: // resource types
				((WorkGroup) elements.get(row))
						.setResourceType((HashMap<ResourceType, Integer>) value);
				break;
			case 3: // priority
				((WorkGroup) elements.get(row)).setPriority((Integer) value);
				break;
			case 4: // duration
				((WorkGroup) elements.get(row)).setDuration(value.toString());
				break;
			}
			fireTableCellUpdated(row, col);
		}

		public WorkGroup get(int index) {
			return (WorkGroup) (elements.get(index));
		}

		public String toString() {
			return Integer.toString(elements.size());
		}
	}
}
