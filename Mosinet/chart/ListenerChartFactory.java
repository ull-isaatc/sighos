package es.ull.isaatc.simulation.listener.xml.chart;

import java.awt.GridLayout;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.SchemaFactory;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.xml.sax.SAXException;

import es.ull.isaatc.simulation.listener.xml.ActivityListener;
import es.ull.isaatc.simulation.listener.xml.ActivityTimeListener;
import es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener;
import es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener;
import es.ull.isaatc.simulation.listener.xml.ListenerInfo;
import es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener;
import es.ull.isaatc.simulation.listener.xml.SelectableActivityListener;
import es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener;
import es.ull.isaatc.simulation.listener.xml.SimulationListener;
import es.ull.isaatc.simulation.listener.xml.SimulationTimeListener;
import es.ull.isaatc.simulation.xml.SighosValidationEventHandler;

public class ListenerChartFactory {

	/** 
	 * Show the charts from the simulation
	 * @param listenerInfo
	 */
	public static void getChart(String listenerInfo) {
		ListenerInfo listInfo = (ListenerInfo) unmarshallObject(
				new StringReader(listenerInfo), // reader
				"es.ull.isaatc.simulation.listener.xml", // contextPath
				"Listeners.xsd"); // systemResource)
		List<SimulationListener> listeners = listInfo.getAverageResults().getListener();
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();
		
		// for each simulation
		for (SimulationListener simListener : listeners) {
			// depend of the listener instance get the corresponding chart/s
			if (simListener instanceof es.ull.isaatc.simulation.listener.xml.ActivityListener)
				chartList.addAll(getChartActivityListener((ActivityListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.SelectableActivityListener)
				chartList.addAll(getChartSelectableActivityListener((SelectableActivityListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.ActivityTimeListener)
				chartList.addAll(getChartActivityTimeListener((ActivityTimeListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener)
				chartList.addAll(getChartSelectableActivityTimeListener((SelectableActivityTimeListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener)
				chartList.addAll(getChartElementStartFinishListener((ElementStartFinishListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.SimulationTimeListener)
				chartList.addAll(getChartSimulationTimeListener(listInfo));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener)
				chartList.addAll(getChartElementTypeTimeListener((ElementTypeTimeListener) simListener));
			else if (simListener instanceof es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener)
				chartList.addAll(getChartResourceStdUsageListener((ResourceStdUsageListener) simListener));
		}
		showChart(chartList);
	}

	/**
	 * create a frame and show the simulation listener charts
	 * @see QUITAR ESTO DE AQUI
	 * @param chartList
	 */
	public static void showChart(ArrayList<JFreeChart> chartList) {
		JFrame frame = new JFrame("Resultado de la simulación");
		JPanel panel = new JPanel(new GridLayout(0, 2));
		for (JFreeChart chart : chartList) {
			panel.add(new ChartPanel(chart));
		}
		frame.add(panel);

		// create and display a frame
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
	}

	/**
	 * @return the ActivityListener Charts
	 */
	public static ArrayList<JFreeChart> getChartActivityListener(
			es.ull.isaatc.simulation.listener.xml.ActivityListener listener) {

		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection dataQueue = new XYSeriesCollection();
		XYSeriesCollection dataPerformed = new XYSeriesCollection();

		// Reads the queue
		for (es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActQueue " + a.getActId());
			es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActQueue actQ = a.getActQueue();
			for (int i = 0; i < actQ.getQueue().size(); i++) {
				serie.add(i, actQ.getQueue().get(i));
			}
			dataQueue.addSeries(serie);
		}
		// Queue graph creation
		JFreeChart chartQueue = ChartFactory.createXYLineChart(
				"ActivityListener Queue", // title
				"simulation time", // xAxisLabel
				"queue", // yAxisLabel
				dataQueue, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chartQueue);

		// Reads the performed activities
		for (es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActPerformed " + a.getActId());
			es.ull.isaatc.simulation.listener.xml.ActivityListener.Activity.ActPerformed actP = a.getActPerformed();
			for (int i = 0; i < actP.getPerformed().size(); i++) {
				serie.add(i, actP.getPerformed().get(i));
			}
			dataPerformed.addSeries(serie);
		}

		// Performed graph creation
		JFreeChart chartPerformed = ChartFactory.createXYLineChart(
				"ActivityListener Performed", // title
				"simulation time", // xAxisLabel
				"performed", // yAxisLabel
				dataPerformed, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chartPerformed);
		return chartList;
	}

	/**
	 * @return the SelectableActivityListener Charts
	 */
	public static ArrayList<JFreeChart> getChartSelectableActivityListener(
			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener listener) {
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection dataQueue = new XYSeriesCollection();
		XYSeriesCollection dataPerformed = new XYSeriesCollection();

		// Reads the queue
		for (es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActQueue " + a.getActId());
			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActQueue actQ = a.getActQueue();
			for (int i = 0; i < actQ.getQueue().size(); i++) {
				serie.add(i, actQ.getQueue().get(i));
			}
			dataQueue.addSeries(serie);
		}
		// Queue graph creation
		JFreeChart chartQueue = ChartFactory.createXYLineChart(
				"SelectableActivityListener Queue", // title
				"simulation time", // xAxisLabel
				"queue", // yAxisLabel
				dataQueue, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chartQueue);

		// Reads the performed activities
		for (es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActPerformed " + a.getActId());
			es.ull.isaatc.simulation.listener.xml.SelectableActivityListener.Activity.ActPerformed actP = a.getActPerformed();
			for (int i = 0; i < actP.getPerformed().size(); i++) {
				serie.add(i, actP.getPerformed().get(i));
			}
			dataPerformed.addSeries(serie);
		}

		// Performed graph creation
		JFreeChart chartPerformed = ChartFactory.createXYLineChart(
				"SelectableActivityListener Performed", // title
				"simulation time", // xAxisLabel
				"performed", // yAxisLabel
				dataPerformed, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);

		chartList.add(chartPerformed);

		return chartList;
	}

	/**
	 * @return the ActivityTimeListener Chart
	 */
	public static ArrayList<JFreeChart> getChartActivityTimeListener(
			es.ull.isaatc.simulation.listener.xml.ActivityTimeListener listener) {
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection dataTime = new XYSeriesCollection();

		// Reads the activity time
		for (es.ull.isaatc.simulation.listener.xml.ActivityTimeListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActTime " + a.getActId());
			for (int i = 0; i < a.getTime().size(); i++) {
				serie.add(i, a.getTime().get(i));
			}
			dataTime.addSeries(serie);
		}
		// Time graph creation
		JFreeChart chartTime = ChartFactory.createXYLineChart(
				"ActivityTimeListener", // title
				"simulation time", // xAxisLabel
				"time", // yAxisLabel
				dataTime, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chartTime);
		return chartList;
	}

	/**
	 * @return the SelectableActivityTimeListener
	 */
	public static ArrayList<JFreeChart> getChartSelectableActivityTimeListener(
			es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener listener) {
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection dataTime = new XYSeriesCollection();

		// Reads the activity time
		for (es.ull.isaatc.simulation.listener.xml.SelectableActivityTimeListener.Activity a : listener.getActivity()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ActTime " + a.getActId());

			for (int i = 0; i < a.getTime().size(); i++) {
				serie.add(i, a.getTime().get(i));
			}
			dataTime.addSeries(serie);
		}
		// Time graph creation
		JFreeChart chartTime = ChartFactory.createXYLineChart(
				"SelectableActivityTimeListener", // title
				"simulation time", // xAxisLabel
				"time", // yAxisLabel
				dataTime, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);

		chartList.add(chartTime);

		return chartList;
	}

	/**
	 * @return the ElementStartFinishListener Chart
	 */
	public static ArrayList<JFreeChart> getChartElementStartFinishListener(
			es.ull.isaatc.simulation.listener.xml.ElementStartFinishListener listener) {
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection data = new XYSeriesCollection();
		XYSeries serieCreated = new XYSeries("Created Elements ");
		XYSeries serieFinished = new XYSeries("Finished Elements ");

		// Reads the created elements
		for (int i = 0; i < listener.getCreated().getValue().size(); i++) {
			serieCreated.add(i, listener.getCreated().getValue().get(i));
			serieFinished.add(i, listener.getFinished().getValue().get(i));
		}
		data.addSeries(serieCreated);
		data.addSeries(serieFinished);

		// Created chart
		JFreeChart chart = ChartFactory.createXYLineChart(
				"ElementStartFinishListener", // title
				"simulation time", // xAxisLabel
				"elements", // yAxisLabel
				data, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chart);
		return chartList;
	}

	/**
	 * @return the ActivityTimeListener Chart
	 */
	public static ArrayList<JFreeChart> getChartElementTypeTimeListener(
			es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener listener) {
		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		XYSeriesCollection dataTime = new XYSeriesCollection();

		// Reads the activity time
		for (es.ull.isaatc.simulation.listener.xml.ElementTypeTimeListener.Et et: listener.getEt()) {
			// One XYSeries per Activity
			XYSeries serie = new XYSeries("ElemType " + et.getId());

			for (int i = 0; i < et.getWorkTime().getValue().size(); i++) {
				serie.add(i, et.getWorkTime().getValue().get(i));
			}
			dataTime.addSeries(serie);
		}
		// Time graph creation
		JFreeChart chartTime = ChartFactory.createXYLineChart(
				"ElementTypeTimeListener", // title
				"simulation time", // xAxisLabel
				"time", // yAxisLabel
				dataTime, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		chartList.add(chartTime);
		return chartList;
	}
	
	/**
	 * @return the SimulationTimeListener Chart
	 */
	public static ArrayList<JFreeChart> getChartSimulationTimeListener(
			ListenerInfo listeners) {

		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// Init graph objects
		DefaultCategoryDataset dataTime = new DefaultCategoryDataset();
		
		// Save the mean of the simulation time
		double mean = 0;
		
		// One bar per simulation
		for (int i = 0; i < listeners.getSimulationResults().size(); i++) {
			for (SimulationListener list : listeners.getSimulationResults().get(i).getListener()) {
				if (list instanceof es.ull.isaatc.simulation.listener.xml.SimulationTimeListener) {
					SimulationTimeListener simList = (SimulationTimeListener)list;
					dataTime.addValue(simList.getSimulationTime(), "Simulation Time", "simul " + i);
					mean += simList.getSimulationTime();
				}
			}
		}
		// Bar chart creation
		JFreeChart chartTime = ChartFactory.createBarChart(
				"SimulationTimeListener", // title
				"simulation time", // xAxisLabel
				"time", // yAxisLabel
				dataTime, // dataset
				PlotOrientation.VERTICAL, // orientation
				true, // legend?
				true, // tooltips?
				false // URLs?
				);
		BarRenderer renderer = ((BarRenderer)((CategoryPlot)chartTime.getPlot()).getRenderer());
		renderer.setMaximumBarWidth(0.02);
		chartList.add(chartTime);
		
		// Mean line creation
		mean /= listeners.getSimulationResults().size();
		DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();

		for (int i = 0; i < listeners.getSimulationResults().size(); i++)
			lineDataset.addValue(mean, "Mean", "simul " + i);
		
		CategoryPlot plot = chartTime.getCategoryPlot();
		

		plot.setDataset(1, lineDataset);
		LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer(true, false);
		lineRenderer.setPlot(plot);
		
		CategoryAxis axis = plot.getDomainAxis();
		axis.clearCategoryLabelToolTips();
		
		//lineRenderer.setBaseShapesFilled(false);
		plot.setRenderer(1, lineRenderer);
		
		return chartList;
	}
	
	/**
	 * @return the ActivityTimeListener Chart
	 */
	public static ArrayList<JFreeChart> getChartResourceStdUsageListener(
			es.ull.isaatc.simulation.listener.xml.ResourceStdUsageListener listener) {

		ArrayList<JFreeChart> chartList = new ArrayList<JFreeChart>();

		// for each resource
		for (ResourceStdUsageListener.Resource resource : listener.getResource()) {
			
			// Dataset for the Usage Bars
			DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
			for (ResourceStdUsageListener.Resource.Usage.Rt rt : resource.getUsage().getRt()) {
				for (int i = 0; i < rt.getValue().size(); i++) {
					barDataset.addValue(rt.getValue().get(i), "usage rt " + rt.getId(), Integer.toString(i));
				}
			}
			
			// Dataset for the Availability Lines
			DefaultCategoryDataset lineDataset = new DefaultCategoryDataset();
			for (ResourceStdUsageListener.Resource.Available.Rt rt: resource.getAvailable().getRt()) {
				for (int i = 0; i < rt.getValue().size(); i++) {
					lineDataset.addValue(rt.getValue().get(i), "available rt " + rt.getId(), Integer.toString(i));
				}
			}
						
			// create the line chart
			JFreeChart chart = ChartFactory.createBarChart(
					"ResourceStdUsage id = " + resource.getId(), // chart title
					"Simulation Time", // range axis label
					"Usage", // domain axis label
					barDataset, // data
					PlotOrientation.VERTICAL, // orientation
					true, // include legend
					true, // tooltips
					false // urls
			);
			
			CategoryPlot plot = chart.getCategoryPlot();
			BarRenderer barRenderer = (BarRenderer)plot.getRenderer();
			barRenderer.setMaximumBarWidth(0.02);
			CategoryAxis axis = plot.getDomainAxis();
			
			axis.setLowerMargin(0.02); // two percent
			axis.setCategoryMargin(0.2); // ten percent
			axis.setUpperMargin(0.02); // two percent
			barRenderer.setItemMargin(0.03);
			
			NumberAxis axisBar = new NumberAxis("Availability");
			plot.setRangeAxis(1, axisBar);
			plot.setRangeAxisLocation(1, AxisLocation.BOTTOM_OR_RIGHT);
			plot.setDataset(1, lineDataset);
			LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
			lineRenderer.setPlot(plot);
			plot.setRenderer(1, lineRenderer);

			chartList.add(chart);
		}
		return chartList;
	}

	public static Object unmarshallObject(Reader reader, String contextPath,
			String systemResource) {
		Object experiment = null;
		try {
			JAXBContext jc = JAXBContext.newInstance(contextPath);
			Unmarshaller u = jc.createUnmarshaller();
			u.setEventHandler(new SighosValidationEventHandler());
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			u.setSchema(schemaFactory.newSchema(new File(
									"C:/Documents and Settings/woo tale woe/Escritorio/ProyectoSpring/sighosws/XMLGHOS/src/es/ull/isaatc/simulation/listener/xml/"
									+ systemResource)));
			// u.setSchema(schemaFactory.newSchema(URLClassLoader.getSystemResource(systemResource)));
			experiment = u.unmarshal(reader);
		} catch (JAXBException je) {
			System.out.println("ERROR : Error found in one of the XML files");
			je.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return experiment;
	}
}
