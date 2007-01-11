/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.SingleFlowState;
import es.ull.isaatc.simulation.state.State;

/**
 * @author Roberto Muñoz
 *
 */
public class DBSingleFlowState extends DBState {

    protected static String SINGLE_FLOW_ID = "single_flow_id";
    protected static String SIGHOS_FLOW_ID = "sighos_flow_id";
    protected static String SIGHOS_ACT_ID = "sighos_activity_id";
    protected static String FINISHED = "finished";
    protected static String EXECUTING = "executing";

    protected static String SIGHOS_RES_ID = "sighos_resource_id";
    
    /** single flow registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO singleflow (sighos_flow_id, sighos_activity_id, finished, executing) VALUES (?, ?, ?, ?)";

    /** single flow registry SQL query */
    protected static String QUERY_SINGLE_FLOW_STATEMENT = "SELECT * from singleflow where single_flow_id = ?";

    /** caught resources of a single flow that is executing update SQL statement string */
    protected static String UPDATE_CAUGHT_RESOURCES = "INSERT INTO caughtresources (single_flow_id, sighos_resource_id) VALUES (?, ?)";

    /** caught resources of a single flow SQL query */
    protected static String QUERY_CAUGHT_RESOURCES_STATEMENT = "SELECT * from caughtresources where single_flow_id = ?";
    
    public DBSingleFlowState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    @Override
    public State getState(int pk) throws SQLException {
        st = conn.prepareStatement(QUERY_SINGLE_FLOW_STATEMENT);
        st.setInt(1, pk);
	
        executeQuery();
        
        cdr.next();
        this.pk = (Integer)cdr.getObject(SINGLE_FLOW_ID);
        
        SingleFlowState sfState = new SingleFlowState((Integer)cdr.getObject(SIGHOS_FLOW_ID),
        	(Integer)cdr.getObject(SIGHOS_ACT_ID),
        	(Boolean)cdr.getObject(FINISHED),
        	(Boolean)cdr.getObject(EXECUTING));
        DBCaughtResources dbCaughtResources = new DBCaughtResources(conn, this);
        sfState.getCaughtResources().addAll(dbCaughtResources.getCaughtResources());
	return sfState; 
    }

    @Override
    public List<State> getState() throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public void storeState(State state) throws SQLException {
	SingleFlowState sfState = (SingleFlowState) state;
	
	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
//	st.setInt(1, container.getPk());
	st.setInt(1, sfState.getFlowId());
	st.setInt(2, sfState.getActId());
	st.setInt(3, (sfState.isFinished() == true) ? 1 : 0);
	st.setInt(4, (sfState.isExecuting() == true) ? 1 : 0);	
	
	executeUpdate();
	
	DBCaughtResources dbCaughtResources = new DBCaughtResources(conn, this);
	dbCaughtResources.storeCaughtResources(sfState.getCaughtResources());
    }
    
    public class DBCaughtResources extends DBSighosRegistry {

	public DBCaughtResources(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}
	
	public List<Integer> getCaughtResources() throws SQLException {
            st = conn.prepareStatement(QUERY_CAUGHT_RESOURCES_STATEMENT);
            st.setInt(1, container.getPk());
            
//            System.out.println(st.toString());
            executeQuery();
            
            List<Integer> caughtResources = new ArrayList<Integer>();
            for (; cdr.next(); ) {
        	caughtResources.add((Integer)cdr.getObject(SIGHOS_RES_ID));
//        	System.out.println((Integer)cdr.getObject(SIGHOS_RES_ID));
            }
	    return caughtResources;
	}
	
	public void storeCaughtResources(ArrayList<Integer> caughtResources) throws SQLException {
            for (int i : caughtResources) {
                st = conn.prepareStatement(UPDATE_CAUGHT_RESOURCES);
                st.setInt(1, container.getPk());
                st.setInt(2, i);
                
                executeUpdate();
            }
	}
    }
}
