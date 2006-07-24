/**
 * 
 */
package es.ull.isaatc.simulation.editor.project.model;

/**
 * @author Roberto Muñoz
 */
public abstract class XMLDescriptedComponent {

	protected String description;
	
	protected String content;

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public abstract boolean validate();
}
