package es.ull.isaatc.mosinet.prototipo.web.webpages.results;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import wicket.Resource;
import wicket.markup.html.image.Image;
import wicket.markup.html.image.resource.DynamicImageResource;
import wicket.model.Model;
import wicket.protocol.http.WebResponse;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;

public class JFreeChartImage extends Image {
	
    private int width;
    private int height;

	public JFreeChartImage(String id, JFreeChart chart, int width, int height){
		super(id, new Model(chart));
		this.width = width;
		this.height = height;
	}

	@Override
    protected Resource getImageResource() {
        return new DynamicImageResource(){
			@Override
			protected byte[] getImageData() {
		        JFreeChart chart = (JFreeChart)getModelObject();
		        return toImageData(chart.createBufferedImage(width, height));
			}

			@Override
		    protected void setHeaders(WebResponse response) {
		        if (isCacheable()) {
		            super.setHeaders(response);
		        } else {
		            response.setHeader("Pragma", "no-cache");
		            response.setHeader("Cache-Control", "no-cache");
		            response.setDateHeader("Expires", 0);
		        }
		    }
        };
    }

}