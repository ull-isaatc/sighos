/**
 * 
 */
package es.ull.isaatc.simulation.editor.framework.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import es.ull.isaatc.simulation.editor.framework.swing.dialog.NewProjectDialog;
import es.ull.isaatc.simulation.editor.project.ProjectModel;

/**
 * This class manages the model
 * 
 * @author Roberto Muñoz
 */
public class SighosFrameworkDesktop extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private static JComponent currentDesktop = null;
	
	private static JSplitPane splitPane = null;

	private static final SighosFrameworkDesktop INSTANCE = new SighosFrameworkDesktop();

	public static SighosFrameworkDesktop getInstance() {
		return INSTANCE;
	}

	private SighosFrameworkDesktop() {
		super();
		initialize();
	}

	private void initialize() {
		setBackground(Color.WHITE);
		setLayout(new BorderLayout());
//		add(getToolBarPanel(), BorderLayout.NORTH);
		add(getSplitPane(), BorderLayout.CENTER);
	}
	
	private JSplitPane getSplitPane() {
		if (splitPane == null) {
			splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			splitPane.setTopComponent(currentDesktop);
			splitPane.setBottomComponent(SighosTabbedPane.getInstance());
		    splitPane.setDividerSize(10);
		    splitPane.setResizeWeight(0.7);
		    splitPane.setOneTouchExpandable(true);
		    splitPane.setVisible(false);
		}
		return splitPane;
	}
	
	public void newProject() {
		ProjectModel.getInstance().reset();
		NewProjectDialog npDialog = new NewProjectDialog();
		npDialog.setVisible(true);
		if (!ProjectModel.getInstance().getName().equals("")) {
			boolean success = (new File(ProjectModel.getInstance().getDirectory() + "\\" + ProjectModel.getInstance().getName())).mkdirs();
		    if (!success) {
		        System.err.println("Error : directory creation failed");
		    }
		}
	}
	
	public void setDesktop(JComponent desktop) {
		currentDesktop = desktop;
		splitPane.setTopComponent(currentDesktop);
		splitPane.setVisible(true);
		validate();
	}
}
