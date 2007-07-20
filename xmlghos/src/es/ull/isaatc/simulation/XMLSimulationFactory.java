/**
 * 
 */
package es.ull.isaatc.simulation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.TreeMap;
import java.util.TreeSet;

import simkit.random.RandomVariate;
import simkit.random.RandomVariateFactory;

import es.ull.isaatc.function.*;
import es.ull.isaatc.util.*;

import es.ull.isaatc.simulation.xml.FunctionChoiceUtility;

/**
 * This factory has the methods for creating the simulation components
 * from definitions in XML.
 * @author Roberto Muñoz
 */
public final class XMLSimulationFactory {
	/**
	 * Relation between time periods (second, minute, hour, day, month, year)
	 */
	private static final double timeRelations[] = { 1, 60, 60, 24, 30, 12, 1 };

	/**
	 * Creates a simulation from a definition in XML
	 * @param xmlWrapper
	 * @param out
	 * @return the Simulation
	 */
	public static Simulation getSimulation(int id, es.ull.isaatc.simulation.xml.XMLWrapper xmlWrapper) {
		return new XMLSimulation(id, xmlWrapper);
	}
	
	/**
	 * Creates a resource type from a definition in XML.
	 * @param simul the simulation
	 * @param rtXML the XML definition of the resource type
	 * @return the simulation resource type
	 */
	public static ResourceType getResourceType(Simulation simul, es.ull.isaatc.simulation.xml.ResourceType rtXML) {
		return new ResourceType(rtXML.getId(), simul, rtXML.getDescription());
	}
	
	/**
	 * Creates a group of resources from a definition in XML.
	 * @param simul the simulation
	 * @param resXML the XML definition of the resource
	 * @return the simulation resources
	 */
	public static Collection<Resource> getResource(Simulation simul, es.ull.isaatc.simulation.xml.Resource resXML, int baseTimeIndex) {
		Collection<Resource> resCollection = new TreeSet<Resource>();
		TreeMap<Integer, ResourceType> rtList = simul.getResourceTypeList();
		
		for (int i = 0; i < resXML.getUnits(); i++) {
			if (resXML.getTimeTable().size() > 0) {
				Resource res = new Resource(resXML.getId() + i, simul, resXML.getDescription());
				// Insert each time table entry as several roles entries
				for (es.ull.isaatc.simulation.xml.Resource.TimeTable timeTable : resXML.getTimeTable()) {
					ArrayList<ResourceType> roles = new ArrayList<ResourceType>();
					for (es.ull.isaatc.simulation.xml.ComponentRef resTypeRef : timeTable.getRtRef())
						roles.add(rtList.get(resTypeRef.getId()));
					res.addTimeTableEntry(
							createCycle(timeTable.getCycle(), baseTimeIndex),
							(int) (timeTable.getDur().getValue() *
									getTimeRelation(
											getTimeUnit(timeTable.getDur().getTimeUnit(), baseTimeIndex),
											baseTimeIndex)),
							roles);
				}
				resCollection.add(res);
			}
		}
		return resCollection;
	}
	
	/**
	 * Creates an activity from a definition in XML.
	 * @param simul the simulation
	 * @param actXML the XML definition of the activity
	 * @param baseTimeIndex the base time unit used in the model
	 * @return the simulation activity
	 */
	public static Activity getActivity(Simulation simul, es.ull.isaatc.simulation.xml.Activity actXML, int baseTimeIndex) {
		// check the activity modifiers
		EnumSet<Activity.Modifier> modifiers = EnumSet.noneOf(Activity.Modifier.class);
		if (!actXML.isPresential())
			modifiers.add(Activity.Modifier.NONPRESENTIAL);
		if (actXML.isInterrumpible())
			modifiers.add(Activity.Modifier.INTERRUPTIBLE);
		
		Activity act = new Activity(actXML.getId(), simul, actXML.getDescription(), actXML.getPriority(), modifiers);
		// create the activity workgroups
		for (es.ull.isaatc.simulation.xml.Activity.WorkGroup wgXML : actXML.getWorkGroup()) {
			int wgId;
			if (wgXML.getWorkGroup() != null) {
				wgId = act.addWorkGroup(createPeriod(FunctionChoiceUtility.getSelectedFunction(wgXML.getDuration()),
						getTimeUnit(wgXML.getTimeUnit(), baseTimeIndex), baseTimeIndex), wgXML.getPriority(),
						simul.getWorkGroup(wgXML.getWorkGroup().getId()));
			}
			else {
				wgId = act.addWorkGroup(createPeriod(FunctionChoiceUtility.getSelectedFunction(wgXML.getDuration()),
						getTimeUnit(wgXML.getTimeUnit(), baseTimeIndex), baseTimeIndex), wgXML.getPriority());
				// Insert each resource type entry for this WorkGroup
				for (es.ull.isaatc.simulation.xml.WorkGroup.Role role : wgXML.getDefinition().getRole())
					act.addWorkGroupEntry(wgId, simul.getResourceType(role.getRtRef().getId()), role.getUnits());
			}
		}
		return act;
	}

