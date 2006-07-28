/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import com.thoughtworks.xstream.XStream;

import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroup;
import es.ull.isaatc.simulation.editor.project.model.Activity.WorkGroupTableModel;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTableTableModel;
import es.ull.isaatc.simulation.editor.project.model.Resource.TimeTable.Duration;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ActivityTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ElementTypeTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ResourceTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.ResourceTypeTableModel;
import es.ull.isaatc.simulation.editor.project.model.tablemodel.RootFlowTableModel;
import es.ull.isaatc.simulation.xml.FlowChoiceUtility;

/**
 * @author Roberto Muñoz
 */
public class XMLModelUtilities {

	private static SchemaFactory schemaFactory;

	public static Schema COMPONENT_XSD;

	static {
		try {
			schemaFactory = SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			COMPONENT_XSD = schemaFactory
					.newSchema(new File(
							"src/es/ull/isaatc/simulation/editor/project/model/validation/components.xsd"));
		} catch (SAXException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static ResourceTypeTableModel getResourceTypeTableModel(
			List<es.ull.isaatc.simulation.xml.ResourceType> rtList) {
		ResourceTypeTableModel rtTableModel = new ResourceTypeTableModel();
		ModelComponentList elements = rtTableModel.getElements();
		Iterator<es.ull.isaatc.simulation.xml.ResourceType> rtIt = rtList
				.iterator();
		while (rtIt.hasNext()) {
			es.ull.isaatc.simulation.xml.ResourceType rtXML = rtIt.next();
			ResourceType rt = new ResourceType();
			rt.setDescription(rtXML.getDescription());
			rt.setId(rtXML.getId());
			elements.add(rt);
		}
		rtTableModel.setElements(elements);
		if (rtTableModel.getRowCount() > 0)
			rtTableModel.initNextId((Integer) rtTableModel.getValueAt(
					rtTableModel.getRowCount() - 1, 0));
		return rtTableModel;
	}

	public static ResourceTableModel getResourceTableModel(
			List<es.ull.isaatc.simulation.xml.Resource> resList) {
		ResourceTableModel resTableModel = new ResourceTableModel();
		ModelComponentList elements = resTableModel.getElements();
		Iterator<es.ull.isaatc.simulation.xml.Resource> resIt = resList
				.iterator();

		while (resIt.hasNext()) {
			es.ull.isaatc.simulation.xml.Resource resXML = resIt.next();
			Resource res = new Resource();
			res.setId(resXML.getId());
			res.setDescription(resXML.getDescription());
			res.setNelem(resXML.getUnits());
			TimeTableTableModel ttTableModel = res.getTimeTableTableModel();
			Iterator<es.ull.isaatc.simulation.xml.Resource.TimeTable> ttIt = resXML
					.getTimeTable().iterator();
			while (ttIt.hasNext()) {
				es.ull.isaatc.simulation.xml.Resource.TimeTable ttXML = ttIt
						.next();
				TimeTable tt = new TimeTable();

				Iterator<Integer> rtIt = ttXML.getRtId().iterator();
				while (rtIt.hasNext()) {
					ResourceType rt = (ResourceType) ProjectModel.getInstance()
							.getModel().getResourceTypeTableModel().search(
									rtIt.next());
					rt.addTimeTable(tt);
					tt.getRTList().add(rt);
				}
				tt.setCycle(getXMLFromJaxb("cycle",
						es.ull.isaatc.simulation.xml.Cycle.class, ttXML
								.getCycle()));
				tt.setDuration(new Duration(ttXML.getDur().getValue(), ttXML
						.getDur().getTimeUnit()));
				ttTableModel.add(tt);
			}
			elements.add(res);
		}
		resTableModel.setElements(elements);
		if (resTableModel.getRowCount() > 0) {
			Resource r = resTableModel.get(resTableModel.getRowCount() - 1);
			resTableModel.initNextId(r.getId() + r.getNelem());
		}
		return resTableModel;
	}

	public static ActivityTableModel getActivityTableModel(
			List<es.ull.isaatc.simulation.xml.Activity> actList) {
		ActivityTableModel actTableModel = new ActivityTableModel();
		ModelComponentList elements = actTableModel.getElements();
		Iterator<es.ull.isaatc.simulation.xml.Activity> actIt = actList
				.iterator();

		while (actIt.hasNext()) {
			es.ull.isaatc.simulation.xml.Activity actXML = actIt.next();
			Activity act = new Activity();
			act.setId(actXML.getId());
			act.setDescription(actXML.getDescription());
			act.setPresential(actXML.isPresencial());
			act.setPriority(actXML.getPriority());
			Iterator<es.ull.isaatc.simulation.xml.Activity.WorkGroup> wgIt = actXML
					.getWorkGroup().iterator();
			WorkGroupTableModel wgTableModel = act.getWorkGroupTableModel();
			while (wgIt.hasNext()) {
				es.ull.isaatc.simulation.xml.Activity.WorkGroup wgXML = wgIt
						.next();
				WorkGroup wg = new WorkGroup();
				wg.setId(wgXML.getId());
				wg.setPriority(wgXML.getPriority());
				wg.setDuration(getXMLFromJaxb("duration",
						es.ull.isaatc.simulation.xml.RandomNumber.class, wgXML
								.getDuration()));
				Iterator<es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role> rtIt = wgXML
						.getRole().iterator();
				while (rtIt.hasNext()) {
					es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role rtXML = rtIt
							.next();
					ResourceType rt = (ResourceType) ProjectModel.getInstance()
							.getModel().getResourceTypeTableModel().search(
									rtXML.getRtId());
					rt.addWorkGroup(wg);
					wg.getResourceType().put(rt, rtXML.getUnits());
				}
				wgTableModel.add(wg);
			}
			elements.add(act);
			if (wgTableModel.getRowCount() > 0)
				wgTableModel.initNextId((Integer) wgTableModel.getValueAt(
						wgTableModel.getRowCount() - 1, 0));
		}
		actTableModel.setElements(elements);
		if (actTableModel.getRowCount() > 0)
			actTableModel.initNextId((Integer) actTableModel.getValueAt(
					actTableModel.getRowCount() - 1, 0));
		return actTableModel;
	}

	public static ElementTypeTableModel getElementTypeTableModel(
			List<es.ull.isaatc.simulation.xml.ElementType> etList) {
		ElementTypeTableModel etTableModel = new ElementTypeTableModel();
		ModelComponentList elements = etTableModel.getElements();
		Iterator<es.ull.isaatc.simulation.xml.ElementType> etIt = etList
				.iterator();
		while (etIt.hasNext()) {
			es.ull.isaatc.simulation.xml.ElementType etXML = etIt.next();
			ElementType et = new ElementType();
			et.setDescription(etXML.getDescription());
			et.setId(etXML.getId());
			elements.add(et);
		}
		etTableModel.setElements(elements);
		if (etTableModel.getRowCount() > 0)
			etTableModel.initNextId((Integer) etTableModel.getValueAt(
					etTableModel.getRowCount() - 1, 0));
		return etTableModel;
	}

	public static RootFlowTableModel getRootFlowTableModel(
			List<es.ull.isaatc.simulation.xml.RootFlow> rootFlowList) {
		// RootFlowTableModel rootFlowTableModel = new RootFlowTableModel();
		RootFlowTableModel rootFlowTableModel = ProjectModel.getInstance()
				.getModel().getRootFlowTableModel();
		ModelComponentList elements = rootFlowTableModel.getElements();
		Iterator<es.ull.isaatc.simulation.xml.RootFlow> rfIt = rootFlowList
				.iterator();

		while (rfIt.hasNext()) {
			es.ull.isaatc.simulation.xml.RootFlow rfXML = rfIt.next();
			RootFlow rf = (RootFlow) ProjectModel.getInstance().getModel()
					.getRootFlowTableModel().search(rfXML.getId());
			if (rf == null)
				rf = new RootFlow();
			rf.setId(rfXML.getId());
			rf.setDescription(rfXML.getDescription());
			rf.setFlow(getFlowFromJaxb(rfXML.getFlow(), null));
			elements.add(rf);
		}

		if (rootFlowTableModel.getRowCount() > 0)
			rootFlowTableModel.initNextId((Integer) rootFlowTableModel
					.getValueAt(rootFlowTableModel.getRowCount() - 1, 0));
		return rootFlowTableModel;
	}

	/**
	 * @param flowChoiceXML
	 *            flow XML description
	 * @return the model flow
	 */
	private static Flow getFlowFromJaxb(es.ull.isaatc.simulation.xml.FlowChoice flowChoiceXML, Flow parent) {
		if (flowChoiceXML == null)
			return null;
		return getFlowFromJaxb(FlowChoiceUtility.getSelectedFlow(flowChoiceXML), parent);
	}

	/**
	 * @param flowXML
	 *            flow XML description
	 * @return the model flow from the flow description in XML
	 */
	private static Flow getFlowFromJaxb(es.ull.isaatc.simulation.xml.Flow flowXML, Flow parent) {

		Flow flow = null;
		if (flowXML instanceof es.ull.isaatc.simulation.xml.SingleFlow) {
			es.ull.isaatc.simulation.xml.SingleFlow sfXML = (es.ull.isaatc.simulation.xml.SingleFlow) flowXML;
			flow = new SingleFlow();
			((SingleFlow) flow).setActivity((Activity) ProjectModel
					.getInstance().getModel().getActivityTableModel().search(
							sfXML.getActId()));
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.PackageFlow) {
			es.ull.isaatc.simulation.xml.PackageFlow pfXML = (es.ull.isaatc.simulation.xml.PackageFlow) flowXML;
			flow = new PackageFlow();
			((PackageFlow) flow).setRootFlow((RootFlow) ProjectModel
					.getInstance().getModel().getRootFlowTableModel().search(
							pfXML.getRootFlowId()), false);
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.ExitFlow) {
			flow = new ExitFlow();
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.SequenceFlow) {
			es.ull.isaatc.simulation.xml.SequenceFlow seqXML = (es.ull.isaatc.simulation.xml.SequenceFlow) flowXML;
			flow = new SequenceFlow();
			Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = seqXML
					.getSingleOrPackageOrSequence().iterator();
			while (flowListIt.hasNext()) {
				((GroupFlow) flow).addFlow(getFlowFromJaxb(flowListIt.next(), flow));
			}
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.SimultaneousFlow) {
			es.ull.isaatc.simulation.xml.SimultaneousFlow simXML = (es.ull.isaatc.simulation.xml.SimultaneousFlow) flowXML;
			flow = new SimultaneousFlow();
			Iterator<es.ull.isaatc.simulation.xml.Flow> flowListIt = simXML
					.getSingleOrPackageOrSequence().iterator();
			while (flowListIt.hasNext()) {
				((GroupFlow) flow).addFlow(getFlowFromJaxb(flowListIt.next(), flow));
			}
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.DecisionFlow) {
			es.ull.isaatc.simulation.xml.DecisionFlow decXML = (es.ull.isaatc.simulation.xml.DecisionFlow) flowXML;
			flow = new DecisionFlow();
			Iterator<es.ull.isaatc.simulation.xml.DecisionOption> flowListIt = decXML
					.getOption().iterator();
			while (flowListIt.hasNext()) {
				es.ull.isaatc.simulation.xml.DecisionOption optXML = flowListIt
						.next();
				DecisionBranchFlow dbFlow = (DecisionBranchFlow)getFlowFromJaxb(optXML, flow);
				((DecisionFlow) flow).setProb(dbFlow, dbFlow.getProb());
			}
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.TypeFlow) {
			es.ull.isaatc.simulation.xml.TypeFlow typeXML = (es.ull.isaatc.simulation.xml.TypeFlow) flowXML;
			flow = new TypeFlow();
			Iterator<es.ull.isaatc.simulation.xml.TypeBranch> flowListIt = typeXML
					.getBranch().iterator();
			while (flowListIt.hasNext()) {
				es.ull.isaatc.simulation.xml.TypeBranch branchXML = flowListIt
						.next();
				ArrayList<ElementType> elementTypes = new ArrayList<ElementType>();
				// set the element types for this branch
				String elemTypesId[] = branchXML.getElemTypes().split(",");
				for (String strId : elemTypesId) {
					elementTypes.add((ElementType) ProjectModel.getInstance()
							.getModel().getElementTypeTableModel().search(
									Integer.parseInt(strId.trim())));
				}
				TypeBranchFlow tbFlow = (TypeBranchFlow) getFlowFromJaxb(branchXML, flow);
				tbFlow.setElemTypes(elementTypes);
				((TypeFlow) flow).setElementTypes(tbFlow, elementTypes);
			}
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.TypeBranch) {
			es.ull.isaatc.simulation.xml.TypeBranch tbXML = (es.ull.isaatc.simulation.xml.TypeBranch) flowXML;
			flow = new TypeBranchFlow();
			((TypeBranchFlow) flow).setOption(getFlowFromJaxb(FlowChoiceUtility
					.getSelectedFlow(tbXML), flow));
		} else if (flowXML instanceof es.ull.isaatc.simulation.xml.DecisionOption) {
			es.ull.isaatc.simulation.xml.DecisionOption doXML = (es.ull.isaatc.simulation.xml.DecisionOption) flowXML;
			flow = new DecisionBranchFlow();
			((DecisionBranchFlow) flow)
					.setOption(getFlowFromJaxb(FlowChoiceUtility
							.getSelectedFlow(doXML), flow));
			((DecisionBranchFlow) flow).setProb(doXML.getProb());
		}

		flow.setId(flowXML.getId());
		flow.setIterations(getXMLFromJaxb("iterations",
				es.ull.isaatc.simulation.xml.RandomNumber.class, flowXML
						.getIterations()));
		flow.setParent(parent);
		return flow;
	}

	private static String getXMLFromJaxb(String alias, Class c, Object obj) {
		XStream xstream = new XStream();
		xstream.alias(alias, c);
		xstream.setMode(XStream.NO_REFERENCES);
		String xml = xstream.toXML(obj);
		if (xml.equalsIgnoreCase("<null/>"))
			return "";
		return xml;
	}

	public static Object getJaxbFromXML(String content, Class c, String alias) {
		XStream xstream = new XStream();
		if (c != null)
			xstream.alias(alias, c);
		return xstream.fromXML(content);
	}

	public static Object validate(String instance, Schema schema, String xml)
			throws JAXBException, ClassNotFoundException {
		JAXBContext jc = JAXBContext.newInstance(new Class[] { Class
				.forName(instance) });
		Unmarshaller u = jc.createUnmarshaller();
		u.setSchema(schema);
		return u.unmarshal(new StringReader(xml));
	}
}
