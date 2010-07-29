package grafical.sighos.plugin.editor.editors;

import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.view.inicial;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

public class CEditor extends EditorPart {

	public static final String ID = "grafical.sighos.plugin.editor.editors.CEditor";
	private C_property c_obj;
	private Text text2;
	private Text text3;	

	public CEditor() {
	}


	@Override
	public void doSave(IProgressMonitor monitor) {
		c_obj.setName(text2.getText());
		c_obj.code_generator();
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
		c_obj = ((CEditorInput) input).getC_property();
		setPartName(c_obj.getName());
	}
	@Override
	public boolean isDirty() {
		if (c_obj.getName().equals(text2.getText())) {
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
		text2.setText(c_obj.getName());

		Label label2 = new Label(parent, SWT.BORDER);
		label2.setText("Var Name:");
		Label label_varName = new Label(parent, SWT.BORDER);		
		label_varName.setText(c_obj.getVarName());

		Label label3 = new Label(parent, SWT.BORDER);
		label3.setText("Number: ");
		Label label_number = new Label(parent, SWT.BORDER);		
		label_number.setText(Integer.toString(c_obj.getObj_Number()));

		
		Label tr = new Label(parent, SWT.NONE);
		tr.setText("");
		tr = new Label(parent, SWT.NONE);
		tr.setText("");	

		Label label31 = new Label(parent, SWT.NONE);
		label31.setText("Modo");
		Label label32 = new Label(parent, SWT.NONE);
		label32.setText(c_obj.getTipo());
		
		if ( c_obj.getTipo().equals("Compuesto")){
			Label alfa = new Label(parent, SWT.NONE);
			alfa.setText("");			
			Label label01 = new Label(parent, SWT.NONE);
			label01.setText(c_obj.getCycle());
		}

		//Especifico
		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Instante de comienzo");
		Label label42 = new Label(parent, SWT.NONE);
		label42.setText(""+c_obj.getVals().get(0));


		Label label5 = new Label(parent, SWT.NONE);
		label5.setText("Duracion del ciclo");
		Label label43 = new Label(parent, SWT.NONE);
		label43.setText(""+c_obj.getVals().get(1));

		Label label6 = new Label(parent, SWT.NONE);
		label6.setText("Iteraciones");
		Label label44 = new Label(parent, SWT.NONE);
		label44.setText(""+c_obj.getVals().get(2));
	
		
		
		//relleno vacio
		Label label57 = new Label(parent, SWT.CENTER);
		label57.setText("");
		Label label_trash = new Label(parent, SWT.CENTER);		
		label_trash.setText("");
		//FIN RELLENO VACIO
		Label label47 = new Label(parent, SWT.BORDER);
		label47.setText("CODE: ");
		//relleno
		Label label59 = new Label(parent, SWT.CENTER);
		label59.setText("");
		label59 = new Label(parent, SWT.CENTER);
		label59.setText("");
		//FIN RELLENO		
		text3 = new Text(parent, SWT.BORDER);	
		text3.setText(c_obj.getCode());		
		text3.setEditable(false);

	}
	@Override
	public void setFocus() {
	}
}