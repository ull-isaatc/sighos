/**
 * 
 */
package es.ull.isaatc.swing;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * @author Roberto
 *
 */
public class TransparentTextFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	
	private JTextArea textArea = getTextArea();

	public TransparentTextFrame() {
		setUndecorated(true);
		setLayout(new FlowLayout());
		add(getTextArea());
	}
	
	private JTextArea getTextArea() {
		textArea = new JTextArea();
		textArea.setOpaque(false);
		textArea.setBackground(new Color(255, 255, 255, 100));
		return textArea;
	}
	
	public void setText(String text) {
		textArea.setText(text);
	}
}
