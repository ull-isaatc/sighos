package es.ull.iis.simulation.port.parking.json;

import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.json.JSONObject;

public class UpdateEvent implements JSONable {

    private final int id;
    private final Map<String, Object> properties;

    public UpdateEvent(int id) {
        this.id = id;
        this.properties = new TreeMap<>();
    }

    public int getId() {
        return this.id;
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
