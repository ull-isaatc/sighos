/**
 * 
 */
package es.ull.isaatc.simulation.xml.validation;

import java.util.TreeMap;

import es.ull.isaatc.simulation.xml.BaseComponent;
import es.ull.isaatc.simulation.xml.ComponentRef;
import es.ull.isaatc.simulation.xml.DecisionFlow;
import es.ull.isaatc.simulation.xml.DecisionOption;
import es.ull.isaatc.simulation.xml.Flow;
import es.ull.isaatc.simulation.xml.FlowChoiceUtility;
import es.ull.isaatc.simulation.xml.ModelMappingTable;
import es.ull.isaatc.simulation.xml.PackageFlow;
import es.ull.isaatc.simulation.xml.RootFlow;
import es.ull.isaatc.simulation.xml.SequenceFlow;
import es.ull.isaatc.simulation.xml.SimultaneousFlow;
import es.ull.isaatc.simulation.xml.SingleFlow;
import es.ull.isaatc.simulation.xml.TypeBranch;
import es.ull.isaatc.simulation.xml.TypeFlow;

/**
 * @author Roberto Muñoz
 */
public class RootFlowValidate extends DescComponentValidate {
    
    private static String ERROR_TYPE = "Root flow error";

    /** Activity validation object */
    ActivityValidate actVal;

    /** Element type validation object */
    ElementTypeValidate elemTypeVal;
    
    /** Error messages */
    private static final String UNKNOW_TYPE = "Unknown type";
    private static final String UNKNOW_ACTIVITY = "Unknown activity";
    private static final String UNKNOW_ROOTFLOW = "Unknown root flow";

    public RootFlowValidate(ActivityValidate actVal, ElementTypeValidate elemTypeVal, TreeMap<Integer, ModelMappingTable> modelList) {
	super(modelList);
	this.actVal = actVal;
	this.elemTypeVal = elemTypeVal;
    }

    protected boolean checkSingleFlow(SingleFlow flow) {
	return actVal.checkReference(flow.getActRef());
    }

    protected boolean checkPackageFlow(PackageFlow flow) {
	return checkReference(flow.getRootFlowRef());
    }
    
    protected boolean checkTypeBranch(TypeBranch flow) {
	boolean hasError = false;

	for (ComponentRef ref : flow.getElementType())
	    hasError |= !elemTypeVal.checkReference(ref);

	return !hasError;
    }

    public boolean validateFlow(BaseComponent component) {
	boolean hasError = false;

	if (component instanceof SingleFlow) {
	    boolean aux = !checkSingleFlow((SingleFlow) component);
	    if (aux)
		error(component, UNKNOW_ACTIVITY);
	    hasError |= aux; 
	}
	else if (component instanceof PackageFlow) {
	    boolean aux = !checkPackageFlow((PackageFlow) component);
	    if (aux)
		error(component, UNKNOW_ROOTFLOW);
	    hasError |= aux; 
	}
	else if (component instanceof DecisionOption)
	    hasError |= !validateFlow(FlowChoiceUtility.getSelectedFlow((DecisionOption)component));
	else if (component instanceof TypeBranch) {
	    TypeBranch branch = (TypeBranch)component;
	    boolean aux = !checkTypeBranch(branch);
	    if (aux)
		error(component, UNKNOW_TYPE);
	    hasError |= aux;
	    hasError |= !validateFlow(FlowChoiceUtility.getSelectedFlow(branch));
	}
	else if (component instanceof SequenceFlow) {
	    SequenceFlow seq = (SequenceFlow)component;
	    for (Flow f : seq.getSingleOrPackageOrSequence())
		hasError |= !validateFlow(f);
	}
	else if (component instanceof SimultaneousFlow) {
	    SimultaneousFlow sim = (SimultaneousFlow)component;
	    for (Flow f : sim.getSingleOrPackageOrSequence())
		hasError |= !validateFlow(f);
	}
	else if (component instanceof DecisionFlow) {
	    DecisionFlow dec = (DecisionFlow)component;
	    for (Flow f : dec.getOption())
		hasError |= !validateFlow(f);
	}
	else if (component instanceof TypeFlow) {
	    TypeFlow type = (TypeFlow)component;
	    for (Flow f : type.getBranch())
		hasError |= !validateFlow(f);
	}

	return !hasError;
    }

    @Override
    public boolean validate(BaseComponent component) throws ModelException {
	boolean hasError = !super.validate(component);
	hasError |= !validateFlow(FlowChoiceUtility.getSelectedFlow(((RootFlow)component).getFlow()));
	
	if (hasError) {
	    throw new ModelException(ERROR_TYPE);
	}

	return !hasError;
    }

    @Override
    public boolean checkReference(ComponentRef ref) {
	if (ref.getModelId() < 0 || ref.getId() < 0)
	    return false;
	ModelMappingTable map = modelList.get(Integer.valueOf(ref.getModelId()));
	if (map == null)
	    return false;
	if (map.getRootFlow(ref.getId()) == null)
	    return false;
	return true;
    }
}
