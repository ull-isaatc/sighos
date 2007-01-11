/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.ResourceTypeState;
import es.ull.isaatc.simulation.state.State;
import es.ull.isaatc.simulation.state.ResourceTypeState.ResourceListEntry;

/**
 * @author Roberto Muñoz
 *
 */
public class DBResourceTypeState extends DBState {

    /** data base resourcetype table columns */
    protected static String RT_ID = "resource_type_id";
    protected static String SIGHOS_RT_ID = "sighos_resource_type_id";
    
    /** data base resourcelistentry table columns */
    protected static String SIGHOS_RES_ID = "sighos_resource_id";
    protected static String COUNT = "count";
    
    /** resourcetype registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO resourcetype (simulation_id, sighos_resource_type_id) VALUES (?, ?)";

    /** resourcetype registry update SQL statement string */
    protected static String QUERY_RESOURCE_TYPE_STATEMENT = "SELECT * FROM resourcetype where simulation_id = ?";

    /** resource list entry registry update SQL statement string */
    protected static String UPDATE_RESLIST_STATEMENT = "INSERT INTO resourcelistentry (resource_type_id, count, sighos_resource_id) VALUES (?, ?, ?)";

    /** resource list entry query SQL statement */
    protected static String QUERY_RESLIST_STATEMENT = "SELECT * FROM resourcelistentry where resource_type_id = ?";

    public DBResourceTypeState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
    }

    @Override
    public State getState(int pk) throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public List<State> getState() throws SQLException {
	st = conn.prepareStatement(QUERY_RESOURCE_TYPE_STATEMENT);
	st.setInt(1, container.getPk());
	
	executeQuery();
	
	List<State> rtStates = new ArrayList<State>();

        for (; cdr.next(); ) {
            pk = (Integer)cdr.getObject(RT_ID);	
            ResourceTypeState rtState = new ResourceTypeState((Integer)cdr.getObject(SIGHOS_RT_ID));
            rtStates.add(rtState);
            // retrieve the resource list entries
            DBResourceListEntry dbResourceListEntry = new DBResourceListEntry(conn, this);
            rtState.getAvailableResourceQueue().addAll(dbResourceListEntry.getResourceListEntry(rtState));
        }
        
	return rtStates;
    }
    
    @Override
    public void storeState(State state) throws SQLException {
	ResourceTypeState rtState = (ResourceTypeState) state;
	
	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, container.getPk());
	st.setInt(2, rtState.getRtId());
		
	executeUpdate();
	
	DBResourceListEntry dbResourceListEntry = new DBResourceListEntry(conn, this);
	for (ResourceListEntry rlEntry : rtState.getAvailableResourceQueue())
	    dbResourceListEntry.storeResourceListEntry(rlEntry);
    }
    
    public class DBResourceListEntry extends DBSighosRegistry {


	public DBResourceListEntry(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

	public List<ResourceListEntry> getResourceListEntry(ResourceTypeState rtState) throws SQLException {
            st = conn.prepareStatement(QUERY_RESLIST_STATEMENT);
            st.setInt(1, container.getPk());
            
            executeQuery();
            
            List<ResourceListEntry> resListStates = new ArrayList<ResourceListEntry>();
            
            for (; cdr.next(); ) {
                ResourceListEntry resListEntry = rtState.new ResourceListEntry((Integer)cdr.getObject(SIGHOS_RES_ID), (Integer)cdr.getObject(COUNT));
                resListStates.add(resListEntry);
            }
                
            return resListStates;
	}

	public void storeResourceListEntry(ResourceListEntry rlEntry) throws SQLException {

	    st = conn.prepareStatement(UPDATE_RESLIST_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	    st.setInt(1, container.getPk());
	    st.setInt(2, rlEntry.getCount());
	    st.setInt(3, rlEntry.getResId());

	    executeUpdate();
	}
    }
}
