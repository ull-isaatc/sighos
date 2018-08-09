package es.ull.iis.simulation.retal.params;

public class VAProgressionPair {
	public long timeToChange;
	public double va;
	
	public VAProgressionPair(long timeToChange, double va) {
		this.timeToChange = timeToChange;
		this.va = va;
	}
	
	@Override
	public String toString() {
		return "(" + timeToChange + "):" + va;
	}
}