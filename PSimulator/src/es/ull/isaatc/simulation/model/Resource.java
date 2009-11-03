package es.ull.isaatc.simulation.model;

import java.util.ArrayList;
import java.util.Collection;

import es.ull.isaatc.simulation.common.ModelCycle;
import es.ull.isaatc.simulation.common.Time;
import es.ull.isaatc.simulation.common.TimeTableEntry;

/**
 * A resource is an element that becomes available at a specific simulation time and 
 * becomes unavailable at other simulation time. The availability of a resource is controlled
 * by means of timetable entries, which define a resource type and an availability cycle.
 * A resource finishes its execution when it has no longer valid timetable entries.
 * @author Carlos Martín Galán
 */
public class Resource extends VariableStoreModelObject implements es.ull.isaatc.simulation.common.Resource, VariableHandler {
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
    	addTimeTableEntry(cycle, new Time(model.getTimeUnit(), dur), role);
    }  

    /**
     * Adds a new entry with a several roles.
     * @param cycle Cycle that characterizes this entry
     * @param dur The long this resource plays this role every cycle expressed in the 
     * simulation time unit
     * @param roleList Roles that the resource play during this cycle
     */
    public void addTimeTableEntry(ModelCycle cycle, double dur, ArrayList<ResourceType> roleList) {
    	addTimeTableEntry(cycle, new Time(model.getTimeUnit(), dur), roleList);
    }  
    
    @Override
	public String getObjectTypeIdentifier() {
		return "RES";
	}

	@Override
	public String getDescription() {
		return description;
	}
    /**
	 * @return the timeTable
	 */
	@Override
	public ArrayList<TimeTableEntry> getTimeTableEntries() {
		return timeTable;
	}

	@Override
	public String getBody(String method) {
		return null;
	}

	@Override
	public String getCompleteMethod(String method) {
		return null;
	}

	@Override
	public String getImports() {
		return "";
	}

	@Override
	public Collection<String> getMethods() {
		return new ArrayList<String>();
	}

	@Override
	public void setImports(String imports) {
	}

	@Override
	public boolean setMethod(String method, String body) {
		return false;
	}
}
