package es.ull.iis.simulation.hta.osdi;

/**
 * A simple class to represent a property in an ontology
 * @author David Prieto González
 *
 */
public class PropertyData {
	private String name;
	private String value;
	private String type;

	public PropertyData (String name) {
		this.name = name;
		this.value = null;
		this.type = null;
	}
	
	public PropertyData(String name, String value) {
		this.name = name;
		this.value = value;
		this.type = null;
	}

	public PropertyData(String name, String value, String type) {
		this.name = name;
		this.value = value;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
	@Override
	public String toString() {
		return String.format("%s[%s]", getValue(), getType());
	}
	
	public PropertyData clone () {
		return new PropertyData (
				this.getName() != null ? new String(this.getName()) : null, 
				this.getValue() != null ? new String(this.getValue()) : null, 
				this.getType() != null ? new String(this.getType()) : null);
	}
}
