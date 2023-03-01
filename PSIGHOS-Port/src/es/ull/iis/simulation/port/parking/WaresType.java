/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.EnumSet;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Un barco debe llevar más de un tipo de mercancía
 */
public enum WaresType {
	TYPE1("Cereal",  
			RandomVariateFactory.getInstance("UniformVariate", 6000, 7000), 200.0, 0.5,
			// TODO: Sacar los muelles de la hoja "Analisis_DistribucionMercancias, pestaña "CARGA (TOTAL MERCANCÍA)
			EnumSet.of(QuayType.QUAY1, QuayType.QUAY2)),
	TYPE2("Carbon", 
			RandomVariateFactory.getInstance("UniformVariate", 6000, 7000), 200.0, 0.5,
			EnumSet.of(QuayType.QUAY3, QuayType.QUAY4));

	private final String description;
	private final RandomVariate typicalVesselLoad;
	private final double tonesPerTruck; 
	private final double proportion; 
	private final EnumSet<QuayType> potentialQuays;
	
	private WaresType(String description, RandomVariate typicalVesselLoad, double tonesPerTruck, double proportion, EnumSet<QuayType> potentialQuays) {
		this.description = description;
		this.typicalVesselLoad = typicalVesselLoad;
		this.tonesPerTruck = tonesPerTruck;
		this.proportion = proportion;
		this.potentialQuays = potentialQuays;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the typicalLoad
	 */
	public RandomVariate getTypicalVesselLoad() {
		return typicalVesselLoad;
	}

	/**
	 * @return the tonesPerTruck
	 */
	public double getTonesPerTruck() {
		return tonesPerTruck;
	}

	public double getProportion() {
		return proportion;
	}

	public EnumSet<QuayType> getPotentialQuays() {
		return potentialQuays;
	}

}
