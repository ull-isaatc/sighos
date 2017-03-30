package es.ull.iis.simulation.inforeceiver;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import es.ull.iis.simulation.info.AsynchronousInfo;
import es.ull.iis.simulation.info.ElementActionInfo;
import es.ull.iis.simulation.info.ElementInfo;
import es.ull.iis.simulation.info.ResourceInfo;
import es.ull.iis.simulation.info.ResourceUsageInfo;
import es.ull.iis.simulation.info.SimulationEndInfo;
import es.ull.iis.simulation.info.SimulationInfo;
import es.ull.iis.simulation.info.SimulationStartInfo;
import es.ull.iis.simulation.info.SynchronousInfo;
import es.ull.iis.simulation.info.TimeChangeInfo;
import es.ull.iis.simulation.info.UserInfo;
import es.ull.iis.simulation.info.VarViewValueRequestInfo;
import es.ull.iis.simulation.inforeceiver.InfoHandler;
import es.ull.iis.simulation.inforeceiver.InfoReceiver;
import es.ull.iis.simulation.inforeceiver.Listener;

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
	
	private void indexReceiver(InfoReceiver receiver) {
		for (Class<?> cl: receiver.getEntrance()) {
			if (definedInfos.contains(cl)){
				ArrayList<InfoReceiver> list = entranceList.get(cl);
				if (list != null)
					list.add(receiver);
				else {
					list = new ArrayList<InfoReceiver>();
					list.add(receiver);
					entranceList.put(cl, list);
				}
			} else {
				Error err = new Error("Unable to index " + receiver.getClass().toString() + " to info " + cl.toString());
				err.printStackTrace();
			}				
		}
	}
	
	public void registerReceivers(InfoReceiver receiver) {	
		if (receiver instanceof Listener)
			addDefinedInfos(((Listener) receiver).getGeneratedInfos());
		indexReceiver(receiver);
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
