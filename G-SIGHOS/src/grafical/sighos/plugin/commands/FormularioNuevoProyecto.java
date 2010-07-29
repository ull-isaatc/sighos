package grafical.sighos.plugin.commands;



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
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class FormularioNuevoProyecto extends TitleAreaDialog {
private Text projectNameText;
private Text autorText;
private Text descripcionText;
private String projectName;
private String autor;
private String descripcion;
private Integer experim;

private Spinner valor;


public FormularioNuevoProyecto(Shell parentShell) {
super(parentShell);
}
@Override
public void create() {
super.create();
// Set the title
setTitle("Nuevo Proyecto de Simulacion");
// Set the message
setMessage("Opciones de simulacion", IMessageProvider.INFORMATION);
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
label1.setText("Nombre del proyecto");
	
	
projectNameText = new Text(parent, SWT.BORDER);
projectNameText.setLayoutData(gridData);
	Label label2 = new Label(parent, SWT.NONE);
	label2.setText("Descripcion");
	descripcionText = new Text(parent, SWT.BORDER);
	descripcionText.setLayoutData(gridData);
	
	Label label3 = new Label(parent, SWT.NONE);
	label3.setText("Nombre autor");
	autorText = new Text(parent, SWT.BORDER);
	autorText.setLayoutData(gridData);
	
	Label label4 = new Label(parent, SWT.NONE);
	label4.setText("Numero de repeticiones");
	valor = new Spinner(parent, 0);
	valor.setMinimum(1);
	valor.setMaximum(200);

	
	
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
	createOkButton(parent, OK, "SIGUIENTE", true);
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
	if (projectNameText.getText().length() == 0) {
	setErrorMessage("Nombre proyecto no valido");
	valid = false;
	}

	if (autorText.getText().length() == 0) {
		setErrorMessage("Nombre de autor no valido");
		valid = false;
	}
	
	/*if (descripcionText.getText().length() == 0) {
		setErrorMessage("Descripcion no valida");
		valid = false;
	}*/
	
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
	projectName = projectNameText.getText();
	autor = autorText.getText();
	descripcion = descripcionText.getText();
	experim = valor.getSelection();

	}
	@Override
	protected void okPressed() {
	saveInput();
	super.okPressed();
	}
	public String getProjectName() {
	return projectName;
	}

	public String getAutor() {
		return autor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public Integer getLoop_exp() {
		return experim;
	}	
	}

