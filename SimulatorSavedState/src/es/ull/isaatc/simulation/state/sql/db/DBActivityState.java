/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.ActivityState;
import es.ull.isaatc.simulation.state.State;
import es.ull.isaatc.simulation.state.ActivityState.ActivityQueueEntry;

/**
 * @author Roberto Muñoz
 * 
 */
public class DBActivityState extends DBState {
    
    /** activity table column names */
    protected static String ACT_ID = "activity_id";
    protected static String SIGHOS_ACT_ID = "sighos_activity_id";

    /** activityqueueentry table column names */
    protected static String SIGHOS_ELEMENT_ID = "sighos_element_id";
    protected static String SIGHOS_FLOW_ID = "sighos_flow_id";
    
    /** activity registry SQL update statement */
    protected static String UPDATE_STATEMENT = "INSERT INTO activity (simulation_id, sighos_activity_id) VALUES (?, ?)";
    
    /** simulation activities SQL query statement */
    protected static String QUERY_ACTIVITY_STATEMENT = "SELECT * FROM activity where simulation_id = ?";

    /** activity queue entry registry update SQL statement */
    protected static String UPDATE_QUEUE_ENTRY_STATEMENT = "INSERT INTO activityqueueentry (activity_id, sighos_element_id, sighos_flow_id) VALUES (?, ?, ?)";

    /** activity queue entry query SQL statement */
    protected static String QUERY_ACT_ENTRIES_STATEMENT = "SELECT * FROM activityqueueentry where activity_id = ?";
    
    public DBActivityState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    @Override
    public List<State> getState() throws SQLException {
	st = conn.prepareStatement(QUERY_ACTIVITY_STATEMENT);
	st.setInt(1, container.getPk());
	
	executeQuery();
	
	List<State> aStates = new ArrayList<State>();

        for (; cdr.next(); ) {
            pk = (Integer)cdr.getObject(ACT_ID);
            ActivityState aState = new ActivityState((Integer)cdr.getObject(SIGHOS_ACT_ID));
            aStates.add(aState);
            // retrieve the requested activities
            DBActivityQueueEntry dbActivityQueueEntry = new DBActivityQueueEntry(conn, this);
            aState.getQueue().addAll(dbActivityQueueEntry.getActivityQueueEntry(aState));
        }
        
	return aStates;
    }
    
    @Override
    public State getState(int pk) throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void storeState(State state) throws SQLException {
	ActivityState aState = (ActivityState) state;

	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, container.getPk());
	st.setInt(2, aState.getActId());

	executeUpdate();
	
	DBActivityQueueEntry dbQueueEntry = new DBActivityQueueEntry(conn, this);
	for (ActivityQueueEntry qEntry : aState.getQueue()) {    
	    dbQueueEntry.storeActivityQueueEntry(qEntry);
	}
    }

    /**
     *  
     * @author Roberto Muñoz
     */
    public class DBActivityQueueEntry extends DBSighosRegistry {

	public DBActivityQueueEntry(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

	/**
	 * Returns all the ActivityQueueEntry objects of a specific activity
	 * @param aState the actvity sate
	 * @return a list with the ActivityQueueEntry of the activity representad by aState
	 * @throws SQLException
	 */
	public List<ActivityQueueEntry> getActivityQueueEntry(ActivityState aState) throws SQLException {
	    ArrayList<ActivityQueueEntry> actEntryArray = new ArrayList<ActivityQueueEntry>();
	    st = conn.prepareStatement(QUERY_ACT_ENTRIES_STATEMENT);
	    st.setInt(1, container.getPk());

	    executeQuery();

	    for (; cdr.next(); ) {
		actEntryArray.add(aState.new ActivityQueueEntry((Integer)cdr.getObject(SIGHOS_FLOW_ID),
			(Integer)cdr.getObject(SIGHOS_ELEMENT_ID)));
	    }

	    return actEntryArray;
	}

	public void storeActivityQueueEntry(ActivityQueueEntry qEntry) throws SQLException {

	    st = conn.prepareStatement(UPDATE_QUEUE_ENTRY_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	    st.setInt(1, container.getPk());
	    st.setInt(2, qEntry.getElemId());
	    st.setInt(3, qEntry.getFlowId());

	    executeUpdate();
	}
    }
}
