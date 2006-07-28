package es.ull.isaatc.simulation.editor.project;

import java.util.List;

import es.ull.isaatc.simulation.editor.framework.swing.table.ProblemTableItem;
import es.ull.isaatc.simulation.editor.project.model.Model;
import es.ull.isaatc.simulation.editor.util.Validatory;

public class ProjectModel implements Validatory {

	private Model model;

	private String name = "";

	private String description = "No description has been given.";

	private String author = "";

	private static final es.ull.isaatc.simulation.xml.ObjectFactory xmlModelFactory = new es.ull.isaatc.simulation.xml.ObjectFactory();

	private transient static final ProjectModel INSTANCE = new ProjectModel();

	public static ProjectModel getInstance() {
		return INSTANCE;
	}

	public ProjectModel() {
		model = new Model();
	}

	public void reset() {

		// initialize project
		setName("");
		setDescription("No description has been given.");
		setAuthor(System.getProperty("user.name"));
		setDirectory(System.getProperty("user.dir"));

		// initialize model
		model.reset();
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author
	 *            the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getDirectory() {
		return ProjectFileModel.getInstance().getFileName();
	}

	public void setDirectory(String fileName) {
		ProjectFileModel.getInstance().setFileName(fileName);
	}

	/**
	 * @return the model
	 */
	public Model getModel() {
		return model;
	}

	/**
	 * @return the xmlModelFactory
	 */
	public static es.ull.isaatc.simulation.xml.ObjectFactory getXmlModelFactory() {
		return xmlModelFactory;
	}

	public es.ull.isaatc.simulation.editor.project.xml.Project getXML() {
		es.ull.isaatc.simulation.editor.project.xml.Project projectXML = new es.ull.isaatc.simulation.editor.project.xml.ObjectFactory()
				.createProject();
		projectXML.setAuthor(getAuthor());
		projectXML.setName(getName());
		projectXML.setDescription(getDescription());
		projectXML.setModel("model.xml");
		return projectXML;
	}

	public List<ProblemTableItem> validate() {
		return model.validate();
	}

}
