package grafical.sighos.plugin.commands;


import java.util.ArrayList;
import java.util.HashMap;

import grafical.sighos.plugin.Application;
import grafical.sighos.plugin.tree.model.handler.A_property;
import grafical.sighos.plugin.tree.model.handler.W_property;

import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.jface.bindings.keys.ParseException;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.SimpleContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class NewActivityForm3 extends TitleAreaDialog {

	private Spinner w11_Number;
	private Spinner w12_Number;
	private Spinner w13_Number;

	private Spinner w21_Number;
	private Spinner w22_Number;
	private Spinner w23_Number;
	
	private Spinner w31_Number;
	private Spinner w32_Number;
	private Spinner w33_Number;

	private Spinner w41_Number;
	private Spinner w42_Number;
	private Spinner w43_Number;

	private Spinner w51_Number;
	private Spinner w52_Number;
	private Spinner w53_Number;	



	private ArrayList<Integer> w_info_n = new ArrayList<Integer>();	

	private ArrayList<String> func = new ArrayList<String>();
	private ArrayList<String> work = new ArrayList<String>();	


	public NewActivityForm3(Shell parentShell, ArrayList<String> fun, ArrayList<String> wor) {
		super(parentShell);
		this.func = wor;
		this.work = fun;		
	}
	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Creación de Nuevo Objeto");
		// Set the message
		setMessage("Nuevo Activity", IMessageProvider.INFORMATION);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);
		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;


		Label label9 = new Label(parent, SWT.NONE);
		label9.setText("Workgroup");
		Label label10 = new Label(parent, SWT.NONE);
		label10.setText("Funcion");	
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
  
		//REPEAT FOR 5 times        
		label10 = new Label(parent, SWT.NONE);
		label10.setText(work.get(0));	
	
		label10 = new Label(parent, SWT.NONE);
		
		label10.setText(func.get(0));
	
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");		
 
		if ( Application.functions.get(func.get(0)) == 3 ){
			w11_Number = new Spinner(parent, 0);
			w11_Number.setEnabled(true);		
		//	w11_Number.setMinimum(0);
			w11_Number.setMaximum(1000);	
	        w11_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
	        
			w12_Number = new Spinner(parent, 0);
			w12_Number.setEnabled(true);		
		//	w12_Number.setMinimum(0);
			w12_Number.setMaximum(1000);	
	        w12_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
	        
			w13_Number = new Spinner(parent, 0);
			w13_Number.setEnabled(true);		
		//	w13_Number.setMinimum(0);
			w13_Number.setMaximum(1000);	
	        w13_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));		        
			
		}
		
		if ( Application.functions.get(func.get(0)) == 2 ){
			w11_Number = new Spinner(parent, 0);
			w11_Number.setEnabled(true);		
		//	w11_Number.setMinimum(0);
			w11_Number.setMaximum(1000);	
	        w11_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
	        
			w12_Number = new Spinner(parent, 0);
			w12_Number.setEnabled(true);		
		//	w12_Number.setMinimum(0);
			w12_Number.setMaximum(1000);	
	        w12_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));

			label10 = new Label(parent, SWT.NONE);
			label10.setText("");	
			
		}	
		if ( Application.functions.get(func.get(0)) == 1 ){

			w11_Number = new Spinner(parent, 0);
			w11_Number.setEnabled(true);		
		//	w11_Number.setMinimum(0);
			w11_Number.setMaximum(1000);	
	        w11_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
	        
			label10 = new Label(parent, SWT.NONE);
			label10.setText("");	

			label10 = new Label(parent, SWT.NONE);
			label10.setText("");	
			
		}			

