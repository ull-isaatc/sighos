package grafical.sighos.plugin.commands;


import java.util.ArrayList;
import java.util.HashMap;

import grafical.sighos.plugin.tree.model.handler.C_property;

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
import org.eclipse.swt.widgets.TableColumn;;

public class NewCycleForm extends TitleAreaDialog {
	private Text varNameText;
	private Text tNumberText;
	private Text nameText;
	private String varName;
	private int tNumber;
	private String name;

	private String tipo;
	private Spinner sp1;
	private Spinner sp2;
	private Spinner sp3;
	private Text w1_Text;
	
	private TableColumn tc;
	

	
	private String cycle;
	
	private ArrayList<Integer> vals = new ArrayList<Integer>();
	
	private HashMap<String,String> convertion = new HashMap<String,String>();


	public NewCycleForm(Shell parentShell, String tip) {
		super(parentShell);
		this.tipo=tip;

	}
	@Override
	public void create() {
		super.create();
		// Set the title
		setTitle("Creación de Nuevo Objeto");
		// Set the message
		setMessage("Nuevo Ciclo", IMessageProvider.INFORMATION);
	}
	@Override
	protected Control createDialogArea(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		// layout.horizontalAlignment = GridData.FILL;
		parent.setLayout(layout);
		// The text fields will grow with the size of the dialog
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		Label label1 = new Label(parent, SWT.NONE);
		label1.setText("Nombre del Ciclo");
		nameText = new Text(parent, SWT.BORDER);
		nameText.setLayoutData(gridData);

		Label label26 = new Label(parent, SWT.NONE);
		label26.setText("Ntablecol");
//		tc = new TableColumn(parent, 0);
		

		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Numero");

		Integer ttNumber = new Integer( C_property.c_List.size() );


		tNumberText = new Text(parent, SWT.BORDER);
		//tNumberText.setLayoutData(gridData);
		tNumberText.setText(ttNumber.toString());
		tNumberText.setEditable(false);

		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Nombre variable");
		varNameText = new Text(parent, SWT.BORDER);
		varNameText.setText("c_"+ttNumber.toString());
		varNameText.setEditable(false);

		Label tr = new Label(parent, SWT.NONE);
		tr.setText("");
		tr = new Label(parent, SWT.NONE);
		tr.setText("");	

		Label label31 = new Label(parent, SWT.NONE);
		label31.setText("Modo");
		Label label32 = new Label(parent, SWT.NONE);
		label32.setText(tipo);

		//Especifico
		Label label4 = new Label(parent, SWT.NONE);
		label4.setText("Instante de comienzo");

		sp1 = new Spinner(parent, 0);
		sp1.setMinimum(0);
		sp1.setEnabled(true);


		Label label5 = new Label(parent, SWT.NONE);
		label5.setText("Duracion del ciclo");
		sp2 = new Spinner(parent, 0);
		sp2.setMinimum(0);
		sp2.setEnabled(true);

		Label label6 = new Label(parent, SWT.NONE);
		label6.setText("Iteraciones");
		sp3 = new Spinner(parent, 0);
		sp3.setMinimum(0);
		sp3.setEnabled(true);

		if ( tipo.equals( "Compuesto")) {
			Label label7 = new Label(parent, SWT.NONE);
			label7.setText("Ciclo a seguir");

			String[] nombres_valido = new String[C_property.c_List.size()];
			int j = 0;
			for ( int i = 0; i < C_property.c_List.size(); i++) {
				if (!C_property.c_List.get(i).getName().equals("$$$$$trash$$$$$") ) {
					//	if (!RT_property.rt_List.get(i).equals("$$$$$trash$$$$$") ) {			
					convertion.put(C_property.c_List.get(i).getName(), C_property.c_List.get(i).getVarName());
					nombres_valido[j] = C_property.c_List.get(i).getName();
					j++;
				}
			}
			String[] nombres_validos = new String[j];		
			for ( int i = 0; i < j; i++) {			
				nombres_validos[i] = nombres_valido[i];
			}		


			w1_Text = new Text(parent, SWT.BORDER);
			//	createDeco(textCombo, "Use CNTL + SPACE to see possible values");
			GridData data = new GridData(GridData.FILL_HORIZONTAL);
			w1_Text.setLayoutData(data);
			ControlDecoration deco = new ControlDecoration(w1_Text, SWT.RIGHT);
			deco.setDescriptionText("Use CTRL + SPACE to see possible values");
			deco.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage());
			deco.setShowOnlyOnFocus(false);
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
		createOkButton(parent, OK, "FINISH", true);
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
		if (nameText.getText().length() == 0) {
			setErrorMessage("Nombre de 'Tipo de Recurso' no valido");
			valid = false;
		}

		int i =0;
		while ( i < C_property.c_List.size() ) {

			if ( C_property.c_List.get(i).getName().equalsIgnoreCase(nameText.getText())  ) {
				valid = false;
				setErrorMessage("Nombre de 'Tipo de Recurso' Repetido");
			}
			i++;
		}
		if ( tipo.equals("Compuesto") ) {
			if ( w1_Text.getText().length() == 0) {
				setErrorMessage("Necesario escoger un ciclo");
				valid = false;
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
		varName = varNameText.getText();
		tNumber = Integer.parseInt(tNumberText.getText());
		name = nameText.getText();
		vals.add( sp1.getSelection() );
		vals.add( sp2.getSelection() );
		vals.add( sp3.getSelection() );		
		
		if ( tipo.equals("Compuesto") )
		   cycle = convertion.get(w1_Text.getText());
		else
			cycle = "";
			

	}
	@Override
	protected void okPressed() {
		saveInput();
		super.okPressed();
	}
	public String getVarName() {
		return varName;
	}
	
	public ArrayList<Integer> getVals() {
		return vals;
	}
	
	public String getCycle() {
		return cycle;
	}

	public int getNumber() {
		return tNumber;
	}
	public String getName() {
		return name;
	}
}