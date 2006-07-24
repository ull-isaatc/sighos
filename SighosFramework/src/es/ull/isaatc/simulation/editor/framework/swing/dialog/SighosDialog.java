package es.ull.isaatc.simulation.editor.framework.swing.dialog;

import info.clearthought.layout.TableLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.swing.Separator;

public abstract class SighosDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JPanel buttonPanel;

	private JPanel topPanel;

	private JLabel dialogTitle;

	private JLabel componentLabel;

	private JButton okButton = null;

	private JButton cancelButton = null;

	public SighosDialog() {
		super();
		initialize();
	}

	protected void initialize() {
		this.setContentPane(createContentPane());
	}

	private JPanel createContentPane() {
		JPanel panel = new JPanel();
		TableLayout panelLayout = new TableLayout(new double[][] {
				{ 5.0, TableLayout.FILL, 5.0 },
				{ 55.0, 5.0, TableLayout.FILL, 5.0, 25.0, 5.0 } });
		panelLayout.setHGap(0);
		panelLayout.setVGap(0);
		panel.setLayout(panelLayout);
		panel.add(getTopPanel(), "0, 0, 2, 0");
		Separator sep = new Separator();
		sep.setThickness(2);
		panel.add(new Separator(), "0, 1, 2, 1, f, t");
		// panel.add(new Separator(), "0, 3, 2, 3, f, b");
		panel.add(getButtonPanel(), "1, 4");
		panel.add(getMainPanel(), "1, 2");

		return panel;
	}

	private JPanel getTopPanel() {
		if (topPanel == null) {
			topPanel = new JPanel();
			TableLayout topPanelLayout = new TableLayout(new double[][] {
					{ 5.0, 15.0, TableLayout.FILL },
					{ 20.0, TableLayout.FILL, 5.0 } });
			topPanelLayout.setHGap(5);
			topPanelLayout.setVGap(5);
			topPanel.setLayout(topPanelLayout);
			topPanel.setBackground(new java.awt.Color(255, 255, 255));
			topPanel.add(getDialogTitle(), "1, 0, 2, 0");
			topPanel.add(getComponentLabel(), "2, 1");
		}
		return topPanel;
	}

	private JLabel getDialogTitle() {
		if (dialogTitle == null) {
			dialogTitle = new JLabel();
			dialogTitle.setFont(new java.awt.Font("Tahoma", 1, 16));
		}
		return dialogTitle;
	}

	private JLabel getComponentLabel() {
		if (componentLabel == null) {
			componentLabel = new JLabel();
		}
		return componentLabel;
	}

	private JPanel getButtonPanel() {
		if (buttonPanel == null) {
			buttonPanel = new JPanel();
			TableLayout jPanel1Layout = new TableLayout(new double[][] {
					{ TableLayout.FILL, TableLayout.FILL },
					{ TableLayout.FILL } });
			jPanel1Layout.setHGap(5);
			jPanel1Layout.setVGap(0);
			buttonPanel.setLayout(jPanel1Layout);
			buttonPanel.add(getOkButton(), "0, 0, r, f");
			buttonPanel.add(getCancelButton(), "1, 0, l, f");
		}
		return buttonPanel;
	}

	/**
	 * This method initializes okButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getOkButton() {
		if (okButton == null) {
			okButton = new JButton(ResourceLoader.getMessage("ok"));
			okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					okButtonAction();
				}
			});
		}
		return okButton;
	}

	/**
	 * This method initializes cancelButton
	 * 
	 * @return javax.swing.JButton
	 */
	private JButton getCancelButton() {
		if (cancelButton == null) {
			cancelButton = new JButton(ResourceLoader.getMessage("cancel"));
			cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					cancelButtonAction();
				}
			});
		}
		return cancelButton;
	}

	public void setTitle(String title) {
		super.setTitle(title);
		dialogTitle.setText(title);
	}

	public JLabel getComponentLabe() {
		return componentLabel;
	}
	
	public void setComponentLabel(String text) {
		componentLabel.setText(text);
	}

	protected abstract void okButtonAction();

	protected abstract JPanel getMainPanel();

	protected void cancelButtonAction() {
		setVisible(false);
	}
}
