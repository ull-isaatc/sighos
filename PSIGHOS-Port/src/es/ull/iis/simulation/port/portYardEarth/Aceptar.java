package es.ull.iis.simulation.port.portYardEarth;

import java.awt.event.ActionEvent; 
import java.awt.event.ActionListener;



public class Aceptar implements ActionListener {
	
	

	public Aceptar() {
	}

	public void actionPerformed(ActionEvent arg0) {
		
			IdeaPort1Main sim = new IdeaPort1Main(Integer.parseInt(DatosEntrada.textField.getText()), Integer.parseInt(DatosEntrada.textField_1.getText()), 
					Integer.parseInt(DatosEntrada.textField_2.getText()),Integer.parseInt(DatosEntrada.textField_3.getText()), Integer.parseInt(DatosEntrada.textField_4.getText()),
					Integer.parseInt(DatosEntrada.textField_5.getText()),Integer.parseInt(DatosEntrada.textField_6.getText()),Integer.parseInt(DatosEntrada.textField_7.getText()),
					Integer.parseInt(DatosEntrada.textField_8.getText()), Integer.parseInt(DatosEntrada.textField_9.getText()), Integer.parseInt(DatosEntrada.textField_10.getText()),
					Integer.parseInt(DatosEntrada.textField_11.getText()),Integer.parseInt(DatosEntrada.textField_12.getText()),Integer.parseInt(DatosEntrada.textField_13.getText()),
					Integer.parseInt(DatosEntrada.textField_14.getText()), Double.parseDouble(DatosEntrada.textField_15.getText()));	
			sim.start();
			TiemposConcluyentes Ventana2 = new TiemposConcluyentes();
			Ventana2.setVisible(true);
		
			
	}
	
}

	


