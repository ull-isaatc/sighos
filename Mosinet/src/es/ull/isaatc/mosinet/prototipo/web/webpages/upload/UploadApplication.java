package es.ull.isaatc.mosinet.prototipo.web.webpages.upload;

import javax.servlet.http.HttpServletRequest;

import wicket.extensions.ajax.markup.html.form.upload.UploadWebRequest;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.spring.injection.annot.SpringComponentInjector;
import wicket.util.file.Folder;


/**
 * Application class for upload model and experiment and simulate.
 * 
 * @author Yurena García
 */
public class UploadApplication extends WebApplication
{
    private Folder uploadFolder = null;

    /**
     * Constructor.
     */


    /**
     * @see org.apache.wicket.Application#getHomePage()
     */
    public Class getHomePage()
    {
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
		addComponentInstantiationListener(new SpringComponentInjector(this));

        getResourceSettings().setThrowExceptionOnMissingResource(false);

        uploadFolder = new Folder(System.getProperty("java.io.tmpdir"), "wicket-uploads");
        // Ensure folder exists
        uploadFolder.mkdirs();

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