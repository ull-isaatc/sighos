package grafical.sighos.plugin.view;


import grafical.sighos.plugin.Application;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.part.ViewPart;

public class comando extends ViewPart {

	
	private static Label text2;
	private static Label text3;
	private static Label text4;
	private static Label text5;	
	private static Spinner num8;
	
	@Override
	public void createPartControl(Composite parent) {


		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		parent.setLayout(layout);
	
		Label label1 = new Label(parent, SWT.BORDER);
		label1.setText("Proyect Name: ");
		text2 = new Label(parent, SWT.BORDER);
		text2.setText("                            ");
		
		Label label5 = new Label(parent, SWT.BORDER);
		label5.setText("Time Unit: ");
		text5 = new Label(parent, SWT.BORDER);
		text5.setText("                  ");			
		
		
		Label label3 = new Label(parent, SWT.BORDER);
		label3.setText("Author: ");
		text3 = new Label(parent, SWT.BORDER);	
		text3.setText("                            ");	
		
		Label label8 = new Label(parent, SWT.BORDER);
		label8.setText("Repeticiones: ");
		num8 = new Spinner(parent, 0);	
		num8.setSelection(1);			
		num8.setEnabled(false);
	
		Label label4 = new Label(parent, SWT.BORDER);
		label4.setText("Descripcion: ");
		text4 = new Label(parent, SWT.BORDER);
		text4.setText("                                                                    " +
				"                                                        ");		

	}

	@Override
	public void setFocus() {
	}
	
	public static void refresh( ) {
		
		text2.setText(Application.name_proyect);
		text5.setText(Application.time_proyect);	
		text3.setText(Application.author_proyect);
		text4.setText(Application.descripcion_proyect);	
		num8.setSelection(Application.loop_exp);
		
	}
	
}
