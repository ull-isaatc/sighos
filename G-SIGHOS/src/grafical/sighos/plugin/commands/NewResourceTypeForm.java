package grafical.sighos.plugin.commands;


import grafical.sighos.plugin.tree.model.handler.RT_property;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
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
import org.eclipse.swt.widgets.Text;

public class NewResourceTypeForm extends TitleAreaDialog {
private Text varNameText;
private Text tNumberText;
private Text nameText;
private String varName;
private int tNumber;
private String name;


public NewResourceTypeForm(Shell parentShell) {
super(parentShell);
}
@Override
public void create() {
super.create();
// Set the title
setTitle("Creación de Nuevo Objeto");
// Set the message
setMessage("Nuevo Tipo de Recursos", IMessageProvider.INFORMATION);
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
label1.setText("Nombre del Tipo");
	
	
    nameText = new Text(parent, SWT.BORDER);
    nameText.setLayoutData(gridData);
	Label label2 = new Label(parent, SWT.NONE);
	label2.setText("Numero");
	
	Integer ttNumber = new Integer( RT_property.rt_List.size() );
	
	
	tNumberText = new Text(parent, SWT.BORDER);
	//tNumberText.setLayoutData(gridData);
	tNumberText.setText(ttNumber.toString());
	tNumberText.setEditable(false);
	
	Label label3 = new Label(parent, SWT.NONE);
	label3.setText("Nombre variable");
	varNameText = new Text(parent, SWT.BORDER);
	varNameText.setText("rt_"+ttNumber.toString());
	varNameText.setEditable(false);
	
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
	else {
	//   boolean repetido = false;
	
       int i =0;
	   while ( i < RT_property.rt_List.size() ) {
	   
	      if ( RT_property.rt_List.get(i).getName().equalsIgnoreCase(nameText.getText())  ) {
	    	  valid = false;
	          setErrorMessage("Nombre de 'Tipo de Recurso' Repetido");
	      }
		  i++;
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

	}
	@Override
	protected void okPressed() {
	saveInput();
	super.okPressed();
	}
	public String getVarName() {
	return varName;
	}

	public int getNumber() {
		return tNumber;
	}
	public String getName() {
		return name;
	}
	}