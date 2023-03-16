package es.ull.iis.simulation.port.parking.json;

import org.json.JSONObject;

public class DeleteEvent implements JSONable {
    
    private final  int id;

    public DeleteEvent(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        //json.
        return json;
    }

    public String toString() {
        final String representation = 
            "DeleteEvent=[" +
                "id=" + this.id +
            "]";
        return representation;
    }
}
