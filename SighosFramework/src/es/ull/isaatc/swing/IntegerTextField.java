/**
 * 
 */
package es.ull.isaatc.swing;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

/**
 * @author Roberto Muñoz
 * 
 */
public class IntegerTextField extends JTextField {

	private static final long serialVersionUID = 1L;

	/**
	 * 
	 */
	public IntegerTextField() {
		this("0");
	}

	/**
	 * @param text
	 */
	public IntegerTextField(String text) {
		super(text);
		
		addKeyListener(new KeyAdapter() {
			public void keyTyped(KeyEvent e) {
				char c = e.getKeyChar();
				if (!((Character.isDigit(c) ||
						(c == KeyEvent.VK_BACK_SPACE) ||
						(c == KeyEvent.VK_DELETE)))) {
					getToolkit().beep();
					e.consume();
				}
			}
		});

	}
	
	public int getValue() {
		return Integer.parseInt(getText());
	}
}
