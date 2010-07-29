package grafical.sighos.plugin.editor.handler;

/*import grafical.sighos.plugin.editor.editors.MiEditorPersonal;
import grafical.sighos.plugin.editor.editors.MiEditorPersonalEntrada;
import grafical.sighos.plugin.editor.model.Person;*/

import grafical.sighos.plugin.editor.editors.AEditor;
import grafical.sighos.plugin.editor.editors.AEditorInput;
import grafical.sighos.plugin.editor.editors.CEditor;
import grafical.sighos.plugin.editor.editors.CEditorInput;
import grafical.sighos.plugin.editor.editors.ETEditor;
import grafical.sighos.plugin.editor.editors.ETEditorInput;
import grafical.sighos.plugin.editor.editors.GEditor;
import grafical.sighos.plugin.editor.editors.GEditorInput;
import grafical.sighos.plugin.editor.editors.REditor;
import grafical.sighos.plugin.editor.editors.REditorInput;
import grafical.sighos.plugin.editor.editors.RTEditor;
import grafical.sighos.plugin.editor.editors.RTEditorInput;
import grafical.sighos.plugin.editor.editors.WEditor;
import grafical.sighos.plugin.editor.editors.WEditorInput;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.ET_property;
import grafical.sighos.plugin.tree.model.handler.G_property;
import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.R_property;
import grafical.sighos.plugin.tree.model.handler.W_property;
import grafical.sighos.plugin.view.inicial;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;


public class LlamadaEditor extends AbstractHandler implements IHandler {
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		// Get the view
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		inicial view = (inicial) page.findView(inicial.ID);
		// Get the selection
		ISelection selection = view.getSite().getSelectionProvider()
		.getSelection();
		if (selection != null && selection instanceof IStructuredSelection) {
			Object obj = ((IStructuredSelection) selection).getFirstElement();
			// If we had a selection lets open the editor
			if (obj != null) {
				if ( obj instanceof RT_property ) {
				   RT_property rt_obj= (RT_property) obj;
			   	   RTEditorInput input = new RTEditorInput(rt_obj);
				   try {				   
					   page.openEditor(input, RTEditor.ID);
				   } catch (PartInitException e) {
					   System.out.println(e.getStackTrace());
				   }

				}
				else if (obj instanceof ET_property ) {
					   ET_property et_obj= (ET_property) obj;
				   	   ETEditorInput input = new ETEditorInput(et_obj);
					   try {				   
						   page.openEditor(input, ETEditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}
				else if (obj instanceof W_property ) {
					   W_property w_obj= (W_property) obj;
				   	   WEditorInput input = new WEditorInput(w_obj);
					   try {				   
						   page.openEditor(input, WEditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}
				else if (obj instanceof R_property ) {
					   R_property r_obj= (R_property) obj;
				   	   REditorInput input = new REditorInput(r_obj);
					   try {				   
						   page.openEditor(input, REditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}
				else if (obj instanceof C_property ) {
					   C_property c_obj= (C_property) obj;
				   	   CEditorInput input = new CEditorInput(c_obj);
					   try {				   
						   page.openEditor(input, CEditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}
				else if (obj instanceof A_property ) {
					   A_property a_obj= (A_property) obj;
				   	   AEditorInput input = new AEditorInput(a_obj);
					   try {				   
						   page.openEditor(input, AEditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}
				else if (obj instanceof G_property ) {
					   G_property g_obj= (G_property) obj;
				   	   GEditorInput input = new GEditorInput(g_obj);
					   try {				   
						   page.openEditor(input, GEditor.ID);
					   } catch (PartInitException e) {
						   System.out.println(e.getStackTrace());
					   }				
				}				
				
			
			}
		}
		return null;
	}

}