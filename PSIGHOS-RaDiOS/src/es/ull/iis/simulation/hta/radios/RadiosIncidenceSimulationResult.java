package es.ull.iis.simulation.hta.radios;

public class RadiosIncidenceSimulationResult {
	private String nameDataSheet;
	private String valueDataSheet;

	public RadiosIncidenceSimulationResult(String name, String value) {
		this.nameDataSheet = name;
		this.valueDataSheet = value;
	}

	public String getNameDataSheet() {
		return nameDataSheet;
	}

	public void setNameDataSheet(String nameDataSheet) {
		this.nameDataSheet = nameDataSheet;
	}

	public String getValueDataSheet() {
		return valueDataSheet;
	}

	public void setValueDataSheet(String valueDataSheet) {
		this.valueDataSheet = valueDataSheet;
	}

}
