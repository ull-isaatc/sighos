package es.ull.isaatc.simulation.editor.framework.swing.dialog;

import info.clearthought.layout.TableLayout;

import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import es.ull.isaatc.simulation.editor.framework.actions.swing.SighosDialogComponentAction;
import es.ull.isaatc.simulation.editor.project.ProjectModel;
import es.ull.isaatc.simulation.editor.util.ResourceLoader;

import java.awt.Dimension;
import java.io.File;

import javax.swing.JButton;
import javax.swing.BorderFactory;

import org.xnap.commons.gui.DirectoryChooser;

public class NewProjectDialog extends SighosDialog {

	private static final long serialVersionUID = 1L;

	private JPanel mainPanel;

	private JTextField name;

	private JTextField author;

	private JTextField location;

	private JButton browseButton = null;

	private JPanel propPanel;

	protected JTextPane description;

	public NewProjectDialog() {
		super();
	}

	@Override
	protected void initialize() {
		setSize(new Dimension(500, 400));
		setModal(true);
		super.initialize();
		setTitle(ResourceLoader.getMessage("create_dialog"));
		setComponentLabel("Describir las propiedades del proyecto");
	}

	@Override
	protected JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel();
			TableLayout panelLayout = new TableLayout(new double[][] {
					{ TableLayout.FILL }, { 75.0, 25.0, TableLayout.FILL } });
			panelLayout.setHGap(5);
			panelLayout.setVGap(5);
			mainPanel.setLayout(panelLayout);
			mainPanel.add(getPropertiesPanel(), "0, 0");
			mainPanel.add(new JLabel(ResourceLoader
					.getMessage("project_description")), "0, 1");
			mainPanel.add(getDescriptionPanel(), "0, 2");
		}
		return mainPanel;
	}

	private JPanel getPropertiesPanel() {
		if (propPanel == null) {
			propPanel = new JPanel();
			TableLayout jPanel1Layout = new TableLayout(new double[][] {
					{ 0.3, TableLayout.FILL, 90 },
					{ TableLayout.FILL, TableLayout.FILL, TableLayout.FILL } });
			jPanel1Layout.setVGap(5);
			propPanel.setLayout(jPanel1Layout);
			propPanel.add(
					new JLabel(ResourceLoader.getMessage("project_name")),
					"0, 0");
			propPanel.add(new JLabel(ResourceLoader
					.getMessage("project_author")), "0, 1");
			propPanel.add(new JLabel(ResourceLoader
					.getMessage("project_location")), "0, 2");
			// TODO : añadir listeners para comprobar que los datos son
			// correctos
			name = new JTextField(ProjectModel.getInstance().getName());
			name.addFocusListener(new SighosDialogComponentAction(this, name) {
				{
					putValue(Action.LONG_DESCRIPTION, ResourceLoader
							.getMessage("project_name_long"));
				}
			});
			name.setBorder(BorderFactory.createEtchedBorder());

			author = new JTextField(ProjectModel.getInstance().getAuthor());
			author.setBorder(BorderFactory.createEtchedBorder());
			location = new JTextField(ProjectModel.getInstance().getDirectory());
			location.addFocusListener(new SighosDialogComponentAction(this,
					location) {
				{
					putValue(Action.LONG_DESCRIPTION, ResourceLoader
							.getMessage("project_location_long"));
				}
			});
			location.setBorder(BorderFactory.createEtchedBorder());
			browseButton = new JButton(ResourceLoader
					.getMessage("project_browse"));
			browseButton.addFocusListener(new SighosDialogComponentAction(this,
					location) {
				{
					putValue(Action.LONG_DESCRIPTION, ResourceLoader
							.getMessage("project_location_long"));
				}
			});
			browseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					DirectoryChooser dirChooser = new DirectoryChooser();
					dirChooser
							.setSelectedDirectory(new File(location.getText()));
					if (DirectoryChooser.APPROVE_OPTION == dirChooser
							.showChooseDialog(null))
						location.setText(dirChooser.getSelectedDirectory()
								.getAbsolutePath());
				}
			});
			propPanel.add(name, "1, 0, 2, 0");
			propPanel.add(author, "1, 1, 2, 1");
			propPanel.add(location, "1, 2");
			propPanel.add(browseButton, "2, 2");
		}
		return propPanel;
	}

	protected JTextPane getDescriptionPanel() {
		if (description == null) {
			description = new JTextPane();
			description.addFocusListener(new SighosDialogComponentAction(this,
					description) {
				{
					putValue(Action.LONG_DESCRIPTION, ResourceLoader
							.getMessage("project_description_long"));
				}
			});

			description.setBorder(BorderFactory.createEtchedBorder());
			description.setText(ProjectModel.getInstance().getDescription());
		}
		return description;
	}

	@Override
	protected void okButtonAction() {
		ProjectModel.getInstance().setName(name.getText());
		ProjectModel.getInstance().setAuthor(author.getText());
		ProjectModel.getInstance().setDescription(description.getText());
		ProjectModel.getInstance().setDirectory(
				location.getText() + "/" + name.getText());
		ProjectModel.getInstance().getModel().setFileName(
				location.getText() + "/" + name.getText() + "/model.xml");
		setVisible(false);
	}
}
