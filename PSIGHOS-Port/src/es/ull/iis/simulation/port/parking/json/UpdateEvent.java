package es.ull.iis.simulation.port.parking.json;

import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONObject;

public class UpdateEvent {

    private int id;
    private Map<String, Object> properties;

    public UpdateEvent(int id, Map<String, Object> properties) {
        this.id = id;
        this.properties = properties;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Object> getProperties() {
        return this.properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONObject propertiesJson = new JSONObject();
        for (Entry<String, Object> entry : this.properties.entrySet()) {
            propertiesJson.put(entry.getKey(), entry.getValue().toString());
        }
        json.put("id", this.id);
        json.put("properties", propertiesJson);
        return json;
    }

    public String toString() {
        final String representation =
            "UpdateEvent=[" + 
                "id=" + this.id + 
                "properties=" + this.properties.toString() +
            "]";
        return representation;
    }
}
