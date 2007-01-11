/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.ResourceState;
import es.ull.isaatc.simulation.state.State;

/**
 * @author Roberto Muñoz
 *
 */
public class DBResourceState extends DBState {

    /** data base resource table columns */
    protected static String SIGHOS_RES_ID = "sighos_resource_id";
    protected static String RES_ID = "resource_id";
    protected static String VALID_TTE = "valid_tte";
    protected static String TIMEOUT = "timeout";
    protected static String SIGHOS_CURR_SF_ID = "sighos_current_sf_id";
    protected static String SIGHOS_CURR_ELEM_ID = "sighos_current_element_id";
    protected static String SIGHOS_CURR_RT_ID = "sighos_current_rt_id";
    
    /** data base currentroles table columns */
    protected static String SIGHOS_RT_ID = "sighos_resource_type_id";

    /** resource registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO resource (simulation_id, sighos_resource_id, valid_tte, timeout, sighos_current_sf_id, sighos_current_element_id, sighos_current_rt_id) VALUES (?, ?, ?, ?, ?, ?, ?)";

    /** resource registry update SQL statement string */
    protected static String QUERY_RESOURCE_STATEMENT = "SELECT * FROM resource where simulation_id = ?";

    /** resource current role registry update SQL statement string */
    protected final String UPDATE_CUURRENT_ROLES_STATEMENT = "INSERT INTO currentroles (resource_id, sighos_resource_type_id) VALUES (?, ?)";

    /** resource list entry query SQL statement */
    protected static String QUERY_CUURRENT_ROLES_STATEMENT = "SELECT sighos_resource_type_id FROM currentroles where resource_id = ?";

    public DBResourceState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    @Override
    public State getState(int pk) throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<State> getState() throws SQLException {
	st = conn.prepareStatement(QUERY_RESOURCE_STATEMENT);
	st.setInt(1, container.getPk());
	
	executeQuery();
	
	List<State> resStates = new ArrayList<State>();

        for (; cdr.next(); ) {
            pk = (Integer)cdr.getObject(RES_ID);
            ResourceState rState;
            Integer sfId = (Integer)cdr.getObject(SIGHOS_CURR_SF_ID);
            if (sfId == null)
        	rState = new ResourceState((Integer)cdr.getObject(SIGHOS_RES_ID), (Integer)cdr.getObject(VALID_TTE));
            else
        	rState = new ResourceState((Integer)cdr.getObject(SIGHOS_RES_ID), (Integer)cdr.getObject(VALID_TTE),
        		(Integer)cdr.getObject(SIGHOS_CURR_SF_ID),
        		(Integer)cdr.getObject(SIGHOS_CURR_ELEM_ID),
        		(Integer)cdr.getObject(SIGHOS_CURR_RT_ID),
        		(Boolean)cdr.getObject(TIMEOUT));
            resStates.add(rState);
            // retrieve the roles this resource belongs to
            DBResourceCurrentRole dbResourceCurrentRole = new DBResourceCurrentRole(conn, this);
            rState.getCurrentRoles().addAll(dbResourceCurrentRole.getResourceCurrentRole());
        }
        
	return resStates;
    }
    
    @Override
    public void storeState(State state) throws SQLException {
	ResourceState resState = (ResourceState) state;
	
	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, container.getPk());
	st.setInt(2, resState.getResId());
	st.setInt(3, resState.getValidTTEs());
	st.setInt(4, (resState.isTimeOut() == true) ? 1 : 0);
	st.setInt(5, resState.getCurrentSFId());
	st.setInt(6, resState.getCurrentElemId());
	st.setInt(7, resState.getCurrentRTId());
		
	executeUpdate();

	DBResourceCurrentRole dbResourceCurrentRole = new DBResourceCurrentRole(conn, this);
	dbResourceCurrentRole.storeResourceCurrentRoles(resState.getCurrentRoles());
    }
    
    public class DBResourceCurrentRole extends DBSighosRegistry {

	public DBResourceCurrentRole(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

	public List<Integer> getResourceCurrentRole() throws SQLException {
            st = conn.prepareStatement(QUERY_CUURRENT_ROLES_STATEMENT);
            st.setInt(1, container.getPk());
            
            executeQuery();
            
            List<Integer> currentRoles = new ArrayList<Integer>();
            
            for (; cdr.next(); ) {
        	currentRoles.add((Integer)cdr.getObject(SIGHOS_RT_ID));
            }
                
            return currentRoles;
	}

	public void storeResourceCurrentRoles(ArrayList<Integer> rtId) throws SQLException {
	    for (int i : rtId) {
                st = conn.prepareStatement(UPDATE_CUURRENT_ROLES_STATEMENT, Statement.RETURN_GENERATED_KEYS);
                st.setInt(1, container.getPk());
                st.setInt(2, i);
                
                executeUpdate();
	    }
	}
    }
}
