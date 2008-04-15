package es.ull.isaatc.mosinet.prototipo.web;

import javax.servlet.http.HttpServletRequest;

import wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.util.file.Folder;
import es.ull.isaatc.mosinet.prototipo.web.webpages.upload.UploadPage;

public class FirstMosinetApp extends WebApplication {
	
    private Folder uploadFolder = new Folder("Mis documentos");
    
    public FirstMosinetApp() {
    }


	@Override
	public Class getHomePage() {
		return UploadPage.class;
	}

    /**
     * @return the folder for uploads
     */
    public Folder getUploadFolder()
    {
        return uploadFolder;
    }

    /**
     * @see org.apache.wicket.examples.WicketExampleApplication#init()
     */
    protected void init()
    {
        getResourceSettings().setThrowExceptionOnMissingResource(false);

        uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "wicket-uploads");
        // Ensure folder exists
        uploadFolder.mkdirs();

        //mountBookmarkablePage("/multi", MultiUploadPage.class);
        mountBookmarkablePage("/single", UploadPage.class);

    }

    /**
     * @see org.apache.wicket.protocol.http.WebApplication#newWebRequest(javax.servlet.http.HttpServletRequest)
     */
    protected WebRequest newWebRequest(HttpServletRequest servletRequest)
    {
        return new UploadWebRequest(servletRequest);
    }
}