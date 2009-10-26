package es.ull.isaatc.simulation.model;

import java.util.ArrayList;

import es.ull.isaatc.simulation.Describable;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class Resource extends VariableStoreModelObject implements Describable {
	/** Timetable which defines the availability structure of the resource. Define RoleOn and RoleOff events. */
    protected final ArrayList<TimeTableEntry> timeTable;
    /** A brief description of the resource */
    protected final String description;

    /**
     * Creates a new instance of Resource.
     * @param id This resource's identifier.
     * @param model Model this resource is attached to.
     * @param description A short text describing this resource.
     */
	public Resource(int id, Model model, String description) {
		super(id, model);
		this.description = description;
        timeTable = new ArrayList<TimeTableEntry>();
        model.add(this);
	}

    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, Time dur, ResourceType role) {
        timeTable.add(new TimeTableEntry(cycle, dur, role));
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, Time dur, ArrayList<ResourceType> roleList) {
    	for (int i = 0; i < roleList.size(); i++)
            addTimeTableEntry(cycle, dur, roleList.get(i));
    }  
    
    /**
     * Adds a new entry with a single role.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param role Role that the resource plays during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, double dur, ResourceType role) {
    	addTimeTableEntry(cycle, new Time(model.getUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, double dur, ArrayList<ResourceType> roleList) {
    	addTimeTableEntry(cycle, new Time(model.getUnit(), dur), roleList);
    }  
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RES";
	}

    /*
     * (non-Javadoc)
     * @see es.ull.isaatc.simulation.Describable#getDescription()
     */
	public String getDescription() {
		return description;
	}
    /**
     * Represents the role that a resource plays at a specific time cycle.
     * @author Iván Castilla Rodríguez
     */
    class TimeTableEntry {
    	/** Cycle that characterizes this entry */
    	private final ModelCycle cycle;
        /** The long this resource plays this role every cycle */
    	private final Time duration;
        /** Role that the resource plays during this cycle */
    	private final ResourceType role;
        
        /** Creates a new instance of TimeTableEntry
         * @param cycle 
         * @param dur The long this resource plays this role every cycle
         * @param role Role that the resource plays during this cycle
         */
    	public TimeTableEntry(ModelCycle cycle, Time dur, ResourceType role) {
    		this.cycle = cycle;
    		this.duration = dur;
    		this.role = role;
    	}
        
        /**
         * Getter for property duration.
         * @return Value of property duration.
         */
        public Time getDuration() {
            return duration;
        }

        /**
         * Getter for property role.
         * @return Value of property role.
         */
        public ResourceType getRole() {
            return role;
        }
        
        @Override
        public String toString() {
            StringBuffer str = new StringBuffer();
            str.append(" | " + role.getDescription() + " | " + duration
                + " | " + cycle + "\r\n");
            return str.toString();
        }
        
    }

	class ClockOnEntry {
		private double init = 0;
		private double finish = 0;
		private double avCounter = 0;
		
		
		public ClockOnEntry(double init) {
			this.init = init;
			finish = 0;
			avCounter = 0;
		}

		public double getFinish() {
			return finish;
		}

		public void setFinish(double finish) {
			this.finish = finish;
		}

		public double getInit() {
			return init;
		}

		public void setInit(double init) {
			this.init = init;
		}

		public double getAvCounter() {
			return avCounter;
		}

		public void setAvCounter(double avCounter) {
			this.avCounter = avCounter;
		}

	}
}
