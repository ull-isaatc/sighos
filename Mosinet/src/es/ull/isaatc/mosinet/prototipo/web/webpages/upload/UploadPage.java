/*
 * $Id: UploadPage.java 4619 2006-02-23 14:25:06 -0800 (Thu, 23 Feb 2006)
 * jdonnerstag $ $Revision: 461192 $ $Date: 2006-02-23 14:25:06 -0800 (Thu, 23 Feb
 * 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package es.ull.isaatc.mosinet.prototipo.web.webpages.upload;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import wicket.PageParameters;
import wicket.extensions.ajax.markup.html.form.upload.UploadProgressBar;
import wicket.markup.html.form.Form;
import wicket.markup.html.form.TextField;
import wicket.markup.html.form.upload.FileUpload;
import wicket.markup.html.form.upload.FileUploadField;
import wicket.markup.html.panel.FeedbackPanel;
import wicket.model.PropertyModel;
import wicket.spring.injection.annot.SpringBean;
import wicket.util.file.Files;
import wicket.util.file.Folder;
import wicket.util.lang.Bytes;
import es.ull.isaatc.mosinet.prototipo.model.SighosExperiment;
import es.ull.isaatc.mosinet.prototipo.model.SighosModel;
import es.ull.isaatc.mosinet.prototipo.service.impl.ServicePrototype;
import es.ull.isaatc.mosinet.prototipo.web.WicketPage;
import es.ull.isaatc.mosinet.prototipo.web.webpages.ComboBox;
import es.ull.isaatc.mosinet.prototipo.web.webpages.results.ShowResults;
import es.ull.isaatc.mosinet.util.Utils;

/**
 * Upload page.
 * 
 * @author Yurena García
 */
public class UploadPage extends WicketPage {

	private static final long serialVersionUID = 8966906119770849753L;

	/** Log. */
	private static final Log log = LogFactory.getLog(UploadPage.class);
	
	/** List of files, model for file table. */
	private List files = new ArrayList();
	private List modelFiles = new ArrayList();
	private List experimentFiles = new ArrayList();

	private SighosModel sighosModel;
	private SighosExperiment sighosExperiment;

	/** Reference to listview for easy access. */
//	private FileListView fileListView;

	/** Upload folder */
	private Folder uploadFolder;

	/**
	 * Constructor.
	 * 
	 * @param parameters
	 *            Page parameters
	 */
	public UploadPage(final PageParameters parameters) {
		
		// Initialize the sighos model and experiment
		sighosModel = null;
		sighosExperiment = null;

		// Set upload folder to tempdir + 'wicket-uploads'.
		this.uploadFolder = new Folder(System.getProperty("java.io.tmpdir"),
				"wicket-uploads");

		// Ensure folder exists
		uploadFolder.mkdirs();

		// Create feedback panels
		final FeedbackPanel uploadFeedback = new FeedbackPanel("uploadFeedback");

		// Add uploadFeedback to the page itself
		add(uploadFeedback);
		
		// Add the simulation form
		final Simulation simulation = new Simulation("simulation");
		add(simulation);

		// Add upload form with ajax progress bar
		// Model upload
		final ModelUploadForm modelUploadForm = new ModelUploadForm(
				"modelUpload");
		modelUploadForm.setModel(new PropertyModel(this, "modelUpload"));
		modelUploadForm.add(new UploadProgressBar("progress", modelUploadForm));
		add(modelUploadForm);

		// Experiment upload
		final ExperimentUploadForm experimentUploadForm = new ExperimentUploadForm(
				"experimentUpload");
		experimentUploadForm.add(new UploadProgressBar("progress",
				experimentUploadForm));
		add(experimentUploadForm);

	}

	/**
	 * Refresh file list.
	 */
	private void refreshFiles() {
//		fileListView.modelChanging();
		files.clear();
		files.addAll(Arrays.asList(uploadFolder.listFiles()));
	}

	/**
	 * Check whether the file allready exists, and if so, try to delete it.
	 * 
	 * @param newFile
	 *            the file to check
	 */
	private void checkFileExists(File newFile) {
		if (newFile.exists()) {
			// Try to delete the file
			if (!Files.remove(newFile)) {
				throw new IllegalStateException("Unable to overwrite "
						+ newFile.getAbsolutePath());
			}
		}
	}
	
	/**
	 * Form to process a simulation from model and experiment xml files
	 * @author Yurena García
	 *
	 */
	private class Simulation extends Form {

		private static final long serialVersionUID = 3691186661576293274L;
		
		private ComboBox modelComboBox;
		private ComboBox experimentComboBox;

		public Simulation(String id) {
			super(id);
			refreshFiles();
			// Add a combobox to select the model
			modelComboBox = new ComboBox("modelComboBox", files);
			add(modelComboBox);
			
			// Add a combobox to select the experiment
			experimentComboBox = new ComboBox("experimentComboBox", files);
			add(experimentComboBox);

		}
		
