/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import es.ull.isaatc.simulation.listener.EventListener;
import es.ull.isaatc.simulation.listener.ListenerController;

/**
 * @author Iván
 *
 */
public class GSListenerController extends ListenerController {
	private String filename;
	private GSListenerControllerArray parent;
	private GSListenerArray gsListeners;

	/**
	 * @param filename
	 */
	public GSListenerController(GSListenerControllerArray parent, String filename, GSListenerArray listeners) {
		super();
		this.filename = filename;
		this.parent = parent;
		this.gsListeners = listeners;
		for (EventListener l : listeners.getListeners())
			addListener(l);
	}

	@Override
	public void end() {
		super.end();
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		
		ArrayList <GSResult> res = new ArrayList<GSResult>();
		for (EventListener l : getListeners()) {
			if (l instanceof GSListener) {
				((GSListener)l).setResults(wb);
				res.add(((GSListener)l).getResults());
			}
			else
				System.out.println(l.toString());
		}
		try {
			FileOutputStream out = new FileOutputStream(filename);
			wb.write(out);
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		parent.notifyEnd(gsListeners);
	}
	
}
