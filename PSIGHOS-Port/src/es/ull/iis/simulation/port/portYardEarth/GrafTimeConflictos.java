package es.ull.iis.simulation.port.portYardEarth;

import java.awt.Color;
import java.awt.Toolkit; 
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel; 
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation; 
import org.jfree.data.category.DefaultCategoryDataset;


public class GrafTimeConflictos extends JFrame{
    JPanel panel;
    
    public GrafTimeConflictos(){
    	setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Daniel\\Desktop\\TFG General\\logotipo-secundario-ULL.png"));
        setTitle("Tiempo promedio de los conflictos");
        setSize(800,500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setVisible(true);
        init();
    }
    private void init() {
        panel = new JPanel();
        getContentPane().add(panel);
        
        // Fuente de Datos
        DefaultCategoryDataset Promedio = new DefaultCategoryDataset();
        Promedio.addValue(ConflictoListener.promedioEsperaInicioGlobal/60, "Aparcamiento", "Tipo de recurso");
        Promedio.addValue(ConflictoListener.promedioReservaGruaGlobal/60, "Grúa", "Tipo de recurso");
        Promedio.addValue(ConflictoListener.promedioTramo1Global/60, "Tramo1", "Tipo de recurso");
        Promedio.addValue(ConflictoListener.promedioTramo2Global/60, "Tramo2", "Tipo de recurso");
        Promedio.addValue(ConflictoListener.promedioTramo3Global/60, "Tramo3", "Tipo de recurso");
        Promedio.addValue(ConflictoListener.promedioTramo4Global/60, "Tramo4", "Tipo de recurso");
   
        // Creando el Grafico
        JFreeChart chart = ChartFactory.createBarChart3D
        ("Tiempo promedio de los conflictos","Recursos", "Tiempo de los conflictos (minutos)", 
        Promedio, PlotOrientation.VERTICAL, true,true, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setPaint(Color.black); 
        CategoryPlot p = chart.getCategoryPlot(); 
        p.setRangeGridlinePaint(Color.red); 
        // Mostrar Grafico
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel);
    }
}
	

	