	public static WorkGroup getWorkGroup(Simulation simul, es.ull.isaatc.simulation.xml.WorkGroup wgXML) {
	
		WorkGroup wg = new WorkGroup(wgXML.getId(), simul, wgXML.getDescription());
		for (es.ull.isaatc.simulation.xml.WorkGroup.Role role : wgXML.getRole())
			wg.add(simul.getResourceType(role.getRtRef().getId()), role.getUnits());
		return wg;
	}
	
	
	/**
	 * Creates an element type from a definition in XML.
	 * @param simul the simulation
	 * @param etXML the XML definition of the element type
	 * @return the simulation element type
	 */
	public static ElementType getElementType(Simulation simul, es.ull.isaatc.simulation.xml.ElementType etXML) {
		return new ElementType(etXML.getId(), simul, etXML.getDescription(), etXML.getPriority());
	}
	
	/**
	 * Creates a cycle froma definition in XML
	 * @param xmlCycle the XML definition of the cycle
	 * @param baseTimeIndex base time unit used in the model
	 * @return the cycle
	 */
	public static Cycle createCycle(es.ull.isaatc.simulation.xml.Cycle xmlCycle, int baseTimeIndex) {
		if (xmlCycle.getType() == es.ull.isaatc.simulation.xml.CycleType.PERIODIC) {
			int cycleTimeUnitIndex = getTimeUnit(xmlCycle.getTimeUnit(), baseTimeIndex);
			es.ull.isaatc.function.TimeFunction rn = createPeriod(
					FunctionChoiceUtility.getSelectedFunction(xmlCycle.getPeriod()), cycleTimeUnitIndex, baseTimeIndex);
			double relationTime = getTimeRelation(cycleTimeUnitIndex, baseTimeIndex);
			double startTs = xmlCycle.getStartTs() * relationTime;
			
			if (xmlCycle.getEndTs() != null) {
				double endTs = xmlCycle.getEndTs() * relationTime;
				if (xmlCycle.getSubCycle() == null) {
					return new PeriodicCycle(startTs, rn, endTs);
				} else {
					return new PeriodicCycle(startTs, rn, endTs, createCycle(xmlCycle.getSubCycle(), baseTimeIndex));
				}
			} else {
				if (xmlCycle.getSubCycle() == null) {
					return new PeriodicCycle(startTs, rn, xmlCycle.getIterations());
				} else {
					return new PeriodicCycle(startTs, rn, xmlCycle.getIterations(), createCycle(xmlCycle.getSubCycle(), baseTimeIndex));
				}
			}
		} else if (xmlCycle.getType() == es.ull.isaatc.simulation.xml.CycleType.TABLE) {
			double ts[] = new double[xmlCycle.getTs().size()];
			int i = 0;
			for (double j : xmlCycle.getTs())
				ts[i++] = j;

			if (xmlCycle.getSubCycle() == null) {
				return new TableCycle(ts);
			} else {
				return new TableCycle(ts,
						createCycle(xmlCycle.getSubCycle(), baseTimeIndex));
			}
		}
		return null;
	}

	/**
	 * Creates a TimeFunction from a definition in XML.
	 * @param tfXML
	 * @param periodIndex
	 * @param baseIndex
	 * @return the TimeFunction
	 */
	public static TimeFunction createPeriod(es.ull.isaatc.simulation.xml.TimeFunction tfXML, int periodIndex, int baseIndex) {
		double value = getTimeRelation(periodIndex, baseIndex);
		return createFunction(tfXML, value);
	}
	
	/**
	 * Returns a TimeFunction from a definition in XML
	 * @param tfXML
	 * @return the TimeFunction or null if the XML description does not represent a TimeFunction
	 */
	public static es.ull.isaatc.function.TimeFunction createFunction(es.ull.isaatc.simulation.xml.TimeFunction tfXML, double k) {
		TimeFunction tf = null;

		if (tfXML instanceof es.ull.isaatc.simulation.xml.RandomNumber) {
			tf = createRandomFunction((es.ull.isaatc.simulation.xml.RandomNumber) tfXML, k);
		} else if (tfXML instanceof es.ull.isaatc.simulation.xml.PolyFunction) {
			tf = createPolynomicFunction((es.ull.isaatc.simulation.xml.PolyFunction) tfXML);
		} else if (tfXML instanceof es.ull.isaatc.simulation.xml.ConstantFunction) {
			tf = new es.ull.isaatc.function.ConstantFunction(
					((es.ull.isaatc.simulation.xml.ConstantFunction) tfXML).getValue());
		}
		return tf;
	}

