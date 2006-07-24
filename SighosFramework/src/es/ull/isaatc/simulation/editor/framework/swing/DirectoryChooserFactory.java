package es.ull.isaatc.simulation.editor.framework.swing;

import java.io.File;

import javax.swing.event.TreeSelectionEvent;

import org.xnap.commons.gui.DirectoryChooser;

public class DirectoryChooserFactory {

	public static DirectoryChooser OPEN_PROJECT_CHOOSER = getOpenProjectDirectoryChooser();
	
	private static DirectoryChooser getOpenProjectDirectoryChooser() {
		DirectoryChooser dirChooser = new DirectoryChooser() {
			private static final long serialVersionUID = 1L;

			public void valueChanged(TreeSelectionEvent e) {
				File projectFile = new File(getSelectedDirectory().getAbsolutePath() + "/project.xml");
				if (projectFile.exists())
					getOkayAction().setEnabled(true);
				else
					getOkayAction().setEnabled(false);
			}
		};
//		dirChooser.setTitle(ResourceLoader.getMessage("open_long"));

		dirChooser.setSelectedDirectory(new File(System.getProperty("user.dir")));
		return dirChooser;
	}
}
