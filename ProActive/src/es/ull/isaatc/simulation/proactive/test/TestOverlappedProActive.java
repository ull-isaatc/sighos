package es.ull.isaatc.simulation.proactive.test;

import org.objectweb.proactive.core.util.wrapper.IntWrapper;

import es.ull.isaatc.simulation.proactive.ExperimentProActive;

/**
 * 
 */

public class TestOverlappedProActive {
	final static int NDAYS = 1;
	final static int NEXP = 2;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ExpOverlappedProActive exp = null;
//		try {
////			org.objectweb.proactive.core.node.Node n = org.objectweb.proactive.core.node.NodeFactory.getNode("//localhost/node1");
//			exp = (ExpOverlappedProActive)org.objectweb.proactive.ProActive.newActive
//				(ExpOverlappedProActive.class.getName(), null, new Object[]{"Solapados"}, "//10.213.2.125/node1");
//		} catch (ActiveObjectCreationException e) {
//			e.printStackTrace();
//		} catch (NodeException e) {
//			e.printStackTrace();
//		}
//		exp.start();
		String [] nodeList = {"//localhost/node1", "//10.213.2.125/node1"};
		ExperimentProActive exp = new ExperimentProActive(OverlappedSimulationProActive.class.getName(), 
				new Object[]{new IntWrapper(1)}, "Solapados", NEXP, 0.0, NDAYS * 24 * 60.0, nodeList);
		exp.start();
		
		
//		Simulation sim = null;
//		try {
//			Object [][] p = new Object[2][];
//			p[0] = params;
//			p[1] = params;
//			Nod [] 
//			sim = (Simulation)org.objectweb.proactive.ProActive.newActiveInParallel
//			(simulClassName, null, p, nodeList);
//			if (previousState == null)
//				sim.start(startTs, endTs);
//			else
//				sim.start(previousState, endTs);
//			processor.process(sim.getState());
//		} catch (ActiveObjectCreationException e) {
//			e.printStackTrace();
//		} catch (NodeException e) {
//			e.printStackTrace();
//		} finally {
//			nodeCounter = (nodeCounter + 1) % nodeList.length;
//		}
		
	}
}
