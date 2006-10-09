import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import es.ull.isaatc.simulation.xml.*;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.simulation.xml.Resource.TimeTable.Dur;

/**
 * 
 */

/**
 * @author Roberto Muñoz
 */
public class ModelGenerator {
	
	
	public static Model model;
	
	
	
	public static void main(String arg[]) {

		int ntr = Integer.parseInt(arg[0]);
		int nr = Integer.parseInt(arg[1]);
		int nact = Integer.parseInt(arg[2]);
		int nte = Integer.parseInt(arg[3]);
		
		model = new Model();
		
		for (int i = 0; i < ntr; i++) {
			ResourceType rt = new ResourceType();
			rt.setId(i);
			rt.setDescription("Tipo de recurso " + i);
			model.getResourceType().add(rt);
		}
		
		for (int i = 0; i < nr; i++) {
			Resource r = new Resource();
			r.setId(i);
			r.setDescription("Recurso " + i);
			r.setUnits(i);
			TimeTable tt = new TimeTable();
			Dur dur = new Dur();
			dur.setTimeUnit(CommonFreq.HOUR);
			dur.setValue(i);
			tt.setDur(dur);
			Cycle cycle = new Cycle();
			cycle.setStartTs(0);
			cycle.setTimeUnit(CommonFreq.HOUR);
			cycle.setIterations(0);
			RandomNumber rn = new RandomNumber();
			rn.setDist(Distribution.FIXED);
			rn.setP1(10.0);
			rn.setP2(10.0);
			rn.setP3(10.0);
			cycle.setPeriod(rn);
			tt.setCycle(cycle);
			tt.getRtId().add(i);
			tt.getRtId().add((i + i) % ntr);
			tt.getRtId().add((i + 2 * i) % ntr);
			r.getTimeTable().add(tt);
			model.getResource().add(r);
		}
		
		for (int i = 0; i < nact; i++) {
			Activity act = new Activity();
			act.setId(i);
			act.setDescription("Actividad " + i);
			model.getActivity().add(act);
		}
		
		for (int i = 0; i < nte; i++) {
			ElementType et = new ElementType();
			et.setId(i);
			et.setDescription("Tipo de elemento " + i);
			model.getElementType().add(et);
		}
		
		RootFlow rf = new RootFlow();
		rf.setId(1);
		rf.setDescription("Flujo 1");
		FlowChoice fc = new FlowChoice();
		SingleFlow sf = new SingleFlow();
		sf.setActId(23);
		sf.setId(1);
		fc.setSingle(sf);
		rf.setFlow(fc);
		
		model.getRootFlow().add(rf);
		
		saveModel(new File("model.xml"));
	}
	
	
	private static void saveModel(File modelFile) {

		try {
			JAXBContext jc = JAXBContext.newInstance("es.ull.isaatc.simulation.xml");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(model, new FileOutputStream(modelFile));
		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
}
