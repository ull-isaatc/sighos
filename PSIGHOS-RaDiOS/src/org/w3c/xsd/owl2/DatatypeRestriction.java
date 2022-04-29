//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2022.04.26 a las 05:42:17 PM BST 
//


package org.w3c.xsd.owl2;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para DatatypeRestriction complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="DatatypeRestriction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://www.w3.org/2002/07/owl#}DataRange"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}Datatype"/&gt;
 *         &lt;element name="FacetRestriction" type="{http://www.w3.org/2002/07/owl#}FacetRestriction" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DatatypeRestriction", propOrder = {
    "datatype",
    "facetRestriction"
})
public class DatatypeRestriction
    extends DataRange
{

    @XmlElement(name = "Datatype", required = true)
    protected Datatype datatype;
    @XmlElement(name = "FacetRestriction", required = true)
    protected List<FacetRestriction> facetRestriction;

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
     * Gets the value of the facetRestriction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the facetRestriction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFacetRestriction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link FacetRestriction }
     * 
     * 
     */
    public List<FacetRestriction> getFacetRestriction() {
        if (facetRestriction == null) {
            facetRestriction = new ArrayList<FacetRestriction>();
        }
        return this.facetRestriction;
    }

}
