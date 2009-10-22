package es.ull.isaatc.simulation.threaded.inforeceiver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import es.ull.isaatc.simulation.info.AsynchronousInfo;
import es.ull.isaatc.simulation.info.SimulationEndInfo;
import es.ull.isaatc.simulation.info.SimulationInfo;
import es.ull.isaatc.simulation.info.SimulationStartInfo;
import es.ull.isaatc.simulation.info.SynchronousInfo;
import es.ull.isaatc.simulation.info.TimeChangeInfo;
import es.ull.isaatc.simulation.info.UserInfo;
import es.ull.isaatc.simulation.info.VarViewValueRequestInfo;
import es.ull.isaatc.simulation.inforeceiver.InfoHandler;
import es.ull.isaatc.simulation.inforeceiver.InfoReceiver;
import es.ull.isaatc.simulation.inforeceiver.Listener;
import es.ull.isaatc.simulation.inforeceiver.VarView;
import es.ull.isaatc.simulation.threaded.info.ElementActionInfo;
import es.ull.isaatc.simulation.threaded.info.ElementInfo;
import es.ull.isaatc.simulation.threaded.info.ResourceInfo;
import es.ull.isaatc.simulation.threaded.info.ResourceUsageInfo;

public class SimulationInfoHandler implements InfoHandler {

	private HashSet<Class<?>> definedInfos;
	private HashMap<Class<?>, ArrayList<InfoReceiver> > entranceList;
	private HashSet<Class<?>> notFinalicedUserInfos;
	
	public SimulationInfoHandler() {
		definedInfos = new HashSet<Class<?>>();
		notFinalicedUserInfos = new HashSet<Class<?>>();
		entranceList = new HashMap<Class<?>, ArrayList<InfoReceiver>>();
		initEntranceList();
	}
	
	private void initEntranceList() {
		definedInfos.add(SimulationStartInfo.class);
		definedInfos.add(SimulationEndInfo.class);
		definedInfos.add(ElementActionInfo.class);
		definedInfos.add(ElementInfo.class);
		definedInfos.add(ResourceInfo.class);
		definedInfos.add(ResourceUsageInfo.class);
		definedInfos.add(TimeChangeInfo.class);
	}
		
	private boolean isUserInfo(Type type) {
		if (type == null)
			return false;
		if (type.equals(UserInfo.class))
			return true;
		return false;
	}
	
	private void addDefinedInfos(ArrayList<Class<?>> infos) {
		definedInfos.addAll(infos);
		for(Class<?> info: infos) {
			if (isUserInfo(info.getGenericSuperclass()))
				notFinalicedUserInfos.add(info);
		}
	}
	
	private void indexReciever(InfoReceiver reciever) {
		if (reciever instanceof VarView) {
			VarView var = (VarView) reciever;
			reciever.getSimul().putVar(var.getVarName(), var);
		}
		for (Class<?> cl: reciever.getEntrance()) {
			if (definedInfos.contains(cl)){
				ArrayList<InfoReceiver> list = entranceList.get(cl);
				if (list != null)
					list.add(reciever);
				else {
					list = new ArrayList<InfoReceiver>();
					list.add(reciever);
					entranceList.put(cl, list);
				}
			} else {
				Error err = new Error("Unable to index " + reciever.getClass().toString() + " to info " + cl.toString());
				err.printStackTrace();
			}				
		}
	}
	
	public void registerRecievers(InfoReceiver reciever) {	
		if (reciever instanceof Listener)
			addDefinedInfos(((Listener) reciever).getGeneratedInfos());
		indexReciever(reciever);
	}

	public void asynchronousInfoProcessing(AsynchronousInfo info) {
		if ((info instanceof UserInfo) && ((UserInfo)info).isFinalInfo()) 
			notFinalicedUserInfos.remove(info);
		ArrayList<InfoReceiver> list = entranceList.get(info.getClass());
		if (list != null)
			for(InfoReceiver reciever: list)
				reciever.infoEmited(info);
	}

	public Number notifyInfo(SimulationInfo info) {
		if (info instanceof AsynchronousInfo)
			asynchronousInfoProcessing((AsynchronousInfo)info);
		else
			if (info instanceof SynchronousInfo)
				return synchronousInfoProcessing((SynchronousInfo)info);
		return null;
	}

	public Number synchronousInfoProcessing(SynchronousInfo info) {
		if (info instanceof VarViewValueRequestInfo) {
			VarViewValueRequestInfo reqInfo = (VarViewValueRequestInfo) info;
			Number value = reqInfo.getSimul().getVar(reqInfo.getVarName()).getValue(reqInfo.getParams());
			if (value != null)
				return value.doubleValue();
		}
		return -1;
	}
	
}
