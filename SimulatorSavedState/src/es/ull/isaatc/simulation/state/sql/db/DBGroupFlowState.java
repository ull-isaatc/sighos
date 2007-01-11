/**
 * 
 */
package es.ull.isaatc.simulation.state.sql.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import es.ull.isaatc.simulation.state.FlowState;
import es.ull.isaatc.simulation.state.GroupFlowState;
import es.ull.isaatc.simulation.state.SequenceFlowState;
import es.ull.isaatc.simulation.state.SimultaneousFlowState;
import es.ull.isaatc.simulation.state.SingleFlowState;
import es.ull.isaatc.simulation.state.State;

/**
 * @author Roberto Muñoz
 * 
 */
public class DBGroupFlowState extends DBState {

    /** group flow types */
    protected static String SEQ = "SEQ";
    protected static String SIM = "SIM";
    protected static String GRP = "GRP";
    protected static String SIN = "SIN";
    
    /** type of group flow */
    protected String type;
    
    protected static String GROUP_FLOW_ID = "group_flow_id";
    protected static String TYPE = "type";
    protected static String FINISHED = "finished";
    protected static String FLOW_ID = "flow_id";
    
    /** group flow registry update SQL statement string */
    protected static String UPDATE_STATEMENT = "INSERT INTO groupflow (finished, type) VALUES (?, ?)";

    /** group flow SQL query statement */
    protected static String QUERY_GROUP_FLOW_STATEMENT = "SELECT * from groupflow WHERE group_flow_id = ?";
    
    /** descendant flow registry update SQL statement string */
    protected final String UPDATE_DESC_FLOW_STATEMENT = "INSERT INTO descendants (group_flow_id, flow_id, type) VALUES (?, ?, ?)";
    
    /** descendant flow SQL query statement */
    protected final String QUERY_DESC_FLOW_STATEMENT = "SELECT * FROM descendants WHERE group_flow_id = ?";

    public DBGroupFlowState(Connection connection, DBSighosRegistry container, String type) throws SQLException {
	super(connection, container);
	this.type = type;
    }

    public DBGroupFlowState(Connection connection, DBSighosRegistry container) throws SQLException {
	super(connection, container);
	this.type = GRP;
    }

    @Override
    public List<State> getState() throws SQLException {
	// TODO Auto-generated method stub
	return null;
    }
    
    @Override
    public State getState(int pk) throws SQLException {
	GroupFlowState fState = null;
	st = conn.prepareStatement(QUERY_GROUP_FLOW_STATEMENT);
	st.setInt(1, pk);
//	System.out.println(st.toString());
	executeQuery();
	
	cdr.next();
	this.pk = pk;
	
	Integer finished = (Integer)cdr.getObject(FINISHED);
	String type = (String)cdr.getObject(TYPE);
	if (type.equals(SEQ)) {
	    fState = new SequenceFlowState(finished);
	}
	else if (type.equals(SIM)) {
	    fState = new SimultaneousFlowState(finished);
	}
	DBDescendantFlow dbDescendantFlow = new DBDescendantFlow(conn, this);
	fState.getDescendants().addAll(dbDescendantFlow.getDescendantFlows());

	return fState;
    }

    @Override
    public void storeState(State state) throws SQLException {
	GroupFlowState gfState = (GroupFlowState) state;

	st = conn.prepareStatement(UPDATE_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	st.setInt(1, gfState.getFinished());
	st.setString(2, type);

	executeUpdate();

	DBDescendantFlow dbDescendantFlow = new DBDescendantFlow(conn, this);
	for (FlowState fState : gfState.getDescendants()) {
	    dbDescendantFlow.storeFlow(fState);
	}
    }

    private class DBDescendantFlow extends DBSighosRegistry {

	public DBDescendantFlow(Connection connection, DBSighosRegistry container) throws SQLException {
	    super(connection, container);
	}

	public List<FlowState> getDescendantFlows() throws SQLException {
	    st = conn.prepareStatement(QUERY_DESC_FLOW_STATEMENT);
	    st.setInt(1, container.getPk());
	    
//	    System.out.println(st.toString());
	    executeQuery();
	    
	    List<FlowState> descendants = new ArrayList<FlowState>();
	    
	    for (; cdr.next(); ) {
                int flowId = (Integer)cdr.getObject(FLOW_ID);
                String type = (String)cdr.getObject(TYPE);
                if (type.equals(GRP))
                    descendants.add((FlowState) new DBGroupFlowState(conn, this).getState());
                else if (type.equals(SIN))
                    descendants.add((FlowState) new DBSingleFlowState(conn, this).getState(flowId));
	    }
	    return descendants;
	}

	public void storeFlow(FlowState fState) throws SQLException {
	    int flowId = -1;
	    String type = "";
	    
	    st = conn.prepareStatement(UPDATE_DESC_FLOW_STATEMENT, Statement.RETURN_GENERATED_KEYS);
	    st.setInt(1, container.getPk());

	    if (fState instanceof SequenceFlowState) {
		DBSequenceFlowState dbSequenceFlowState = new DBSequenceFlowState(conn, container);
		dbSequenceFlowState.storeState(fState);
		flowId = dbSequenceFlowState.getPk();
		type = "GRP";
	    } else if (fState instanceof SimultaneousFlowState) {
		DBSimultaneousFlowState dbSimultaneousFlowState = new DBSimultaneousFlowState(conn, container);
		dbSimultaneousFlowState.storeState(fState);
		flowId = dbSimultaneousFlowState.getPk();
		type = "GRP";
	    } else if (fState instanceof SingleFlowState) {
		DBSingleFlowState dbSingleFlowState = new DBSingleFlowState(conn, container);
		dbSingleFlowState.storeState(fState);
		flowId = dbSingleFlowState.getPk();
		type = "SIN";
	    }
	    st.setInt(2, flowId);
	    st.setString(3, type);
	    
	    executeUpdate();
	}
    }
}
