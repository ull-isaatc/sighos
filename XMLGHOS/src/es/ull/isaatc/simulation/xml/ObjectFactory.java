//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b26-ea3 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2006.10.18 at 06:31:27 PM BST 
//

package es.ull.isaatc.simulation.xml;

import javax.xml.bind.annotation.XmlRegistry;
import es.ull.isaatc.simulation.xml.Activity;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup;
import es.ull.isaatc.simulation.xml.Activity.WorkGroup.Role;
import es.ull.isaatc.simulation.xml.ClassReference;
import es.ull.isaatc.simulation.xml.ClassReference.Param;
import es.ull.isaatc.simulation.xml.Cycle;
import es.ull.isaatc.simulation.xml.DecisionFlow;
import es.ull.isaatc.simulation.xml.DecisionOption;
import es.ull.isaatc.simulation.xml.ElementType;
import es.ull.isaatc.simulation.xml.ExitFlow;
import es.ull.isaatc.simulation.xml.Experiment;
import es.ull.isaatc.simulation.xml.Flow;
import es.ull.isaatc.simulation.xml.FlowChoice;
import es.ull.isaatc.simulation.xml.Generation;
import es.ull.isaatc.simulation.xml.Generator;
import es.ull.isaatc.simulation.xml.Model;
import es.ull.isaatc.simulation.xml.ObjectFactory;
import es.ull.isaatc.simulation.xml.PackageFlow;
import es.ull.isaatc.simulation.xml.ProbTree;
import es.ull.isaatc.simulation.xml.RandomNumber;
import es.ull.isaatc.simulation.xml.Resource;
import es.ull.isaatc.simulation.xml.Resource.TimeTable;
import es.ull.isaatc.simulation.xml.Resource.TimeTable.Dur;
import es.ull.isaatc.simulation.xml.ResourceType;
import es.ull.isaatc.simulation.xml.RootFlow;
import es.ull.isaatc.simulation.xml.SequenceFlow;
import es.ull.isaatc.simulation.xml.SimultaneousFlow;
import es.ull.isaatc.simulation.xml.SingleFlow;
import es.ull.isaatc.simulation.xml.TypeBranch;
import es.ull.isaatc.simulation.xml.TypeFlow;

/**
 * This object contains factory methods for each Java content interface and Java
 * element interface generated in the es.ull.isaatc.simulation.xml package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the
 * Java representation for XML content. The Java representation of XML content
 * can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory
 * methods for each of these are provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    /**
         * Create a new ObjectFactory that can be used to create new instances
         * of schema derived classes for package: es.ull.isaatc.simulation.xml
         * 
         */
    public ObjectFactory() {
    }

    /**
         * Create an instance of {@link Resource }
         * 
         */
    public Resource createResource() {
	return new Resource();
    }

    /**
         * Create an instance of {@link Role }
         * 
         */
    public Role createActivityWorkGroupRole() {
	return new Role();
    }

    /**
         * Create an instance of {@link Flow }
         * 
         */
    public Flow createFlow() {
	return new Flow();
    }

    /**
         * Create an instance of {@link ProbTree }
         * 
         */
    public ProbTree createProbTree() {
	return new ProbTree();
    }

    /**
         * Create an instance of {@link Activity }
         * 
         */
    public Activity createActivity() {
	return new Activity();
    }

    /**
         * Create an instance of {@link RootFlow }
         * 
         */
    public RootFlow createRootFlow() {
	return new RootFlow();
    }

    /**
         * Create an instance of {@link ElementType }
         * 
         */
    public ElementType createElementType() {
	return new ElementType();
    }

    /**
         * Create an instance of {@link Param }
         * 
         */
    public Param createClassReferenceParam() {
	return new Param();
    }

    /**
         * Create an instance of {@link ClassReference }
         * 
         */
    public ClassReference createClassReference() {
	return new ClassReference();
    }

    /**
         * Create an instance of {@link DecisionFlow }
         * 
         */
    public DecisionFlow createDecisionFlow() {
	return new DecisionFlow();
    }

    /**
         * Create an instance of {@link Dur }
         * 
         */
    public Dur createResourceTimeTableDur() {
	return new Dur();
    }

    /**
         * Create an instance of {@link TimeTable }
         * 
         */
    public TimeTable createResourceTimeTable() {
	return new TimeTable();
    }

    /**
         * Create an instance of {@link PackageFlow }
         * 
         */
    public PackageFlow createPackageFlow() {
	return new PackageFlow();
    }

    /**
         * Create an instance of {@link TypeBranch }
         * 
         */
    public TypeBranch createTypeBranch() {
	return new TypeBranch();
    }

    /**
         * Create an instance of {@link ResourceType }
         * 
         */
    public ResourceType createResourceType() {
	return new ResourceType();
    }

    /**
         * Create an instance of {@link Cycle }
         * 
         */
    public Cycle createCycle() {
	return new Cycle();
    }

    /**
         * Create an instance of {@link TypeFlow }
         * 
         */
    public TypeFlow createTypeFlow() {
	return new TypeFlow();
    }

    /**
         * Create an instance of {@link Generator }
         * 
         */
    public Generator createGenerator() {
	return new Generator();
    }

    /**
         * Create an instance of {@link RandomNumber }
         * 
         */
    public RandomNumber createRandomNumber() {
	return new RandomNumber();
    }

    /**
         * Create an instance of {@link WorkGroup }
         * 
         */
    public WorkGroup createActivityWorkGroup() {
	return new WorkGroup();
    }

    /**
         * Create an instance of {@link FlowChoice }
         * 
         */
    public FlowChoice createFlowChoice() {
	return new FlowChoice();
    }

    /**
         * Create an instance of {@link DecisionOption }
         * 
         */
    public DecisionOption createDecisionOption() {
	return new DecisionOption();
    }

    /**
         * Create an instance of {@link Experiment }
         * 
         */
    public Experiment createExperiment() {
	return new Experiment();
    }

    /**
         * Create an instance of {@link SingleFlow }
         * 
         */
    public SingleFlow createSingleFlow() {
	return new SingleFlow();
    }

    /**
         * Create an instance of {@link ExitFlow }
         * 
         */
    public ExitFlow createExitFlow() {
	return new ExitFlow();
    }

    /**
         * Create an instance of {@link SimultaneousFlow }
         * 
         */
    public SimultaneousFlow createSimultaneousFlow() {
	return new SimultaneousFlow();
    }

    /**
         * Create an instance of {@link Model }
         * 
         */
    public Model createModel() {
	return new Model();
    }

    /**
         * Create an instance of {@link Generation }
         * 
         */
    public Generation createGeneration() {
	return new Generation();
    }

    /**
         * Create an instance of {@link SequenceFlow }
         * 
         */
    public SequenceFlow createSequenceFlow() {
	return new SequenceFlow();
    }

}
