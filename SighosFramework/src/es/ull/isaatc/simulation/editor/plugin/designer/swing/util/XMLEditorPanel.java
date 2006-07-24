/**
 * 
 */
package es.ull.isaatc.simulation.editor.plugin.designer.swing.util;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This code was edited or generated using CloudGarden's Jigloo
 * SWT/Swing GUI Builder, which is free for non-commercial
 * use. If Jigloo is being used commercially (ie, by a corporation,
 * company or business for any purpose whatever) then you
 * should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details.
 * Use of Jigloo implies acceptance of these licensing terms.
 * A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
 * THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
 * LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
/**
 * @author Roberto Muñoz
 * 
 */
public class XMLEditorPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JTextArea editor = new JTextArea();

	JTextArea errors = new JTextArea();

	DocumentListener editorListener = new DocumentListener() {
		public void insertUpdate(DocumentEvent e) {
			validateXML(e.getDocument());
		}

		public void removeUpdate(DocumentEvent e) {
			validateXML(e.getDocument());
		}

		public void changedUpdate(DocumentEvent e) {
			validateXML(e.getDocument());
		}
	};

	{
		editor.setFont(new java.awt.Font("Courier New", 0, 12));
		setLayout(new BorderLayout());
		errors.setLineWrap(true);
		errors.setEditable(false);
		errors.setForeground(Color.red);
		editor.getDocument().addDocumentListener(
				new DelayedDocumentListener(editorListener));
		JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				createScroll(editor), createScroll(errors));
		split.setResizeWeight(0.8d);
		add(split, BorderLayout.CENTER);
	}

	private void validateXML(Document doc) {
		try {
			errors.setText("");

			Reader reader = new StringReader(doc.getText(0, doc.getLength()));
			SAXParserFactory.newInstance().newSAXParser().parse(
					new InputSource(reader), new DefaultHandler() {
						public void warning(SAXParseException e) {
							errors.append("Warning [" + e.getLineNumber() + ':'
									+ e.getColumnNumber() + "]: "
									+ e.getMessage() + "\n");
						}

						public void error(SAXParseException e) {
							errors.append("Error [" + e.getLineNumber() + ':'
									+ e.getColumnNumber() + "]: "
									+ e.getMessage() + "\n");
						}

						public void fatalError(SAXParseException e)
								throws SAXException {
							errors.append("FatalError [" + e.getLineNumber()
									+ ':' + e.getColumnNumber() + "]: "
									+ e.getMessage() + "\n");
							super.fatalError(e);
						}
					});
		} catch (Exception ignore) {
		}
	}

	private JScrollPane createScroll(Component c) {
		JScrollPane scroll = new JScrollPane(c);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		return scroll;
	}
	
	public JTextArea getEditor() {
		return editor;
	}
	
	public String getEditorText() {
		return editor.getText();
	}
	
	public void setEditorText(String text) {
		editor.setText(text);
	}

	public void setErrorText(String text) {
		errors.setText(text);
	}

}