//fin de 1
		
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		
		if ( work.size() > 1) {
			label10 = new Label(parent, SWT.NONE);
			label10.setText(work.get(1));	
			label10 = new Label(parent, SWT.NONE);
			label10.setText(func.get(1));
			label10 = new Label(parent, SWT.NONE);
			label10.setText("");		
	 
			if ( Application.functions.get(func.get(1)) == 3 ){
				w21_Number = new Spinner(parent, 0);
				w21_Number.setEnabled(true);		
			//	w21_Number.setMinimum(0);
				w21_Number.setMaximum(1000);	
		        w21_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w22_Number = new Spinner(parent, 0);
				w22_Number.setEnabled(true);		
			//	w22_Number.setMinimum(0);
				w22_Number.setMaximum(1000);	
		        w22_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
		        
				w23_Number = new Spinner(parent, 0);
				w23_Number.setEnabled(true);		
			//	w23_Number.setMinimum(0);
				w23_Number.setMaximum(1000);	
		        w23_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));		        
				
			}
			
			if ( Application.functions.get(func.get(1)) == 2 ){
				w21_Number = new Spinner(parent, 0);
				w21_Number.setEnabled(true);		
			//	w21_Number.setMinimum(0);
				w21_Number.setMaximum(1000);	
		        w21_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w22_Number = new Spinner(parent, 0);
				w22_Number.setEnabled(true);		
			//	w22_Number.setMinimum(0);
				w22_Number.setMaximum(1000);	
		        w22_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}	
			if ( Application.functions.get(func.get(1)) == 1 ){

				w21_Number = new Spinner(parent, 0);
				w21_Number.setEnabled(true);		
			//	w21_Number.setMinimum(0);
				w21_Number.setMaximum(1000);	
		        w21_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}			
		}
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");		
		if ( work.size() > 2) {
			label10 = new Label(parent, SWT.NONE);
			label10.setText(work.get(2));	
			label10 = new Label(parent, SWT.NONE);
			label10.setText(func.get(2));
			label10 = new Label(parent, SWT.NONE);
			label10.setText("");		
	 
			if ( Application.functions.get(func.get(2)) == 3 ){
				w31_Number = new Spinner(parent, 0);
				w31_Number.setEnabled(true);		
			//	w31_Number.setMinimum(0);
				w31_Number.setMaximum(1000);	
		        w31_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w32_Number = new Spinner(parent, 0);
				w32_Number.setEnabled(true);		
			//	w32_Number.setMinimum(0);
				w32_Number.setMaximum(1000);	
		        w32_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
		        
				w33_Number = new Spinner(parent, 0);
				w33_Number.setEnabled(true);		
			//	w33_Number.setMinimum(0);
				w33_Number.setMaximum(1000);	
		        w33_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));		        
				
			}
			
			if ( Application.functions.get(func.get(2)) == 2 ){
				w31_Number = new Spinner(parent, 0);
				w31_Number.setEnabled(true);		
			//	w31_Number.setMinimum(0);
				w31_Number.setMaximum(1000);	
		        w31_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w32_Number = new Spinner(parent, 0);
				w32_Number.setEnabled(true);		
			//	w32_Number.setMinimum(0);
				w32_Number.setMaximum(1000);	
		        w32_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}	
			if ( Application.functions.get(func.get(2)) == 1 ){

				w31_Number = new Spinner(parent, 0);
				w31_Number.setEnabled(true);		
			//	w31_Number.setMinimum(0);
				w31_Number.setMaximum(1000);	
		        w31_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}			
		}
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");		
		if ( work.size() > 3) {
			label10 = new Label(parent, SWT.NONE);
			label10.setText(work.get(3));	
			label10 = new Label(parent, SWT.NONE);
			label10.setText(func.get(3));
			label10 = new Label(parent, SWT.NONE);
			label10.setText("");		
	 
			if ( Application.functions.get(func.get(3)) == 3 ){
				w41_Number = new Spinner(parent, 0);
				w41_Number.setEnabled(true);		
			//	w41_Number.setMinimum(0);
				w41_Number.setMaximum(1000);	
		        w41_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w42_Number = new Spinner(parent, 0);
				w42_Number.setEnabled(true);		
			//	w42_Number.setMinimum(0);
				w42_Number.setMaximum(1000);	
		        w42_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
		        
				w43_Number = new Spinner(parent, 0);
				w43_Number.setEnabled(true);		
			//	w43_Number.setMinimum(0);
				w43_Number.setMaximum(1000);	
		        w43_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));		        
				
			}
			
			if ( Application.functions.get(func.get(3)) == 2 ){
				w41_Number = new Spinner(parent, 0);
				w41_Number.setEnabled(true);		
			//	w41_Number.setMinimum(0);
				w41_Number.setMaximum(1000);	
		        w41_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w42_Number = new Spinner(parent, 0);
				w42_Number.setEnabled(true);		
			//	w42_Number.setMinimum(0);
				w42_Number.setMaximum(1000);	
		        w42_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}	
			if ( Application.functions.get(func.get(3)) == 1 ){

				w41_Number = new Spinner(parent, 0);
				w41_Number.setEnabled(true);		
			//	w41_Number.setMinimum(0);
				w41_Number.setMaximum(1000);	
		        w41_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}			
		}
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("");		
		if ( work.size() > 4) {
			label10 = new Label(parent, SWT.NONE);
			label10.setText(work.get(4));	
			label10 = new Label(parent, SWT.NONE);
			label10.setText(func.get(4));
			label10 = new Label(parent, SWT.NONE);
			label10.setText("");		
	 
			if ( Application.functions.get(func.get(4)) == 3 ){
				w51_Number = new Spinner(parent, 0);
				w51_Number.setEnabled(true);		
			//	w51_Number.setMinimum(0);
				w51_Number.setMaximum(1000);	
		        w51_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w52_Number = new Spinner(parent, 0);
				w52_Number.setEnabled(true);		
			//	w52_Number.setMinimum(0);
				w52_Number.setMaximum(1000);	
		        w52_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
		        
				w53_Number = new Spinner(parent, 0);
				w53_Number.setEnabled(true);		
			//	w53_Number.setMinimum(0);
				w53_Number.setMaximum(1000);	
		        w53_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));		        
				
			}
			
			if ( Application.functions.get(func.get(4)) == 2 ){
				w51_Number = new Spinner(parent, 0);
				w51_Number.setEnabled(true);		
			//	w51_Number.setMinimum(0);
				w51_Number.setMaximum(1000);	
		        w51_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				w52_Number = new Spinner(parent, 0);
				w52_Number.setEnabled(true);		
			//	w52_Number.setMinimum(0);
				w52_Number.setMaximum(1000);	
		        w52_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}	
			if ( Application.functions.get(func.get(4)) == 1 ){

				w51_Number = new Spinner(parent, 0);
				w51_Number.setEnabled(true);		
			//	w51_Number.setMinimum(0);
				w51_Number.setMaximum(1000);	
		        w51_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));
		        
				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	

				label10 = new Label(parent, SWT.NONE);
				label10.setText("");	
				
			}			
		}




		return parent;

	}





	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		GridData gridData = new GridData();
		gridData.verticalAlignment = GridData.FILL;
		gridData.horizontalSpan = 3;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = SWT.CENTER;
		parent.setLayoutData(gridData);
		// Create Add button
		// Own method as we need to overview the SelectionAdapter
		createOkButton(parent, OK, "FIN", true);
		// Add a SelectionListener
		// Create Cancel button
		Button cancelButton = createButton(parent, CANCEL, "Cancel", false);
		// Add a SelectionListener
		cancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setReturnCode(CANCEL);
				close();
			}
		});
	}
	protected Button createOkButton(Composite parent, int id, String label,
			boolean defaultButton) {
		// increment the number of columns in the button bar
		((GridLayout) parent.getLayout()).numColumns++;
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		button.setFont(JFaceResources.getDialogFont());
		button.setData(new Integer(id));
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (isValidInput()) {
					okPressed();
				}
			}
		});
		if (defaultButton) {
			Shell shell = parent.getShell();
			if (shell != null) {
				shell.setDefaultButton(button);
			}
		}
		setButtonLayoutData(button);
		return button;
	}
	private boolean isValidInput() {
		boolean valid = true;
		return valid;
	}
	// We allow the user to resize this dialog
	@Override
	protected boolean isResizable() {
		return true;
	}
	// We need to have the textFields into Strings because the UI gets disposed
	// and the Text Fields are not accessible any more.
	private void saveInput() {

		
		

		w_info_n.add(new Integer(w11_Number.getSelection()));		
		if ( Application.functions.get(func.get(0)) > 1) 
			w_info_n.add(new Integer(w12_Number.getSelection()));	
		if ( Application.functions.get(func.get(0)) > 2)
			w_info_n.add(new Integer(w13_Number.getSelection()));				
		if ( func.size() > 1){
			w_info_n.add(new Integer(w21_Number.getSelection()));		
			if ( Application.functions.get(func.get(1)) > 1) 
				w_info_n.add(new Integer(w22_Number.getSelection()));	
			if ( Application.functions.get(func.get(1)) > 2)
				w_info_n.add(new Integer(w23_Number.getSelection()));	
		}
		if ( func.size() > 2) {
			w_info_n.add(new Integer(w31_Number.getSelection()));		
			if ( Application.functions.get(func.get(2)) > 1) 
				w_info_n.add(new Integer(w32_Number.getSelection()));	
			if ( Application.functions.get(func.get(2)) > 2)
				w_info_n.add(new Integer(w33_Number.getSelection()));	
		}
		 if ( func.size() > 3 ) {
				w_info_n.add(new Integer(w41_Number.getSelection()));		
				if ( Application.functions.get(func.get(3)) > 1) 
					w_info_n.add(new Integer(w42_Number.getSelection()));	
				if ( Application.functions.get(func.get(3)) > 2)
					w_info_n.add(new Integer(w43_Number.getSelection()));	
		 }
		if ( func.size() > 4) {
			w_info_n.add(new Integer(w51_Number.getSelection()));		
			if ( Application.functions.get(func.get(4)) > 1) 
				w_info_n.add(new Integer(w52_Number.getSelection()));	
			if ( Application.functions.get(func.get(4)) > 2)
				w_info_n.add(new Integer(w53_Number.getSelection()));		
		}


	}
	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}


	public ArrayList<Integer> getW_Info_n() {
		return w_info_n;
	}	


}