		protected void onSubmit() {
			
			// Update the sighos model
			File modelFile = (File)modelComboBox.getSelectedMake();
			sighosModel = new SighosModel("modelo", Utils
					.getContents(modelFile));
			System.out.println(modelComboBox.getSelectedMake());
			File experimentFile = (File)experimentComboBox.getSelectedMake();
			System.out.println(experimentComboBox.getSelectedMake());
			sighosExperiment = new SighosExperiment("experimento", Utils
					.getContents(experimentFile));
			if (sighosModel == null) {
				UploadPage.this.info("Model file is null");
			} else if (sighosExperiment == null) {
				UploadPage.this.info("Experiment file is null");
			} else {
				try {
					ServicePrototype sp = new ServicePrototype();
					String data = sp.simulate(sighosModel, sighosExperiment);

					UploadPage.this.info("Simulación procesada");
					setResponsePage(new ShowResults(data));
				} catch (Exception e) {
					UploadPage.this.info("Se produjo una excepción al simular\n");
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Form for model uploads.
	 */
	private class ModelUploadForm extends Form {

		private static final long serialVersionUID = -6044659936855962102L;
		
		@SpringBean(name="servicio")
		private ServicePrototype sp;
		private FileUploadField fileUploadField;
		private TextField modelNameField;
		String modelName = "modelo";

		/**
		 * Construct.
		 * 
		 * @param name
		 *            Component name
		 */
		public ModelUploadForm(String name) {
			super(name);

			// set this form to multipart mode (always needed for uploads!)
			setMultiPart(true);

			// Display the text field for the model name
			add(modelNameField = new TextField("modelName", new PropertyModel(
					this, "modelName")));

			// Add one file input field
			add(fileUploadField = new FileUploadField("modelInput"));
			
			// Set maximum size to 100K for demo purposes
			setMaxSize(Bytes.kilobytes(100));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		protected void onSubmit() {
			final FileUpload upload = fileUploadField.getFileUpload();
			if (upload != null) {
				// Create a new file
				File newFile = new File(uploadFolder, upload
						.getClientFileName());

				// Check new file, delete if it already existed
				checkFileExists(newFile);
				try {
					// Save to new file
					newFile.createNewFile();
					upload.writeTo(newFile);

					// Update the sighos model
					sighosModel = new SighosModel(getModelName(), Utils
							.getContents(newFile));
					
					// Save model into database
					//sp = new ServicePrototype();
					if (sp.newSighosModel(sighosModel.getModelName(), sighosModel.getXmlModel()))
						System.out.println("Pudo guardar");
					else
						System.out.println("No pudo guardar");

					// Show info
					UploadPage.this.info("saved model file: "
							+ upload.getClientFileName());
				} catch (Exception e) {
					throw new IllegalStateException(
							"Unable to write model file");
				}

				// refresh the file list view
				refreshFiles();
			}
		}

		public String getModelName() {
			return modelName;
		}

		public void setModelName(String modelName) {
			this.modelName = modelName;
		}
	}

	/**
	 * Form for experiments uploads.
	 */
	private class ExperimentUploadForm extends Form {

		private static final long serialVersionUID = -2806484730752681141L;
		
		private FileUploadField fileUploadField;
		private TextField experimentNameField;
		String experimentName = "experimento";

		/**
		 * Construct.
		 * 
		 * @param name
		 *            Component name
		 */
		public ExperimentUploadForm(String name) {
			super(name);

			// Display the text field for the experiment name
			add(experimentNameField = new TextField("experimentName",
					new PropertyModel(this, "experimentName")));

			// set this form to multipart mode (always needed for uploads!)
			setMultiPart(true);

			// Add one file input field
			add(fileUploadField = new FileUploadField("experimentInput"));

			// Set maximum size to 100K for demo purposes
			setMaxSize(Bytes.kilobytes(100));
		}

		/**
		 * @see wicket.markup.html.form.Form#onSubmit()
		 */
		protected void onSubmit() {
			final FileUpload upload = fileUploadField.getFileUpload();
			if (upload != null) {
				// Create a new file
				File newFile = new File(uploadFolder, upload
						.getClientFileName());

				// Check new file, delete if it already existed
				checkFileExists(newFile);
				try {
					// Save to new file
					newFile.createNewFile();
					upload.writeTo(newFile);
					// System.out.print(Utils.getContents(newFile));

					// Update the sighos model
					sighosExperiment = new SighosExperiment(getExperimentName(), 
							Utils.getContents(newFile));

					UploadPage.this.info("saved experiment file: "
							+ upload.getClientFileName());
				} catch (Exception e) {
					throw new IllegalStateException(
							"Unable to write experiment file");
				}

				// refresh the file list view
				refreshFiles();
			}
		}

		public String getExperimentName() {
			return experimentName;
		}

		public void setExperimentName(String experimentName) {
			this.experimentName = experimentName;
		}
	}

	/**
	 * List view for files in upload folder.
	 */
//	private class FileListView extends ListView {
//		/**
//		 * Construct.
//		 * 
//		 * @param name
//		 *            Component name
//		 * @param files
//		 *            The file list model
//		 */
//		public FileListView(String name, final List files) {
//			super(name, files);
//		}
//
//		/**
//		 * @see ListView#populateItem(ListItem)
//		 */
//		protected void populateItem(ListItem listItem) {
//			final File file = (File) listItem.getModelObject();
//			listItem.add(new Label("file", file.getName()));
//			listItem.add(new Link("delete") {
//				public void onClick() {
//					Files.remove(file);
//					refreshFiles();
//					UploadPage.this.info("Deleted " + file);
//				}
//			});
//		}
//	}
}
