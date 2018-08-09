package es.ull.iis.simulation.retal.params;

/**
 * 
 * @author Iván Castilla Rodríguez
 *
 */
public final class CNVStageAndValue {
	private final CNVStage stage;
	private final long value;

	public CNVStageAndValue(CNVStage.Type type, CNVStage.Position position, long value) {
		this.stage = new CNVStage(type, position);
		this.value = value;
	}
	
	public CNVStage getStage() {
		return stage;
	}

	/**
	 * @return the type
	 */
	public CNVStage.Type getType() {
		return stage.getType();
	}

	/**
	 * @return the position
	 */
	public CNVStage.Position getPosition() {
		return stage.getPosition();
	}

	/**
	 * @return the value
	 */
	public long getValue() {
		return value;
	}
}