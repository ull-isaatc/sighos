package es.ull.iis.simulation.hta.osdi;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.w3c.xsd.owl2.Axiom;
import org.w3c.xsd.owl2.ClassAssertion;
import org.w3c.xsd.owl2.ClassExpression;
import org.w3c.xsd.owl2.DataPropertyAssertion;
import org.w3c.xsd.owl2.Declaration;
import org.w3c.xsd.owl2.NamedIndividual;
import org.w3c.xsd.owl2.ObjectProperty;
import org.w3c.xsd.owl2.ObjectPropertyAssertion;
import org.w3c.xsd.owl2.ObjectPropertyDomain;
import org.w3c.xsd.owl2.ObjectPropertyRange;
import org.w3c.xsd.owl2.Ontology;
import org.w3c.xsd.owl2.SubClassOf;

/**
 * A convenient class to help parsing an ontology
 * @deprecated
 * @author masbe
 *
 */
public class OwlHelper {
	private final Map<String, Map<String, List<String>>> dataPropertyValues;
	private final Map<String, Map<String, List<String>>> objectPropertyValues;
	private final Map<String, String> instanceToClazz;
	private final Map<String, ClazzHierarchyNode> hierarchy = new TreeMap<>();
	private final Ontology ontology;
	
	
	/**
	 * Initializes the inner structures of the helper
	 * @param ontology The ontology 
	 * @throws IOException 
	 * @throws JAXBException 
	 * @throws FileNotFoundException 
	 */
	public OwlHelper (String path) throws FileNotFoundException, JAXBException, IOException {
		this.ontology = loadOntology(path);
		instanceToClazz = eTLClassIndividuals(ontology);
		dataPropertyValues = eTLDataPropertyValues(ontology);
		objectPropertyValues = eTLObjectProperties(ontology);
		initializeHierarchy();
	}
	

