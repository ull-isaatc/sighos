/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.ElementState;
import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.SequenceFlowState;
import es.ull.isaatc.simulation.state.SimultaneousFlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;
import es.ull.isaatc.simulation.state.State;

/**
 * @author Roberto Muñoz
 *
 */
public class DBElementState extends DBState {

    protected static String ELEMENT_ID = "element_id";
    protected static String SIGHOS_ELEMENT_ID = "sighos_element_id";
    protected static String ELEMENT_TYPE_ID = "element_type_id";
    protected static String CURR_SF_ID = "current_single_flow_id";
    protected static String FLOW_STATE_TYPE = "flow_state_type";
    protected static String FLOW_STATE_ID = "flow_state_id";
    protected static String SIGHOS_ACT_ID = "sighos_activity_id";
    protected static String PRESENTIAL = "presential";
    
    /** element registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO element (simulation_id, sighos_element_id, element_type_id, current_single_flow_id, flow_state_type, flow_state_id) VALUES (?, ?, ?, ?, ?, ?)";

    /** simulation elements query SQL statement string */
    protected static String QUERY_ELEMENT_STATEMENT = "SELECT * FROM element where simulation_id = ?";

    /** pending activity registry update SQL statement string */
    protected static String UPDATE_PENDING_ACT_STATEMENT = "INSERT INTO pendingactivities (element_id, sighos_activity_id) VALUES (?, ?)";

    /** pending activities sql query */
    protected static String QUERY_PENDING_ACT_STATEMENT = "SELECT sighos_activity_id FROM pendingactivities where element_id = ?";
 
    /** requested activity registry update SQL statement string */
    protected static String UPDATE_REQUESTED_ACT_STATEMENT = "INSERT INTO requestedactivities (element_id, sighos_activity_id, presential) VALUES (?, ?, ?)";

    /** requested activities sql query */
    protected static String QUERY_REQUESTED_ACT_PRE_STATEMENT = "SELECT * FROM requestedactivities where element_id = ? and presential = 1";
    protected static String QUERY_REQUESTED_ACT_NONPRE_STATEMENT = "SELECT * FROM requestedactivities where element_id = ? and presential = 0";
    
    /**
     * @param connection
     * @param container
     * @throws SQLException
     */
    public DBElementState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    @Override
    public List<State> getState() throws SQLException {
	st = conn.prepareStatement(QUERY_ELEMENT_STATEMENT);
	st.setInt(1, container.getPk());
	
	executeQuery();
	
	List<State> eStates = new ArrayList<State>();

        for (; cdr.next(); ) {
            pk = (Integer)cdr.getObject(ELEMENT_ID);
            // retrieve pending activities
            DBPendingActivity dbPendingActivity = new DBPendingActivity(conn, this);
            DBRequestedActivity dbRequestedActivity = new DBRequestedActivity(conn, this);
            String flowStateType = (String)cdr.getObject(FLOW_STATE_TYPE);
            
            DBState dbFlowState = null;
            if (flowStateType.equals("SEQ")) {  // sequence flow
        	dbFlowState= new DBSequenceFlowState(conn, this);
            }
            else if (flowStateType.equals("SIM")) {  // simulatneous flow
        	dbFlowState= new DBSimultaneousFlowState(conn, this);
            }
            else if (flowStateType.equals("SIN"))  { // single flow
        	dbFlowState = new DBSingleFlowState(conn, this);
            }
            
            ElementState eState;
            int currSFId = (Integer)cdr.getObject(CURR_SF_ID);
            if (currSFId == -1)
        	eState = new ElementState((Integer)cdr.getObject(SIGHOS_ELEMENT_ID),
        	    (Integer)cdr.getObject(ELEMENT_TYPE_ID),
        	    (FlowState)dbFlowState.getState((Integer)cdr.getObject(FLOW_STATE_ID)),
        	    dbPendingActivity.getPendingActivities(),
        	    dbRequestedActivity.getRequestedActivities());
            else
        	eState = new ElementState((Integer)cdr.getObject(SIGHOS_ELEMENT_ID),
            	    (Integer)cdr.getObject(ELEMENT_TYPE_ID),
            	    (FlowState)dbFlowState.getState((Integer)cdr.getObject(FLOW_STATE_ID)),
            	    dbPendingActivity.getPendingActivities(),
            	    dbRequestedActivity.getRequestedActivities(),
            	    currSFId);
        	
            eStates.add(eState);
        }
        
	return eStates;
    }
    
