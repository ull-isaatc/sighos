package es.ull.isaatc.simulation.hospital.view;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import es.ull.isaatc.simulation.common.Simulation;
import es.ull.isaatc.simulation.common.TimeStamp;
import es.ull.isaatc.simulation.common.info.ElementActionInfo;
import es.ull.isaatc.simulation.common.info.SimulationEndInfo;
import es.ull.isaatc.simulation.common.info.SimulationInfo;
import es.ull.isaatc.simulation.common.inforeceiver.View;

public class ActivityQueueChartView extends View {

	private int finishedCounter = 0;
	private int startedCounter = 0;
	private XYSeries finishedSeries;
	private XYSeries startedSeries;
	private XYSeries averageSeries;
	private XYSeriesCollection dataset; 
	private JFreeChart chart;
	private ChartFrame frame;
	private long day = 0;
	private long time = 0;
	private final long simulUnit;
	
	public ActivityQueueChartView(Simulation simul, String description) {
		super(simul, description);
		simulUnit = simul.getTimeUnit().convert(TimeStamp.getDay());
		addEntrance(ElementActionInfo.class);
		addEntrance(SimulationEndInfo.class);
		startedSeries = new XYSeries("Gente que llega");
		startedSeries.add(0,0);
		finishedSeries = new XYSeries("Gente que compra");
		finishedSeries.add(0,0);
		averageSeries = new XYSeries("Cola");
		averageSeries.add(0,0);
		dataset = new XYSeriesCollection();
		dataset.addSeries(startedSeries);
		dataset.addSeries(finishedSeries);
		dataset.addSeries(averageSeries);
		chart = ChartFactory.createXYLineChart("Colas para la compra de entradas", // Title
                "Tiempo de simulación", // x-axis Label
                "Número de personas", // y-axis Label
                dataset, // Dataset
                PlotOrientation.VERTICAL, // Plot Orientation
                true, // Show Legend
                true, // Use tooltips
                false // Configure chart to generate URLs?
            );
		frame = new ChartFrame("Colas para la compra de entradas", chart);
		frame.pack();
		frame.setVisible(true);
	}

	@Override
	public void infoEmited(SimulationInfo info) {
		if (info instanceof ElementActionInfo) {
			ElementActionInfo elemInfo = (ElementActionInfo) info;
			switch(elemInfo.getType()) {
			case REQACT: {
				if (elemInfo.getTs() > time) {
					updateFrame(startedCounter, finishedCounter, day);					
				}
				startedCounter++;
				break;
			}
			case STAACT: {
				if (elemInfo.getTs() > time) {
					updateFrame(startedCounter, finishedCounter, day);					
				}
				finishedCounter++;
				break;
			}
			default: break;
			}
		} else if (info instanceof SimulationEndInfo) {
			updateFrame(startedCounter, finishedCounter, day++);
			time += simulUnit;
		} else {
			Error err = new Error("Incorrect info recieved: " + info.toString());
			err.printStackTrace();
		}
	}
	
	private void updateFrame(int startedCounter, int finishedCounter, long simulationTime) {
		
		startedSeries.add(simulationTime, startedCounter);
		finishedSeries.add(simulationTime, finishedCounter);
		averageSeries.add(simulationTime, startedCounter - finishedCounter);
		frame.repaint();
		this.startedCounter = 0;
		this.finishedCounter= 0;
		day++;
		time += simulUnit;
	}
}