	/**
	 * 
	 * @param ontology
	 * @return
	 */
	public static Map<String, String> eTLClassIndividuals(Ontology ontology) {
		Map<String, String> result = new TreeMap<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ClassAssertion) {
				ClassAssertion classAssertion = (ClassAssertion) axiom;
				String clazzName = classAssertion.getNamedIndividual().getIRI();
				String clazzType = classAssertion.getClazz().getIRI();
				result.put(clazzName, clazzType);
			}
		}
		return result;
	}
	
	private Map<String, ClazzHierarchyNode> initializeHierarchy() {
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof SubClassOf) {
				List<JAXBElement<? extends ClassExpression>> list =((SubClassOf) axiom).getRest();
				// Assuming that the first element is always a class
				String name = ((org.w3c.xsd.owl2.Class)list.get(0).getValue()).getIRI();
				ClazzHierarchyNode parent;
				if (hierarchy.containsKey(name))
					parent = hierarchy.get(name);
				else
					parent = new ClazzHierarchyNode(name);
				// If the second element is a class too, then it is inheritance
				if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.Class) {
					parent.addAscendant(((org.w3c.xsd.owl2.Class)list.get(1).getValue()).getIRI());
				}
			}			
		}
		return hierarchy;
	}

	public List<String> getClazzes () {
		List<String> clazzes = new ArrayList<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof Declaration) {
				Declaration decl = (Declaration) axiom;
				if (decl.getClazz() != null)
					clazzes.add(decl.getClazz().getIRI());
			}			
		}
		return clazzes;
	}

	public List<String> getObjectProperties () {
		List<String> properties = new ArrayList<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof Declaration) {
				Declaration decl = (Declaration) axiom;
				if (decl.getObjectProperty() != null)
					properties.add(decl.getObjectProperty().getIRI());
			}			
		}
		return properties;
	}

	public TreeMap<String, Set<String>> getObjectPropertiesByClazz() {
		final TreeMap<String, Set<String>> clazzes = new TreeMap<>();
		for (String clazz : getClazzes())
			clazzes.put(clazz, new TreeSet<>());
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ObjectPropertyDomain) {
				final ObjectPropertyDomain decl = (ObjectPropertyDomain) axiom;
				final String prop = decl.getObjectProperty().getIRI();
				if (decl.getClazz() != null) {
					clazzes.get(decl.getClazz().getIRI()).add(prop);
				}
				else if (decl.getObjectUnionOf() != null) {
					final List<ClassExpression> list = decl.getObjectUnionOf().getClassExpression();
					for (ClassExpression exp : list) {
						if (exp instanceof org.w3c.xsd.owl2.Class)
						clazzes.get(((org.w3c.xsd.owl2.Class)exp).getIRI()).add(prop);
					}
				}
			}
			else if (axiom instanceof SubClassOf) {
				final List<JAXBElement<? extends ClassExpression>> list =((SubClassOf) axiom).getRest();
				// Assuming that the first element is always a class
				final String name = ((org.w3c.xsd.owl2.Class)list.get(0).getValue()).getIRI();
				if (!(list.get(1).getValue() instanceof org.w3c.xsd.owl2.Class)) {
					if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.ObjectExactCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.ObjectExactCardinality)list.get(1).getValue()).getObjectProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.ObjectMaxCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.ObjectMaxCardinality)list.get(1).getValue()).getObjectProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.ObjectMinCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.ObjectMinCardinality)list.get(1).getValue()).getObjectProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.ObjectSomeValuesFrom) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.ObjectSomeValuesFrom)list.get(1).getValue()).getObjectProperty().getIRI());
					}
				}
			}			
			
		}
		for (String clazz : clazzes.keySet())
			propagateProperties(clazz, clazzes, new TreeSet<>());
		return clazzes;
	}

	public TreeMap<String, Set<String>> getClazzesByObjectProperty() {
		final TreeMap<String, Set<String>> properties = new TreeMap<>();
		for (String prop : getObjectProperties())
			properties.put(prop, new TreeSet<>());
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ObjectPropertyRange) {
				final ObjectPropertyRange decl = (ObjectPropertyRange) axiom;
				final String prop = decl.getObjectProperty().getIRI();
				if (decl.getClazz() != null) {
					properties.get(prop).add(decl.getClazz().getIRI());
				}
				else if (decl.getObjectUnionOf() != null) {
					final List<ClassExpression> list = decl.getObjectUnionOf().getClassExpression();
					for (ClassExpression exp : list) {
						if (exp instanceof org.w3c.xsd.owl2.Class)
						properties.get(prop).add(((org.w3c.xsd.owl2.Class)exp).getIRI());
					}
				}
			}			
		}
		return properties;
	}

	public List<String> getDataProperties () {
		List<String> properties = new ArrayList<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof Declaration) {
				Declaration decl = (Declaration) axiom;
				if (decl.getDataProperty() != null)
					properties.add(decl.getDataProperty().getIRI());
			}			
		}
		return properties;
	}

	public TreeMap<String, Set<String>> getDataPropertiesByClazz() {
		final TreeMap<String, Set<String>> clazzes = new TreeMap<>();
		for (String clazz : getClazzes())
			clazzes.put(clazz, new TreeSet<>());
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof SubClassOf) {
				final List<JAXBElement<? extends ClassExpression>> list =((SubClassOf) axiom).getRest();
				// Assuming that the first element is always a class
				final String name = ((org.w3c.xsd.owl2.Class)list.get(0).getValue()).getIRI();
				if (!(list.get(1).getValue() instanceof org.w3c.xsd.owl2.Class)) {
					if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.DataExactCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.DataExactCardinality)list.get(1).getValue()).getDataProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.DataMaxCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.DataMaxCardinality)list.get(1).getValue()).getDataProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.DataMinCardinality) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.DataMinCardinality)list.get(1).getValue()).getDataProperty().getIRI());
					}
					else if (list.get(1).getValue() instanceof org.w3c.xsd.owl2.DataSomeValuesFrom) {
						clazzes.get(name).add(((org.w3c.xsd.owl2.DataSomeValuesFrom)list.get(1).getValue()).getDataPropertyExpression().get(0).getIRI());
					}
				}
			}			
		}
		for (String clazz : clazzes.keySet())
			propagateProperties(clazz, clazzes, new TreeSet<>());
		return clazzes;
	}
	
	private void propagateProperties(String clazz, TreeMap<String, Set<String>> properties, Set<String> extraProperties) {
		// Assign any extra property from ascendants
		final Set<String> ownProperties = properties.get(clazz);
		ownProperties.addAll(extraProperties);
		final List<ClazzHierarchyNode> descendants = hierarchy.get(clazz).getDescendants();
		final Set<String> moreProperties = new TreeSet<>(ownProperties);
		for (ClazzHierarchyNode node : descendants) {
			propagateProperties(node.getName(), properties, moreProperties);
		}
	}
	
	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology.
	 * If there are is than one value for that property, returns the first one. 
	 * If the data property or the instance are not defined, returns the default value. Be aware that the instance might be not defined simply because 
	 * it does not define any data property, and not because it does not exists.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @param defaultValue Value that is returned when not defined 
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public String getDataPropertyValue (String instanceName, String propertyName, String defaultValue) {
		final Map<String, List<String>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return defaultValue;
		if (data.get(propertyName) == null)
			return defaultValue;
		return data.get(propertyName).get(0);
	}
	
	/**
	 * Returns the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 * If the data instance is not defined, returns null. If the data property is not defined, returns an empty list.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string values corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public List<String> getDataPropertyValues(String instanceName, String propertyName) {
		final Map<String, List<String>> data = dataPropertyValues.get(instanceName);
		if (data == null)
			return null;
		final ArrayList<String> values = new ArrayList<>();
		if (data.get(propertyName) != null)
			for (String dataItem : data.get(propertyName)) {
				values.add(dataItem);
			}
		return values;
	}
	
	/**
	 * Returns the string corresponding to the specified data property defined in the specified instance in the ontology. 
	 * If the data property or the instance are not defined, returns null.
	 * @param instanceName Name of the instance that defines the data property
	 * @param propertyName Name of the data property
	 * @return the string corresponding to the specified data property defined in the specified instance in the ontology.
	 */
	public String getDataPropertyValue (String instanceName, String propertyName) {
		return getDataPropertyValue(instanceName, propertyName, null);
	}
	
	/**
	 * Returns the object associated to another object by means of the specified object property. If there are more than 
	 * one, returns the first one.  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the object associated to another object by means of the specified object property
	 */
	public String getObjectPropertyByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return null;
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return null;
		return list.get(0);
	}
	
	/**
	 * Returns the list of objects associated to another object by means of the specified object property  
	 * @param instanceName Name of the original instance
	 * @param objectPropertyName Name of the property
	 * @return the list of objects associated to another object by means of the specified object property
	 */
	public List<String> getObjectPropertiesByName(String instanceName, String objectPropertyName) {
		final Map<String, List<String>> map = objectPropertyValues.get(instanceName);
		if (map == null)
			return new ArrayList<>();
		List<String> list = map.get(objectPropertyName);
		if (list == null)
			return new ArrayList<>();
		return list;
	}
	
	public List<String> getChilds(String objectName) {
		List<String> result = null;
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				result.addAll(objectProperties.get(key));
			}
		}
		return result;
	}

	public List<String> getChildsByClassName(String objectName, String className) {
		List<String> result = new ArrayList<>();
		Map<String, List<String>> objectProperties = objectPropertyValues.get(objectName);
		if (objectProperties != null && !objectProperties.keySet().isEmpty()) {
			result = new ArrayList<>();
			for (String key: objectProperties.keySet()) {
				List<String> values = objectProperties.get(key);
				if (values != null && !values.isEmpty()) {
					for (String value: values) {
						if (className.equals(instanceToClazz.get(value))) {
							result.add(value);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * @param path
	 * @return
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Ontology loadOntology(String path) throws JAXBException, FileNotFoundException, IOException {
		Ontology ontology = null;
	
		try (InputStream xmlOwl = new FileInputStream(path)) {
			JAXBContext jc = JAXBContext.newInstance("org.w3c.xsd.owl2");
			Unmarshaller unmarshaller = jc.createUnmarshaller();
		
			JAXBElement<Ontology> jaxbOntology = unmarshaller.unmarshal(new StreamSource(xmlOwl), Ontology.class);
			ontology = jaxbOntology.getValue();
		}
		return ontology;
	}

	/**
	 * @param ontology
	 * @return
	 */
	private static Map<String, Map<String, List<String>>> eTLObjectProperties(Ontology ontology) {
		Map<String, Map<String, List<String>>> result = new HashMap<String, Map<String, List<String>>>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof ObjectPropertyAssertion) {
				ObjectPropertyAssertion assertion = (ObjectPropertyAssertion) axiom;
				String assertionLink = ((ObjectProperty) assertion.getRest().get(0).getValue()).getIRI();
				String assertionLeftSide = ((NamedIndividual) assertion.getRest().get(1).getValue()).getIRI();
				String assertionRightSide = ((NamedIndividual) assertion.getRest().get(2).getValue()).getIRI();

				if (result.get(assertionLeftSide) == null) {
					result.put(assertionLeftSide, new HashMap<String, List<String>>());
				}
				if (result.get(assertionLeftSide).get(assertionLink) == null) {
					result.get(assertionLeftSide).put(assertionLink, new ArrayList<String>());
				}
				result.get(assertionLeftSide).get(assertionLink).add(assertionRightSide);
			}
		}
		return result;
	}

	/**
	 * @param ontology
	 * @return
	 */
	private static Map<String, Map<String, List<String>>> eTLDataPropertyValues(Ontology ontology) {
		Map<String, Map<String, List<String>>> result = new HashMap<>();
		for (Axiom axiom : ontology.getAxiom()) {
			if (axiom instanceof DataPropertyAssertion) {
				DataPropertyAssertion dataPropertyAssertion = (DataPropertyAssertion) axiom;
				String clazzName = dataPropertyAssertion.getNamedIndividual().getIRI();
				Map<String, List<String>> dataProperties = result.get(clazzName);
				if (dataProperties == null) {
					dataProperties = new HashMap<>();
				}
				
				String dataPropertyName = dataPropertyAssertion.getDataProperty().getIRI();
				if (!dataProperties.containsKey(dataPropertyName))
					dataProperties.put(dataPropertyName, new ArrayList<>());
				
				String dataPropertyValue = dataPropertyAssertion.getLiteral().getValue();
				dataProperties.get(dataPropertyName).add(dataPropertyValue);

				result.put(clazzName, dataProperties);
			}
		}
		return result;
	}
	
	public static void main(String[] args) {
//		String path = System.getProperty("user.dir") + "\\resources\\OSDi.owl";
		String path = "H:/Mi unidad/Investigación/Proyectos/OSDi/OSDi.owl";
		try {
			System.out.println("Parsing " + path);
			OwlHelper helper = new OwlHelper(path);
			System.out.println("\nClasses");
			for (String clazz : helper.getClazzes())
				System.out.println(clazz);
			System.out.println("\nObject properties");
			for (String prop : helper.getObjectProperties())
				System.out.println(prop);
			System.out.println("\nData properties");
			for (String prop : helper.getDataProperties())
				System.out.println(prop);
			System.out.println("\nMapping class --> Object properties");
			TreeMap<String, Set<String>> clazzWithOP = helper.getObjectPropertiesByClazz();
			for (String clazz : clazzWithOP.keySet()) {
				System.out.print(clazz);
				for (String prop : clazzWithOP.get(clazz))
					System.out.print("\t" + prop);
				System.out.println();
			}
			System.out.println("\nMapping Object properties --> Class");
			TreeMap<String, Set<String>> objPropRanges = helper.getClazzesByObjectProperty();
			for (String prop : objPropRanges.keySet()) {
				System.out.print(prop);
				for (String clazz : objPropRanges.get(prop))
					System.out.print("\t" + clazz);
				System.out.println();
			}
			System.out.println("\nMapping Data properties --> Class");
			TreeMap<String, Set<String>> clazzWithDP = helper.getDataPropertiesByClazz();
			for (String clazz : clazzWithDP.keySet()) {
				System.out.print(clazz);
				for (String prop : clazzWithDP.get(clazz))
					System.out.print("\t" + prop);
				System.out.println();
			}
			System.out.println("\nInstances --> Class");
			for (String instance : helper.instanceToClazz.keySet()) {
				System.out.println(instance + "\t" + helper.instanceToClazz.get(instance));
			}
		} catch (JAXBException | IOException e) {
			e.printStackTrace();
		}

	}
	public class ClazzHierarchyNode {
		private final String name;
		private final List<ClazzHierarchyNode> descendants;
		private final List<ClazzHierarchyNode> ascendants;
		
		public ClazzHierarchyNode(String name) {
			this.name = name;
			this.descendants = new ArrayList<ClazzHierarchyNode>();
			this.ascendants = new ArrayList<ClazzHierarchyNode>();
			hierarchy.put(name, this);
		}
		public void addAscendant(String ascendant) {
			ClazzHierarchyNode node;
			if (hierarchy.containsKey(ascendant))
				node = hierarchy.get(ascendant);
			else 
				node = new ClazzHierarchyNode(ascendant);
			ascendants.add(node);
			node.descendants.add(this);
		}
		public List<ClazzHierarchyNode> getDescendants() {
			return descendants;
		}
		public List<ClazzHierarchyNode> getAscendants() {
			return ascendants;
		}
		public String getName() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
}
