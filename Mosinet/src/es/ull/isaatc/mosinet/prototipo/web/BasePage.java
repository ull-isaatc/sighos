package es.ull.isaatc.mosinet.prototipo.web;

import wicket.markup.html.WebPage;
import wicket.markup.html.link.BookmarkablePageLink;

public class BasePage extends WebPage {

	public BasePage() {
		add(new BookmarkablePageLink("home-link", HomePage.class));
	}
}
