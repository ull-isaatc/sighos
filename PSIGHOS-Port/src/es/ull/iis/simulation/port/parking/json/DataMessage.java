package es.ull.iis.simulation.port.parking.json;

import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataMessage {

    private String time;
    private List<NewEvent> newEvents;
    private List<UpdateEvent> updateEvents;
    private List<DeleteEvent> deleteEvents;

    public DataMessage(String time, List<NewEvent> newEvents, List<UpdateEvent> updateEvents,
            List<DeleteEvent> deleteEvents) {
        this.time = time;
        this.newEvents = newEvents;
        this.updateEvents = updateEvents;
        this.deleteEvents = deleteEvents;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<NewEvent> getNewEvents() {
        return this.newEvents;
    }

    public void setNewEvents(List<NewEvent> newEvents) {
        this.newEvents = newEvents;
    }

    public List<UpdateEvent> getUpdateEvents() {
        return this.updateEvents;
    }

    public void setUpdateEvents(List<UpdateEvent> updateEvents) {
        this.updateEvents = updateEvents;
    }

    public List<DeleteEvent> getDeleteEvents() {
        return this.deleteEvents;
    }

    public void setDeleteEvents(List<DeleteEvent> deleteEvents) {
        this.deleteEvents = deleteEvents;
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
