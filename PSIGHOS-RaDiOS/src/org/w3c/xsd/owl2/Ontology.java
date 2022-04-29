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
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Clase Java para Ontology complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="Ontology"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}Prefix" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element ref="{http://www.w3.org/2002/07/owl#}Import" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}ontologyAnnotations"/&gt;
 *         &lt;group ref="{http://www.w3.org/2002/07/owl#}Axiom" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;attGroup ref="{http://www.w3.org/XML/1998/namespace}specialAttrs"/&gt;
 *       &lt;attribute name="ontologyIRI" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *       &lt;attribute name="versionIRI" type="{http://www.w3.org/2001/XMLSchema}anyURI" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Ontology", propOrder = {
    "prefix",
    "_import",
    "annotation",
    "axiom"
})
public class Ontology {

    @XmlElement(name = "Prefix")
    protected List<Prefix> prefix;
    @XmlElement(name = "Import")
    protected List<Import> _import;
    @XmlElement(name = "Annotation")
    protected List<Annotation> annotation;
    @XmlElements({
        @XmlElement(name = "Declaration", type = Declaration.class),
        @XmlElement(name = "SubClassOf", type = SubClassOf.class),
        @XmlElement(name = "EquivalentClasses", type = EquivalentClasses.class),
        @XmlElement(name = "DisjointClasses", type = DisjointClasses.class),
        @XmlElement(name = "DisjointUnion", type = DisjointUnion.class),
        @XmlElement(name = "SubObjectPropertyOf", type = SubObjectPropertyOf.class),
        @XmlElement(name = "EquivalentObjectProperties", type = EquivalentObjectProperties.class),
        @XmlElement(name = "DisjointObjectProperties", type = DisjointObjectProperties.class),
        @XmlElement(name = "InverseObjectProperties", type = InverseObjectProperties.class),
        @XmlElement(name = "ObjectPropertyDomain", type = ObjectPropertyDomain.class),
        @XmlElement(name = "ObjectPropertyRange", type = ObjectPropertyRange.class),
        @XmlElement(name = "FunctionalObjectProperty", type = FunctionalObjectProperty.class),
        @XmlElement(name = "InverseFunctionalObjectProperty", type = InverseFunctionalObjectProperty.class),
        @XmlElement(name = "ReflexiveObjectProperty", type = ReflexiveObjectProperty.class),
        @XmlElement(name = "IrreflexiveObjectProperty", type = IrreflexiveObjectProperty.class),
        @XmlElement(name = "SymmetricObjectProperty", type = SymmetricObjectProperty.class),
        @XmlElement(name = "AsymmetricObjectProperty", type = AsymmetricObjectProperty.class),
        @XmlElement(name = "TransitiveObjectProperty", type = TransitiveObjectProperty.class),
        @XmlElement(name = "SubDataPropertyOf", type = SubDataPropertyOf.class),
        @XmlElement(name = "EquivalentDataProperties", type = EquivalentDataProperties.class),
        @XmlElement(name = "DisjointDataProperties", type = DisjointDataProperties.class),
        @XmlElement(name = "DataPropertyDomain", type = DataPropertyDomain.class),
        @XmlElement(name = "DataPropertyRange", type = DataPropertyRange.class),
        @XmlElement(name = "FunctionalDataProperty", type = FunctionalDataProperty.class),
        @XmlElement(name = "DatatypeDefinition", type = DatatypeDefinition.class),
        @XmlElement(name = "HasKey", type = HasKey.class),
        @XmlElement(name = "SameIndividual", type = SameIndividual.class),
        @XmlElement(name = "DifferentIndividuals", type = DifferentIndividuals.class),
        @XmlElement(name = "ClassAssertion", type = ClassAssertion.class),
        @XmlElement(name = "ObjectPropertyAssertion", type = ObjectPropertyAssertion.class),
        @XmlElement(name = "NegativeObjectPropertyAssertion", type = NegativeObjectPropertyAssertion.class),
        @XmlElement(name = "DataPropertyAssertion", type = DataPropertyAssertion.class),
        @XmlElement(name = "NegativeDataPropertyAssertion", type = NegativeDataPropertyAssertion.class),
        @XmlElement(name = "AnnotationAssertion", type = AnnotationAssertion.class),
        @XmlElement(name = "SubAnnotationPropertyOf", type = SubAnnotationPropertyOf.class),
        @XmlElement(name = "AnnotationPropertyDomain", type = AnnotationPropertyDomain.class),
        @XmlElement(name = "AnnotationPropertyRange", type = AnnotationPropertyRange.class)
    })
    protected List<Axiom> axiom;
    @XmlAttribute(name = "ontologyIRI")
    @XmlSchemaType(name = "anyURI")
    protected String ontologyIRI;
    @XmlAttribute(name = "versionIRI")
    @XmlSchemaType(name = "anyURI")
    protected String versionIRI;
    @XmlAttribute(name = "base", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlSchemaType(name = "anyURI")
    protected String base;
    @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace")
    protected String lang;
    @XmlAttribute(name = "space", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String space;
    @XmlAttribute(name = "id", namespace = "http://www.w3.org/XML/1998/namespace")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    /**
     * Gets the value of the prefix property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the prefix property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPrefix().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Prefix }
     * 
     * 
     */
    public List<Prefix> getPrefix() {
        if (prefix == null) {
            prefix = new ArrayList<Prefix>();
        }
        return this.prefix;
    }

    /**
     * Gets the value of the import property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the import property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImport().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Import }
     * 
     * 
     */
    public List<Import> getImport() {
        if (_import == null) {
            _import = new ArrayList<Import>();
        }
        return this._import;
    }

    /**
     * Gets the value of the annotation property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annotation property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnotation().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Annotation }
     * 
     * 
     */
    public List<Annotation> getAnnotation() {
        if (annotation == null) {
            annotation = new ArrayList<Annotation>();
        }
        return this.annotation;
    }

    /**
     * Gets the value of the axiom property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the axiom property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAxiom().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Declaration }
     * {@link SubClassOf }
     * {@link EquivalentClasses }
     * {@link DisjointClasses }
     * {@link DisjointUnion }
     * {@link SubObjectPropertyOf }
     * {@link EquivalentObjectProperties }
     * {@link DisjointObjectProperties }
     * {@link InverseObjectProperties }
     * {@link ObjectPropertyDomain }
     * {@link ObjectPropertyRange }
     * {@link FunctionalObjectProperty }
     * {@link InverseFunctionalObjectProperty }
     * {@link ReflexiveObjectProperty }
     * {@link IrreflexiveObjectProperty }
     * {@link SymmetricObjectProperty }
     * {@link AsymmetricObjectProperty }
     * {@link TransitiveObjectProperty }
     * {@link SubDataPropertyOf }
     * {@link EquivalentDataProperties }
     * {@link DisjointDataProperties }
     * {@link DataPropertyDomain }
     * {@link DataPropertyRange }
     * {@link FunctionalDataProperty }
     * {@link DatatypeDefinition }
     * {@link HasKey }
     * {@link SameIndividual }
     * {@link DifferentIndividuals }
     * {@link ClassAssertion }
     * {@link ObjectPropertyAssertion }
     * {@link NegativeObjectPropertyAssertion }
     * {@link DataPropertyAssertion }
     * {@link NegativeDataPropertyAssertion }
     * {@link AnnotationAssertion }
     * {@link SubAnnotationPropertyOf }
     * {@link AnnotationPropertyDomain }
     * {@link AnnotationPropertyRange }
     * 
     * 
     */
    public List<Axiom> getAxiom() {
        if (axiom == null) {
            axiom = new ArrayList<Axiom>();
        }
        return this.axiom;
    }

    /**
     * Obtiene el valor de la propiedad ontologyIRI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOntologyIRI() {
        return ontologyIRI;
    }

    /**
     * Define el valor de la propiedad ontologyIRI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOntologyIRI(String value) {
        this.ontologyIRI = value;
    }

    /**
     * Obtiene el valor de la propiedad versionIRI.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVersionIRI() {
        return versionIRI;
    }

    /**
     * Define el valor de la propiedad versionIRI.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVersionIRI(String value) {
        this.versionIRI = value;
    }

    /**
     * Obtiene el valor de la propiedad base.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBase() {
        return base;
    }

    /**
     * Define el valor de la propiedad base.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBase(String value) {
        this.base = value;
    }

    /**
     * Obtiene el valor de la propiedad lang.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLang() {
        return lang;
    }

    /**
     * Define el valor de la propiedad lang.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLang(String value) {
        this.lang = value;
    }

    /**
     * Obtiene el valor de la propiedad space.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpace() {
        return space;
    }

    /**
     * Define el valor de la propiedad space.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpace(String value) {
        this.space = value;
    }

    /**
     * Obtiene el valor de la propiedad id.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Define el valor de la propiedad id.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

}
