package es.ull.iis.simulation.retal.params;

public final class CNVStage implements Comparable<CNVStage> {
	/**
	 * Position of exudative lesions in the choroidal neovascularization subtype of the age-related macular degeneration
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public enum Position {
		EF,	// Extrafoveal
		JF,	// Juxtafoveal
		SF	// Subfoveal
	}
	/**
	 * Type of exudative lesions in the choroidal neovascularization subtype of the age-related macular degeneration
	 * @author Iván Castilla Rodríguez
	 *
	 */
	public enum Type {
		OCCULT,	// Occult
		MC,		// Minimally classic
		PC		// Predominantly classic
	}

	public final static CNVStage[] ALL_STAGES = new CNVStage[Position.values().length * Type.values().length];
	static {
		int i = 0;
		for (CNVStage.Type t : CNVStage.Type.values()) {
			for (CNVStage.Position pos : CNVStage.Position.values()) {
				ALL_STAGES[i++] = new CNVStage(t, pos);
			}
		}
	}
	private final Type type;
	private final Position position;
	
	public CNVStage(Type type, Position position) {
		this.type = type;
		this.position = position;
	}
	
	@Override
	public boolean equals(Object obj) {
		CNVStage other = (CNVStage) obj; 
		return (type == other.type) && (position == other.position);
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}

	/**
	 * @return the position
	 */
	public Position getPosition() {
		return position;
	}

	@Override
	public int compareTo(CNVStage o) {
		final int firstCompare = type.compareTo(o.type); 
		if (firstCompare == 0)
			return position.compareTo(o.position);
		return firstCompare;
	}
}
