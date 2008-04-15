package es.ull.isaatc.mosinet.prototipo.web.webpages;

import java.util.List;

import wicket.markup.html.form.DropDownChoice;
import wicket.markup.html.form.Form;
import wicket.markup.html.panel.Panel;
import wicket.model.PropertyModel;

/**
 * @author Yurena Garcia
 */
public class ComboBox extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9152990715615125207L;
	private Object selectedMake;
	//@SpringBean(name="servicio")
	//private PrototipoService service;
	//private List<Alumno> lista;
	
	public ComboBox(String id, List list) {
		super(id);
		final DropDownChoice makes = new DropDownChoice("list",new PropertyModel(this,"selectedMake"),list);
		add(makes);
	}
	
	public Object getSelectedMake() {
		return selectedMake;
	}
	
	public void setSelectedMake(Object selectedMake) {
		this.selectedMake = selectedMake;
	}

	
}

