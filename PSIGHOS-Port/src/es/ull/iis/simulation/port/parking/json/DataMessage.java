package es.ull.iis.simulation.port.parking.json;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataMessage implements JSONable {

    private final String time;
    private final List<NewEvent> newEvents;
    private final List<UpdateEvent> updateEvents;
    private final List<DeleteEvent> deleteEvents;

    public DataMessage(String time) {
        this.time = time;
        this.newEvents = new ArrayList<>();
        this.updateEvents = new ArrayList<>();
        this.deleteEvents = new ArrayList<>();
    }

    public String getTime() {
        return this.time;
    }

    public void addEvent(NewEvent ev) {
    	newEvents.add(ev);
    }
    
    public List<NewEvent> getNewEvents() {
        return this.newEvents;
    }

    public void addEvent(UpdateEvent ev) {
    	updateEvents.add(ev);
    }
    
    public List<UpdateEvent> getUpdateEvents() {
        return this.updateEvents;
    }

    public void addEvent(DeleteEvent ev) {
    	deleteEvents.add(ev);
    }
    
    public List<DeleteEvent> getDeleteEvents() {
        return this.deleteEvents;
    }
    
    public boolean hasEvents() {
    	return (newEvents.size() > 0 || updateEvents.size() > 0 || deleteEvents.size() > 0);
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        JSONArray newEvents = new JSONArray();
        for (NewEvent event : this.newEvents) {
            newEvents.put(event.toJson());
        }
        JSONArray updateEvents = new JSONArray();
        for (UpdateEvent event : this.updateEvents) {
            updateEvents.put(event.toJson());
        }
        JSONArray deleteEvents = new JSONArray();
        for (DeleteEvent event : this.deleteEvents) {
            deleteEvents.put(event.getId());
        }
        json.put("time", this.time);
        if (!newEvents.isEmpty()) {
            json.put("new", newEvents);
        }
        if (!updateEvents.isEmpty()) {
            json.put("update", updateEvents);
        }
        if (!deleteEvents.isEmpty()) {
            json.put("delete", deleteEvents);
        }
        return json;
    }

    @Override
    public String toString() {
        return "DataMessage [" + "time=" + time + ", " + "newEvents=" + newEvents + ", "
                + "updateEvents=" + updateEvents + ", " + "deleteEvents=" + deleteEvents + "]";
    }
}
