package es.ull.iis.simulation.port.parking.json;

import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.json.JSONObject;

public class NewEvent implements JSONable {
    
    private final int id;
    private final String type;
    private final Map<String, Object> properties;

    public NewEvent(int id, String type) {
        this.id = id;
        this.type = type;
        this.properties = new TreeMap<>();
    }

    public int getId() {
        return this.id;
    }
    
    public String getType() {
        return this.type;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void addProperty(String key, Object property) {
        this.properties.put(key, property);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONObject propertiesJson = new JSONObject();
        for (Entry<String, Object> entry : this.properties.entrySet()) {
            propertiesJson.put(entry.getKey(), entry.getValue().toString());
        }
        json.put("id", this.id);
        json.put("type", this.type);
        json.put("properties", propertiesJson);
        return json;
    }

    public String toString() {
        final String representation = 
            "NewEvent=[" +
                "id=" + this.id +
                "type=" + this.type +
                "properties=" + this.properties.toString() +
            "]";
        return representation;
    }
}
