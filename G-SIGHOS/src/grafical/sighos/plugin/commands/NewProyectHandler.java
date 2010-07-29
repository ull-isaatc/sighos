
package grafical.sighos.plugin.commands;


import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.commands.FormularioNuevoProyecto;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;
import grafical.sighos.plugin.view.comando;
import grafical.sighos.plugin.view.inicial;


public class NewProyectHandler extends AbstractHandler {

	private Object[] result;

	@Override

	public Object execute(ExecutionEvent event) throws ExecutionException {

		Shell shell1 = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
		boolean loading = true;
		if ( Application.initialized_proyect == true){
			loading = false;
			if ( MessageDialog.openConfirm(shell1, "Confirm", "The unsaved proyect must be lost") )
				loading = true;
		}		
		if (loading == true ) {

			FormularioNuevoProyecto dialog = new FormularioNuevoProyecto(HandlerUtil.getActiveWorkbenchWindow(
					event).getShell());
			dialog.create();
			if( dialog.open() == Window.OK) {

				Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
				ElementListSelectionDialog dialog2 = new ElementListSelectionDialog(
						shell, new LabelProvider());
				dialog2.setElements(new String[] { "DAY", "HOUR", "MINUTE", "SECOND" });
				dialog2.setTitle("Nuevo Proyecto de Simulacion");
				dialog2.setMessage("Seleccione la unidad de tiempo");
				// User pressed cancel
				if (dialog2.open()!= Window.OK){
					return false;
				}
				result = dialog2.getResult();
				String unitTime = "";
				for (Object s : result) {
					unitTime += s.toString();
				}
				RT_property.rt_List  = new ArrayList<RT_property>();
				ET_property.et_List  = new ArrayList<ET_property>();
				C_property.c_List  = new ArrayList<C_property>();
				R_property.r_List  = new ArrayList<R_property>();
				W_property.w_List  = new ArrayList<W_property>();
				A_property.a_List  = new ArrayList<A_property>();

				//Para cerrar el editor
				IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
				IWorkbenchPage page = window.getActivePage();			
				page.closeAllEditors(false);			

				/* Nuevas propiedades del proyecto */			
				Application.name_proyect = dialog.getProjectName();
				Application.author_proyect = dialog.getAutor();
				Application.descripcion_proyect = dialog.getDescripcion();
				Application.loop_exp = dialog.getLoop_exp();				
				Application.time_proyect = unitTime;

				comando.refresh();

				/*Crea el arbol base */			
				inicial.treeViewer.setInput(inicial.getInitalInput());
				Application.initialized_proyect = true;


			}
		}
		return null;
	}
}
