package es.ull.isaatc.simulation.editor.project;

import es.ull.isaatc.simulation.editor.framework.swing.SighosTabbedPane;

public class ArchivingThread extends Thread {

	// TODO : transform theese constants to Enum
	private static final int NOTHING = 0;
	
	private static final int NEW = 1;

	private static final int SAVE = 2;

	private static final int SAVEAS = 3;

	private static final int OPEN = 4;

	private static final int OPEN_FILE = 5;

	private static final int CLOSE = 6;

	private static final int VALIDATE = 7;

	private static final int EXIT = 9;

	private static final int SLEEP_PERIOD = 100;

	private int request = NOTHING;

	private String openFileName;

	private static final ArchivingThread INSTANCE = new ArchivingThread();

	public static ArchivingThread getInstance() {
		return INSTANCE;
	}

	private ArchivingThread() {
	}

	public synchronized void newProject() {
		request = NEW;
	}

	public synchronized void open() {
		request = OPEN;
	}

	public synchronized void open(String fileName) {
		request = OPEN_FILE;
		openFileName = fileName;
	}

	public synchronized void save() {
		request = SAVE;
	}

	public synchronized void saveAs() {
		request = SAVEAS;
	}

	public synchronized void close() {
		request = CLOSE;
	}

	public synchronized void validate() {
		request = VALIDATE;
	}

	public synchronized void exit() {
		request = EXIT;
	}

	public void run() {
		while (true) {
			try {
				sleep(SLEEP_PERIOD);
			} catch (Exception e) {
			}
			processRequestState();
		}
	}

	private synchronized void processRequestState() {
		if (request == NOTHING) {
			return;
		}
		ProjectFileModel.getInstance().busy();
		switch (request) {
		case NEW: {
			ProjectArchiveHandler.getInstance().newProject();
			break;
		}
		case OPEN: {
			ProjectArchiveHandler.getInstance().open();
			validate();
			break;
		}
		case OPEN_FILE: {
			ProjectArchiveHandler.getInstance().open(openFileName);
			validate();
			break;
		}
		case SAVE: {
			ProjectArchiveHandler.getInstance().save();
			break;
		}
		case SAVEAS: {
			ProjectArchiveHandler.getInstance().saveAs();
			break;
		}
		case CLOSE: {
			ProjectArchiveHandler.getInstance().close();
			break;
		}
		case VALIDATE: {
			SighosTabbedPane.getInstance().getProblemTable().setProblemList(
					ProjectModel.getInstance().validate());
			SighosTabbedPane.getInstance().setSelectedIndex(0);
			break;
		}
		case EXIT: {
			ProjectArchiveHandler.getInstance().exit();
			break;
		}
		}
		request = NOTHING;
		ProjectFileModel.getInstance().notBusy();
	}

}