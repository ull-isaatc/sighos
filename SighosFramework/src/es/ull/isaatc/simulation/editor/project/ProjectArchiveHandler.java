package es.ull.isaatc.simulation.editor.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;
import es.ull.isaatc.simulation.editor.framework.swing.DirectoryChooserFactory;
import es.ull.isaatc.simulation.editor.framework.swing.FileChooserFactory;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.NewProjectDialog;
import es.ull.isaatc.simulation.editor.framework.swing.dialog.SighosDirectoryChooser;
import es.ull.isaatc.simulation.editor.project.model.Model;
import es.ull.isaatc.simulation.editor.project.model.XMLModelUtilities;
import es.ull.isaatc.simulation.editor.project.xml.Project;

public class ProjectArchiveHandler {
	private static final String PROJECT_FILE_TYPE = "xml";

	private static final String DESCRIPTION = "Sighos Project";

	private static final JFileChooser SAVE_FILE_CHOOSER = FileChooserFactory
			.buildFileChooser(PROJECT_FILE_TYPE, DESCRIPTION,
					"Save project to ", " file",
					FileChooserFactory.SAVING_AND_LOADING);

	private static final SighosDirectoryChooser OPEN_PROJECT_CHOOSER = DirectoryChooserFactory.OPEN_PROJECT_CHOOSER;
	
	private static final NewProjectDialog NEW_PROJECT_DIALOG = new NewProjectDialog(SighosFramework.getInstance());

	private transient static final ProjectArchiveHandler INSTANCE = new ProjectArchiveHandler();

	public static ProjectArchiveHandler getInstance() {
		return INSTANCE;
	}

	private ProjectArchiveHandler() {
	}

	public void newProject() {
		ProjectModel.getInstance().reset();
		NEW_PROJECT_DIALOG.setVisible(true);
		if (NEW_PROJECT_DIALOG.isOkay()) {
			//!ProjectModel.getInstance().getName().equals("")) {
			boolean success = (new File(ProjectModel.getInstance()
					.getDirectory()
					+ "\\" + ProjectModel.getInstance().getName())).mkdirs();
			if (!success) {
				System.err.println("Error : directory creation failed");
			}
			ProjectFileModel.getInstance().incrementFileCount();
		}
	}

	/**
	 * Opens a project
	 */
	public void open() {
		open(null);
	}

	/**
	 * Opens a project
	 * 
	 * @param folderFileName
	 */
	public void open(String folderFileName) {
		File folderFile;

		if (folderFileName == null) { // prompt user for the file
			OPEN_PROJECT_CHOOSER.setVisible(true);
			if (!OPEN_PROJECT_CHOOSER.isOkay())
				return;
			folderFile = OPEN_PROJECT_CHOOSER.getSelectedDirectory();
		} else {
			folderFile = new File(folderFileName);
			if (!folderFile.exists()) { // create a project with this name
				ProjectModel.getInstance().setDirectory(folderFileName);
				ProjectFileModel.getInstance().incrementFileCount();
				return;
			} else if (!folderFile.canRead()) { // file exists, but can't be
				// read
				return;
			}
		}
		openProjectFromFile(folderFile);
	}

