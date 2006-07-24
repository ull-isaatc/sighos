package es.ull.isaatc.simulation.editor.plugin.designer.swing.dialog;

import java.awt.Component;
import java.awt.Font;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

import java.awt.Dimension;

import javax.swing.JTabbedPane;
import javax.swing.border.TitledBorder;

import es.ull.isaatc.simulation.editor.util.ModelComponent;

/**
 * 
 * @author Roberto Muñoz
 * 
 */
public abstract class PropertyDialog extends JDialog {

	protected ModelComponent editObject = null;

	protected JPanel contentPane = null;

	protected JTabbedPane tabbedPane = null;

	protected JButton OkButton = null;

	protected JButton CancelButton = null;

	protected JPanel generalPanel = null;

	protected JTextPane commentTextArea = null;

	protected abstract void getExtendedTabbedPane();

	protected abstract Component getDescField();

	protected abstract void okButtonAction();

	protected abstract void cancelButtonAction();

	/** Creates the reusable dialog. */
	public PropertyDialog(ModelComponent editObject) {
		super();

		this.editObject = editObject;
		initialize();
	}

	/**
	 * This method initializes this dialog.
	 * 
	 */
	protected void initialize() {
		this.setSize(new Dimension(400, 350));
		// this.setBounds(parent.getX() + (parent.getWidth() / 2 - 200) ,
		// parent.getY() + (parent.getHeight() / 2 - 175), 400, 350);
		this.setContentPane(getMainPane());
		this.setResizable(false);
		this.setModal(true);
		getRootPane().setDefaultButton(OkButton);
	}
	
	/**
	 * This method initializes contentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getMainPane() {
		if (contentPane == null) {
			contentPane = new JPanel();
			contentPane.setLayout(null);
			contentPane.add(getTabbedPane(), null);
			contentPane.add(getOkButton(), null);
			contentPane.add(getCancelButton(), null);
		}
		return contentPane;
	}

	/**
	 * This method initializes tabbedPane
	 * 
	 * @return javax.swing.JTabbedPane
	 */
	protected JTabbedPane getTabbedPane() {
		if (tabbedPane == null) {
			tabbedPane = new JTabbedPane();
			tabbedPane.addTab("General", getJGeneralPanel());
			getExtendedTabbedPane();
			tabbedPane.setBounds(new Rectangle(10, 15, 370, 260));
		}
		return tabbedPane;
	}

	/**
	 * This method initializes OkButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getOkButton() {
		if (OkButton == null) {
			OkButton = new JButton();
			OkButton.setText("Ok");
			OkButton.setBounds(new Rectangle(100, 285, 90, 20));
			OkButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					okButtonAction();
				}
			});
		}
		return OkButton;
	}

	/**
	 * This method initializes CancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	protected JButton getCancelButton() {
		if (CancelButton == null) {
			CancelButton = new JButton();
			CancelButton.setText("Cancel");
			CancelButton.setBounds(new Rectangle(210, 285, 90, 20));
			CancelButton
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							cancelButtonAction();
						}
					});
		}
		return CancelButton;
	}

	/**
	 * This method initializes generalPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	protected JPanel getJGeneralPanel() {
		if (generalPanel == null) {
			generalPanel = new JPanel();
			generalPanel.setLayout(null);
			JLabel jDescLabel = new JLabel("Description:");
			jDescLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jDescLabel.setBounds(new Rectangle(10, 10, 100, 20));
			generalPanel.add(jDescLabel);
			JLabel jDocLabel = new JLabel("Documentation:");
			jDocLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
			jDocLabel.setBounds(new Rectangle(10, 40, 100, 20));
			generalPanel.add(jDocLabel);
			generalPanel.add(getJDocTextArea());
			generalPanel.add(getDescField());
		}
		return generalPanel;
	}

	/**
	 * This method initializes commentTextArea
	 * 
	 * @return javax.swing.JTextPane
	 */
	protected JTextPane getJDocTextArea() {
		if (commentTextArea == null) {
			commentTextArea = new JTextPane();
			commentTextArea.setBounds(new Rectangle(10, 60, 345, 160));
			TitledBorder border = BorderFactory
					.createTitledBorder(BorderFactory.createEtchedBorder());
			commentTextArea.setBorder(border);
		}
		return commentTextArea;
	}

	public abstract ModelComponent getModelComponent();
}
