package es.ull.isaatc.mosinet.prototipo.web.webpages.results;

import java.util.List;

import org.jfree.chart.JFreeChart;

import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import es.ull.isaatc.mosinet.prototipo.web.WicketPage;
import es.ull.isaatc.simulation.listener.xml.chart.ListenerChartFactory;

public class ShowResults extends WicketPage {

	/**
	 * 
	 */
	private static final long serialVersionUID = -620477526046860710L;
	
	private ListenerListView listenerListView;
	private List chartList;

	public ShowResults(final String data) {
		//add(new MultiLineLabel("resultsLabel", data));	
		
		add(new Link("xmlResults") {
			public void onClick() {
				setResponsePage(new XmlResults(data));
				//Files.remove(file);
				//refreshFiles();
				//UploadPage.this.info("Deleted " + file);
			}
		});

		chartList = ListenerChartFactory.getChart(data);
		
		listenerListView = new ListenerListView("chartList", chartList);
		add(listenerListView);
	}
	
	/**
	 * List view for files in upload folder.
	 */
	private class ListenerListView extends ListView {
		/**
		 * Construct.
		 * 
		 * @param name
		 *            Component name
		 * @param files
		 *            The file list model
		 */
		public ListenerListView(String name, final List list) {
			super(name, list);
		}

		/**
		 * @see ListView#populateItem(ListItem)
		 */
		protected void populateItem(ListItem listItem) {
			final JFreeChart chart = (JFreeChart) listItem.getModelObject();
			listItem.add(new JFreeChartImage("chart", chart, 500, 250));
			
		}
	}
}
