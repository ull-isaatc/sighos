/**
 * 
 */
package es.ull.isaatc.simulation.jppf.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jppf.client.JPPFClient;
import org.jppf.server.protocol.JPPFTask;
import org.jppf.task.storage.MemoryMapDataProvider;

class HelloTask extends JPPFTask {
	private static final long serialVersionUID = -8673516108218654234L;
	int id;
	public HelloTask(int id) {
		this.id = id;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		
		// TODO Auto-generated method stub
		setResult("Hello World " + id);
	}

}

class Paquito implements Serializable {
	int i;
	Paquito(int i) {
		this.i = i;
	}
}
class DummyTask extends JPPFTask {
	int id;
//	transient Paquito p;
	
	DummyTask(int id) {
		this.id = id;
	}
	public void run() {
		id = -id;
//		p = new Paquito(id);
		try {
			Paquito p = (Paquito)getDataProvider().getValue("SIM");
			p.i--;
			getDataProvider().setValue("RES", new Integer(2));
			setResult(new Integer(p.i));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TestJPPF {
	public static final int NTASK = 1;
    static final int NDIAS = 1;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JPPFClient client = new JPPFClient();
		List<JPPFTask> tasks = new ArrayList<JPPFTask>();
		MemoryMapDataProvider data = new MemoryMapDataProvider();
		Paquito p = new Paquito(10);
		data.setValue("SIM", p);
		data.setValue("RES", 1);
		for (int i = 0; i < NTASK; i++)
			tasks.add(new DummyTask(i+1));
		List<JPPFTask> results;
		try {
//			results = client.submit(tasks, null);
			results = client.submit(tasks, data);
			for (JPPFTask t : results) {
				Object result = t.getResult();
				System.out.println(result + " " + t.getPosition() + " " + p.i);
			}
			System.out.println((Integer)data.getValue("RES"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			client.close();
			System.exit(0);
		}
	}

}
