package es.ull.isaatc.simulation;

import java.util.ArrayList;

import simkit.random.RandomVariateFactory;

/**
 * @author Iván Castilla Rodríguez
 *
 */
public class TypeBranchMetaFlow extends MetaFlow {
	protected MetaFlow descendant;
	protected ArrayList<ElementType> elementsType;

	public TypeBranchMetaFlow(int id, TypeMetaFlow parent, ElementType elementType) {
		super(id, parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
		this.elementsType = new ArrayList<ElementType>();
		this.elementsType.add(elementType);
	}

	public TypeBranchMetaFlow(int id, TypeMetaFlow parent, ArrayList<ElementType> elementsType) {
		super(id, parent, RandomVariateFactory.getInstance("ConstantVariate", 1));
		this.elementsType = elementsType;
	}

	public boolean getFlow(Flow parentFlow, Element e) {
		return descendant.getFlow(parentFlow, e);
	}

	protected void add(MetaFlow descendant) {
		this.descendant = descendant;
	}

	public boolean hasElementType(ElementType et) {
		return elementsType.contains(et);
	}
}
