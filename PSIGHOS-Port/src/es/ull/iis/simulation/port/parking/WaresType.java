/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.EnumSet;

/**
 * @author Iván Castilla Rodríguez
 * TODO: Un barco debe llevar más de un tipo de mercancía y distinguir entre carga y descarga
 */
public enum WaresType {
	SIDEROMETALURGY("Siderometalúrgico",
			EnumSet.of(QuayType.RAOS3, QuayType.NMONTANA)),
	AGRO_LIVESTOCK_FOOD("Agro-ganadero y alimentación",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS4)),
	CONSTRUCTION("Material de Construcción",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS5)),
	ENERGY("Energético", 
			EnumSet.of(QuayType.RAOS1)),
	CHEMICAL("Químicos",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS5, QuayType.RAOS2)),
	NON_METALLIC_MINERALS("Minerales no metálicos",
			EnumSet.of(QuayType.RAOS3)),
	FERTILIZER("Abonos",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS1)),
	OTHER("Otras mercancías", 
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS5));

	private final String description;
	private final EnumSet<QuayType> potentialQuays;
	
	private WaresType(String description, EnumSet<QuayType> potentialQuays) {
		this.description = description;
		this.potentialQuays = potentialQuays;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public EnumSet<QuayType> getPotentialQuays() {
		return potentialQuays;
	}

}
