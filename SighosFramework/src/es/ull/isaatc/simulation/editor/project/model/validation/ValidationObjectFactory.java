
package es.ull.isaatc.simulation.editor.project.model.validation;

import javax.xml.bind.annotation.XmlRegistry;

import es.ull.isaatc.simulation.editor.project.model.validation.ValidationObjectFactory;

@XmlRegistry
public class ValidationObjectFactory extends es.ull.isaatc.simulation.xml.ObjectFactory {


    /**
     * Create a new ValidationObjectFactory that can be used to create new instances of schema derived classes for package: es.ull.isaatc.simulation.editor.project.model.validation
     * 
     */
    public ValidationObjectFactory() {
    }
    
    /**
     * Create an instance of {@link Component }
     * 
     */
    public Component createComponent() {
        return new Component();
    }

}
