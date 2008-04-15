package es.ull.isaatc.mosinet.prototipo.web;

import wicket.markup.html.WebPage;
import wicket.model.IModel;
import wicket.util.string.Strings;
import es.ull.isaatc.mosinet.prototipo.web.webpages.header.WicketHeader;

/**
 * Base class for all example pages.
 * 
 * @author Yurena García
 */
public class WicketPage extends WebPage {

	private static final long serialVersionUID = 7462977474395759970L;

	/**
	 * Constructor
	 */
	public WicketPage()	{
		this(null);
	}

	/**
	 * Constructor
	 * 
	 * @param model
	 */
	public WicketPage(IModel model)	{
		super(model);
		final String packageName = getClass().getPackage().getName();
		add(new WicketHeader("mainNavigation", Strings.afterLast(packageName, '.'), this));
		explain();
	}

	/**
	 * Override base method to provide an explanation
	 */
	protected void explain()
	{
	}
}
