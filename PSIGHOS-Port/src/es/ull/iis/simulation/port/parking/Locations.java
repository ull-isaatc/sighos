/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.TreeMap;

import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla
 *
 */
public enum Locations {
	TRUCK_SRC_SOUTH1(new Node("Origen camiones 1 (sur)"), 43.44270552582938, -3.8389108757412127),
	TRUCK_SRC_SOUTH2(new Node("Origen camiones 2 (sur)"), 43.4356052540279, -3.84179664716269),
	TRUCK_SRC_SOUTH3(new Node("Origen camiones 3 (sur)"), 43.43237515979002, -3.83685510817604),
	TRUCK_SRC_NORTH1(new Node("Origen camiones 4 (norte)"), 43.4572112472372, -3.8096205632077),
	TRUCK_WAIT_AREA(new Node("Zona de espera de los camiones (rectángulo azul)"), 43.444779414992, -3.83004650128634),
	TRUCK_EXIT_POINT(new Node("Punto de salida de los camiones del muelle"), 43.444779414992, -3.83004650128634), // Ad hoc: uses the same coordinates as TRUCK_WAIT_AREA
	TRUCK_TRANSSHIPMENT_AREA(new Node("Zona de carga/descarga de los camiones (rectángulo rojo)"), 43.447391830393, -3.82025441376013),
	VESSEL_SRC(new Node("Origen de barcos"), 43.4646423872297, -3.7447206645557),
	VESSEL_ANCHORAGE(new Node("Zona de fondeo"), 43.4464086268505, -3.79531152421035),
	VESSEL_QUAY1(new Node("Raos 1"), 43.4448322301184, -3.8258625341629),
	VESSEL_QUAY2(new Node("Raos 2"), 43.445443143835, -3.82344339280911),
	VESSEL_QUAY3(new Node("Raos 3"), 43.4450613234851, -3.81713258927749),
	VESSEL_QUAY4(new Node("Raos 4"), 43.4461558687067, -3.81473097793352),
	VESSEL_QUAY5(new Node("Raos 5"), 43.4480012780134, -3.81892065027812),
	VESSEL_QUAY6(new Node("Raos 6"), 43.4434831071257, -3.82759800556273),
	VESSEL_QUAY7(new Node("Raos 7"), 43.4420448579559, -3.82177804225204),
	VESSEL_QUAY8(new Node("Raos 8"), 43.4420448579559, -3.82177804225204),
	VESSEL_QUAY9(new Node("Raos 8 Este"), 43.4461558687067, -3.81473097793352);
	
	private final static TreeMap<Node, Locations> REVERSE_MAPPING = new TreeMap<>();
	static {
		for (Locations loc : Locations.values())
			REVERSE_MAPPING.put(loc.node, loc);
	}
	private final Node node;
	private final double latitude;
	private final double longitude;
	
	/**
	 * @param node
	 * @param latitude
	 * @param longitude
	 */
	private Locations(Node node, double latitude, double longitude) {
		this.node = node;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	/**
	 * @return the node
	 */
	public Node getNode() {
		return node;
	}

	/**
	 * @return the latitude
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public double getLongitude() {
		return longitude;
	}
	
	public static Locations getLocationForNode(Node node) {
		return REVERSE_MAPPING.get(node);
	}
	
}
