/**
 * 
 */
package es.ull.iis.ontology;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.util.OWLAPIStreamUtils;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import uk.ac.manchester.cs.owl.owlapi.OWLClassImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLDataPropertyImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLNamedIndividualImpl;
import uk.ac.manchester.cs.owl.owlapi.OWLObjectPropertyImpl;

/**
 * A wrapper for an ontology in OWL. Creates convenient methods that shorten the use of OWLApi.
 * @author Iv�n Castilla
 *
 */
public class OWLOntologyWrapper {
	protected final OWLOntologyManager manager;
	protected final OWLOntology ontology;
	protected final PrefixManager pm;
    protected final OWLDataFactory factory;
    protected final OWLReasoner reasoner;

    /**
	 * Creates a wrapper for the ontology in the file
	 * @param file The file with the ontology 
	 * @throws OWLOntologyCreationException If the ontology cannot be opened
	 */
	public OWLOntologyWrapper(File file, String prefix) throws OWLOntologyCreationException {
		manager = OWLManager.createOWLOntologyManager();
		ontology = manager.loadOntologyFromOntologyDocument(file);
        pm = new DefaultPrefixManager(prefix);
        factory = manager.getOWLDataFactory();
        OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
        reasoner = reasonerFactory.createReasoner(ontology);
        // Ask the reasoner to do all the necessary work now
        reasoner.precomputeInferences();
    }

	/**
	 * Creates a wrapper for the ontology in the file with the specified path
	 * @param path Path to the file with the ontology
	 * @throws OWLOntologyCreationException If the ontology cannot be opened
	 */
	public OWLOntologyWrapper(String path, String prefix) throws OWLOntologyCreationException {
		this(new File(path), prefix);
	}
	public void save() throws OWLOntologyStorageException {
		manager.saveOntology(ontology);
	}

	/**
	 * Adds an individual of a specified class to the ontology, unless the individual already exists
	 * @param classIRI The IRI of the class
	 * @param individualIRI The IRI of the new individual
	 * @return True if the individual was created; false otherwise
	 */
	public boolean addIndividual(String classIRI, String individualIRI) {
        final OWLNamedIndividual owlIndividual = factory.getOWLNamedIndividual(individualIRI, pm);
        final boolean ok = !ontology.containsIndividualInSignature(owlIndividual.getIRI());
		final OWLClass owlClass = factory.getOWLClass(classIRI, pm);			
        final OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(owlClass, owlIndividual);
        manager.addAxiom(ontology, classAssertion);
		return ok;
	}
	
	public void addObjectPropertyValue(String srcIndividualIRI, String objectProperty, String destIndividualIRI) {
        final OWLNamedIndividual owlSrcIndividual = factory.getOWLNamedIndividual(srcIndividualIRI, pm);
        final OWLNamedIndividual owlDestIndividual = factory.getOWLNamedIndividual(destIndividualIRI, pm);
        final OWLObjectProperty owlObjectProperty = factory.getOWLObjectProperty(objectProperty, pm);
        final OWLObjectPropertyAssertionAxiom objectPropertyAssertion =
            factory.getOWLObjectPropertyAssertionAxiom(owlObjectProperty, owlSrcIndividual, owlDestIndividual);
        manager.addAxiom(ontology, objectPropertyAssertion);
		
	}