    @Override
    public State getState(int pk) throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public void storeState(State state) throws SQLException {
	ElementState eState = (ElementState) state;
	String flowStateType = "";
	int flowStateId = -1;
	
	// store the flow state
	if (eState.getFlowState() instanceof SequenceFlowState) {
	    DBSequenceFlowState dbSequenceFlowState = new DBSequenceFlowState(conn, this);
	    dbSequenceFlowState.storeState(eState.getFlowState());
	    flowStateType = "SEQ";
	    flowStateId = dbSequenceFlowState.getPk();
	}
	else if (eState.getFlowState() instanceof SimultaneousFlowState) {
	    DBSimultaneousFlowState dbSimultaneousFlowState = new DBSimultaneousFlowState(conn, this);
	    dbSimultaneousFlowState.storeState(eState.getFlowState());
	    flowStateType = "SIM";
	    flowStateId = dbSimultaneousFlowState.getPk();
	}
	else if (eState.getFlowState() instanceof SingleFlowState) {
	    DBSingleFlowState dbSingleFlowState = new DBSingleFlowState(conn, this);
	    dbSingleFlowState.storeState(eState.getFlowState());
	    flowStateType = "SIN";
	    flowStateId = dbSingleFlowState.getPk();
	}
	
	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, container.getPk());
	st.setInt(2, eState.getElemId());
	st.setInt(3, eState.getElemTypeId());
	st.setInt(4, eState.getCurrentSFId());
	st.setString(5, flowStateType);
	st.setInt(6, flowStateId);
		
	executeUpdate();
	
	DBPendingActivity dbPendingActivity = new DBPendingActivity(conn, this);
	dbPendingActivity.storePendingActivities(eState.getPending());

	DBRequestedActivity dbRequestedActivity = new DBRequestedActivity(conn, this);
	dbRequestedActivity.storeRequestedActivities(eState.getRequested()[0], 1);
	dbRequestedActivity.storeRequestedActivities(eState.getRequested()[1], 0);	
    }
    
    public class DBPendingActivity extends DBSighosRegistry {

	public DBPendingActivity(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

        public int[]getPendingActivities() throws SQLException {

            st = conn.prepareStatement(QUERY_PENDING_ACT_STATEMENT);
            st.setInt(1, container.getPk());
	
            executeQuery();

            cdr.last();
            int pendingActivities[] = new int[cdr.getRow()];
            cdr.beforeFirst();
            
            for (int i = 0; cdr.next(); i++) {
        	pendingActivities[i] = (Integer)cdr.getObject(1);
            }
            
            return pendingActivities;
	}

	public void storePendingActivities(int actId[]) throws SQLException {
	    for (int i : actId) {
		st = conn.prepareStatement(UPDATE_PENDING_ACT_STATEMENT, Statement.RETURN_GENERATED_KEYS);
		st.setInt(1, container.getPk());
		st.setInt(2, i);

		executeUpdate();
	    }
	}
    }

    public class DBRequestedActivity extends DBSighosRegistry {

	public DBRequestedActivity(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

        public int[][]getRequestedActivities() throws SQLException {
            int requestedActivities[][] = new int[2][];
            
            st = conn.prepareStatement(QUERY_REQUESTED_ACT_PRE_STATEMENT);
            st.setInt(1, container.getPk());
	
            executeQuery();

            cdr.last();
            requestedActivities[0] = new int[cdr.getRow()];
            cdr.beforeFirst();
            
            for (int i = 0; cdr.next(); i++) {
        	requestedActivities[0][i] = (Integer)cdr.getObject(SIGHOS_ACT_ID);
            }
            
            st = conn.prepareStatement(QUERY_REQUESTED_ACT_NONPRE_STATEMENT);
            st.setInt(1, container.getPk());
	
            executeQuery();

            cdr.last();
            requestedActivities[1] = new int[cdr.getRow()];
            cdr.beforeFirst();
            
            for (int i = 0; cdr.next(); i++) {
        	requestedActivities[1][i] = (Integer)cdr.getObject(SIGHOS_ACT_ID);
            }

            return requestedActivities;
	}

	public void storeRequestedActivities(int actId[], int presential) throws SQLException {
	    for (int i : actId) {
                st = conn.prepareStatement(UPDATE_REQUESTED_ACT_STATEMENT, Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, container.getPk());
                st.setInt(2, i);
                st.setInt(3, presential);
                
                executeUpdate();
	    }
	}
    }
}
