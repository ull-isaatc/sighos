package es.ull.isaatc.simulation.editor.framework.actions.project;

import es.ull.isaatc.simulation.editor.framework.actions.SighosBaseAction;
import es.ull.isaatc.simulation.editor.project.ProjectFileModel;
import es.ull.isaatc.simulation.editor.project.ProjectFileModelListener;

/**
 * This class is an abstract class that supplies a basic Action for concrete Action classes
 * to build from. It becomes enabled only when there are no specifications loaded, and disabled
 * when there is a specification loaded.
 * 
 * @author Lindsay Bradford
 */

public abstract class SighosNoOpenProjectAction extends SighosBaseAction
		implements ProjectFileModelListener {

	private final ProjectFileModel fileModel = ProjectFileModel.getInstance();

	{
		getFileModel().subscribe(this);
	}

	public void projectFileModelStateChanged(int state) {
		if (state == ProjectFileModel.NOT_LOADED) {
			setEnabled(true);
		}
		else
			setEnabled(false);
	}

	public ProjectFileModel getFileModel() {
		return fileModel;
	}
}