	public void addDataPropertyValue(String individualIRI, String dataProperty, String value) {
        final OWLNamedIndividual owlIndividual = factory.getOWLNamedIndividual(individualIRI, pm);
        final OWLDataProperty owlDataProperty = factory.getOWLDataProperty(dataProperty, pm);
        final OWLDataPropertyAssertionAxiom dataPropertyAssertion =
            factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, owlIndividual, value);
        manager.addAxiom(ontology, dataPropertyAssertion);
		
	}

	public void addDataPropertyValue(String individualIRI, String dataProperty, String value, OWL2Datatype dataType) {
        final OWLNamedIndividual owlIndividual = factory.getOWLNamedIndividual(individualIRI, pm);
        final OWLDataProperty owlDataProperty = factory.getOWLDataProperty(dataProperty, pm);
        final OWLLiteral literal = factory.getOWLLiteral(value, factory.getOWLDatatype(dataType));
        final OWLAxiom ax = factory.getOWLDataPropertyAssertionAxiom(owlDataProperty, owlIndividual, literal);
        manager.addAxiom(ontology, ax);		
	}
	
	public OWLClass getClass(String classIRI) {
		return factory.getOWLClass(classIRI, pm);
	}
	
	public OWLObjectProperty getObjectProperty(String objectPropIRI) {
		return factory.getOWLObjectProperty(objectPropIRI, pm);
	}
	
	public OWLDataProperty getDataProperty(String dataPropIRI) {
		return factory.getOWLDataProperty(dataPropIRI, pm);
	}
	
	public OWLIndividual getIndividual(String individualIRI) {
		return factory.getOWLNamedIndividual(individualIRI, pm);
	}
	
	public Set<String> individualsToString() {
		final TreeSet<String> set = new TreeSet<>();
		for (OWLNamedIndividual individual : ontology.getIndividualsInSignature()) {
			set.add(individual.getIRI().getShortForm());
		}
		return set;
	}
	
	public Set<String> classesToString() {
		final TreeSet<String> set = new TreeSet<>();
		for (OWLClass clazz : ontology.getClassesInSignature()) {
			set.add(clazz.getIRI().getShortForm());
		}
		return set;
	}
	
	public Set<String> dataPropertiesToString() {
		final TreeSet<String> set = new TreeSet<>();
		for (OWLDataProperty dataProp : ontology.getDataPropertiesInSignature()) {
			set.add(dataProp.getIRI().getShortForm());
		}
		return set;
	}
	
	public Set<String> objectPropertiesToString() {
		final TreeSet<String> set = new TreeSet<>();
		for (OWLObjectProperty objectProp : ontology.getObjectPropertiesInSignature()) {
			set.add(objectProp.getIRI().getShortForm());
		}
		return set;
	}
	
	/**
	 * Returns true if the specified individual is an instance of the specified class (or any of its subclasses)
	 * @param individualIRI The IRI of an individual in the ontology
	 * @param classIRI The IRI of a class in the ontology
	 * @return true if the specified individual is instance of the specified class (or any of its subclasses).
	 */
	public boolean isInstanceOf(String individualIRI, String classIRI) {
		return getIndividuals(classIRI).contains(individualIRI);
	}
	
	/**
	 * Returns a set of individuals belonging at the same time to ALL the specified classes or any of its subclasses
	 * @param classIRIs A collection of IRIs for classes in the ontology
	 * @return a set of individuals belonging at the same time to ALL the specified classes or any of its subclasses
	 */
	public Set<String> getIndividuals(ArrayList<String> classIRIs) {
		if (classIRIs.size() == 0)
			return new TreeSet<>();
		final Set<String> set = getIndividuals(classIRIs.get(0));
		for (int i = 1; i < classIRIs.size(); i++)
			set.retainAll(getIndividuals(classIRIs.get(i)));
		return set;
	}
	
	/**
	 * Returns a set of individuals belonging to the specified class or any of its subclasses
	 * @param classIRI The IRI of a class in the ontology
	 * @return a set of individuals belonging to the specified class or any of its subclasses
	 */
	public Set<String> getIndividuals(String classIRI) {
		final TreeSet<String> set = new TreeSet<>();
		final OWLClass owlClass = factory.getOWLClass(classIRI, pm);
		final NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(owlClass, false);
		final Set<OWLNamedIndividual> individuals = OWLAPIStreamUtils.asSet(individualsNodeSet.entities());
		for (OWLNamedIndividual individual : individuals) {
			set.add(individual.getIRI().getShortForm());
		}
		return set;
	}
	
	public ArrayList<String> getIndividualProperties(String individualIRI, String sep) {
		final ArrayList<String> list = new ArrayList<>();
		for (OWLClassAssertionAxiom axiom : ontology.getAxioms(AxiomType.CLASS_ASSERTION)) {
		    if (axiom.getIndividual().equals(factory.getOWLNamedIndividual(individualIRI, pm))) {
		        OWLClassImpl classExpression = (OWLClassImpl) axiom.getClassExpression();
		        if (classExpression.isAnonymous()) {
		            // Skip anonymous classes
		            continue;
		        }
		        list.add("SUBCLASS_OF" + sep + ((OWLClassImpl)classExpression.asOWLClass()).getIRI().getShortForm());
		    }
		}
		for (OWLObjectPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.OBJECT_PROPERTY_ASSERTION)) {
		    if (axiom.getSubject().equals(factory.getOWLNamedIndividual(individualIRI, pm))) {
		        OWLObjectPropertyImpl property = (OWLObjectPropertyImpl) axiom.getProperty();
		        list.add(property.getIRI().getShortForm() + sep + ((OWLNamedIndividualImpl)axiom.getObject()).getIRI().getShortForm());
		    }
		}
		for (OWLDataPropertyAssertionAxiom axiom : ontology.getAxioms(AxiomType.DATA_PROPERTY_ASSERTION)) {
		    if (axiom.getSubject().equals(factory.getOWLNamedIndividual(individualIRI, pm))) {
		        OWLDataPropertyImpl property = (OWLDataPropertyImpl) axiom.getProperty();
		        list.add(property.getIRI().getShortForm() + sep + axiom.getObject().getLiteral());
		    }
		}		
		return list;
	}
	
	/**
	 * Returns a list of strings representing the values the specified dataProperty has for the specified individual
	 * @param individualIRI An individual in the ontology
	 * @param dataPropIRI A data property in the ontology
	 * @return a list of strings representing the values the specified dataProperty has for the specified individual
	 */
	public ArrayList<String> getDataPropertyValue(String individualIRI, String dataPropIRI) {
		ArrayList<String> list = new ArrayList<>();
		EntitySearcher.getDataPropertyValues(factory.getOWLNamedIndividual(individualIRI, pm), factory.getOWLDataProperty(dataPropIRI, pm), ontology).forEach(item -> {
			list.add(item.getLiteral());
		});
		return list;
	}
	
	/**
	 * Returns a set of strings representing the names of the individuals referenced by the specified objectProperty of specified individual
	 * @param individualIRI An individual in the ontology
	 * @param objectPropIRI An object property in the ontology
	 * @return a set of strings representing the names of the individuals referenced by the specified objectProperty of specified individual
	 */
	public Set<String> getObjectPropertyValue(String individualIRI, String objectPropIRI) {
		TreeSet<String> list = new TreeSet<>();
		EntitySearcher.getObjectPropertyValues(factory.getOWLNamedIndividual(individualIRI, pm), factory.getOWLObjectProperty(objectPropIRI, pm), ontology).forEach(item -> {
			list.add(simplifyIRI(item.toStringID()));
		});
		return list;
	}
	
	/**
	 * Returns a set containing solely those individuals from the passed collection that belongs to the specified class
	 * @param individuals A collection of individual names 
	 * @param classIRI The IRI of a class in the ontology
	 * @return a set containing solely those individuals from the passed collection that belongs to the specified class
	 */
	public Set<String> getIndividualsSubclassOf(Collection<String> individuals, String classIRI) {
		final Set<String> result = getIndividuals(classIRI); 
		result.retainAll(individuals);
		return result;
	}
	
	public void removeIndividualsOfClass(String classIRI) {
		final OWLClass owlClass = factory.getOWLClass(classIRI, pm);
		final NodeSet<OWLNamedIndividual> individualsNodeSet = reasoner.getInstances(owlClass, false);
		final Set<OWLNamedIndividual> individuals = OWLAPIStreamUtils.asSet(individualsNodeSet.entities());
		final OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		for (OWLNamedIndividual individual : individuals) {
			individual.accept(remover);
		}
		manager.applyChanges(remover.getChanges());
		remover.reset();
	}
	
	public void createClassSubClassOf(String classIRI, String superclassIRI) {
		final OWLClassExpression owlSuperClass = factory.getOWLClass(superclassIRI, pm);
		final OWLClass owlClass = factory.getOWLClass(classIRI, pm);
		OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(owlClass, owlSuperClass);
		AddAxiom addAx = new AddAxiom(ontology, ax);
	    manager.applyChange(addAx);
	}
	
	public void changeInstanceToSubclass(String classIRI, String prefix) {
		final Set<String> individuals = getIndividuals(classIRI);
		removeIndividualsOfClass(classIRI);
		for (String ind : individuals) {
			createClassSubClassOf(prefix + ind, classIRI);
		}
	}
	
	/**
	 * Adapted from https://www.geeksforgeeks.org/convert-camel-case-string-to-snake-case-in-java/
	 * @param name
	 */
	public static String camel2SNAKE(String name) {
        // Regular Expression
        final String regex = "([a-z])([A-Z]+)";
 
        // Replacement string
        final String replacement = "$1_$2";
 
        // Replace the given regex
        // with replacement string
        // and convert it to upper case.
        return name.replaceAll(regex, replacement).toUpperCase();
	}

	/**
	 * A convenient method to get just the name from IRIs. It depends on the ontology prefix, so it must be overriden by subclasses.
	 * @param IRI Original complex IRI
	 * @return The simplified IRI 
	 */
	public String simplifyIRI(String IRI) {
		return IRI;
	}
	
	public void printIndividuals(boolean full) {
		if (full) {
			for (String individual : individualsToString()) {
				final ArrayList<String> props = getIndividualProperties(individual, "\t");
				for (String prop : props)
					System.out.println(individual + "\t" + prop);
			}
		}
		else  {
			for (String individual : individualsToString())
				System.out.println(individual);
		}
	}

	public void printIndividuals(String classIRI, boolean full) {
		if (full) {
			for (String individual : getIndividuals(classIRI)) {
				final ArrayList<String> props = getIndividualProperties(individual, "\t");
				for (String prop : props)
					System.out.println(individual + "\t" + prop);
			}
		}
		else  {
			for (String individual : getIndividuals(classIRI))
				System.out.println(individual);
		}
	}
	
	public void printClasses() {
		for (String clazz: classesToString())
			System.out.println(clazz);
	}
	
	public void printClassesAsEnum() {
		for (String name : classesToString()) {
			System.out.println(camel2SNAKE(name) + "(\"" + name + "\"),");
		}
	}

	public void printDataProperties() {
		for (String dataProp: dataPropertiesToString())
			System.out.println(dataProp);
	}
	
	public void printDataPropertiesAsEnum() {
		for (String name : dataPropertiesToString()) {
			System.out.println(camel2SNAKE(name) + "(\"" + name + "\"),");
		}
	}

	public void printObjectProperties() {
		for (String objectProp: objectPropertiesToString())
			System.out.println(objectProp);
	}
	
	public void printObjectPropertiesAsEnum() {
		for (String name : objectPropertiesToString()) {
			System.out.println(camel2SNAKE(name) + "(\"" + name + "\"),");
		}
	}
	
	
}
