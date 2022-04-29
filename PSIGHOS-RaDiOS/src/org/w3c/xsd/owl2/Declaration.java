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
 * <p>Clase Java para Declaration complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Declaration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}Axiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}Entity"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Declaration", propOrder = {
    "clazz",
    "datatype",
    "objectProperty",
    "dataProperty",
    "annotationProperty",
    "namedIndividual"
})
public class Declaration
    extends Axiom
{

    @XmlElement(name = "Class")
    protected Class clazz;
    @XmlElement(name = "Datatype")
    protected Datatype datatype;
    @XmlElement(name = "ObjectProperty")
    protected ObjectProperty objectProperty;
    @XmlElement(name = "DataProperty")
    protected DataProperty dataProperty;
    @XmlElement(name = "AnnotationProperty")
    protected AnnotationProperty annotationProperty;
    @XmlElement(name = "NamedIndividual")
    protected NamedIndividual namedIndividual;

    /**
     * Obtiene el valor de la propiedad clazz.
     * 
     * @return
     *     possible object is
     *     {@link Class }
     *     
     */
    public Class getClazz() {
        return clazz;
    }

    /**
     * Define el valor de la propiedad clazz.
     * 
     * @param value
     *     allowed object is
     *     {@link Class }
     *     
     */
    public void setClazz(Class value) {
        this.clazz = value;
    }

    /**
     * Obtiene el valor de la propiedad datatype.
     * 
     * @return
     *     possible object is
     *     {@link Datatype }
     *     
     */
    public Datatype getDatatype() {
        return datatype;
    }

    /**
     * Define el valor de la propiedad datatype.
     * 
     * @param value
     *     allowed object is
     *     {@link Datatype }
     *     
     */
    public void setDatatype(Datatype value) {
        this.datatype = value;
    }

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
     * Obtiene el valor de la propiedad dataProperty.
     * 
     * @return
     *     possible object is
     *     {@link DataProperty }
     *     
     */
    public DataProperty getDataProperty() {
        return dataProperty;
    }

    /**
     * Define el valor de la propiedad dataProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link DataProperty }
     *     
     */
    public void setDataProperty(DataProperty value) {
        this.dataProperty = value;
    }

    /**
     * Obtiene el valor de la propiedad annotationProperty.
     * 
     * @return
     *     possible object is
     *     {@link AnnotationProperty }
     *     
     */
    public AnnotationProperty getAnnotationProperty() {
        return annotationProperty;
    }

    /**
     * Define el valor de la propiedad annotationProperty.
     * 
     * @param value
     *     allowed object is
     *     {@link AnnotationProperty }
     *     
     */
    public void setAnnotationProperty(AnnotationProperty value) {
        this.annotationProperty = value;
    }

    /**
     * Obtiene el valor de la propiedad namedIndividual.
     * 
     * @return
     *     possible object is
     *     {@link NamedIndividual }
     *     
     */
    public NamedIndividual getNamedIndividual() {
        return namedIndividual;
    }

    /**
     * Define el valor de la propiedad namedIndividual.
     * 
     * @param value
     *     allowed object is
     *     {@link NamedIndividual }
     *     
     */
    public void setNamedIndividual(NamedIndividual value) {
        this.namedIndividual = value;
    }

}
