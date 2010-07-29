
package grafical.sighos.plugin.commands;



import grafical.sighos.plugin.Application;


import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.view.inicial;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;




public class NewCycleFormHandler extends AbstractHandler {


	@Override

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if ( Application.initialized_proyect ) {
			

			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ElementListSelectionDialog dialog2 = new ElementListSelectionDialog(
					shell, new LabelProvider());


			dialog2.setElements(new String[] { "Simple","Compuesto" });
			dialog2.setTitle("Creación de Nuevo Objeto");
			dialog2.setMessage("Nuevo Ciclo");
			// User pressed cancel
			if (dialog2.open()== Window.OK){

				Object[] result = dialog2.getResult();
				String unitTime = "";
				for (Object s : result) {
					unitTime += s.toString();
				}
				//System.out.println(unitTime);

				NewCycleForm dialog = new NewCycleForm(HandlerUtil.getActiveWorkbenchWindow(
						event).getShell(), unitTime);
				dialog.create();
				if( dialog.open() == Window.OK) {


					C_property aux = new C_property( dialog.getName(),	dialog.getVarName(),
							dialog.getNumber(), unitTime, dialog.getCycle(), dialog.getVals() );
					
					aux.code_generator();

					C_property.c_List.add(aux);
					inicial.c_branch.add(aux);

					// This call reset the view and the new element type
					inicial.refresh();


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
