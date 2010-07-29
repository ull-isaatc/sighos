package grafical.sighos.plugin.commands;



import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.swt.SWT;

import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;

import java.io.*;

public class saveProyectHandler extends AbstractHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		if ( Application.initialized_proyect ) {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			// File standard dialog

			FileDialog fileDialog = new FileDialog( shell, SWT.SAVE );
			// Set the text
			fileDialog.setText("Select File");
			// Set filter on .txt files
			fileDialog.setFilterExtensions(new String[] { "*.gdata" });
			// Put in a readable name for the filter
			fileDialog.setFilterNames(new String[] { "GSighosFile(*.gdata)" });
			// Open Dialog and save result of selection


			String selected = fileDialog.open();

			System.out.println(selected);


			FileOutputStream fos = null;
			ObjectOutputStream out  = null;
			try
			{
				fos = new FileOutputStream(selected);
				out = new ObjectOutputStream(fos);				

				//Descripcion del proyecto		
				out.writeObject(Application.name_proyect);
				out.writeObject(Application.author_proyect);
				out.writeObject(Application.descripcion_proyect);
				out.writeObject(Application.time_proyect);
				out.writeObject(Application.loop_exp);				
				//Tipos de recursos
				out.writeObject(RT_property.rt_List);
				out.writeObject(ET_property.et_List);
				out.writeObject(C_property.c_List);
				out.writeObject(R_property.r_List);
				out.writeObject(W_property.w_List);
				out.writeObject(A_property.a_List);


			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					// Nuevamente aprovechamos el finally para 
					// asegurarnos que se cierra el fichero.
					if (null != fos)
						fos.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

		}
		else {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			MessageDialog.openWarning(shell, "Warning", "Need a proyect First");
		}

		return null;
	}
}
