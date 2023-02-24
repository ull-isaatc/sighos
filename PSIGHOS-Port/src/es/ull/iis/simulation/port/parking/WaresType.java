/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.EnumSet;

import es.ull.iis.function.TimeFunction;
import es.ull.iis.function.TimeFunctionFactory;
import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Un barco debe llevar más de un tipo de mercancía
 */
public enum WaresType {
	TYPE1("Cereal", RandomVariateFactory.getInstance("UniformVariate", 20, 25), // TODO: Dejar las toneladas por camión fijo en 20 independientemente del tipo de mercancía 
			RandomVariateFactory.getInstance("UniformVariate", 6000, 7000), 200.0, 0.5,
			TimeFunctionFactory.getInstance("UniformVariate", 30, 40), // TODO: No relacionar estos tiempos con el tipo de mercancía 
			TimeFunctionFactory.getInstance("UniformVariate", 20, 30), // TODO: No relacionar estos tiempos con el tipo de mercancía
			// TODO: Sacar los muelles de la hoja "Analisis_DistribucionMercancias, pestaña "CARGA (TOTAL MERCANCÍA)
			EnumSet.of(QuayType.QUAY1, QuayType.QUAY2),
			new double[] {0.2, 0.3, 0.5}), // TODO: No relacionar estos tiempos con el tipo de mercancía
	TYPE2("Carbon", RandomVariateFactory.getInstance("UniformVariate", 20, 25),
			RandomVariateFactory.getInstance("UniformVariate", 6000, 7000), 200.0, 0.5,
			TimeFunctionFactory.getInstance("UniformVariate", 30, 40),
			TimeFunctionFactory.getInstance("UniformVariate", 20, 30),
			EnumSet.of(QuayType.QUAY3, QuayType.QUAY4),
			new double[] {0.4, 0.4, 0.2});

	private final String description;
	private final RandomVariate typicalVesselLoad;
	private final RandomVariate typicalTruckLoad;
	private final TimeFunction paperWorkIn;
	private final TimeFunction paperWorkOut;
	private final double tonesPerTruck; 
	private final double proportion; 
	private final EnumSet<QuayType> potentialQuays;
	private final double[] proportionPerTruckSource;
	
	private WaresType(String description, RandomVariate typicalTruckLoad, RandomVariate typicalVesselLoad, double tonesPerTruck, double proportion, TimeFunction paperWorkIn, TimeFunction paperWorkOut, EnumSet<QuayType> potentialQuays, double[] proportionPerTruckSource) {
		this.description = description;
		this.typicalTruckLoad = typicalTruckLoad;
		this.typicalVesselLoad = typicalVesselLoad;
		this.tonesPerTruck = tonesPerTruck;
		this.proportion = proportion;
		this.paperWorkIn = paperWorkIn;
		this.paperWorkOut = paperWorkOut;
		this.potentialQuays = potentialQuays;
		this.proportionPerTruckSource = proportionPerTruckSource;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the typicalTruckLoad
	 */
	public RandomVariate getTypicalTruckLoad() {
		return typicalTruckLoad;
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

	public TimeFunction getPaperWorkIn() {
		return paperWorkIn;
	}

	public TimeFunction getPaperWorkOut() {
		return paperWorkOut;
	}

	public EnumSet<QuayType> getPotentialQuays() {
		return potentialQuays;
	}

	/**
	 * @return the proportionPerTruckSource
	 */
	public double[] getProportionPerTruckSource() {
		return proportionPerTruckSource;
	}
}
