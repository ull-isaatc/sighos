package grafical.sighos.plugin.editor.editors;

import grafical.sighos.plugin.tree.model.handler.RT_property;
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

public class RTEditor extends EditorPart {

	public static final String ID = "grafical.sighos.plugin.editor.editors.RTEditor";
	private RT_property rt_obj;
	private Text text2;
	private Text text3;	

	public RTEditor() {
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		rt_obj.setName(text2.getText());
		rt_obj.code_generator();
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
		rt_obj = ((RTEditorInput) input).getRT_property();
		setPartName(rt_obj.getName());
	}
	@Override
	public boolean isDirty() {
		if (rt_obj.getName().equals(text2.getText())) {
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
		text2.setText(rt_obj.getName());

		Label label2 = new Label(parent, SWT.BORDER);
		label2.setText("Var Name:");
		Label label_varName = new Label(parent, SWT.BORDER);		
		label_varName.setText(rt_obj.getVarName());

		Label label3 = new Label(parent, SWT.BORDER);
		label3.setText("Number: ");
		Label label_number = new Label(parent, SWT.BORDER);		
		label_number.setText(Integer.toString(rt_obj.getObj_Number()));

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
		text3.setText(rt_obj.getCode());		
		text3.setEditable(false);

	}
	@Override
	public void setFocus() {
	}
}