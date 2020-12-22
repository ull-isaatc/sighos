package es.ull.iis.simulation.hta.radios.wrappers;

import java.util.ArrayList;
import java.util.List;

import es.ull.iis.ontology.radios.utils.CollectionUtils;
import es.ull.iis.ontology.radios.xml.datatables.ColumnType;
import es.ull.iis.ontology.radios.xml.datatables.Datatable;
import es.ull.iis.ontology.radios.xml.datatables.RowType;
import es.ull.iis.simulation.hta.radios.transforms.XmlTransform;

public class MatrixDistribution {
	private List<IntervalDistribution> intervals;

	public MatrixDistribution(List<IntervalDistribution> intervals) {
		this.intervals = intervals;
	}

	public MatrixDistribution(String datatableStr) {		
		try {
			Datatable datatable = XmlTransform.getDataTable(datatableStr);
			if (datatable != null) {
				List<RowType> rows = datatable.getContent().getRow();
				if (CollectionUtils.notIsEmpty(rows)) {
					for (RowType row : rows) {
						for (ColumnType column : row.getColumn()) {
							System.out.println(String.format("value: %s", column.getValue()));	
						}							
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("ERROR => Error processing probability value.");
		}
	}

	private List<IntervalDistribution> getIntervals () {
		if (this.intervals == null) {
			this.intervals = new ArrayList<>();
		}
		return this.intervals;
	}
	
	public void addInterval(IntervalDistribution interval) {
		// TODO: Insertarlo ordenado
		getIntervals().add(interval);
	}

	public ProbabilityDistribution getValue (Double fromValue) {
		// TODO: recuperar de los intervalos el valor correspondiente
		return intervals.get(0).getProbabilityDistribution();
	}
	
}
