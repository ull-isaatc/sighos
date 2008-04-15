package es.ull.isaatc.mosinet.prototipo.web;

import wicket.markup.html.link.Link;


public class HomePage extends BasePage{

	public HomePage() {
		add(new Link("modelos-link") {

			public void onClick() {
				//setResponsePage(new HelloWorld());
			}
		});
	}
}
