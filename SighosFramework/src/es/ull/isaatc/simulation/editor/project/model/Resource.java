package es.ull.isaatc.simulation.editor.project.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable.Duration;
import es.ull.isaatc.simulation.editor.util.ModelComponent;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.simulation.xml.CommonFreq;

public class Resource extends ModelComponent {
	/** Number of resources equals as this */
	private int nelem;

	/** Time table entries for this resource */
	private TimeTableTableModel timeTableTableModel;

	/**
	 * Creates a new resource
	 */
	public Resource() {
		super(ComponentType.RESOURCE);
		timeTableTableModel = new TimeTableTableModel();
	}

	/**
	 * @return the nelem
	 */
	public int getNelem() {
		return nelem;
	}

	/**
	 * @param nelem
	 *            the nelem to set
	 */
	public void setNelem(int nelem) {
		this.nelem = nelem;
	}

	/**
	 * @return the timeTableTableModel
	 */
	public TimeTableTableModel getTimeTableTableModel() {
		return timeTableTableModel;
	}

	/**
	 * @param timeTableTableModel
	 *            the timeTableTableModel to set
	 */
	public void setTimeTableTableModel(TimeTableTableModel timeTableTableModel) {
		this.timeTableTableModel = timeTableTableModel;
	}

	@Override
	public Object getXML() {
		es.ull.isaatc.simulation.xml.Resource resXML = ProjectModel
				.getXmlModelFactory().createResource();
		resXML.setId(getId());
		resXML.setDescription(getDescription());
		resXML.setUnits(getNelem());
		getTimeTableTableModel().getXML(resXML);
		return resXML;
	}

	public static class TimeTable implements Cloneable {

		protected List<ResourceType> rtList;

		protected String cycle;

		protected Duration duration;

		public List<ResourceType> getRTList() {
			if (rtList == null) {
				rtList = new ArrayList<ResourceType>();
			}
			return this.rtList;
		}

		public void setRTList(List<ResourceType> rtList) {
			/** Remove this TimeTable entry from the ResourceType ttlist */
			Iterator<ResourceType> rtIt = getRTList().iterator();
			while (rtIt.hasNext())
				rtIt.next().removeTimeTable(this);

			this.rtList = rtList;
		}

		/**
		 * @return the duration
		 */
		public Duration getDuration() {
			if (duration == null) {
				duration = new Duration();
			}
			return duration;
		}

		/**
		 * @param duration
		 *            the duration to set
		 */
		public void setDuration(Duration duration) {
			this.duration = duration;
		}

		/**
		 * @return the cycle
		 */
		public String getCycle() {
			return cycle;
		}

		/**
		 * @param cycle
		 *            the cycle to set
		 */
		public void setCycle(String cycle) {
			this.cycle = cycle;
		}

		public es.ull.isaatc.simulation.xml.Resource.TimeTable getXML() {
			es.ull.isaatc.simulation.xml.Resource.TimeTable ttXML = ProjectModel
					.getXmlModelFactory().createResourceTimeTable();
			ttXML.setDur(getDuration().getXML());
			try {
				ttXML.setCycle((es.ull.isaatc.simulation.xml.Cycle)XMLModelUtilities.getJaxbFromXML(getCycle(), Class.forName("es.ull.isaatc.simulation.xml.Cycle"), "cycle"));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			Iterator<ResourceType> rtIt = getRTList().iterator();
			while (rtIt.hasNext())
				ttXML.getRtId().add(rtIt.next().getId());
			return ttXML;
		}

		public Object clone() {
			Object obj = null;
			try {
				obj = super.clone();
			} catch (CloneNotSupportedException ex) {
				System.out.println("TimeTable : Clone not supported");
			}
			return obj;
		}

		public static class Duration implements Cloneable {

			protected int value;

			protected CommonFreq timeUnit;

			public Duration() {
				this(0, CommonFreq.SECOND);
			}

			public Duration(int value, CommonFreq timeUnit) {
				this.value = value;
				this.timeUnit = timeUnit;
			}

			/**
			 * @return the timeUnit
			 */
			public CommonFreq getTimeUnit() {
				return timeUnit;
			}

			/**
			 * @param timeUnit
			 *            the timeUnit to set
			 */
			public void setTimeUnit(CommonFreq timeUnit) {
				this.timeUnit = timeUnit;
			}

			/**
			 * @return the value
			 */
			public int getValue() {
				return value;
			}

			/**
			 * @param value
			 *            the value to set
			 */
			public void setValue(int value) {
				this.value = value;
			}

			public es.ull.isaatc.simulation.xml.Resource.TimeTable.Dur getXML() {
				es.ull.isaatc.simulation.xml.Resource.TimeTable.Dur durXML = ProjectModel
						.getXmlModelFactory().createResourceTimeTableDur();
				durXML.setTimeUnit(getTimeUnit());
				durXML.setValue(getValue());
				return durXML;
			}

			public Object clone() {
				Object obj = null;
				try {
					obj = super.clone();
				} catch (CloneNotSupportedException ex) {
					System.out.println("Duration : Clone not supported");
				}
				return obj;
			}
		}
	}

	public class TimeTableTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;

		/** Column names */
		protected String[] columnNames;

		/** Elements in the table */
		protected List<TimeTable> elements = new ArrayList<TimeTable>();

		public TimeTableTableModel() {
			super();

			columnNames = new String[4];
			columnNames[0] = ResourceLoader
					.getMessage("resource_timetable_roles");
			columnNames[1] = ResourceLoader
					.getMessage("resource_timetable_cycle");
			columnNames[2] = ResourceLoader
					.getMessage("resource_timetable_duration");
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return elements.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			switch (col) {
			case 0:
				return ((TimeTable) (elements.get(row))).getRTList();
			case 1:
				return ((TimeTable) (elements.get(row))).getCycle();
			case 2:
				Duration dur = ((TimeTable) (elements.get(row))).getDuration();
				return new String(dur.getValue() + " "
						+ dur.getTimeUnit().toString());
			}
			return null;
		}

		public boolean isCellEditable(int row, int col) {
			switch (col) {
			case 0: // roles
				return false;
			case 1: // cycle
				return false;
			case 2: // duration
				return false;
			default:
				return false;
			}
		}

		public void setValueAt(Object value, int row, int col) {
			switch (col) {
			case 0: // roles
				elements.get(row).setRTList((List<ResourceType>) value);
				break;
			case 1: // cycle
				elements.get(row).setCycle((String) value);
				break;
			case 2: // duration
				elements.get(row).setDuration((Duration) value);
				break;
			}
			fireTableCellUpdated(row, col);
		}

		public void add(TimeTable obj) {
			elements.add(obj);
			fireTableDataChanged();
		}

		public void remove(int index) {
			elements.remove(index);
			fireTableDataChanged();
		}

		public TimeTable get(int index) {
			return (TimeTable) (elements.get(index));
		}

		public String toString() {
			return Integer.toString(elements.size());
		}

		public void getXML(es.ull.isaatc.simulation.xml.Resource resXML) {
			List<es.ull.isaatc.simulation.xml.Resource.TimeTable> ttXMLList = resXML
					.getTimeTable();
			Iterator<TimeTable> elemIt = elements.iterator();
			while (elemIt.hasNext())
				ttXMLList.add(elemIt.next().getXML());
		}
	}
}
