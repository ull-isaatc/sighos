/**
 * 
 */
package es.ull.isaatc.HUNSC.cirgen.listener;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

	/**
	 * @param filename
	 */
	public GSListenerController(GSListenerControllerArray parent, String filename, EventListener[] listeners) {
		super();
		this.filename = filename;
		this.parent = parent;
		for (EventListener l : listeners)
			addListener(l);
	}

	@Override
	public void end() {
		super.end();
		// create a new workbook
		HSSFWorkbook wb = new HSSFWorkbook();
		
		for (EventListener l : getListeners()) {
			if (l instanceof ToExcel)
				((ToExcel)l).setResult(wb);
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
		parent.notifyEnd();
	}
	
}