	/**
	 * Creates an object that represents a PolynomicFunction
	 * @param pfXML polynomic function described in XML
	 * @return PolynomicFunction object
	 */
	public static PolynomicFunction createPolynomicFunction(
			es.ull.isaatc.simulation.xml.PolyFunction pfXML) {
		es.ull.isaatc.function.TimeFunction params[] = new TimeFunction[pfXML.getParam().size()];
		int i = 0;

		for (es.ull.isaatc.simulation.xml.FunctionChoice fChoiceXML : pfXML.getParam()) {
			params[i++] = createFunction(FunctionChoiceUtility.getSelectedFunction(fChoiceXML), 1.0);
		}
		return new PolynomicFunction(params);
	}

	/**
	 * Creates an object that represents a RandomFunction
	 * @param rnXML probabiblity distribution expressed in XML
	 * @return TimeFunction object
	 */
	public static TimeFunction createRandomFunction(es.ull.isaatc.simulation.xml.RandomNumber rnXML, double k) {
		return TimeFunctionFactory.getInstance("RandomFunction", createCompoundRandomNumber(rnXML, k));
	}

	/**
	 * Creates an object that represents a probability distribution
	 * @param crnXML probabiblity distribution expressed in XML
	 * @return RandomNumber subclass
	 */
	public static RandomVariate createCompoundRandomNumber(es.ull.isaatc.simulation.xml.RandomNumber crnXML, double k) {
		// FIXME: There is no data integrity check
		if (crnXML == null)
			return RandomVariateFactory.getInstance("ConstantVariate", 1);
		if (crnXML.getFunction() != null)
			return createRandomNumber(crnXML, k);
		if (crnXML.getOp().equals(es.ull.isaatc.simulation.xml.Operation.ADDITION))
			return RandomVariateFactory.getInstance("ConvolutionVariate",   
					createCompoundRandomNumber(crnXML.getOperand().get(0), k),
					createCompoundRandomNumber(crnXML.getOperand().get(1), k));
//		if (crnXML.getOp().equals(es.ull.isaatc.simulation.xml.Operation.MULTIPLICATION))
//			return new es.ull.isaatc.random.MultRandomNumber(
//					createCompoundRandomNumber(crnXML.getOperand().get(0), k),
//					createCompoundRandomNumber(crnXML.getOperand().get(1), k));
		return null;
	}

	private static RandomVariate createRandomNumber(es.ull.isaatc.simulation.xml.RandomNumber rnXML, double k) {

		if (rnXML == null)
			return RandomVariateFactory.getInstance("ConstantVariate", 1);
		else if (rnXML.getFunction().equals("DiscreteVariate"))
			return RandomVariateFactory.getInstance("DiscreteVariate", rnXML.getParam().toArray());
		else {
			es.ull.isaatc.simulation.xml.RandomNumber newRn = new es.ull.isaatc.simulation.xml.RandomNumber();
			newRn.setFunction(rnXML.getFunction());
			for (double i : rnXML.getParam())
				newRn.getParam().add(i * k);
			
			return RandomVariateFactory.getInstance(newRn.getFunction(), newRn.getParam().toArray());
		}

	}	
	
	/**
	 * Returns the index in the timeRelations vector for the value
	 */
	public static int getTimeUnit(es.ull.isaatc.simulation.xml.CommonFreq value, int baseTimeIndex) {
		if (value == null)  // if the value is null then uses the baseTime
			return baseTimeIndex;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.YEAR))
			return 6;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.MONTH))
			return 5;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.DAY))
			return 4;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.HOUR))
			return 3;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.MINUTE))
			return 2;
		if (value.equals(es.ull.isaatc.simulation.xml.CommonFreq.SECOND))
			return 1;
		return baseTimeIndex;
	}

	/**
	 * Returns the relation value between the two parameters
	 * 
	 * @param valueIndex
	 * @param baseIndex
	 */
	private static double getTimeRelation(int valueIndex, int baseIndex) {
		double value = 1;
		
		for (int i = baseIndex; i < valueIndex; i++)
			value *= timeRelations[i];
		return value;
	}
	
	/**
	 * Returns a time value expressed in the base unit relatio
	 * @param value time value
	 * @param valueIndex 
	 * @param baseIndex
	 * @return
	 */
	public static double getNormalizedTime(double value, es.ull.isaatc.simulation.xml.CommonFreq valueTimeUnit, int baseIndex) {
		return value * getTimeRelation(getTimeUnit(valueTimeUnit, baseIndex), baseIndex);
	}
}
