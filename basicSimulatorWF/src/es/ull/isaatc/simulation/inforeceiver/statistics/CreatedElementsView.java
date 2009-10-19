package es.ull.isaatc.simulation.inforeceiver.statistics;

import java.util.HashMap;
import es.ull.isaatc.simulation.ElementType;
import es.ull.isaatc.simulation.Simulation;
import es.ull.isaatc.simulation.info.ElementInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.inforeceiver.VarView;

public class CreatedElementsView extends VarView {

	private int createdElements;
	private HashMap<ElementType, Integer> etCreatedElements;
	
	public CreatedElementsView(Simulation simul) {
		super(simul, "createdElements");
		createdElements = 0;
		etCreatedElements = new HashMap<ElementType,Integer>();
		addEntrance(ElementInfo.class);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementInfo) {
			ElementInfo elemInfo = (ElementInfo) info;
			switch(elemInfo.getType()) {
			case START: {
				createdElements++;
				ElementType et = elemInfo.getElem().getElementType();
				Integer counter = etCreatedElements.get(et);
				if (counter != null)
					etCreatedElements.put(et, new Integer(counter.intValue() + 1));
				else
					etCreatedElements.put(et, new Integer(1));
				if (isDebugMode()) {
					String message = new String();
					message += elemInfo.toString() + "\n";
					message += "Elements created: " + createdElements + "\n";
					message += "Element created with type " + et.getDescription() + ": " + etCreatedElements.get(et) + "\n";
					debug(message);
				}
				break;
			}
			default: break;
			}
		}  else {
			Error err = new Error("Incorrect info recieved: " + info.toString());
			err.printStackTrace();
		}
	}

	public Number getValue(Object... params) {
		if (params[0] instanceof ElementType) {
			ElementType type = (ElementType) params[0];
			Integer counter = etCreatedElements.get(type);
			if (counter == null)
				return new Integer(0);
			else
				return counter;
		} else
			return createdElements;
	}

}
