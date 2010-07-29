package grafical.sighos.plugin.editor.editors;

import grafical.sighos.plugin.tree.model.handler.W_property;
import grafical.sighos.plugin.view.inicial;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class WEditor extends EditorPart {

	public static final String ID = "grafical.sighos.plugin.editor.editors.WEditor";
	private W_property w_obj;
	private Text text2;
	private Text text3;
	private Text text4;	
	private Text text5;	
	private Text text6;	
	private Text text7;	
	private Text text9;		

	public WEditor() {
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		w_obj.setName(text2.getText());
		w_obj.code_generator();
		inicial.refresh();
		//rt_obj.setCode(text3.getText());			
	}
	@Override
	public void doSaveAs() {
	}
	@Override
	public void init(IEditorSite site, IEditorInput input)
	throws PartInitException {
		setSite(site);
		setInput(input);
		w_obj = ((WEditorInput) input).getW_property();
		setPartName(w_obj.getName());
	}
	@Override
	public boolean isDirty() {
		if (w_obj.getName().equals(text2.getText())) {
			return false;
		}
		//if (rt_obj.getCode().equals(text3.getText())) {
		//	   return false;
		//	}		
		return true;
	}
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);



		Label label1 = new Label(parent, SWT.BORDER);
		label1.setText("Name: ");
		text2 = new Text(parent, SWT.BORDER);
		text2.setText(w_obj.getName());

		Label label2 = new Label(parent, SWT.BORDER);
		label2.setText("Var Name:");
		Label label_varName = new Label(parent, SWT.BORDER);		
		label_varName.setText(w_obj.getVarName());

		Label label3 = new Label(parent, SWT.BORDER);
		label3.setText("Number: ");
		Label label_number = new Label(parent, SWT.BORDER);		
		label_number.setText(Integer.toString(w_obj.getObj_Number()));

		//relleno vacio
		Label label5 = new Label(parent, SWT.CENTER);
		label5.setText("");
		Label label_trash = new Label(parent, SWT.CENTER);		
		label_trash.setText("");
		//FIN RELLENO VACIO
		Label label4 = new Label(parent, SWT.BORDER);
		label4.setText("CODE: ");
		//relleno
		label5 = new Label(parent, SWT.CENTER);
		label5.setText("");
		label5 = new Label(parent, SWT.CENTER);
		label5.setText("");
		//FIN RELLENO		
		text3 = new Text(parent, SWT.BORDER);	
		text3.setText(w_obj.getCode(0));		
		text3.setEditable(false);
				
		Label label50 = new Label(parent, SWT.CENTER);
		label50.setText("");

		//FIN RELLENO		
		text9 = new Text(parent, SWT.BORDER);	
		text9.setText(w_obj.getCode(1));		
		text9.setEditable(false);		
			
		
if ( w_obj.getCode().size() > 2) {
	
	Label  label51 = new Label(parent, SWT.CENTER);
	label51.setText("");
	//FIN RELLENO		
	text4 = new Text(parent, SWT.BORDER);	
	text4.setText(w_obj.getCode(2));		
	text4.setEditable(false);
}
if ( w_obj.getCode().size() > 3) {
	
	Label label52 = new Label(parent, SWT.CENTER);
	label52.setText("");

	//FIN RELLENO		
	text5 = new Text(parent, SWT.BORDER);	
	text5.setText(w_obj.getCode(3));		
	text5.setEditable(false);
}
if ( w_obj.getCode().size() > 4) {	
	
	Label label53 = new Label(parent, SWT.CENTER);
	label53.setText("");
	//FIN RELLENO		
	text6 = new Text(parent, SWT.BORDER);	
	text6.setText(w_obj.getCode(4));		
	text6.setEditable(false);
}
if ( w_obj.getCode().size() > 5) {
	
	Label label54 = new Label(parent, SWT.CENTER);
	label54.setText("");
	//FIN RELLENO		
	text7 = new Text(parent, SWT.BORDER);	
	text7.setText(w_obj.getCode(5));		
	text7.setEditable(false);
}

	}
	@Override
	public void setFocus() {
	}
}
