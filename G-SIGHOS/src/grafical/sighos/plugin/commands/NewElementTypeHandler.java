package grafical.sighos.plugin.commands;



import grafical.sighos.plugin.Application;


import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.view.inicial;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.handlers.HandlerUtil;





public class NewElementTypeHandler extends AbstractHandler {



	@Override

	public Object execute(ExecutionEvent event) throws ExecutionException {
		if ( Application.initialized_proyect ) {
			NewElementTypeForm dialog = new NewElementTypeForm(HandlerUtil.getActiveWorkbenchWindow(
					event).getShell());
			dialog.create();
			if( dialog.open() == Window.OK) {

				/*
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			ElementListSelectionDialog dialog2 = new ElementListSelectionDialog(
			shell, new LabelProvider());


			dialog2.setElements(new String[] { "Dias", "Horas", "Minutos", "Segundos" });
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
						System.out.println(unitTime);

				 */



				//Add the new type of element			


				System.out.println(dialog.getName());
				System.out.println();
				String code_generator = "ElementType " + dialog.getVarName() +
				" = new ElementType( " + dialog.getNumber() + ", this, \"" +
				dialog.getName() + "\" );";

				ET_property aux = new ET_property( dialog.getName(),	dialog.getVarName(),
						dialog.getNumber(),code_generator );

				ET_property.et_List.add(aux);
				inicial.et_branch.add(aux);

				// This call reset the view and the new element type
				inicial.refresh();

			}


		}
		else {
			Shell shell = HandlerUtil.getActiveWorkbenchWindow(event).getShell();
			MessageDialog.openWarning(shell, "Warning", "Need a proyect First");
		}
		return null;
	}
}


