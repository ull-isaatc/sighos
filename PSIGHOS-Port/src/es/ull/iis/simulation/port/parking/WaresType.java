/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.EnumSet;

/**
 * @author Iv�n Castilla Rodr�guez
 * TODO: Un barco debe llevar m�s de un tipo de mercanc�a y distinguir entre carga y descarga
 */
public enum WaresType {
	FERTILIZER("Abonos",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS1)),
	AGRO_LIVESTOCK_FOOD("Agro-ganadero y alimentaci�n",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS4)),
	ENERGY("Energ�tico", 
			EnumSet.of(QuayType.RAOS1)),
	CONSTRUCTION("Material de Construcci�n",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS5)),
	NON_METALLIC_MINERALS("Minerales no met�licos",
			EnumSet.of(QuayType.RAOS3)),
	CHEMICAL("Qu�micos",
			EnumSet.of(QuayType.RAOS3, QuayType.RAOS5, QuayType.RAOS2)),
	SIDEROMETALURGY("Siderometal�rgico",
			EnumSet.of(QuayType.RAOS3, QuayType.NMONTANA)),
	VEHICLE("Veh�culos y elementos de transporte", // Currently not used
			EnumSet.noneOf(QuayType.class)),
	OTHER("Otras mercanc�as", 
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
