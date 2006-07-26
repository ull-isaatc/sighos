package es.ull.isaatc.simulation.editor.framework.swing.dialog;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;

import info.clearthought.layout.TableLayout;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;

import es.ull.isaatc.simulation.editor.util.ResourceLoader;
import es.ull.isaatc.swing.Separator;

public abstract class SighosDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	protected Component parent;

	private JPanel buttonPanel;

	private JPanel topPanel;

	private JLabel dialogTitle;

	private JLabel componentLabel;

	private Action okayAction = new OkayAction();

	private Action cancelAction = new CancelAction();

	private JButton okButton = null;

	private JButton cancelButton = null;

	protected boolean isOkay = false;

	public SighosDialog(Component parent) {
		super();
		this.parent = parent;
		initialize();
		// Locate the dialog in the center of the its parent container
		if (parent != null) {
			Rectangle parentBounds = parent.getBounds();
			Rectangle bounds = getBounds();
			Point p = new Point();
			p.setLocation(parentBounds.getCenterX() - bounds.getWidth() / 2,
					parentBounds.getCenterY() - bounds.getHeight() / 2);
			this.setLocation(p);
		}
	}

	protected void initialize() {
		this.setContentPane(createContentPane());
		addWindowListener(new CloseListener());
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
			okButton = new JButton(okayAction);
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
			cancelButton = new JButton(cancelAction);
		}
		return cancelButton;
	}

	/**
	 * @return the cancelAction
	 */
	public Action getCancelAction() {
		return cancelAction;
	}

	/**
	 * @return the okayAction
	 */
	public Action getOkayAction() {
		return okayAction;
	}

	/**
	 * @return the isOkay
	 */
	public boolean isOkay() {
		return isOkay;
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

	protected void close() {
		setVisible(false);
	}

	protected abstract boolean apply();

	protected abstract JPanel getMainPanel();

	protected void cancelled() { }

	/**
	 * Handle the Okay button. Invokes <code>apply()</code> and
	 * <code>close()</code> when pressed.
	 * 
	 * @see #apply()
	 * @see #close()
	 */
	private class OkayAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public OkayAction() {
			putValue(Action.NAME, ResourceLoader.getMessage("ok"));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_O));
		}

		public void actionPerformed(ActionEvent event) {
			if (!apply()) {
				return;
			}
			isOkay = true;
			close();
		}

	}

	/**
	 * Handles the Cancel button. Invokes <code>close()</code> when pressed.
	 * 
	 * @see #close()
	 */
	private class CancelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		public CancelAction() {
			putValue(Action.NAME, ResourceLoader.getMessage("cancel"));
			putValue(Action.MNEMONIC_KEY, new Integer(KeyEvent.VK_C));
		}

		public void actionPerformed(ActionEvent event) {
			isOkay = false;
			cancelled();
			close();
		}

	}

	private class CloseListener extends WindowAdapter {

		public void windowClosing(java.awt.event.WindowEvent evt) {
			isOkay = false;
			cancelled();
			close();
		}
	}
}
