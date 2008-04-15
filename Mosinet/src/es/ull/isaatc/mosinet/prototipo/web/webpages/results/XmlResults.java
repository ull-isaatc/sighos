package es.ull.isaatc.mosinet.prototipo.web.webpages.results;

import wicket.markup.html.WebPage;
import wicket.markup.html.basic.MultiLineLabel;

public class XmlResults extends WebPage {

	private static final long serialVersionUID = 1251219815822290443L;

	private String xmlResults;
	
	public XmlResults (String xmlResults) {
		this.xmlResults = xmlResults;
		add(new MultiLineLabel("xmlResults", xmlResults));
	}
	
//	@Override
//	public String getMarkupType() {
//	    return "text/xml";
//	}
}
