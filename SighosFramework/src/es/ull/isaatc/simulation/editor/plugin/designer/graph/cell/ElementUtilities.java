package es.ull.isaatc.simulation.editor.plugin.designer.graph.cell;

import org.jgraph.graph.Edge;

import es.ull.isaatc.simulation.editor.plugin.designer.graph.RootFlowGraphModel;

/**
 * @author Roberto Muñoz
 * 
 * A library of standard utilities that return information in selected graph elements. 
 */
public final class ElementUtilities {

  public static SighosCell getTargetOf(RootFlowGraphModel net, Edge edge) {
    return (SighosCell) RootFlowGraphModel.getTargetVertex(net, edge );
  }
  
  public static SighosCell getSourceOf(RootFlowGraphModel net, Edge edge) {
    return (SighosCell) RootFlowGraphModel.getSourceVertex(net, edge );
  }

}
