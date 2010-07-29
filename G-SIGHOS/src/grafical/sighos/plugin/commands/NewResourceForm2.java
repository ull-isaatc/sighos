package grafical.sighos.plugin.commands;


import java.util.ArrayList;
import java.util.HashMap;

import grafical.sighos.plugin.tree.model.handler.RT_property;
import grafical.sighos.plugin.tree.model.handler.C_property;
import grafical.sighos.plugin.tree.model.handler.R_property;

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
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class NewResourceForm2 extends TitleAreaDialog {
	private Text w1_Text;
	private Text w2_Text;
	private Text w3_Text;
	private Text w4_Text;
	private Text w5_Text;	
	private Text w12_Text;
	private Text w22_Text;
	private Text w32_Text;
	private Text w42_Text;
	private Text w52_Text;	
	private Spinner w1_Number;
	private Spinner w2_Number;
	private Spinner w3_Number;
	private Spinner w4_Number;
	private Spinner w5_Number;	

	private ArrayList<String> w_info = new ArrayList<String>();
	private ArrayList<String> w_info2 = new ArrayList<String>();	
	private ArrayList<Integer> w_info_n = new ArrayList<Integer>();	
	
	private HashMap<String, String> convertion = new HashMap<String, String>();
	private HashMap<String, String> convertion2 = new HashMap<String, String>();	

	private int size;


	public NewResourceForm2(Shell parentShell, int siz) {
		super(parentShell);
		this.size = siz;
	}
	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Creación de Nuevo Objeto");
		// Set the message
		setMessage("Nuevo Resource", IMessageProvider.INFORMATION);
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
		gridData.horizontalAlignment = GridData.CENTER;
		gridData.verticalAlignment = GridData.CENTER;		


		Label label9 = new Label(parent, SWT.NONE);
		label9.setText("Numero de entradas seleccionado:");
		Spinner sp1 = new Spinner(parent, 0);
		sp1.setSelection(size);
		sp1.setEnabled(false);
		Label label10 = new Label(parent, SWT.NONE);
		label10.setText("");	
		



		String[] nombres_valido = new String[RT_property.rt_List.size()];
		int j = 0;
		for ( int i = 0; i < RT_property.rt_List.size(); i++) {
			if (!RT_property.rt_List.get(i).getName().equals("$$$$$trash$$$$$") ) {
		//	if (!RT_property.rt_List.get(i).equals("$$$$$trash$$$$$") ) {			
				convertion.put(RT_property.rt_List.get(i).getName(), RT_property.rt_List.get(i).getVarName());
				nombres_valido[j] = RT_property.rt_List.get(i).getName();
				j++;
			}
		}
	
		String[] nombres_validos = new String[j];		
		for ( int i = 0; i < j; i++) {			
				nombres_validos[i] = nombres_valido[i];
		}	
		
		String[] nombres_valido2 = new String[C_property.c_List.size()];
		j = 0;
		for ( int i = 0; i < C_property.c_List.size(); i++) {
			if (!C_property.c_List.get(i).getName().equals("$$$$$trash$$$$$") ) {
		//	if (!RT_property.rt_List.get(i).equals("$$$$$trash$$$$$") ) {			
				convertion2.put(C_property.c_List.get(i).getName(), C_property.c_List.get(i).getVarName());
				nombres_valido2[j] = C_property.c_List.get(i).getName();
				j++;
			}
		}
		

		String[] nombres_validos2 = new String[j];		
		for ( int i = 0; i < j; i++) {			
				nombres_validos2[i] = nombres_valido2[i];
		}			


		//REPEAT FOR 5 times
		Label labelCombo = new Label(parent, SWT.NONE);
		labelCombo.setText("Selecciona el \"ciclo\" ");
		label10 = new Label(parent, SWT.NONE);
		label10.setText("El instante de comienzo");
		label10 = new Label(parent, SWT.NONE);
		label10.setText(" El tipo de recurso");		
		
		
		w1_Text = new Text(parent, SWT.BORDER);
		w1_Number = new Spinner(parent, 0);
		w1_Number.setEnabled(true);		
		w1_Number.setMinimum(0);
		w1_Number.setMaximum(1440);		
        w1_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));	
		w12_Text = new Text(parent, SWT.BORDER);		
		//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		w1_Text.setLayoutData(data);
		ControlDecoration deco = new ControlDecoration(w1_Text, SWT.RIGHT);
		deco.setDescriptionText("Use CTRL + SPACE to see possible values");
		deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		deco.setShowOnlyOnFocus(false);
		w12_Text.setLayoutData(data);
		ControlDecoration deco2 = new ControlDecoration(w12_Text, SWT.RIGHT);
		deco2.setDescriptionText("Use CTRL + SPACE to see possible values");
		deco2.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
		deco2.setShowOnlyOnFocus(false);		
		// Help the user with the possible inputs
		// "." and "#" will also activate the content proposals
		char[] autoActivationCharacters = new char[] { '.', '#' };
		KeyStroke keyStroke;
		try {
			//
			keyStroke = KeyStroke.getInstance("Ctrl+Space");
			// assume that myTextControl has already been created in some way
			ContentProposalAdapter adapter = new ContentProposalAdapter(w1_Text,
					new TextContentAdapter(),
					new SimpleContentProposalProvider(nombres_validos2),
					keyStroke, autoActivationCharacters);
			ContentProposalAdapter adapter2 = new ContentProposalAdapter(w12_Text,
					new TextContentAdapter(),
					new SimpleContentProposalProvider(nombres_validos),
					keyStroke, autoActivationCharacters);			
		} catch (ParseException e) {
			e.printStackTrace();
		}		
		if ( size > 1) {

			w2_Text = new Text(parent, SWT.BORDER);
			w2_Number = new Spinner(parent, 0);
			w2_Number.setEnabled(true);		
			w2_Number.setMinimum(0);
			w2_Number.setMaximum(1440);	
	        w2_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));				
			w22_Text = new Text(parent, SWT.BORDER);		
			//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
			GridData data2 = new GridData(GridData.FILL_HORIZONTAL);
			w2_Text.setLayoutData(data2);
			ControlDecoration deco21 = new ControlDecoration(w2_Text, SWT.RIGHT);
			deco21.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco21.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco21.setShowOnlyOnFocus(false);
			w22_Text.setLayoutData(data);
			ControlDecoration deco22 = new ControlDecoration(w22_Text, SWT.RIGHT);
			deco22.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco22.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco22.setShowOnlyOnFocus(false);		
			// Help the user with the possible inputs

			try {
				//
				keyStroke = KeyStroke.getInstance("Ctrl+Space");
				// assume that myTextControl has already been created in some way
				ContentProposalAdapter adapter = new ContentProposalAdapter(w2_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos2),
						keyStroke, autoActivationCharacters);
				ContentProposalAdapter adapter2 = new ContentProposalAdapter(w22_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos),
						keyStroke, autoActivationCharacters);			
			} catch (ParseException e) {
				e.printStackTrace();
			}		
		}
		if ( size > 2) {

			w3_Text = new Text(parent, SWT.BORDER);
			w3_Number = new Spinner(parent, 0);
			w3_Number.setEnabled(true);		
			w3_Number.setMinimum(0);
			w3_Number.setMaximum(1440);
	        w3_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));				
			w32_Text = new Text(parent, SWT.BORDER);		
			//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
			GridData data3 = new GridData(GridData.FILL_HORIZONTAL);
			w3_Text.setLayoutData(data);
			ControlDecoration deco3 = new ControlDecoration(w3_Text, SWT.RIGHT);
			deco3.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco3.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco.setShowOnlyOnFocus(false);
			w32_Text.setLayoutData(data3);
			ControlDecoration deco32 = new ControlDecoration(w32_Text, SWT.RIGHT);
			deco32.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco32.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco32.setShowOnlyOnFocus(false);		
			// Help the user with the possible inputs

			try {
				//
				keyStroke = KeyStroke.getInstance("Ctrl+Space");
				// assume that myTextControl has already been created in some way
				ContentProposalAdapter adapter = new ContentProposalAdapter(w3_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos2),
						keyStroke, autoActivationCharacters);
				ContentProposalAdapter adapter2 = new ContentProposalAdapter(w32_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos),
						keyStroke, autoActivationCharacters);			
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}
		if ( size > 3) {

			w4_Text = new Text(parent, SWT.BORDER);
			w4_Number = new Spinner(parent, 0);
			w4_Number.setEnabled(true);		
			w4_Number.setMinimum(0);
			w4_Number.setMaximum(1440);
	        w4_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));				
			w42_Text = new Text(parent, SWT.BORDER);		
			//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
			GridData data4 = new GridData(GridData.FILL_HORIZONTAL);
			w4_Text.setLayoutData(data4);
			ControlDecoration deco4 = new ControlDecoration(w4_Text, SWT.RIGHT);
			deco4.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco4.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco4.setShowOnlyOnFocus(false);
			w42_Text.setLayoutData(data);
			ControlDecoration deco42 = new ControlDecoration(w42_Text, SWT.RIGHT);
			deco42.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco42.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco42.setShowOnlyOnFocus(false);		
			// Help the user with the possible inputs

			try {
				//
				keyStroke = KeyStroke.getInstance("Ctrl+Space");
				// assume that myTextControl has already been created in some way
				ContentProposalAdapter adapter = new ContentProposalAdapter(w4_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos2),
						keyStroke, autoActivationCharacters);
				ContentProposalAdapter adapter2 = new ContentProposalAdapter(w42_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos),
						keyStroke, autoActivationCharacters);			
			} catch (ParseException e) {
				e.printStackTrace();
			}	
		}
		if ( size > 4) {

			w5_Text = new Text(parent, SWT.BORDER);
			w5_Number = new Spinner(parent, 0);
			w5_Number.setEnabled(true);		
			w5_Number.setMinimum(0);
			w5_Number.setMaximum(1440);	
	        w5_Number.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER,true, true, 1, 1));				
			w52_Text = new Text(parent, SWT.BORDER);		
			//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
			GridData data5 = new GridData(GridData.FILL_HORIZONTAL);
			w5_Text.setLayoutData(data5);
			ControlDecoration deco5 = new ControlDecoration(w5_Text, SWT.RIGHT);
			deco5.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco5.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco5.setShowOnlyOnFocus(false);
			w52_Text.setLayoutData(data5);
			ControlDecoration deco52 = new ControlDecoration(w52_Text, SWT.RIGHT);
			deco52.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco52.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco52.setShowOnlyOnFocus(false);		
			// Help the user with the possible inputs

			try {
				//
				keyStroke = KeyStroke.getInstance("Ctrl+Space");
				// assume that myTextControl has already been created in some way
				ContentProposalAdapter adapter = new ContentProposalAdapter(w5_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos2),
						keyStroke, autoActivationCharacters);
				ContentProposalAdapter adapter2 = new ContentProposalAdapter(w52_Text,
						new TextContentAdapter(),
						new SimpleContentProposalProvider(nombres_validos),
						keyStroke, autoActivationCharacters);			
			} catch (ParseException e) {
				e.printStackTrace();
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

		if ( size == 5) {
			if ( w1_Text.getText().equals("") || w2_Text.getText().equals("") || w3_Text.getText().equals("") || w4_Text.getText().equals("") || w5_Text.getText().equals("")) {
				setErrorMessage("Faltan Cycle (5)");

				return false;	
			}
		}
		else if ( size == 4){ 
			if ( w1_Text.getText().equals("") || w2_Text.getText().equals("") || w3_Text.getText().equals("") || w4_Text.getText().equals("") ) {
				setErrorMessage("Faltan Cycle (4)");

				return false;	
			}        	
		}
		else if ( size == 3){
			if ( w1_Text.getText().equals("") || w2_Text.getText().equals("") || w3_Text.getText().equals("") ){
				setErrorMessage("Faltan Cycle (3)");

				return false;	
			}	        	
		}
		else if( size == 2) {
			if ( w1_Text.getText().equals("") || w2_Text.getText().equals("") ){
				setErrorMessage("Faltan Cycle (2)");

				return false;	
			}       	
		}
		else if( size == 1) {
			if ( w1_Text.getText().equals("")){
				setErrorMessage("Faltan Cycle (1)");

				return false;	
			}
		}
//Para el segundo campo		
		if ( size == 5) {
			if ( w12_Text.getText().equals("") || w22_Text.getText().equals("") || w32_Text.getText().equals("") || w42_Text.getText().equals("") || w52_Text.getText().equals("")) {
				setErrorMessage("Faltan ResourceType (5)");

				return false;	
			}
		}
		else if ( size == 4){ 
			if ( w12_Text.getText().equals("") || w22_Text.getText().equals("") || w32_Text.getText().equals("") || w42_Text.getText().equals("") ) {
				setErrorMessage("Faltan ResourceType (4)");

				return false;	
			}        	
		}
		else if ( size == 3){
			if ( w12_Text.getText().equals("") || w22_Text.getText().equals("") || w32_Text.getText().equals("") ){
				setErrorMessage("Faltan ResourceType (3)");

				return false;	
			}	        	
		}
		else if( size == 2) {
			if ( w12_Text.getText().equals("") || w22_Text.getText().equals("") ){
				setErrorMessage("Faltan ResourceType (2)");

				return false;	
			}       	
		}
		else if( size == 1) {
			if ( w12_Text.getText().equals("")){
				setErrorMessage("Faltan ResourceType (1)");

				return false;	
			}
		}		
		

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

		w_info.add(convertion2.get(w1_Text.getText()));
		w_info2.add(convertion.get(w12_Text.getText()));		
		w_info_n.add(new Integer(w1_Number.getSelection()));		
		if ( size > 1){
			w_info.add(convertion2.get(w2_Text.getText()));
			w_info2.add(convertion.get(w22_Text.getText()));				
			w_info_n.add(new Integer(w2_Number.getSelection()));
		}
		if ( size > 2) {
			w_info.add(convertion2.get(w3_Text.getText()));
			w_info2.add(convertion.get(w32_Text.getText()));				
			w_info_n.add(new Integer(w3_Number.getSelection()));
		}
		 if ( size > 3 ) {
				w_info.add(convertion2.get(w4_Text.getText()));
				w_info2.add(convertion.get(w42_Text.getText()));					
				w_info_n.add(new Integer(w4_Number.getSelection()));	
		 }
		if ( size > 4) {
			w_info.add(convertion2.get(w5_Text.getText()));
			w_info2.add(convertion.get(w52_Text.getText()));				
			w_info_n.add(new Integer(w5_Number.getSelection()));	
		}


	}
	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}
	public ArrayList<String> getW_Info() {
		return w_info;
	}
	
	public ArrayList<String> getW_Info2() {
		return w_info2;
	}
	
	public ArrayList<Integer> getW_Info_n() {
		return w_info_n;
	}	


}