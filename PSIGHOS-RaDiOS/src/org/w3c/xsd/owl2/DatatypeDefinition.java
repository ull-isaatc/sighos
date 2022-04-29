//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DatatypeDefinition complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DatatypeDefinition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}Axiom"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}Datatype"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}DataRange"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatatypeDefinition", propOrder = {
    "rest"
})
public class DatatypeDefinition
    extends Axiom
{

    @XmlElementRefs({
        @XmlElementRef(name = "DataUnionOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "Datatype", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataOneOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DatatypeRestriction", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataComplementOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false),
        @XmlElementRef(name = "DataIntersectionOf", namespace = "http://www.w3.org/2002/07/owl#", type = JAXBElement.class, required = false)
    })
    protected List<JAXBElement<? extends DataRange>> rest;

    /**
     * Obtiene el resto del modelo de contenido. 
     * 
     * <p>
     * Ha obtenido esta propiedad que permite capturar todo por el siguiente motivo: 
     * El nombre de campo "Datatype" se está utilizando en dos partes diferentes de un esquema. Consulte: 
     * línea 218 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
     * línea 887 de file:/C:/Users/Iván%20Castilla/git/RaDiOS-MTT/src/main/resources/xsd/owl2.xsd
     * <p>
     * Para deshacerse de esta propiedad, aplique una personalización de propiedad a una
     * de las dos declaraciones siguientes para cambiarles de nombre: 
     * Gets the value of the rest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the rest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link JAXBElement }{@code <}{@link DataUnionOf }{@code >}
     * {@link JAXBElement }{@code <}{@link Datatype }{@code >}
     * {@link JAXBElement }{@code <}{@link DataOneOf }{@code >}
     * {@link JAXBElement }{@code <}{@link DataComplementOf }{@code >}
     * {@link JAXBElement }{@code <}{@link DatatypeRestriction }{@code >}
     * {@link JAXBElement }{@code <}{@link DataIntersectionOf }{@code >}
     * 
     * 
     */
    public List<JAXBElement<? extends DataRange>> getRest() {
        if (rest == null) {
            rest = new ArrayList<JAXBElement<? extends DataRange>>();
        }
        return this.rest;
    }

}
