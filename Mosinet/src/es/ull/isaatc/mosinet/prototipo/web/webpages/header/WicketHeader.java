package es.ull.isaatc.mosinet.prototipo.web.webpages.header;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;

public class WicketHeader extends Panel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5878210821460449356L;

	/**
	 * Construct 
	 * @param id 
	 *   id of the component
	 * @param title
	 *   title of the page
	 * @param page
	 *   page where it's inserted
	 */
	public WicketHeader (String id, String title, WebPage page) {
		super(id);
		add(new Label("title", title));
	
	}
}
