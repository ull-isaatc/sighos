package es.ull.isaatc.simulation.editor.project;

import java.util.LinkedList;

/**
 * A singleton model of specification file state. This model keeps track of 
 * the state of specification files, and publishes state changes to subscribers
 * implementing the the {@link ProjectFileModelListener} interface. Together, this singleton and 
 * subscribing objects implementing the <code>SpecificationFileModelListener</code> interface are example of the 
 * publish/subscribe design pattern.
 * 
 * @see ProjectFileModelListener
 * @author Lindsay Bradford
 */

public class ProjectFileModel {
	static private int fileCount = 0;

	private String fileName = "";

	/** State indicating that no project file is currently open */
	public static final int NOT_LOADED = 0;

	/** State indicating that a project file is currently open */
	public static final int LOADED = 1;

	/** State indicating that the current project is not validated */
	public static final int NOT_VALIDATED = 2;

	/** State indicating that the current project is validated */
	public static final int VALIDATED = 4;

	/** State indicating that some project file operation is currently in progress */
	public static final int BUSY = 128;

	static private int state = NOT_LOADED;

	static private int oldState = NOT_LOADED;

	static private LinkedList<ProjectFileModelListener> subscribers = new LinkedList<ProjectFileModelListener>();

	private static final ProjectFileModel INSTANCE = new ProjectFileModel();

	private ProjectFileModel() {
	}

	/**
	 * Returns the one and only allowable instance of <code>projectFileModel</code>
	 * allowed to exist.
	 */

	public static ProjectFileModel getInstance() {
		return INSTANCE;
	}

	/**
	 * Adds a new subscriber to list of subscribers wanting to be informed of 
	 * <code>projectFileModel</code> state change.  Note that the act of subscription 
	 * will have the side-effect of the <code>projectFileModel</code> publishing its current 
	 * state to the new subscriber only.
	 * 
	 * @param subscriber The object needing notification of state change..
	 * @see ProjectFileModelListener
	 */

	public void subscribe(final ProjectFileModelListener subscriber) {
		subscribers.add(subscriber);
		subscriber.projectFileModelStateChanged(state);
	}

	private void publishState(final int inputState) {
		if (state == BUSY) {
			oldState = inputState;
			return;
		}

		state = inputState;
		publishState();
	}

	private void publishState() {
		for (int i = 0; i < subscribers.size(); i++) {
			ProjectFileModelListener listener = (ProjectFileModelListener) subscribers
					.get(i);
			listener.projectFileModelStateChanged(state);
		}
	}

	/**
	 * A method allowing other objects to inform the model of the opening of a new project file.
	 * This may trigger the publishing of an {@link #LOADED} state to all subscribing 
	 * {@link ProjectFileModelListener} objects.
	 * 
	 * @see ProjectFileModelListener
	 */

	public void incrementFileCount() {
		final int oldFileCount = fileCount;
		fileCount++;
		if (oldFileCount == 0) {
			publishState(LOADED);
		}
	}

	/**
	 * A method allowing other objects to inform the model of the closing of an open project file.
	 * This may trigger the publishing of an {@link #NOT_LOADED} state to all subscribing 
	 * {@link ProjectFileModelListener} objects.
	 * 
	 * @see ProjectFileModelListener
	 */

	public void decrementFileCount() {
		final int oldFileCount = fileCount;
		fileCount--;
		if (oldFileCount == 1) {
			publishState(NOT_LOADED);
		}
	}

	/**
	 * A method allowing other objects to retrieve the number of open project files. This has
	 * no state change side-effects. Note that at the moment, this method should return only 0 or 1.
	 * @return The number of project files currently open
	 */

	public int getFileCount() {
		return fileCount;
	}

	/**
	 * A method allowing other objects to alert the <CODE>projectFileModel</CODE> that some
	 * project file operation is in progress. This causes a publication of the {@link #BUSY} state
	 * to all subscribed {@link ProjectFileModelListener} objects. Note that the calling object, 
	 * by invoking this method, is accepting responsibility for invoking the {@link #notBusy()} method 
	 * once the file operation is complete.
	 * 
	 * @see ProjectFileModelListener
	 * @see #notBusy()
	 */
	public void busy() {
		oldState = state;
		publishState(BUSY);
	}

	/**
	 * A method allowing other objects to alert the <CODE>projectFileModel</CODE> that some
	 * project file operation has completed. This causes a publication of the stete what was 
	 * in place before the last call to the {@link #busy()} method was made to all currently subscribed
	 * {@link ProjectFileModelListener} objects.
	 * 
	 * @see ProjectFileModelListener
	 * @see #busy()
	 */
	public void notBusy() {
		state = oldState;
		publishState();
	}

	/**
	 * A method allowing other objects to get the file name that will be used for saving and loading 
	 * project files.
	 * @return The name (as a full path) of the project file.
	 */
	public String getFileName() {
		return this.fileName;
	}

	/**
	 * A method allowing other objects to set the file name that will be used for saving and loading 
	 * project files.
	 * @param fileName The name (as a full path) of the project file.
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
		if (fileName != null) {
			publishState();
		}
	}
}
