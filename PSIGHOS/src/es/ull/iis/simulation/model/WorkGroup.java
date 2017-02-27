/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * A set of pairs &lt{@link ResourceType}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link ActivityFlow}).
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup extends ModelObject {
	protected final ResourceType[] resourceTypes;
	protected final int[] needed;
		
	/**
	 * 
	 */
	public WorkGroup(Model model) {
		this(model, new ResourceType[0], new int[0]);
	}

    /**
     * Creates a new instance of WorkGroup initializing the list of pairs
     * <resource type, needed resources> with one pair. 
     * @param rt Resource Type
     * @param needed Resources needed
     */    
    public WorkGroup(Model model, ResourceType rt, int needed) {
        this(model, new ResourceType[] {rt}, new int[] {needed});
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * <resource type, needed resources>.
     * @param rts The resource types which compounds this WG.
     * @param needs The amounts of resource types required by this WG.
     */    
    public WorkGroup(Model model, ResourceType[] rts, int []needs) {
    	super(model, model.getWorkGroupList().size(), "WG");
    	this.resourceTypes = rts;
    	this.needed = needs;
		model.add(this);
    }

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return resourceTypes.length;
    }
    
    /**
     * Returns the resource type from the position ind of the table.
     * @param ind Index of the entry
     * @return The resource type from the position ind. 
     */
    public ResourceType getResourceType(int ind) {
        return resourceTypes[ind];
    }

    /**
     * Returns the needed amount of resources from the position ind of the table.
     * @param ind Index of the entry
     * @return The needed amount of resources from the position ind. 
     */
    public int getNeeded(int ind) {
        return needed[ind];
    }

    public int[] getNeeded() {
    	return needed;    	
    }
    
	@Override
	protected void assignSimulation(SimulationEngine simul) {
		// TODO Auto-generated method stub
		
	}
}
