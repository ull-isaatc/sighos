/**
 * 
 */
package es.ull.isaatc.swing;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextField;

/**
 * @author Roberto Muñoz
 * 
 */
public class FloatTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public FloatTextField() {
		this("0");
	}

	/**
	 * @param text
	 */
	public FloatTextField(String text) {
		super(text);

		  addFocusListener(new FocusAdapter() {
			    public void focusLost(FocusEvent e) {
			      JTextField textField = (JTextField)e.getSource();
			      String content = textField.getText();
			      if (content.length() != 0) {
			        try {
			          Double.parseDouble(content);
			        } catch (NumberFormatException nfe) {
			          getToolkit().beep();
			          textField.requestFocus();
			        }
			      }
			      else
			    	  textField.setText("0");
			    }
			  });
	}
	
	public double getValue() {
		return Double.parseDouble(getText());
	}
}
