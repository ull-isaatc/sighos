import com.thoughtworks.xstream.XStream;

import es.ull.isaatc.simulation.state.SimulationState;
import es.ull.isaatc.simulation.state.StateProcessor;

/**
 * @author Roberto Muñoz
 */
public class XMLStateProcessor implements StateProcessor {

    private String xmlStr;

    public XMLStateProcessor() {

	super();
    }

    public void process(SimulationState state) {

	XStream xstream = new XStream();
	// xstream.alias("elem_stats",
	// Class.forName("es.ull.isaatc.simulation.state.SimulationState"));
	// xstream.alias("pflow",
	// Class.forName("es.ull.isaatc.simulation.results.PendingFlowStatistics"));
	// xstream.alias("act_stats",
	// Class.forName("es.ull.isaatc.simulation.results.ActivityStatistics"));
	xstream.setMode(XStream.NO_REFERENCES);
	xmlStr = xstream.toXML(state.getLpStates().get(0).getWaitQueue().get(0)
		.getType());
	System.out.println(xmlStr);
    }
}
