package es.ull.iis.simulation.hta.osdi;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.ull.iis.simulation.hta.osdi.exceptions.TranspilerException;

public class NodeData {
	private String name;
	private String type;
	private Map<String, PropertyData> properties;

	private Map<String, PropertyData> initProperties() throws TranspilerException {
		this.addProperty(OSDiNames.DataProperty.HAS_PROBABILITY.getName(), String.format(Locale.US, Constants.CONSTANT_DOUBLE_FORMAT_STRING_3DEC, 1.0), Constants.CONSTANT_DOUBLE_TYPE);
		this.addProperty(OSDiNames.ObjectProperty.HAS_COST.getName(), String.format(Locale.US, Constants.CONSTANT_DOUBLE_FORMAT_STRING_3DEC, 0.0), Constants.CONSTANT_DOUBLE_TYPE);
		this.addProperty(OSDiNames.DataProperty.HAS_TEMPORAL_BEHAVIOR.getName(), OSDiNames.DataPropertyRange.TEMPORAL_BEHAVIOR_ANNUAL_VALUE.getName(), Constants.CONSTANT_STRING_TYPE);
		return this.getProperties();
	}

	public NodeData(String name) {
		this.name = name;
		this.type = null;
		try {
			this.properties = initProperties();
		} catch (TranspilerException e) {
		}
	}

	public NodeData(String name, String type) {
		this.name = name;
		this.type = type;
		try {
			this.properties = initProperties();
		} catch (TranspilerException e) {
		}
	}

	public NodeData(String name, String type, Map<String, PropertyData> properties) {
		this.name = name;
		this.type = type;
		this.properties = properties;
	}

	public String getName() {
		return name;
	}

	public NodeData setName(String name) {
		this.name = name;
		return this;
	}

	public String getType() {
		return type;
	}

	public NodeData setType(String type) {
		this.type = type;
		return this;
	}

	public Map<String, PropertyData> getProperties() {
		if (properties == null) {
			properties = new HashMap<String, PropertyData>();
		}
		return properties;
	}

	public NodeData setProperties(Map<String, PropertyData> properties) {
		this.properties = properties;
		return this;
	}

	public NodeData addProperties(Map<String, PropertyData> properties) throws TranspilerException {
		if (properties != null) {
			for (String key : properties.keySet()) {
				PropertyData propertyData = (PropertyData) properties.get(key);
				addProperty(key, new String(propertyData.getValue()), new String(propertyData.getType()));
			}
		}
		return this;
	}

	public NodeData addProperty(String key, String value, String type) throws TranspilerException {
		if (type == null || type.isEmpty()) {
			addProperty(key, value);
		} else {
			this.getProperties().put(key, new PropertyData(key, value, type));
		}
		return this;
	}

	public NodeData addProperty(String key, String value) throws TranspilerException {
		PropertyData propertyData = new PropertyData(key);
		String[] splitValue = parsePropertyValue(value);

		if (splitValue != null) {
			if (splitValue.length == 1) {
				propertyData.setValue(splitValue[0]);
			} else if (splitValue.length == 2) {
				propertyData.setValue(splitValue[0]);
				propertyData.setType(splitValue[1]);
			}
		}

		this.getProperties().put(key, propertyData);
		return this;
	}

	private String[] parsePropertyValue(String propertyValue) {
		String[] splitValue = { propertyValue };

		if (propertyValue != null) {
			if (propertyValue.matches(Constants.REGEX_ANYEXPRESION_TYPE)) {
				splitValue = propertyValue.split(Constants.CONSTANT_SPLIT_TYPE);
			}
		}

		return splitValue;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(String.format("%s", this.getName()));

		if (this.getProperties() != null) {
			StringBuilder tmp = new StringBuilder("{");
			for (String property : this.getProperties().keySet()) {
//				if (property.equals(Constants.DATAPROPERTY_PROBABILITY)) {
//					Double value = Double.valueOf(this.getProperties().get(Constants.DATAPROPERTY_PROBABILITY).getValue());
//					if (getName().startsWith(Constants.CONSTANT_NEGATION)) {
//						value = 1.0 - value;
//					}
//					tmp.append(String.format(" p=%s", value));					
//				}
				if (property.equals(Constants.CUSTOM_PROPERTY_CUMULATIVE_PROBABILITY)) {
					tmp.append(String.format(" P=%s", this.getProperties().get(Constants.CUSTOM_PROPERTY_CUMULATIVE_PROBABILITY).getValue()));
				}

//				if (property.equals(Constants.DATAPROPERTY_COST)) {
//					tmp.append(String.format(" c=%s", this.getProperties().get(Constants.DATAPROPERTY_COST).getValue()));					
//				}
				if (property.equals(Constants.CUSTOM_PROPERTY_CUMULATIVE_COST)) {
					tmp.append(String.format(" C=%s", this.getProperties().get(Constants.CUSTOM_PROPERTY_CUMULATIVE_COST).getValue()));					
				}

//				if (property.equals(Constants.DATAPROPERTY_ONSET_AGE)) {
//					tmp.append(String.format(" i=%s", this.getProperties().get(Constants.DATAPROPERTY_ONSET_AGE).getValue()));					
//				}
//				if (property.equals(Constants.DATAPROPERTY_END_AGE)) {
//					tmp.append(String.format(" e=%s", this.getProperties().get(Constants.DATAPROPERTY_END_AGE).getValue()));					
//				}

//				if (property.equals(Constants.DATAPROPERTY_VALUE)) {
//					tmp.append(String.format(" q=%s", this.getProperties().get(Constants.DATAPROPERTY_VALUE).getValue()));					
//				}				
				if (property.equals(Constants.CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM_WITH_DISCOUNT)) {
					tmp.append(String.format(" Q=%s", this.getProperties().get(Constants.CUSTOM_PROPERTY_UTILITY_VALUE_MINIMUM_WITH_DISCOUNT).getValue()));					
				}				
			}
			tmp.append(" }");
			if (!tmp.toString().equals("{ }")) {
				sb.append(tmp);
			}
		}

		return sb.toString();
	}
}
