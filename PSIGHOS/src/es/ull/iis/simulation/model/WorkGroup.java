/**
 * 
 */
package es.ull.iis.simulation.model;

/**
 * A set of pairs &lt{@link ResourceType}, {@link Integer}&gt which defines how many resources 
 * from each type are required to do something (typically an {@link ActivityFlow}).
 * @author Iván Castilla Rodríguez
 */
public class WorkGroup implements ModelObject {
	public class Pair {
		public final ResourceType rt;
		public final int needed;
		
		public Pair(ResourceType rt, int needed) {
			this.rt = rt;
			this.needed = needed;
		}
	}

	protected final Pair[] pairs;
	
	/**
	 * 
	 */
	public WorkGroup() {
		// TODO: Check if it should be "null" or an empty array
		pairs = null;
	}

    /**
     * Creates a new instance of WorkGroup initializing the list of pairs
     * <resource type, needed resources> with one pair. 
     * @param rt Resource Type
     * @param needed Resources needed
     */    
    public WorkGroup(ResourceType rt, int needed) {
        this(new ResourceType[] {rt}, new int[] {needed});
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * <resource type, needed resources>.
     * @param rts The resource types which compounds this WG.
     * @param needs The amounts of resource types required by this WG.
     */    
    public WorkGroup(ResourceType[] rts, int []needs) {
    	this.pairs = new Pair[rts.length];
    	for (int i = 0; i < rts.length; i++)
    		pairs[i] = new Pair(rts[i], needs[i]);
    }

    /**
     * Creates a new instance of WorkGroup, initializing the list of pairs
     * {resource type, #needed resources}.
     * @param pairs The pairs <resource type, needed resources>.
     */    
    public WorkGroup(Pair[] pairs) {
    	this.pairs = pairs;
    }

	/**
     * Returns the amount of entries of the resource type table.
     * @return Amount of entries.
     */
    public int size() {
        return pairs.length;
    }
    
	@Override
	public String getObjectTypeIdentifier() {
		return "WG";
	}

	/**
	 * Returns the {@link Pair}s of this {@link WorkGroup}.
	 * @return the {@link Pair}s of this {@link WorkGroup}
	 */
	public Pair[] getPairs() {
		return pairs;
	}
}
