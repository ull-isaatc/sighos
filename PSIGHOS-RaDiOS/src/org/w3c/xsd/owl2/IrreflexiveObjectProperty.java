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
 * <p>Clase Java para IrreflexiveObjectProperty complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="IrreflexiveObjectProperty"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}ObjectPropertyAxiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ObjectPropertyExpression"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IrreflexiveObjectProperty", propOrder = {
    "objectProperty",
    "objectInverseOf"
})
public class IrreflexiveObjectProperty
    extends ObjectPropertyAxiom
{

    @XmlElement(name = "ObjectProperty")
    protected ObjectProperty objectProperty;
    @XmlElement(name = "ObjectInverseOf")
    protected ObjectInverseOf objectInverseOf;

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

    /**
     * Obtiene el valor de la propiedad objectInverseOf.
     * 
     * @return
     *     possible object is
     *     {@link ObjectInverseOf }
     *     
     */
    public ObjectInverseOf getObjectInverseOf() {
        return objectInverseOf;
    }

    /**
     * Define el valor de la propiedad objectInverseOf.
     * 
     * @param value
     *     allowed object is
     *     {@link ObjectInverseOf }
     *     
     */
    public void setObjectInverseOf(ObjectInverseOf value) {
        this.objectInverseOf = value;
    }

}
