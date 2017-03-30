/**
 * 
 */
package es.ull.iis.simulation.examples.WFP;

import java.util.Set;
import java.util.TreeMap;

import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public abstract class CheckElementActionsView extends WFPTestView {
	protected TreeMap<Long, ElementReferenceInfos[]> refRequests;
	protected TreeMap<Long, ElementReferenceInfos[]> refStartActs;
	protected TreeMap<Long, ElementReferenceInfos[]> refEndActs;
	private boolean ok = true;

	public CheckElementActionsView(String description) {
		this(description, true);
	}

	public CheckElementActionsView(String description, boolean detailed) {
		super(description, detailed);
		refRequests = new TreeMap<Long, ElementReferenceInfos[]>();
		refStartActs = new TreeMap<Long, ElementReferenceInfos[]>();
		refEndActs = new TreeMap<Long, ElementReferenceInfos[]>();
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		addEntrance(SimulationStartInfo.class);
	}
	
	/* (non-Javadoc)
	 * @see es.ull.iis.simulation.inforeceiver.InfoReceiver#infoEmited(es.ull.iis.simulation.info.SimulationInfo)
	 */
	@Override
	public void infoEmited(SimulationInfo info) {
		ElementReferenceInfos [] ref = null;
		if (info instanceof ElementActionInfo) {
			ElementActionInfo eInfo = (ElementActionInfo) info;
			if (detailed)
				System.out.print(eInfo + "...\t");
			switch(eInfo.getType()) {
			case REQ:
				ref = refRequests.get(eInfo.getTs());
				break;
			case START:
				ref = refStartActs.get(eInfo.getTs());
				break;
			case END:
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
			System.out.println("Checking " + info.getSimul().getDescription());
		}
		else if (info instanceof SimulationEndInfo) {
			System.out.println();
			checkMissed(info, refRequests, "REQUEST ACTIVITY");
			checkMissed(info, refStartActs, "START ACTIVITY");
			checkMissed(info, refEndActs, "END ACTIVITY");
			notifyResult(ok);
		}
	}

	private void checkMissed(SimulationInfo info, TreeMap<Long, ElementReferenceInfos[]> references, String type) {
		for (long ts : references.keySet()) {
			ElementReferenceInfos[] ref = references.get(ts);
			for (int i = 0; i < ref.length; i++) {
				if (ref[i] != null) {
					for (int actId : ref[i].getActivities())
						if (!ref[i].finalCheck(actId)) {
							if (detailed)
								System.out.println(info.getSimul().long2SimulationTime(ts) + "\t" + "[E" + i + "]\t" + type + " " + actId + "\tERROR!!: Event missed");
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
