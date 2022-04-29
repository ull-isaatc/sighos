//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ObjectInverseOf complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ObjectInverseOf"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ObjectPropertyExpression"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}ObjectProperty"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ObjectInverseOf", propOrder = {
    "objectProperty"
})
public class ObjectInverseOf
    extends ObjectPropertyExpression
{

    @XmlElement(name = "ObjectProperty", required = true)
    protected ObjectProperty objectProperty;

    /**
     * Obtiene el valor de la propiedad objectProperty.
     * 
     * @return
     *     possible object is
     *     {@link ObjectProperty }
     *     
     */
    public ObjectProperty getObjectProperty() {
        return objectProperty;
    }

    /**
     * Define el valor de la propiedad objectProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectProperty }
     *     
     */
    public void setObjectProperty(ObjectProperty value) {
        this.objectProperty = value;
    }

}
