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

public class GrafNumConflictos extends JFrame{
	JPanel panel;
    public GrafNumConflictos(){
    	setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Daniel\\Desktop\\TFG General\\logotipo-secundario-ULL.png"));
        setTitle("Número de conflictos por recursos");
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
        DefaultCategoryDataset conflictos = new DefaultCategoryDataset();
        conflictos.addValue(ConflictoListener.contadorEsperaInicioGlobal, "Aparcamiento", "Tipo de recurso");
        conflictos.addValue(ConflictoListener.contadorReservaGruaGlobal, "Grúa", "Tipo de recurso");
        conflictos.addValue(ConflictoListener.contadorTramo1Global, "Tramo1", "Tipo de recurso");
        conflictos.addValue(ConflictoListener.contadorTramo2Global, "Tramo2", "Tipo de recurso");
        conflictos.addValue(ConflictoListener.contadorTramo3Global, "Tramo3", "Tipo de recurso");
        conflictos.addValue(ConflictoListener.contadorTramo4Global, "Tramo4", "Tipo de recurso");
        
        // Creando el Grafico
        JFreeChart chart = ChartFactory.createBarChart3D
        ("Número de conflictos por recurso","Recursos", "Número de conflictos (Unidad)", 
        conflictos, PlotOrientation.VERTICAL, true,true, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setPaint(Color.black); 
        CategoryPlot p = chart.getCategoryPlot(); 
        p.setRangeGridlinePaint(Color.red); 
        // Mostrar Grafico
        ChartPanel chartPanel = new ChartPanel(chart);
        panel.add(chartPanel);
    }    
}


	