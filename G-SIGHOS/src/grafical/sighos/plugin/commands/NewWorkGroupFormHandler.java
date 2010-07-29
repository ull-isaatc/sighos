
package grafical.sighos.plugin.commands;



import grafical.sighos.plugin.Application;


import grafical.sighos.plugin.tree.model.handler.W_property;
import grafical.sighos.plugin.view.inicial;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;




public class NewWorkGroupFormHandler extends AbstractHandler {


	@Override

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if ( Application.initialized_proyect ) {
			NewWorkGroupForm dialog = new NewWorkGroupForm(HandlerUtil.getActiveWorkbenchWindow(
					event).getShell());
			dialog.create();
			if( dialog.open() == Window.OK) {

				//Add the new type of element						
				NewWorkGroupForm2 dialog2 = new NewWorkGroupForm2(HandlerUtil.getActiveWorkbenchWindow(
						event).getShell(), dialog.getSize_Work());

				dialog2.create();

				if( dialog2.open() == Window.OK) {


					System.out.println(dialog.getName());
					System.out.println();
					String code_generator = "";

					W_property aux = new W_property( dialog.getName(),	dialog.getVarName(),
							dialog.getNumber(), dialog2.getW_Info(), dialog2.getW_Info_n() );
					aux.code_generator();

					W_property.w_List.add(aux);
					inicial.w_branch.add(aux);

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