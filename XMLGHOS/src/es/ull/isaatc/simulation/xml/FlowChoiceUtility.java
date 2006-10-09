/**
 * 
 */
package es.ull.isaatc.simulation.xml;

/**
 * @author Roberto Muñoz
 */
public class FlowChoiceUtility {
	
	
	public static Flow getSelectedFlow(DecisionOption df) {

		if (df.getSingle() != null)
			return df.getSingle();
		if (df.getPackage() != null)
			return df.getPackage();
		if (df.getExit() != null)
			return df.getExit();
		if (df.getSequence() != null)
			return df.getSequence();
		if (df.getSimultaneous() != null)
			return df.getSimultaneous();
		if (df.getDecision() != null)
			return df.getDecision();
		if (df.getType() != null)
			return df.getType();
		return null;
	}
	
	
	public static Flow getSelectedFlow(TypeBranch bf) {

		if (bf.getSingle() != null)
			return bf.getSingle();
		if (bf.getPackage() != null)
			return bf.getPackage();
		if (bf.getExit() != null)
			return bf.getExit();
		if (bf.getSequence() != null)
			return bf.getSequence();
		if (bf.getSimultaneous() != null)
			return bf.getSimultaneous();
		if (bf.getDecision() != null)
			return bf.getDecision();
		if (bf.getType() != null)
			return bf.getType();
		return null;
	}
	
	
	public static Flow getSelectedFlow(FlowChoice fc) {

		if (fc.getSingle() != null)
			return fc.getSingle();
		if (fc.getPackage() != null)
			return fc.getPackage();
		if (fc.getExit() != null)
			return fc.getExit();
		if (fc.getSequence() != null)
			return fc.getSequence();
		if (fc.getSimultaneous() != null)
			return fc.getSimultaneous();
		if (fc.getDecision() != null)
			return fc.getDecision();
		if (fc.getType() != null)
			return fc.getType();
		return null;
	}
}