	/**
	 * Open a project
	 * 
	 * @param file
	 *            project file
	 */
	public void openProjectFromFile(File file) {

		if (file == null) {
			return;
		}

		try {
			ProjectModel.getInstance().reset();
			readProject(file);
			ProjectFileModel.getInstance().incrementFileCount();
			SighosFramework.getInstance().setSubTitle(
					ProjectModel.getInstance().getName());
		} catch (Exception e) {
			JOptionPane
					.showMessageDialog(
							SighosFramework.getInstance(),
							"Error discovered reading Sighos Framework Projectfile.\nDiscarding this load file.\n",
							"Project File Loading Error",
							JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			close();
		}
	}

	public void save() {
		if (ProjectModel.getInstance().getDirectory().equals("")) {
			promptForAndSetSaveFileName();
		}
		saveProjectToDisk();
	}

	private void promptForAndSetSaveFileName() {

		if (JFileChooser.CANCEL_OPTION == SAVE_FILE_CHOOSER
				.showSaveDialog(SighosFramework.getInstance())) {
			return;
		}

		File file = SAVE_FILE_CHOOSER.getSelectedFile();

		if (file.exists()
		// && !getFullNameFromFile(file).equals(
		// ProjectModel.getInstance().getFileName())
		) {
			if (JOptionPane.NO_OPTION == JOptionPane
					.showConfirmDialog(
							SighosFramework.getInstance(),
							"You have chosen an existing project file.\n"
									+ "If you save to this file, you will overwrite the file's contents.\n\n"
									+ "Are you absolutely certain you want to save your project to this file?\n",
							"Existing Project File Selected",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE)) {
				return;
			}
		}
		// ProjectModel.getInstance().setFileName(getFullNameFromFile(file));
	}

	public void saveAs() {
		promptForAndSetSaveFileName();
		saveProjectToDisk();
	}

	private void saveProjectToDisk() {
		String fullFileName = ProjectModel.getInstance().getDirectory()
				+ "\\project.xml";

		if (ProjectModel.getInstance().getName().equals("")) {
			return;
		}

		try {
			JAXBContext jc = JAXBContext
					.newInstance("es.ull.isaatc.simulation.editor.project.xml");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(ProjectModel.getInstance().getXML(),
					new FileOutputStream(fullFileName));
			saveModel(new File(ProjectModel.getInstance().getModel()
					.getFileName()));
		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}

	public void close() {
		if (ProjectFileModel.getInstance().getFileCount() == 0) {
			return;
		}
		int response = getSaveOnCloseResponse();
		if (response == JOptionPane.CANCEL_OPTION) {
			return;
		}
		if (response == JOptionPane.YES_OPTION) {
			saveWhilstClosing();
		} else {
			closeWithoutSaving();
		}
	}

	private int getSaveOnCloseResponse() {
		return JOptionPane
				.showConfirmDialog(
						SighosFramework.getInstance(),
						"You have chosen to close this project.\n"
								+ "Do you wish to save your changes before closing?\n\n"
								+ "Choose 'yes' to save the project as-is, 'no' to lose all unsaved changes.",
						"Save changes before closing?",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE);
	}

	private void doPreSaveClosingWork() {
		ProjectFileModel.getInstance().decrementFileCount();
	}

	private void doPostSaveClosingWork() {
		// TODO : resetear los plugins
		ProjectModel.getInstance().reset();
		SighosFramework.getInstance().reset();
	}

	private void saveWhilstClosing() {
		doPreSaveClosingWork();
		save();
		doPostSaveClosingWork();
	}

	private void closeWithoutSaving() {
		doPreSaveClosingWork();
		doPostSaveClosingWork();
	}

	public void exit() {
		int response = getSaveOnCloseResponse();
		if (response == JOptionPane.CANCEL_OPTION) {
			return;
		}

		SighosFramework.getInstance().setVisible(false);

		if (response == JOptionPane.YES_OPTION) {
			saveWhilstClosing();
		} else {
			closeWithoutSaving();
		}

		System.exit(0);
	}

	private void readProject(File file) {
		Project project = null;
		File projectFile = new File(file.getAbsolutePath() + "/project."
				+ PROJECT_FILE_TYPE);
		String fullFileName = projectFile.getAbsolutePath();

		try {
			JAXBContext jc = JAXBContext
					.newInstance("es.ull.isaatc.simulation.editor.project.xml");
			Unmarshaller u = jc.createUnmarshaller();
			project = (Project) u.unmarshal(new FileInputStream(fullFileName));
		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}

		if (project == null)
			return;

		readModel(new File(file.getAbsolutePath() + "/model.xml"));

		ProjectModel.getInstance().setName(project.getName());
		ProjectModel.getInstance().setDescription(project.getDescription());
		ProjectModel.getInstance().setAuthor(project.getAuthor());
		ProjectModel.getInstance().setDirectory(file.getAbsolutePath());
	}

	private void readModel(File modelFile) {
		es.ull.isaatc.simulation.xml.Model xmlModel = null;
		String fullFileName = modelFile.getAbsolutePath();

		try {
			JAXBContext jc = JAXBContext
					.newInstance("es.ull.isaatc.simulation.xml");
			Unmarshaller u = jc.createUnmarshaller();
			xmlModel = (es.ull.isaatc.simulation.xml.Model) u
					.unmarshal(new FileInputStream(fullFileName));

		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}

		if (xmlModel != null) {
			Model model = ProjectModel.getInstance().getModel();
			model.setResourceTypeTableModel(XMLModelUtilities
					.getResourceTypeTableModel(xmlModel.getResourceType()));
			model.setResourceTableModel(XMLModelUtilities
					.getResourceTableModel(xmlModel.getResource()));
			model.setActivityTableModel(XMLModelUtilities
					.getActivityTableModel(xmlModel.getActivity()));
			model.setElementTypeTableModel(XMLModelUtilities
					.getElementTypeTableModel(xmlModel.getElementType()));
			model.setRootFlowTableModel(XMLModelUtilities
					.getRootFlowTableModel(xmlModel.getRootFlow()));
			model.setFileName(fullFileName);
		}
	}

	private void saveModel(File modelFile) {
		es.ull.isaatc.simulation.xml.Model xmlModel = ProjectModel
				.getInstance().getModel().getXML();

		try {
			JAXBContext jc = JAXBContext
					.newInstance("es.ull.isaatc.simulation.xml");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(xmlModel, new FileOutputStream(modelFile));
		} catch (JAXBException je) {
			je.printStackTrace();
			System.exit(-1);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(-1);
		}
	}
}
