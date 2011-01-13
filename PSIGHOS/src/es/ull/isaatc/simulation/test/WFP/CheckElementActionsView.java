/**
 * 
 */
package es.ull.isaatc.simulation.test.WFP;

import java.util.Set;
import java.util.TreeMap;

import es.ull.isaatc.simulation.core.Simulation;
import es.ull.isaatc.simulation.info.ElementActionInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class CheckElementActionsView extends WFPTestView {
	protected TreeMap<Long, ElementReferenceInfos[]> refRequests;
	protected TreeMap<Long, ElementReferenceInfos[]> refStartActs;
	protected TreeMap<Long, ElementReferenceInfos[]> refEndActs;
	private boolean ok = true;

	public CheckElementActionsView(Simulation simul, String description) {
		this(simul, description, true);
	}

	public CheckElementActionsView(Simulation simul, String description, boolean detailed) {
		super(simul, description, detailed);
		refRequests = new TreeMap<Long, ElementReferenceInfos[]>();
		refStartActs = new TreeMap<Long, ElementReferenceInfos[]>();
		refEndActs = new TreeMap<Long, ElementReferenceInfos[]>();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(SimulationStartInfo.class);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.isaatc.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.isaatc.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		ElementReferenceInfos [] ref = null;
		if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			if (detailed)
				System.out.print(eInfo + "...\t");
			switch(eInfo.getType()) {
			case REQACT:
				ref = refRequests.get(eInfo.getTs());
				break;
			case STAACT:
				ref = refStartActs.get(eInfo.getTs());
				break;
			case ENDACT:
				ref = refEndActs.get(eInfo.getTs());
				break;
			}
			if (ref == null) {
				if (detailed)
					System.out.println("ERROR!!: Unexpected event (wrong TS)");
				ok = false;
			}
			else if (ref[eInfo.getElement().getIdentifier()] == null) {	
				if (detailed)
					System.out.println("ERROR!!: Unexpected event (wrong Element)");
				ok = false;
			}
			else if (!ref[eInfo.getElement().getIdentifier()].check(eInfo.getActivity().getIdentifier())) {
				if (detailed)
					System.out.println("ERROR!!: Unexpected event (wrong Activity)");
				ok = false;					
			}
			else if (detailed)
				System.out.println("PASSED");
		}
		else if (info instanceof SimulationStartInfo) {
			System.out.println("--------------------------------------------------");
			System.out.println("Checking " + getSimul().getDescription());
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println();
			checkMissed(refRequests, "REQUEST ACTIVITY");
			checkMissed(refStartActs, "START ACTIVITY");
			checkMissed(refEndActs, "END ACTIVITY");
			notifyResult(ok);
		}
	}

	private void checkMissed(TreeMap<Long, ElementReferenceInfos[]> references, String type) {
		for (long ts : references.keySet()) {
			ElementReferenceInfos[] ref = references.get(ts);
			for (int i = 0; i < ref.length; i++) {
				if (ref[i] != null) {
					for (int actId : ref[i].getActivities())
						if (!ref[i].finalCheck(actId)) {
							if (detailed)
								System.out.println(getSimul().long2SimulationTime(ts) + "\t" + "[E" + i + "]\t" + type + " " + actId + "\tERROR!!: Event missed");
							ok = false;
						}
				}
			}
		}		
	}
	
	protected class ElementReferenceInfos {
		final TreeMap<Integer, Boolean> actIds;
		
		public ElementReferenceInfos() {
			this.actIds = new TreeMap<Integer, Boolean>();
		}
		
		public ElementReferenceInfos(Integer ...ids) {
			this.actIds = new TreeMap<Integer, Boolean>();
			for (Integer id : ids)
				add(id);
		}
		
		public void add(int actId) {
			actIds.put(actId, false);
		}
		
		public Set<Integer> getActivities() {
			return actIds.keySet();
		}
		
		public boolean finalCheck(int actId) {
			 return actIds.get(actId);
		}
		
		public boolean check(int actId) {
			if (actIds.get(actId) == null)
				return false;
			actIds.put(actId, true);
			return true;
		}
		
		public boolean isChecked(int actId) {
			return actIds.get(actId);
		}
	}
}
