package es.ull.isaatc.simulation.editor.framework.swing;

import java.awt.Component;
import java.awt.HeadlessException;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import es.ull.isaatc.simulation.editor.framework.SighosFramework;

public class FileChooserFactory {

	public static final int SAVING_AND_LOADING = 0;

	public static final int IMPORTING_AND_EXPORTING = 1;
	
	private static final String SAVING_AND_LOADING_LABEL = "lastUsedSaveLoadDirectory";

	private static final String IMPORTING_AND_EXPORTING_LABEL = "lastUsedImportExportDirectory";

	private static final Preferences prefs = Preferences
			.userNodeForPackage(SighosFramework.class);

	public static JFileChooser buildFileChooser(final String fileType,
			final String description, final String titlePrefix,
			final String titleSuffix, final int usage) {

		JFileChooser fileChooser = new JFileChooser() {
			private static final long serialVersionUID = 1L;

			public int showDialog(Component parent, String approveButtonText)
					throws HeadlessException {

				// just before showing the dialog, point the dialog at the
				// last directory used.

				switch (usage) {
				case SAVING_AND_LOADING: {
					setCurrentDirectory(new File(prefs.get(
							SAVING_AND_LOADING_LABEL, System
									.getProperty("user.dir"))));
					break;

				}
				case IMPORTING_AND_EXPORTING: {
					setCurrentDirectory(new File(prefs.get(
							IMPORTING_AND_EXPORTING_LABEL, System
									.getProperty("user.dir"))));
					break;
				}
				}

				return super.showDialog(parent, approveButtonText);
			}

			public File getSelectedFile() {

				// When the user retrieves the file, remember
				// the directory used for next time.

				File selectedFile = super.getSelectedFile();
				if (selectedFile != null) {
					switch (usage) {
					case SAVING_AND_LOADING: {
						prefs.put(SAVING_AND_LOADING_LABEL,
								getCurrentDirectory().getAbsolutePath());
						break;
					}
					case IMPORTING_AND_EXPORTING: {
						prefs.put(IMPORTING_AND_EXPORTING_LABEL,
								getCurrentDirectory().getAbsolutePath());
						break;
					}
					}
				}
				return selectedFile;
			}
		};

		fileChooser.setDialogTitle(titlePrefix + fileType.toUpperCase()
				+ titleSuffix);

		fileChooser.setFileFilter(new FileFilter() {
			public String getDescription() {
				return description + " (*." + fileType + ")";
			}

			public boolean accept(File file) {
				return file.isDirectory()
						|| file.getName().toLowerCase()
								.endsWith("." + fileType);
			}
		});

		return fileChooser;
	}
}
