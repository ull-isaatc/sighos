package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import es.ull.isaatc.simulation.state.*;
import es.ull.isaatc.simulation.state.ResourceTypeState.ResourceListEntry;
import es.ull.isaatc.simulation.state.SimulationState.EventEntry;
import es.ull.isaatc.simulation.state.SimulationState.EventType;

public class DBSimulationState extends DBState {

    protected static String LAST_ELEM_ID = "last_elem_id";
    protected static String LAST_SF_ID = "last_single_flow_id";
    protected static String END_TS = "end_ts";
    
    protected static String SIGHOS_ELEM_ID = "sighos_element_id";
    protected static String TS = "ts";
    protected static String VALUE = "value";
    protected static String EVENT_TYPE = "event_type";
    
    /** simulation registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO simulation (last_elem_id, last_single_flow_id, end_ts) VALUES (?, ?, ?)";
    
    /** simulation registry query SQL statement string */
    protected static String QUERY_SIMULATION_STATEMENT = "SELECT * FROM simulation where simulation_id = ?";
    
    /** event registry update SQL statement string */
    protected static String UPDATE_EVENT_QUEUE_STATEMENT = "INSERT INTO event (simulation_id, sighos_element_id, ts, value, event_type) VALUES (?, ?, ?, ?, ?)";

    /** event registry query SQL statement string */
    protected static String QUERY_EVENT_QUEUE_STATEMENT = "SELECT * from event where simulation_id = ?";

    public DBSimulationState(Connection connection) throws SQLException {
	super(connection);
    }
    
    @Override
    public List<State> getState() throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public State getState(int pk) throws SQLException {
	st = conn.prepareStatement(QUERY_SIMULATION_STATEMENT);
	st.setInt(1, pk);
	
	this.pk = pk;
	
	executeQuery();
	
	cdr.next();

	SimulationState simState = new SimulationState((Integer)cdr.getObject(LAST_ELEM_ID), (Integer)cdr.getObject(LAST_SF_ID), (Double)cdr.getObject(END_TS));
	
	// retrieve the model activities
	DBActivityState dbActivityState = new DBActivityState(conn, this);
	simState.getAStates().addAll((Collection<? extends ActivityState>) dbActivityState.getState());

	// retrieve the elements
	DBElementState dbElementState = new DBElementState(conn, this);
	simState.getElemStates().addAll((Collection<? extends ElementState>) dbElementState.getState());

	// retrieve the resource types
	DBResourceTypeState dbResourceTypeState = new DBResourceTypeState(conn, this);
	simState.getRtStates().addAll((Collection<? extends ResourceTypeState>) dbResourceTypeState.getState());

	// retrieve the resources
	DBResourceState dbResourceState = new DBResourceState(conn, this);
	simState.getResStates().addAll((Collection<? extends ResourceState>) dbResourceState.getState());

	// retrieve the events
	DBEventEntry dbEventEntry = new DBEventEntry(conn, this);
	simState.getWaitQueue().addAll(dbEventEntry.getEventEntryList(simState));
	
	return simState;
    }

    @Override
    public void storeState(State state) throws SQLException {
	SimulationState simState = (SimulationState) state;
	
	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, simState.getLastElemId());
	st.setInt(2, simState.getLastSFId());
	st.setDouble(3, simState.getEndTs());
		
	executeUpdate();

	// store the activities
	DBActivityState dbActivityState = new DBActivityState(conn, this);
	for (ActivityState aState : simState.getAStates()) {
	    dbActivityState.storeState(aState);
	}

	// store the elements
	DBElementState dbElementState = new DBElementState(conn, this);
	for (ElementState eState : simState.getElemStates()) {
	    dbElementState.storeState(eState);
	}
	
	// store the resource types
	DBResourceTypeState dbResourceTypeState = new DBResourceTypeState(conn, this);
	for (ResourceTypeState rtState : simState.getRtStates()) {
	    dbResourceTypeState.storeState(rtState);
	}
	
	// store the resources
	DBResourceState dbResourceState = new DBResourceState(conn, this);
	for (ResourceState resState : simState.getResStates()) {
	    dbResourceState.storeState(resState);
	}

	// store the events
	DBEventEntry dbEventEntry = new DBEventEntry(conn, this);
	for (SimulationState.EventEntry eventEntry: simState.getWaitQueue()) {
	    dbEventEntry.storeEventEntry(eventEntry);
	}
    }
    
    class DBEventEntry extends DBSighosRegistry {
		
	public DBEventEntry(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

	public List<EventEntry> getEventEntryList(SimulationState simState) throws SQLException {
            st = conn.prepareStatement(QUERY_EVENT_QUEUE_STATEMENT);
            st.setInt(1, container.getPk());
            
            executeQuery();
            
	    List<EventEntry> eventEntryQueue = new ArrayList<EventEntry>();
            
            for (; cdr.next(); ) {
        	EventEntry eEntry = simState.new EventEntry(EventType.valueOf((String)cdr.getObject(EVENT_TYPE)),
        		(Integer)cdr.getObject(SIGHOS_ELEM_ID),
        		(Double)cdr.getObject(TS),
        		(Integer)cdr.getObject(VALUE)); 
                eventEntryQueue.add(eEntry);
            }
	    

	    
	    return eventEntryQueue;
	}

	public void storeEventEntry(EventEntry event) throws SQLException {
	    
	    st = conn.prepareStatement(UPDATE_EVENT_QUEUE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	    st.setInt(1, container.getPk());
	    st.setInt(2, event.getId());
	    st.setDouble(3, event.getTs());
	    st.setInt(4, event.getValue());
	    st.setString(5, event.getType().toString());

	    executeUpdate();	    
	}
    }
}
