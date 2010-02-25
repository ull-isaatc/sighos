/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

/**
 * @author Iv�n Castilla Rodr�guez
 *
 */
public class CheckElementActionViewBuilder extends View {
	TreeMap<Long, TreeMap<Integer, ArrayList<Integer>>> reqEvents; 
	TreeMap<Long, TreeMap<Integer, ArrayList<Integer>>> staEvents; 
	TreeMap<Long, TreeMap<Integer, ArrayList<Integer>>> endEvents; 
	TreeSet<Integer> elements;
	/**
	 * @param simul
	 * @param description
	 */
	public CheckElementActionViewBuilder(Simulation simul) {
		super(simul, "Builds the code to check a WFP");
		reqEvents = new TreeMap<Long, TreeMap<Integer,ArrayList<Integer>>>();
		staEvents = new TreeMap<Long, TreeMap<Integer,ArrayList<Integer>>>();
		endEvents = new TreeMap<Long, TreeMap<Integer,ArrayList<Integer>>>();
		elements = new TreeSet<Integer>();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
	}

	private void fillEvent(ElementActionInfo eInfo, TreeMap<Long, TreeMap<Integer, ArrayList<Integer>>> events) {
		TreeMap<Integer,ArrayList<Integer>> entry;
		ArrayList<Integer> list;
		entry = events.get(eInfo.getTs()); 
		if (entry == null) {
			entry = new TreeMap<Integer, ArrayList<Integer>>();
			events.put(eInfo.getTs(), entry);
			list = new ArrayList<Integer>();
		}
		else {
			list = entry.get(eInfo.getElem().getIdentifier());
			if (list == null)
				list = new ArrayList<Integer>();
		}
		list.add(eInfo.getActivity().getIdentifier());
		entry.put(eInfo.getElem().getIdentifier(), list);		
	}
	
	private void printEvent(TreeMap<Long, TreeMap<Integer, ArrayList<Integer>>> events, String eventType) {
		for (Long ts : events.keySet()) {
			System.out.println("\t\tref = new ElementReferenceInfos[" + elements.size() + "];");
			System.out.println("\t\t" + eventType + ".put(" + ts + ", ref);");
			for (Integer elemId : events.get(ts).keySet()) {
				System.out.println("\t\tref[" + elemId + "] = new ElementReferenceInfos();");
				for (Integer actId : events.get(ts).get(elemId))
					System.out.println("\t\tref[" + elemId + "].add(" + actId + ");");
			}
			
		}
		
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			if (!elements.contains(eInfo.getElem().getIdentifier()))
				elements.add(eInfo.getElem().getIdentifier());
			switch(eInfo.getType()) {
			case REQACT:
				fillEvent(eInfo, reqEvents);
				break;
			case STAACT:
				fillEvent(eInfo, staEvents);
				break;
			case ENDACT:
				fillEvent(eInfo, endEvents);
				break;
			}
		}
		else if (info instanceof SimulationEndInfo) {
			String className = getSimul().getClass().getSimpleName().substring(0, 5);
			System.out.println("class " + className + "CheckView extends CheckElementActionsView {");
			System.out.println("\tpublic " + className + "CheckView(" + getSimul().getClass().getSimpleName() + " simul) {");
			System.out.println("\t\tthis(simul, true);");
			System.out.println("\t}");
			System.out.println();
			System.out.println("\tpublic " + className + "CheckView(" + getSimul().getClass().getSimpleName() + " simul, boolean detailed) {");
			System.out.println("\t\tsuper(simul, \"Checking " + className + "...\", detailed);");
			System.out.println();
			System.out.println("\t\tElementReferenceInfos [] ref;");
			printEvent(reqEvents, "refRequests");
			printEvent(staEvents, "refStartActs");
			printEvent(endEvents, "refEndActs");
			System.out.println("\t}");
			System.out.println("}");
		}

	}

}