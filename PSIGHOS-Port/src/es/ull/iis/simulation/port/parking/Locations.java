/**
 * 
 */
package es.ull.iis.simulation.port.parking;

import java.util.TreeMap;

import es.ull.iis.simulation.model.location.Node;

/**
 * @author Iván Castilla
 */
public enum Locations {
	TRUCK_SRC_SOUTH1(	new Node("TRUCK SOURCE 1 (SOUTH)"), 		"43.44270552582938", "-3.8389108757412127"),
	TRUCK_SRC_SOUTH2(	new Node("TRUCK SOURCE 2 (SOUTH)"), 		"43.43560525402792", "-3.8417966471626963"),
	TRUCK_SRC_SOUTH3(	new Node("TRUCK SOURCE 3 (SOUTH)"),			"43.43237515979002", "-3.8368551081760405"),
	TRUCK_SRC_NORTH1(	new Node("TRUCK SOURCE 4 (NORTH)"), 		"43.45721124723722", "-3.8096205632077074"),
	TRUCK_WAIT_AREA(	new Node("TRUCK WAITING AREA"), 			"43.44477941499202", "-3.8300465012863407"),
	TRUCK_EXIT_POINT(	new Node("TRUCK EXIT POINT"), 				"43.44477941499202", "-3.8300465012863407"), // Ad hoc: uses the same coordinates as TRUCK_WAIT_AREA
	TRUCK_TRANSSHIPMENT_AREA(new Node("TRUCK TRANSSHIPMENT AREA"), 	"43.447391830393045", "-3.8202544137601357"),
	VESSEL_SRC(			new Node("VESSEL SOURCE"), 					"43.46464238722976", "-3.7447206645557043"),
	VESSEL_ANCHORAGE(	new Node("VESSEL ANCHORAGE"), 				"43.44640862685058", "-3.7953115242103532"),
	VESSEL_QUAY_RAOS1(	new Node("RAOS 1"), 						"43.44483223011844", "-3.8258625341629044"),
	VESSEL_QUAY_RAOS2(	new Node("RAOS 2"), 						"43.44544314383501", "-3.823443392809118"),
	VESSEL_QUAY_RAOS3(	new Node("RAOS 3"), 						"43.44506132348514", "-3.8171325892774988"),
	VESSEL_QUAY_RAOS4(	new Node("RAOS 4"), 						"43.44615586870671", "-3.814730977933522"),
	VESSEL_QUAY_RAOS5(	new Node("RAOS 5"), 						"43.44800127801341", "-3.8189206502781246"),
	VESSEL_QUAY_NMONTANA(new Node("NUEVA MONTAÑA"), 				"43.443483107125715", "-3.8275980055627334");
	
	private final static TreeMap<Node, Locations> REVERSE_MAPPING = new TreeMap<>();
	static {
		for (Locations loc : Locations.values())
			REVERSE_MAPPING.put(loc.node, loc);
	}
	private final Node node;
	private final String latitude;
	private final String longitude;
	
	/**
	 * @param node
	 * @param latitude
	 * @param longitude
	 */
	private Locations(Node node, String latitude, String longitude) {
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
	public String getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public String getLongitude() {
		return longitude;
	}
	
	public static Locations getLocationForNode(Node node) {
		return REVERSE_MAPPING.get(node);
	}
	
}
